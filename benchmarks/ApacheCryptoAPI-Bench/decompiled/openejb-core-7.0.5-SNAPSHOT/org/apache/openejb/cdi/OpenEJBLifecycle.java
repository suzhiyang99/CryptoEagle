package org.apache.openejb.cdi;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.el.ELResolver;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.component.BuiltInOwbBean;
import org.apache.webbeans.component.SimpleProducerFactory;
import org.apache.webbeans.component.WebBeansType;
import org.apache.webbeans.config.BeansDeployer;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.config.WebBeansFinder;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.el.ELContextStore;
import org.apache.webbeans.intercept.InterceptorResolutionService;
import org.apache.webbeans.portable.AbstractProducer;
import org.apache.webbeans.portable.InjectionTargetImpl;
import org.apache.webbeans.portable.ProviderBasedProducer;
import org.apache.webbeans.portable.events.discovery.BeforeShutdownImpl;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.spi.JNDIService;
import org.apache.webbeans.spi.ResourceInjectionService;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.spi.adaptor.ELAdaptor;
import org.apache.webbeans.util.WebBeansUtil;

public class OpenEJBLifecycle implements ContainerLifecycle {
   public static final ThreadLocal<AppInfo> CURRENT_APP_INFO = new ThreadLocal();
   private static final Logger logger;
   protected ScannerService scannerService;
   protected final ContextsService contextsService;
   private final BeansDeployer deployer;
   private final JNDIService jndiService;
   private final BeanManagerImpl beanManager;
   private final WebBeansContext webBeansContext;

   public OpenEJBLifecycle(WebBeansContext webBeansContext) {
      this.webBeansContext = webBeansContext;
      this.beanManager = webBeansContext.getBeanManagerImpl();
      this.deployer = new BeansDeployer(webBeansContext);
      this.jndiService = (JNDIService)webBeansContext.getService(JNDIService.class);
      this.scannerService = webBeansContext.getScannerService();
      this.contextsService = webBeansContext.getContextsService();
   }

   public BeanManager getBeanManager() {
      return this.beanManager;
   }

   public void startApplication(Object startupObject) {
      if (ServletContextEvent.class.isInstance(startupObject)) {
         this.startServletContext((ServletContext)ServletContext.class.cast(this.getServletContext(startupObject)));
      } else if (!StartupObject.class.isInstance(startupObject)) {
         logger.debug("startupObject is not of StartupObject type; ignored");
      } else {
         StartupObject stuff = (StartupObject)startupObject;
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         logger.info("OpenWebBeans Container is starting...");
         long begin = System.currentTimeMillis();

         try {
            Thread.currentThread().setContextClassLoader(stuff.getClassLoader());
            AppContext appContext = stuff.getAppContext();
            if (stuff.getWebContext() == null) {
               appContext.setWebBeansContext(this.webBeansContext);
            }

            this.webBeansContext.getPluginLoader().startUp();
            CdiPlugin cdiPlugin = (CdiPlugin)this.webBeansContext.getPluginLoader().getEjbPlugin();
            cdiPlugin.setClassLoader(stuff.getClassLoader());
            cdiPlugin.setWebBeansContext(this.webBeansContext);
            cdiPlugin.configureDeployments(stuff.getBeanContexts());
            CdiResourceInjectionService injectionService = (CdiResourceInjectionService)this.webBeansContext.getService(ResourceInjectionService.class);
            injectionService.setAppContext(stuff.getAppContext(), stuff.getBeanContexts() != null ? stuff.getBeanContexts() : Collections.emptyList());
            CdiScanner cdiScanner = null;

            try {
               logger.debug("Scanning classpaths for beans artifacts.");
               if (CdiScanner.class.isInstance(this.scannerService)) {
                  cdiScanner = (CdiScanner)CdiScanner.class.cast(this.scannerService);
                  cdiScanner.setContext(this.webBeansContext);
                  cdiScanner.init(startupObject);
               } else {
                  cdiScanner = new CdiScanner();
                  cdiScanner.setContext(this.webBeansContext);
                  cdiScanner.init(startupObject);
               }

               this.scannerService.scan();
               CURRENT_APP_INFO.set(stuff.getAppInfo());
               this.addInternalBeans();
               SystemInstance.get().fireEvent(new WebBeansContextBeforeDeploy(this.webBeansContext));
               this.deployer.deploy(this.scannerService);
               this.contextsService.init(startupObject);
            } catch (Exception e1) {
               ((Assembler)SystemInstance.get().getComponent(Assembler.class)).logger.error("CDI Beans module deployment failed", (Throwable)e1);
               throw new OpenEJBRuntimeException(e1);
            } finally {
               CURRENT_APP_INFO.remove();
            }

            ArrayList e1 = new ArrayList(stuff.getBeanContexts().size());

            for(BeanContext bc : stuff.getBeanContexts()) {
               e1.add(bc.getManagedClass());
               CdiEjbBean cdiEjbBean = (CdiEjbBean)bc.get(CdiEjbBean.class);
               if (cdiEjbBean != null) {
                  if (AbstractProducer.class.isInstance(cdiEjbBean)) {
                     ((AbstractProducer)AbstractProducer.class.cast(cdiEjbBean)).defineInterceptorStack(cdiEjbBean, cdiEjbBean.getAnnotatedType(), cdiEjbBean.getWebBeansContext());
                  }

                  bc.mergeOWBAndOpenEJBInfo();
                  bc.set(InterceptorResolutionService.BeanInterceptorInfo.class, ((InjectionTargetImpl)InjectionTargetImpl.class.cast(cdiEjbBean.getInjectionTarget())).getInterceptorInfo());
                  cdiEjbBean.initInternals();
               }
            }

            if (this.beanManager instanceof WebappBeanManager) {
               ((WebappBeanManager)this.beanManager).afterStart();
            }

            for(Class<?> clazz : cdiScanner.getStartupClasses()) {
               if (!e1.contains(clazz)) {
                  this.starts(this.beanManager, clazz);
               }
            }
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
            OptimizedLoaderService.ADDITIONAL_EXTENSIONS.remove();
         }

         logger.info("OpenWebBeans Container has started, it took {0} ms.", Long.toString(System.currentTimeMillis() - begin));
      }
   }

