package org.apache.openejb.cdi;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.inject.spi.SessionBeanType;
import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ivm.IntraVmProxy;
import org.apache.openejb.util.proxy.ProxyManager;
import org.apache.webbeans.component.AbstractOwbBean;
import org.apache.webbeans.component.OwbBean;
import org.apache.webbeans.component.ProducerFieldBean;
import org.apache.webbeans.component.ProducerMethodBean;
import org.apache.webbeans.component.WebBeansType;
import org.apache.webbeans.component.creation.BeanAttributesBuilder;
import org.apache.webbeans.component.creation.ObserverMethodsBuilder;
import org.apache.webbeans.component.creation.ProducerFieldBeansBuilder;
import org.apache.webbeans.component.creation.ProducerMethodBeansBuilder;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.config.WebBeansFinder;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.ee.beans.UserTransactionBean;
import org.apache.webbeans.ee.beans.ValidatorBean;
import org.apache.webbeans.ee.beans.ValidatorFactoryBean;
import org.apache.webbeans.event.ObserverMethodImpl;
import org.apache.webbeans.exception.WebBeansConfigurationException;
import org.apache.webbeans.portable.events.discovery.BeforeShutdownImpl;
import org.apache.webbeans.portable.events.generics.GProcessSessionBean;
import org.apache.webbeans.proxy.NormalScopeProxyFactory;
import org.apache.webbeans.spi.ResourceInjectionService;
import org.apache.webbeans.spi.SecurityService;
import org.apache.webbeans.spi.TransactionService;
import org.apache.webbeans.spi.plugins.AbstractOwbPlugin;
import org.apache.webbeans.spi.plugins.OpenWebBeansEjbPlugin;
import org.apache.webbeans.spi.plugins.OpenWebBeansJavaEEPlugin;
import org.apache.webbeans.util.GenericsUtil;
import org.apache.webbeans.util.WebBeansUtil;

public class CdiPlugin extends AbstractOwbPlugin implements OpenWebBeansJavaEEPlugin, OpenWebBeansEjbPlugin {
   private Map<Class<?>, BeanContext> beans;
   private WebBeansContext webBeansContext;
   private ClassLoader classLoader;
   private Map<Contextual<?>, Object> cacheProxies;

   public void setWebBeansContext(WebBeansContext webBeansContext) {
      this.webBeansContext = webBeansContext;
      if (webBeansContext != null) {
         if (!WebappWebBeansContext.class.isInstance(webBeansContext)) {
            this.cacheProxies = new ConcurrentHashMap();
         } else {
            WebBeansContext parent = ((WebappWebBeansContext)WebappWebBeansContext.class.cast(webBeansContext)).getParent();
            if (parent != null) {
               this.cacheProxies = ((CdiPlugin)CdiPlugin.class.cast(parent.getPluginLoader().getEjbPlugin())).cacheProxies;
            } else {
               this.cacheProxies = new ConcurrentHashMap();
            }
         }

      }
   }

   public boolean isEEComponent(Class<?> impl) {
      return Servlet.class.isAssignableFrom(impl) || Filter.class.isAssignableFrom(impl) || ServletContextListener.class.isAssignableFrom(impl) || JspTag.class.isAssignableFrom(impl);
   }

   public void registerEEBeans() {
      BeanManagerImpl beanManagerImpl = this.webBeansContext.getBeanManagerImpl();
      beanManagerImpl.addInternalBean(new ValidatorBean(this.webBeansContext));
      beanManagerImpl.addInternalBean(new ValidatorFactoryBean(this.webBeansContext));
      beanManagerImpl.addInternalBean(new UserTransactionBean(this.webBeansContext));
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public void shutDown() {
      super.shutDown();
      if (this.beans != null) {
         this.beans.clear();
      }

   }

   public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
      return new CdiEjbBean.EJBBeanAttributesImpl<T>(findBeanContext(this.webBeansContext, type.getJavaClass()), BeanAttributesBuilder.forContext(this.webBeansContext).newBeanAttibutes(type).build());
   }

