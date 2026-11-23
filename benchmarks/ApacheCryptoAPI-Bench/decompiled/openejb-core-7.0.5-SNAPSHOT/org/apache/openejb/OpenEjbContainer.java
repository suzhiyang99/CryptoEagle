package org.apache.openejb;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.LogManager;
import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.ejb.spi.EJBContainerProvider;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.cdi.ScopeHelper;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.ConnectorModule;
import org.apache.openejb.config.DeploymentLoader;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.NewLoaderLogic;
import org.apache.openejb.config.PersistenceModule;
import org.apache.openejb.config.ValidationFailedException;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.core.ProvidedClassLoaderFinder;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Exceptions;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.JuliLogStreamFactory;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.OptionsLog;
import org.apache.openejb.util.ServiceManagerProxy;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.web.lifecycle.test.MockHttpSession;
import org.apache.webbeans.web.lifecycle.test.MockServletContext;
import org.apache.xbean.naming.context.ContextFlyweight;

public final class OpenEjbContainer extends EJBContainer {
   public static final String OPENEJB_EMBEDDED_REMOTABLE = "openejb.embedded.remotable";
   public static final String OPENEJB_EJBCONTAINER_CLOSE = "openejb.ejbcontainer.close";
   public static final String OPENEJB_EJBCONTAINER_CLOSE_SINGLE = "single-jvm";
   private static OpenEjbContainer instance;
   private static Logger logger;
   private ServiceManagerProxy serviceManager;
   private final Options options;
   private final GlobalContext globalJndiContext;
   private final WebBeansContext webBeanContext;
   private volatile ServletContext servletContext;
   private volatile HttpSession session;

   private OpenEjbContainer(Map<?, ?> map, AppContext appContext) {
      this.webBeanContext = appContext.getWebBeansContext();
      this.globalJndiContext = new GlobalContext(appContext.getGlobalJndiContext());
      Properties properties = new Properties();
      properties.putAll(map);
      this.options = new Options(properties);
      this.startNetworkServices();
      if (this.webBeanContext != null) {
         this.servletContext = new MockServletContext();
         this.session = new MockHttpSession();

         try {
            ScopeHelper.startContexts(this.webBeanContext.getContextsService(), this.servletContext, this.session);
         } catch (Exception e) {
            logger().warning("can't start all CDI contexts", (Throwable)e);
         }
      }

   }

   public void close() {
      if (!isSingleClose()) {
         this.doClose();
      }
   }

   private static boolean isSingleClose() {
      return "single-jvm".equals(SystemInstance.get().getProperty("openejb.ejbcontainer.close", "by-invocation"));
   }

   private synchronized void doClose() {
      if (instance != null) {
         if (this.serviceManager != null) {
            this.serviceManager.stop();
         }

         try {
            this.globalJndiContext.close();
         } catch (NamingException e) {
            throw new IllegalStateException(e);
         }

         Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
         if (assembler != null) {
            for(AppInfo info : assembler.getDeployedApplications()) {
               try {
                  assembler.destroyApplication(info);
               } catch (UndeployException e) {
                  logger().error(e.getMessage(), (Throwable)e);
               }
            }
         }

         if (this.webBeanContext != null) {
            try {
               ScopeHelper.stopContexts(this.webBeanContext.getContextsService(), this.servletContext, this.session);
            } catch (Exception e) {
               logger().warning("can't stop all CDI contexts", (Throwable)e);
            }
         }

         logger().info("Destroying OpenEJB container");
         OpenEJB.destroy();
         instance = null;
      }
   }

   public Context getContext() {
      return this.globalJndiContext;
   }

   private void startNetworkServices() {
      if (this.options.get("openejb.embedded.remotable", false)) {
         try {
            this.serviceManager = new ServiceManagerProxy();
            this.serviceManager.start();
         } catch (ServiceManagerProxy.AlreadyStartedException var2) {
            logger().debug("Network services already started.  Ignoring option openejb.embedded.remotable");
         }

      }
   }

   private static Logger logger() {
      if (logger == null) {
         logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, OpenEjbContainer.class);
      }

