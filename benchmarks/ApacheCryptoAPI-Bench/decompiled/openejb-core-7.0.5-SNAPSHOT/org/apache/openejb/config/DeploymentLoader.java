package org.apache.openejb.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.zip.ZipEntry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.LocalClient;
import org.apache.openejb.api.RemoteClient;
import org.apache.openejb.cdi.CompositeBeans;
import org.apache.openejb.classloader.ClassLoaderConfigurer;
import org.apache.openejb.classloader.WebAppEnricher;
import org.apache.openejb.config.event.BeforeDeploymentEvent;
import org.apache.openejb.config.sys.Resources;
import org.apache.openejb.core.EmptyResourcesClassLoader;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.FacesConfig;
import org.apache.openejb.jee.JavaWsdlMapping;
import org.apache.openejb.jee.JspConfig;
import org.apache.openejb.jee.Taglib;
import org.apache.openejb.jee.TldTaglib;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.sxc.ApplicationXml;
import org.apache.openejb.util.JarExtractor;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.IAnnotationFinder;
import org.apache.xbean.finder.ResourceFinder;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.Filters;

public class DeploymentLoader implements DeploymentFilterable {
   public static final Logger logger;
   public static final String OPENEJB_ALTDD_PREFIX = "openejb.altdd.prefix";
   private static final String ddDir = "META-INF/";
   public static final String EAR_WEBAPP_PERSISTENCE_XML_JARS = "ear-webapp-persistence-xml-jars";
   public static final String EAR_SCOPED_CDI_BEANS = "ear-scoped-cdi-beans_";
   public static final String RAR_URLS_KEY = "rar-urls";
   public static final String URLS_KEY = "urls";
   private static final String RESOURCES_XML = "resources.xml";
   private static final String WEB_FRAGMENT_XML = "web-fragment.xml";
   private final boolean scanManagedBeans = true;
   private static final Collection<String> KNOWN_DESCRIPTORS;
   private static String ALTDD;
   private volatile List<URL> containerUrls = null;

   /** @deprecated */
   @Deprecated
   public AppModule load(File jarFile) throws OpenEJBException {
      return this.load(jarFile, (ExternalConfiguration)null);
   }

   public AppModule load(File jarFile, ExternalConfiguration config) throws OpenEJBException {
      String jarPath;
      try {
         jarPath = jarFile.getCanonicalPath();
      } catch (IOException e) {
         throw new OpenEJBException("Invalid application file path " + jarFile, e);
      }

      URL baseUrl = getFileUrl(jarFile);
      ClassLoader doNotUseClassLoader = null;

      AppModule appModule;
      try {
         Class<? extends DeploymentModule> moduleClass;
         try {
            doNotUseClassLoader = ClassLoaderUtil.createClassLoader(jarPath, new URL[]{baseUrl}, this.getOpenEJBClassLoader());
            moduleClass = this.discoverModuleType(baseUrl, ClassLoaderUtil.createTempClassLoader(doNotUseClassLoader), true);
         } catch (Exception e) {
            throw new UnknownModuleTypeException("Unable to determine module type for jar: " + baseUrl.toExternalForm(), e);
         }

         if (!ResourcesModule.class.equals(moduleClass)) {
            if (AppModule.class.equals(moduleClass)) {
               AppModule jarLocation = this.createAppModule(jarFile, jarPath);
               return jarLocation;
            }

            if (EjbModule.class.equals(moduleClass)) {
               URL[] urls = new URL[]{baseUrl};
               SystemInstance.get().fireEvent(new BeforeDeploymentEvent(urls));
               ClassLoader classLoader = ClassLoaderUtil.createTempClassLoader(jarPath, urls, this.getOpenEJBClassLoader());
               EjbModule ejbModule = this.createEjbModule(baseUrl, jarPath, classLoader);
               appModule = new AppModule(new EjbModule[]{ejbModule});
               addPersistenceUnits(appModule, baseUrl);
               AppModule var43 = appModule;
               return var43;
            }

            if (ClientModule.class.equals(moduleClass)) {
               String jarLocation = URLs.toFilePath(baseUrl);
               ClientModule clientModule = this.createClientModule(baseUrl, jarLocation, this.getOpenEJBClassLoader(), (String)null);
               appModule = new AppModule(new ClientModule[]{clientModule});
               return appModule;
            }

            if (ConnectorModule.class.equals(moduleClass)) {
               String jarLocation = URLs.toFilePath(baseUrl);
               ConnectorModule connectorModule = createConnectorModule(jarLocation, jarLocation, this.getOpenEJBClassLoader(), (String)null);
               if (connectorModule != null) {
                  List<ConnectorModule> connectorModules = new ArrayList();
                  String id = connectorModule.getModuleId();
                  if (!"true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.connector." + id + ".skip-default", "false"))) {
                     connectorModules.add(connectorModule);
                  }

                  String aliases = SystemInstance.get().getProperty("openejb.connector." + id + ".aliases");
                  if (aliases != null) {
                     for(String alias : aliases.split(",")) {
                        ConnectorModule aliasModule = createConnectorModule(jarLocation, jarLocation, this.getOpenEJBClassLoader(), alias);
                        connectorModules.add(aliasModule);
                     }
                  }

                  AppModule appModule = new AppModule((DeploymentModule[])connectorModules.toArray(new ConnectorModule[connectorModules.size()]));
                  AppModule var45 = appModule;
                  return var45;
               }
            }

            if (WebModule.class.equals(moduleClass)) {
               File file = URLs.toFile(baseUrl);
               WebModule webModule = this.createWebModule(file.getAbsolutePath(), baseUrl, this.getOpenEJBClassLoader(), this.getContextRoot(), this.getModuleName(), config);
               appModule = new AppModule(webModule.getClassLoader(), file.getAbsolutePath(), new Application(), true);
               addWebModule(webModule, appModule);
               addWebModuleDescriptors(baseUrl, webModule, appModule);
               appModule.setStandloneWebModule();
               appModule.setDelegateFirst(true);
               AppModule id = appModule;
               return id;
            }

            if (PersistenceModule.class.equals(moduleClass)) {
               String jarLocation = URLs.toFilePath(baseUrl);
               ClassLoader classLoader = ClassLoaderUtil.createTempClassLoader(jarPath, new URL[]{baseUrl}, this.getOpenEJBClassLoader());
               appModule = new AppModule(classLoader, jarLocation);
               addPersistenceUnits(appModule, baseUrl);
               AppModule ejbModule = appModule;
               return ejbModule;
            }

            throw new UnsupportedModuleTypeException("Unsupported module type: " + moduleClass.getSimpleName());
         }

