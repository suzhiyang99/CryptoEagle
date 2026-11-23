package org.apache.openejb.assembler.classic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.DeploymentException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.XATerminator;
import javax.resource.spi.work.WorkManager;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.geronimo.connector.GeronimoBootstrapContext;
import org.apache.geronimo.connector.outbound.AbstractConnectionManager;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.connector.work.HintsContextHandler;
import org.apache.geronimo.connector.work.TransactionContextHandler;
import org.apache.geronimo.connector.work.WorkContextHandler;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.Container;
import org.apache.openejb.DeploymentContext;
import org.apache.openejb.DuplicateDeploymentIdException;
import org.apache.openejb.Extensions;
import org.apache.openejb.Injection;
import org.apache.openejb.JndiConstants;
import org.apache.openejb.MethodContext;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.api.jmx.MBean;
import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.assembler.classic.event.AssemblerBeforeApplicationDestroyed;
import org.apache.openejb.assembler.classic.event.AssemblerCreated;
import org.apache.openejb.assembler.classic.event.AssemblerDestroyed;
import org.apache.openejb.assembler.classic.event.BeforeStartEjbs;
import org.apache.openejb.assembler.classic.event.ContainerSystemPostCreate;
import org.apache.openejb.assembler.classic.event.ContainerSystemPreDestroy;
import org.apache.openejb.assembler.classic.event.ResourceBeforeDestroyed;
import org.apache.openejb.assembler.classic.event.ResourceCreated;
import org.apache.openejb.assembler.classic.util.ServiceInfos;
import org.apache.openejb.assembler.monitoring.JMXContainer;
import org.apache.openejb.async.AsynchronousPool;
import org.apache.openejb.batchee.BatchEEServiceManager;
import org.apache.openejb.cdi.CdiAppContextsService;
import org.apache.openejb.cdi.CdiBuilder;
import org.apache.openejb.cdi.CdiPlugin;
import org.apache.openejb.cdi.CdiResourceInjectionService;
import org.apache.openejb.cdi.CdiScanner;
import org.apache.openejb.cdi.CustomELAdapter;
import org.apache.openejb.cdi.ManagedSecurityService;
import org.apache.openejb.cdi.OpenEJBBeanInfoService;
import org.apache.openejb.cdi.OpenEJBJndiService;
import org.apache.openejb.cdi.OpenEJBTransactionService;
import org.apache.openejb.cdi.OptimizedLoaderService;
import org.apache.openejb.classloader.ClassLoaderConfigurer;
import org.apache.openejb.classloader.CompositeClassLoaderConfigurer;
import org.apache.openejb.component.ClassLoaderEnricher;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.NewLoaderLogic;
import org.apache.openejb.config.QuickJarsTxtParser;
import org.apache.openejb.config.TldScanner;
import org.apache.openejb.core.ConnectorReference;
import org.apache.openejb.core.CoreContainerSystem;
import org.apache.openejb.core.CoreUserTransaction;
import org.apache.openejb.core.JndiFactory;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.core.SimpleTransactionSynchronizationRegistry;
import org.apache.openejb.core.TransactionSynchronizationRegistryWrapper;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.core.ivm.ContextHandler;
import org.apache.openejb.core.ivm.IntraVmProxy;
import org.apache.openejb.core.ivm.naming.ContextualJndiReference;
import org.apache.openejb.core.ivm.naming.IvmContext;
import org.apache.openejb.core.ivm.naming.IvmJndiFactory;
import org.apache.openejb.core.ivm.naming.JndiUrlReference;
import org.apache.openejb.core.ivm.naming.LazyObjectReference;
import org.apache.openejb.core.ivm.naming.Reference;
import org.apache.openejb.core.security.SecurityContextHandler;
import org.apache.openejb.core.timer.EjbTimerServiceImpl;
import org.apache.openejb.core.timer.MemoryTimerStore;
import org.apache.openejb.core.timer.NullEjbTimerServiceImpl;
import org.apache.openejb.core.timer.ScheduleData;
import org.apache.openejb.core.timer.TimerStore;
import org.apache.openejb.core.transaction.JtaTransactionPolicyFactory;
import org.apache.openejb.core.transaction.SimpleBootstrapContext;
import org.apache.openejb.core.transaction.SimpleWorkManager;
import org.apache.openejb.core.transaction.TransactionPolicyFactory;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.javaagent.Agent;
import org.apache.openejb.jpa.integration.MakeTxLookup;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.JarLocation;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.ProvisioningUtil;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.DynamicMBeanWrapper;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ObjectNameBuilder;
import org.apache.openejb.monitoring.remote.RemoteResourceMonitor;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.persistence.JtaEntityManagerRegistry;
import org.apache.openejb.persistence.PersistenceClassLoaderHandler;
import org.apache.openejb.quartz.Scheduler;
import org.apache.openejb.resource.GeronimoConnectionManagerFactory;
import org.apache.openejb.resource.PropertiesFactory;
import org.apache.openejb.resource.jdbc.DataSourceFactory;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.Contexts;
import org.apache.openejb.util.DaemonThreadFactory;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.ExecutorBuilder;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEJBErrorHandler;
import org.apache.openejb.util.PropertiesHelper;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.References;
import org.apache.openejb.util.SafeToolkit;
import org.apache.openejb.util.SetAccessible;
import org.apache.openejb.util.SuperProperties;
import org.apache.openejb.util.URISupport;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.classloader.ClassLoaderAwareHandler;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.apache.openejb.util.proxy.ProxyFactory;
import org.apache.openejb.util.proxy.ProxyManager;
import org.apache.webbeans.component.ResourceBean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.inject.OWBInjector;
import org.apache.webbeans.logger.JULLoggerFactory;
import org.apache.webbeans.spi.BeanArchiveService;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.spi.JNDIService;
import org.apache.webbeans.spi.LoaderService;
import org.apache.webbeans.spi.ResourceInjectionService;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.spi.TransactionService;
import org.apache.webbeans.spi.adaptor.ELAdaptor;
import org.apache.webbeans.spi.api.ResourceReference;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.ClassLoaders;
import org.apache.xbean.finder.ResourceFinder;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.UnsetPropertiesRecipe;

public class Assembler extends AssemblerTool implements org.apache.openejb.spi.Assembler, JndiConstants {
   public static final String OPENEJB_URL_PKG_PREFIX;
   public static final String OPENEJB_JPA_DEPLOY_TIME_ENHANCEMENT_PROP = "openejb.jpa.deploy-time-enhancement";
   public static final String PROPAGATE_APPLICATION_EXCEPTIONS = "openejb.propagate.application-exceptions";
   private static final String GLOBAL_UNIQUE_ID = "global";
   public static final String TIMER_STORE_CLASS = "timerStore.class";
   private static final ReentrantLock lock;
   public static final String OPENEJB_TIMERS_ON = "openejb.timers.on";
   static final String FORCE_READ_ONLY_APP_NAMING = "openejb.forceReadOnlyAppNamingContext";
   public static final Class<?>[] VALIDATOR_FACTORY_INTERFACES;
   public static final Class<?>[] VALIDATOR_INTERFACES;
   private final boolean skipLoaderIfPossible;
   Messages messages;
   public final Logger logger;
   public final String resourceDestroyTimeout;
   public final boolean threadStackOnTimeout;
   private final CoreContainerSystem containerSystem;
   private final PersistenceClassLoaderHandler persistenceClassLoaderHandler;
   private final JndiBuilder jndiBuilder;
   private TransactionManager transactionManager;
   private SecurityService securityService;
   protected OpenEjbConfigurationFactory configFactory;
   private final Map<String, AppInfo> deployedApplications;
   private final Map<ObjectName, CreationalContext> creationalContextForAppMbeans;
   private final Set<ObjectName> containerObjectNames;
   private final RemoteResourceMonitor remoteResourceMonitor;
   protected SafeToolkit toolkit;
   protected OpenEjbConfiguration config;
   private static final ThreadLocal<Map<String, Object>> context;

   public ContainerSystem getContainerSystem() {
      return this.containerSystem;
   }

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public SecurityService getSecurityService() {
      return this.securityService;
   }

   public void addDeploymentListener(DeploymentListener deploymentListener) {
      ReentrantLock l = lock;
      l.lock();

      try {
         this.logger.warning("DeploymentListener API is replaced by @Observes event");
         SystemInstance.get().addObserver(new DeploymentListenerObserver(deploymentListener));
      } finally {
         l.unlock();
      }

   }

   public void removeDeploymentListener(DeploymentListener deploymentListener) {
      ReentrantLock l = lock;
      l.lock();

      try {
         SystemInstance.get().removeObserver(new DeploymentListenerObserver(deploymentListener));
      } finally {
         l.unlock();
      }

   }

   public Assembler() {
      this(new IvmJndiFactory());
   }

   public Assembler(JndiFactory jndiFactory) {
      this.messages = new Messages(Assembler.class.getPackage().getName());
      this.deployedApplications = new HashMap();
      this.creationalContextForAppMbeans = new HashMap();
      this.containerObjectNames = new HashSet();
      this.remoteResourceMonitor = new RemoteResourceMonitor();
      this.toolkit = SafeToolkit.getToolkit("Assembler");
      this.logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, Assembler.class);
      this.skipLoaderIfPossible = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.classloader.skip-app-loader-if-possible", "true"));
      this.resourceDestroyTimeout = SystemInstance.get().getProperty("openejb.resources.destroy.timeout");
      this.threadStackOnTimeout = "true".equals(SystemInstance.get().getProperty("openejb.resources.destroy.stack-on-timeout", "false"));
      this.persistenceClassLoaderHandler = new PersistenceClassLoaderHandlerImpl();
      installNaming();
      SystemInstance system = SystemInstance.get();
      system.setComponent(org.apache.openejb.spi.Assembler.class, this);
      system.setComponent(Assembler.class, this);
      this.containerSystem = new CoreContainerSystem(jndiFactory);
      system.setComponent(ContainerSystem.class, this.containerSystem);
      this.jndiBuilder = new JndiBuilder(this.containerSystem.getJNDIContext());
      this.setConfiguration(new OpenEjbConfiguration());
      ApplicationServer appServer = (ApplicationServer)system.getComponent(ApplicationServer.class);
      if (appServer == null) {
         system.setComponent(ApplicationServer.class, new ServerFederation());
      }