      return logger;
   }

   static {
      String logManger = JavaSecurityManagers.getSystemProperty("java.util.logging.manager");
      if (logManger != null) {
         try {
            Thread.currentThread().getContextClassLoader().loadClass(logManger);
         } catch (Exception var5) {
            try {
               Field field = LogManager.class.getDeclaredField("manager");
               field.setAccessible(true);
               field.set((Object)null, new JuliLogStreamFactory.OpenEJBLogManager());
            } catch (Exception var4) {
            }
         }
      }

   }

   public static class Provider implements EJBContainerProvider {
      public static final String OPENEJB_ADDITIONNAL_CALLERS_KEY = "openejb.additionnal.callers";

      public EJBContainer createEJBContainer(Map<?, ?> map) {
         if (map == null) {
            map = new HashMap();
         }

         if (isOtherProvider(map)) {
            return null;
         } else if (OpenEjbContainer.instance == null && !OpenEJB.isInitialized()) {
            OpenEjbContainer var13;
            try {
               if (JavaSecurityManagers.getSystemProperty("java.naming.factory.url.pkgs") != null) {
                  JavaSecurityManagers.removeSystemProperty("java.naming.factory.url.pkgs");
               }

               Properties properties = new Properties();
               properties.putAll(map);
               SystemInstance.reset();
               SystemInstance.init(properties);
               SystemInstance.get().setProperty("openejb.embedded", "true");
               SystemInstance.get().setProperty(EJBContainer.class.getName(), "true");
               if (SystemInstance.get().getComponent(ParentClassLoaderFinder.class) == null) {
                  ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                  if (tccl == null) {
                     tccl = OpenEjbContainer.class.getClassLoader();
                  }

                  SystemInstance.get().setComponent(ParentClassLoaderFinder.class, new ProvidedClassLoaderFinder(tccl));
               }

               OptionsLog.install();
               OpenEJB.init(properties);
               Core.warmup();
               DeploymentLoader.reloadAltDD();
               ConfigurationFactory configurationFactory = new ConfigurationFactory();
               AppModule appModule = this.load(map, configurationFactory);
               Set<String> callers;
               if (map.containsKey("openejb.additionnal.callers")) {
                  callers = new LinkedHashSet();
                  callers.addAll(Arrays.asList(((String)map.get("openejb.additionnal.callers")).split(",")));
               } else {
                  callers = NewLoaderLogic.callers();
               }

               EjbJar ejbJar = new EjbJar();
               OpenejbJar openejbJar = new OpenejbJar();

               for(String caller : callers) {
                  if (this.isValid(caller)) {
                     String name = caller;
                     if (caller.contains("$")) {
                        name = caller.replace("$", "_");
                     }

                     ManagedBean bean = (ManagedBean)ejbJar.addEnterpriseBean(new ManagedBean(name, caller, true));
                     bean.localBean();
                     bean.setTransactionType(TransactionType.BEAN);
                     EjbDeployment ejbDeployment = openejbJar.addEjbDeployment(bean);
                     ejbDeployment.setDeploymentId(name);
                  }
               }

               EjbModule ejbModule = new EjbModule(ejbJar, openejbJar);
               ejbModule.getProperties().setProperty("openejb.cdi.activated", "false");
               ejbModule.setBeans(new Beans());
               appModule.getEjbModules().add(ejbModule);

               AppInfo appInfo;
               try {
                  appInfo = configurationFactory.configureApplication(appModule);
               } catch (ValidationFailedException e) {
                  OpenEjbContainer.logger().warning("configureApplication.loadFailed", appModule.getModuleId(), e.getMessage());
                  throw new InvalidApplicationException(e);
               } catch (OpenEJBException e) {
                  OpenEjbContainer.logger().warning("configureApplication.loadFailed", e, appModule.getModuleId(), e.getMessage());
                  throw new ConfigureApplicationException(e);
               }

               Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);

               AppContext appContext;
               try {
                  appContext = assembler.createApplication(appInfo, appModule.getClassLoader());
               } catch (ValidationException ve) {
                  throw ve;
               } catch (Exception e) {
                  throw new AssembleApplicationException(e);
               }

               OpenEjbContainer openEjbContainer = OpenEjbContainer.instance = new OpenEjbContainer(map, appContext);
               if (OpenEjbContainer.isSingleClose()) {
                  Runtime.getRuntime().addShutdownHook(new Thread() {
                     public void run() {
                        if (OpenEjbContainer.instance != null) {
                           OpenEjbContainer.instance.doClose();
                        }

                     }
                  });
               }

               var13 = openEjbContainer;
            } catch (MalformedURLException | OpenEJBException e) {
               throw new EJBException(e);
            } catch (ValidationException ve) {
               throw ve;
            } catch (Exception e) {
               if (e instanceof EJBException) {
                  throw (EJBException)e;
               }

               throw new InitializationException(e);
            } finally {
               if (OpenEjbContainer.instance == null && OpenEJB.isInitialized()) {
                  try {
                     OpenEJB.destroy();
                  } catch (Exception var27) {
                  }
               }

            }

            return var13;
         } else {
            if (!OpenEjbContainer.isSingleClose()) {
               OpenEjbContainer.logger().info("EJBContainer already initialized.  Call ejbContainer.close() to allow reinitialization");
            }

            return OpenEjbContainer.instance;
         }
      }

      private boolean isValid(String caller) {
         try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = loader.loadClass(caller);
            int modifiers = clazz.getModifiers();
            return !clazz.isEnum() && !clazz.isInterface() && !Modifier.isAbstract(modifiers);
         } catch (ClassNotFoundException var5) {
            return false;
         }
      }

      private AppModule load(Map<?, ?> map, ConfigurationFactory configurationFactory) throws MalformedURLException, OpenEJBException {
         String appId = (String)map.get("javax.ejb.embeddable.appName");
         Object modules = map.get("javax.ejb.embeddable.modules");
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         List<File> moduleLocations;
         if (modules instanceof String) {
            moduleLocations = configurationFactory.getModulesFromClassPath((List)null, classLoader);
            Iterator<File> i = moduleLocations.iterator();

            while(i.hasNext()) {
               File file = (File)i.next();
               if (!this.match((String)modules, file)) {
                  i.remove();
               }
            }
         } else if (modules instanceof String[]) {
            moduleLocations = configurationFactory.getModulesFromClassPath((List)null, classLoader);
            int matched = 0;
            Iterator<File> i = moduleLocations.iterator();

            while(i.hasNext()) {
               File file = (File)i.next();
               boolean remove = true;

               for(String s : (String[])modules) {
                  if (this.match(s, file)) {
                     remove = false;
                     ++matched;
                     break;
                  }
               }

               if (remove) {
                  i.remove();
               }
            }

            if (matched != ((String[])((String[])modules)).length) {
               throw this.specifiedModulesNotFound();
            }
         } else if (modules instanceof File) {
            URL url = ((File)modules).toURI().toURL();
            classLoader = new URLClassLoader(new URL[]{url}, classLoader);
            moduleLocations = Collections.singletonList((File)modules);
         } else if (modules instanceof File[]) {
            File[] files = (File[])modules;
            URL[] urls = new URL[files.length];

            for(int i = 0; i < urls.length; ++i) {
               urls[i] = files[i].toURI().toURL();
            }

            classLoader = new URLClassLoader(urls, classLoader);
            moduleLocations = Arrays.asList((File[])modules);
         } else {
            if (modules != null) {
               AppModule appModule = this.load(map);
               if (appModule != null) {
                  return appModule;
               }

               throw this.invalidModulesValue(modules);
            }

            moduleLocations = configurationFactory.getModulesFromClassPath((List)null, classLoader);
         }

         if (moduleLocations.isEmpty()) {
            throw Exceptions.newNoModulesFoundException();
         } else {
            return configurationFactory.loadApplication(classLoader, appId, moduleLocations);
         }
      }

      private AppModule load(Map<?, ?> map) {
         String appId = (String)map.get("javax.ejb.embeddable.appName");
         Object modules = map.get("javax.ejb.embeddable.modules");
         map.size();
         Application application = null;
         AppModule appModule = new AppModule(this.getClass().getClassLoader(), appId);
         if (modules instanceof EjbJar) {
            EjbJar ejbJar = (EjbJar)modules;
            appModule.getEjbModules().add(new EjbModule(ejbJar));
         } else if (modules instanceof EnterpriseBean) {
            EnterpriseBean bean = (EnterpriseBean)modules;
            EjbJar ejbJar = new EjbJar();
            ejbJar.addEnterpriseBean(bean);
            appModule.getEjbModules().add(new EjbModule(ejbJar));
         } else if (modules instanceof Application) {
            application = (Application)modules;
         } else if (modules instanceof Connector) {
            Connector connector = (Connector)modules;
            appModule.getConnectorModules().add(new ConnectorModule(connector));
         } else if (modules instanceof Persistence) {
            Persistence persistence = (Persistence)modules;
            appModule.addPersistenceModule(new PersistenceModule(appModule, "", persistence));
         } else if (modules instanceof PersistenceUnit) {
            PersistenceUnit unit = (PersistenceUnit)modules;
            appModule.addPersistenceModule(new PersistenceModule(appModule, "", new Persistence(new PersistenceUnit[]{unit})));
         } else if (modules instanceof Beans) {
            Beans beans = (Beans)modules;
            EjbModule ejbModule = new EjbModule(new EjbJar());
            ejbModule.setBeans(beans);
            appModule.getEjbModules().add(ejbModule);
         }

         if (application != null) {
            AppModule newModule = new AppModule(appModule.getClassLoader(), appModule.getModuleId(), application, false);
            newModule.getClientModules().addAll(appModule.getClientModules());
            newModule.addPersistenceModules(appModule.getPersistenceModules());
            newModule.getEjbModules().addAll(appModule.getEjbModules());
            newModule.getConnectorModules().addAll(appModule.getConnectorModules());
            appModule = newModule;
         }

         return appModule;
      }

      private EJBException specifiedModulesNotFound() {
         return new NoSuchModuleException("some modules not matched on classpath");
      }

      private InvalidModulesPropertyException invalidModulesValue(Object value) {
         String[] spec = new String[]{"java.lang.String", "java.lang.String[]", "java.io.File", "java.io.File[]"};
         String type = value == null ? null : value.getClass().getName();
         return new InvalidModulesPropertyException(String.format("Invalid '%s' value '%s'. Valid values are: %s", "javax.ejb.embeddable.modules", type, Join.join(", ", (Object[])spec)));
      }

      private static boolean isOtherProvider(Map<?, ?> properties) {
         Object provider = properties.get("javax.ejb.embeddable.provider");
         return provider != null && !provider.equals(OpenEjbContainer.class) && !provider.equals(OpenEjbContainer.class.getName()) && !"openejb".equals(provider);
      }

      private boolean match(String s, File file) {
         String s2 = file.getName();
         String s3 = file.getAbsolutePath();
         boolean matches;
         if (file.isDirectory()) {
            matches = s2.equals(s) || s2.equals(s + ".jar") || s3.equals(s);
         } else {
            matches = s2.equals(s + ".jar");
         }

         return matches;
      }
   }

   private class GlobalContext extends ContextFlyweight {
      private final Context globalJndiContext;

      public GlobalContext(Context globalJndiContext) {
         this.globalJndiContext = globalJndiContext;
      }

      protected Context getContext() throws NamingException {
         return this.globalJndiContext;
      }

      protected Name getName(Name name) throws NamingException {
         String first = name.get(0);
         if (!first.startsWith("java:")) {
            throw new NameNotFoundException("Name must be in java: namespace");
         } else {
            first = first.substring("java:".length());
            name = name.getSuffix(1);
            return name.add(0, first);
         }
      }

      protected String getName(String name) throws NamingException {
         if ("inject".equals(name)) {
            return name;
         } else if (!name.startsWith("java:")) {
            throw new NameNotFoundException("Name must be in java: namespace");
         } else {
            return name.substring("java:".length());
         }
      }

      public void bind(Name name, Object obj) throws NamingException {
         if (name.size() == 1 && "inject".equals(name.get(0))) {
            Injector.inject(obj);
         } else {
            super.bind(name, obj);
         }

      }

      public void bind(String name, Object obj) throws NamingException {
         if (name != null && "inject".equals(name)) {
            Injector.inject(obj);
         } else {
            super.bind(name, obj);
         }

      }

      public void unbind(Name name) throws NamingException {
         if (name.size() != 1 || !"inject".equals(name.get(0))) {
            super.unbind(name);
         }

      }

      public void unbind(String name) throws NamingException {
         if (name == null || !"inject".equals(name)) {
            super.unbind(name);
         }

      }
   }

   public static class InitializationException extends EJBException {
      public InitializationException(String s) {
         super(s);
      }

      public InitializationException(Exception cause) {
         super(cause);
      }
   }

   public static class InvalidModulesPropertyException extends InitializationException {
      public InvalidModulesPropertyException(String s) {
         super(s);
      }
   }

   public static class NoSuchModuleException extends InitializationException {
      public NoSuchModuleException(String s) {
         super(s);
      }
   }

   public static class NoModulesFoundException extends InitializationException {
      public NoModulesFoundException(String s) {
         super(s);
      }
   }

   public static class ConfigureApplicationException extends InitializationException {
      public ConfigureApplicationException(Exception cause) {
         super(cause);
      }
   }

   public static class AssembleApplicationException extends InitializationException {
      public AssembleApplicationException(Exception cause) {
         super(cause);
      }
   }

   public static class InvalidApplicationException extends InitializationException {
      public InvalidApplicationException(Exception cause) {
         super(cause);
      }
   }
}
