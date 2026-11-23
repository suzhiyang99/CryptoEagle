package org.apache.openejb.cdi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.decorator.Decorator;
import javax.ejb.NoSuchEJBException;
import javax.ejb.Remove;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.SessionBeanType;
import javax.transaction.UserTransaction;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.ProxyInterfaceResolver;
import org.apache.openejb.core.ivm.BaseEjbProxyHandler;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;
import org.apache.openejb.util.proxy.ProxyManager;
import org.apache.webbeans.component.BeanAttributesImpl;
import org.apache.webbeans.component.InterceptedMarker;
import org.apache.webbeans.config.DeploymentValidationService;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.InjectionTargetFactoryImpl;
import org.apache.webbeans.context.creational.CreationalContextImpl;
import org.apache.webbeans.ejb.common.component.BaseEjbBean;
import org.apache.webbeans.intercept.InterceptorResolutionService;
import org.apache.webbeans.portable.InjectionTargetImpl;
import org.apache.webbeans.util.GenericsUtil;

public class CdiEjbBean<T> extends BaseEjbBean<T> implements InterceptedMarker, DeploymentValidationService.BeanInterceptorInfoProvider {
   private final Map<Integer, Object> dependentSFSBToBeRemoved;
   private final BeanContext beanContext;
   private final boolean isDependentAndStateful;
   private final boolean passivable;
   private BeanContext.BusinessLocalBeanHome homeLocalBean;
   private BeanContext.BusinessLocalHome home;
   private BeanContext.BusinessRemoteHome remote;

   public CdiEjbBean(BeanContext beanContext, WebBeansContext webBeansContext, AnnotatedType<T> at, BeanAttributes<T> attributes) {
      this(beanContext, webBeansContext, beanContext.getManagedClass(), at, new EjbInjectionTargetFactory(beanContext, at, webBeansContext), attributes);
      ((EjbInjectionTargetImpl)EjbInjectionTargetImpl.class.cast(this.getInjectionTarget())).setCdiEjbBean(this);
   }

   public CdiEjbBean(BeanContext bc, WebBeansContext webBeansContext, Class beanClass, AnnotatedType<T> at, InjectionTargetFactoryImpl<T> factory, BeanAttributes<T> attributes) {
      super(webBeansContext, toSessionType(bc.getComponentType()), at, new EJBBeanAttributesImpl(bc, attributes), beanClass, factory);
      this.dependentSFSBToBeRemoved = new ConcurrentHashMap();
      this.beanContext = bc;
      bc.set(Bean.class, this);
      this.passivatingId = bc.getDeploymentID() + this.getReturnType().getName();
      boolean stateful = BeanType.STATEFUL.equals(bc.getComponentType());
      boolean isDependent = this.getScope().equals(Dependent.class);
      this.isDependentAndStateful = isDependent && stateful;
      if (webBeansContext.getBeanManagerImpl().isPassivatingScope(this.getScope()) && stateful) {
         if (!this.getBeanContext().isPassivable()) {
            throw new DefinitionException(this.getBeanContext().getBeanClass() + " is a not apssivation-capable @Stateful with a scope " + this.getScope().getSimpleName() + " which need passivation");
         }

         this.passivable = true;
      } else {
         this.passivable = false;
      }

      if (!isDependent) {
         for(Type type : attributes.getTypes()) {
            if (ParameterizedType.class.isInstance(type)) {
               throw new DefinitionException("Parameterized session bean should be @Dependent: " + beanClass);
            }
         }
      }

      if (this.getAnnotatedType().isAnnotationPresent(javax.interceptor.Interceptor.class) || this.getAnnotatedType().isAnnotationPresent(Decorator.class)) {
         throw new DefinitionException("An EJB can't be an interceptor or a decorator: " + beanClass);
      }
   }

   public InterceptorResolutionService.BeanInterceptorInfo interceptorInfo() {
      return ((EjbInjectionTargetImpl)EjbInjectionTargetImpl.class.cast(this.getInjectionTarget())).getInterceptorInfo();
   }

   public boolean isPassivationCapable() {
      return this.passivable;
   }

   public BeanContext getBeanContext() {
      return this.beanContext;
   }

   private static SessionBeanType toSessionType(BeanType beanType) {
      switch (beanType) {
         case SINGLETON:
            return SessionBeanType.SINGLETON;
         case MESSAGE_DRIVEN:
         case STATELESS:
         case MANAGED:
            return SessionBeanType.STATELESS;
         case STATEFUL:
            return SessionBeanType.STATEFUL;
         default:
            throw new IllegalStateException("Unknown Session BeanType " + beanType);
      }
   }

   public String getEjbName() {
      return this.beanContext.getEjbName();
   }

