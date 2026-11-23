package org.apache.openejb.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.naming.InitialContext;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.Deployer;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.ClientInfo;
import org.apache.openejb.assembler.classic.ConnectorInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.InfoObject;
import org.apache.openejb.assembler.classic.WebAppInfo;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.JavaSecurityManagers;

public class VmDeploymentManager implements DeploymentManager {
   private static final Target DEFAULT_TARGET = new TargetImpl("DefaultTarget", "OpenEJB Default Target");
   private static final DConfigBeanVersionType DCONFIG_BEAN_VERSION;
   private static final Locale LOCALE;
   private boolean connected = true;
   private Deployer deployer;
   private final String openejbUri;
   private boolean deployerLocal;

   public VmDeploymentManager() {
      String openejbHome = JavaSecurityManagers.getSystemProperty("openejb.home");
      File openejbHomeDir = new File(openejbHome);
      if (!openejbHomeDir.exists()) {
         throw new IllegalArgumentException("OpenEJB home dir does not exist: " + openejbHomeDir);
      } else if (!openejbHomeDir.isDirectory()) {
         throw new IllegalArgumentException("OpenEJB home dir is not a directory: " + openejbHomeDir);
      } else {
         String openejbUri = JavaSecurityManagers.getSystemProperty("openejb.server.uri");
         if (openejbUri == null) {
            try {
               openejbUri = (new URI("ejbd", (String)null, "localhost", 4201, (String)null, (String)null, (String)null)).toString();
            } catch (URISyntaxException e) {
               throw new OpenEJBRuntimeException(e);
            }
         }

         this.openejbUri = openejbUri;
      }
   }

   private Deployer getDeployer() {
      String deployerJndi = JavaSecurityManagers.getSystemProperty("openejb.deployer.jndiname", "openejb/DeployerBusinessRemote");
      if (this.deployer == null) {
         try {
            Properties p = new Properties();
            p.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
            p.put("java.naming.provider.url", this.openejbUri);
            InitialContext ctx = new InitialContext(p);
            this.deployer = (Deployer)ctx.lookup(deployerJndi);
         } catch (Exception e) {
            throw new OpenEJBRuntimeException(e);
         }
      }

      return this.deployer;
   }

   private boolean isDeployerLocal() {
      if (this.deployer == null) {
         this.deployerLocal = (new File(this.getDeployer().getUniqueFile())).exists();
      }

      return this.deployerLocal;
   }

   public void release() {
      this.connected = false;
   }

