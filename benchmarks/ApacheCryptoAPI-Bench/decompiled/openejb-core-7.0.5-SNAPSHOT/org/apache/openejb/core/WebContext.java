package org.apache.openejb.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.openejb.AppContext;
import org.apache.openejb.Injection;
import org.apache.openejb.InjectionProcessor;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.cdi.ConstructorInjectionBean;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.component.InjectionTargetBean;
import org.apache.webbeans.config.WebBeansContext;

public class WebContext {
   private String id;
   private ClassLoader classLoader;
   private final Collection<Injection> injections = new ArrayList();
   private Context jndiEnc;
   private final AppContext appContext;
   private Map<String, Object> bindings;
   private final ConcurrentMap<Object, CreationalContext<?>> creationalContexts = new ConcurrentHashMap();
   private WebBeansContext webbeansContext;
   private String contextRoot;
   private String host;
   private Context initialContext;
   private ServletContext servletContext;
   private final Map<Class<?>, ConstructorInjectionBean<Object>> constructorInjectionBeanCache = new ConcurrentHashMap();

   public Context getInitialContext() {
      if (this.initialContext != null) {
         return this.initialContext;
      } else {
         try {
            this.initialContext = (Context)(new InitialContext()).lookup("java:");
         } catch (NamingException e) {
            throw new IllegalStateException(e);
         }

         return this.initialContext;
      }
   }

   public ServletContext getServletContext() {
      return this.servletContext;
   }

   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public String getHost() {
      return this.host;
   }

   public void setInitialContext(Context initialContext) {
      this.initialContext = initialContext;
   }