   public boolean needsBeanLocalViewAddedToTypes() {
      return this.beanContext.isLocalbean() && this.beanContext.getBeanClass().getAnnotation(Typed.class) == null;
   }

   public List<Class<?>> getBusinessLocalInterfaces() {
      List<Class<?>> classes = new ArrayList();

      for(Type t : this.getTypes()) {
         if (Class.class.isInstance(t)) {
            classes.add(Class.class.cast(t));
         }
      }

      return classes;
   }

   public void destroyComponentInstance(T instance) {
      if (this.getScope() != null && Dependent.class != this.getScope()) {
         this.destroyScopedStateful(instance, (CreationalContext)null);
      } else {
         this.destroyStatefulSessionBeanInstance(instance, (Object)null);
      }

   }

   protected void destroyStatefulSessionBeanInstance(T proxyInstance, Object unused) {
      if (proxyInstance instanceof BeanContext.Removable) {
         try {
            ((BeanContext.Removable)proxyInstance).$$remove();
         } catch (NoSuchEJBException var4) {
         } catch (UndeclaredThrowableException nsoe) {
            if (!(nsoe.getCause() instanceof NoSuchObjectException)) {
               throw nsoe;
            }
         } catch (Exception e) {
            if (!(e instanceof NoSuchObjectException)) {
               if (e instanceof RuntimeException) {
                  throw (RuntimeException)e;
               }

               throw new OpenEJBRuntimeException(e);
            }
         }
      }

   }

   public List<Method> getRemoveMethods() {
      return this.beanContext.isLocalbean() ? this.findRemove(this.beanContext.getBeanClass(), this.beanContext.getBeanClass()) : this.findRemove(this.beanContext.getBeanClass(), this.beanContext.getBusinessLocalInterface());
   }

   private List<Method> findRemove(Class<?> beanClass, Class<?> beanInterface) {
      List<Method> toReturn = new ArrayList();
      Method[] methods = beanClass.getMethods();

      for(Method method : methods) {
         Remove annotation = (Remove)method.getAnnotation(Remove.class);
         if (annotation != null) {
            try {
               Method interfaceMethod = beanInterface.getMethod(method.getName(), method.getParameterTypes());
               toReturn.add(interfaceMethod);
            } catch (SecurityException e) {
               e.printStackTrace();
            } catch (NoSuchMethodException var13) {
            }
         }
      }

      return toReturn;
   }

   protected T createEjb(CreationalContext<T> creationalContext) {
      CurrentCreationalContext currentCreationalContext = (CurrentCreationalContext)this.beanContext.get(CurrentCreationalContext.class);
      CreationalContext existing = currentCreationalContext.get();
      currentCreationalContext.set(creationalContext);

      Object var5;
      try {
         T instance;
         if (this.homeLocalBean != null) {
            instance = (T)this.homeLocalBean.create();
         } else if (this.home != null) {
            instance = (T)this.home.create();
         } else {
            if (this.remote == null) {
               throw new IllegalStateException("no interface to proxy for ejb " + this.beanContext.getEjbName() + ", is this is a MDB maybe you shouldn't use a scope?");
            }

            instance = (T)this.remote.create();
         }

         if (this.isDependentAndStateful) {
            ((CreationalContextImpl)CreationalContextImpl.class.cast(creationalContext)).addDependent(this, instance);
         }

         var5 = instance;
      } finally {
         currentCreationalContext.set(existing);
      }

      return (T)var5;
   }

   private void destroyScopedStateful(T instance, CreationalContext<T> cc) {
      try {
         instance.hashCode();
      } catch (NoSuchEJBException e) {
         InvocationHandler handler = null;
         if (LocalBeanProxyFactory.isProxy(instance.getClass())) {
            handler = LocalBeanProxyFactory.getInvocationHandler(instance);
         } else if (ProxyManager.isProxyClass(instance.getClass())) {
            handler = ProxyManager.getInvocationHandler(instance);
         }

         if (BaseEjbProxyHandler.class.isInstance(handler) && !((BaseEjbProxyHandler)BaseEjbProxyHandler.class.cast(handler)).isValid()) {
            return;
         }

         this.logger.log(Level.FINE, "The stateful instance " + instance + " can't be removed since it was invalidated", e);
         return;
      }

      Object ejbInstance = this.dependentSFSBToBeRemoved.remove(System.identityHashCode(instance));
      if (ejbInstance != null) {
         this.destroyStatefulSessionBeanInstance(ejbInstance, cc);
      } else {
         this.destroyStatefulSessionBeanInstance(instance, cc);
      }

   }

   public void storeStatefulInstance(Object proxy, T instance) {
      this.dependentSFSBToBeRemoved.put(System.identityHashCode(proxy), instance);
   }