   private void addInternalBeans() {
      this.beanManager.getInjectionResolver().clearCaches();
      if (!hasBean(this.beanManager, HttpServletRequest.class)) {
         this.beanManager.addInternalBean(new HttpServletRequestBean(this.webBeansContext));
      }

      if (!hasBean(this.beanManager, HttpSession.class)) {
         this.beanManager.addInternalBean(new InternalBean(this.webBeansContext, HttpSession.class, HttpSession.class));
      }

      if (!hasBean(this.beanManager, ServletContext.class)) {
         this.beanManager.addInternalBean(new InternalBean(this.webBeansContext, ServletContext.class, ServletContext.class));
      }

      this.beanManager.getInjectionResolver().clearCaches();
   }

   private static boolean hasBean(BeanManagerImpl beanManagerImpl, Class<?> type) {
      return !beanManagerImpl.getInjectionResolver().implResolveByType(false, type, new Annotation[0]).isEmpty();
   }

   private void starts(BeanManager beanManager, Class<?> clazz) {
      Bean<?> bean = beanManager.resolve(beanManager.getBeans(clazz, new Annotation[0]));
      if (!beanManager.isNormalScope(bean.getScope())) {
         throw new IllegalStateException("Only normal scoped beans can use @Startup - likely @ApplicationScoped");
      } else {
         CreationalContext<Object> creationalContext = beanManager.createCreationalContext((Contextual)null);
         beanManager.getReference(bean, clazz, creationalContext).toString();
      }
   }

   public void stopApplication(Object endObject) {
      logger.debug("OpenWebBeans Container is stopping.");

      try {
         if (WebappBeanManager.class.isInstance(this.beanManager)) {
            ((WebappBeanManager)WebappBeanManager.class.cast(this.beanManager)).beforeStop();
         }

         this.webBeansContext.getContextsService().endContext(RequestScoped.class, endObject);
         this.webBeansContext.getContextsService().endContext(ConversationScoped.class, endObject);
         this.webBeansContext.getContextsService().endContext(SessionScoped.class, endObject);
         this.webBeansContext.getContextsService().endContext(ApplicationScoped.class, endObject);
         this.webBeansContext.getContextsService().endContext(Singleton.class, endObject);
         ELContextStore elStore = ELContextStore.getInstance(false);
         if (elStore != null) {
            elStore.destroyELContextStore();
         }

         this.beanManager.fireEvent(new BeforeShutdownImpl(), true, new Annotation[0]);
         this.contextsService.destroy(endObject);
         if (this.jndiService != null) {
            this.jndiService.unbind("java:comp/BeanManager");
         }

         ((CdiPlugin)this.webBeansContext.getPluginLoader().getEjbPlugin()).clearProxies();
         this.webBeansContext.getPluginLoader().shutDown();
         this.webBeansContext.getExtensionLoader().clear();
         this.beanManager.getInjectionResolver().clearCaches();
         this.webBeansContext.getAnnotatedElementFactory().clear();
         ResourceInjectionService injectionServices = (ResourceInjectionService)this.webBeansContext.getService(ResourceInjectionService.class);
         if (injectionServices != null) {
            injectionServices.clear();
         }

         ((CdiAppContextsService)CdiAppContextsService.class.cast(this.contextsService)).removeThreadLocals();
         WebBeansFinder.clearInstances(WebBeansUtil.getCurrentClassLoader());
         this.beanManager.clear();
         WebBeansFinder.clearInstances(WebBeansUtil.getCurrentClassLoader());
      } catch (Exception e) {
         logger.error("An error occured while stopping the container.", (Throwable)e);
      }

   }