   public WebContext(AppContext appContext) {
      this.appContext = appContext;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public Collection<Injection> getInjections() {
      return this.injections;
   }

   public Context getJndiEnc() {
      return this.jndiEnc;
   }

   public void setJndiEnc(Context jndiEnc) {
      this.jndiEnc = jndiEnc;
   }

   public AppContext getAppContext() {
      return this.appContext;
   }

   public <T> Instance newWeakableInstance(Class<T> beanClass) throws OpenEJBException {
      WebBeansContext webBeansContext = this.getWebBeansContext();
      ConstructorInjectionBean<Object> beanDefinition = this.getConstructorInjectionBean(beanClass, webBeansContext);
      CreationalContext<Object> creationalContext;
      Object o;
      if (webBeansContext == null) {
         creationalContext = null;

         try {
            o = beanClass.newInstance();
         } catch (IllegalAccessException | InstantiationException e) {
            throw new OpenEJBException(e);
         }
      } else {
         creationalContext = webBeansContext.getBeanManagerImpl().createCreationalContext(beanDefinition);
         o = beanDefinition.create(creationalContext);
      }

      Context unwrap = InjectionProcessor.unwrap(this.getInitialContext());
      InjectionProcessor injectionProcessor = new InjectionProcessor(o, this.injections, unwrap);

      Object beanInstance;
      try {
         beanInstance = injectionProcessor.createInstance();
         if (webBeansContext != null) {
            InjectionTargetBean<Object> bean = (InjectionTargetBean)InjectionTargetBean.class.cast(beanDefinition);
            bean.getInjectionTarget().inject(beanInstance, creationalContext);
            if (this.shouldBeReleased(bean.getScope())) {
               this.creationalContexts.put(beanInstance, creationalContext);
            }
         }
      } catch (OpenEJBException oejbe) {
         if (creationalContext != null) {
            creationalContext.release();
         }

         throw oejbe;
      }

      return new Instance(beanInstance, creationalContext);
   }

   public Object newInstance(Class beanClass) throws OpenEJBException {
      return this.newWeakableInstance(beanClass).getValue();
   }

   private ConstructorInjectionBean<Object> getConstructorInjectionBean(Class beanClass, WebBeansContext webBeansContext) {
      if (webBeansContext == null) {
         return null;
      } else {
         ConstructorInjectionBean<Object> beanDefinition = (ConstructorInjectionBean)this.constructorInjectionBeanCache.get(beanClass);
         if (beanDefinition == null) {
            synchronized(this) {
               beanDefinition = (ConstructorInjectionBean)this.constructorInjectionBeanCache.get(beanClass);
               if (beanDefinition == null) {
                  AnnotatedType annotatedType = webBeansContext.getAnnotatedElementFactory().newAnnotatedType(beanClass);
                  if (isWeb(beanClass)) {
                     beanDefinition = new ConstructorInjectionBean<Object>(webBeansContext, beanClass, annotatedType, false);
                  } else {
                     beanDefinition = new ConstructorInjectionBean<Object>(webBeansContext, beanClass, annotatedType);
                  }

                  this.constructorInjectionBeanCache.put(beanClass, beanDefinition);
               }
            }
         }

         return beanDefinition;
      }
   }

   private static boolean isWeb(Class<?> beanClass) {
      if (!Servlet.class.isAssignableFrom(beanClass) && !Filter.class.isAssignableFrom(beanClass)) {
         if (!EventListener.class.isAssignableFrom(beanClass)) {
            return false;
         } else {
            return HttpSessionAttributeListener.class.isAssignableFrom(beanClass) || ServletContextListener.class.isAssignableFrom(beanClass) || ServletRequestListener.class.isAssignableFrom(beanClass) || ServletContextAttributeListener.class.isAssignableFrom(beanClass) || HttpSessionListener.class.isAssignableFrom(beanClass) || HttpSessionBindingListener.class.isAssignableFrom(beanClass) || HttpSessionActivationListener.class.isAssignableFrom(beanClass) || HttpSessionIdListener.class.isAssignableFrom(beanClass) || ServletRequestAttributeListener.class.isAssignableFrom(beanClass);
         }
      } else {
         return true;
      }
   }

   public WebBeansContext getWebBeansContext() {
      return this.webbeansContext == null ? this.getAppContext().getWebBeansContext() : this.webbeansContext;
   }

   public Object inject(Object o) throws OpenEJBException {
      try {
         WebBeansContext webBeansContext = this.getWebBeansContext();
         Context initialContext = (Context)(new InitialContext()).lookup("java:");
         Context unwrap = InjectionProcessor.unwrap(initialContext);
         InjectionProcessor injectionProcessor = new InjectionProcessor(o, this.injections, unwrap);
         Object beanInstance = injectionProcessor.createInstance();
         if (webBeansContext != null) {
            ConstructorInjectionBean<Object> beanDefinition = this.getConstructorInjectionBean(o.getClass(), webBeansContext);
            CreationalContext<Object> creationalContext = webBeansContext.getBeanManagerImpl().createCreationalContext(beanDefinition);
            InjectionTargetBean<Object> bean = (InjectionTargetBean)InjectionTargetBean.class.cast(beanDefinition);
            bean.getInjectionTarget().inject(beanInstance, creationalContext);
            if (this.shouldBeReleased(beanDefinition.getScope())) {
               this.creationalContexts.put(beanInstance, creationalContext);
            }
         }

         return beanInstance;
      } catch (OpenEJBException | NamingException e) {
         throw new OpenEJBException(e);
      }
   }

   private boolean shouldBeReleased(Class<? extends Annotation> scope) {
      return scope == null || !this.getWebBeansContext().getBeanManagerImpl().isNormalScope(scope);
   }

   public void setBindings(Map<String, Object> bindings) {
      this.bindings = bindings;
   }

   public Map<String, Object> getBindings() {
      return this.bindings;
   }

   public void setWebbeansContext(WebBeansContext webbeansContext) {
      this.webbeansContext = webbeansContext;
   }

   public WebBeansContext getWebbeansContext() {
      return this.webbeansContext;
   }

   public void setContextRoot(String contextRoot) {
      this.contextRoot = contextRoot;
   }

   public String getContextRoot() {
      return this.contextRoot;
   }

   public void destroy(Object o) {
      CreationalContext<?> ctx = (CreationalContext)this.creationalContexts.remove(o);
      if (ctx != null) {
         ctx.release();
      }

   }

   public void release() {
      for(CreationalContext<?> cc : this.creationalContexts.values()) {
         try {
            cc.release();
         } catch (RuntimeException re) {
            Logger.getInstance(LogCategory.OPENEJB, WebContext.class.getName()).warning("Can't release properly a creational context", (Throwable)re);
         }
      }

      this.creationalContexts.clear();
   }

   public static class Instance {
      private final Object value;
      private final CreationalContext<?> cc;

      public Instance(Object value, CreationalContext<?> cc) {
         this.value = value;
         this.cc = cc;
      }

      public Object getValue() {
         return this.value;
      }

      public CreationalContext<?> getCreationalContext() {
         return this.cc;
      }
   }
}