   public void initInternals() {
      List<Class> classes = this.beanContext.getBusinessLocalInterfaces();
      boolean noLocalInterface = classes.isEmpty();
      if (this.beanContext.getComponentType().isMessageDriven()) {
         this.homeLocalBean = null;
         this.home = null;
         this.remote = null;
      } else if (!this.beanContext.isLocalbean() && (!noLocalInterface || !this.beanContext.getBusinessRemoteInterfaces().isEmpty())) {
         if (!noLocalInterface) {
            Class<?> mainInterface = (Class)classes.get(0);
            List<Class> interfaces = ProxyInterfaceResolver.getInterfaces(this.beanContext.getBeanClass(), mainInterface, classes);
            interfaces.addAll(this.beanContext.getBusinessRemoteInterfaces());
            this.home = this.beanContext.getBusinessLocalHome(interfaces, mainInterface);
            this.homeLocalBean = null;
            this.remote = null;
         } else {
            Class<?> mainInterface = this.beanContext.getBusinessRemoteInterface();
            List<Class> interfaces = ProxyInterfaceResolver.getInterfaces(this.beanContext.getBeanClass(), mainInterface, this.beanContext.getBusinessRemoteInterfaces());
            this.remote = this.beanContext.getBusinessRemoteHome(interfaces, mainInterface);
            this.home = null;
            this.homeLocalBean = null;
         }
      } else {
         this.homeLocalBean = this.beanContext.getBusinessLocalBeanHome();
         this.home = null;
         this.remote = null;
      }

   }

   public static class EJBBeanAttributesImpl<T> extends BeanAttributesImpl<T> {
      private final BeanContext beanContext;
      private final Set<Type> ejbTypes;

      public EJBBeanAttributesImpl(BeanContext bc, BeanAttributes<T> beanAttributes) {
         super(beanAttributes, false);
         this.beanContext = bc;
         this.ejbTypes = new HashSet();
         this.initTypes();
      }

      public Set<Type> getTypes() {
         return this.ejbTypes;
      }

      public void initTypes() {
         if (this.beanContext.isLocalbean()) {
            addApiTypes(this.ejbTypes, this.beanContext.getBeanClass());
         }

         if (this.beanContext.getProxyClass() != null) {
            addApiTypes(this.ejbTypes, this.beanContext.getProxyClass());
         }

         List<Class> cl = this.beanContext.getBusinessLocalInterfaces();
         if (cl != null && !cl.isEmpty()) {
            Map<Class<?>, Type> apis = new HashMap(cl.size());

            for(Type t : this.beanContext.getManagedClass().getGenericInterfaces()) {
               if (ParameterizedType.class.isInstance(t)) {
                  try {
                     apis.put(Class.class.cast(((ParameterizedType)ParameterizedType.class.cast(t)).getRawType()), t);
                  } catch (Throwable var8) {
                  }
               }
            }

            for(Class<?> c : cl) {
               Type type = (Type)apis.get(c);
               this.ejbTypes.addAll(GenericsUtil.getTypeClosure((Type)(type != null ? type : c)));
            }
         }

         this.ejbTypes.add(Object.class);
      }

      private static void addApiTypes(Collection<Type> clazzes, Class<?> beanClass) {
         Typed typed = (Typed)beanClass.getAnnotation(Typed.class);
         if (typed != null && typed.value().length != 0) {
            Collections.addAll(clazzes, typed.value());
         } else {
            for(Type current = beanClass; current != null && Object.class != current; current = Class.class.isInstance(current) ? ((Class)Class.class.cast(current)).getGenericSuperclass() : null) {
               clazzes.add(current);
            }
         }

      }
   }

   public static class EjbInjectionTargetFactory<T> extends InjectionTargetFactoryImpl<T> {
      private final BeanContext beanContext;

      public EjbInjectionTargetFactory(BeanContext bc, AnnotatedType<T> annotatedType, WebBeansContext webBeansContext) {
         super(annotatedType, webBeansContext);
         this.beanContext = bc;
      }

      public InjectionTarget<T> createInjectionTarget(Bean<T> bean) {
         EjbInjectionTargetImpl<T> injectionTarget = new EjbInjectionTargetImpl<T>(this.getAnnotatedType(), this.createInjectionPoints(bean), this.getWebBeansContext());
         InjectionTarget<T> it = this.getWebBeansContext().getWebBeansUtil().fireProcessInjectionTargetEvent(injectionTarget, this.getAnnotatedType()).getInjectionTarget();

         for(InjectionPoint ip : it.getInjectionPoints()) {
            if (ip.getType() == UserTransaction.class && this.beanContext.getTransactionType() != TransactionType.BeanManaged) {
               throw new DefinitionException("@Inject UserTransaction is only valid for BeanManaged beans");
            }
         }

         if (!EjbInjectionTargetImpl.class.isInstance(it)) {
            return new EjbInjectionTargetImpl<T>(injectionTarget, it);
         } else {
            return it;
         }
      }