   public void configureDeployments(List<BeanContext> ejbDeployments) {
      this.beans = new WeakHashMap();

      for(BeanContext deployment : ejbDeployments) {
         if (deployment.getComponentType().isCdiCompatible()) {
            if (deployment.isLocalbean() && !deployment.isDynamicallyImplemented()) {
               this.beans.put(((BeanContext.ProxyClass)deployment.get(BeanContext.ProxyClass.class)).getProxy(), deployment);
            }

            this.beans.put(deployment.getBeanClass(), deployment);
         }
      }

   }

   public void stop() throws OpenEJBException {
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(this.classLoader);
         this.webBeansContext.getBeanManagerImpl().fireEvent(new BeforeShutdownImpl(), new Annotation[0]);
         this.webBeansContext.getContextsService().destroy((Object)null);
         this.webBeansContext.getPluginLoader().shutDown();
         this.webBeansContext.getExtensionLoader().clear();
         this.webBeansContext.getBeanManagerImpl().getInjectionResolver().clearCaches();
         this.webBeansContext.getAnnotatedElementFactory().clear();
         CdiResourceInjectionService injectionServices = (CdiResourceInjectionService)this.webBeansContext.getService(ResourceInjectionService.class);
         injectionServices.clear();
         WebBeansFinder.clearInstances(WebBeansUtil.getCurrentClassLoader());
      } catch (Exception e) {
         throw new OpenEJBException(e);
      } finally {
         Thread.currentThread().setContextClassLoader(oldCl);
      }

   }

   public <T> T getSupportedService(Class<T> serviceClass) {
      return (T)(this.supportService(serviceClass) ? serviceClass.cast(this) : null);
   }

   public void isManagedBean(Class<?> clazz) {
   }

   public boolean supportService(Class<?> serviceClass) {
      return serviceClass == TransactionService.class || serviceClass == SecurityService.class;
   }

   public Object getSessionBeanProxy(Bean<?> inBean, Class<?> interfce, CreationalContext<?> creationalContext) {
      Object instance = this.cacheProxies.get(inBean);
      if (instance != null) {
         return instance;
      } else {
         synchronized(inBean) {
            instance = this.cacheProxies.get(inBean);
            if (instance != null) {
               return instance;
            } else {
               Class<? extends Annotation> scopeClass = inBean.getScope();
               CdiEjbBean<Object> cdiEjbBean = (CdiEjbBean)inBean;
               if (scopeClass != null && Dependent.class != scopeClass) {
                  InstanceBean<Object> bean = new InstanceBean<Object>(cdiEjbBean);
                  if (this.webBeansContext.getBeanManagerImpl().isNormalScope(scopeClass)) {
                     BeanContext beanContext = cdiEjbBean.getBeanContext();
                     final Provider provider = this.webBeansContext.getNormalScopeProxyFactory().getInstanceProvider(beanContext.getClassLoader(), cdiEjbBean);
                     if (!beanContext.isLocalbean()) {
                        List<Class> interfaces = new ArrayList();
                        InterfaceType type = beanContext.getInterfaceType(interfce);
                        if (type != null) {
                           interfaces.addAll(beanContext.getInterfaces(type));
                        } else {
                           interfaces.addAll(beanContext.getInterfaces(InterfaceType.BUSINESS_LOCAL));
                        }

                        interfaces.add(Serializable.class);
                        interfaces.add(IntraVmProxy.class);
                        if (BeanType.STATEFUL.equals(beanContext.getComponentType()) || BeanType.MANAGED.equals(beanContext.getComponentType())) {
                           interfaces.add(BeanContext.Removable.class);
                        }

                        try {
                           instance = ProxyManager.newProxyInstance((Class[])interfaces.toArray(new Class[interfaces.size()]), new InvocationHandler() {
                              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                 try {
                                    return method.invoke(provider.get(), args);
                                 } catch (InvocationTargetException ite) {
                                    throw ite.getCause();
                                 }
                              }
                           });
                        } catch (IllegalAccessException e) {
                           throw new OpenEJBRuntimeException(e);
                        }
                     } else {
                        NormalScopeProxyFactory normalScopeProxyFactory = this.webBeansContext.getNormalScopeProxyFactory();
                        Class<?> proxyClass = normalScopeProxyFactory.createProxyClass(beanContext.getClassLoader(), beanContext.getBeanClass());
                        instance = normalScopeProxyFactory.createProxyInstance(proxyClass, provider);
                     }

                     this.cacheProxies.put(inBean, instance);
                  } else {
                     Context context = this.webBeansContext.getBeanManagerImpl().getContext(scopeClass);
                     instance = context.get(bean, creationalContext);
                  }

                  bean.setOwbProxy(instance);
                  return instance;
               } else {
                  return cdiEjbBean.createEjb(creationalContext);
               }
            }
         }
      }
   }

   public boolean isSessionBean(Class<?> clazz) {
      return this.beans != null && this.beans.containsKey(clazz);
   }

   public boolean isNewSessionBean(Class<?> clazz) {
      return this.isNewSessionBean(this.webBeansContext, clazz) || this.isNewSessionBean(superContext(this.webBeansContext), clazz);
   }

   public <T> Bean<T> defineSessionBean(Class<T> clazz, BeanAttributes<T> attributes, AnnotatedType<T> annotatedType) {
      BeanContext bc = findBeanContext(this.webBeansContext, clazz);
      Class<?> superClass = bc.getManagedClass().getSuperclass();
      if (annotatedType.isAnnotationPresent(Specializes.class)) {
         if (superClass != Object.class && !this.isSessionBean(superClass)) {
            throw new DefinitionException("You can only specialize another EJB: " + clazz);
         }

         BeanContext parentBc = findBeanContext(this.webBeansContext, superClass);
         List<Class> businessLocalInterfaces = new ArrayList(parentBc.getBusinessLocalInterfaces());

         for(Class<?> api : bc.getBusinessLocalInterfaces()) {
            businessLocalInterfaces.removeAll(GenericsUtil.getTypeClosure(api));
         }

         if (!businessLocalInterfaces.isEmpty()) {
            throw new DefinitionException("You can only specialize another EJB with at least the same API: " + clazz);
         }
      }

      CdiEjbBean<T> bean = (new OpenEJBBeanBuilder(bc, this.webBeansContext, annotatedType, attributes)).createBean(clazz, !annotatedType.isAnnotationPresent(Vetoed.class));
      bc.set(CdiEjbBean.class, bean);
      bc.set(CurrentCreationalContext.class, new CurrentCreationalContext());
      validateDisposeMethods(bean);
      validateScope(bean);
      Set<ObserverMethod<?>> observerMethods;
      if (bean.isEnabled()) {
         observerMethods = (new ObserverMethodsBuilder(this.webBeansContext, bean.getAnnotatedType())).defineObserverMethods(bean);
      } else {
         observerMethods = new HashSet();
      }

      WebBeansUtil webBeansUtil = this.webBeansContext.getWebBeansUtil();
      Set<ProducerFieldBean<?>> producerFields = (new ProducerFieldBeansBuilder(bean.getWebBeansContext(), bean.getAnnotatedType())).defineProducerFields(bean);
      Set<ProducerMethodBean<?>> producerMethods = (new ProducerMethodBeansBuilder(bean.getWebBeansContext(), bean.getAnnotatedType())).defineProducerMethods(bean, producerFields);
      Map<ProducerMethodBean<?>, AnnotatedMethod<?>> annotatedMethods = new HashMap();

      for(ProducerMethodBean<?> producerMethod : producerMethods) {
         AnnotatedMethod<?> method = this.webBeansContext.getAnnotatedElementFactory().newAnnotatedMethod(producerMethod.getCreatorMethod(), annotatedType);
         webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessProducer event observers for ProducerMethods. Look at logs for further details");
         annotatedMethods.put(producerMethod, method);
      }

      Map<ProducerFieldBean<?>, AnnotatedField<?>> annotatedFields = new HashMap();

      for(ProducerFieldBean<?> producerField : producerFields) {
         if (!Modifier.isStatic(producerField.getCreatorField().getModifiers())) {
            throw new DefinitionException("In an EJB all producer fields should be static");
         }

         webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessProducer event observers for ProducerFields. Look at logs for further details");
         annotatedFields.put(producerField, this.webBeansContext.getAnnotatedElementFactory().newAnnotatedField(producerField.getCreatorField(), this.webBeansContext.getAnnotatedElementFactory().newAnnotatedType(producerField.getBeanClass())));
      }

      Map<ObserverMethod<?>, AnnotatedMethod<?>> observerMethodsMap = new HashMap();

      for(ObserverMethod<?> observerMethod : observerMethods) {
         ObserverMethodImpl<?> impl = (ObserverMethodImpl)observerMethod;
         AnnotatedMethod<?> method = impl.getObserverMethod();
         observerMethodsMap.put(observerMethod, method);
      }

      validateProduceMethods(bean, producerMethods);
      validateObserverMethods(bean, observerMethodsMap);
      BeanManagerImpl beanManager = this.webBeansContext.getBeanManagerImpl();
      GProcessSessionBean event = new GProcessSessionBean((Bean)Bean.class.cast(bean), annotatedType, bc.getEjbName(), bean.getEjbType());
      beanManager.fireEvent(event, true, new Annotation[0]);
      event.setStarted();
      webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessSessionBean event observers for managed beans. Look at logs for further details");
      webBeansUtil.fireProcessProducerMethodBeanEvent(annotatedMethods, annotatedType);
      webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessProducerMethod event observers for producer method beans. Look at logs for further details");
      webBeansUtil.fireProcessProducerFieldBeanEvent(annotatedFields);
      webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessProducerField event observers for producer field beans. Look at logs for further details");
      webBeansUtil.fireProcessObservableMethodBeanEvent(observerMethodsMap);
      webBeansUtil.inspectDeploymentErrorStack("There are errors that are added by ProcessObserverMethod event observers for observer methods. Look at logs for further details");
      if (!webBeansUtil.isAnnotatedTypeDecoratorOrInterceptor(annotatedType)) {
         for(ProducerMethodBean<?> producerMethod : producerMethods) {
            beanManager.addBean(producerMethod);
         }

         for(ProducerFieldBean<?> producerField : producerFields) {
            beanManager.addBean(producerField);
         }
      }

      beanManager.addBean(bean);
      return bean;
   }

   private boolean isNewSessionBean(WebBeansContext ctx, Class<?> clazz) {
      if (ctx == null) {
         return false;
      } else {
         Map<Class<?>, BeanContext> map = pluginBeans(ctx);
         return map != null && (map.containsKey(clazz) || clazz.isInterface() && findBeanContext(ctx, clazz) != null);
      }
   }

   private static WebBeansContext superContext(WebBeansContext ctx) {
      return !WebappWebBeansContext.class.isInstance(ctx) ? null : ((WebappWebBeansContext)WebappWebBeansContext.class.cast(ctx)).getParent();
   }

   private static BeanContext findBeanContext(WebBeansContext ctx, Class<?> clazz) {
      Map<Class<?>, BeanContext> beans = pluginBeans(ctx);
      BeanContext b = (BeanContext)beans.get(clazz);
      if (b != null) {
         return b;
      } else {
         for(BeanContext bc : beans.values()) {
            if (!bc.isLocalbean()) {
               CdiEjbBean<?> cdiEjbBean = (CdiEjbBean)bc.get(CdiEjbBean.class);
               if (cdiEjbBean != null) {
                  for(Class<?> itf : cdiEjbBean.getBusinessLocalInterfaces()) {
                     if (itf.equals(clazz)) {
                        return bc;
                     }
                  }
               }
            }
         }

         WebBeansContext parentCtx = superContext(ctx);
         if (parentCtx != null) {
            return findBeanContext(parentCtx, clazz);
         } else {
            return null;
         }
      }
   }

   public <T> Bean<T> defineNewSessionBean(Class<T> clazz) {
      return new NewCdiEjbBean<T>((CdiEjbBean)findBeanContext(this.webBeansContext, clazz).get(CdiEjbBean.class));
   }

   private static Map<Class<?>, BeanContext> pluginBeans(WebBeansContext ctx) {
      return ((CdiPlugin)CdiPlugin.class.cast(ctx.getPluginLoader().getEjbPlugin())).beans;
   }

   private static void validateObserverMethods(CdiEjbBean<?> bean, Map<ObserverMethod<?>, AnnotatedMethod<?>> methods) {
      BeanContext beanContext = bean.getBeanContext();
      if (!beanContext.isLocalbean()) {
         for(Map.Entry<ObserverMethod<?>, AnnotatedMethod<?>> m : methods.entrySet()) {
            Method method = ((AnnotatedMethod)m.getValue()).getJavaMember();
            if (!Modifier.isStatic(method.getModifiers()) && doResolveViewMethod(bean, method) == null) {
               throw new WebBeansConfigurationException("@Observes " + method + " neither in the ejb view of ejb " + bean.getBeanContext().getEjbName() + " nor static");
            }
         }

      }
   }

   private static void validateProduceMethods(CdiEjbBean<?> bean, Set<ProducerMethodBean<?>> methods) {
      BeanContext beanContext = bean.getBeanContext();
      if (!beanContext.isLocalbean()) {
         for(ProducerMethodBean<?> m : methods) {
            Method method = m.getCreatorMethod();
            if (doResolveViewMethod(bean, method) == null) {
               throw new WebBeansConfigurationException("@Produces " + method + " not in the ejb view of ejb " + beanContext.getEjbName());
            }
         }

      }
   }

   private static void validateScope(CdiEjbBean<?> bean) {
      Class<? extends Annotation> scope = bean.getScope();
      BeanType beanType = bean.getBeanContext().getComponentType();
      if (BeanType.STATELESS.equals(beanType) && !Dependent.class.equals(scope)) {
         throw new WebBeansConfigurationException("@Stateless can only be @Dependent");
      } else if (BeanType.SINGLETON.equals(beanType) && !Dependent.class.equals(scope) && !ApplicationScoped.class.equals(scope)) {
         throw new WebBeansConfigurationException("@Singleton can only be @Dependent or @ApplicationScoped");
      }
   }

   private static void validateDisposeMethods(CdiEjbBean<?> bean) {
      if (!bean.getBeanContext().isLocalbean()) {
         for(Method m : bean.getBeanContext().getBeanClass().getMethods()) {
            if (!m.getDeclaringClass().equals(Object.class) && m.getParameterTypes().length > 0) {
               for(Annotation[] a : m.getParameterAnnotations()) {
                  for(Annotation ann : a) {
                     if (ann.annotationType().equals(Disposes.class) && doResolveViewMethod(bean, m) == null) {
                        throw new WebBeansConfigurationException("@Disposes is forbidden on non business EJB methods");
                     }
                  }
               }
            }
         }
      }

   }

   public boolean isSingletonBean(Class<?> clazz) {
      throw new IllegalStateException("Statement should never be reached");
   }

   public boolean isStatefulBean(Class<?> clazz) {
      return BeanType.STATEFUL.equals(((BeanContext)this.beans.get(clazz)).getComponentType());
   }

   public boolean isStatelessBean(Class<?> clazz) {
      throw new IllegalStateException("Statement should never be reached");
   }

   public static Method doResolveViewMethod(Bean<?> component, Method declaredMethod) {
      if (!(component instanceof CdiEjbBean)) {
         return declaredMethod;
      } else {
         CdiEjbBean cdiEjbBean = (CdiEjbBean)component;
         BeanContext beanContext = cdiEjbBean.getBeanContext();

         for(Class intface : beanContext.getBusinessLocalInterfaces()) {
            try {
               return intface.getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
            } catch (NoSuchMethodException var8) {
            }
         }

         for(Class intface : beanContext.getBusinessRemoteInterfaces()) {
            try {
               return intface.getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
            } catch (NoSuchMethodException var7) {
            }
         }

         return null;
      }
   }

   public Method resolveViewMethod(Bean<?> component, Method declaredMethod) {
      Method m = doResolveViewMethod(component, declaredMethod);
      return m == null ? declaredMethod : m;
   }

   public void clearProxies() {
      this.cacheProxies.clear();
   }

   private static class InstanceBean<T> implements OwbBean<T>, PassivationCapable {
      private final CdiEjbBean<T> bean;
      private T owbProxy;

      public InstanceBean(CdiEjbBean<T> tCdiEjbBean) {
         this.bean = tCdiEjbBean;
      }

      public Set<Type> getTypes() {
         return this.bean.getTypes();
      }

      public Set<Annotation> getQualifiers() {
         return this.bean.getQualifiers();
      }

      public Class<? extends Annotation> getScope() {
         return this.bean.getScope();
      }

      public String getName() {
         return this.bean.getName();
      }

      public boolean isNullable() {
         return this.bean.isNullable();
      }

      public Set<InjectionPoint> getInjectionPoints() {
         return Collections.emptySet();
      }

      public Class<?> getBeanClass() {
         return this.bean.getBeanClass();
      }

      public Set<Class<? extends Annotation>> getStereotypes() {
         return this.bean.getStereotypes();
      }

      public boolean isAlternative() {
         return this.bean.isAlternative();
      }

      public T create(CreationalContext<T> creationalContext) {
         T instance = this.bean.createEjb(creationalContext);
         if (this.owbProxy != null && SessionBeanType.STATEFUL.equals(this.bean.getEjbType())) {
            this.bean.storeStatefulInstance(this.owbProxy, instance);
         }

         return instance;
      }

      public void destroy(T instance, CreationalContext<T> cc) {
         if (SessionBeanType.STATEFUL.equals(this.bean.getEjbType())) {
            this.bean.destroy(instance, cc);
         }
      }

      public Producer<T> getProducer() {
         return new EjbProducer<T>(this, this.bean);
      }

      public WebBeansType getWebBeansType() {
         return this.bean.getWebBeansType();
      }

      public Class<T> getReturnType() {
         return this.bean.getReturnType();
      }

      public void setSpecializedBean(boolean specialized) {
      }

      public boolean isSpecializedBean() {
         return this.bean.isSpecializedBean();
      }

      public void setEnabled(boolean enabled) {
      }

      public boolean isEnabled() {
         return this.bean.isEnabled();
      }

      public String getId() {
         return this.bean.getId();
      }

      public boolean isPassivationCapable() {
         return this.bean.isPassivationCapable();
      }

      public boolean isDependent() {
         return this.bean.isDependent();
      }

      public WebBeansContext getWebBeansContext() {
         return this.bean.getWebBeansContext();
      }

      public void setOwbProxy(T owbProxy) {
         this.owbProxy = owbProxy;
      }

      public boolean equals(Object o) {
         if (AbstractOwbBean.class.isInstance(o)) {
            return this.bean.equals(o);
         } else {
            return InstanceBean.class.isInstance(o) && this.bean.equals(((InstanceBean)InstanceBean.class.cast(o)).bean);
         }
      }

      public int hashCode() {
         return this.bean.hashCode();
      }
   }

   private static class EjbProducer<T> implements Producer<T> {
      private final CdiEjbBean<T> ejb;
      private final InstanceBean<T> instance;

      public EjbProducer(InstanceBean<T> tInstanceBean, CdiEjbBean<T> bean) {
         this.instance = tInstanceBean;
         this.ejb = bean;
      }

      public T produce(CreationalContext<T> creationalContext) {
         return this.instance.create(creationalContext);
      }

      public void dispose(T instance) {
         this.ejb.destroyComponentInstance(instance);
      }

      public Set<InjectionPoint> getInjectionPoints() {
         return Collections.emptySet();
      }
   }
}
