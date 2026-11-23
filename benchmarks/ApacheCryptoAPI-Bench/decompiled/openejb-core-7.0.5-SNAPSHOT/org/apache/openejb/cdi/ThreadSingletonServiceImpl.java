package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.inject.spi.DeploymentException;
import javax.transaction.Transactional;
import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.cdi.transactional.TransactionContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.AppFinder;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.classloader.MultipleClassLoader;
import org.apache.webbeans.config.OpenWebBeansConfiguration;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.corespi.se.DefaultApplicationBoundaryService;
import org.apache.webbeans.intercept.ApplicationScopedBeanInterceptorHandler;
import org.apache.webbeans.intercept.NormalScopedBeanInterceptorHandler;
import org.apache.webbeans.intercept.RequestScopedBeanInterceptorHandler;
import org.apache.webbeans.intercept.SessionScopedBeanInterceptorHandler;
import org.apache.webbeans.spi.ApplicationBoundaryService;
import org.apache.webbeans.spi.BeanArchiveService;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.spi.ConversationService;
import org.apache.webbeans.spi.JNDIService;
import org.apache.webbeans.spi.LoaderService;
import org.apache.webbeans.spi.ResourceInjectionService;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.spi.SecurityService;
import org.apache.webbeans.spi.TransactionService;
import org.apache.webbeans.spi.adaptor.ELAdaptor;

public class ThreadSingletonServiceImpl implements ThreadSingletonService {
   public static final Logger logger;
   private Object lazyInit;
   private volatile boolean cachedApplicationScoped;
   private volatile boolean cachedRequestScoped;
   private volatile boolean cachedSessionScoped;
   private static final ThreadLocal<WebBeansContext> contexts;
   private static final Map<ClassLoader, WebBeansContext> contextByClassLoader;