   protected ScannerService getScannerService() {
      return this.scannerService;
   }

   public ContextsService getContextService() {
      return this.contextsService;
   }

   protected JNDIService getJndiService() {
      return this.jndiService;
   }

   public void initApplication(Properties properties) {
   }

   public void startServletContext(ServletContext servletContext) {
      initializeServletContext(servletContext, this.webBeansContext);
   }

   public static void initializeServletContext(ServletContext servletContext, WebBeansContext context) {
      if (context != null && context.getBeanManagerImpl().isInUse()) {
         ELAdaptor elAdaptor = (ELAdaptor)context.getService(ELAdaptor.class);
         ELResolver resolver = elAdaptor.getOwbELResolver();
         if (context.getOpenWebBeansConfiguration().isJspApplication()) {
            logger.debug("Application is configured as JSP. Adding EL Resolver.");
            setJspELFactory(servletContext, resolver);
         }

         servletContext.setAttribute(BeanManager.class.getName(), context.getBeanManagerImpl());
      }
   }

   private static void setJspELFactory(ServletContext startupObject, ELResolver resolver) {
      JspFactory factory = JspFactory.getDefaultFactory();
      if (factory == null) {
         try {
            try {
               Class.forName("org.apache.jasper.servlet.JasperInitializer");
            } catch (Throwable var4) {
               Class.forName("org.apache.jasper.compiler.JspRuntimeContext");
            }

            factory = JspFactory.getDefaultFactory();
         } catch (Exception var5) {
         }
      }

      if (factory != null) {
         JspApplicationContext applicationCtx = factory.getJspApplicationContext(startupObject);
         applicationCtx.addELResolver(resolver);
      } else {
         logger.debug("Default JSPFactroy instance has not found. Skipping OWB JSP handling");
      }

   }

   private Object getServletContext(Object object) {
      if (ServletContextEvent.class.isInstance(object)) {
         Object var2 = ((ServletContextEvent)ServletContextEvent.class.cast(object)).getServletContext();
         return var2;
      } else {
         return object;
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_CDI, OpenEJBLifecycle.class);
   }

   public static class InternalBean<T> extends BuiltInOwbBean<T> {
      private final String id;

      protected InternalBean(WebBeansContext webBeansContext, Class<T> api, Class<?> type) {
         super(webBeansContext, WebBeansType.MANAGED, api, new SimpleProducerFactory(new ProviderBasedProducer(webBeansContext, type, new OpenEJBComponentProvider(webBeansContext, type), false)));
         this.id = "openejb#container#" + api.getName();
      }

      public boolean isPassivationCapable() {
         return true;
      }

      protected String providedId() {
         return this.id;
      }

      public Class<?> proxyableType() {
         return null;
      }
   }

   public static class HttpServletRequestBean extends InternalBean<HttpServletRequest> {
      private final Set<Type> types = new HashSet();

      protected HttpServletRequestBean(WebBeansContext webBeansContext) {
         super(webBeansContext, HttpServletRequest.class, HttpServletRequest.class);
         this.types.add(HttpServletRequest.class);
         this.types.add(ServletRequest.class);
         this.types.add(Object.class);
      }

      public Set<Type> getTypes() {
         return this.types;
      }
   }

   private static class OpenEJBComponentProvider<T> implements Provider<T>, Serializable {
      private Class<?> type;
      private transient WebBeansContext webBeansContext;

      public OpenEJBComponentProvider(WebBeansContext webBeansContext, Class<?> type) {
         this.webBeansContext = webBeansContext;
         this.type = type;
      }

      public T get() {
         if (this.webBeansContext == null) {
            this.webBeansContext = WebBeansContext.currentInstance();
         }

         return (T)SystemInstance.get().getComponent(this.type);
      }

      Object readResolve() throws ObjectStreamException {
         return this.get();
      }
   }
}
