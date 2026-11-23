package org.apache.openejb.config;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;

public class OpenEJBDeploymentManager implements DeploymentManager {
   public static final DConfigBeanVersionType DEFAULT_DCONFIG_BEAN_VERSION;
   public static final String DEFAULT_TARGET_NAME = "DefaultTarget";
   private Deployment deployment;
   private final Locale locale;
   private final DConfigBeanVersionType dconfigBeanVersion;
   private final Target defaultTarget;
   private final SortedMap<String, Target> targets;
   private final List<String> targetPaths;

   public OpenEJBDeploymentManager() {
      this.locale = Locale.getDefault();
      this.dconfigBeanVersion = DEFAULT_DCONFIG_BEAN_VERSION;
      this.defaultTarget = null;
      this.targets = null;
      this.targetPaths = null;
   }

   public OpenEJBDeploymentManager(Deployment deployment) throws DeploymentManagerCreationException {
      this.deployment = deployment;
      Properties properties = deployment.getProperties();
      String localeString = properties.getProperty("locale");
      if (localeString != null) {
         this.locale = new Locale(localeString);
      } else {
         this.locale = Locale.getDefault();
      }

      String dconfigBeanVersionString = properties.getProperty("locale");
      this.dconfigBeanVersion = parseDConfigBeanVersionType(dconfigBeanVersionString);
      SortedMap<String, Target> targets = new TreeMap();

      for(Map.Entry entry : properties.entrySet()) {
         String key = (String)entry.getKey();
         String targetName = (String)entry.getValue();
         if (key.startsWith("target.") && !key.endsWith(".description")) {
            String targetDescription = properties.getProperty(key + ".description");
            TargetImpl target = new TargetImpl(targetName, targetDescription);
            targets.put(targetName, target);
         }
      }

      String defaultTargetName = properties.getProperty("target.default");
      if (defaultTargetName == null) {
         defaultTargetName = "DefaultTarget";
      }

      if (!targets.containsKey(defaultTargetName)) {
         targets.put(defaultTargetName, new TargetImpl(defaultTargetName, (String)null));
      }

      this.defaultTarget = (Target)targets.get(defaultTargetName);
      this.targets = Collections.unmodifiableSortedMap(targets);
      this.targetPaths = new ArrayList();

      for(String targetName : targets.keySet()) {
         this.targetPaths.add(targetName + "/");
      }

      Collections.reverse(this.targetPaths);
   }

   public void release() {
      if (this.deployment != null) {
         this.deployment.release();
         this.deployment = null;
      }

   }