      protected List<AnnotatedMethod<?>> getPostConstructMethods() {
         return Collections.emptyList();
      }

      protected List<AnnotatedMethod<?>> getPreDestroyMethods() {
         return Collections.emptyList();
      }
   }

   public static class EjbInjectionTargetImpl<T> extends InjectionTargetImpl<T> {
      private CdiEjbBean<T> bean;
      private InjectionTarget<T> delegate;

      public EjbInjectionTargetImpl(AnnotatedType<T> annotatedType, Set<InjectionPoint> points, WebBeansContext webBeansContext) {
         super(annotatedType, points, webBeansContext, Collections.emptyList(), Collections.emptyList());
      }

      public EjbInjectionTargetImpl(EjbInjectionTargetImpl<T> original, InjectionTarget<T> delegate) {
         super(original.annotatedType, original.getInjectionPoints(), original.webBeansContext, Collections.emptyList(), Collections.emptyList());
         this.delegate = delegate;
      }

      public void setCdiEjbBean(CdiEjbBean<T> bean) {
         this.bean = bean;
      }

      protected void defineLifecycleInterceptors(Bean<T> bean, AnnotatedType<T> annotatedType, WebBeansContext webBeansContext) {
         if (!isDynamicBean(bean)) {
            super.defineLifecycleInterceptors(bean, annotatedType, webBeansContext);
         }

      }

      public void defineInterceptorStack(Bean<T> bean, AnnotatedType<T> annotatedType, WebBeansContext webBeansContext) {
         super.defineInterceptorStack(bean, isDynamicBean(bean) ? webBeansContext.getAnnotatedElementFactory().newAnnotatedType(((CdiEjbBean)CdiEjbBean.class.cast(bean)).getBeanContext().getManagedClass()) : annotatedType, webBeansContext);
      }

      protected boolean needsProxy() {
         return !this.bean.beanContext.isDynamicallyImplemented() && super.needsProxy();
      }

      public T produce(CreationalContext<T> creationalContext) {
         return (T)(this.delegate == null ? this.bean.createEjb(creationalContext) : this.delegate.produce(creationalContext));
      }

      public void dispose(T instance) {
         if (this.delegate == null) {
            this.bean.destroyComponentInstance(instance);
         } else {
            this.delegate.dispose(instance);
         }

      }

      public Set<InjectionPoint> getInjectionPoints() {
         return this.delegate == null ? super.getInjectionPoints() : this.delegate.getInjectionPoints();
      }

      public void inject(T instance, CreationalContext<T> ctx) {
         if (this.delegate == null) {
            super.inject(instance, ctx);
         } else {
            this.delegate.inject(instance, ctx);
         }

      }

      public void postConstruct(T instance) {
         if (this.delegate == null) {
            super.postConstruct(instance);
         } else {
            this.delegate.postConstruct(instance);
         }

      }

      public void preDestroy(T instance) {
         if (this.delegate == null) {
            super.preDestroy(instance);
         } else {
            this.delegate.preDestroy(instance);
         }

      }

      public T createNewPojo(CreationalContext<T> creationalContext) {
         CreationalContextImpl<T> ccImpl = (CreationalContextImpl)CreationalContextImpl.class.cast(creationalContext);
         Map<Interceptor<?>, Object> interceptorInstances = super.createInterceptorInstances(ccImpl);
         InterceptorResolutionService.BeanInterceptorInfo interceptorInfo = super.getInterceptorInfo();
         if (interceptorInfo != null) {
            Map<Constructor<?>, InterceptorResolutionService.BusinessMethodInterceptorInfo> constructorInterceptorInfos = interceptorInfo.getConstructorInterceptorInfos();
            if (!constructorInterceptorInfos.isEmpty()) {
               Interceptor<?>[] ejbInterceptors = ((InterceptorResolutionService.BusinessMethodInterceptorInfo)constructorInterceptorInfos.values().iterator().next()).getEjbInterceptors();
               if (null != ejbInterceptors) {
                  for(Interceptor interceptorBean : ejbInterceptors) {
                     if (!interceptorInstances.containsKey(interceptorBean)) {
                        ccImpl.putContextual(interceptorBean);
                        interceptorInstances.put(interceptorBean, interceptorBean.create(ccImpl));
                     }
                  }
               }
            }
         }

         T produce = (T)super.produce(interceptorInstances, ccImpl);
         return (T)(produce == null ? super.newInstance(ccImpl) : produce);
      }

      private static boolean isDynamicBean(Bean<?> bean) {
         return CdiEjbBean.class.isInstance(bean) && ((CdiEjbBean)CdiEjbBean.class.cast(bean)).beanContext.isDynamicallyImplemented();
      }
   }
}