   public void initialize(StartupObject startupObject) {
      if (this.lazyInit == null) {
         synchronized(this) {
            if (this.lazyInit == null) {
               this.lazyInit = new Object();
               this.cachedApplicationScoped = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.cdi.applicationScope.cached", "true").trim());
               this.cachedRequestScoped = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.cdi.requestScope.cached", "true").trim());
               this.cachedSessionScoped = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.cdi.sessionScope.cached", "true").trim());
            }
         }
      }

      AppContext appContext = startupObject.getAppContext();
      appContext.setCdiEnabled(this.hasBeans(startupObject.getAppInfo()));
      Properties properties = new Properties();
      properties.setProperty("org.apache.webbeans.application.jsp", "true");
      properties.setProperty("org.apache.webbeans.spi.deployer.useEjbMetaDataDiscoveryService", "true");
      properties.setProperty("org.apache.webbeans.forceNoCheckedExceptions", "false");
      properties.setProperty(SecurityService.class.getName(), ManagedSecurityService.class.getName());
      properties.setProperty("org.apache.webbeans.conversation.Conversation.periodicDelay", "1800000");
      properties.setProperty("org.apache.webbeans.application.supportsConversation", "true");
      properties.setProperty("org.apache.webbeans.ignoredDecoratorInterfaces", "org.apache.aries.proxy.weaving.WovenProxy");
      boolean tomee = SystemInstance.get().getProperty("openejb.loader", "foo").startsWith("tomcat");
      String defaultNormalScopeHandlerClass = NormalScopedBeanInterceptorHandler.class.getName();
      properties.setProperty("org.apache.webbeans.proxy.mapping.javax.enterprise.context.ApplicationScoped", this.cachedApplicationScoped ? ApplicationScopedBeanInterceptorHandler.class.getName() : defaultNormalScopeHandlerClass);
      properties.setProperty("org.apache.webbeans.proxy.mapping.javax.enterprise.context.RequestScoped", tomee && this.cachedRequestScoped ? RequestScopedBeanInterceptorHandler.class.getName() : defaultNormalScopeHandlerClass);
      properties.setProperty("org.apache.webbeans.proxy.mapping.javax.enterprise.context.SessionScoped", tomee && this.cachedSessionScoped ? SessionScopedBeanInterceptorHandler.class.getName() : defaultNormalScopeHandlerClass);
      properties.put("org.apache.webbeans.application.supportsProducerInterception", SystemInstance.get().getProperty("openejb.cdi.producer.interception", "true"));
      properties.putAll(appContext.getProperties());
      properties.put(ContextsService.class.getName(), CdiAppContextsService.class.getName());
      properties.put(ResourceInjectionService.class.getName(), CdiResourceInjectionService.class.getName());
      properties.put(TransactionService.class.getName(), OpenEJBTransactionService.class.getName());
      Map<Class<?>, Object> services = new HashMap();
      services.put(AppContext.class, appContext);
      if (!properties.containsKey(ApplicationBoundaryService.class.getName())) {
         services.put(ApplicationBoundaryService.class, new DefaultApplicationBoundaryService());
      }

      if (!properties.containsKey(ScannerService.class.getName())) {
         services.put(ScannerService.class, new CdiScanner());
      }

      if (!properties.containsKey(JNDIService.class.getName())) {
         services.put(JNDIService.class, new OpenEJBJndiService());
      }

      if (!properties.containsKey(BeanArchiveService.class.getName())) {
         services.put(BeanArchiveService.class, new OpenEJBBeanInfoService());
      }

      if (!properties.containsKey(ELAdaptor.class.getName())) {
         try {
            services.put(ELAdaptor.class, new CustomELAdapter(appContext));
         } catch (NoClassDefFoundError var19) {
         }
      }

      if (!properties.containsKey(LoaderService.class.getName())) {
         LoaderService loaderService = (LoaderService)SystemInstance.get().getComponent(LoaderService.class);
         if (loaderService == null && !properties.containsKey(LoaderService.class.getName())) {
            services.put(LoaderService.class, new OptimizedLoaderService(appContext.getProperties()));
         } else if (loaderService != null) {
            services.put(LoaderService.class, loaderService);
         }
      }

      ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
      ClassLoader cl;
      if (oldClassLoader != ThreadSingletonServiceImpl.class.getClassLoader() && ThreadSingletonServiceImpl.class.getClassLoader() != oldClassLoader.getParent()) {
         cl = new MultipleClassLoader(oldClassLoader, ThreadSingletonServiceImpl.class.getClassLoader());
      } else {
         cl = oldClassLoader;
      }

      Thread.currentThread().setContextClassLoader(cl);
      Object old = null;

      try {
         WebBeansContext webBeansContext;
         if (startupObject.getWebContext() == null) {
            webBeansContext = new WebBeansContext(services, properties);
            appContext.set(WebBeansContext.class, webBeansContext);
         } else {
            webBeansContext = new WebappWebBeansContext(services, properties, appContext.getWebBeansContext());
            startupObject.getWebContext().setWebbeansContext(webBeansContext);
         }

         if (webBeansContext.getOpenWebBeansConfiguration().supportsConversation() && "org.apache.webbeans.jsf.DefaultConversationService".equals(webBeansContext.getOpenWebBeansConfiguration().getProperty(ConversationService.class.getName()))) {
            webBeansContext.registerService(ConversationService.class, ConversationService.class.cast(webBeansContext.getService(ContextsService.class)));
         }

         BeanManagerImpl beanManagerImpl = webBeansContext.getBeanManagerImpl();
         beanManagerImpl.addContext(new TransactionContext());
         webBeansContext.getInterceptorsManager().addInterceptorBindingType(Transactional.class, new Annotation[0]);
         SystemInstance.get().fireEvent(new WebBeansContextCreated(webBeansContext));
         old = this.contextEntered(webBeansContext);
         this.setConfiguration(webBeansContext.getOpenWebBeansConfiguration());

         try {
            ((ContainerLifecycle)webBeansContext.getService(ContainerLifecycle.class)).startApplication(startupObject);
         } catch (Exception e) {
            throw new DeploymentException("couldn't start owb context", e);
         }
      } finally {
         this.contextExited(old);
         Thread.currentThread().setContextClassLoader(oldClassLoader);
      }

   }