         AppModule appModule = new AppModule((ClassLoader)null, jarPath);
         ResourcesModule module = new ResourcesModule();
         module.getAltDDs().put("resources.xml", baseUrl);
         ReadDescriptors.readResourcesXml(module);
         module.initAppModule(appModule);
         appModule = appModule;
      } finally {
         if (null != doNotUseClassLoader) {
            ClassLoaderUtil.destroyClassLoader(doNotUseClassLoader);
         }

      }

      return appModule;
   }

   public static void addWebModuleDescriptors(URL baseUrl, WebModule webModule, AppModule appModule) throws OpenEJBException {
      Map<String, Object> otherDD = new HashMap();
      List<URL> urls = webModule.getScannableUrls();
      ResourceFinder finder = new ResourceFinder("", (URL[])urls.toArray(new URL[urls.size()]));
      otherDD.putAll(getDescriptors(finder, false));

      try {
         List<URL> persistenceXmls = finder.findAll("META-INF/persistence.xml");
         if (persistenceXmls.size() >= 1) {
            URL old = (URL)otherDD.get("persistence.xml");
            if (old != null && !persistenceXmls.contains(old)) {
               persistenceXmls.add(old);
            }

            otherDD.put("persistence.xml", persistenceXmls);
         }
      } catch (IOException var8) {
      }

      addConnectorModules(appModule, webModule);
      addWebPersistenceDD("persistence.xml", otherDD, appModule);
      addWebPersistenceDD("persistence-fragment.xml", otherDD, appModule);
      addPersistenceUnits(appModule, baseUrl);
      addWebFragments(webModule, urls);
   }

   private static void addConnectorModules(AppModule appModule, WebModule webModule) throws OpenEJBException {
      if (webModule.getAltDDs().containsKey("ra.xml")) {
         String jarLocation = (new File(webModule.getJarLocation(), "/WEB-INF/classes")).getAbsolutePath();
         ConnectorModule connectorModule = createConnectorModule(jarLocation, jarLocation, webModule.getClassLoader(), webModule.getModuleId() + "RA", (URL)webModule.getAltDDs().get("ra.xml"));
         if (connectorModule != null) {
            appModule.getConnectorModules().add(connectorModule);
         }
      }

      for(URL url : webModule.getRarUrls()) {
         try {
            File file = URLs.toFile(url);
            if (file.getName().endsWith(".rar")) {
               String jarLocation = file.getAbsolutePath();
               ConnectorModule connectorModule = createConnectorModule(jarLocation, jarLocation, webModule.getClassLoader(), (String)null);
               if (connectorModule != null) {
                  appModule.getConnectorModules().add(connectorModule);
               }
            }
         } catch (Exception e) {
            logger.error("error processing url " + url.toExternalForm(), (Throwable)e);
         }
      }

      for(URL url : webModule.getScannableUrls()) {
         try {
            File file = URLs.toFile(url);
            if (file.getName().endsWith(".jar")) {
               JarFile jarFile = new JarFile(file);
               Throwable var31 = null;

               try {
                  String name = (ALTDD != null ? ALTDD + "." : "") + "ra.xml";
                  JarEntry entry = jarFile.getJarEntry(name);
                  if (entry == null) {
                     name = "META-INF/" + name;
                     entry = jarFile.getJarEntry(name);
                  }

                  if (entry != null) {
                     String jarLocation = file.getAbsolutePath();
                     ConnectorModule connectorModule = createConnectorModule(jarLocation, jarLocation, webModule.getClassLoader(), (String)null);
                     if (connectorModule != null) {
                        appModule.getConnectorModules().add(connectorModule);
                     }
                  }
               } catch (Throwable var21) {
                  var31 = var21;
                  throw var21;
               } finally {
                  if (jarFile != null) {
                     if (var31 != null) {
                        try {
                           jarFile.close();
                        } catch (Throwable var20) {
                           var31.addSuppressed(var20);
                        }
                     } else {
                        jarFile.close();
                     }
                  }

               }
            }
         } catch (Exception e) {
            logger.error("error processing url " + url.toExternalForm(), (Throwable)e);
         }
      }

   }

   protected ClassLoader getOpenEJBClassLoader() {
      return ParentClassLoaderFinder.Helper.get();
   }

   private static void addWebPersistenceDD(String name, Map<String, Object> otherDD, AppModule appModule) {
      if (otherDD.containsKey(name)) {
         List<URL> persistenceUrls = (List)appModule.getAltDDs().get(name);
         if (persistenceUrls == null) {
            persistenceUrls = new ArrayList();
            appModule.getAltDDs().put(name, persistenceUrls);
         }

         if (otherDD.containsKey(name)) {
            Object otherUrl = otherDD.get(name);
            if (otherUrl instanceof URL && !persistenceUrls.contains(otherUrl)) {
               persistenceUrls.add((URL)otherUrl);
            } else if (otherUrl instanceof List) {
               for(URL url : (List)otherDD.get(name)) {
                  if (!persistenceUrls.contains(url)) {
                     persistenceUrls.add(url);
                  }
               }
            }
         }
      }

   }

   protected AppModule createAppModule(File jarFile, String jarPath) throws OpenEJBException {
      File appDir = unpack(jarFile);

      try {
         appDir = appDir.getCanonicalFile();
      } catch (IOException var52) {
         throw new OpenEJBException("Invalid application directory " + appDir.getAbsolutePath());
      }

      URL appUrl = getFileUrl(appDir);
      String appId = appDir.getAbsolutePath();
      ClassLoader tmpClassLoader = ClassLoaderUtil.createTempClassLoader(appId, new URL[]{appUrl}, this.getOpenEJBClassLoader());
      ResourceFinder finder = new ResourceFinder("", tmpClassLoader, new URL[]{appUrl});
      Map<String, URL> appDescriptors = getDescriptors(finder);

      try {
         Map<String, URL> ejbModules = new LinkedHashMap();
         Map<String, URL> clientModules = new LinkedHashMap();
         Map<String, URL> resouceModules = new LinkedHashMap();
         Map<String, URL> webModules = new LinkedHashMap();
         Map<String, String> webContextRoots = new LinkedHashMap();
         URL applicationXmlUrl = (URL)appDescriptors.get("application.xml");
         List<URL> extraLibs = new ArrayList();
         Application application;
         if (applicationXmlUrl != null) {
            application = unmarshal(applicationXmlUrl);

            for(org.apache.openejb.jee.Module module : application.getModule()) {
               try {
                  if (module.getEjb() != null) {
                     URL url = finder.find(module.getEjb().trim());
                     ejbModules.put(module.getEjb(), url);
                  } else if (module.getJava() != null) {
                     URL url = finder.find(module.getJava().trim());
                     clientModules.put(module.getJava(), url);
                     extraLibs.add(url);
                  } else if (module.getConnector() != null) {
                     URL url = finder.find(module.getConnector().trim());
                     resouceModules.put(module.getConnector(), url);
                  } else if (module.getWeb() != null) {
                     URL url = finder.find(module.getWeb().getWebUri().trim());
                     webModules.put(module.getWeb().getWebUri(), url);
                     webContextRoots.put(module.getWeb().getWebUri(), module.getWeb().getContextRoot());
                  }
               } catch (IOException e) {
                  throw new OpenEJBException("Invalid path to module " + e.getMessage(), e);
               }
            }
         } else {
            application = new Application();
            HashMap<String, URL> files = new HashMap();
            scanDir(appDir, files, "", false);
            files.remove("META-INF/MANIFEST.MF");
            this.createApplicationFromFiles(appId, tmpClassLoader, ejbModules, clientModules, resouceModules, webModules, files);
         }

         ClassLoaderConfigurer configurer = QuickJarsTxtParser.parse(new File(appDir, "META-INF/jars.txt"));
         Collection<URL> jarsXmlLib = new ArrayList();
         if (configurer != null) {
            for(URL url : configurer.additionalURLs()) {
               try {
                  this.detectAndAddModuleToApplication(appId, tmpClassLoader, ejbModules, clientModules, resouceModules, webModules, new ImmutablePair(URLs.toFile(url).getAbsolutePath(), url));
               } catch (Exception var50) {
                  jarsXmlLib.add(url);
               }
            }
         }

         if (application.getLibraryDirectory() == null) {
            application.setLibraryDirectory("lib/");
         } else {
            String dir = application.getLibraryDirectory();
            if (!dir.endsWith("/")) {
               application.setLibraryDirectory(dir + "/");
            }
         }

         try {
            Map<String, URL> libs = finder.getResourcesMap(application.getLibraryDirectory());
            extraLibs.addAll(libs.values());
         } catch (IOException e) {
            logger.warning("Cannot load libs from '" + application.getLibraryDirectory() + "' : " + e.getMessage(), (Throwable)e);
         }

         try {
            Map<String, URL> libs = finder.getResourcesMap("APP-INF/lib/");
            extraLibs.addAll(libs.values());
         } catch (IOException e) {
            logger.warning("Cannot load libs from 'APP-INF/lib/' : " + e.getMessage(), (Throwable)e);
         }

         try {
            Map<String, URL> libs = finder.getResourcesMap("META-INF/lib/");
            extraLibs.addAll(libs.values());
         } catch (IOException e) {
            logger.warning("Cannot load libs from 'META-INF/lib/' : " + e.getMessage(), (Throwable)e);
         }

         HashMap<String, URL> rarLibs = new HashMap();

         for(Map.Entry<String, URL> entry : resouceModules.entrySet()) {
            try {
               File rarFile = URLs.toFile((URL)entry.getValue());
               rarFile = unpack(rarFile);
               entry.setValue(rarFile.toURI().toURL());
               scanDir(appDir, rarLibs, "");
            } catch (MalformedURLException e) {
               throw new OpenEJBException("Malformed URL to app. " + e.getMessage(), e);
            }
         }

         Iterator<Map.Entry<String, URL>> iterator = rarLibs.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry<String, URL> fileEntry = (Map.Entry)iterator.next();
            if (((String)fileEntry.getKey()).endsWith(".jar")) {
               iterator.remove();
            }
         }

         List<URL> classPath = new ArrayList();
         classPath.addAll(ejbModules.values());
         classPath.addAll(clientModules.values());
         classPath.addAll(rarLibs.values());
         classPath.addAll(extraLibs);
         classPath.addAll(jarsXmlLib);
         URL[] urls = (URL[])classPath.toArray(new URL[classPath.size()]);
         SystemInstance.get().fireEvent(new BeforeDeploymentEvent(urls));
         ClassLoader appClassLoader = ClassLoaderUtil.createTempClassLoader(appId, urls, this.getOpenEJBClassLoader());
         final AppModule appModule = new AppModule(appClassLoader, appId, application, false);
         appModule.getAdditionalLibraries().addAll(extraLibs);
         appModule.getAltDDs().putAll(appDescriptors);
         appModule.getWatchedResources().add(appId);
         if (applicationXmlUrl != null) {
            appModule.getWatchedResources().add(URLs.toFilePath(applicationXmlUrl));
         }

         if (appDescriptors.containsKey("resources.xml")) {
            final Map<String, Object> altDd = new HashMap(appDescriptors);
            ReadDescriptors.readResourcesXml(new Module(false) {
               public Map<String, Object> getAltDDs() {
                  return altDd;
               }

               public void initResources(Resources resources) {
                  appModule.getContainers().addAll(resources.getContainer());
                  appModule.getResources().addAll(resources.getResource());
                  appModule.getServices().addAll(resources.getService());
               }
            });
         }

         for(String moduleName : ejbModules.keySet()) {
            try {
               URL ejbUrl = (URL)ejbModules.get(moduleName);
               if (ClassLoaderUtil.isUrlCached(appModule.getJarLocation(), ejbUrl)) {
                  try {
                     ejbUrl = ClassLoaderUtil.getUrlCachedName(appModule.getJarLocation(), ejbUrl).toURI().toURL();
                  } catch (MalformedURLException var44) {
                  }
               }

               File ejbFile = URLs.toFile(ejbUrl);
               String absolutePath = ejbFile.getAbsolutePath();
               EjbModule ejbModule = this.createEjbModule(ejbUrl, absolutePath, appClassLoader);
               appModule.getEjbModules().add(ejbModule);
            } catch (OpenEJBException e) {
               logger.error("Unable to load EJBs from EAR: " + appId + ", module: " + moduleName + ". Exception: " + e.getMessage(), (Throwable)e);
            }
         }

         for(String moduleName : clientModules.keySet()) {
            try {
               URL clientUrl = (URL)clientModules.get(moduleName);
               if (ClassLoaderUtil.isUrlCached(appModule.getJarLocation(), clientUrl)) {
                  try {
                     clientUrl = ClassLoaderUtil.getUrlCachedName(appModule.getJarLocation(), clientUrl).toURI().toURL();
                  } catch (MalformedURLException var42) {
                  }
               }

               File clientFile = URLs.toFile(clientUrl);
               String absolutePath = clientFile.getAbsolutePath();
               ClientModule clientModule = this.createClientModule(clientUrl, absolutePath, appClassLoader, (String)null);
               appModule.getClientModules().add(clientModule);
            } catch (Exception e) {
               logger.error("Unable to load App Client from EAR: " + appId + ", module: " + moduleName + ". Exception: " + e.getMessage(), (Throwable)e);
            }
         }

         for(String moduleName : resouceModules.keySet()) {
            try {
               URL rarUrl = (URL)resouceModules.get(moduleName);
               if (ClassLoaderUtil.isUrlCached(appModule.getJarLocation(), rarUrl)) {
                  try {
                     rarUrl = ClassLoaderUtil.getUrlCachedName(appModule.getJarLocation(), rarUrl).toURI().toURL();
                  } catch (MalformedURLException var40) {
                  }
               }

               ConnectorModule connectorModule = createConnectorModule(appId, URLs.toFilePath(rarUrl), appClassLoader, moduleName);
               if (connectorModule != null) {
                  appModule.getConnectorModules().add(connectorModule);
               }
            } catch (OpenEJBException e) {
               logger.error("Unable to load RAR: " + appId + ", module: " + moduleName + ". Exception: " + e.getMessage(), (Throwable)e);
            }
         }

         for(String moduleName : webModules.keySet()) {
            try {
               URL warUrl = (URL)webModules.get(moduleName);
               this.addWebModule(appModule, warUrl, appClassLoader, (String)webContextRoots.get(moduleName), (String)null);
            } catch (OpenEJBException e) {
               logger.error("Unable to load WAR: " + appId + ", module: " + moduleName + ". Exception: " + e.getMessage(), (Throwable)e);
            }
         }

         this.addBeansXmls(appModule);
         Properties p = new Properties();
         p.put(appModule.getModuleId(), appModule.getJarLocation());
         FileUtils base = new FileUtils(appModule.getModuleId(), appModule.getModuleId(), p);
         List<URL> filteredUrls = new ArrayList();
         DeploymentsResolver.loadFromClasspath(base, filteredUrls, appModule.getClassLoader());
         addPersistenceUnits(appModule, (URL[])filteredUrls.toArray(new URL[filteredUrls.size()]));
         Object pXmls = appModule.getAltDDs().get("persistence.xml");
         Iterator var93 = appModule.getWebModules().iterator();

         while(true) {
            List<URL> foundRootUrls;
            WebModule webModule;
            label222:
            while(true) {
               if (!var93.hasNext()) {
                  for(DeploymentModule module : appModule.getDeploymentModule()) {
                     module.setStandaloneModule(false);
                  }

                  return appModule;
               }

               webModule = (WebModule)var93.next();
               foundRootUrls = new ArrayList();

               for(URL url : webModule.getScannableUrls()) {
                  if (!addPersistenceUnits(appModule, url).isEmpty()) {
                     foundRootUrls.add(url);
                  }
               }

               if (pXmls == null || !Collection.class.isInstance(pXmls)) {
                  break;
               }

               File webapp = webModule.getFile();
               if (webapp != null) {
                  String webappAbsolutePath = webapp.getAbsolutePath();
                  Collection<URL> list = (Collection)Collection.class.cast(pXmls);
                  Iterator var35 = list.iterator();

                  while(true) {
                     if (!var35.hasNext()) {
                        break label222;
                     }

                     URL url = (URL)var35.next();

                     try {
                        File file = URLs.toFile(url);
                        if (file.getAbsolutePath().startsWith(webappAbsolutePath)) {
                           foundRootUrls.add(url);
                        }
                     } catch (IllegalArgumentException var38) {
                     }
                  }
               }
            }

            webModule.getAltDDs().put("ear-webapp-persistence-xml-jars", foundRootUrls);
         }
      } catch (OpenEJBException e) {
         logger.error("Unable to load EAR: " + jarPath, (Throwable)e);
         throw e;
      }
   }

   private void createApplicationFromFiles(String appId, ClassLoader tmpClassLoader, Map<String, URL> ejbModules, Map<String, URL> clientModules, Map<String, URL> resouceModules, Map<String, URL> webModules, HashMap<String, URL> files) throws OpenEJBException {
      for(Map.Entry<String, URL> entry : files.entrySet()) {
         if (((String)entry.getKey()).matches(".*\\.(jar|war|rar|ear)")) {
            try {
               this.detectAndAddModuleToApplication(appId, tmpClassLoader, ejbModules, clientModules, resouceModules, webModules, entry);
            } catch (UnknownModuleTypeException | UnsupportedOperationException var11) {
               logger.info("Ignoring unknown module type: " + (String)entry.getKey());
            } catch (Exception e) {
               throw new OpenEJBException("Unable to determine the module type of " + (String)entry.getKey() + ": Exception: " + e.getMessage(), e);
            }
         }
      }

   }

   private void detectAndAddModuleToApplication(String appId, ClassLoader tmpClassLoader, Map<String, URL> ejbModules, Map<String, URL> clientModules, Map<String, URL> resouceModules, Map<String, URL> webModules, Map.Entry<String, URL> entry) throws IOException, UnknownModuleTypeException {
      ClassLoader moduleClassLoader = ClassLoaderUtil.createTempClassLoader(appId, new URL[]{(URL)entry.getValue()}, tmpClassLoader);
      Class<? extends DeploymentModule> moduleType = this.discoverModuleType((URL)entry.getValue(), moduleClassLoader, true);
      if (EjbModule.class.equals(moduleType)) {
         ejbModules.put(entry.getKey(), entry.getValue());
      } else if (ClientModule.class.equals(moduleType)) {
         clientModules.put(entry.getKey(), entry.getValue());
      } else if (ConnectorModule.class.equals(moduleType)) {
         resouceModules.put(entry.getKey(), entry.getValue());
      } else if (WebModule.class.equals(moduleType)) {
         webModules.put(entry.getKey(), entry.getValue());
      }

   }

   protected ClientModule createClientModule(URL clientUrl, String absolutePath, ClassLoader appClassLoader, String moduleName) throws OpenEJBException {
      return this.createClientModule(clientUrl, absolutePath, appClassLoader, moduleName, true);
   }

   protected ClientModule createClientModule(URL clientUrl, String absolutePath, ClassLoader appClassLoader, String moduleName, boolean log) throws OpenEJBException {
      ResourceFinder clientFinder = new ResourceFinder(new URL[]{clientUrl});
      URL manifestUrl = null;

      try {
         manifestUrl = clientFinder.find("META-INF/MANIFEST.MF");
      } catch (IOException var14) {
      }

      String mainClass = null;
      if (manifestUrl != null) {
         try {
            InputStream is = IO.read(manifestUrl);
            Manifest manifest = new Manifest(is);
            mainClass = manifest.getMainAttributes().getValue(Name.MAIN_CLASS);
         } catch (IOException e) {
            throw new OpenEJBException("Unable to determine Main-Class defined in META-INF/MANIFEST.MF file", e);
         }
      }

      Map<String, URL> descriptors = getDescriptors(clientFinder, log);
      ApplicationClient applicationClient = null;
      URL clientXmlUrl = (URL)descriptors.get("application-client.xml");
      if (clientXmlUrl != null) {
         applicationClient = ReadDescriptors.readApplicationClient(clientXmlUrl);
      }

      ClientModule clientModule = new ClientModule(applicationClient, appClassLoader, absolutePath, mainClass, moduleName);
      clientModule.getAltDDs().putAll(descriptors);
      if (absolutePath != null) {
         clientModule.getWatchedResources().add(absolutePath);
      }

      if (clientXmlUrl != null && "file".equals(clientXmlUrl.getProtocol())) {
         clientModule.getWatchedResources().add(URLs.toFilePath(clientXmlUrl));
      }

      return clientModule;
   }

   protected EjbModule createEjbModule(URL baseUrl, String jarPath, ClassLoader classLoader) throws OpenEJBException {
      Map<String, URL> descriptors;
      if (baseUrl != null) {
         descriptors = getDescriptors(baseUrl);
      } else {
         try {
            descriptors = this.getDescriptors(classLoader, (URL)null);
         } catch (IOException var9) {
            descriptors = new HashMap();
         }
      }

      EjbJar ejbJar = null;
      URL ejbJarXmlUrl = (URL)descriptors.get("ejb-jar.xml");
      if (ejbJarXmlUrl != null) {
         try {
            ejbJar = ReadDescriptors.readEjbJar(ejbJarXmlUrl.openStream());
         } catch (IOException e) {
            throw new OpenEJBException(e);
         }
      }

      EjbModule ejbModule = new EjbModule(classLoader, (String)null, jarPath, ejbJar, (OpenejbJar)null);
      ejbModule.getAltDDs().putAll(descriptors);
      if (jarPath != null) {
         ejbModule.getWatchedResources().add(jarPath);
      }

      if (ejbJarXmlUrl != null && "file".equals(ejbJarXmlUrl.getProtocol())) {
         ejbModule.getWatchedResources().add(URLs.toFilePath(ejbJarXmlUrl));
      }

      ejbModule.setClientModule(this.createClientModule(baseUrl, jarPath, classLoader, (String)null, false));
      addWebservices(ejbModule);
      return ejbModule;
   }

   private WebModule createWebModule(String jar, URL warUrl, ClassLoader parentClassLoader, String contextRoot, String moduleName, ExternalConfiguration config) throws OpenEJBException {
      return this.createWebModule(jar, URLs.toFilePath(warUrl), parentClassLoader, contextRoot, moduleName, config);
   }

   public void addWebModule(AppModule appModule, URL warUrl, ClassLoader parentClassLoader, String contextRoot, String moduleName) throws OpenEJBException {
      WebModule webModule = this.createWebModule(appModule.getJarLocation(), (String)URLs.toFilePath(warUrl), parentClassLoader, contextRoot, moduleName, (ExternalConfiguration)null);
      addWebModule(webModule, appModule);
   }

   public static EjbModule addWebModule(WebModule webModule, AppModule appModule) throws OpenEJBException {
      appModule.getWebModules().add(webModule);
      if (appModule.isStandaloneModule()) {
         appModule.getAdditionalLibraries().addAll(webModule.getUrls());
      }

      Object pXml = appModule.getAltDDs().get("persistence.xml");
      List<URL> persistenceXmls = (List<URL>)(pXml == null ? null : (List.class.isInstance(pXml) ? (List)pXml : new ArrayList(Arrays.asList((URL)URL.class.cast(pXml)))));
      if (persistenceXmls == null) {
         persistenceXmls = new ArrayList();
         appModule.getAltDDs().put("persistence.xml", persistenceXmls);
      }

      Object o = webModule.getAltDDs().get("persistence.xml");
      if (o instanceof URL) {
         URL url = (URL)o;
         persistenceXmls.add(url);
      }

      if (o instanceof List) {
         List<URL> urls = (List)o;
         persistenceXmls.addAll(urls);
      }

      pXml = new EjbModule(webModule.getClassLoader(), webModule.getModuleId(), webModule.getJarLocation(), (EjbJar)null, (OpenejbJar)null);
      ((EjbModule)pXml).setWebapp(true);
      ((EjbModule)pXml).getAltDDs().putAll(webModule.getAltDDs());
      appModule.getEjbModules().add(pXml);

      try {
         fillEjbJar(webModule, (EjbModule)pXml);
         if (webModule.getFinder() == null) {
            if (isMetadataComplete(webModule, (EjbModule)pXml)) {
               persistenceXmls = new AnnotationFinder(new ClassesArchive(new Class[0]));
               webModule.setFinder(persistenceXmls);
               ((EjbModule)pXml).setFinder(persistenceXmls);
            } else {
               IAnnotationFinder finder = FinderFactory.createFinder(webModule);
               webModule.setFinder(finder);
               ((EjbModule)pXml).setFinder(finder);
            }
         } else if (((EjbModule)pXml).getFinder() == null) {
            ((EjbModule)pXml).setFinder(webModule.getFinder());
         }
      } catch (Exception e) {
         throw new OpenEJBException("Unable to create annotation scanner for web module " + webModule.getModuleId(), e);
      }

      addWebservices((WsModule)pXml);
      return (EjbModule)pXml;
   }

   private static void fillEjbJar(WebModule webModule, EjbModule ejbModule) {
      Object o = webModule.getAltDDs().get("ejb-jar.xml");
      if (o == null) {
         if (ejbModule.getEjbJar() == null) {
            EjbJar ejbJar = new EjbJar();
            WebApp webApp = webModule.getWebApp();
            ejbJar.setMetadataComplete(webApp.isMetadataComplete());
            ejbModule.setEjbJar(ejbJar);
         }
      }
   }

   private static boolean isMetadataComplete(WebModule webModule, EjbModule ejbModule) {
      if (webModule.getWebApp() == null) {
         return false;
      } else if (!webModule.getWebApp().isMetadataComplete()) {
         return false;
      } else if (webModule.getAltDDs().get("beans.xml") == null) {
         return true;
      } else {
         return ejbModule.getEjbJar() == null ? true : ejbModule.getEjbJar().isMetadataComplete();
      }
   }

   public WebModule createWebModule(String appId, String warPath, ClassLoader parentClassLoader, String contextRoot, String moduleName, ExternalConfiguration config) throws OpenEJBException {
      File warFile = new File(warPath);
      if (!warFile.isDirectory()) {
         warFile = unpack(warFile);
      }

      Map<String, URL> descriptors;
      try {
         descriptors = getWebDescriptors(warFile);
      } catch (IOException e) {
         throw new OpenEJBException("Unable to collect descriptors in web module: " + contextRoot, e);
      }

      URL webXmlUrl = (URL)descriptors.get("web.xml");
      WebApp webApp;
      if (webXmlUrl != null) {
         webApp = ReadDescriptors.readWebApp(webXmlUrl);
      } else {
         webApp = new WebApp();
      }

      List<URL> webUrls = new ArrayList();
      this.ensureContainerUrls();
      webUrls.addAll(this.containerUrls);
      SystemInstance systemInstance = SystemInstance.get();
      String externalRepos = systemInstance.getProperty("tomee." + warFile.getName().replace(".war", "") + ".externalRepositories");
      List<URL> externalUrls = null;
      if (externalRepos != null) {
         externalUrls = new ArrayList();

         for(String additional : externalRepos.split(",")) {
            String trim = additional.trim();
            if (!trim.isEmpty()) {
               try {
                  externalUrls.add((new File(trim)).toURI().toURL());
               } catch (MalformedURLException e) {
                  logger.error(e.getMessage());
               }
            }
         }

         webUrls.addAll(externalUrls);
      }

      Map<String, URL[]> urls = getWebappUrlsAndRars(warFile);
      webUrls.addAll(Arrays.asList(urls.get("urls")));
      List<URL> addedUrls = new ArrayList();

      for(URL url : (URL[])urls.get("rar-urls")) {
         File[] files = unpack(URLs.toFile(url)).listFiles();
         if (files != null) {
            for(File f : files) {
               if (f.getName().endsWith(".jar")) {
                  try {
                     addedUrls.add(f.toURI().toURL());
                  } catch (MalformedURLException e) {
                     logger.warning("War path bad: " + f.getAbsolutePath(), (Throwable)e);
                  }
               }
            }
         }
      }

      webUrls.addAll(addedUrls);
      if (config != null && config.getClasspath() != null && config.getClasspath().length > 0) {
         Set<URL> contextXmlUrls = new LinkedHashSet();

         for(String location : config.getClasspath()) {
            try {
               webUrls.add((new File(location)).toURI().toURL());
            } catch (MalformedURLException e) {
               throw new IllegalArgumentException(e);
            }
         }

         webUrls.addAll(contextXmlUrls);
      }

      ClassLoaderConfigurer configurer = QuickJarsTxtParser.parse(new File(warFile, "WEB-INF/jars.txt"));
      if (configurer != null) {
         ClassLoaderConfigurer.Helper.configure(webUrls, configurer);
      }

      URL[] webUrlsArray = (URL[])webUrls.toArray(new URL[webUrls.size()]);
      systemInstance.fireEvent(new BeforeDeploymentEvent(webUrlsArray, parentClassLoader));
      ClassLoader warClassLoader = ClassLoaderUtil.createTempClassLoader(appId, webUrlsArray, parentClassLoader);
      List<URL> scannableUrls = filterWebappUrls(webUrlsArray, config == null ? null : config.customerFilter, (URL)descriptors.get("exclusions.list"));
      if (Boolean.parseBoolean(systemInstance.getProperty("openejb.core.skip-war-in-loader", "true"))) {
         File archive = warFile;
         if (!warFile.getName().endsWith(".war")) {
            archive = new File(warFile.getParentFile(), warFile.getName() + ".war");
            String unpackDir = systemInstance.getProperty("tomee.unpack.dir");
            if (unpackDir != null && !archive.isFile()) {
               try {
                  archive = new File(systemInstance.getBase().getDirectory(unpackDir, false), warFile.getName());
               } catch (IOException var28) {
               }
            }
         }

         if (archive.isFile()) {
            try {
               scannableUrls.remove(archive.toURI().toURL());
            } catch (MalformedURLException var27) {
            }
         }
      }

      if (externalUrls != null) {
         for(URL url : externalUrls) {
            if (scannableUrls.contains(url)) {
               scannableUrls.remove(url);
               scannableUrls.add(0, url);
            }
         }
      }

      WebModule webModule = new WebModule(webApp, contextRoot, warClassLoader, warFile.getAbsolutePath(), moduleName);
      webModule.setUrls(webUrls);
      webModule.setAddedUrls(addedUrls);
      webModule.setRarUrls(Arrays.asList(urls.get("rar-urls")));
      webModule.setScannableUrls(scannableUrls);
      webModule.getAltDDs().putAll(descriptors);
      webModule.getWatchedResources().add(warPath);
      webModule.getWatchedResources().add(warFile.getAbsolutePath());
      if (webXmlUrl != null && "file".equals(webXmlUrl.getProtocol())) {
         webModule.getWatchedResources().add(URLs.toFilePath(webXmlUrl));
      }

      this.addTagLibraries(webModule);
      addWebservices(webModule);
      this.addFacesConfigs(webModule);
      addBeansXmls(webModule);
      return webModule;
   }

   private void ensureContainerUrls() {
      if (this.containerUrls == null) {
         if ("true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.scan.webapp.container", "false"))) {
            synchronized(this) {
               if (this.containerUrls == null) {
                  try {
                     UrlSet urlSet = new UrlSet(ParentClassLoaderFinder.Helper.get());
                     urlSet = URLs.cullSystemJars(urlSet);
                     urlSet = NewLoaderLogic.applyBuiltinExcludes(urlSet);
                     this.containerUrls = urlSet.getUrls();
                     boolean skipContainerFolders = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.scan.webapp.container.skip-folder", "true"));
                     Iterator<URL> it = this.containerUrls.iterator();

                     while(it.hasNext()) {
                        File file = URLs.toFile((URL)it.next());
                        String name = file.getName();
                        if (skipContainerFolders && file.isDirectory() || name.endsWith("tomcat-websocket.jar") || name.startsWith("commons-jcs-") || name.startsWith("xx-arquillian-tomee") || "lib".equals(name) && file.isDirectory() && (new File(JavaSecurityManagers.getSystemProperty("openejb.base", "-"))).equals(file.getParentFile())) {
                           it.remove();
                        }
                     }
                  } catch (Exception e) {
                     logger.error(e.getMessage(), (Throwable)e);
                  }
               }
            }
         } else {
            this.containerUrls = Collections.emptyList();
         }
      }

   }

   public static List<URL> filterWebappUrls(URL[] webUrls, Filter filter, URL exclusions) {
      Filter excludeFilter = null;
      if (exclusions != null) {
         try {
            String[] prefixes = NewLoaderLogic.readInputStreamList(exclusions.openStream());
            excludeFilter = Filters.prefixes(prefixes);
         } catch (IOException var7) {
            logger.warning("can't read " + exclusions.toExternalForm());
         }
      }

      UrlSet urls = new UrlSet(webUrls);

      try {
         urls = NewLoaderLogic.applyBuiltinExcludes(urls, filter, excludeFilter);
      } catch (MalformedURLException var6) {
         return Arrays.asList(webUrls);
      }

      return urls.getUrls();
   }

   public static void addBeansXmls(WebModule webModule) {
      List<URL> urls = webModule.getScannableUrls();
      URLClassLoader loader = new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]), new EmptyResourcesClassLoader());
      List<URL> xmls = new LinkedList();

      try {
         URL e = (URL)webModule.getAltDDs().get("beans.xml");
         if (e != null) {
            xmls.add(e);
         }

         xmls.addAll(Collections.list(loader.getResources("META-INF/beans.xml")));
      } catch (IOException var7) {
         return;
      }

      CompositeBeans complete = new CompositeBeans();

      for(URL url : xmls) {
         if (url != null) {
            mergeBeansXml(complete, url);
         }
      }

      if (!complete.getDiscoveryByUrl().isEmpty()) {
         complete.removeDuplicates();
      }

      webModule.getAltDDs().put("beans.xml", complete);
   }

   private static Beans mergeBeansXml(CompositeBeans current, URL url) {
      try {
         Beans beans;
         try {
            beans = ReadDescriptors.readBeans(url.openStream());
         } catch (IOException var4) {
            return current;
         }

         doMerge(url, current, beans);
      } catch (OpenEJBException e) {
         logger.error("Unable to read beans.xml from: " + url.toExternalForm(), (Throwable)e);
      }

      return current;
   }

   public static void doMerge(URL url, CompositeBeans current, Beans beans) {
      current.mergeClasses(url, beans);
      current.getScan().getExclude().addAll(beans.getScan().getExclude());
      ReadDescriptors.checkDuplicatedByBeansXml(beans, current);
      String beanDiscoveryMode = beans.getBeanDiscoveryMode();
      current.getDiscoveryByUrl().put(url, beanDiscoveryMode == null ? "ALL" : beanDiscoveryMode);
   }

   private void addBeansXmls(AppModule appModule) {
      List<URL> urls = appModule.getAdditionalLibraries();
      URLClassLoader loader = new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]));

      ArrayList<URL> xmls;
      try {
         xmls = Collections.list(loader.getResources("META-INF/beans.xml"));
      } catch (IOException var9) {
         return;
      }

      CompositeBeans complete = new CompositeBeans();

      for(URL url : xmls) {
         if (url != null) {
            mergeBeansXml(complete, url);
         }
      }

      if (!complete.getDiscoveryByUrl().isEmpty()) {
         complete.removeDuplicates();
         this.ensureContainerUrls();
         appModule.getScannableContainerUrls().addAll(this.containerUrls);

         IAnnotationFinder finder;
         try {
            finder = FinderFactory.createFinder(appModule);
         } catch (Exception var8) {
            finder = new FinderFactory.ModuleLimitedFinder(new FinderFactory.OpenEJBAnnotationFinder(new WebappAggregatedArchive(appModule.getClassLoader(), appModule.getAltDDs(), xmls)));
         }

         appModule.setEarLibFinder(finder);
         EjbModule ejbModule = new EjbModule(appModule.getClassLoader(), "ear-scoped-cdi-beans_" + appModule.getModuleId(), new EjbJar(), new OpenejbJar());
         ejbModule.setBeans(complete);
         ejbModule.setFinder(finder);
         ejbModule.setEjbJar(new EmptyEjbJar());
         appModule.getEjbModules().add(ejbModule);
      }
   }

   protected String getContextRoot() {
      return null;
   }

   protected String getModuleName() {
      return null;
   }

   public static Map<String, URL[]> getWebappUrlsAndRars(File warFile) {
      Set<URL> webClassPath = new HashSet();
      Set<URL> webRars = new HashSet();
      File webInfDir = new File(warFile, "WEB-INF");

      try {
         webClassPath.add((new File(webInfDir, "classes")).toURI().toURL());
      } catch (MalformedURLException e) {
         logger.warning("War path bad: " + new File(webInfDir, "classes"), (Throwable)e);
      }

      File libDir = new File(webInfDir, "lib");
      if (libDir.exists()) {
         File[] list = libDir.listFiles();
         if (list != null) {
            for(File file : list) {
               String name = file.getName();
               if (!name.endsWith(".jar") && !name.endsWith(".zip")) {
                  if (name.endsWith(".rar")) {
                     try {
                        webRars.add(file.toURI().toURL());
                     } catch (MalformedURLException e) {
                        logger.warning("War path bad: " + file, (Throwable)e);
                     }
                  }
               } else {
                  try {
                     webClassPath.add(file.toURI().toURL());
                  } catch (MalformedURLException e) {
                     logger.warning("War path bad: " + file, (Throwable)e);
                  }
               }
            }
         }
      }

      WebAppEnricher enricher = (WebAppEnricher)SystemInstance.get().getComponent(WebAppEnricher.class);
      if (enricher != null) {
         webClassPath.addAll(Arrays.asList(enricher.enrichment((ClassLoader)null)));
      }

      Map<String, URL[]> urls = new HashMap();
      urls.put("urls", webClassPath.toArray(new URL[webClassPath.size()]));
      urls.put("rar-urls", webRars.toArray(new URL[webRars.size()]));
      return urls;
   }

   public static URL[] getWebappUrls(File warFile) {
      return (URL[])getWebappUrlsAndRars(warFile).get("urls");
   }

   private static void addWebservices(WsModule wsModule) throws OpenEJBException {
      boolean webservicesEnabled = SystemInstance.get().getOptions().get("openejb.webservices.enabled", true);
      if (!webservicesEnabled) {
         wsModule.getAltDDs().remove("webservices.xml");
         wsModule.setWebservices((Webservices)null);
      } else {
         Object webservicesObject = wsModule.getAltDDs().get("webservices.xml");
         if (webservicesObject != null && webservicesObject instanceof URL) {
            URL webservicesUrl = (URL)webservicesObject;

            URL moduleUrl;
            try {
               File jarFile = new File(wsModule.getJarLocation());
               moduleUrl = jarFile.toURI().toURL();
               if (jarFile.isFile()) {
                  moduleUrl = new URL("jar", "", -1, moduleUrl + "!/");
               }
            } catch (MalformedURLException var13) {
               logger.warning("Invalid module location " + wsModule.getJarLocation());
               return;
            }

            Map<URL, JavaWsdlMapping> jaxrpcMappingCache = new HashMap();
            Webservices webservices = ReadDescriptors.readWebservices(webservicesUrl);
            wsModule.setWebservices(webservices);
            if ("file".equals(webservicesUrl.getProtocol())) {
               wsModule.getWatchedResources().add(URLs.toFilePath(webservicesUrl));
            }

            for(WebserviceDescription webserviceDescription : webservices.getWebserviceDescription()) {
               String jaxrpcMappingFile = webserviceDescription.getJaxrpcMappingFile();
               if (jaxrpcMappingFile != null) {
                  try {
                     URL jaxrpcMappingUrl = new URL(moduleUrl, jaxrpcMappingFile);
                     JavaWsdlMapping jaxrpcMapping = (JavaWsdlMapping)jaxrpcMappingCache.get(jaxrpcMappingUrl);
                     if (jaxrpcMapping == null) {
                        jaxrpcMapping = ReadDescriptors.readJaxrpcMapping(jaxrpcMappingUrl);
                        jaxrpcMappingCache.put(jaxrpcMappingUrl, jaxrpcMapping);
                     }

                     webserviceDescription.setJaxrpcMapping(jaxrpcMapping);
                     if ("file".equals(jaxrpcMappingUrl.getProtocol())) {
                        wsModule.getWatchedResources().add(URLs.toFilePath(jaxrpcMappingUrl));
                     }
                  } catch (MalformedURLException var12) {
                     logger.warning("Invalid jaxrpc-mapping-file location " + jaxrpcMappingFile);
                  }
               }
            }

         }
      }
   }

   private void addTagLibraries(WebModule webModule) throws OpenEJBException {
      Set<URL> tldLocations = new HashSet();
      File warFile = new File(webModule.getJarLocation());
      WebApp webApp = webModule.getWebApp();
      if (webApp != null) {
         for(JspConfig jspConfig : webApp.getJspConfig()) {
            for(Taglib taglib : jspConfig.getTaglib()) {
               String location = taglib.getTaglibLocation();
               if (!location.startsWith("/")) {
                  location = "/WEB-INF/" + location;
               }

               try {
                  File file = (new File(warFile, location)).getCanonicalFile().getAbsoluteFile();
                  tldLocations.addAll(TldScanner.scanForTagLibs(file));
               } catch (IOException e) {
                  logger.warning("JSP tag library location bad: " + location, (Throwable)e);
               }
            }
         }
      }

      Set<URL> urls = TldScanner.scanWarForTagLibs(warFile);
      tldLocations.addAll(urls);
      ClassLoader parentClassLoader = webModule.getClassLoader().getParent();
      urls = TldScanner.scan(parentClassLoader);
      tldLocations.addAll(urls);

      for(URL location : tldLocations) {
         TldTaglib taglib = ReadDescriptors.readTldTaglib(location);
         if (taglib != null && taglib != ReadDescriptors.SKIP_TAGLIB) {
            webModule.getTaglibs().add(taglib);
            if ("file".equals(location.getProtocol())) {
               webModule.getWatchedResources().add(URLs.toFilePath(location));
            }
         }
      }

      TldScanner.quickClean(parentClassLoader);
   }

   private void addFacesConfigs(WebModule webModule) throws OpenEJBException {
      Set<URL> facesConfigLocations = new HashSet();
      File warFile = new File(webModule.getJarLocation());
      WebApp webApp = webModule.getWebApp();
      if (webApp != null) {
         String foundContextParam = (String)webApp.contextParamsAsMap().get("javax.faces.CONFIG_FILES");
         if (foundContextParam != null) {
            String commaDelimitedListOfFiles = foundContextParam.trim();
            String[] configFiles = commaDelimitedListOfFiles.split(",");
            String[] trimmedConfigFiles = new String[configFiles.length];

            for(int i = 0; i < configFiles.length; ++i) {
               trimmedConfigFiles[i] = configFiles[i].trim();
            }

            for(String location : trimmedConfigFiles) {
               if (!location.startsWith("/")) {
                  logger.error("A faces configuration file should be context relative when specified in web.xml. Please fix the value of context parameter javax.faces.CONFIG_FILES for the file " + location);
               }

               try {
                  File file = (new File(warFile, location)).getCanonicalFile().getAbsoluteFile();
                  URL url = file.toURI().toURL();
                  facesConfigLocations.add(url);
               } catch (IOException e) {
                  logger.error("Faces configuration file location bad: " + location, (Throwable)e);
               }
            }
         } else {
            logger.debug("faces config file is null");
         }
      }

      File webInf = new File(warFile, "WEB-INF");
      if (webInf.isDirectory()) {
         File facesConfigFile = new File(webInf, "faces-config.xml");
         if (facesConfigFile.exists()) {
            try {
               facesConfigFile = facesConfigFile.getCanonicalFile().getAbsoluteFile();
               URL url = facesConfigFile.toURI().toURL();
               facesConfigLocations.add(url);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

      for(URL location : facesConfigLocations) {
         FacesConfig facesConfig = ReadDescriptors.readFacesConfig(location);
         webModule.getFacesConfigs().add(facesConfig);
         if ("file".equals(location.getProtocol())) {
            webModule.getWatchedResources().add(URLs.toFilePath(location));
         }
      }

   }

   protected static ConnectorModule createConnectorModule(String appId, String rarPath, ClassLoader parentClassLoader, String moduleId) throws OpenEJBException {
      return createConnectorModule(appId, rarPath, parentClassLoader, moduleId, (URL)null);
   }

   protected static ConnectorModule createConnectorModule(String appId, String rarPath, ClassLoader parentClassLoader, String moduleId, URL raXmlUrl) throws OpenEJBException {
      File rarFile = new File(rarPath);
      if (!rarFile.exists()) {
         logger.warning(rarPath + " doesn't exist, skipping connector");
         return null;
      } else {
         rarFile = unpack(rarFile);
         URL baseUrl = getFileUrl(rarFile);
         Map<String, URL> descriptors = getDescriptors(baseUrl);
         Connector connector = null;
         URL rarXmlUrl = (URL)descriptors.get("ra.xml");
         if (rarXmlUrl == null && raXmlUrl != null) {
            descriptors.put("ra.xml", raXmlUrl);
            rarXmlUrl = raXmlUrl;
         }

         if (rarXmlUrl != null) {
            connector = ReadDescriptors.readConnector(rarXmlUrl);
         }

         HashMap<String, URL> rarLibs = new HashMap();
         scanDir(rarFile, rarLibs, "");
         Iterator<Map.Entry<String, URL>> iterator = rarLibs.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry<String, URL> fileEntry = (Map.Entry)iterator.next();
            if (!((String)fileEntry.getKey()).endsWith(".jar")) {
               iterator.remove();
            }
         }

         List<URL> classPath = new ArrayList();
         classPath.addAll(rarLibs.values());
         ClassLoaderConfigurer configurer = QuickJarsTxtParser.parse(new File(rarFile, "META-INF/jars.txt"));
         if (configurer != null) {
            ClassLoaderConfigurer.Helper.configure(classPath, configurer);
         }

         URL[] urls = (URL[])classPath.toArray(new URL[classPath.size()]);
         ClassLoader appClassLoader = ClassLoaderUtil.createTempClassLoader(appId, urls, parentClassLoader);
         ConnectorModule connectorModule = new ConnectorModule(connector, appClassLoader, rarPath, moduleId);
         connectorModule.getAltDDs().putAll(descriptors);
         connectorModule.getLibraries().addAll(classPath);
         connectorModule.getWatchedResources().add(rarPath);
         connectorModule.getWatchedResources().add(rarFile.getAbsolutePath());
         if (rarXmlUrl != null && "file".equals(rarXmlUrl.getProtocol())) {
            connectorModule.getWatchedResources().add(URLs.toFilePath(rarXmlUrl));
         }

         return connectorModule;
      }
   }

   protected static void addWebFragments(WebModule webModule, Collection<URL> urls) throws OpenEJBException {
      if (urls != null) {
         List<URL> webFragmentUrls;
         try {
            webFragmentUrls = (List)webModule.getAltDDs().get("web-fragment.xml");
         } catch (ClassCastException var9) {
            Object value = webModule.getAltDDs().get("web-fragment.xml");
            webFragmentUrls = new ArrayList();
            webFragmentUrls.add(URL.class.cast(value));
            webModule.getAltDDs().put("web-fragment.xml", webFragmentUrls);
         }

         if (webFragmentUrls == null) {
            webFragmentUrls = new ArrayList();
            webModule.getAltDDs().put("web-fragment.xml", webFragmentUrls);
         }

         for(URL url : urls) {
            ResourceFinder finder = new ResourceFinder("", webModule.getClassLoader(), new URL[]{url});
            Map<String, URL> descriptors = getDescriptors(finder, false);
            if (descriptors.containsKey("web-fragment.xml")) {
               URL descriptor = (URL)descriptors.get("web-fragment.xml");
               if (!webFragmentUrls.contains(descriptor)) {
                  String urlString = descriptor.toExternalForm();
                  if (!urlString.contains("META-INF/web-fragment.xml")) {
                     logger.info("AltDD persistence.xml -> " + urlString);
                  }

                  webFragmentUrls.add(descriptor);
               }
            }
         }

      }
   }

   protected static Collection<URL> addPersistenceUnits(AppModule appModule, URL... urls) throws OpenEJBException {
      Collection<URL> added = new ArrayList();

      List<URL> persistenceUrls;
      try {
         persistenceUrls = (List)appModule.getAltDDs().get("persistence.xml");
      } catch (ClassCastException var13) {
         Object value = appModule.getAltDDs().get("persistence.xml");
         persistenceUrls = new ArrayList();
         persistenceUrls.add(URL.class.cast(value));
         added.add(persistenceUrls.iterator().next());
         appModule.getAltDDs().put("persistence.xml", persistenceUrls);
      }

      if (persistenceUrls == null) {
         persistenceUrls = new ArrayList();
         appModule.getAltDDs().put("persistence.xml", persistenceUrls);
      }

      List<URL> persistenceFragmentsUrls = (List)appModule.getAltDDs().get("persistence-fragment.xml");
      if (persistenceFragmentsUrls == null) {
         persistenceFragmentsUrls = new ArrayList();
         appModule.getAltDDs().put("persistence-fragment.xml", persistenceFragmentsUrls);
      }

      for(URL url : urls) {
         ResourceFinder finder = new ResourceFinder("", appModule.getClassLoader(), new URL[]{url});
         Map<String, URL> descriptors = getDescriptors(finder, false);
         if (descriptors.containsKey("persistence.xml")) {
            URL descriptor = (URL)descriptors.get("persistence.xml");
            if (!persistenceUrls.contains(descriptor)) {
               String urlString = descriptor.toExternalForm();
               if (!urlString.contains("META-INF/persistence.xml")) {
                  logger.info("AltDD persistence.xml -> " + urlString);
               }

               persistenceUrls.add(descriptor);
               added.add(descriptor);
            }
         }
      }

      for(URL url : urls) {
         ResourceFinder finder = new ResourceFinder("", appModule.getClassLoader(), new URL[]{url});
         Map<String, URL> descriptors = getDescriptors(finder, false);
         if (descriptors.containsKey("persistence-fragment.xml")) {
            URL descriptor = (URL)descriptors.get("persistence-fragment.xml");
            if (!persistenceFragmentsUrls.contains(descriptor)) {
               String urlString = descriptor.toExternalForm();
               if (!urlString.contains("META-INF/persistence-fragment.xml")) {
                  logger.info("AltDD persistence-fragment.xml -> " + urlString);
               }

               persistenceFragmentsUrls.add(descriptor);
               added.add(descriptor);
            }
         }
      }

      return added;
   }

   public static Map<String, URL> getDescriptors(URL moduleUrl) throws OpenEJBException {
      ResourceFinder finder = new ResourceFinder(new URL[]{moduleUrl});
      return getDescriptors(finder);
   }

   private static Map<String, URL> getDescriptors(ResourceFinder finder) throws OpenEJBException {
      return getDescriptors(finder, true);
   }

   private static Map<String, URL> getDescriptors(ResourceFinder finder, boolean log) throws OpenEJBException {
      try {
         return altDDSources(mapDescriptors(finder), log);
      } catch (IOException e) {
         throw new OpenEJBException("Unable to determine descriptors in jar.", e);
      }
   }

   public static Map<String, URL> mapDescriptors(ResourceFinder finder) throws IOException {
      Map<String, URL> map = finder.getResourcesMap("META-INF/");
      if (map.size() == 0) {
         for(String descriptor : KNOWN_DESCRIPTORS) {
            URL url = finder.getResource("META-INF/" + descriptor);
            if (url != null) {
               map.put(descriptor, url);
            }
         }
      }

      return map;
   }

   public static Map<String, URL> altDDSources(Map<String, URL> map, boolean log) {
      if (ALTDD != null && ALTDD.length() > 0) {
         List<String> list = new ArrayList(Arrays.asList(ALTDD.split(",")));
         Collections.reverse(list);
         Map<String, URL> alts = new HashMap();

         for(String prefix : list) {
            prefix = prefix.trim();
            if (!prefix.matches(".*[.-]$")) {
               prefix = prefix + ".";
            }

            for(Map.Entry<String, URL> entry : (new HashMap(map)).entrySet()) {
               String key = (String)entry.getKey();
               URL value = (URL)entry.getValue();
               if (key.startsWith(prefix)) {
                  key = key.substring(prefix.length());
                  alts.put(key, value);
               }
            }
         }

         for(Map.Entry<String, URL> alt : alts.entrySet()) {
            String key = (String)alt.getKey();
            URL value = (URL)alt.getValue();
            if (!value.equals(map.get(key))) {
               if (log) {
                  logger.info("AltDD " + key + " -> " + value.toExternalForm());
               }

               map.put(key, value);
            }
         }

         return map;
      } else {
         return map;
      }
   }

   public static Map<String, URL> getWebDescriptors(File warFile) throws IOException {
      Map<String, URL> descriptors = new TreeMap();
      if (warFile.isFile()) {
         URL jarURL = new URL("jar", "", -1, warFile.toURI().toURL() + "!/");

         try {
            JarFile jarFile = new JarFile(warFile);
            Throwable var4 = null;

            try {
               for(JarEntry entry : Collections.list(jarFile.entries())) {
                  String entryName = entry.getName();
                  if (!entry.isDirectory() && entryName.startsWith("WEB-INF/") && (KNOWN_DESCRIPTORS.contains(entryName.substring("WEB-INF/".length())) || entryName.endsWith(".xml"))) {
                     descriptors.put(entryName, new URL(jarURL, entry.getName()));
                  }
               }
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (jarFile != null) {
                  if (var4 != null) {
                     try {
                        jarFile.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     jarFile.close();
                  }
               }

            }
         } catch (IOException var18) {
         }
      } else if (warFile.isDirectory()) {
         File webInfDir = new File(warFile, "WEB-INF");
         if (webInfDir.isDirectory()) {
            File[] files = webInfDir.listFiles();
            if (files != null) {
               for(File file : files) {
                  if (!file.isDirectory()) {
                     descriptors.put(file.getName(), file.toURI().toURL());
                  }
               }
            }
         }

         File webAppDdDir = new File(webInfDir, "classes/META-INF/");
         if (webAppDdDir.isDirectory()) {
            File[] files = webAppDdDir.listFiles();
            if (files != null) {
               for(File file : files) {
                  String name = file.getName();
                  if (!descriptors.containsKey(name)) {
                     descriptors.put(name, file.toURI().toURL());
                  } else {
                     logger.warning("Can't have a " + name + " in WEB-INF and WEB-INF/classes/META-INF, second will be ignored");
                  }
               }
            }
         }
      }

      return descriptors;
   }

   protected File getFile(URL warUrl) {
      if ("jar".equals(warUrl.getProtocol())) {
         String pathname = warUrl.getPath();
         if (!pathname.startsWith("file:")) {
            return null;
         } else {
            pathname = pathname.substring("file:".length());
            pathname = pathname.substring(0, pathname.lastIndexOf(33));

            try {
               pathname = URLDecoder.decode(pathname, "UTF-8");
            } catch (Exception var4) {
               pathname = URLDecoder.decode(pathname);
            }

            return new File(pathname);
         }
      } else if ("file".equals(warUrl.getProtocol())) {
         String pathname = warUrl.getPath();

         try {
            return new File(URLDecoder.decode(pathname, "UTF-8"));
         } catch (UnsupportedEncodingException var5) {
            return new File(URLDecoder.decode(pathname));
         }
      } else {
         return null;
      }
   }

   public static Application unmarshal(URL url) throws OpenEJBException {
      try {
         return ApplicationXml.unmarshal(url);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered error parsing the application.xml file: " + url.toExternalForm(), e);
      }
   }

   public static void scanDir(File dir, Map<String, URL> files, String path) {
      scanDir(dir, files, path, true);
   }

   public static void scanDir(File dir, Map<String, URL> files, String path, boolean recursive) {
      File[] dirFiles = dir.listFiles();
      if (dirFiles != null) {
         for(File file : dirFiles) {
            if (file.isDirectory()) {
               if (!DeploymentsResolver.isExtractedDir(file) && recursive) {
                  scanDir(file, files, path + file.getName() + "/");
               }
            } else {
               String name = file.getName();

               try {
                  files.put(path + name, file.toURI().toURL());
               } catch (MalformedURLException e) {
                  logger.warning("EAR path bad: " + path + name, (Throwable)e);
               }
            }
         }
      }

   }

   public Class<? extends DeploymentModule> discoverModuleType(URL baseUrl, ClassLoader classLoader, boolean searchForDescriptorlessApplications) throws IOException, UnknownModuleTypeException {
      Set<RequireDescriptors> search = new HashSet();
      if (!searchForDescriptorlessApplications) {
         search.addAll(Arrays.asList(RequireDescriptors.values()));
      }

      return this.discoverModuleType(baseUrl, classLoader, search);
   }

   public Class<? extends DeploymentModule> discoverModuleType(URL baseUrl, ClassLoader classLoader, Set<RequireDescriptors> requireDescriptor) throws IOException, UnknownModuleTypeException {
      boolean scanPotentialEjbModules = !requireDescriptor.contains(RequireDescriptors.EJB);
      boolean scanPotentialClientModules = !requireDescriptor.contains(RequireDescriptors.CLIENT);
      URL pathToScanDescriptors = baseUrl;
      String path;
      if (baseUrl != null) {
         path = URLs.toFile(baseUrl).getAbsolutePath();
         if (baseUrl.getProtocol().equals("file") && path.endsWith("WEB-INF/classes/")) {
            pathToScanDescriptors = new URL(path.substring(0, path.lastIndexOf("WEB-INF/classes/")));
         }
      } else {
         path = "";
      }

      Map<String, URL> descriptors = this.getDescriptors(classLoader, pathToScanDescriptors);
      if (path.endsWith("/")) {
         path = path.substring(0, path.length() - 1);
      }

      if (!path.endsWith(".xml") && !path.endsWith(".json")) {
         if (!descriptors.containsKey("application.xml") && !path.endsWith(".ear")) {
            if (!descriptors.containsKey("ra.xml") && !path.endsWith(".rar")) {
               if (baseUrl != null) {
                  Map<String, URL> webDescriptors = getWebDescriptors(this.getFile(baseUrl));
                  if (webDescriptors.containsKey("web.xml") || webDescriptors.containsKey("web-fragment.xml") || path.endsWith(".war") || (new File(path, "WEB-INF")).exists()) {
                     return WebModule.class;
                  }
               }

               if (!descriptors.containsKey("ejb-jar.xml") && !descriptors.containsKey("beans.xml")) {
                  if (descriptors.containsKey("application-client.xml")) {
                     return ClientModule.class;
                  } else {
                     URL manifestUrl = (URL)descriptors.get("MANIFEST.MF");
                     if (scanPotentialClientModules && manifestUrl != null) {
                        InputStream is = new BufferedInputStream(manifestUrl.openStream());
                        Manifest manifest = new Manifest(is);
                        String mainClass = manifest.getMainAttributes().getValue(Name.MAIN_CLASS);
                        if (mainClass != null) {
                           return ClientModule.class;
                        }
                     }

                     Class<? extends DeploymentModule> cls = this.checkAnnotations(baseUrl, classLoader, scanPotentialEjbModules, scanPotentialClientModules);
                     if (cls != null) {
                        return cls;
                     } else if (!descriptors.containsKey("persistence.xml") && !descriptors.containsKey("persistence-fragment.xml")) {
                        File file = URLs.toFile(baseUrl);
                        if (DeploymentsResolver.isValidDirectory(file)) {
                           File[] files = file.listFiles();
                           if (containsEarAssets(files)) {
                              return AppModule.class;
                           }

                           if (containsWebAssets(files)) {
                              return WebModule.class;
                           }
                        }

                        Class<? extends DeploymentModule> defaultType = SystemInstance.get().getOptions().get("openejb.default.deployment-module", (Class)null);
                        if (defaultType == null) {
                           throw new UnknownModuleTypeException("Unknown module type: url=" + path);
                        } else if (!WebModule.class.equals(defaultType) || !path.endsWith(".jar!") && !path.endsWith(".jar")) {
                           logger.debug("type for '" + path + "' was not found, defaulting to " + defaultType.getSimpleName());
                           return defaultType;
                        } else {
                           throw new UnknownModuleTypeException("Unknown module type: url=" + path + " which can't be a war.");
                        }
                     } else {
                        return PersistenceModule.class;
                     }
                  }
               } else {
                  return EjbModule.class;
               }
            } else {
               return ConnectorModule.class;
            }
         } else {
            return AppModule.class;
         }
      } else {
         return ResourcesModule.class;
      }
   }

   private static boolean containsWebAssets(File[] files) {
      if (files != null) {
         for(File file : files) {
            String fn = file.getName().toLowerCase(Locale.ENGLISH);
            if (fn.endsWith(".jsp")) {
               return true;
            }

            if (fn.endsWith(".html")) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean containsEarAssets(File[] files) {
      if (files != null) {
         for(File file : files) {
            String fn = file.getName().toLowerCase(Locale.ENGLISH);
            if (fn.endsWith(".jar")) {
               return true;
            }

            if (fn.endsWith(".war")) {
               return true;
            }

            if (fn.endsWith(".rar")) {
               return true;
            }
         }
      }

      return false;
   }

   private Map<String, URL> getDescriptors(ClassLoader classLoader, URL pathToScanDescriptors) throws IOException {
      ResourceFinder finder = new ResourceFinder("", classLoader, new URL[]{pathToScanDescriptors});
      return altDDSources(mapDescriptors(finder), false);
   }

   private Class<? extends DeploymentModule> checkAnnotations(URL urls, ClassLoader classLoader, final boolean scanPotentialEjbModules, final boolean scanPotentialClientModules) {
      Class<? extends DeploymentModule> cls = null;
      if (scanPotentialEjbModules || scanPotentialClientModules) {
         org.apache.openejb.util.AnnotationFinder classFinder = new org.apache.openejb.util.AnnotationFinder(classLoader, urls);
         final Set<Class<? extends DeploymentModule>> otherTypes = new LinkedHashSet();
         org.apache.openejb.util.AnnotationFinder.Filter filter = new org.apache.openejb.util.AnnotationFinder.Filter() {
            final String packageName = LocalClient.class.getName().replace("LocalClient", "");

            public boolean accept(String annotationName) {
               if (scanPotentialClientModules && annotationName.startsWith(this.packageName)) {
                  if (LocalClient.class.getName().equals(annotationName)) {
                     otherTypes.add(ClientModule.class);
                  }

                  if (RemoteClient.class.getName().equals(annotationName)) {
                     otherTypes.add(ClientModule.class);
                  }
               } else if (scanPotentialEjbModules) {
                  if (annotationName.startsWith("javax.ejb.")) {
                     if ("javax.ejb.Stateful".equals(annotationName)) {
                        return true;
                     }

                     if ("javax.ejb.Stateless".equals(annotationName)) {
                        return true;
                     }

                     if ("javax.ejb.Singleton".equals(annotationName)) {
                        return true;
                     }

                     if ("javax.ejb.MessageDriven".equals(annotationName)) {
                        return true;
                     }
                  } else if ("javax.annotation.ManagedBean".equals(annotationName)) {
                     return true;
                  }
               }

               return false;
            }
         };
         if (classFinder.find(filter)) {
            cls = EjbModule.class;

            try {
               if (logger.isWarningEnabled()) {
                  File ar = URLs.toFile(urls);
                  if (!ar.isDirectory() && !ar.getName().endsWith("ar")) {
                     JarFile war = new JarFile(ar);
                     Throwable var11 = null;

                     try {
                        ZipEntry entry = war.getEntry("WEB-INF/");
                        if (entry != null) {
                           logger.warning("you deployed " + urls.toExternalForm() + ", it seems it is a war with no extension, please rename it");
                        }
                     } catch (Throwable var21) {
                        var11 = var21;
                        throw var21;
                     } finally {
                        if (war != null) {
                           if (var11 != null) {
                              try {
                                 war.close();
                              } catch (Throwable var20) {
                                 var11.addSuppressed(var20);
                              }
                           } else {
                              war.close();
                           }
                        }

                     }
                  }
               }
            } catch (Exception var23) {
            }
         }

         if (otherTypes.size() > 0) {
            cls = (Class)otherTypes.iterator().next();
         }
      }

      return cls;
   }

   public static File unpack(File jarFile) throws OpenEJBException {
      if (!jarFile.isDirectory() && !jarFile.getName().endsWith(".jar")) {
         String name = jarFile.getName();
         if (!name.endsWith(".ear") && !name.endsWith(".zip") && !name.endsWith(".war") && !name.endsWith(".rar")) {
            name = name + ".unpacked";
         } else {
            name = name.replaceFirst("....$", "");
         }

         try {
            return JarExtractor.extract(jarFile, name);
         } catch (Throwable e) {
            throw new OpenEJBException("Unable to extract jar. " + e.getMessage(), e);
         }
      } else {
         return jarFile;
      }
   }

   protected static URL getFileUrl(File jarFile) throws OpenEJBException {
      try {
         URL baseUrl = jarFile.toURI().toURL();
         return baseUrl;
      } catch (MalformedURLException e) {
         throw new OpenEJBException("Malformed URL to app. " + e.getMessage(), e);
      }
   }

   public static void reloadAltDD() {
      ALTDD = SystemInstance.get().getOptions().get("openejb.altdd.prefix", (String)null);
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, "org.apache.openejb.util.resources");
      KNOWN_DESCRIPTORS = Arrays.asList("app-ctx.xml", "module.properties", "application.properties", "web.xml", "ejb-jar.xml", "openejb-jar.xml", "env-entries.properties", "beans.xml", "ra.xml", "application.xml", "application-client.xml", "persistence-fragment.xml", "persistence.xml", "validation.xml", "exclusions.list");
      ALTDD = SystemInstance.get().getOptions().get("openejb.altdd.prefix", (String)null);
   }

   public static class ExternalConfiguration {
      private final String[] classpath;
      private final Filter customerFilter;

      public ExternalConfiguration(String[] classpath, Filter customerFilter) {
         this.classpath = classpath;
         this.customerFilter = customerFilter;
      }

      public Filter getCustomerFilter() {
         return this.customerFilter;
      }

      public String[] getClasspath() {
         return this.classpath;
      }
   }
}