   public Target[] getTargets() {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         return new Target[]{DEFAULT_TARGET};
      }
   }

   public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else if (!this.containsDefaultTarget(targetList)) {
         return null;
      } else {
         Set<TargetModuleID> targetModuleIds = toTargetModuleIds(this.getDeployer().getDeployedApps(), moduleType);
         return (TargetModuleID[])targetModuleIds.toArray(new TargetModuleID[targetModuleIds.size()]);
      }
   }

   private static Set<TargetModuleID> toTargetModuleIds(Collection<AppInfo> deployedApps, ModuleType allowedModuleType) {
      Set<TargetModuleID> targetModuleIds = new HashSet(deployedApps.size());

      for(AppInfo deployedApp : deployedApps) {
         TargetModuleID moduleId = toTargetModuleId(deployedApp, allowedModuleType);
         if (moduleId != null) {
            targetModuleIds.add(moduleId);
         }
      }

      return targetModuleIds;
   }

   private static TargetModuleID toTargetModuleId(AppInfo appInfo, ModuleType allowedModuleType) {
      List<InfoObject> infos = new ArrayList();
      infos.addAll(appInfo.clients);
      infos.addAll(appInfo.ejbJars);
      infos.addAll(appInfo.webApps);
      infos.addAll(appInfo.connectors);
      if (infos.size() == 1) {
         InfoObject infoObject = (InfoObject)infos.get(0);
         if (infoObject instanceof ClientInfo) {
            ClientInfo clientInfo = (ClientInfo)infoObject;
            if (null != appInfo.path && appInfo.path.equals(clientInfo.path)) {
               if (allowedModuleType != null && !allowedModuleType.equals(ModuleType.CAR)) {
                  return null;
               }

               if (null != clientInfo.moduleId && clientInfo.moduleId.equals(appInfo.path)) {
                  return new TargetModuleIDImpl(DEFAULT_TARGET, clientInfo.moduleId);
               }
            }
         }

         if (infoObject instanceof EjbJarInfo) {
            EjbJarInfo ejbJarInfo = (EjbJarInfo)infoObject;
            if (null != appInfo.path && appInfo.path.equals(ejbJarInfo.path)) {
               if (allowedModuleType != null && !allowedModuleType.equals(ModuleType.EJB)) {
                  return null;
               }

               if (null != ejbJarInfo.moduleName && ejbJarInfo.moduleName.equals(appInfo.appId)) {
                  return new TargetModuleIDImpl(DEFAULT_TARGET, ejbJarInfo.moduleName);
               }
            }
         }

         if (infoObject instanceof ConnectorInfo) {
            ConnectorInfo connectorInfo = (ConnectorInfo)infoObject;
            if (null != appInfo.path && appInfo.path.equals(connectorInfo.path)) {
               if (allowedModuleType != null && !allowedModuleType.equals(ModuleType.RAR)) {
                  return null;
               }

               if (null != connectorInfo.moduleId && connectorInfo.moduleId.equals(appInfo.path)) {
                  return new TargetModuleIDImpl(DEFAULT_TARGET, connectorInfo.moduleId);
               }
            }
         }

         if (infoObject instanceof WebAppInfo) {
            WebAppInfo webAppInfo = (WebAppInfo)infoObject;
            if (null != appInfo.path && appInfo.path.equals(webAppInfo.path)) {
               if (allowedModuleType != null && !allowedModuleType.equals(ModuleType.WAR)) {
                  return null;
               }

               if (null != webAppInfo.moduleId && webAppInfo.moduleId.equals(appInfo.path)) {
                  return new TargetModuleIDImpl(DEFAULT_TARGET, webAppInfo.moduleId);
               }
            }
         }
      }

      if (allowedModuleType != null && !allowedModuleType.equals(ModuleType.EAR)) {
         return null;
      } else {
         TargetModuleIDImpl earModuleId = new TargetModuleIDImpl(DEFAULT_TARGET, appInfo.path);

         for(ClientInfo clientInfo : appInfo.clients) {
            TargetModuleIDImpl clientModuleId = new TargetModuleIDImpl(DEFAULT_TARGET, clientInfo.moduleId);
            clientModuleId.setParentTargetModuleID(earModuleId);
         }

         for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
            TargetModuleIDImpl ejbJarModuleId = new TargetModuleIDImpl(DEFAULT_TARGET, ejbJarInfo.moduleName);
            ejbJarModuleId.setParentTargetModuleID(earModuleId);
         }

         for(ConnectorInfo connectorInfo : appInfo.connectors) {
            TargetModuleIDImpl clientModuleId = new TargetModuleIDImpl(DEFAULT_TARGET, connectorInfo.moduleId);
            clientModuleId.setParentTargetModuleID(earModuleId);
         }

         for(WebAppInfo webAppInfo : appInfo.webApps) {
            TargetModuleIDImpl clientModuleId = new TargetModuleIDImpl(DEFAULT_TARGET, webAppInfo.moduleId, webAppInfo.contextRoot);
            clientModuleId.setParentTargetModuleID(earModuleId);
         }

         return earModuleId;
      }
   }

   public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         return !this.containsDefaultTarget(targetList) ? null : new TargetModuleIDImpl[0];
      }
   }

   public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else if (!this.containsDefaultTarget(targetList)) {
         return null;
      } else {
         Set<TargetModuleID> targetModuleIds = toTargetModuleIds(this.getDeployer().getDeployedApps(), moduleType);
         return (TargetModuleID[])targetModuleIds.toArray(new TargetModuleID[targetModuleIds.size()]);
      }
   }

   public ProgressObject distribute(Target[] targetList, File moduleFile, File planFile) {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else if (!this.isDeployerLocal()) {
         throw new UnsupportedOperationException("Deployment from a remote computer is not currently supproted");
      } else {
         Properties properties = new Properties();
         if (planFile != null) {
            try {
               IO.readProperties(planFile, properties);
            } catch (IOException var6) {
            }
         }

         return this.deploy(targetList, properties);
      }
   }

   public ProgressObject distribute(Target[] targetList, InputStream moduleStream, InputStream planStream) {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else if (this.isDeployerLocal()) {
         IO.close(moduleStream);
         Properties properties = new Properties();
         if (planStream != null) {
            try {
               properties.load(planStream);
            } catch (IOException var9) {
            } finally {
               IO.close(planStream);
            }
         }

         return this.deploy(targetList, properties);
      } else {
         throw new UnsupportedOperationException("Deployment from a remote computer is not currently supproted");
      }
   }

   private ProgressObject deploy(Target[] targetList, Properties properties) {
      if (targetList == null) {
         return new ProgressObjectImpl(CommandType.DISTRIBUTE, new NullPointerException("targetList is null"));
      } else if (!this.containsDefaultTarget(targetList)) {
         return new ProgressObjectImpl(CommandType.DISTRIBUTE, Collections.emptySet());
      } else {
         try {
            AppInfo appInfo = this.getDeployer().deploy(properties);
            TargetModuleID targetModuleId = toTargetModuleId(appInfo, (ModuleType)null);
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, Collections.singleton(targetModuleId));
         } catch (ValidationFailedException e) {
            String s = JavaSecurityManagers.getSystemProperty("openejb.validation.output.level", "3");
            int level = Integer.parseInt(s);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(baos);
            out.println(e.getMessage());
            this.print(e.getErrors(), out, level);
            this.print(e.getFailures(), out, level);
            this.print(e.getWarnings(), out, level);
            out.close();
            ValidationFailedException appInfo = new ValidationFailedException(new String(baos.toByteArray()), e);
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, appInfo);
         } catch (OpenEJBException e) {
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, e);
         }
      }
   }

   protected void print(ValidationException[] exceptions, PrintStream out, int level) {
      for(ValidationException exception : exceptions) {
         out.print(" ");
         out.print(exception.getPrefix());
         out.print(" ... ");
         if (!(exception instanceof ValidationError)) {
            out.print(exception.getComponentName());
            out.print(": ");
         }

         out.println(exception.getMessage(level));
      }

   }

   private boolean containsDefaultTarget(Target[] targetList) {
      for(Target target : targetList) {
         if (DEFAULT_TARGET.equals(target)) {
            return true;
         }
      }

      return false;
   }

   public ProgressObject start(TargetModuleID[] moduleIdList) {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         Set<TargetModuleID> deployedModules = toTargetModuleIds(this.getDeployer().getDeployedApps(), (ModuleType)null);
         Set<TargetModuleID> targetModuleIds = new HashSet(Arrays.asList(moduleIdList));
         targetModuleIds.retainAll(deployedModules);
         return new ProgressObjectImpl(CommandType.START, targetModuleIds);
      }
   }

   public ProgressObject stop(TargetModuleID[] moduleIdList) {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         return new ProgressObjectImpl(CommandType.START, Collections.emptySet());
      }
   }

   public ProgressObject undeploy(TargetModuleID[] moduleIdList) {
      if (!this.connected) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         UndeployException undeployException = null;
         Set<TargetModuleID> results = new TreeSet();

         for(TargetModuleID targetModuleId : moduleIdList) {
            try {
               this.getDeployer().undeploy(targetModuleId.getModuleID());
               results.add(targetModuleId);
            } catch (UndeployException e) {
               if (undeployException == null) {
                  undeployException = e;
               }
            } catch (NoSuchApplicationException var10) {
            }
         }

         return undeployException == null ? new ProgressObjectImpl(CommandType.UNDEPLOY, results) : new ProgressObjectImpl(CommandType.UNDEPLOY, undeployException);
      }
   }

   public boolean isRedeploySupported() {
      return false;
   }

   public ProgressObject redeploy(TargetModuleID[] moduleIDList, File moduleArchive, File deploymentPlan) {
      throw new UnsupportedOperationException("redeploy is not supported");
   }

   public ProgressObject redeploy(TargetModuleID[] moduleIDList, InputStream moduleArchive, InputStream deploymentPlan) {
      throw new UnsupportedOperationException("redeploy is not supported");
   }

   public Locale[] getSupportedLocales() {
      return new Locale[]{this.getDefaultLocale()};
   }

   public Locale getCurrentLocale() {
      return this.getDefaultLocale();
   }

   public Locale getDefaultLocale() {
      return LOCALE;
   }

   public boolean isLocaleSupported(Locale locale) {
      return this.getDefaultLocale().equals(locale);
   }

   public void setLocale(Locale locale) {
      if (!this.isLocaleSupported(locale)) {
         throw new UnsupportedOperationException("Unsupported locale");
      }
   }

   public DConfigBeanVersionType getDConfigBeanVersion() {
      return DCONFIG_BEAN_VERSION;
   }

   public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType version) {
      return DCONFIG_BEAN_VERSION.equals(version);
   }

   public void setDConfigBeanVersion(DConfigBeanVersionType version) throws DConfigBeanVersionUnsupportedException {
      if (!this.isDConfigBeanVersionSupported(version)) {
         throw new DConfigBeanVersionUnsupportedException("Version not supported " + version);
      }
   }

   public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
      throw new InvalidModuleException("Not supported: " + deployableObject.getType());
   }

   public String toString() {
      return this.connected ? "OpenEJBDeploymentManager - connected" : "OpenEJBDeploymentManager - disconnected";
   }

   static {
      DCONFIG_BEAN_VERSION = DConfigBeanVersionType.V1_4;
      LOCALE = Locale.getDefault();
   }

   public static class TargetImpl implements Target, Comparable, Serializable {
      private static final long serialVersionUID = -7257857314911948377L;
      private final String name;
      private final String description;

      public TargetImpl(String name) {
         this(name, (String)null);
      }

      public TargetImpl(String name, String description) {
         if (name == null) {
            throw new NullPointerException("name is null");
         } else {
            this.name = name;
            this.description = description;
         }
      }

      public String getName() {
         return this.name;
      }

      public String getDescription() {
         return this.description;
      }

      public String toString() {
         return this.name;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof TargetImpl)) {
            return false;
         } else {
            TargetImpl target = (TargetImpl)o;
            return this.name.equals(target.name);
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public int compareTo(Object o) {
         TargetImpl target = (TargetImpl)o;
         return this.name.compareTo(target.name);
      }
   }

   public static class TargetModuleIDImpl implements TargetModuleID, Comparable, Serializable {
      private static final long serialVersionUID = 2471961579457311472L;
      private final Target target;
      private final String moduleId;
      private final String webUrl;
      private TargetModuleID parentTargetModuleId;
      private final Set<TargetModuleID> children;

      public TargetModuleIDImpl(Target target, String moduleId) {
         this(target, moduleId, (String)null);
      }

      public TargetModuleIDImpl(Target target, String moduleId, String webUrl) {
         this.children = new TreeSet();
         if (target == null) {
            throw new NullPointerException("target is null");
         } else if (moduleId == null) {
            throw new NullPointerException("moduleId is null");
         } else {
            this.target = target;
            this.moduleId = moduleId;
            if (webUrl != null && !webUrl.startsWith("http:")) {
               webUrl = "http://localhost:8080/" + webUrl;
            }

            this.webUrl = webUrl;
         }
      }

      public Target getTarget() {
         return this.target;
      }

      public String getModuleID() {
         return this.moduleId;
      }

      public TargetModuleID getParentTargetModuleID() {
         return this.parentTargetModuleId;
      }

      public void setParentTargetModuleID(TargetModuleIDImpl parentTargetModuleId) {
         this.parentTargetModuleId = parentTargetModuleId;
         parentTargetModuleId.children.add(this);
      }

      public TargetModuleID[] getChildTargetModuleID() {
         return (TargetModuleID[])this.children.toArray(new TargetModuleID[this.children.size()]);
      }

      public String getWebURL() {
         return this.webUrl;
      }

      public String toString() {
         return this.target + "/" + this.moduleId + (this.webUrl == null ? " " : this.webUrl);
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof TargetModuleIDImpl)) {
            return false;
         } else {
            TargetModuleIDImpl targetModuleID = (TargetModuleIDImpl)o;
            return this.target.equals(targetModuleID.target) && this.moduleId.equals(targetModuleID.moduleId);
         }
      }

      public int hashCode() {
         int result = this.target.hashCode();
         result = 29 * result + this.moduleId.hashCode();
         return result;
      }

      public int compareTo(Object o) {
         TargetModuleIDImpl targetModuleID = (TargetModuleIDImpl)o;
         int val = this.target.getName().compareTo(targetModuleID.target.getName());
         return val != 0 ? val : this.moduleId.compareTo(targetModuleID.moduleId);
      }
   }

   public class ProgressObjectImpl implements ProgressObject {
      private final Set<TargetModuleID> targetModuleIds;
      private final ProgressEvent event;
      private final DeploymentStatus deploymentStatus;

      public ProgressObjectImpl(CommandType command, Set<TargetModuleID> targetModuleIds) {
         this.targetModuleIds = targetModuleIds;
         this.deploymentStatus = new DeploymentStatusImpl(command);
         this.event = new ProgressEvent(this, (TargetModuleID)null, this.deploymentStatus);
      }

      public ProgressObjectImpl(CommandType command, Exception exception) {
         this.targetModuleIds = null;
         this.deploymentStatus = new DeploymentStatusImpl(command, exception);
         this.event = new ProgressEvent(this, (TargetModuleID)null, this.deploymentStatus);
      }

      public synchronized TargetModuleID[] getResultTargetModuleIDs() {
         return this.targetModuleIds == null ? new TargetModuleID[0] : (TargetModuleID[])this.targetModuleIds.toArray(new TargetModuleID[this.targetModuleIds.size()]);
      }

      public synchronized DeploymentStatus getDeploymentStatus() {
         return this.deploymentStatus;
      }

      public ClientConfiguration getClientConfiguration(TargetModuleID id) {
         return null;
      }

      public boolean isCancelSupported() {
         return false;
      }

      public void cancel() throws OperationUnsupportedException {
         throw new OperationUnsupportedException("cancel is not supported");
      }

      public boolean isStopSupported() {
         return false;
      }

      public void stop() throws OperationUnsupportedException {
         throw new OperationUnsupportedException("stop is not supported");
      }

      public void addProgressListener(ProgressListener pol) {
         pol.handleProgressEvent(this.event);
      }

      public void removeProgressListener(ProgressListener pol) {
      }
   }

   public static class DeploymentStatusImpl implements DeploymentStatus {
      private final CommandType command;
      private final StateType state;
      private final String message;

      public DeploymentStatusImpl(CommandType command) {
         this.command = command;
         this.state = StateType.COMPLETED;
         this.message = null;
      }

      public DeploymentStatusImpl(CommandType command, Exception exception) {
         this.command = command;
         this.state = StateType.FAILED;
         StringWriter writer = new StringWriter();
         exception.printStackTrace(new PrintWriter(writer, true));
         this.message = writer.toString();
      }

      public CommandType getCommand() {
         return this.command;
      }

      public ActionType getAction() {
         return ActionType.EXECUTE;
      }

      public String getMessage() {
         return this.message;
      }

      public StateType getState() {
         return this.state;
      }

      public boolean isRunning() {
         return StateType.RUNNING.equals(this.state);
      }

      public boolean isCompleted() {
         return StateType.COMPLETED.equals(this.state);
      }

      public boolean isFailed() {
         return StateType.FAILED.equals(this.state);
      }

      public String toString() {
         StringBuilder buf = new StringBuilder();
         buf.append("DeploymentStatus[").append(this.command).append(',');
         buf.append(this.state);
         if (this.message != null) {
            buf.append(',').append(this.message);
         }

         buf.append(']');
         return buf.toString();
      }
   }
}