   private boolean hasBeans(AppInfo appInfo) {
      for(EjbJarInfo ejbJar : appInfo.ejbJars) {
         if (ejbJar.beans != null) {
            return true;
         }
      }

      return false;
   }

   private void setConfiguration(OpenWebBeansConfiguration configuration) {
      this.setProperty(configuration, SecurityService.class.getName(), ManagedSecurityService.class.getName());
      this.setProperty(configuration, "org.apache.webbeans.forceNoCheckedExceptions", "false");
      this.setProperty(configuration, "org.apache.webbeans.spi.ContainerLifecycle", OpenEJBLifecycle.class.getName());
      this.setProperty(configuration, "org.apache.webbeans.spi.TransactionService", OpenEJBTransactionService.class.getName());
      this.setProperty(configuration, "org.apache.webbeans.spi.ScannerService", CdiScanner.class.getName());
      this.setProperty(configuration, "org.apache.webbeans.spi.ContextsService", CdiAppContextsService.class.getName());
      this.setProperty(configuration, "org.apache.webbeans.spi.ValidatorService", OpenEJBValidatorService.class.getName());
      this.setProperty(configuration, ResourceInjectionService.class.getName(), CdiResourceInjectionService.class.getName());
   }

   private void setProperty(OpenWebBeansConfiguration configuration, String name, String value) {
      if (configuration.getProperty(name) == null) {
         configuration.setProperty(name, value);
      }

   }

   public Object contextEntered(WebBeansContext newOWBContext) {
      return enter(newOWBContext);
   }

   public static WebBeansContext enter(WebBeansContext newOWBContext) {
      WebBeansContext oldContext = (WebBeansContext)contexts.get();
      if (newOWBContext != null) {
         contexts.set(newOWBContext);
      } else {
         contexts.remove();
      }

      if (logger.isDebugEnabled()) {
         logger.debug("Enter:'" + newOWBContext + "'");
      }

      return oldContext;
   }

   public void contextExited(Object oldContext) {
      exit(oldContext);
   }

   public static void exit(Object oldContext) {
      if (oldContext != null && !(oldContext instanceof WebBeansContext)) {
         throw new IllegalArgumentException("ThreadSingletonServiceImpl can only be used with WebBeansContext, not " + oldContext.getClass().getName());
      } else {
         contexts.set((WebBeansContext)oldContext);
      }
   }

   private WebBeansContext getContext(ClassLoader cl) {
      return get(cl);
   }

   public static WebBeansContext get(ClassLoader cl) {
      WebBeansContext context = (WebBeansContext)contextByClassLoader.get(cl);
      if (context != null) {
         return context;
      } else {
         context = (WebBeansContext)AppFinder.findAppContextOrWeb(cl, AppFinder.WebBeansContextTransformer.INSTANCE);
         if (context == null) {
            context = (WebBeansContext)contexts.get();
            if (context == null) {
               throw new IllegalStateException("On a thread without an initialized context nor a classloader mapping a deployed app");
            }
         } else {
            contextByClassLoader.put(cl, context);
         }

         return context;
      }
   }

   public WebBeansContext get(Object key) {
      return this.getContext((ClassLoader)key);
   }

   public void clear(Object key) {
      WebBeansContext ctx = this.getContext((ClassLoader)key);
      if (logger.isDebugEnabled()) {
         logger.debug("Clearing:'" + ctx + "'");
      }

      contextByClassLoader.remove(key);
      if (ctx != null) {
         ctx.clear();
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, ThreadSingletonServiceImpl.class);
      contexts = new ThreadLocal();
      contextByClassLoader = new ConcurrentHashMap();
   }
}