   public Target[] getTargets() {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         return (Target[])this.targets.values().toArray(new Target[this.targets.size()]);
      }
   }

   public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            String type = null;
            if (type != null) {
               moduleType.toString();
            }

            Set<String> targetModulesStrings = this.deployment.list(type, (String)null, this.toTargetSet(targetList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModulesStrings);
            return (TargetModuleID[])targetModules.toArray(new TargetModuleID[targetModules.size()]);
         } catch (DeploymentException e) {
            throw new IllegalStateException("DeployerException while listing deployments", e);
         }
      }
   }

   public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            String type = null;
            if (type != null) {
               moduleType.toString();
            }

            Set<String> targetModulesStrings = this.deployment.list(type, "stopped", this.toTargetSet(targetList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModulesStrings);
            return (TargetModuleID[])targetModules.toArray(new TargetModuleID[targetModules.size()]);
         } catch (DeploymentException e) {
            throw new IllegalStateException("DeployerException while listing deployments", e);
         }
      }
   }

   public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] targetList) throws TargetException {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            String type = null;
            if (type != null) {
               moduleType.toString();
            }

            Set<String> targetModulesStrings = this.deployment.list(type, "running", this.toTargetSet(targetList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModulesStrings);
            return (TargetModuleID[])targetModules.toArray(new TargetModuleID[targetModules.size()]);
         } catch (DeploymentException e) {
            throw new IllegalStateException("DeployerException while listing deployments", e);
         }
      }
   }

   public ProgressObject distribute(Target[] targetList, File moduleArchive, File deploymentPlan) {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            Set<String> targetModuleStrings = this.deployment.deploy(this.toTargetSet(targetList), moduleArchive);
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModuleStrings);
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, targetModules);
         } catch (DeploymentException e) {
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, e);
         }
      }
   }

   public ProgressObject distribute(Target[] targetList, InputStream moduleArchive, InputStream deploymentPlan) {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            Set<String> targetModuleStrings = this.deployment.deploy(this.toTargetSet(targetList), (File)null);
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModuleStrings);
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, targetModules);
         } catch (DeploymentException e) {
            return new ProgressObjectImpl(CommandType.DISTRIBUTE, e);
         }
      }
   }

   public ProgressObject start(TargetModuleID[] moduleIdList) {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            Set<String> targetModuleStrings = this.deployment.start(this.toModuleSet(moduleIdList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModuleStrings);
            return new ProgressObjectImpl(CommandType.START, targetModules);
         } catch (DeploymentException e) {
            return new ProgressObjectImpl(CommandType.START, e);
         }
      }
   }

   public ProgressObject stop(TargetModuleID[] moduleIdList) {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            Set<String> targetModuleStrings = this.deployment.stop(this.toModuleSet(moduleIdList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModuleStrings);
            return new ProgressObjectImpl(CommandType.STOP, targetModules);
         } catch (DeploymentException e) {
            return new ProgressObjectImpl(CommandType.STOP, e);
         }
      }
   }

   public ProgressObject undeploy(TargetModuleID[] moduleIdList) {
      if (this.deployment == null) {
         throw new IllegalStateException("Deployment manager is disconnected");
      } else {
         try {
            Set<String> targetModuleStrings = this.deployment.undeploy(this.toModuleSet(moduleIdList));
            Set<TargetModuleID> targetModules = this.toTargetModuleIds(targetModuleStrings);
            return new ProgressObjectImpl(CommandType.UNDEPLOY, targetModules);
         } catch (DeploymentException e) {
            return new ProgressObjectImpl(CommandType.UNDEPLOY, e);
         }
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
      return this.locale;
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
      return this.dconfigBeanVersion;
   }

   public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType version) {
      return this.dconfigBeanVersion.equals(version);
   }

   public void setDConfigBeanVersion(DConfigBeanVersionType version) throws DConfigBeanVersionUnsupportedException {
      if (!this.isDConfigBeanVersionSupported(version)) {
         throw new DConfigBeanVersionUnsupportedException("Version not supported " + version);
      }
   }

   public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
      throw new InvalidModuleException("Not supported: " + deployableObject.getType());
   }

   private Target getTargetFor(String moduleId) {
      for(String targetName : this.targetPaths) {
         if (moduleId.startsWith(moduleId)) {
            return (Target)this.targets.get(targetName);
         }
      }

      return null;
   }

   private Set<TargetModuleID> toTargetModuleIds(Set<String> modules) {
      Set<TargetModuleID> targetModuleIds = new HashSet();

      for(String module : modules) {
         int spaceIndex = module.indexOf(32);
         String moduleId;
         String webUrl;
         if (spaceIndex > 1) {
            moduleId = module.substring(0, spaceIndex);
            webUrl = module.substring(spaceIndex + 1);
         } else {
            moduleId = module;
            webUrl = null;
         }

         Target target = this.getTargetFor(moduleId);
         if (target != null) {
            if (moduleId.startsWith(target.getName())) {
               moduleId = moduleId.substring(target.getName().length());
            }
         } else {
            target = this.defaultTarget;
         }

         TargetModuleIDImpl targetModuleID = new TargetModuleIDImpl(target, moduleId, webUrl);
         targetModuleIds.add(targetModuleID);
      }

      return targetModuleIds;
   }

   private Set<String> toTargetSet(Target[] targets) {
      if (targets == null) {
         return Collections.emptySet();
      } else {
         TreeSet<String> targetSet = new TreeSet();

         for(Target target : targets) {
            targetSet.add(target.getName());
         }

         return targetSet;
      }
   }

   private Set<String> toModuleSet(TargetModuleID[] moduleIDList) {
      if (moduleIDList == null) {
         return Collections.emptySet();
      } else {
         TreeSet<String> moduleSet = new TreeSet();

         for(TargetModuleID module : moduleIDList) {
            moduleSet.add(module.getTarget().getName() + "/" + module.getModuleID());
         }

         return moduleSet;
      }
   }

   public static DConfigBeanVersionType parseDConfigBeanVersionType(String string) throws DeploymentManagerCreationException {
      if (string == null) {
         return DEFAULT_DCONFIG_BEAN_VERSION;
      } else {
         try {
            Field field = DConfigBeanVersionType.class.getField(string);
            if (field.getType().equals(DConfigBeanVersionType.class) && Modifier.isStatic(field.getModifiers())) {
               return (DConfigBeanVersionType)field.get((Object)null);
            }
         } catch (Exception var2) {
         }

         throw new DeploymentManagerCreationException("Unknown DConfig bean version: " + string);
      }
   }

   static {
      DEFAULT_DCONFIG_BEAN_VERSION = DConfigBeanVersionType.V1_4;
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
         return (TargetModuleID[])this.targetModuleIds.toArray(new TargetModuleID[this.targetModuleIds.size()]);
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
