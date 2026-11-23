package org.apache.openejb.assembler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Alternative;
import javax.validation.ValidationException;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.DeploymentExceptionManager;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.DeploymentLoader;
import org.apache.openejb.config.DeploymentModule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.config.sys.AdditionalDeployments;
import org.apache.openejb.config.sys.Deployments;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.ProvisioningUtil;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.loader.provisining.ProvisioningResolver;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

@Singleton(
   name = "openejb/Deployer"
)
@Lock(LockType.READ)
@Remote({Deployer.class})
@TransactionManagement(TransactionManagementType.BEAN)
@Alternative
public class DeployerEjb implements Deployer {
   private static final Logger LOGGER;
   public static final String OPENEJB_DEPLOYER_FORCED_APP_ID_PROP = "openejb.deployer.forced.appId";
   public static final String OPENEJB_DEPLOYER_HOST = "openejb.deployer.host";
   public static final String OPENEJB_USE_BINARIES = "openejb.deployer.binaries.use";
   public static final String OPENEJB_PATH_BINARIES = "openejb.deployer.binaries.path";
   public static final String OPENEJB_VALUE_BINARIES = "openejb.deployer.binaries.value";
   public static final String OPENEJB_APP_AUTODEPLOY = "openejb.app.autodeploy";
   public static final ThreadLocal<Boolean> AUTO_DEPLOY;
   private static final File uniqueFile;
   private static final boolean oldWarDeployer;
   private static final String OPENEJB_DEPLOYER_SAVE_DEPLOYMENTS = "openejb.deployer.save-deployments";
   private static final boolean SAVE_DEPLOYMENTS;
   private final DeploymentLoader deploymentLoader = new DeploymentLoader();
   private final ConfigurationFactory configurationFactory;
   private final Assembler assembler;

   public DeployerEjb() {
      ConfigurationFactory component = (ConfigurationFactory)SystemInstance.get().getComponent(ConfigurationFactory.class);
      this.configurationFactory = component == null ? new ConfigurationFactory() : component;
      this.assembler = (Assembler)SystemInstance.get().getComponent(org.apache.openejb.spi.Assembler.class);
   }

   public String getUniqueFile() {
      return uniqueFile.getAbsolutePath();
   }

   public Collection<AppInfo> getDeployedApps() {
      return this.assembler.getDeployedApplications();
   }

   public AppInfo deploy(String location) throws OpenEJBException {
      return this.deploy(location, (Properties)null);
   }

   public AppInfo deploy(Properties properties) throws OpenEJBException {
      return this.deploy((String)null, properties);
   }