      system.setComponent(EjbResolver.class, new EjbResolver((EjbResolver)null, EjbResolver.Scope.GLOBAL, new EjbJarInfo[0]));
      this.installExtensions();
      system.fireEvent(new AssemblerCreated());
      this.initBValFiltering();
   }

   private void initBValFiltering() {
      if ("true".equals(SystemInstance.get().getProperty("openejb.cdi.bval.filter", "true"))) {
         try {
            ClassLoader loader = ParentClassLoaderFinder.Helper.get();
            Object filter = loader.loadClass("org.apache.openejb.bval.BValCdiFilter").newInstance();
            loader.loadClass("org.apache.bval.cdi.BValExtension").getMethod("setAnnotatedTypeFilter", loader.loadClass("org.apache.bval.cdi.BValExtension$AnnotatedTypeFilter")).invoke((Object)null, filter);
         } catch (Throwable var3) {
         }
      }

   }

   private void installExtensions() {
      try {
         Collection<URL> urls = NewLoaderLogic.applyBuiltinExcludes((new UrlSet(Assembler.class.getClassLoader())).excludeJvm()).getUrls();
         Extensions.installExtensions(new Extensions.Finder("META-INF", false, (URL[])urls.toArray(new URL[urls.size()])));
      } catch (IOException var2) {
         Extensions.installExtensions(new Extensions.Finder("META-INF", true, new URL[0]));
      }
   }

   private void setConfiguration(OpenEjbConfiguration config) {
      this.config = config;
      if (config.containerSystem == null) {
         config.containerSystem = new ContainerSystemInfo();
      }

      if (config.facilities == null) {
         config.facilities = new FacilitiesInfo();
      }

      SystemInstance.get().setComponent(OpenEjbConfiguration.class, this.config);
   }

   public void init(Properties props) throws OpenEJBException {
      this.props = new Properties(props);
      Options options = new Options(props, SystemInstance.get().getOptions());
      String className = options.get("openejb.configurator", "org.apache.openejb.config.ConfigurationFactory");
      if ("org.apache.openejb.config.ConfigurationFactory".equals(className)) {
         this.configFactory = new ConfigurationFactory();
      } else {
         this.configFactory = (OpenEjbConfigurationFactory)this.toolkit.newInstance(className);
      }

      this.configFactory.init(props);
      SystemInstance.get().setComponent(OpenEjbConfigurationFactory.class, this.configFactory);
   }

   public static void installNaming() {
      if (!SystemInstance.get().hasProperty("openejb.geronimo")) {
         installNaming(OPENEJB_URL_PKG_PREFIX);
      }
   }

   public static void installNaming(String prefix) {
      installNaming(prefix, false);
   }

   public static void installNaming(String prefix, boolean clean) {
      ReentrantLock l = lock;
      l.lock();

      try {
         Properties systemProperties = JavaSecurityManagers.getSystemProperties();
         String str = systemProperties.getProperty("java.naming.factory.url.pkgs");
         if (str != null && !clean) {
            if (!str.contains(prefix)) {
               str = str + ":" + prefix;
            }
         } else {
            str = prefix;
         }

         systemProperties.setProperty("java.naming.factory.url.pkgs", str);
      } finally {
         l.unlock();
      }

   }

   public static void setContext(Map<String, Object> map) {
      context.set(map);
   }

   public static Map<String, Object> getContext() {
      Map<String, Object> map = (Map)context.get();
      if (map == null) {
         map = new HashMap();
         context.set(map);
      }

      return map;
   }

   public void build() throws OpenEJBException {
      setContext(new HashMap());

      try {
         OpenEjbConfiguration config = this.getOpenEjbConfiguration();
         this.buildContainerSystem(config);
      } catch (OpenEJBException ae) {
         throw ae;
      } catch (Exception e) {
         OpenEJBErrorHandler.handleUnknownError(e, "Assembler");
         throw new OpenEJBException(e);
      } finally {
         context.set((Object)null);
      }

   }

   protected OpenEjbConfiguration getOpenEjbConfiguration() throws OpenEJBException {
      return this.configFactory.getOpenEjbConfiguration();
   }

   public void buildContainerSystem(OpenEjbConfiguration configInfo) throws Exception {
      SystemInstance systemInstance = SystemInstance.get();
      if (systemInstance.getOptions().get("openejb.jpa.deploy-time-enhancement", false)) {
         systemInstance.addObserver(new DeployTimeEnhancer());
      }

      if (hasBatchEE()) {
         systemInstance.addObserver(new BatchEEServiceManager());
      }

      for(ServiceInfo serviceInfo : configInfo.facilities.services) {
         this.createService(serviceInfo);
      }

      ContainerSystemInfo containerSystemInfo = configInfo.containerSystem;
      if (configInfo.facilities.intraVmServer != null) {
         this.createProxyFactory(configInfo.facilities.intraVmServer);
      }

      for(JndiContextInfo contextInfo : configInfo.facilities.remoteJndiContexts) {
         this.createExternalContext(contextInfo);
      }

      this.createTransactionManager(configInfo.facilities.transactionService);
      this.createSecurityService(configInfo.facilities.securityService);
      Set<String> reservedResourceIds = new HashSet(configInfo.facilities.resources.size());

      for(AppInfo appInfo : containerSystemInfo.applications) {
         reservedResourceIds.addAll(appInfo.resourceIds);
      }

      Set<String> rIds = new HashSet(configInfo.facilities.resources.size());

      for(ResourceInfo resourceInfo : configInfo.facilities.resources) {
         this.createResource(configInfo.facilities.services, resourceInfo);
         rIds.add(resourceInfo.id);
      }

      rIds.removeAll(reservedResourceIds);
      ContainerSystem component = (ContainerSystem)systemInstance.getComponent(ContainerSystem.class);
      if (component == null) {
         throw new RuntimeException("ContainerSystem has not been initialzed");
      } else {
         this.postConstructResources(rIds, ParentClassLoaderFinder.Helper.get(), component.getJNDIContext(), (AppContext)null);

         for(ContainerInfo serviceInfo : containerSystemInfo.containers) {
            this.createContainer(serviceInfo);
         }

         this.createJavaGlobal();

         for(AppInfo appInfo : containerSystemInfo.applications) {
            try {
               this.createApplication(appInfo, this.createAppClassLoader(appInfo));
            } catch (DuplicateDeploymentIdException var11) {
            } catch (Throwable e) {
               this.logger.error("appNotDeployed", e, appInfo.path);
               DeploymentExceptionManager exceptionManager = (DeploymentExceptionManager)systemInstance.getComponent(DeploymentExceptionManager.class);
               if (exceptionManager != null && e instanceof Exception) {
                  exceptionManager.saveDeploymentException(appInfo, (Exception)e);
               }
            }
         }

         systemInstance.fireEvent(new ContainerSystemPostCreate());
      }
   }

   private static boolean hasBatchEE() {
      try {
         Class.forName("org.apache.batchee.container.services.ServicesManager", true, Assembler.class.getClassLoader());
         return true;
      } catch (Throwable var1) {
         return false;
      }
   }

   private void createJavaGlobal() {
      try {
         this.containerSystem.getJNDIContext().createSubcontext("global");
      } catch (NamingException var2) {
      }

   }

   public AppInfo getAppInfo(String path) {
      return (AppInfo)this.deployedApplications.get(ProvisioningUtil.realLocation(path).iterator().next());
   }

   public boolean isDeployed(String path) {
      return this.deployedApplications.containsKey(ProvisioningUtil.realLocation(path).iterator().next());
   }

   public Collection<AppInfo> getDeployedApplications() {
      return new ArrayList(this.deployedApplications.values());
   }

   public AppContext createApplication(EjbJarInfo ejbJar) throws NamingException, IOException, OpenEJBException {
      return this.createEjbJar(ejbJar);
   }

   public AppContext createEjbJar(EjbJarInfo ejbJar) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = ejbJar.path;
      appInfo.appId = ejbJar.moduleName;
      appInfo.ejbJars.add(ejbJar);
      return this.createApplication(appInfo);
   }

   public AppContext createApplication(EjbJarInfo ejbJar, ClassLoader classLoader) throws NamingException, IOException, OpenEJBException {
      return this.createEjbJar(ejbJar, classLoader);
   }

   public AppContext createEjbJar(EjbJarInfo ejbJar, ClassLoader classLoader) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = ejbJar.path;
      appInfo.appId = ejbJar.moduleName;
      appInfo.ejbJars.add(ejbJar);
      return this.createApplication(appInfo, classLoader);
   }

   public AppContext createClient(ClientInfo clientInfo) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = clientInfo.path;
      appInfo.appId = clientInfo.moduleId;
      appInfo.clients.add(clientInfo);
      return this.createApplication(appInfo);
   }

   public AppContext createClient(ClientInfo clientInfo, ClassLoader classLoader) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = clientInfo.path;
      appInfo.appId = clientInfo.moduleId;
      appInfo.clients.add(clientInfo);
      return this.createApplication(appInfo, classLoader);
   }

   public AppContext createConnector(ConnectorInfo connectorInfo) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = connectorInfo.path;
      appInfo.appId = connectorInfo.moduleId;
      appInfo.connectors.add(connectorInfo);
      return this.createApplication(appInfo);
   }

   public AppContext createConnector(ConnectorInfo connectorInfo, ClassLoader classLoader) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = connectorInfo.path;
      appInfo.appId = connectorInfo.moduleId;
      appInfo.connectors.add(connectorInfo);
      return this.createApplication(appInfo, classLoader);
   }

   public AppContext createWebApp(WebAppInfo webAppInfo) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = webAppInfo.path;
      appInfo.appId = webAppInfo.moduleId;
      appInfo.webApps.add(webAppInfo);
      return this.createApplication(appInfo);
   }

   public AppContext createWebApp(WebAppInfo webAppInfo, ClassLoader classLoader) throws NamingException, IOException, OpenEJBException {
      AppInfo appInfo = new AppInfo();
      appInfo.path = webAppInfo.path;
      appInfo.appId = webAppInfo.moduleId;
      appInfo.webApps.add(webAppInfo);
      return this.createApplication(appInfo, classLoader);
   }

   public AppContext createApplication(AppInfo appInfo) throws OpenEJBException, IOException, NamingException {
      return this.createApplication(appInfo, this.createAppClassLoader(appInfo));
   }

   public AppContext createApplication(AppInfo appInfo, ClassLoader classLoader) throws OpenEJBException, IOException, NamingException {
      return this.createApplication(appInfo, classLoader, true);
   }

   private AppContext createApplication(AppInfo appInfo, ClassLoader classLoader, boolean start) throws OpenEJBException, IOException, NamingException {
      AppContext var111;
      try {
         try {
            mergeServices(appInfo);
         } catch (URISyntaxException var71) {
            this.logger.info("Can't merge resources.xml services and appInfo.properties");
         }

         if (appInfo.appId == null) {
            throw new IllegalArgumentException("AppInfo.appId cannot be null");
         }

         if (appInfo.path == null) {
            appInfo.path = appInfo.appId;
         }

         Extensions.addExtensions(classLoader, appInfo.eventClassesNeedingAppClassloader);
         this.logger.info("createApplication.start", appInfo.path);
         Context containerSystemContext = this.containerSystem.getJNDIContext();
         List<String> used = this.getDuplicates(appInfo);
         if (used.size() > 0) {
            String message = this.logger.error("createApplication.appFailedDuplicateIds", appInfo.path);

            for(String id : used) {
               this.logger.error("createApplication.deploymentIdInUse", id);
               message = message + "\n    " + id;
            }

            throw new DuplicateDeploymentIdException(message);
         }

         InjectionBuilder injectionBuilder = new InjectionBuilder(classLoader);
         Set<Injection> injections = new HashSet();
         injections.addAll(injectionBuilder.buildInjections(appInfo.globalJndiEnc));
         injections.addAll(injectionBuilder.buildInjections(appInfo.appJndiEnc));
         JndiEncBuilder globalBuilder = new JndiEncBuilder(appInfo.globalJndiEnc, injections, appInfo.appId, (URI)null, "global", classLoader, appInfo.properties);
         Map<String, Object> globalBindings = globalBuilder.buildBindings(JndiEncBuilder.JndiScope.global);
         Context globalJndiContext = globalBuilder.build(globalBindings);
         JndiEncBuilder appBuilder = new JndiEncBuilder(appInfo.appJndiEnc, injections, appInfo.appId, (URI)null, appInfo.appId, classLoader, appInfo.properties);
         Map<String, Object> appBindings = appBuilder.buildBindings(JndiEncBuilder.JndiScope.app);
         Context appJndiContext = appBuilder.build(appBindings);
         boolean cdiActive = this.shouldStartCdi(appInfo);

         try {
            CmpJarBuilder cmpJarBuilder = new CmpJarBuilder(appInfo, classLoader);
            File generatedJar = cmpJarBuilder.getJarFile();
            if (generatedJar != null) {
               classLoader = ClassLoaderUtil.createClassLoader(appInfo.path, new URL[]{generatedJar.toURI().toURL()}, classLoader);
            }

            AppContext appContext = new AppContext(appInfo.appId, SystemInstance.get(), classLoader, globalJndiContext, appJndiContext, appInfo.standaloneModule);
            appContext.getProperties().putAll(appInfo.properties);
            appContext.getInjections().addAll(injections);
            appContext.getBindings().putAll(globalBindings);
            appContext.getBindings().putAll(appBindings);
            this.containerSystem.addAppContext(appContext);
            appContext.set(AsynchronousPool.class, AsynchronousPool.create(appContext));
            Map<String, LazyValidatorFactory> lazyValidatorFactories = new HashMap();
            Map<String, LazyValidator> lazyValidators = new HashMap();
            boolean isGeronimo = SystemInstance.get().hasProperty("openejb.geronimo");
            Map<ComparableValidationConfig, ValidatorFactory> validatorFactoriesByConfig = new HashMap();
            if (!isGeronimo) {
               List<CommonInfoObject> vfs = listCommonInfoObjectsForAppInfo(appInfo);
               Map<String, ValidatorFactory> validatorFactories = new HashMap();
               Iterator var24 = vfs.iterator();

               label948:
               while(true) {
                  CommonInfoObject info;
                  while(true) {
                     if (!var24.hasNext()) {
                        for(Map.Entry<String, ValidatorFactory> validatorFactory : validatorFactories.entrySet()) {
                           String id = (String)validatorFactory.getKey();
                           ValidatorFactory factory = (ValidatorFactory)validatorFactory.getValue();

                           try {
                              containerSystemContext.bind("openejb/ValidatorFactory/" + id, factory);

                              Validator validator;
                              try {
                                 LazyValidator lazyValidator = new LazyValidator(factory);
                                 validator = (Validator)Proxy.newProxyInstance(appContext.getClassLoader(), VALIDATOR_INTERFACES, lazyValidator);
                                 lazyValidators.put(id, lazyValidator);
                              } catch (Exception e) {
                                 this.logger.error(e.getMessage(), (Throwable)e);
                                 continue;
                              }

                              containerSystemContext.bind("openejb/Validator/" + id, validator);
                           } catch (NameAlreadyBoundException e) {
                              throw new OpenEJBException("ValidatorFactory already exists for module " + id, e);
                           } catch (Exception e) {
                              throw new OpenEJBException(e);
                           }
                        }

                        validatorFactories.clear();
                        break label948;
                     }

                     info = (CommonInfoObject)var24.next();
                     if (info.validationInfo != null) {
                        ComparableValidationConfig conf = new ComparableValidationConfig(info.validationInfo.providerClassName, info.validationInfo.messageInterpolatorClass, info.validationInfo.traversableResolverClass, info.validationInfo.constraintFactoryClass, info.validationInfo.parameterNameProviderClass, info.validationInfo.version, info.validationInfo.propertyTypes, info.validationInfo.constraintMappings, info.validationInfo.executableValidationEnabled, info.validationInfo.validatedTypes);
                        factory = (ValidatorFactory)validatorFactoriesByConfig.get(conf);
                        if (factory == null) {
                           try {
                              LazyValidatorFactory handler = new LazyValidatorFactory(classLoader, info.validationInfo);
                              factory = (ValidatorFactory)Proxy.newProxyInstance(appContext.getClassLoader(), VALIDATOR_FACTORY_INTERFACES, handler);
                              lazyValidatorFactories.put(info.uniqueId, handler);
                           } catch (ValidationException ve) {
                              this.logger.warning("can't build the validation factory for module " + info.uniqueId, (Throwable)ve);
                              continue;
                           }

                           validatorFactoriesByConfig.put(conf, factory);
                           break;
                        }

                        lazyValidatorFactories.put(info.uniqueId, LazyValidatorFactory.class.cast(Proxy.getInvocationHandler(factory)));
                        break;
                     }
                  }

                  validatorFactories.put(info.uniqueId, factory);
               }
            }

            Map<String, String> units = new HashMap();
            PersistenceBuilder persistenceBuilder = new PersistenceBuilder(this.persistenceClassLoaderHandler);

            for(PersistenceUnitInfo info : appInfo.persistenceUnits) {
               ReloadableEntityManagerFactory factory;
               try {
                  factory = persistenceBuilder.createEntityManagerFactory(info, classLoader, validatorFactoriesByConfig, cdiActive);
                  this.containerSystem.getJNDIContext().bind("openejb/PersistenceUnit/" + info.id, factory);
                  units.put(info.name, "openejb/PersistenceUnit/" + info.id);
               } catch (NameAlreadyBoundException var69) {
                  throw new OpenEJBException("PersistenceUnit already deployed: " + info.persistenceUnitRootUrl);
               } catch (Exception e) {
                  throw new OpenEJBException(e);
               }

               factory.register();
            }

            this.logger.debug("Loaded peristence units: " + units);

            for(ConnectorInfo connector : appInfo.connectors) {
               ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
               Thread.currentThread().setContextClassLoader(classLoader);

               try {
                  if (connector.resourceAdapter != null) {
                     this.createResource((Collection)null, connector.resourceAdapter);
                  }

                  for(ResourceInfo outbound : connector.outbound) {
                     this.createResource((Collection)null, outbound);
                     outbound.properties.setProperty("openejb.connector", "true");
                  }

                  for(MdbContainerInfo inbound : connector.inbound) {
                     this.createContainer(inbound);
                  }

                  for(ResourceInfo adminObject : connector.adminObject) {
                     this.createResource((Collection)null, adminObject);
                  }
               } finally {
                  Thread.currentThread().setContextClassLoader(oldClassLoader);
               }
            }

            List<BeanContext> allDeployments = this.initEjbs(classLoader, appInfo, appContext, injections, new ArrayList(), (String)null);
            if ("true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.propagate.application-exceptions", appInfo.properties.getProperty("openejb.propagate.application-exceptions", "false")))) {
               this.propagateApplicationExceptions(appInfo, classLoader, allDeployments);
            }

            if (cdiActive) {
               (new CdiBuilder()).build(appInfo, appContext, allDeployments);
               this.ensureWebBeansContext(appContext);
               appJndiContext.bind("app/BeanManager", appContext.getBeanManager());
               appContext.getBindings().put("app/BeanManager", appContext.getBeanManager());
            } else {
               appInfo.properties.setProperty("openejb.cdi.activated", "false");
            }

            if (!isGeronimo) {
               for(Map.Entry<String, LazyValidator> lazyValidator : lazyValidators.entrySet()) {
                  String id = (String)lazyValidator.getKey();
                  ValidatorFactory factory = ((LazyValidatorFactory)lazyValidatorFactories.get(lazyValidator.getKey())).getFactory();

                  try {
                     String factoryName = "openejb/ValidatorFactory/" + id;
                     containerSystemContext.unbind(factoryName);
                     containerSystemContext.bind(factoryName, factory);
                     String validatoryName = "openejb/Validator/" + id;

                     try {
                        Validator val = ((LazyValidator)lazyValidator.getValue()).getValidator();
                        containerSystemContext.unbind(validatoryName);
                        containerSystemContext.bind(validatoryName, val);
                     } catch (Exception e) {
                        this.logger.error(e.getMessage(), (Throwable)e);
                     }
                  } catch (NameAlreadyBoundException e) {
                     throw new OpenEJBException("ValidatorFactory already exists for module " + id, e);
                  } catch (Exception e) {
                     throw new OpenEJBException(e);
                  }
               }
            }

            this.startEjbs(start, allDeployments);

            for(ClientInfo clientInfo : appInfo.clients) {
               List<Injection> clientInjections = injectionBuilder.buildInjections(clientInfo.jndiEnc);
               JndiEncBuilder jndiEncBuilder = new JndiEncBuilder(clientInfo.jndiEnc, clientInjections, "Bean", clientInfo.moduleId, (URI)null, clientInfo.uniqueId, classLoader, new Properties());
               if (clientInfo.remoteClients.size() > 0 || clientInfo.localClients.size() == 0) {
                  jndiEncBuilder.setClient(true);
               }

               jndiEncBuilder.setUseCrossClassLoaderRef(false);
               Context context = jndiEncBuilder.build(JndiEncBuilder.JndiScope.comp);
               containerSystemContext.bind("openejb/client/" + clientInfo.moduleId, context);
               if (clientInfo.path != null) {
                  context.bind("info/path", clientInfo.path);
               }

               if (clientInfo.mainClass != null) {
                  context.bind("info/mainClass", clientInfo.mainClass);
               }

               if (clientInfo.callbackHandler != null) {
                  context.bind("info/callbackHandler", clientInfo.callbackHandler);
               }

               context.bind("info/injections", clientInjections);

               for(String clientClassName : clientInfo.remoteClients) {
                  containerSystemContext.bind("openejb/client/" + clientClassName, clientInfo.moduleId);
               }

               for(String clientClassName : clientInfo.localClients) {
                  containerSystemContext.bind("openejb/client/" + clientClassName, clientInfo.moduleId);
                  this.logger.getChildLogger("client").info("createApplication.createLocalClient", clientClassName, clientInfo.moduleId);
               }
            }

            SystemInstance systemInstance = SystemInstance.get();
            WebAppBuilder webAppBuilder = (WebAppBuilder)systemInstance.getComponent(WebAppBuilder.class);
            if (webAppBuilder != null) {
               webAppBuilder.deployWebApps(appInfo, classLoader);
            }

            if (start) {
               EjbResolver globalEjbResolver = (EjbResolver)systemInstance.getComponent(EjbResolver.class);
               globalEjbResolver.addAll(appInfo.ejbJars);
            }

            this.bindGlobals(appContext.getBindings());
            this.validateCdiResourceProducers(appContext, appInfo);

            for(String mbean : appInfo.mbeans) {
               this.deployMBean(appContext.getWebBeansContext(), classLoader, mbean, appInfo.jmx, appInfo.appId);
            }

            for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
               for(String mbean : ejbJarInfo.mbeans) {
                  this.deployMBean(appContext.getWebBeansContext(), classLoader, mbean, appInfo.jmx, ejbJarInfo.moduleName);
               }
            }

            for(ConnectorInfo connectorInfo : appInfo.connectors) {
               for(String mbean : connectorInfo.mbeans) {
                  this.deployMBean(appContext.getWebBeansContext(), classLoader, mbean, appInfo.jmx, appInfo.appId + ".add-lib");
               }
            }

            this.postConstructResources(appInfo.resourceIds, classLoader, containerSystemContext, appContext);
            this.deployedApplications.put(appInfo.path, appInfo);
            this.resumePersistentSchedulers(appContext);
            systemInstance.fireEvent(new AssemblerAfterApplicationCreated(appInfo, appContext, allDeployments));
            this.logger.info("createApplication.success", appInfo.path);
            if (this.setAppNamingContextReadOnly(allDeployments)) {
               this.logger.info("createApplication.naming", appInfo.path);
            }

            var111 = appContext;
         } catch (DeploymentException | ValidationException ve) {
            throw ve;
         } catch (Throwable t) {
            try {
               this.destroyApplication(appInfo);
            } catch (Exception e1) {
               this.logger.debug("createApplication.undeployFailed", e1, appInfo.path);
            }

            throw new OpenEJBException(this.messages.format("createApplication.failed", appInfo.path), t);
         }
      } finally {
         for(WebAppInfo webApp : appInfo.webApps) {
            appInfo.properties.remove(webApp);
         }

      }

      return var111;
   }

   boolean setAppNamingContextReadOnly(List<BeanContext> allDeployments) {
      if ("true".equals(SystemInstance.get().getProperty("openejb.forceReadOnlyAppNamingContext", "false"))) {
         for(BeanContext beanContext : allDeployments) {
            Context ctx = beanContext.getJndiContext();
            if (IvmContext.class.isInstance(ctx)) {
               ((IvmContext)IvmContext.class.cast(ctx)).setReadOnly(true);
            } else if (ContextHandler.class.isInstance(ctx)) {
               ((ContextHandler)ContextHandler.class.cast(ctx)).setReadOnly();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<String> getDuplicates(AppInfo appInfo) {
      List<String> used = new ArrayList();

      for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
         for(EnterpriseBeanInfo beanInfo : ejbJarInfo.enterpriseBeans) {
            if (this.containerSystem.getBeanContext(beanInfo.ejbDeploymentId) != null) {
               used.add(beanInfo.ejbDeploymentId);
            }
         }
      }

      return used;
   }

   private boolean shouldStartCdi(AppInfo appInfo) {
      if (!"true".equalsIgnoreCase(appInfo.properties.getProperty("openejb.cdi.activated", "true"))) {
         return false;
      } else {
         for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
            if (ejbJarInfo.beans != null && (!ejbJarInfo.beans.bdas.isEmpty() || !ejbJarInfo.beans.noDescriptorBdas.isEmpty())) {
               return true;
            }
         }

         return false;
      }
   }

   private void validateCdiResourceProducers(AppContext appContext, AppInfo info) {
      if (appContext.getWebBeansContext() != null) {
         if (appContext.isStandaloneModule() && !appContext.getProperties().containsKey("openejb.cdi.skip-resource-validation")) {
            Map<String, Object> bindings = appContext.getWebContexts().isEmpty() ? appContext.getBindings() : ((WebContext)appContext.getWebContexts().iterator().next()).getBindings();
            if (bindings != null && appContext.getWebBeansContext() != null && appContext.getWebBeansContext().getBeanManagerImpl().isInUse()) {
               Iterator var4 = appContext.getWebBeansContext().getBeanManagerImpl().getBeans().iterator();

               while(true) {
                  ResourceReference reference;
                  Object lookup;
                  label121:
                  while(true) {
                     if (!var4.hasNext()) {
                        return;
                     }

                     Bean<?> bean = (Bean)var4.next();
                     if (ResourceBean.class.isInstance(bean)) {
                        reference = ((ResourceBean)ResourceBean.class.cast(bean)).getReference();
                        String jndi = reference.getJndiName().replace("java:", "");
                        if (reference.getJndiName().startsWith("java:/")) {
                           jndi = jndi.substring(1);
                        }

                        lookup = bindings.get(jndi);
                        if (lookup == null && reference.getAnnotation(EJB.class) != null) {
                           CdiPlugin plugin = (CdiPlugin)CdiPlugin.class.cast(appContext.getWebBeansContext().getPluginLoader().getEjbPlugin());
                           if (!plugin.isSessionBean(reference.getResourceType())) {
                              boolean ok = false;

                              for(BeanContext bc : appContext.getBeanContexts()) {
                                 if (bc.getBusinessLocalInterfaces().contains(reference.getResourceType()) || bc.getBusinessRemoteInterfaces().contains(reference.getResourceType())) {
                                    ok = true;
                                    break;
                                 }
                              }

                              if (!ok) {
                                 throw new DefinitionException("EJB " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to " + reference.getResourceType());
                              }
                           }
                        }

                        if (!Reference.class.isInstance(lookup)) {
                           if (lookup != null) {
                              break;
                           }

                           Resource r = (Resource)Resource.class.cast(reference.getAnnotation(Resource.class));
                           if (r == null) {
                              break;
                           }

                           if (!r.lookup().isEmpty()) {
                              this.checkBuiltInResourceTypes(reference, r.lookup());
                              break;
                           }

                           if (r.name().isEmpty()) {
                              break;
                           }

                           String name = "comp/env/" + r.name();
                           boolean done = false;
                           Iterator var20 = info.webApps.iterator();

                           while(true) {
                              if (!var20.hasNext()) {
                                 break label121;
                              }

                              WebAppInfo w = (WebAppInfo)var20.next();

                              for(EnvEntryInfo e : w.jndiEnc.envEntries) {
                                 if (name.equals(e.referenceName)) {
                                    if (e.type != null && !reference.getResourceType().getName().equals(e.type)) {
                                       throw new DefinitionException("Env Entry " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to " + reference.getResourceType());
                                    }

                                    done = true;
                                    break;
                                 }
                              }

                              if (done) {
                                 break label121;
                              }
                           }
                        }

                        try {
                           lookup = ((Reference)Reference.class.cast(lookup)).getContent();
                           break;
                        } catch (Exception var16) {
                           if (JndiUrlReference.class.isInstance(lookup)) {
                              this.checkBuiltInResourceTypes(reference, ((JndiUrlReference)JndiUrlReference.class.cast(lookup)).getJndiName());
                           }
                        }
                     }
                  }

                  if (lookup != null && !reference.getResourceType().isInstance(lookup)) {
                     throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast, instance is " + lookup);
                  }
               }
            }
         }

      }
   }

   private void checkBuiltInResourceTypes(ResourceReference reference, String jndi) {
      Class<?> resourceType = reference.getResourceType();
      if ("java:comp/BeanManager".equals(jndi) && resourceType != BeanManager.class) {
         throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to a BeanManager");
      } else if ("java:comp/TransactionSynchronizationRegistry".equals(jndi) && resourceType != TransactionSynchronizationRegistry.class) {
         throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to a TransactionSynchronizationRegistry");
      } else if ("java:comp/TransactionManager".equals(jndi) && resourceType != TransactionManager.class) {
         throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to a TransactionManager");
      } else if ("java:comp/ValidatorFactory".equals(jndi) && resourceType != ValidatorFactory.class) {
         throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to a ValidatorFactory");
      } else if ("java:comp/Validator".equals(jndi) && resourceType != Validator.class) {
         throw new DefinitionException("Resource " + reference.getJndiName() + " in " + reference.getOwnerClass() + " can't be cast to a Validator");
      }
   }

   private void postConstructResources(Set<String> resourceIds, ClassLoader classLoader, Context containerSystemContext, AppContext appContext) throws NamingException, OpenEJBException {
      Thread thread = Thread.currentThread();
      ClassLoader oldCl = thread.getContextClassLoader();

      try {
         thread.setContextClassLoader(classLoader);

         for(ResourceInfo resourceInfo : this.config.facilities.resources) {
            if (resourceIds.contains(resourceInfo.id) && !isTemplatizedResource(resourceInfo)) {
               try {
                  Class<?> clazz;
                  try {
                     clazz = classLoader.loadClass(resourceInfo.className);
                  } catch (ClassNotFoundException var25) {
                     clazz = containerSystemContext.lookup("openejb/Resource/" + resourceInfo.id).getClass();
                  }

                  boolean initialize = "true".equalsIgnoreCase(String.valueOf(resourceInfo.properties.remove("InitializeAfterDeployment")));
                  AnnotationFinder finder = Proxy.isProxyClass(clazz) ? null : new AnnotationFinder(new ClassesArchive(Classes.ancestors(clazz)));
                  List<Method> postConstructs = finder == null ? Collections.emptyList() : finder.findAnnotatedMethods(PostConstruct.class);
                  List<Method> preDestroys = finder == null ? Collections.emptyList() : finder.findAnnotatedMethods(PreDestroy.class);
                  resourceInfo.postConstructMethods = new ArrayList();
                  resourceInfo.preDestroyMethods = new ArrayList();
                  this.addMethodsToResourceInfo(resourceInfo.postConstructMethods, PostConstruct.class, postConstructs);
                  this.addMethodsToResourceInfo(resourceInfo.preDestroyMethods, PreDestroy.class, preDestroys);
                  CreationalContext<?> creationalContext = null;
                  Object originalResource = null;
                  if (!postConstructs.isEmpty() || initialize) {
                     originalResource = containerSystemContext.lookup("openejb/Resource/" + resourceInfo.id);
                     Object resource = originalResource;
                     if (originalResource instanceof Reference) {
                        resource = unwrapReference(originalResource);
                        this.bindResource(resourceInfo.id, resource, true);
                     }

                     try {
                        if (appContext != null && appContext.getWebBeansContext() != null) {
                           BeanManagerImpl beanManager = appContext.getWebBeansContext().getBeanManagerImpl();
                           if (beanManager.isInUse()) {
                              creationalContext = beanManager.createCreationalContext((Contextual)null);
                              OWBInjector.inject(beanManager, resource, creationalContext);
                           }
                        }

                        if (!"none".equals(resourceInfo.postConstruct)) {
                           if (resourceInfo.postConstruct != null) {
                              Method p = clazz.getDeclaredMethod(resourceInfo.postConstruct);
                              if (!p.isAccessible()) {
                                 SetAccessible.on(p);
                              }

                              p.invoke(resource);
                           }

                           for(Method m : postConstructs) {
                              if (!m.isAccessible()) {
                                 SetAccessible.on(m);
                              }

                              m.invoke(resource);
                           }
                        }
                     } catch (Exception e) {
                        this.logger.fatal("Error calling @PostConstruct method on " + resource.getClass().getName());
                        throw new OpenEJBException(e);
                     }
                  }

                  if (!"none".equals(resourceInfo.preDestroy)) {
                     if (resourceInfo.preDestroy != null) {
                        Method p = clazz.getDeclaredMethod(resourceInfo.preDestroy);
                        if (!p.isAccessible()) {
                           SetAccessible.on(p);
                        }

                        preDestroys.add(p);
                     }

                     if (!preDestroys.isEmpty() || creationalContext != null) {
                        String name = "openejb/Resource/" + resourceInfo.id;
                        if (originalResource == null) {
                           originalResource = containerSystemContext.lookup(name);
                        }

                        this.bindResource(resourceInfo.id, new ResourceInstance(name, originalResource, preDestroys, creationalContext), true);
                     }
                  }

                  if (resourceInfo.unsetProperties != null && !isPassthroughType(resourceInfo)) {
                     for(String key : resourceInfo.unsetProperties.stringPropertyNames()) {
                        unusedProperty(resourceInfo.id, this.logger, key);
                     }
                  }
               } catch (Exception e) {
                  this.logger.fatal("Error calling PostConstruct method on " + resourceInfo.id);
                  this.logger.fatal("Resource " + resourceInfo.id + " could not be initialized. Application will be undeployed.");
                  throw new OpenEJBException(e);
               }
            }
         }
      } finally {
         thread.setContextClassLoader(oldCl);
      }

   }

   private void addMethodsToResourceInfo(List<String> list, Class type, List<Method> methodList) throws OpenEJBException {
      for(Method method : methodList) {
         if (method.getParameterTypes().length > 0) {
            throw new OpenEJBException(type.getSimpleName() + " method " + method.getDeclaringClass().getName() + "." + method.getName() + " should have zero arguments");
         }

         list.add(method.getName());
      }

   }

   private static boolean isTemplatizedResource(ResourceInfo resourceInfo) {
      return resourceInfo.className == null || resourceInfo.className.isEmpty();
   }

   public static void mergeServices(AppInfo appInfo) throws URISyntaxException {
      for(ServiceInfo si : appInfo.services) {
         if (!appInfo.properties.containsKey(si.id)) {
            Map<String, String> query = new HashMap();
            if (si.types != null && !si.types.isEmpty()) {
               query.put("type", si.types.iterator().next());
            }

            if (si.className != null) {
               query.put("class-name", si.className);
            }

            if (si.factoryMethod != null) {
               query.put("factory-name", si.factoryMethod);
            }

            if (si.constructorArgs != null && !si.constructorArgs.isEmpty()) {
               query.put("constructor", Join.join(",", (Collection)si.constructorArgs));
            }

            appInfo.properties.put(si.id, "new://Service?" + URISupport.createQueryString(query));
            if (si.properties != null) {
               for(String k : si.properties.stringPropertyNames()) {
                  appInfo.properties.setProperty(si.id + "." + k, si.properties.getProperty(k));
               }
            }
         }
      }

   }

   private static List<CommonInfoObject> listCommonInfoObjectsForAppInfo(AppInfo appInfo) {
      List<CommonInfoObject> vfs = new ArrayList(appInfo.clients.size() + appInfo.connectors.size() + appInfo.ejbJars.size() + appInfo.webApps.size());

      for(ClientInfo clientInfo : appInfo.clients) {
         vfs.add(clientInfo);
      }

      for(ConnectorInfo connectorInfo : appInfo.connectors) {
         vfs.add(connectorInfo);
      }

      for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
         vfs.add(ejbJarInfo);
      }

      for(WebAppInfo webAppInfo : appInfo.webApps) {
         vfs.add(webAppInfo);
      }

      return vfs;
   }

   public void bindGlobals(Map<String, Object> bindings) throws NamingException {
      Context containerSystemContext = this.containerSystem.getJNDIContext();

      for(Map.Entry<String, Object> value : bindings.entrySet()) {
         String path = (String)value.getKey();
         if (!path.startsWith("module/") && !path.startsWith("app/") && !path.startsWith("comp/") && !path.equalsIgnoreCase("global/dummy")) {
            Context lastContext = Contexts.createSubcontexts(containerSystemContext, path);

            try {
               lastContext.rebind(path.substring(path.lastIndexOf("/") + 1, path.length()), value.getValue());
            } catch (NameAlreadyBoundException nabe) {
               nabe.printStackTrace();
            }

            containerSystemContext.rebind(path, value.getValue());
         }
      }

   }

   private void propagateApplicationExceptions(AppInfo appInfo, ClassLoader classLoader, List<BeanContext> allDeployments) {
      for(BeanContext context : allDeployments) {
         if (!BeanContext.Comp.class.equals(context.getBeanClass())) {
            for(EjbJarInfo jar : appInfo.ejbJars) {
               for(ApplicationExceptionInfo exception : jar.applicationException) {
                  try {
                     Class<?> exceptionClass = classLoader.loadClass(exception.exceptionClass);
                     context.addApplicationException(exceptionClass, exception.rollback, exception.inherited);
                  } catch (Exception var11) {
                  }
               }
            }
         }
      }

   }

   private void resumePersistentSchedulers(AppContext appContext) {
      try {
         Scheduler globalScheduler = (Scheduler)SystemInstance.get().getComponent(Scheduler.class);
         Collection<Scheduler> schedulers = new ArrayList();

         for(BeanContext ejb : appContext.getBeanContexts()) {
            Scheduler scheduler = (Scheduler)ejb.get(Scheduler.class);
            if (scheduler != null && scheduler != globalScheduler && !schedulers.contains(scheduler)) {
               schedulers.add(scheduler);

               try {
                  scheduler.resumeAll();
               } catch (Exception e) {
                  this.logger.warning("Can't resume scheduler for " + ejb.getEjbName(), (Throwable)e);
               }
            }
         }
      } catch (NoClassDefFoundError var9) {
      }

   }

   public List<BeanContext> initEjbs(ClassLoader classLoader, AppInfo appInfo, AppContext appContext, Set<Injection> injections, List<BeanContext> allDeployments, String webappId) throws OpenEJBException {
      String globalTimersOn = SystemInstance.get().getProperty("openejb.timers.on", "true");
      EjbJarBuilder ejbJarBuilder = new EjbJarBuilder(this.props, appContext);

      for(EjbJarInfo ejbJar : appInfo.ejbJars) {
         if (!this.isSkip(appInfo, webappId, ejbJar)) {
            HashMap<String, BeanContext> deployments = ejbJarBuilder.build(ejbJar, injections, classLoader);
            JaccPermissionsBuilder jaccPermissionsBuilder = new JaccPermissionsBuilder();
            PolicyContext policyContext = jaccPermissionsBuilder.build(ejbJar, deployments);
            jaccPermissionsBuilder.install(policyContext);
            TransactionPolicyFactory transactionPolicyFactory = this.createTransactionPolicyFactory(ejbJar, classLoader);

            for(BeanContext beanContext : deployments.values()) {
               beanContext.setTransactionPolicyFactory(transactionPolicyFactory);
            }

            MethodTransactionBuilder methodTransactionBuilder = new MethodTransactionBuilder();
            methodTransactionBuilder.build(deployments, ejbJar.methodTransactions);
            MethodConcurrencyBuilder methodConcurrencyBuilder = new MethodConcurrencyBuilder();
            methodConcurrencyBuilder.build(deployments, ejbJar.methodConcurrency);

            for(BeanContext beanContext : deployments.values()) {
               this.containerSystem.addDeployment(beanContext);
            }

            this.jndiBuilder.build(ejbJar, deployments);

            for(BeanContext beanContext : deployments.values()) {
               if (beanContext.getComponentType() != BeanType.STATEFUL) {
                  Method ejbTimeout = beanContext.getEjbTimeout();
                  boolean timerServiceRequired = false;
                  if (ejbTimeout != null) {
                     if (beanContext.getTransactionType(ejbTimeout) == TransactionType.RequiresNew) {
                        beanContext.setMethodTransactionAttribute(ejbTimeout, TransactionType.Required);
                     }

                     timerServiceRequired = true;
                  }

                  Iterator<Map.Entry<Method, MethodContext>> it = beanContext.iteratorMethodContext();

                  while(it.hasNext()) {
                     Map.Entry<Method, MethodContext> entry = (Map.Entry)it.next();
                     MethodContext methodContext = (MethodContext)entry.getValue();
                     if (methodContext.getSchedules().size() > 0) {
                        timerServiceRequired = true;
                        Method method = (Method)entry.getKey();
                        if (beanContext.getTransactionType(method) == TransactionType.RequiresNew) {
                           beanContext.setMethodTransactionAttribute(method, TransactionType.Required);
                        }
                     }
                  }

                  if (timerServiceRequired && "true".equalsIgnoreCase(appInfo.properties.getProperty("openejb.timers.on", globalTimersOn))) {
                     EjbTimerServiceImpl timerService = new EjbTimerServiceImpl(beanContext, this.newTimerStore(beanContext));
                     TimerStore timerStore = timerService.getTimerStore();
                     Iterator<Map.Entry<Method, MethodContext>> it = beanContext.iteratorMethodContext();

                     while(it.hasNext()) {
                        Map.Entry<Method, MethodContext> entry = (Map.Entry)it.next();
                        MethodContext methodContext = (MethodContext)entry.getValue();

                        for(ScheduleData scheduleData : methodContext.getSchedules()) {
                           timerStore.createCalendarTimer(timerService, (String)beanContext.getDeploymentID(), (Object)null, (Method)entry.getKey(), scheduleData.getExpression(), scheduleData.getConfig(), true);
                        }
                     }

                     beanContext.setEjbTimerService(timerService);
                  } else {
                     beanContext.setEjbTimerService(new NullEjbTimerServiceImpl());
                  }
               }

               Iterator<Map.Entry<Method, MethodContext>> it = beanContext.iteratorMethodContext();

               while(it.hasNext()) {
                  Map.Entry<Method, MethodContext> entry = (Map.Entry)it.next();
                  if (((MethodContext)entry.getValue()).isAsynchronous() && beanContext.getTransactionType((Method)entry.getKey()) == TransactionType.RequiresNew) {
                     beanContext.setMethodTransactionAttribute((Method)entry.getKey(), TransactionType.Required);
                  }
               }

               if (beanContext.isLocalbean() && !beanContext.getComponentType().isMessageDriven() && !beanContext.isDynamicallyImplemented()) {
                  List<Class> interfaces = new ArrayList(3);
                  interfaces.add(Serializable.class);
                  interfaces.add(IntraVmProxy.class);
                  BeanType type = beanContext.getComponentType();
                  if (BeanType.STATEFUL.equals(type) || BeanType.MANAGED.equals(type)) {
                     interfaces.add(BeanContext.Removable.class);
                  }

                  beanContext.set(BeanContext.ProxyClass.class, new BeanContext.ProxyClass(beanContext, (Class[])interfaces.toArray(new Class[interfaces.size()])));
               }
            }

            for(ApplicationExceptionInfo exceptionInfo : ejbJar.applicationException) {
               try {
                  Class exceptionClass = classLoader.loadClass(exceptionInfo.exceptionClass);

                  for(BeanContext beanContext : deployments.values()) {
                     beanContext.addApplicationException(exceptionClass, exceptionInfo.rollback, exceptionInfo.inherited);
                  }
               } catch (ClassNotFoundException e) {
                  this.logger.error("createApplication.invalidClass", e, exceptionInfo.exceptionClass, e.getMessage());
               }
            }

            allDeployments.addAll(deployments.values());
         }
      }

      List<BeanContext> ejbs = sort(allDeployments);

      for(BeanContext b : ejbs) {
         if (!appContext.getBeanContexts().contains(b)) {
            appContext.getBeanContexts().add(b);
         }
      }

      return ejbs;
   }

   private boolean isSkip(AppInfo appInfo, String webappId, EjbJarInfo ejbJar) {
      boolean skip = false;
      if (!appInfo.webAppAlone) {
         if (webappId == null) {
            skip = ejbJar.webapp;
         } else if (!ejbJar.webapp || !ejbJar.moduleId.equals(webappId) && !ejbJar.properties.getProperty("openejb.ejbmodule.webappId", "-").equals(webappId)) {
            skip = true;
         }
      }

      return skip;
   }

   private TimerStore newTimerStore(BeanContext beanContext) {
      for(DeploymentContext context : Arrays.asList(beanContext, beanContext.getModuleContext(), beanContext.getModuleContext().getAppContext())) {
         String timerStoreClass = context.getProperties().getProperty("timerStore.class");
         if (timerStoreClass != null) {
            this.logger.info("Found timer class: " + timerStoreClass);

            try {
               Class<?> clazz = beanContext.getClassLoader().loadClass(timerStoreClass);

               try {
                  Constructor<?> constructor = clazz.getConstructor(TransactionManager.class);
                  return (TimerStore)TimerStore.class.cast(constructor.newInstance(EjbTimerServiceImpl.getDefaultTransactionManager()));
               } catch (Exception var7) {
                  return (TimerStore)TimerStore.class.cast(clazz.newInstance());
               }
            } catch (Exception var8) {
               this.logger.error("Can't instantiate " + timerStoreClass + ", using default memory timer store");
            }
         }
      }

      return new MemoryTimerStore(EjbTimerServiceImpl.getDefaultTransactionManager());
   }

   public void startEjbs(boolean start, List<BeanContext> allDeployments) throws OpenEJBException {
      if (start) {
         SystemInstance.get().fireEvent(new BeforeStartEjbs(allDeployments));
         Collection<BeanContext> toStart = new ArrayList();

         for(BeanContext deployment : allDeployments) {
            try {
               Container container = deployment.getContainer();
               if (container.getBeanContext(deployment.getDeploymentID()) == null) {
                  container.deploy(deployment);
                  if (!((String)deployment.getDeploymentID()).endsWith(".Comp") && !deployment.isHidden()) {
                     this.logger.info("createApplication.createdEjb", deployment.getDeploymentID(), deployment.getEjbName(), container.getContainerID());
                  }

                  if (this.logger.isDebugEnabled()) {
                     for(Map.Entry<Object, Object> entry : deployment.getProperties().entrySet()) {
                        this.logger.info("createApplication.createdEjb.property", deployment.getEjbName(), entry.getKey(), entry.getValue());
                     }
                  }

                  toStart.add(deployment);
               }
            } catch (Throwable t) {
               throw new OpenEJBException("Error deploying '" + deployment.getEjbName() + "'.  Exception: " + t.getClass() + ": " + t.getMessage(), t);
            }
         }

         for(BeanContext deployment : toStart) {
            try {
               Container container = deployment.getContainer();
               container.start(deployment);
               if (!((String)deployment.getDeploymentID()).endsWith(".Comp") && !deployment.isHidden()) {
                  this.logger.info("createApplication.startedEjb", deployment.getDeploymentID(), deployment.getEjbName(), container.getContainerID());
               }
            } catch (Throwable t) {
               throw new OpenEJBException("Error starting '" + deployment.getEjbName() + "'.  Exception: " + t.getClass() + ": " + t.getMessage(), t);
            }
         }
      }

   }

   private void deployMBean(WebBeansContext wc, ClassLoader cl, String mbeanClass, Properties appMbeans, String id) {
      if (LocalMBeanServer.isJMXActive()) {
         Class<?> clazz;
         try {
            clazz = cl.loadClass(mbeanClass);
         } catch (ClassNotFoundException e) {
            throw new OpenEJBRuntimeException(e);
         }

         Bean<?> bean;
         BeanManager bm;
         if (wc == null) {
            bm = null;
            bean = null;
         } else {
            bm = wc.getBeanManagerImpl();
            Set<Bean<?>> beans = bm.getBeans(clazz, new Annotation[0]);
            bean = bm.resolve(beans);
         }

         CreationalContext creationalContext;
         Object instance;
         if (bean == null) {
            try {
               instance = clazz.newInstance();
            } catch (InstantiationException e) {
               this.logger.error("the mbean " + mbeanClass + " can't be registered because it can't be instantiated", (Throwable)e);
               return;
            } catch (IllegalAccessException e) {
               this.logger.error("the mbean " + mbeanClass + " can't be registered because it can't be accessed", (Throwable)e);
               return;
            }

            creationalContext = null;
         } else {
            creationalContext = bm.createCreationalContext(bean);
            instance = bm.getReference(bean, clazz, creationalContext);
         }

         MBeanServer server = LocalMBeanServer.get();

         try {
            MBean annotation = (MBean)clazz.getAnnotation(MBean.class);
            ObjectName leaf = annotation != null && !annotation.objectName().isEmpty() ? new ObjectName(annotation.objectName()) : (new ObjectNameBuilder("openejb.user.mbeans")).set("application", id).set("group", clazz.getPackage().getName()).set("name", clazz.getSimpleName()).build();
            server.registerMBean(new DynamicMBeanWrapper(wc, instance), leaf);
            appMbeans.put(mbeanClass, leaf.getCanonicalName());
            if (creationalContext != null && (bean.getScope() == null || Dependent.class.equals(bean.getScope()))) {
               this.creationalContextForAppMbeans.put(leaf, creationalContext);
            }

            this.logger.info("Deployed MBean(" + leaf.getCanonicalName() + ")");
         } catch (Exception e) {
            this.logger.error("the mbean " + mbeanClass + " can't be registered", (Throwable)e);
         }
      }

   }

   private void ensureWebBeansContext(AppContext appContext) {
      WebBeansContext webBeansContext = (WebBeansContext)appContext.get(WebBeansContext.class);
      if (webBeansContext == null) {
         webBeansContext = appContext.getWebBeansContext();
         if (webBeansContext == null) {
            Map<Class<?>, Object> services = new HashMap();
            services.put(JNDIService.class, new OpenEJBJndiService());
            services.put(AppContext.class, appContext);
            services.put(ScannerService.class, new CdiScanner());
            services.put(BeanArchiveService.class, new OpenEJBBeanInfoService());
            services.put(ELAdaptor.class, new CustomELAdapter(appContext));
            services.put(LoaderService.class, new OptimizedLoaderService(appContext.getProperties()));
            Properties properties = new Properties();
            properties.setProperty(org.apache.webbeans.spi.SecurityService.class.getName(), ManagedSecurityService.class.getName());
            properties.setProperty(ContextsService.class.getName(), CdiAppContextsService.class.getName());
            properties.setProperty(ResourceInjectionService.class.getName(), CdiResourceInjectionService.class.getName());
            properties.setProperty(TransactionService.class.getName(), OpenEJBTransactionService.class.getName());
            webBeansContext = new WebBeansContext(services, properties);
            appContext.setCdiEnabled(false);
            appContext.set(WebBeansContext.class, webBeansContext);
            appContext.setWebBeansContext(webBeansContext);
         }

      } else {
         if (null == appContext.getWebBeansContext()) {
            appContext.setWebBeansContext(webBeansContext);
         }

      }
   }

   private TransactionPolicyFactory createTransactionPolicyFactory(EjbJarInfo ejbJar, ClassLoader classLoader) {
      TransactionPolicyFactory factory = null;
      Object value = ejbJar.properties.get(TransactionPolicyFactory.class.getName());
      if (value instanceof TransactionPolicyFactory) {
         factory = (TransactionPolicyFactory)value;
      } else if (value instanceof String) {
         try {
            String[] parts = ((String)value).split(":", 2);
            ResourceFinder finder = new ResourceFinder("META-INF", classLoader);
            Map<String, Class<? extends TransactionPolicyFactory>> plugins = finder.mapAvailableImplementations(TransactionPolicyFactory.class);
            Class<? extends TransactionPolicyFactory> clazz = (Class)plugins.get(parts[0]);
            if (clazz != null) {
               if (parts.length == 1) {
                  factory = (TransactionPolicyFactory)clazz.getConstructor(String.class).newInstance(parts[1]);
               } else {
                  factory = (TransactionPolicyFactory)clazz.newInstance();
               }
            }
         } catch (Exception var9) {
         }
      }

      if (factory == null) {
         factory = new JtaTransactionPolicyFactory(this.transactionManager);
      }

      return factory;
   }

   private static List<BeanContext> sort(List<BeanContext> deployments) {
      Collections.sort(deployments, new Comparator<BeanContext>() {
         public int compare(BeanContext a, BeanContext b) {
            int aa = a.getComponentType() == BeanType.SINGLETON ? 1 : 0;
            int bb = b.getComponentType() == BeanType.SINGLETON ? 1 : 0;
            return aa - bb;
         }
      });
      deployments = References.<BeanContext>sort(deployments, new References.Visitor<BeanContext>() {
         public String getName(BeanContext t) {
            return (String)t.getDeploymentID();
         }

         public Set<String> getReferences(BeanContext t) {
            return t.getDependsOn();
         }
      });
      Collections.sort(deployments, new Comparator<BeanContext>() {
         public int compare(BeanContext a, BeanContext b) {
            int aa = a.getComponentType() == BeanType.MESSAGE_DRIVEN ? 1 : 0;
            int bb = b.getComponentType() == BeanType.MESSAGE_DRIVEN ? 1 : 0;
            return aa - bb;
         }
      });
      return deployments;
   }

   public void destroy() {
      ReentrantLock l = lock;
      l.lock();

      try {
         SystemInstance systemInstance = SystemInstance.get();
         systemInstance.fireEvent(new ContainerSystemPreDestroy());

         try {
            EjbTimerServiceImpl.shutdown();
         } catch (Exception e) {
            this.logger.warning("Unable to shutdown scheduler", (Throwable)e);
         } catch (NoClassDefFoundError var29) {
         }

         this.logger.debug("Undeploying Applications");
         Assembler assembler = this;
         List<AppInfo> deployedApps = new ArrayList(this.getDeployedApplications());
         Collections.reverse(deployedApps);

         for(AppInfo appInfo : deployedApps) {
            try {
               assembler.destroyApplication(appInfo.path);
            } catch (UndeployException e) {
               this.logger.error("Undeployment failed: " + appInfo.path, (Throwable)e);
            } catch (NoSuchApplicationException var27) {
            }
         }

         Iterator<ObjectName> it = this.containerObjectNames.iterator();

         for(MBeanServer server = LocalMBeanServer.get(); it.hasNext(); it.remove()) {
            try {
               server.unregisterMBean((ObjectName)it.next());
            } catch (Exception var25) {
            }
         }

         try {
            this.remoteResourceMonitor.unregister();
         } catch (Exception var24) {
         }

         NamingEnumeration<Binding> namingEnumeration = null;

         try {
            namingEnumeration = this.containerSystem.getJNDIContext().listBindings("openejb/Resource");
         } catch (NamingException var23) {
         }

         this.destroyResourceTree("", namingEnumeration);

         try {
            this.containerSystem.getJNDIContext().unbind("java:global");
         } catch (NamingException var22) {
         }

         systemInstance.removeComponent(OpenEjbConfiguration.class);
         systemInstance.removeComponent(JtaEntityManagerRegistry.class);
         systemInstance.removeComponent(TransactionSynchronizationRegistry.class);
         systemInstance.removeComponent(EjbResolver.class);
         systemInstance.fireEvent(new AssemblerDestroyed());
         systemInstance.removeObservers();
         if (DestroyableResource.class.isInstance(this.securityService)) {
            ((DestroyableResource)DestroyableResource.class.cast(this.securityService)).destroyResource();
         }

         if (DestroyableResource.class.isInstance(this.transactionManager)) {
            ((DestroyableResource)DestroyableResource.class.cast(this.transactionManager)).destroyResource();
         }

         for(Container c : this.containerSystem.containers()) {
            if (DestroyableResource.class.isInstance(c)) {
               ((DestroyableResource)DestroyableResource.class.cast(c)).destroyResource();
            }
         }

         SystemInstance.reset();
      } finally {
         l.unlock();
      }

   }

   private Collection<DestroyingResource> destroyResourceTree(String base, NamingEnumeration<Binding> namingEnumeration) {
      List<DestroyingResource> resources = new LinkedList();

      while(namingEnumeration != null && namingEnumeration.hasMoreElements()) {
         Binding binding = (Binding)namingEnumeration.nextElement();
         Object object = binding.getObject();
         if (Context.class.isInstance(object)) {
            try {
               resources.addAll(this.destroyResourceTree(IvmContext.class.isInstance(object) ? ((IvmContext)IvmContext.class.cast(object)).mynode.getAtomicName() : "", ((Context)Context.class.cast(object)).listBindings("")));
            } catch (Exception var8) {
            }
         } else {
            resources.add(new DestroyingResource((base != null && !base.isEmpty() ? base + '/' : "") + binding.getName(), binding.getClassName(), object));
         }
      }

      Collections.sort(resources, new Comparator<DestroyingResource>() {
         public int compare(DestroyingResource o1, DestroyingResource o2) {
            boolean ra1 = this.isRa(o1.instance);
            boolean ra2 = this.isRa(o2.instance);
            if (ra2 && !ra1) {
               return -1;
            } else {
               return ra1 && !ra2 ? 1 : o1.name.compareTo(o2.name);
            }
         }

         private boolean isRa(Object instance) {
            return ResourceAdapter.class.isInstance(instance) || ResourceAdapterReference.class.isInstance(instance);
         }
      });

      for(DestroyingResource resource : resources) {
         try {
            this.destroyResource(resource.name, resource.clazz, resource.instance);
         } catch (Throwable th) {
            this.logger.debug(th.getMessage(), th);
         }
      }

      return resources;
   }

   private void destroyResource(final String name, final String className, Object inObject) {
      Object object;
      try {
         object = LazyResource.class.isInstance(inObject) && ((LazyResource)LazyResource.class.cast(inObject)).isInitialized() ? ((LazyResource)LazyResource.class.cast(inObject)).getObject() : inObject;
      } catch (NamingException var24) {
         object = inObject;
      }

      Collection<Method> preDestroy = null;
      if (this.resourceDestroyTimeout != null) {
         Duration d = new Duration(this.resourceDestroyTimeout);
         ExecutorService es = Executors.newSingleThreadExecutor(new DaemonThreadFactory(new Object[]{"openejb-resource-destruction-" + name}));
         final Object o = object;

         try {
            es.submit(new Runnable() {
               public void run() {
                  Assembler.this.doResourceDestruction(name, className, o);
               }
            }).get(d.getTime(), d.getUnit());
         } catch (InterruptedException var21) {
            Thread.interrupted();
         } catch (ExecutionException e) {
            throw (RuntimeException)RuntimeException.class.cast(e.getCause());
         } catch (TimeoutException e) {
            this.logger.error("Can't destroy " + name + " in " + this.resourceDestroyTimeout + ", giving up.", (Throwable)e);
            if (this.threadStackOnTimeout) {
               ThreadInfo[] dump = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
               ByteArrayOutputStream writer = new ByteArrayOutputStream();
               PrintStream stream = new PrintStream(writer);

               for(ThreadInfo info : dump) {
                  stream.println('"' + info.getThreadName() + "\" suspended=" + info.isSuspended() + " state=" + info.getThreadState());

                  for(StackTraceElement traceElement : info.getStackTrace()) {
                     stream.println("\tat " + traceElement);
                  }
               }

               this.logger.info("Dump on " + name + " destruction timeout:\n" + new String(writer.toByteArray()));
            }
         }
      } else {
         this.doResourceDestruction(name, className, object);
      }

      this.callPreDestroy(name, object);
      this.removeResourceInfo(name);
   }

   private void callPreDestroy(String name, Object object) {
      if (object != null) {
         if (ResourceInstance.class.isInstance(object)) {
            ((ResourceInstance)ResourceInstance.class.cast(object)).destroyResource();
         } else {
            Class<?> objectClass = object.getClass();
            ResourceInfo ri = this.findResourceInfo(name);
            if (ri != null) {
               Set<String> destroyMethods = new HashSet();
               if (ri.preDestroy != null) {
                  destroyMethods.add(ri.preDestroy);
               }

               if (ri.preDestroyMethods != null && ri.preDestroyMethods.size() > 0) {
                  destroyMethods.addAll(ri.preDestroyMethods);
               }

               for(String destroyMethod : destroyMethods) {
                  try {
                     Method p = objectClass.getDeclaredMethod(destroyMethod);
                     if (!p.isAccessible()) {
                        SetAccessible.on(p);
                     }

                     p.invoke(object);
                  } catch (Exception e) {
                     this.logger.error("Unable to call pre destroy method " + destroyMethod + " on " + objectClass.getName() + ". Continuing with resource destruction.", (Throwable)e);
                  }
               }

            }
         }
      }
   }

   private ResourceInfo findResourceInfo(String name) {
      for(ResourceInfo resourceInfo : this.config.facilities.resources) {
         if (resourceInfo.id.equals(name)) {
            return resourceInfo;
         }
      }

      return null;
   }

   private void doResourceDestruction(String name, String className, Object jndiObject) {
      ResourceBeforeDestroyed event = new ResourceBeforeDestroyed(jndiObject, name);
      SystemInstance.get().fireEvent(event);
      Object object = event.getReplacement() == null ? jndiObject : event.getReplacement();
      if (object instanceof ResourceAdapterReference) {
         ResourceAdapterReference resourceAdapter = (ResourceAdapterReference)object;

         try {
            this.logger.info("Stopping ResourceAdapter: " + name);
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Stopping ResourceAdapter: " + className);
            }

            if (resourceAdapter.pool != null && ExecutorService.class.isInstance(resourceAdapter.pool)) {
               ((ExecutorService)ExecutorService.class.cast(resourceAdapter.pool)).shutdownNow();
            }

            resourceAdapter.ra.stop();
         } catch (Throwable t) {
            this.logger.fatal("ResourceAdapter Shutdown Failed: " + name, t);
         }
      } else if (object instanceof ResourceAdapter) {
         ResourceAdapter resourceAdapter = (ResourceAdapter)object;

         try {
            this.logger.info("Stopping ResourceAdapter: " + name);
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Stopping ResourceAdapter: " + className);
            }

            resourceAdapter.stop();
         } catch (Throwable t) {
            this.logger.fatal("ResourceAdapter Shutdown Failed: " + name, t);
         }
      } else if (DataSourceFactory.knows(object)) {
         this.logger.info("Closing DataSource: " + name);

         try {
            DataSourceFactory.destroy(object);
         } catch (Throwable var10) {
         }
      } else if (object instanceof ConnectorReference) {
         ConnectorReference cr = (ConnectorReference)object;

         try {
            ConnectionManager cm = cr.getConnectionManager();
            if (cm != null && cm instanceof AbstractConnectionManager) {
               ((AbstractConnectionManager)cm).doStop();
            }
         } catch (Exception e) {
            this.logger.debug("Not processing resource on destroy: " + className, (Throwable)e);
         }
      } else if (DestroyableResource.class.isInstance(object)) {
         try {
            ((DestroyableResource)DestroyableResource.class.cast(object)).destroyResource();
         } catch (RuntimeException e) {
            this.logger.error(e.getMessage(), (Throwable)e);
         }
      } else if (this.logger.isDebugEnabled() && !DataSource.class.isInstance(object)) {
         this.logger.debug("Not processing resource on destroy: " + className);
      }

   }

   public ResourceInfo removeResourceInfo(String name) {
      try {
         OpenEjbConfiguration configuration = (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
         if (configuration == null) {
            throw new Exception("OpenEjbConfiguration has not been initialized");
         }

         Iterator<ResourceInfo> iterator = configuration.facilities.resources.iterator();

         while(iterator.hasNext()) {
            ResourceInfo info = (ResourceInfo)iterator.next();
            if (name.equals(info.id)) {
               iterator.remove();
               return info;
            }
         }
      } catch (Exception e) {
         this.logger.debug("Failed to purge resource on destroy: " + e.getMessage());
      }

      return null;
   }

   private static Object unwrapReference(Object object) {
      Object o = object;

      while(o != null && Reference.class.isInstance(o)) {
         try {
            o = ((Reference)Reference.class.cast(o)).getObject();
         } catch (NamingException var3) {
         }
      }

      if (o == null) {
         o = object;
      }

      return o;
   }

   public void destroyApplication(String filePath) throws UndeployException, NoSuchApplicationException {
      ReentrantLock l = lock;
      l.lock();

      try {
         AppInfo appInfo = (AppInfo)this.deployedApplications.remove(filePath);
         if (appInfo == null) {
            throw new NoSuchApplicationException(filePath);
         }

         this.destroyApplication(appInfo);
      } finally {
         l.unlock();
      }

   }

   public void destroyApplication(AppContext appContext) throws UndeployException {
      ReentrantLock l = lock;
      l.lock();

      try {
         AppInfo appInfo = (AppInfo)this.deployedApplications.remove(appContext.getId());
         if (appInfo == null) {
            throw new IllegalStateException(String.format("Cannot find AppInfo for app: %s", appContext.getId()));
         }

         this.destroyApplication(appInfo);
      } finally {
         l.unlock();
      }

   }

   public void destroyApplication(AppInfo appInfo) throws UndeployException {
      ReentrantLock l = lock;
      l.lock();

      try {
         this.deployedApplications.remove(appInfo.path);
         this.logger.info("destroyApplication.start", appInfo.path);
         Context globalContext = this.containerSystem.getJNDIContext();
         AppContext appContext = this.containerSystem.getAppContext(appInfo.appId);
         ClassLoader classLoader = appContext.getClassLoader();
         SystemInstance.get().fireEvent(new AssemblerBeforeApplicationDestroyed(appInfo, appContext));
         if (null != appContext) {
            WebBeansContext webBeansContext = appContext.getWebBeansContext();
            if (webBeansContext != null) {
               ClassLoader old = Thread.currentThread().getContextClassLoader();
               Thread.currentThread().setContextClassLoader(classLoader);

               try {
                  ServletContext context = appContext.isStandaloneModule() && appContext.getWebContexts().iterator().hasNext() ? ((WebContext)appContext.getWebContexts().iterator().next()).getServletContext() : null;
                  ((ContainerLifecycle)webBeansContext.getService(ContainerLifecycle.class)).stopApplication(context);
               } finally {
                  Thread.currentThread().setContextClassLoader(old);
               }
            }

            Map<String, Object> cb = appContext.getBindings();

            for(Map.Entry<String, Object> value : cb.entrySet()) {
               String path = (String)value.getKey();
               if (path.startsWith("global")) {
                  path = "java:" + path;
               }

               if (path.startsWith("java:global")) {
                  if (IvmContext.class.isInstance(globalContext)) {
                     ((IvmContext)IvmContext.class.cast(globalContext)).setReadOnly(false);
                  }

                  this.unbind(globalContext, path);
                  this.unbind(globalContext, "openejb/global/" + path.substring("java:".length()));
                  this.unbind(globalContext, path.substring("java:global".length()));
               }
            }

            if (appInfo.appId != null && !appInfo.appId.isEmpty() && !"openejb".equals(appInfo.appId)) {
               this.unbind(globalContext, "global/" + appInfo.appId);
               this.unbind(globalContext, appInfo.appId);
            }

            EjbResolver globalResolver = new EjbResolver((EjbResolver)null, EjbResolver.Scope.GLOBAL, new EjbJarInfo[0]);

            for(AppInfo info : this.deployedApplications.values()) {
               globalResolver.addAll(info.ejbJars);
            }

            SystemInstance.get().setComponent(EjbResolver.class, globalResolver);
            UndeployException undeployException = new UndeployException(this.messages.format("destroyApplication.failed", appInfo.path));
            WebAppBuilder webAppBuilder = (WebAppBuilder)SystemInstance.get().getComponent(WebAppBuilder.class);
            if (webAppBuilder != null && !appInfo.webAppAlone) {
               try {
                  webAppBuilder.undeployWebApps(appInfo);
               } catch (Exception e) {
                  undeployException.getCauses().add(new Exception("App: " + appInfo.path + ": " + e.getMessage(), e));
               }
            }

            List<BeanContext> deployments = new ArrayList();

            for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
               for(EnterpriseBeanInfo beanInfo : ejbJarInfo.enterpriseBeans) {
                  String deploymentId = beanInfo.ejbDeploymentId;
                  BeanContext beanContext = this.containerSystem.getBeanContext(deploymentId);
                  if (beanContext == null) {
                     undeployException.getCauses().add(new Exception("deployment not found: " + deploymentId));
                  } else {
                     deployments.add(beanContext);
                  }
               }
            }

            deployments = sort(deployments);
            Collections.reverse(deployments);

            for(BeanContext deployment : deployments) {
               String deploymentID = String.valueOf(deployment.getDeploymentID());

               try {
                  Container container = deployment.getContainer();
                  container.stop(deployment);
               } catch (Throwable t) {
                  undeployException.getCauses().add(new Exception("bean: " + deploymentID + ": " + t.getMessage(), t));
               }
            }

            for(BeanContext bean : deployments) {
               String deploymentID = String.valueOf(bean.getDeploymentID());

               try {
                  Container container = bean.getContainer();
                  container.undeploy(bean);
                  bean.setContainer((Container)null);
               } catch (Throwable t) {
                  undeployException.getCauses().add(new Exception("bean: " + deploymentID + ": " + t.getMessage(), t));
               } finally {
                  bean.setDestroyed(true);
               }
            }

            if (webAppBuilder != null && appInfo.webAppAlone) {
               try {
                  webAppBuilder.undeployWebApps(appInfo);
               } catch (Exception e) {
                  undeployException.getCauses().add(new Exception("App: " + appInfo.path + ": " + e.getMessage(), e));
               }
            }

            List<String> clientIds = new ArrayList();

            for(ClientInfo clientInfo : appInfo.clients) {
               clientIds.add(clientInfo.moduleId);

               for(String className : clientInfo.localClients) {
                  clientIds.add(className);
               }

               for(String className : clientInfo.remoteClients) {
                  clientIds.add(className);
               }
            }

            for(WebContext webContext : appContext.getWebContexts()) {
               this.containerSystem.removeWebContext(webContext);
            }

            TldScanner.forceCompleteClean(classLoader);

            for(BeanContext deployment : deployments) {
               String deploymentID = String.valueOf(deployment.getDeploymentID());

               try {
                  this.containerSystem.removeBeanContext(deployment);
               } catch (Throwable t) {
                  undeployException.getCauses().add(new Exception(deploymentID, t));
               }

               JndiBuilder.Bindings bindings = (JndiBuilder.Bindings)deployment.get(JndiBuilder.Bindings.class);
               if (bindings != null) {
                  for(String name : bindings.getBindings()) {
                     try {
                        globalContext.unbind(name);
                     } catch (Throwable t) {
                        undeployException.getCauses().add(new Exception("bean: " + deploymentID + ": " + t.getMessage(), t));
                     }
                  }
               }
            }

            AsynchronousPool pool = (AsynchronousPool)appContext.get(AsynchronousPool.class);
            if (pool != null) {
               pool.stop();
            }

            for(CommonInfoObject jar : listCommonInfoObjectsForAppInfo(appInfo)) {
               try {
                  globalContext.unbind("openejb/ValidatorFactory/" + jar.uniqueId);
                  globalContext.unbind("openejb/Validator/" + jar.uniqueId);
               } catch (NamingException e) {
                  if (EjbJarInfo.class.isInstance(jar)) {
                     undeployException.getCauses().add(new Exception("validator: " + jar.uniqueId + ": " + e.getMessage(), e));
                  }
               }
            }

            try {
               if (globalContext instanceof IvmContext) {
                  IvmContext ivmContext = (IvmContext)globalContext;
                  ivmContext.prune("openejb/Deployment");
                  ivmContext.prune("openejb/local");
                  ivmContext.prune("openejb/remote");
                  ivmContext.prune("openejb/global");
               }
            } catch (NamingException e) {
               undeployException.getCauses().add(new Exception("Unable to prune openejb/Deployments and openejb/local namespaces, this could cause future deployments to fail.", e));
            }

            deployments.clear();

            for(String clientId : clientIds) {
               try {
                  globalContext.unbind("/openejb/client/" + clientId);
               } catch (Throwable t) {
                  undeployException.getCauses().add(new Exception("client: " + clientId + ": " + t.getMessage(), t));
               }
            }

            MBeanServer server = LocalMBeanServer.get();

            for(Object objectName : appInfo.jmx.values()) {
               try {
                  ObjectName on = new ObjectName((String)objectName);
                  if (server.isRegistered(on)) {
                     server.unregisterMBean(on);
                  }

                  CreationalContext cc = (CreationalContext)this.creationalContextForAppMbeans.remove(on);
                  if (cc != null) {
                     cc.release();
                  }
               } catch (InstanceNotFoundException e) {
                  this.logger.warning("can't unregister " + objectName + " because the mbean was not found", (Throwable)e);
               } catch (MBeanRegistrationException e) {
                  this.logger.warning("can't unregister " + objectName, (Throwable)e);
               } catch (MalformedObjectNameException mone) {
                  this.logger.warning("can't unregister because the ObjectName is malformed: " + objectName, (Throwable)mone);
               }
            }

            for(PersistenceUnitInfo unitInfo : appInfo.persistenceUnits) {
               try {
                  Object object = globalContext.lookup("openejb/PersistenceUnit/" + unitInfo.id);
                  globalContext.unbind("openejb/PersistenceUnit/" + unitInfo.id);
                  ReloadableEntityManagerFactory remf = (ReloadableEntityManagerFactory)object;
                  remf.close();
                  this.persistenceClassLoaderHandler.destroy(unitInfo.id);
                  remf.unregister();
               } catch (Throwable t) {
                  undeployException.getCauses().add(new Exception("persistence-unit: " + unitInfo.id + ": " + t.getMessage(), t));
               }
            }

            for(String id : appInfo.resourceAliases) {
               String name = "openejb/Resource/" + id;
               ContextualJndiReference.followReference.set(false);

               try {
                  Object object;
                  try {
                     object = globalContext.lookup(name);
                  } finally {
                     ContextualJndiReference.followReference.remove();
                  }

                  if (object instanceof ContextualJndiReference) {
                     ContextualJndiReference contextualJndiReference = (ContextualJndiReference)ContextualJndiReference.class.cast(object);
                     contextualJndiReference.removePrefix(appContext.getId());
                     if (contextualJndiReference.hasNoMorePrefix()) {
                        globalContext.unbind(name);
                     }
                  } else {
                     globalContext.unbind(name);
                  }
               } catch (NamingException var128) {
                  this.logger.warning("can't unbind resource '{0}'", id);
               }
            }

            for(String id : appInfo.resourceIds) {
               String name = "openejb/Resource/" + id;

               try {
                  this.destroyLookedUpResource(globalContext, id, name);
               } catch (NamingException var126) {
                  this.logger.warning("can't unbind resource '{0}'", id);
               }
            }

            for(ConnectorInfo connector : appInfo.connectors) {
               if (connector.resourceAdapter != null && connector.resourceAdapter.id != null) {
                  String name = "openejb/Resource/" + connector.resourceAdapter.id;

                  try {
                     this.destroyLookedUpResource(globalContext, connector.resourceAdapter.id, name);
                  } catch (NamingException var125) {
                     this.logger.warning("can't unbind resource '{0}'", connector);
                  }

                  for(ResourceInfo outbound : connector.outbound) {
                     try {
                        this.destroyLookedUpResource(globalContext, outbound.id, "openejb/Resource/" + outbound.id);
                     } catch (Exception var124) {
                     }
                  }

                  for(ResourceInfo outbound : connector.adminObject) {
                     try {
                        this.destroyLookedUpResource(globalContext, outbound.id, "openejb/Resource/" + outbound.id);
                     } catch (Exception var123) {
                     }
                  }

                  for(MdbContainerInfo container : connector.inbound) {
                     try {
                        this.containerSystem.removeContainer(container.id);
                        this.config.containerSystem.containers.remove(container);
                        this.containerSystem.getJNDIContext().unbind("openejb/" + container.service + "/" + container.id);
                     } catch (Exception var122) {
                     }
                  }
               }
            }

            for(String id : appInfo.containerIds) {
               this.removeContainer(id);
            }

            this.containerSystem.removeAppContext(appInfo.appId);
            if (!appInfo.properties.containsKey("tomee.destroying")) {
               try {
                  Method m = classLoader.getClass().getMethod("internalStop");
                  m.invoke(classLoader);
               } catch (NoSuchMethodException var120) {
               } catch (Exception e) {
                  this.logger.error("error stopping classloader of webapp " + appInfo.appId, (Throwable)e);
               }

               ClassLoaderUtil.cleanOpenJPACache(classLoader);
            }

            ClassLoaderUtil.destroyClassLoader(appInfo.appId, appInfo.path);
            if (undeployException.getCauses().size() > 0) {
               throw undeployException;
            }

            this.logger.debug("destroyApplication.success", appInfo.path);
            return;
         }

         this.logger.warning("Application id '" + appInfo.appId + "' not found in: " + Arrays.toString(this.containerSystem.getAppContextKeys()));
      } finally {
         l.unlock();
      }

   }

   private void destroyLookedUpResource(Context globalContext, String id, String name) throws NamingException {
      Object object;
      try {
         object = globalContext.lookup(name);
      } catch (NamingException e) {
         String ctx = name.substring(0, name.lastIndexOf("/"));
         String objName = name.substring(ctx.length() + 1);
         NamingEnumeration<Binding> bindings = globalContext.listBindings(ctx);

         while(bindings.hasMoreElements()) {
            Binding binding = (Binding)bindings.nextElement();
            if (binding.getName().equals(objName) && LazyObjectReference.class.isInstance(binding.getObject())) {
               LazyObjectReference<?> ref = (LazyObjectReference)LazyObjectReference.class.cast(binding.getObject());
               if (!ref.isInitialized()) {
                  globalContext.unbind(name);
                  this.removeResourceInfo(name);
                  return;
               }
            }
         }

         throw e;
      }

      String clazz;
      if (object == null) {
         clazz = "?";
      } else {
         clazz = object.getClass().getName();
      }

      this.destroyResource(id, clazz, object);
      globalContext.unbind(name);
   }

   private void unbind(Context context, String name) {
      try {
         context.unbind(name);
      } catch (NamingException var4) {
      }

   }

   public ClassLoader createAppClassLoader(AppInfo appInfo) throws OpenEJBException, IOException {
      if ("openejb".equals(appInfo.appId)) {
         return ParentClassLoaderFinder.Helper.get();
      } else {
         Set<URL> jars = new HashSet();

         for(EjbJarInfo info : appInfo.ejbJars) {
            if (info.path != null) {
               jars.add(this.toUrl(info.path));
            }
         }

         for(ClientInfo info : appInfo.clients) {
            if (info.path != null) {
               jars.add(this.toUrl(info.path));
            }
         }

         for(ConnectorInfo info : appInfo.connectors) {
            for(String jarPath : info.libs) {
               jars.add(this.toUrl(jarPath));
            }
         }

         for(String jarPath : appInfo.libs) {
            jars.add(this.toUrl(jarPath));
         }

         if (appInfo.libs.size() > 0) {
            try {
               File jpaIntegrationFile = JarLocation.jarLocation(MakeTxLookup.class);
               URL url = jpaIntegrationFile.toURI().toURL();
               if (!jars.contains(url)) {
                  jars.add(url);
               }
            } catch (RuntimeException var16) {
               this.logger.warning("Unable to find the open-jpa-integration jar");
            }
         }

         ClassLoaderEnricher component = (ClassLoaderEnricher)SystemInstance.get().getComponent(ClassLoaderEnricher.class);
         if (component != null) {
            jars.addAll(Arrays.asList(component.applicationEnrichment()));
         } else {
            this.logger.warning("Unable to find open-jpa-integration jar");
         }

         ClassLoader parent = ParentClassLoaderFinder.Helper.get();
         String prefix;
         if (appInfo.webAppAlone) {
            prefix = "WEB-INF/";
         } else {
            prefix = "META-INF/";
         }

         ClassLoaderConfigurer configurer1 = QuickJarsTxtParser.parse(new File(appInfo.path, prefix + "jars.txt"));
         ClassLoaderConfigurer configurer2 = ClassLoaderUtil.configurer(appInfo.appId);
         if (configurer1 != null || configurer2 != null) {
            ClassLoaderConfigurer configurer = new CompositeClassLoaderConfigurer(new ClassLoaderConfigurer[]{configurer1, configurer2});
            ClassLoaderConfigurer.Helper.configure(jars, configurer);
         }

         URL[] filtered = (URL[])jars.toArray(new URL[jars.size()]);
         if (this.skipLoaderIfPossible) {
            if ("classpath.ear".equals(appInfo.appId)) {
               return parent;
            }

            Collection<File> urls = new HashSet();

            for(URL url : ClassLoaders.findUrls(parent)) {
               try {
                  urls.add(URLs.toFile(url).getCanonicalFile());
               } catch (Exception error) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Can't determine url for: " + url.toExternalForm(), (Throwable)error);
                  }
               }
            }

            boolean allIsIntheClasspath = true;

            for(URL url : filtered) {
               try {
                  if (!urls.contains(URLs.toFile(url).getCanonicalFile())) {
                     allIsIntheClasspath = false;
                     if (this.logger.isDebugEnabled()) {
                        this.logger.debug(url.toExternalForm() + " (" + URLs.toFile(url) + ") is not in the classloader so we'll create a dedicated classloader for this app");
                     }
                     break;
                  }
               } catch (Exception ignored) {
                  allIsIntheClasspath = false;
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug(url.toExternalForm() + " (" + URLs.toFile(url) + ") is not in the classloader", (Throwable)ignored);
                  }
                  break;
               }
            }

            if (allIsIntheClasspath) {
               this.logger.info("Not creating another application classloader for " + appInfo.appId);
               return parent;
            }

            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Logging all urls from the app since we don't skip the app classloader creation:");

               for(URL url : filtered) {
                  this.logger.debug(" -> " + url.toExternalForm());
               }

               this.logger.debug("Logging all urls from the classloader since we don't skip the app classloader creation:");

               for(File url : urls) {
                  this.logger.debug(" -> " + url.getAbsolutePath());
               }
            }
         }

         this.logger.info("Creating dedicated application classloader for " + appInfo.appId);
         if (!appInfo.delegateFirst) {
            return ClassLoaderUtil.createClassLoader(appInfo.path, filtered, parent);
         } else {
            return ClassLoaderUtil.createClassLoaderFirst(appInfo.path, filtered, parent);
         }
      }
   }

   public void createExternalContext(JndiContextInfo contextInfo) throws OpenEJBException {
      this.logger.getChildLogger("service").info("createService", contextInfo.service, contextInfo.id, contextInfo.className);

      InitialContext initialContext;
      try {
         initialContext = new InitialContext(contextInfo.properties);
      } catch (NamingException ne) {
         throw new OpenEJBException(String.format("JndiProvider(id=\"%s\") could not be created.  Failed to create the InitialContext using the supplied properties", contextInfo.id), ne);
      }

      try {
         this.containerSystem.getJNDIContext().bind("openejb/remote_jndi_contexts/" + contextInfo.id, initialContext);
      } catch (NamingException e) {
         throw new OpenEJBException("Cannot bind " + contextInfo.service + " with id " + contextInfo.id, e);
      }

      this.config.facilities.remoteJndiContexts.add(contextInfo);
      this.logger.getChildLogger("service").debug("createService.success", contextInfo.service, contextInfo.id, contextInfo.className);
   }

   public void createContainer(ContainerInfo serviceInfo) throws OpenEJBException {
      ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
      serviceRecipe.setProperty("id", serviceInfo.id);
      serviceRecipe.setProperty("transactionManager", this.props.get(TransactionManager.class.getName()));
      serviceRecipe.setProperty("securityService", this.props.get(SecurityService.class.getName()));
      serviceRecipe.setProperty("properties", new UnsetPropertiesRecipe());
      this.replaceResourceAdapterProperty(serviceRecipe);
      Object service = serviceRecipe.create();
      serviceRecipe.getUnsetProperties().remove("id");
      serviceRecipe.getUnsetProperties().remove("securityService");
      logUnusedProperties(serviceRecipe, serviceInfo);
      Class interfce = (Class)serviceInterfaces.get(serviceInfo.service);
      checkImplementation(interfce, service.getClass(), serviceInfo.service, serviceInfo.id);
      this.bindService(serviceInfo, service);
      this.setSystemInstanceComponent(interfce, service);
      this.props.put(interfce.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.containerSystem.addContainer(serviceInfo.id, (Container)service);
      this.config.containerSystem.containers.add(serviceInfo);
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
      if (Container.class.isInstance(service) && LocalMBeanServer.isJMXActive()) {
         ObjectName objectName = ObjectNameBuilder.uniqueName("containers", serviceInfo.id, service);

         try {
            LocalMBeanServer.get().registerMBean(new DynamicMBeanWrapper(new JMXContainer(serviceInfo, (Container)service)), objectName);
            this.containerObjectNames.add(objectName);
         } catch (NoClassDefFoundError | Exception var7) {
         }
      }

   }

   private void bindService(ServiceInfo serviceInfo, Object service) throws OpenEJBException {
      try {
         this.containerSystem.getJNDIContext().bind("openejb/" + serviceInfo.service + "/" + serviceInfo.id, service);
      } catch (NamingException e) {
         throw new OpenEJBException(this.messages.format("assembler.cannotBindServiceWithId", serviceInfo.service, serviceInfo.id), e);
      }
   }

   public void removeContainer(String containerId) {
      this.containerSystem.removeContainer(containerId);
      Iterator<ContainerInfo> iterator = this.config.containerSystem.containers.iterator();

      while(iterator.hasNext()) {
         ContainerInfo containerInfo = (ContainerInfo)iterator.next();
         if (containerInfo.id.equals(containerId)) {
            iterator.remove();

            try {
               this.containerSystem.getJNDIContext().unbind("openejb/" + containerInfo.service + "/" + containerInfo.id);
            } catch (Exception var5) {
               this.logger.error("removeContainer.unbindFailed", containerId);
            }
         }
      }

   }

   public void createService(ServiceInfo serviceInfo) throws OpenEJBException {
      ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
      serviceRecipe.setProperty("properties", new UnsetPropertiesRecipe());
      Object service = serviceRecipe.create();
      SystemInstance.get().addObserver(service);
      logUnusedProperties(serviceRecipe, serviceInfo);
      Class<?> serviceClass = service.getClass();
      getContext().put(serviceClass.getName(), service);
      this.props.put(serviceClass.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.config.facilities.services.add(serviceInfo);
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
   }

   public void createProxyFactory(ProxyFactoryInfo serviceInfo) throws OpenEJBException {
      ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
      Object service = serviceRecipe.create();
      logUnusedProperties(serviceRecipe, serviceInfo);
      Class interfce = (Class)serviceInterfaces.get(serviceInfo.service);
      checkImplementation(interfce, service.getClass(), serviceInfo.service, serviceInfo.id);
      ProxyManager.registerFactory(serviceInfo.id, (ProxyFactory)service);
      ProxyManager.setDefaultFactory(serviceInfo.id);
      this.bindService(serviceInfo, service);
      this.setSystemInstanceComponent(interfce, service);
      getContext().put(interfce.getName(), service);
      this.props.put(interfce.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.config.facilities.intraVmServer = serviceInfo;
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
   }

   private void replaceResourceAdapterProperty(ObjectRecipe serviceRecipe) throws OpenEJBException {
      Object resourceAdapterId = serviceRecipe.getProperty("ResourceAdapter");
      if (resourceAdapterId instanceof String) {
         String id = (String)resourceAdapterId;
         id = id.trim();
         Object resourceAdapter = null;

         try {
            resourceAdapter = this.containerSystem.getJNDIContext().lookup("openejb/Resource/" + id);
         } catch (NamingException var7) {
         }

         if (Reference.class.isInstance(resourceAdapter)) {
            try {
               resourceAdapter = ((Reference)Reference.class.cast(resourceAdapter)).getContent();
            } catch (NamingException var6) {
            }
         }

         if (resourceAdapter == null) {
            throw new OpenEJBException("No existing resource adapter defined with id '" + id + "'.");
         }

         if (!(resourceAdapter instanceof ResourceAdapter)) {
            throw new OpenEJBException(this.messages.format("assembler.resourceAdapterNotResourceAdapter", id, resourceAdapter.getClass()));
         }

         serviceRecipe.setProperty("ResourceAdapter", resourceAdapter);
      }

   }

   /** @deprecated */
   @Deprecated
   public void createResource(ResourceInfo serviceInfo) throws OpenEJBException {
      this.createResource((Collection)null, serviceInfo);
   }

   public void createResource(Collection<ServiceInfo> infos, ResourceInfo serviceInfo) throws OpenEJBException {
      boolean usesCdiPwdCipher = this.usesCdiPwdCipher(serviceInfo);
      Object service = !"true".equalsIgnoreCase(String.valueOf(serviceInfo.properties.remove("Lazy"))) && !usesCdiPwdCipher ? this.doCreateResource(infos, serviceInfo) : this.newLazyResource(infos, serviceInfo);
      if (usesCdiPwdCipher && !serviceInfo.properties.contains("InitializeAfterDeployment")) {
         serviceInfo.properties.put("InitializeAfterDeployment", "true");
      }

      this.bindResource(serviceInfo.id, service, false);

      for(String alias : serviceInfo.aliases) {
         this.bindResource(alias, service, false);
      }

      if (serviceInfo.originAppName != null && !serviceInfo.originAppName.isEmpty() && !"/".equals(serviceInfo.originAppName) && !serviceInfo.id.startsWith("global")) {
         String baseJndiName = serviceInfo.id.substring(serviceInfo.originAppName.length() + 1);
         serviceInfo.aliases.add(baseJndiName);
         ContextualJndiReference ref = new ContextualJndiReference(baseJndiName);
         ref.addPrefix(serviceInfo.originAppName);
         this.bindResource(baseJndiName, ref, false);
      }

      this.config.facilities.resources.add(serviceInfo);
      if (this.logger.isDebugEnabled()) {
         this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
      }

   }

   private boolean usesCdiPwdCipher(ResourceInfo serviceInfo) {
      for(Object val : serviceInfo.properties.values()) {
         if (String.valueOf(val).startsWith("cipher:cdi:")) {
            return true;
         }
      }

      return false;
   }

   private LazyResource newLazyResource(final Collection<ServiceInfo> infos, final ResourceInfo serviceInfo) {
      return new LazyResource(new Callable<Object>() {
         public Object call() throws Exception {
            boolean appClassLoader = "true".equals(serviceInfo.properties.remove("UseAppClassLoader")) || serviceInfo.originAppName != null;
            Thread thread = Thread.currentThread();
            ClassLoader old = thread.getContextClassLoader();
            if (!appClassLoader) {
               ClassLoader classLoader = Assembler.class.getClassLoader();
               thread.setContextClassLoader(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader);
            }

            Object var8;
            try {
               var8 = Assembler.this.doCreateResource(infos, serviceInfo);
            } finally {
               thread.setContextClassLoader(old);
            }

            return var8;
         }
      });
   }

   private Object doCreateResource(Collection<ServiceInfo> infos, ResourceInfo serviceInfo) throws OpenEJBException {
      String skipPropertiesFallback = (String)serviceInfo.properties.remove("SkipPropertiesFallback");
      final ObjectRecipe serviceRecipe = this.createRecipe(infos, serviceInfo);
      boolean properties = PropertiesFactory.class.getName().equals(serviceInfo.className);
      if ("false".equalsIgnoreCase(serviceInfo.properties.getProperty("SkipImplicitAttributes", "false")) && !properties) {
         serviceRecipe.setProperty("transactionManager", this.transactionManager);
         serviceRecipe.setProperty("ServiceId", serviceInfo.id);
      }

      serviceInfo.properties.remove("SkipImplicitAttributes");
      final AtomicReference<Properties> injectedProperties = new AtomicReference();
      if (!"true".equalsIgnoreCase(skipPropertiesFallback)) {
         serviceRecipe.setProperty("properties", new UnsetPropertiesRecipe() {
            protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
               final Map<String, Object> original = serviceRecipe.getUnsetProperties();
               Properties properties = (new SuperProperties() {
                  public Object remove(Object key) {
                     original.remove(key);
                     return super.remove(key);
                  }
               }).caseInsensitive(true);

               for(Map.Entry<String, Object> entry : original.entrySet()) {
                  properties.put(entry.getKey(), entry.getValue());
               }

               injectedProperties.set(properties);
               return properties;
            }
         });
      } else {
         final Map<String, Object> unsetProperties = serviceRecipe.getUnsetProperties();
         injectedProperties.set(new Properties() {
            public String getProperty(String key) {
               Object obj = unsetProperties.get(key);
               return String.class.isInstance(obj) ? String.valueOf(obj) : null;
            }

            public Set<String> stringPropertyNames() {
               return unsetProperties.keySet();
            }

            public Set<Object> keySet() {
               return (Set)Set.class.cast(unsetProperties.keySet());
            }

            public synchronized boolean containsKey(Object key) {
               return this.getProperty(String.valueOf(key)) != null;
            }
         });
      }

      if (serviceInfo.types.contains("DataSource") || serviceInfo.types.contains(DataSource.class.getName())) {
         Properties props = PropertyPlaceHolderHelper.simpleHolds(serviceInfo.properties);
         if (serviceInfo.properties.containsKey("Definition")) {
            Object encoding = serviceInfo.properties.remove("DefinitionEncoding");

            try {
               InputStream is = new ByteArrayInputStream(serviceInfo.properties.getProperty("Definition").getBytes(encoding != null ? encoding.toString() : "ISO-8859-1"));
               Properties p = new SuperProperties();
               IO.readProperties(is, p);

               for(Map.Entry<Object, Object> entry : p.entrySet()) {
                  String key = entry.getKey().toString();
                  if (!props.containsKey(key) && (!key.equalsIgnoreCase("url") || !props.containsKey("JdbcUrl"))) {
                     props.put(key, entry.getValue());
                  }
               }
            } catch (Exception var25) {
            }
         }

         serviceRecipe.setProperty("Definition", PropertiesHelper.propertiesToString(props));
      }

      this.replaceResourceAdapterProperty(serviceRecipe);
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      boolean customLoader = false;

      try {
         if (serviceInfo.classpath != null && serviceInfo.classpath.length > 0) {
            URL[] urls = new URL[serviceInfo.classpath.length];

            for(int i = 0; i < serviceInfo.classpath.length; ++i) {
               urls[i] = serviceInfo.classpath[i].toURL();
            }

            loader = new URLClassLoaderFirst(urls, loader);
            customLoader = true;
            serviceRecipe.setProperty("OpenEJBResourceClasspath", "true");
         }
      } catch (MalformedURLException e) {
         throw new OpenEJBException("Unable to create a classloader for " + serviceInfo.id, e);
      }

      if (!customLoader && serviceInfo.classpathAPI != null) {
         throw new IllegalArgumentException("custom-api provided but not classpath used for " + serviceInfo.id);
      } else {
         Object service = serviceRecipe.create(loader);
         if (customLoader) {
            Collection<Class<?>> apis;
            if (serviceInfo.classpathAPI == null) {
               apis = new ArrayList(Arrays.asList(service.getClass().getInterfaces()));
            } else {
               String[] split = serviceInfo.classpathAPI.split(" *, *");
               apis = new ArrayList(split.length);
               ClassLoader apiLoader = Thread.currentThread().getContextClassLoader();

               for(String fqn : split) {
                  try {
                     apis.add(apiLoader.loadClass(fqn));
                  } catch (ClassNotFoundException e) {
                     throw new IllegalArgumentException(fqn + " not usable as API for " + serviceInfo.id, e);
                  }
               }
            }

            if (apis.size() - (apis.contains(Serializable.class) ? 1 : 0) - (apis.contains(Externalizable.class) ? 1 : 0) > 0) {
               service = Proxy.newProxyInstance(loader, (Class[])apis.toArray(new Class[apis.size()]), new ClassLoaderAwareHandler((String)null, service, loader));
            }
         }

         serviceInfo.unsetProperties = (Properties)injectedProperties.get();
         if (service instanceof ResourceAdapter) {
            ResourceAdapter resourceAdapter = (ResourceAdapter)service;
            int threadPoolSize = this.getIntProperty(serviceInfo.properties, "threadPoolSize", 30);
            Executor threadPool;
            if (threadPoolSize <= 0) {
               this.logger.warning("Thread pool for '" + serviceInfo.id + "' is (unbounded), consider setting a size using: " + serviceInfo.id + ".QueueSize=[size]");
               threadPool = Executors.newCachedThreadPool(new DaemonThreadFactory(new Object[]{serviceInfo.id + "-worker-"}));
            } else {
               threadPool = (new ExecutorBuilder()).size(threadPoolSize).prefix(serviceInfo.id).threadFactory(new DaemonThreadFactory(new Object[]{serviceInfo.id + "-worker-"})).build(new Options(serviceInfo.properties, SystemInstance.get().getOptions()));
               this.logger.info("Thread pool size for '" + serviceInfo.id + "' is (" + threadPoolSize + ")");
            }

            WorkManager workManager;
            if (GeronimoTransactionManager.class.isInstance(this.transactionManager)) {
               GeronimoTransactionManager geronimoTransactionManager = (GeronimoTransactionManager)this.transactionManager;
               TransactionContextHandler txWorkContextHandler = new TransactionContextHandler(geronimoTransactionManager);
               String securityRealmName = this.getStringProperty(serviceInfo.properties, "realm", serviceInfo.id);
               SecurityContextHandler securityContextHandler = new SecurityContextHandler(securityRealmName);
               HintsContextHandler hintsContextHandler = new HintsContextHandler();
               Collection<WorkContextHandler> workContextHandlers = new ArrayList();
               workContextHandlers.add(txWorkContextHandler);
               workContextHandlers.add(securityContextHandler);
               workContextHandlers.add(hintsContextHandler);
               workManager = new GeronimoWorkManager(threadPool, threadPool, threadPool, workContextHandlers);
            } else {
               workManager = new SimpleWorkManager(threadPool);
            }

            BootstrapContext bootstrapContext;
            if (this.transactionManager instanceof GeronimoTransactionManager) {
               bootstrapContext = new GeronimoBootstrapContext((GeronimoWorkManager)GeronimoWorkManager.class.cast(workManager), (GeronimoTransactionManager)this.transactionManager, (GeronimoTransactionManager)this.transactionManager);
            } else if (this.transactionManager instanceof XATerminator) {
               bootstrapContext = new SimpleBootstrapContext(workManager, (XATerminator)this.transactionManager);
            } else {
               bootstrapContext = new SimpleBootstrapContext(workManager);
            }

            try {
               this.logger.debug("createResource.startingResourceAdapter", serviceInfo.id, service.getClass().getName());
               resourceAdapter.start(bootstrapContext);
            } catch (ResourceAdapterInternalException e) {
               throw new OpenEJBException(e);
            }

            Map<String, Object> unset = serviceRecipe.getUnsetProperties();
            unset.remove("threadPoolSize");
            logUnusedProperties((Map)unset, serviceInfo);
            service = new ResourceAdapterReference(resourceAdapter, threadPool, "openejb/Resource/" + serviceInfo.id);
         } else if (service instanceof ManagedConnectionFactory) {
            ManagedConnectionFactory managedConnectionFactory = (ManagedConnectionFactory)service;
            ObjectRecipe connectionManagerRecipe = new ObjectRecipe(GeronimoConnectionManagerFactory.class, "create");
            connectionManagerRecipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
            connectionManagerRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
            connectionManagerRecipe.setAllProperties(serviceInfo.properties);
            connectionManagerRecipe.setProperty("name", serviceInfo.id);
            connectionManagerRecipe.setProperty("mcf", managedConnectionFactory);
            connectionManagerRecipe.setProperty("transactionManager", this.transactionManager);
            ClassLoader classLoader = loader;
            if (loader == null) {
               classLoader = this.getClass().getClassLoader();
            }

            if (classLoader == null) {
               classLoader = ClassLoader.getSystemClassLoader();
            }

            connectionManagerRecipe.setProperty("classLoader", classLoader);
            this.logger.getChildLogger("service").info("createResource.createConnectionManager", serviceInfo.id, service.getClass().getName());
            ConnectionManager connectionManager = (ConnectionManager)connectionManagerRecipe.create();
            if (connectionManager == null) {
               throw new OpenEJBRuntimeException(this.messages.format("assembler.invalidConnectionManager", serviceInfo.id));
            }

            Map<String, Object> unsetA = serviceRecipe.getUnsetProperties();
            Map<String, Object> unsetB = connectionManagerRecipe.getUnsetProperties();
            Map<String, Object> unset = new HashMap();

            for(Map.Entry<String, Object> entry : unsetA.entrySet()) {
               if (unsetB.containsKey(entry.getKey())) {
                  unset.put(entry.getKey(), entry.getValue());
               }
            }

            service = new ConnectorReference(connectionManager, managedConnectionFactory);
            Object eagerInit = unset.remove("eagerInit");
            if (eagerInit != null && eagerInit instanceof String && "true".equalsIgnoreCase((String)eagerInit) && connectionManager instanceof AbstractConnectionManager) {
               try {
                  ((AbstractConnectionManager)connectionManager).doStart();

                  try {
                     Object cf = managedConnectionFactory.createConnectionFactory(connectionManager);
                     if (cf instanceof ConnectionFactory) {
                        Connection connection = ((ConnectionFactory)cf).getConnection();
                        connection.getMetaData();
                        connection.close();
                     }
                  } catch (Exception var20) {
                  }
               } catch (Exception e) {
                  this.logger.warning("Can't start connection manager", (Throwable)e);
               }
            }

            logUnusedProperties((Map)unset, serviceInfo);
         } else if (service instanceof DataSource) {
            ClassLoader classLoader = loader;
            if (loader == null) {
               classLoader = this.getClass().getClassLoader();
            }

            ImportSql importer = new ImportSql(classLoader, serviceInfo.id, (DataSource)service);
            if (importer.hasSomethingToImport()) {
               importer.doImport();
            }

            ObjectRecipe recipe = DataSourceFactory.forgetRecipe(service, serviceRecipe);
            if (recipe != serviceRecipe || !serviceInfo.properties.containsKey("XaDataSource")) {
               logUnusedProperties(recipe, serviceInfo);
            }

            Properties prop = serviceInfo.properties;
            String url = prop.getProperty("JdbcUrl", prop.getProperty("url"));
            if (url == null) {
               url = prop.getProperty("jdbcUrl");
            }

            if (url == null) {
               this.logger.debug("Unable to find url for " + serviceInfo.id + " will not monitor it");
            } else {
               String host = extractHost(url);
               if (host != null) {
                  this.remoteResourceMonitor.addHost(host);
                  this.remoteResourceMonitor.registerIfNot();
               }
            }
         } else if (!Properties.class.isInstance(service) && (serviceInfo.unsetProperties == null || isTemplatizedResource(serviceInfo))) {
            logUnusedProperties(serviceRecipe, serviceInfo);
         }

         ResourceCreated event = new ResourceCreated(service, serviceInfo.id);
         SystemInstance.get().fireEvent(event);
         return event.getReplacement() == null ? service : event.getReplacement();
      }
   }

   private void bindResource(String id, Object service, boolean canReplace) throws OpenEJBException {
      String name = "openejb/Resource/" + id;
      Context jndiContext = this.containerSystem.getJNDIContext();
      Object existing = null;

      try {
         ContextualJndiReference.followReference.set(false);
         existing = jndiContext.lookup(name);
      } catch (Exception var19) {
      } finally {
         ContextualJndiReference.followReference.remove();
      }

      boolean rebind = false;
      if (existing != null) {
         boolean existingIsContextual = ContextualJndiReference.class.isInstance(existing);
         boolean serviceIsExisting = ContextualJndiReference.class.isInstance(service);
         if (!existingIsContextual && serviceIsExisting) {
            ((ContextualJndiReference)ContextualJndiReference.class.cast(service)).setDefaultValue(existing);
            rebind = true;
         } else if (existingIsContextual && !serviceIsExisting) {
            ((ContextualJndiReference)ContextualJndiReference.class.cast(existing)).setDefaultValue(service);
         } else if (existingIsContextual) {
            ContextualJndiReference contextual = (ContextualJndiReference)ContextualJndiReference.class.cast(existing);
            if (canReplace && contextual.prefixesSize() == 1) {
               contextual.removePrefix(contextual.lastPrefix());
               contextual.setDefaultValue(service);
            } else {
               contextual.addPrefix(((ContextualJndiReference)ContextualJndiReference.class.cast(service)).lastPrefix());
            }

            return;
         }
      }

      try {
         if (canReplace && existing != null) {
            jndiContext.unbind(name);
         }

         if (rebind) {
            jndiContext.rebind(name, service);
         } else {
            jndiContext.bind(name, service);
         }
      } catch (NameAlreadyBoundException var17) {
         this.logger.warning("unbounding resource " + name + " can happen because of a redeployment or because of a duplicated id");

         try {
            jndiContext.unbind(name);
            jndiContext.bind(name, service);
         } catch (NamingException e) {
            throw new OpenEJBException("Cannot bind resource adapter with id " + id, e);
         }
      } catch (NamingException e) {
         throw new OpenEJBException("Cannot bind resource adapter with id " + id, e);
      }

   }

   private static String extractHost(String url) {
      if (url != null && url.contains("://")) {
         int idx = url.indexOf("://");
         String subUrl = url.substring(idx + 3);
         int port = subUrl.indexOf(58);
         int slash = subUrl.indexOf(47);
         int end = port;
         if (port < 0 || slash > 0 && slash < port) {
            end = slash;
         }

         return end > 0 ? subUrl.substring(0, end) : subUrl;
      } else {
         return null;
      }
   }

   private int getIntProperty(Properties properties, String propertyName, int defaultValue) {
      String propertyValue = this.getStringProperty(properties, propertyName, Integer.toString(defaultValue));
      if (propertyValue == null) {
         return defaultValue;
      } else {
         try {
            return Integer.parseInt(propertyValue);
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException(propertyName + " is not an integer " + propertyValue, e);
         }
      }
   }

   private String getStringProperty(Properties properties, String propertyName, String defaultValue) {
      String propertyValue = properties.getProperty(propertyName);
      return propertyValue == null ? defaultValue : propertyValue;
   }

   public void createConnectionManager(ConnectionManagerInfo serviceInfo) throws OpenEJBException {
      ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
      Object object = this.props.get("TransactionManager");
      serviceRecipe.setProperty("transactionManager", object);
      Object service = serviceRecipe.create();
      logUnusedProperties(serviceRecipe, serviceInfo);
      Class interfce = (Class)serviceInterfaces.get(serviceInfo.service);
      checkImplementation(interfce, service.getClass(), serviceInfo.service, serviceInfo.id);
      this.bindService(serviceInfo, service);
      this.setSystemInstanceComponent(interfce, service);
      getContext().put(interfce.getName(), service);
      this.props.put(interfce.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.config.facilities.connectionManagers.add(serviceInfo);
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
   }

   public void createSecurityService(SecurityServiceInfo serviceInfo) throws OpenEJBException {
      Object service = SystemInstance.get().getComponent(SecurityService.class);
      if (service == null) {
         ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
         service = serviceRecipe.create();
         logUnusedProperties(serviceRecipe, serviceInfo);
      }

      Class interfce = (Class)serviceInterfaces.get(serviceInfo.service);
      checkImplementation(interfce, service.getClass(), serviceInfo.service, serviceInfo.id);

      try {
         this.containerSystem.getJNDIContext().bind("openejb/" + serviceInfo.service, service);
      } catch (NamingException e) {
         throw new OpenEJBException("Cannot bind " + serviceInfo.service + " with id " + serviceInfo.id, e);
      }

      this.setSystemInstanceComponent(interfce, service);
      getContext().put(interfce.getName(), service);
      this.props.put(interfce.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.securityService = (SecurityService)service;
      this.config.facilities.securityService = serviceInfo;
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
   }

   public void createTransactionManager(TransactionServiceInfo serviceInfo) throws OpenEJBException {
      Object service = SystemInstance.get().getComponent(TransactionManager.class);
      if (service == null) {
         ObjectRecipe serviceRecipe = this.createRecipe(Collections.emptyList(), serviceInfo);
         service = serviceRecipe.create();
         logUnusedProperties(serviceRecipe, serviceInfo);
      } else {
         this.logger.info("Reusing provided TransactionManager " + service);
      }

      Class interfce = (Class)serviceInterfaces.get(serviceInfo.service);
      checkImplementation(interfce, service.getClass(), serviceInfo.service, serviceInfo.id);

      try {
         this.containerSystem.getJNDIContext().bind("openejb/" + serviceInfo.service, service);
         this.containerSystem.getJNDIContext().bind("comp/UserTransaction", new CoreUserTransaction((TransactionManager)service));
         this.containerSystem.getJNDIContext().bind("comp/TransactionManager", service);
      } catch (NamingException e) {
         throw new OpenEJBException("Cannot bind " + serviceInfo.service + " with id " + serviceInfo.id, e);
      }

      this.setSystemInstanceComponent(interfce, service);
      getContext().put(interfce.getName(), service);
      this.props.put(interfce.getName(), service);
      this.props.put(serviceInfo.service, service);
      this.props.put(serviceInfo.id, service);
      this.transactionManager = (TransactionManager)service;
      this.config.facilities.transactionService = serviceInfo;
      TransactionSynchronizationRegistry synchronizationRegistry;
      if (this.transactionManager instanceof TransactionSynchronizationRegistry) {
         synchronizationRegistry = (TransactionSynchronizationRegistry)this.transactionManager;
      } else {
         synchronizationRegistry = new SimpleTransactionSynchronizationRegistry(this.transactionManager);
      }

      getContext().put(TransactionSynchronizationRegistry.class.getName(), synchronizationRegistry);
      SystemInstance.get().setComponent(TransactionSynchronizationRegistry.class, synchronizationRegistry);

      try {
         this.containerSystem.getJNDIContext().bind("comp/TransactionSynchronizationRegistry", new TransactionSynchronizationRegistryWrapper());
      } catch (NamingException e) {
         throw new OpenEJBException("Cannot bind java:comp/TransactionSynchronizationRegistry", e);
      }

      JtaEntityManagerRegistry jtaEntityManagerRegistry = new JtaEntityManagerRegistry(synchronizationRegistry);
      getContext().put(JtaEntityManagerRegistry.class.getName(), jtaEntityManagerRegistry);
      SystemInstance.get().setComponent(JtaEntityManagerRegistry.class, jtaEntityManagerRegistry);
      this.logger.getChildLogger("service").debug("createService.success", serviceInfo.service, serviceInfo.id, serviceInfo.className);
   }

   public static void logUnusedProperties(ObjectRecipe serviceRecipe, ServiceInfo info) {
      Map<String, Object> unsetProperties = serviceRecipe.getUnsetProperties();
      logUnusedProperties(unsetProperties, info);
   }

   private static void logUnusedProperties(Map<String, ?> unsetProperties, ServiceInfo info) {
      if (!isPassthroughType(info)) {
         boolean ignoreJdbcDefault = "Annotation".equalsIgnoreCase(info.properties.getProperty("Origin"));
         Logger logger = null;

         for(String property : unsetProperties.keySet()) {
            if ((!ignoreJdbcDefault || !"JdbcUrl".equals(property) && !"UserName".equals(property) && !"Password".equals(property) && !"PasswordCipher".equals(property)) && !property.equalsIgnoreCase("Definition") && !property.equalsIgnoreCase("SkipImplicitAttributes") && !property.equalsIgnoreCase("JndiName") && !property.equalsIgnoreCase("Origin") && !property.equalsIgnoreCase("DatabaseName")) {
               if (property.equalsIgnoreCase("connectionAttributes")) {
                  return;
               }

               if (property.equalsIgnoreCase("properties")) {
                  return;
               }

               if (!property.equalsIgnoreCase("ApplicationWide") && !property.equalsIgnoreCase("OpenEJBResourceClasspath") && !isInternalProperty(property) && (!info.types.isEmpty() || !"class".equalsIgnoreCase(property)) && (!"destination".equalsIgnoreCase(property) || !info.id.equals(unsetProperties.get("destination")))) {
                  if (logger == null) {
                     Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
                     if (assembler != null) {
                        logger = assembler.logger;
                     } else {
                        System.err.println("Assembler has not been initialized");
                     }
                  }

                  unusedProperty(info.id, logger, property);
               }
            }
         }

      }
   }

   private static boolean isPassthroughType(ServiceInfo info) {
      return info.types.contains("javax.mail.Session");
   }

   private static void unusedProperty(String id, Logger parentLogger, String property) {
      if (!isInternalProperty(property)) {
         String msg = "unused property '" + property + "' for resource '" + id + "'";
         if (null != parentLogger) {
            parentLogger.getChildLogger("service").warning(msg);
         } else {
            System.out.println(msg);
         }

      }
   }

   private static boolean isInternalProperty(String property) {
      return property.equalsIgnoreCase("ServiceId") || property.equalsIgnoreCase("transactionManager");
   }

   private static void unusedProperty(String id, String property) {
      Assembler component = (Assembler)SystemInstance.get().getComponent(Assembler.class);
      Logger logger = component != null ? component.logger : null;
      unusedProperty(id, logger, property);
   }

   public static ObjectRecipe prepareRecipe(ServiceInfo info) {
      String[] constructorArgs = (String[])info.constructorArgs.toArray(new String[info.constructorArgs.size()]);
      ObjectRecipe serviceRecipe = new ObjectRecipe(info.className, info.factoryMethod, constructorArgs, (Class[])null);
      serviceRecipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
      serviceRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
      serviceRecipe.allow(Option.PRIVATE_PROPERTIES);
      return serviceRecipe;
   }

   private ObjectRecipe createRecipe(Collection<ServiceInfo> services, ServiceInfo info) {
      Logger serviceLogger = this.logger.getChildLogger("service");
      if (info instanceof ResourceInfo) {
         List<String> aliasesList = ((ResourceInfo)info).aliases;
         if (!aliasesList.isEmpty()) {
            String aliases = Join.join(", ", (Collection)aliasesList);
            serviceLogger.info("createServiceWithAliases", info.service, info.id, aliases);
         } else {
            serviceLogger.info("createService", info.service, info.id);
         }
      } else {
         serviceLogger.info("createService", info.service, info.id);
      }

      ObjectRecipe serviceRecipe = prepareRecipe(info);
      Object value = info.properties.remove("SkipImplicitAttributes");
      Properties allProperties = PropertyPlaceHolderHelper.simpleHolds(info.properties);
      allProperties.remove("SkipPropertiesFallback");
      if (services == null) {
         serviceRecipe.setAllProperties(allProperties);
      } else {
         info.properties = allProperties;
         ServiceInfos.setProperties(services, info, serviceRecipe);
      }

      if (value != null) {
         info.properties.put("SkipImplicitAttributes", value);
      }

      if (serviceLogger.isDebugEnabled()) {
         for(Map.Entry<String, Object> entry : serviceRecipe.getProperties().entrySet()) {
            serviceLogger.debug("createService.props", entry.getKey(), entry.getValue());
         }
      }

      return serviceRecipe;
   }

   private void setSystemInstanceComponent(Class interfce, Object service) {
      SystemInstance.get().setComponent(interfce, service);
   }

   private URL toUrl(String jarPath) throws OpenEJBException {
      try {
         return (new File(jarPath)).toURI().toURL();
      } catch (MalformedURLException e) {
         throw new OpenEJBException(this.messages.format("cl0001", jarPath, e.getMessage()), e);
      }
   }

   static {
      JULLoggerFactory.class.getName();
      OPENEJB_URL_PKG_PREFIX = IvmContext.class.getPackage().getName();
      lock = new ReentrantLock(true);
      VALIDATOR_FACTORY_INTERFACES = new Class[]{ValidatorFactory.class, Serializable.class};
      VALIDATOR_INTERFACES = new Class[]{Validator.class};
      context = new ThreadLocal();
   }

   private static class PersistenceClassLoaderHandlerImpl implements PersistenceClassLoaderHandler {
      private static final AtomicBoolean logged = new AtomicBoolean(false);
      private final Map<String, List<ClassFileTransformer>> transformers;

      private PersistenceClassLoaderHandlerImpl() {
         this.transformers = new TreeMap();
      }

      public void addTransformer(String unitId, ClassLoader classLoader, ClassFileTransformer classFileTransformer) {
         Instrumentation instrumentation = Agent.getInstrumentation();
         if (instrumentation != null) {
            instrumentation.addTransformer(classFileTransformer);
            if (unitId != null) {
               List<ClassFileTransformer> transformers = (List)this.transformers.get(unitId);
               if (transformers == null) {
                  transformers = new ArrayList(1);
                  this.transformers.put(unitId, transformers);
               }

               transformers.add(classFileTransformer);
            }
         } else if (!logged.getAndSet(true)) {
            Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
            if (assembler != null) {
               assembler.logger.info("assembler.noAgent");
            } else {
               System.err.println("addTransformer: Assembler not initialized: JAVA AGENT NOT INSTALLED");
            }
         }

      }

      public void destroy(String unitId) {
         List<ClassFileTransformer> transformers = (List)this.transformers.remove(unitId);
         if (transformers != null) {
            Instrumentation instrumentation = Agent.getInstrumentation();
            if (instrumentation != null) {
               for(ClassFileTransformer transformer : transformers) {
                  instrumentation.removeTransformer(transformer);
               }
            } else {
               Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
               if (assembler != null) {
                  assembler.logger.info("assembler.noAgent");
               } else {
                  System.err.println("destroy: Assembler not initialized: JAVA AGENT NOT INSTALLED");
               }
            }
         }

      }

      public ClassLoader getNewTempClassLoader(ClassLoader classLoader) {
         return ClassLoaderUtil.createTempClassLoader(classLoader);
      }
   }

   public static class DeploymentListenerObserver {
      private final DeploymentListener delegate;

      public DeploymentListenerObserver(DeploymentListener deploymentListener) {
         this.delegate = deploymentListener;
      }

      public void afterApplicationCreated(@Observes AssemblerAfterApplicationCreated event) {
         this.delegate.afterApplicationCreated(event.getApp());
      }

      public void beforeApplicationDestroyed(@Observes AssemblerBeforeApplicationDestroyed event) {
         this.delegate.beforeApplicationDestroyed(event.getApp());
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof DeploymentListenerObserver)) {
            return false;
         } else {
            boolean var10000;
            label34: {
               DeploymentListenerObserver that = (DeploymentListenerObserver)o;
               if (this.delegate != null) {
                  if (this.delegate.equals(that.delegate)) {
                     break label34;
                  }
               } else if (that.delegate == null) {
                  break label34;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return this.delegate != null ? this.delegate.hashCode() : 0;
      }
   }

   private static final class DestroyingResource {
      private final String name;
      private final String clazz;
      private final Object instance;

      private DestroyingResource(String name, String clazz, Object instance) {
         this.name = name;
         this.clazz = clazz;
         this.instance = instance;
      }
   }

   public static final class ResourceAdapterReference extends Reference {
      private final transient ResourceAdapter ra;
      private final transient Executor pool;
      private final String jndi;

      public ResourceAdapterReference(ResourceAdapter ra, Executor pool, String jndi) {
         this.ra = ra;
         this.pool = pool;
         this.jndi = jndi;
      }

      public Executor getPool() {
         return this.pool;
      }

      public ResourceAdapter getRa() {
         return this.ra;
      }

      public String getJndi() {
         return this.jndi;
      }

      public Object getObject() throws NamingException {
         return this.ra;
      }

      protected Object readResolve() throws ObjectStreamException {
         try {
            ContainerSystem component = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
            if (component != null) {
               return component.getJNDIContext().lookup(this.jndi);
            } else {
               throw new NamingException("ContainerSystem has not been initialized");
            }
         } catch (NamingException e) {
            InvalidObjectException objectException = new InvalidObjectException("name not found: " + this.jndi);
            objectException.initCause(e);
            throw objectException;
         }
      }
   }

   public static class LazyResource extends LazyObjectReference<Object> {
      public LazyResource(Callable<Object> creator) {
         super(creator);
      }

      Object writeReplace() throws ObjectStreamException {
         try {
            return this.getObject();
         } catch (NamingException var2) {
            return null;
         }
      }
   }

   public static class ResourceInstance extends Reference implements Serializable, DestroyableResource {
      private final String name;
      private final Object delegate;
      private final transient Collection<Method> preDestroys;
      private final transient CreationalContext<?> context;
      private volatile boolean destroyed = false;

      public ResourceInstance(String name, Object delegate, Collection<Method> preDestroys, CreationalContext<?> context) {
         this.name = name;
         this.delegate = delegate;
         this.preDestroys = preDestroys;
         this.context = context;
      }

      public Object getObject() throws NamingException {
         return this.delegate;
      }

      public synchronized void destroyResource() {
         if (!this.destroyed) {
            Object o = Assembler.unwrapReference(this.delegate);

            for(Method m : this.preDestroys) {
               try {
                  if (!m.isAccessible()) {
                     SetAccessible.on(m);
                  }

                  m.invoke(o);
               } catch (Exception e) {
                  Assembler component = (Assembler)SystemInstance.get().getComponent(Assembler.class);
                  if (component != null) {
                     component.logger.error(e.getMessage(), (Throwable)e);
                  } else {
                     System.err.println("" + e.getMessage());
                  }
               }
            }

            try {
               if (this.context != null) {
                  this.context.release();
               }
            } catch (Exception var6) {
            }

            this.destroyed = true;
         }
      }

      Object readResolve() throws ObjectStreamException {
         try {
            ContainerSystem component = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
            if (component != null) {
               return component.getJNDIContext().lookup(this.name);
            } else {
               throw new Exception("ContainerSystem is not initialized");
            }
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      }
   }
}