   public AppInfo deploy(String inLocation, Properties properties) throws OpenEJBException {
      String rawLocation = inLocation;
      if (inLocation == null && properties == null) {
         throw new NullPointerException("location and properties are null");
      } else {
         if (inLocation == null) {
            rawLocation = properties.getProperty("filename");
         }

         if (properties == null) {
            properties = new Properties();
         }

         AppModule appModule = null;
         File file;
         if ("true".equalsIgnoreCase(properties.getProperty("openejb.deployer.binaries.use", "false"))) {
            file = this.copyBinaries(properties);
         } else {
            file = new File((String)ProvisioningUtil.realLocation(rawLocation).iterator().next());
         }

         boolean autoDeploy = Boolean.parseBoolean(properties.getProperty("openejb.app.autodeploy", "false"));
         String host = properties.getProperty("openejb.deployer.host", (String)null);
         if (WebAppDeployer.Helper.isWebApp(file) && !oldWarDeployer) {
            AUTO_DEPLOY.set(autoDeploy);

            AppInfo var24;
            try {
               AppInfo appInfo = ((WebAppDeployer)SystemInstance.get().getComponent(WebAppDeployer.class)).deploy(host, this.contextRoot(properties, file.getAbsolutePath()), file);
               if (appInfo == null) {
                  throw new OpenEJBException("can't deploy " + file.getAbsolutePath());
               }

               this.saveIfNeeded(properties, file, appInfo);
               var24 = appInfo;
            } finally {
               AUTO_DEPLOY.remove();
            }

            return var24;
         } else {
            AppInfo appInfo = null;

            try {
               appModule = this.deploymentLoader.load(file, (DeploymentLoader.ExternalConfiguration)null);
               Map<String, DeploymentModule> modules = new TreeMap();

               for(DeploymentModule module : appModule.getEjbModules()) {
                  modules.put(module.getModuleId(), module);
               }

               for(DeploymentModule module : appModule.getClientModules()) {
                  modules.put(module.getModuleId(), module);
               }

               for(WebModule module : appModule.getWebModules()) {
                  String contextRoot = this.contextRoot(properties, module.getJarLocation());
                  if (contextRoot != null) {
                     module.setContextRoot(contextRoot);
                     module.setHost(host);
                  }

                  modules.put(module.getModuleId(), module);
               }

               for(DeploymentModule module : appModule.getConnectorModules()) {
                  modules.put(module.getModuleId(), module);
               }

               for(Map.Entry<Object, Object> entry : properties.entrySet()) {
                  String name = (String)entry.getKey();
                  if (name.startsWith("altDD/")) {
                     name = name.substring("altDD".length() + 1);
                     int slash = name.indexOf(47);
                     DeploymentModule module;
                     if (slash > 0) {
                        String moduleId = name.substring(0, slash);
                        name = name.substring(slash + 1);
                        module = (DeploymentModule)modules.get(moduleId);
                     } else {
                        module = appModule;
                     }

                     if (module != null) {
                        String value = (String)entry.getValue();
                        File dd = new File(value);
                        if (dd.canRead()) {
                           module.getAltDDs().put(name, dd.toURI().toURL());
                        } else {
                           module.getAltDDs().put(name, value);
                        }
                     }
                  }
               }

               appInfo = this.configurationFactory.configureApplication(appModule);
               appInfo.autoDeploy = autoDeploy;
               if (properties != null && properties.containsKey("openejb.deployer.forced.appId")) {
                  appInfo.appId = properties.getProperty("openejb.deployer.forced.appId");
               }

               if (!appInfo.webApps.isEmpty()) {
                  appInfo.properties.setProperty("tomcat.unpackWar", "false");
               }

               this.assembler.createApplication(appInfo);
               this.saveIfNeeded(properties, file, appInfo);
               return appInfo;
            } catch (Throwable e) {
               if (appModule != null) {
                  ClassLoaderUtil.destroyClassLoader(appModule.getJarLocation());
               }

               if (null != appInfo) {
                  ClassLoaderUtil.destroyClassLoader(appInfo.appId, appInfo.path);
               }

               LOGGER.error("Can't deploy " + inLocation, e);
               if (e instanceof ValidationException) {
                  throw (ValidationException)e;
               } else {
                  DeploymentExceptionManager dem = (DeploymentExceptionManager)SystemInstance.get().getComponent(DeploymentExceptionManager.class);
                  Throwable ex;
                  if (dem != null) {
                     if (dem.hasDeploymentFailed()) {
                        ex = dem.getLastException();
                     } else {
                        ex = e;
                     }

                     if (appInfo != null) {
                        dem.clearLastException(appInfo);
                     }
                  } else {
                     ex = e;
                  }

                  if (ex instanceof OpenEJBException) {
                     if (ex.getCause() instanceof ValidationException) {
                        throw (ValidationException)ex.getCause();
                     } else {
                        throw (OpenEJBException)ex;
                     }
                  } else {
                     throw new OpenEJBException(ex);
                  }
               }
            }
         }
      }
   }

   private void saveIfNeeded(Properties properties, File file, AppInfo appInfo) {
      if (SAVE_DEPLOYMENTS && null == properties.getProperty("openejb.deployer.save-deployments") || "true".equalsIgnoreCase(properties.getProperty("openejb.deployer.save-deployments", "false"))) {
         appInfo.properties.setProperty("save-deployment", "true");
         this.saveDeployment(file, true);
      }

   }

   private synchronized File copyBinaries(Properties props) throws OpenEJBException {
      File dump = ProvisioningResolver.cacheFile(props.getProperty("openejb.deployer.binaries.path", "dump.war"));
      if (dump.exists()) {
         Files.delete(dump);
         String name = dump.getName();
         if (name.endsWith("ar") && name.length() > 4) {
            File exploded = new File(dump.getParentFile(), name.substring(0, name.length() - 4));
            if (exploded.exists()) {
               Files.delete(exploded);
            }
         }
      }

      try {
         IO.copy((byte[])byte[].class.cast(props.get("openejb.deployer.binaries.value")), dump);
         return dump;
      } catch (IOException e) {
         throw new OpenEJBException(e);
      }
   }

   private synchronized void saveDeployment(File file, boolean add) {
      Deployments deps = new Deployments();
      if (file.isDirectory()) {
         deps.setDir(file.getAbsolutePath());
      } else {
         deps.setFile(file.getAbsolutePath());
      }

      File config;
      try {
         config = SystemInstance.get().getBase().getFile("conf/deployments.xml", false);
      } catch (IOException var22) {
         config = null;
      }

      if (config != null && config.getParentFile().exists()) {
         OutputStream os = null;

         try {
            AdditionalDeployments additionalDeployments;
            if (config.exists() && config.length() > 0L) {
               InputStream fis = IO.read(config);

               try {
                  additionalDeployments = (AdditionalDeployments)JaxbOpenejb.unmarshal(AdditionalDeployments.class, fis);
               } finally {
                  IO.close(fis);
               }
            } else {
               additionalDeployments = new AdditionalDeployments();
            }

            if (add) {
               if (!additionalDeployments.getDeployments().contains(deps)) {
                  additionalDeployments.getDeployments().add(deps);
               }
            } else {
               Iterator<Deployments> it = additionalDeployments.getDeployments().iterator();

               while(it.hasNext()) {
                  Deployments current = (Deployments)it.next();
                  if (deps.getDir() != null && deps.getDir().equals(current.getDir())) {
                     it.remove();
                     break;
                  }

                  if (deps.getFile() != null && deps.getFile().equals(current.getFile())) {
                     it.remove();
                     break;
                  }

                  String jar = deps.getFile();
                  if (jar != null && jar.length() > 3) {
                     String substring = jar.substring(0, jar.length() - 4);
                     if (substring.equals(current.getDir()) || substring.equals(current.getFile())) {
                        it.remove();
                        break;
                     }
                  } else {
                     String jarC = current.getFile();
                     if (jarC != null && jarC.length() > 3) {
                        String substring = jarC.substring(0, jarC.length() - 4);
                        if (substring.equals(deps.getDir()) || substring.equals(deps.getFile())) {
                           it.remove();
                           break;
                        }
                     }
                  }
               }
            }

            os = IO.write(config);
            JaxbOpenejb.marshal(AdditionalDeployments.class, additionalDeployments, os);
         } catch (Exception e) {
            LOGGER.error("cannot save the added app, will not be present next time you'll start", (Throwable)e);
         } finally {
            IO.close(os);
         }

      } else {
         LOGGER.info("Cannot save the added app because the conf folder does not exist, it will not be present on a restart");
      }
   }

   public void undeploy(String moduleId) throws UndeployException, NoSuchApplicationException {
      AppInfo appInfo = this.assembler.getAppInfo(moduleId);
      if (appInfo == null) {
         appInfo = this.assembler.getAppInfo((String)ProvisioningUtil.realLocation(moduleId).iterator().next());
         if (appInfo == null) {
            appInfo = this.assembler.getAppInfo((new File(moduleId)).getAbsolutePath());
            if (appInfo == null) {
               appInfo = this.assembler.getAppInfo((new File((String)ProvisioningUtil.realLocation(moduleId).iterator().next())).getAbsolutePath());
            }
         }
      }

      if (appInfo != null) {
         try {
            this.assembler.destroyApplication(appInfo);
         } finally {
            if (appInfo.properties.containsKey("save-deployment")) {
               this.saveDeployment(new File(moduleId), false);
            }

         }

      } else {
         throw new NoSuchApplicationException(moduleId);
      }
   }

   private String contextRoot(Properties properties, String jarPath) {
      return properties.getProperty("webapp." + jarPath + ".context-root");
   }

   public void reload(String moduleId) {
      for(AppInfo info : this.assembler.getDeployedApplications()) {
         if (info.path.equals(moduleId)) {
            this.reload(info);
            break;
         }
      }

   }

   private void reload(AppInfo info) {
      if (info.webAppAlone) {
         WebAppDeployer component = (WebAppDeployer)SystemInstance.get().getComponent(WebAppDeployer.class);
         if (null != component) {
            component.reload(info.path);
            return;
         }
      }

      try {
         this.assembler.destroyApplication(info);
         this.assembler.createApplication(info);
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, DeployerEjb.class);
      AUTO_DEPLOY = new ThreadLocal();
      oldWarDeployer = "old".equalsIgnoreCase(SystemInstance.get().getOptions().get("openejb.deployer.war", "new"));
      SAVE_DEPLOYMENTS = SystemInstance.get().getOptions().get("openejb.deployer.save-deployments", false);
      String uniqueName = "OpenEJB-" + (new BigInteger(128, new SecureRandom())).toString(36);
      String tempDir = JavaSecurityManagers.getSystemProperty("java.io.tmpdir");

      File unique;
      try {
         unique = (new File(tempDir, uniqueName)).getCanonicalFile();
         if (!unique.createNewFile()) {
            throw new IOException("Failed to create file in temp: " + unique);
         }
      } catch (IOException e) {
         unique = new File(SystemInstance.get().getBase().getDirectory(), "work");
         if (!unique.exists()) {
            throw new OpenEJBRuntimeException("cannot create unique file, please set java.io.tmpdir to a writable folder or create work folder", e);
         }

         try {
            unique = (new File(unique, uniqueName)).getCanonicalFile();
            if (!unique.createNewFile()) {
               throw new IOException("Failed to create file in work: " + unique);
            }
         } catch (IOException var5) {
            throw new OpenEJBRuntimeException(e);
         }
      }

      uniqueFile = unique;
      uniqueFile.deleteOnExit();
   }
}
