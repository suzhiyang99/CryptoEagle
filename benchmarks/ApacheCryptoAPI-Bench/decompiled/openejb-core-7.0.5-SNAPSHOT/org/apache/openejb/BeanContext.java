package org.apache.openejb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityBean;
import javax.ejb.Handle;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.MessageDrivenBean;
import javax.ejb.SessionBean;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.naming.Context;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import org.apache.openejb.assembler.classic.ProxyInterfaceResolver;
import org.apache.openejb.cdi.CdiEjbBean;
import org.apache.openejb.cdi.ConstructorInjectionBean;
import org.apache.openejb.cdi.CurrentCreationalContext;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.InstanceContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.cmp.KeyGenerator;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorInstance;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.ivm.ContextHandler;
import org.apache.openejb.core.ivm.EjbHomeProxyHandler;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionPolicyFactory;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Index;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.proxy.DynamicProxyImplFactory;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;
import org.apache.openejb.util.reflection.Reflections;
import org.apache.webbeans.annotation.AnnotationManager;
import org.apache.webbeans.component.CdiInterceptorBean;
import org.apache.webbeans.component.InjectionTargetBean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.context.creational.CreationalContextImpl;
import org.apache.webbeans.context.creational.DependentCreationalContext;
import org.apache.webbeans.inject.OWBInjector;
import org.apache.webbeans.intercept.DecoratorHandler;
import org.apache.webbeans.intercept.InterceptorResolutionService;
import org.apache.webbeans.portable.InjectionTargetImpl;
import org.apache.webbeans.proxy.InterceptorDecoratorProxyFactory;
import org.apache.webbeans.util.AnnotationUtil;
import org.apache.xbean.recipe.ConstructionException;

public class BeanContext extends DeploymentContext {
   private static final Logger logger;
   public static final String USER_INTERCEPTOR_KEY = "org.apache.openejb.default.system.interceptors";
   public static final String USER_INTERCEPTOR_SEPARATOR = ",| |;";
   private static final Field DEPENDENTS_OBJECTS;
   private ConstructorInjectionBean<Object> constructorInjectionBean;
   private final boolean passivable;
   private final ModuleContext moduleContext;
   private final Context jndiContext;
   private Object containerData;
   private boolean destroyed;
   private final Class beanClass;
   private final List<Class> businessLocals;
   private final List<Class> businessRemotes;
   private Class serviceEndpointInterface;
   private Method ejbTimeout;
   private EjbTimerService ejbTimerService;
   private boolean isBeanManagedTransaction;
   private boolean isBeanManagedConcurrency;
   private Container container;
   private String ejbName;
   private String runAs;
   private String runAsUser;
   private final BeanType componentType;
   private boolean hidden;
   private final Map<Method, Method> methodMap;
   private final Map<Method, MethodContext> methodContextMap;
   private final Map<String, ViewContext> viewContextMap;
   private TransactionPolicyFactory transactionPolicyFactory;
   private final List<InterceptorData> callbackInterceptors;
   private final List<InterceptorData> beanCallbackInterceptors;
   private final Set<InterceptorData> instanceScopedInterceptors;
   private final List<InterceptorInstance> systemInterceptors;
   private final List<InterceptorInstance> userInterceptors;
   private final List<Injection> injections;
   private final Map<Class, InterfaceType> interfaces;
   private final Map<Class, ExceptionType> exceptions;
   private final boolean localbean;
   private Duration accessTimeout;
   private final Set<Class<?>> asynchronousClasses;
   private final Set<String> asynchronousMethodSignatures;
   private Class<?> proxyClass;
   private Mdb mdb;
   private Singleton singleton;
   private Stateful stateful;
   private Cmp cmp;
   private LegacyView legacyView;
   private final Map<String, String> securityRoleReferences;
   private final Map<Method, Boolean> removeExceptionPolicy;
   private final Set<InterceptorData> cdiInterceptors;

   public boolean isDynamicallyImplemented() {
      return this.proxyClass != null;
   }

   public void mergeOWBAndOpenEJBInfo() {
      CdiEjbBean cdiEjbBean = (CdiEjbBean)this.get(CdiEjbBean.class);
      if (cdiEjbBean != null) {
         InjectionTargetImpl<?> injectionTarget = (InjectionTargetImpl)InjectionTargetImpl.class.cast(((CdiEjbBean)this.get(CdiEjbBean.class)).getInjectionTarget());
         InterceptorResolutionService.BeanInterceptorInfo info = injectionTarget.getInterceptorInfo();
         if (info != null) {
            Collection<Interceptor<?>> postConstructInterceptors = (Collection)Collection.class.cast(Reflections.get(injectionTarget, "postConstructInterceptors"));
            Collection<Interceptor<?>> preDestroyInterceptors = (Collection)Collection.class.cast(Reflections.get(injectionTarget, "preDestroyInterceptors"));
            if (postConstructInterceptors != null) {
               for(Interceptor<?> pc : postConstructInterceptors) {
                  if (!this.isEjbInterceptor(pc)) {
                     InterceptorData interceptorData = this.createInterceptorData(pc);
                     this.instanceScopedInterceptors.add(interceptorData);
                     this.cdiInterceptors.add(interceptorData);
                  }
               }
            }

            if (preDestroyInterceptors != null) {
               for(Interceptor<?> pd : preDestroyInterceptors) {
                  if (!this.isEjbInterceptor(pd) && !postConstructInterceptors.contains(pd)) {
                     InterceptorData interceptorData = this.createInterceptorData(pd);
                     this.instanceScopedInterceptors.add(interceptorData);
                     this.cdiInterceptors.add(interceptorData);
                  }
               }
            }

            for(Map.Entry<Method, InterceptorResolutionService.BusinessMethodInterceptorInfo> entry : info.getBusinessMethodsInfo().entrySet()) {
               Interceptor<?>[] interceptors = ((InterceptorResolutionService.BusinessMethodInterceptorInfo)entry.getValue()).getCdiInterceptors();
               if (interceptors != null) {
                  for(Interceptor<?> i : interceptors) {
                     if (!postConstructInterceptors.contains(i) && !preDestroyInterceptors.contains(i)) {
                        InterceptorData data = this.createInterceptorData(i);
                        this.addCdiMethodInterceptor((Method)entry.getKey(), data);
                     }
                  }

                  ((InterceptorResolutionService.BusinessMethodInterceptorInfo)entry.getValue()).setEjbInterceptors(new ArrayList());
                  ((InterceptorResolutionService.BusinessMethodInterceptorInfo)entry.getValue()).setCdiInterceptors(new ArrayList());
               }
            }

            if (info.getSelfInterceptorBean() != null) {
               try {
                  Field field = InterceptorResolutionService.BeanInterceptorInfo.class.getDeclaredField("selfInterceptorBean");
                  field.setAccessible(true);
                  field.set(info, (Object)null);
               } catch (Exception var14) {
               }
            }

            ((Map)Map.class.cast(Reflections.get(injectionTarget, "methodInterceptors"))).clear();
            clear((Collection)Collection.class.cast(postConstructInterceptors));
            clear((Collection)Collection.class.cast(preDestroyInterceptors));
            clear((Collection)Collection.class.cast(Reflections.get(injectionTarget, "postConstructMethods")));
            clear((Collection)Collection.class.cast(Reflections.get(injectionTarget, "preDestroyMethods")));
            clear((Collection)Collection.class.cast(Reflections.get(info, "ejbInterceptors")));
            clear((Collection)Collection.class.cast(Reflections.get(info, "cdiInterceptors")));
            Method timeout = this.getEjbTimeout();
            if (timeout != null) {
               AnnotatedType annotatedType = cdiEjbBean.getAnnotatedType();
               AnnotationManager annotationManager = this.getWebBeansContext().getAnnotationManager();
               Collection<Annotation> annotations = new HashSet();
               annotations.addAll(annotationManager.getInterceptorAnnotations(annotatedType.getAnnotations()));

               for(AnnotatedMethod<?> m : annotatedType.getMethods()) {
                  if (timeout.equals(m.getJavaMember())) {
                     annotations.addAll(annotationManager.getInterceptorAnnotations(m.getAnnotations()));
                     break;
                  }
               }

               if (!annotations.isEmpty()) {
                  for(Interceptor<?> timeoutInterceptor : this.getWebBeansContext().getBeanManagerImpl().resolveInterceptors(InterceptionType.AROUND_TIMEOUT, AnnotationUtil.asArray(annotations))) {
                     if (!this.isEjbInterceptor(timeoutInterceptor)) {
                        InterceptorData data = this.createInterceptorData(timeoutInterceptor);
                        this.addCdiMethodInterceptor(timeout, data);
                     }
                  }
               }
            }

         }
      }
   }

   private boolean isEjbInterceptor(Interceptor<?> pc) {
      Set<Annotation> interceptorBindings = pc.getInterceptorBindings();
      return interceptorBindings == null || interceptorBindings.isEmpty();
   }

   private InterceptorData createInterceptorData(Interceptor<?> i) {
      InterceptorData data;
      if (CdiInterceptorBean.class.isInstance(i)) {
         CdiInterceptorBean cdiInterceptorBean = (CdiInterceptorBean)CdiInterceptorBean.class.cast(i);
         data = new InterceptorData(i.getBeanClass());
         data.getAroundInvoke().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.AROUND_INVOKE));
         data.getPostConstruct().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.POST_CONSTRUCT));
         data.getPreDestroy().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.PRE_DESTROY));
         data.getPostActivate().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.POST_ACTIVATE));
         data.getPrePassivate().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.PRE_PASSIVATE));
         data.getAroundTimeout().addAll(this.getInterceptionMethodAsListOrEmpty(cdiInterceptorBean, InterceptionType.AROUND_TIMEOUT));
      } else {
         data = InterceptorData.scan(i.getBeanClass());
      }

      return data;
   }

   private List<Method> getInterceptionMethodAsListOrEmpty(CdiInterceptorBean cdiInterceptorBean, InterceptionType aroundInvoke) {
      Method[] methods = cdiInterceptorBean.getInterceptorMethods(aroundInvoke);
      return methods == null ? Collections.emptyList() : Arrays.asList(methods);
   }

   private static void clear(Collection<?> c) {
      if (c != null) {
         c.clear();
      }

   }

   public Class getInterface(InterfaceType interfaceType) {
      switch (interfaceType) {
         case EJB_HOME:
            return this.getHomeInterface();
         case EJB_OBJECT:
            return this.getRemoteInterface();
         case EJB_LOCAL_HOME:
            return this.getLocalHomeInterface();
         case EJB_LOCAL:
            return this.getLocalInterface();
         case BUSINESS_LOCAL:
            return this.getBusinessLocalInterface();
         case BUSINESS_REMOTE:
            return this.getBusinessRemoteInterface();
         case TIMEOUT:
            return Timeout.class;
         case BUSINESS_REMOTE_HOME:
            return BusinessRemoteHome.class;
         case BUSINESS_LOCAL_HOME:
            return BusinessLocalHome.class;
         case SERVICE_ENDPOINT:
            return this.getServiceEndpointInterface();
         case LOCALBEAN:
            return this.getBeanClass();
         case BUSINESS_LOCALBEAN_HOME:
            return BusinessLocalBeanHome.class;
         default:
            throw new IllegalStateException("Unexpected enum constant: " + interfaceType);
      }
   }

   public List<Class> getInterfaces(InterfaceType interfaceType) {
      switch (interfaceType) {
         case BUSINESS_LOCAL:
            return this.getBusinessLocalInterfaces();
         case BUSINESS_REMOTE:
            return this.getBusinessRemoteInterfaces();
         default:
            List<Class> interfaces = new ArrayList();
            interfaces.add(this.getInterface(interfaceType));
            return interfaces;
      }
   }

   public InterfaceType getInterfaceType(Class clazz) {
      InterfaceType type = (InterfaceType)this.interfaces.get(clazz);
      if (type != null) {
         return type;
      } else if (EJBLocalHome.class.isAssignableFrom(clazz)) {
         return InterfaceType.EJB_LOCAL_HOME;
      } else if (EJBLocalObject.class.isAssignableFrom(clazz)) {
         return InterfaceType.EJB_LOCAL;
      } else if (EJBHome.class.isAssignableFrom(clazz)) {
         return InterfaceType.EJB_HOME;
      } else if (EJBObject.class.isAssignableFrom(clazz)) {
         return InterfaceType.EJB_OBJECT;
      } else {
         for(Map.Entry<Class, InterfaceType> entry : this.interfaces.entrySet()) {
            if (((Class)entry.getKey()).getName().equals(clazz.getName())) {
               return (InterfaceType)entry.getValue();
            }
         }

         return null;
      }
   }

   private BeanContext(String id, Context jndiContext, ModuleContext moduleContext, BeanType componentType, boolean localBean, Class beanClass, boolean passivable) {
      super(id, moduleContext.getOptions());
      this.businessLocals = new ArrayList();
      this.businessRemotes = new ArrayList();
      this.methodMap = new HashMap();
      this.methodContextMap = new HashMap();
      this.viewContextMap = new HashMap();
      this.callbackInterceptors = new ArrayList();
      this.beanCallbackInterceptors = new ArrayList();
      this.instanceScopedInterceptors = new HashSet();
      this.systemInterceptors = new ArrayList();
      this.userInterceptors = new ArrayList();
      this.injections = new ArrayList();
      this.interfaces = new HashMap();
      this.exceptions = new ConcurrentHashMap();
      this.asynchronousClasses = new HashSet();
      this.asynchronousMethodSignatures = new HashSet();
      this.securityRoleReferences = new HashMap();
      this.removeExceptionPolicy = new HashMap();
      this.cdiInterceptors = new LinkedHashSet();
      if (beanClass == null) {
         throw new NullPointerException("beanClass input parameter is null");
      } else {
         this.moduleContext = moduleContext;
         this.jndiContext = new ContextHandler(jndiContext);
         this.localbean = localBean;
         this.componentType = componentType;
         this.beanClass = beanClass;
         this.passivable = passivable;
         String interceptors = SystemInstance.get().getProperties().getProperty("org.apache.openejb.default.system.interceptors");
         if (interceptors != null) {
            String[] interceptorArray = interceptors.split(",| |;");
            ClassLoader classLoader = moduleContext.getClassLoader();

            for(String interceptor : interceptorArray) {
               if (interceptor != null && !interceptor.isEmpty()) {
                  Object interceptorObject;
                  try {
                     Class<?> clazz = classLoader.loadClass(interceptor);
                     interceptorObject = clazz.newInstance();
                  } catch (Exception var17) {
                     logger.warning("interceptor " + interceptor + " not found, are you sure the container can load it?");
                     continue;
                  }

                  this.addUserInterceptor(interceptorObject);
               }
            }
         }

      }
   }

   public BeanContext(String id, Context jndiContext, ModuleContext moduleContext, Class beanClass, Class homeInterface, Class remoteInterface, Class localHomeInterface, Class localInterface, Class proxy, Class serviceEndpointInterface, List<Class> businessLocals, List<Class> businessRemotes, Class pkClass, BeanType componentType, boolean localBean, boolean passivable) throws SystemException {
      this(id, jndiContext, moduleContext, componentType, localBean, beanClass, passivable);
      this.proxyClass = proxy;
      if (homeInterface != null) {
         this.getLegacyView().homeInterface = homeInterface;
      }

      if (localInterface != null) {
         this.getLegacyView().localInterface = localInterface;
      }

      if (localHomeInterface != null) {
         this.getLegacyView().localHomeInterface = localHomeInterface;
      }

      if (remoteInterface != null) {
         this.getLegacyView().remoteInterface = remoteInterface;
      }

      if (businessLocals != null) {
         this.businessLocals.addAll(businessLocals);
      }

      if (businessRemotes != null) {
         this.businessRemotes.addAll(businessRemotes);
      }

      if (pkClass != null) {
         this.getCmp().pkClass = pkClass;
      }

      this.serviceEndpointInterface = serviceEndpointInterface;
      if (TimedObject.class.isAssignableFrom(beanClass)) {
         try {
            this.ejbTimeout = beanClass.getMethod("ejbTimeout", Timer.class);
         } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
         }
      }

      this.addInterface(this.getServiceEndpointInterface(), InterfaceType.SERVICE_ENDPOINT);
      this.addInterface(EJBHome.class, InterfaceType.EJB_HOME);
      this.addInterface(EJBObject.class, InterfaceType.EJB_OBJECT);
      this.addInterface(EJBLocalHome.class, InterfaceType.EJB_LOCAL_HOME);
      this.addInterface(EJBLocalObject.class, InterfaceType.EJB_LOCAL);
      this.addInterface(this.getHomeInterface(), InterfaceType.EJB_HOME);
      this.addInterface(this.getRemoteInterface(), InterfaceType.EJB_OBJECT);
      this.addInterface(this.getLocalHomeInterface(), InterfaceType.EJB_LOCAL_HOME);
      this.addInterface(this.getLocalInterface(), InterfaceType.EJB_LOCAL);
      this.addInterface(BusinessRemoteHome.class, InterfaceType.BUSINESS_REMOTE_HOME);

      for(Class businessRemote : this.businessRemotes) {
         this.addInterface(businessRemote, InterfaceType.BUSINESS_REMOTE);
      }

      this.addInterface(BusinessLocalHome.class, InterfaceType.BUSINESS_LOCAL_HOME);

      for(Class businessLocal : this.businessLocals) {
         this.addInterface(businessLocal, InterfaceType.BUSINESS_LOCAL);
      }

      if (localBean) {
         this.addInterface(beanClass, InterfaceType.LOCALBEAN);
      }

      this.initDefaultLock();
   }

   private LegacyView getLegacyView() {
      if (this.legacyView == null) {
         this.legacyView = new LegacyView();
      }

      return this.legacyView;
   }

   private Mdb getMdb() {
      if (this.mdb == null) {
         this.mdb = new Mdb();
      }

      return this.mdb;
   }

   private Singleton getSingleton() {
      if (this.singleton == null) {
         this.singleton = new Singleton();
      }

      return this.singleton;
   }

   private Stateful getStateful() {
      if (this.stateful == null) {
         this.stateful = new Stateful();
      }

      return this.stateful;
   }

   private Cmp getCmp() {
      if (this.cmp == null) {
         this.cmp = new Cmp();
      }

      return this.cmp;
   }

   private void addInterface(Class interfce, InterfaceType type) {
      if (interfce != null) {
         this.interfaces.put(interfce, type);

         for(Class clazz : interfce.getInterfaces()) {
            this.addInterface(clazz, type);
         }

      }
   }

   public void addApplicationException(Class exception, boolean rollback, boolean inherited) {
      if (inherited) {
         if (rollback) {
            this.exceptions.put(exception, ExceptionType.APPLICATION_ROLLBACK);
         } else {
            this.exceptions.put(exception, ExceptionType.APPLICATION);
         }
      } else if (rollback) {
         this.exceptions.put(exception, ExceptionType.APPLICATION_ROLLBACK_NOT_INHERITED);
      } else {
         this.exceptions.put(exception, ExceptionType.APPLICATION_NOT_INHERITED);
      }

   }

   public ExceptionType getExceptionType(Throwable e) {
      if (!(e instanceof Exception)) {
         return ExceptionType.SYSTEM;
      } else {
         Class<?> exceptionClass = e.getClass();

         for(boolean inherited = false; exceptionClass != Object.class; inherited = true) {
            ExceptionType type = (ExceptionType)this.exceptions.get(exceptionClass);
            if (type == ExceptionType.APPLICATION || type == ExceptionType.APPLICATION_ROLLBACK) {
               return type;
            }

            if (type != null) {
               if (inherited) {
                  return ExceptionType.SYSTEM;
               }

               if (type == ExceptionType.APPLICATION_NOT_INHERITED) {
                  return ExceptionType.APPLICATION;
               }

               return ExceptionType.APPLICATION_ROLLBACK;
            }

            exceptionClass = exceptionClass.getSuperclass();
         }

         Class<? extends Throwable> eClass = e.getClass();
         javax.ejb.ApplicationException applicationException = (javax.ejb.ApplicationException)eClass.getAnnotation(javax.ejb.ApplicationException.class);
         if (applicationException != null) {
            this.addApplicationException(eClass, applicationException.rollback(), applicationException.inherited());
            return this.getExceptionType(e);
         } else if (e instanceof RuntimeException) {
            return ExceptionType.SYSTEM;
         } else {
            return ExceptionType.APPLICATION;
         }
      }
   }

   public BeanContext(String id, Context jndiContext, ModuleContext moduleContext, Class beanClass, Class mdbInterface, Map<String, String> activationProperties) throws SystemException {
      this(id, jndiContext, moduleContext, BeanType.MESSAGE_DRIVEN, false, beanClass, false);
      this.getMdb().mdbInterface = mdbInterface;
      this.getMdb().activationProperties.putAll(activationProperties);
      if (TimedObject.class.isAssignableFrom(beanClass)) {
         try {
            this.ejbTimeout = beanClass.getMethod("ejbTimeout", Timer.class);
         } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
         }
      }

      this.initDefaultLock();
      this.createMethodMap();
   }

   private void initDefaultLock() {
      if (BeanType.SINGLETON.equals(this.componentType)) {
         ArrayList<Class> classes = new ArrayList();

         for(Class local : this.businessRemotes) {
            classes.add(local);
         }

         for(Class local : this.businessLocals) {
            classes.add(local);
         }

         classes.add(this.beanClass);

         for(Class c : classes) {
            Lock lock = null;

            try {
               lock = (Lock)c.getAnnotation(Lock.class);
               this.getSingleton().lockType = lock.value();
               if (logger.isDebugEnabled()) {
                  logger.debug("Declared Lock for " + c.getName() + " is " + this.getSingleton().lockType);
               }
            } catch (NullPointerException var6) {
            } catch (Throwable var7) {
               logger.warning("Failed to determine from: " + lock);
            }
         }

         if (logger.isDebugEnabled()) {
            logger.debug("Default Lock for " + this.beanClass.getName() + " is " + this.getSingleton().lockType);
         }

      }
   }

   public Object getContainerData() {
      return this.containerData;
   }

   public void setContainerData(Object containerData) {
      this.containerData = containerData;
   }

   public ClassLoader getClassLoader() {
      return this.moduleContext.getClassLoader();
   }

   public Context getJndiContext() {
      return this.jndiContext;
   }

   public ModuleContext getModuleContext() {
      return this.moduleContext;
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public void setDestroyed(boolean destroyed) {
      this.destroyed = destroyed;
   }

   public List<Injection> getInjections() {
      return this.injections;
   }

   public Index<EntityManagerFactory, EntityManagerConfiguration> getExtendedEntityManagerFactories() {
      return this.getStateful().extendedEntityManagerFactories;
   }

   public void setExtendedEntityManagerFactories(Index<EntityManagerFactory, EntityManagerConfiguration> extendedEntityManagerFactories) {
      this.getStateful().extendedEntityManagerFactories = extendedEntityManagerFactories;
   }

   public void setContainer(Container container) {
      this.container = container;
   }

   public BeanType getComponentType() {
      return this.componentType;
   }

   public LockType getConcurrencyAttribute(Method beanMethod) {
      return this.getMethodContext(beanMethod).getLockType();
   }

   public LockType getLockType() {
      return this.getSingleton().lockType;
   }

   public TransactionType getTransactionType(Method method) {
      return this.getTransactionType(method, (InterfaceType)null);
   }

   public TransactionType getTransactionType(Method method, InterfaceType interfaceType) {
      MethodContext methodContext = null;
      if (interfaceType != null) {
         methodContext = this.getViewMethodContext(method, interfaceType.getSpecName());
      }

      if (methodContext == null) {
         methodContext = (MethodContext)this.methodContextMap.get(method);
      }

      if (methodContext == null) {
         Method beanMethod = this.getMatchingBeanMethod(method);
         methodContext = this.getMethodContext(beanMethod);
      }

      return methodContext.getTransactionType();
   }

   public TransactionType getTransactionType() {
      return this.isBeanManagedTransaction ? TransactionType.BeanManaged : TransactionType.Required;
   }

   public TransactionPolicyFactory getTransactionPolicyFactory() {
      return this.transactionPolicyFactory;
   }

   public void setTransactionPolicyFactory(TransactionPolicyFactory transactionPolicyFactory) {
      this.transactionPolicyFactory = transactionPolicyFactory;
   }

   public Container getContainer() {
      return this.container;
   }

   public Object getDeploymentID() {
      return this.getId();
   }

   public boolean isBeanManagedTransaction() {
      return this.isBeanManagedTransaction;
   }

   public boolean isBeanManagedConcurrency() {
      return this.isBeanManagedConcurrency;
   }

   public Class getHomeInterface() {
      return this.legacyView == null ? null : this.getLegacyView().homeInterface;
   }

   public Class getRemoteInterface() {
      return this.legacyView == null ? null : this.getLegacyView().remoteInterface;
   }

   public Class getLocalHomeInterface() {
      return this.legacyView == null ? null : this.getLegacyView().localHomeInterface;
   }

   public Class getLocalInterface() {
      return this.legacyView == null ? null : this.getLegacyView().localInterface;
   }

   public Class getBeanClass() {
      return this.beanClass;
   }

   public Class getBusinessLocalInterface() {
      return this.businessLocals.size() > 0 ? (Class)this.businessLocals.get(0) : null;
   }

   public Class getBusinessRemoteInterface() {
      return this.businessRemotes.size() > 0 ? (Class)this.businessRemotes.get(0) : null;
   }

   public List<Class> getBusinessLocalInterfaces() {
      return this.businessLocals;
   }

   public List<Class> getBusinessRemoteInterfaces() {
      return this.businessRemotes;
   }

   public Class getMdbInterface() {
      return this.mdb == null ? null : this.getMdb().mdbInterface;
   }

   public Class getServiceEndpointInterface() {
      return this.serviceEndpointInterface;
   }

   public Map<String, String> getActivationProperties() {
      return this.getMdb().activationProperties;
   }

   public Class getPrimaryKeyClass() {
      return this.cmp == null ? null : this.cmp.pkClass;
   }

   public EJBHome getEJBHome() {
      if (this.getHomeInterface() == null) {
         throw new IllegalStateException("This component has no home interface: " + this.getDeploymentID());
      } else {
         if (this.getLegacyView().ejbHomeRef == null) {
            this.getLegacyView().ejbHomeRef = (EJBHome)EjbHomeProxyHandler.createHomeProxy(this, InterfaceType.EJB_HOME);
         }

         return this.getLegacyView().ejbHomeRef;
      }
   }

   public EJBLocalHome getEJBLocalHome() {
      if (this.getLocalHomeInterface() == null) {
         throw new IllegalStateException("This component has no local home interface: " + this.getDeploymentID());
      } else {
         if (this.getLegacyView().ejbLocalHomeRef == null) {
            this.getLegacyView().ejbLocalHomeRef = (EJBLocalHome)EjbHomeProxyHandler.createHomeProxy(this, InterfaceType.EJB_LOCAL_HOME);
         }

         return this.getLegacyView().ejbLocalHomeRef;
      }
   }

   public BusinessLocalHome getBusinessLocalHome() {
      return this.getBusinessLocalHome(this.getBusinessLocalInterfaces(), (Class)null);
   }

   public BusinessLocalBeanHome getBusinessLocalBeanHome() {
      List<Class> interfaces = new ArrayList();
      interfaces.add(this.beanClass);
      return (BusinessLocalBeanHome)EjbHomeProxyHandler.createHomeProxy(this, InterfaceType.BUSINESS_LOCALBEAN_HOME, interfaces, this.beanClass);
   }

   public BusinessLocalHome getBusinessLocalHome(Class mainInterface) {
      List<Class> localInterfaces = this.getBusinessLocalInterfaces();
      List<Class> interfaces = ProxyInterfaceResolver.getInterfaces(this.getBeanClass(), mainInterface, localInterfaces);
      return this.getBusinessLocalHome(interfaces, mainInterface);
   }

   public BusinessLocalHome getBusinessLocalHome(List<Class> interfaces, Class mainInterface) {
      if (this.getBusinessLocalInterfaces().size() == 0) {
         throw new IllegalStateException("This component has no business local interfaces: " + this.getDeploymentID());
      } else if (interfaces.size() == 0) {
         throw new IllegalArgumentException("No interface classes were specified");
      } else {
         for(Class clazz : interfaces) {
            if (!this.getBusinessLocalInterfaces().contains(clazz) && !this.getBusinessRemoteInterfaces().contains(clazz)) {
               throw new IllegalArgumentException("Not a business interface of this bean:" + clazz.getName());
            }
         }

         return (BusinessLocalHome)EjbHomeProxyHandler.createHomeProxy(this, InterfaceType.BUSINESS_LOCAL_HOME, interfaces, mainInterface);
      }
   }

   public BusinessRemoteHome getBusinessRemoteHome() {
      return this.getBusinessRemoteHome(this.getBusinessRemoteInterfaces(), (Class)null);
   }

   public BusinessRemoteHome getBusinessRemoteHome(Class mainInterface) {
      List<Class> remoteInterfaces = this.getBusinessRemoteInterfaces();
      List<Class> interfaces = ProxyInterfaceResolver.getInterfaces(this.getBeanClass(), mainInterface, remoteInterfaces);
      return this.getBusinessRemoteHome(interfaces, mainInterface);
   }

   public BusinessRemoteHome getBusinessRemoteHome(List<Class> interfaces, Class mainInterface) {
      if (this.getBusinessRemoteInterfaces().size() == 0) {
         throw new IllegalStateException("This component has no business remote interfaces: " + this.getDeploymentID());
      } else if (interfaces.size() == 0) {
         throw new IllegalArgumentException("No interface classes were specified");
      } else {
         for(Class clazz : interfaces) {
            if (!this.getBusinessRemoteInterfaces().contains(clazz)) {
               throw new IllegalArgumentException("Not a business interface of this bean:" + clazz.getName());
            }
         }

         return (BusinessRemoteHome)EjbHomeProxyHandler.createHomeProxy(this, InterfaceType.BUSINESS_REMOTE_HOME, interfaces, mainInterface);
      }
   }

   public String getDestinationId() {
      return this.getMdb().destinationId;
   }

   public void setDestinationId(String destinationId) {
      this.getMdb().destinationId = destinationId;
   }

   public void setBeanManagedTransaction(boolean value) {
      this.isBeanManagedTransaction = value;
   }

   public void setBeanManagedConcurrency(boolean beanManagedConcurrency) {
      this.isBeanManagedConcurrency = beanManagedConcurrency;
   }

   public Context getJndiEnc() {
      return this.jndiContext;
   }

   public boolean isReentrant() {
      return this.getCmp().isReentrant;
   }

   public void setIsReentrant(boolean reentrant) {
      this.getCmp().isReentrant = reentrant;
   }

   public Method getMatchingBeanMethod(Method interfaceMethod) {
      Method method = (Method)this.methodMap.get(interfaceMethod);
      return method == null ? interfaceMethod : method;
   }

   public MethodContext getMethodContext(Method method) {
      MethodContext methodContext = (MethodContext)this.methodContextMap.get(method);
      if (methodContext == null) {
         methodContext = new MethodContext(this, method);
         this.methodContextMap.put(method, methodContext);
      }

      return methodContext;
   }

   public Iterator<Map.Entry<Method, MethodContext>> iteratorMethodContext() {
      return this.methodContextMap.entrySet().iterator();
   }

   public void setMethodConcurrencyAttribute(Method method, LockType concurrencyAttribute) {
      this.getMethodContext(method).setLockType(concurrencyAttribute);
   }

   public void setMethodTransactionAttribute(Method method, TransactionType transactionType) throws OpenEJBException {
      this.setMethodTransactionAttribute(method, transactionType, (String)null);
   }

   public void setMethodTransactionAttribute(Method method, TransactionType transactionType, String view) throws OpenEJBException {
      if (view == null) {
         this.getMethodContext(method).setTransactionType(transactionType);
      } else {
         this.initViewMethodContext(method, view).setTransactionType(transactionType);
      }

   }

   public List<Method> getRemoveMethods() {
      return this.getStateful().removeMethods;
   }

   public void setRetainIfExeption(Method removeMethod, boolean retain) {
      if (this.getRemoveMethods().contains(removeMethod)) {
         this.removeExceptionPolicy.put(removeMethod, retain);
      }

   }

   public boolean retainIfExeption(Method removeMethod) {
      Boolean retain = (Boolean)this.removeExceptionPolicy.get(removeMethod);
      return retain != null && retain;
   }

   public Set<InterceptorData> getInstanceScopedInterceptors() {
      return this.instanceScopedInterceptors;
   }

   public void addSystemInterceptor(Object interceptor) {
      this.systemInterceptors.add(new InterceptorInstance(interceptor));
   }

   public void addFirstSystemInterceptor(Object interceptor) {
      this.systemInterceptors.add(0, new InterceptorInstance(interceptor));
   }

   public void addUserInterceptor(Object interceptor) {
      this.userInterceptors.add(new InterceptorInstance(interceptor));
   }

   public List<InterceptorInstance> getUserAndSystemInterceptors() {
      List<InterceptorInstance> interceptors = new ArrayList(this.systemInterceptors);
      interceptors.addAll(this.userInterceptors);
      return interceptors;
   }

   public List<InterceptorData> getCallbackInterceptors() {
      List<InterceptorData> datas = this.getInterceptorData();
      datas.addAll(this.callbackInterceptors);
      datas.addAll(this.cdiInterceptors);
      datas.addAll(this.beanCallbackInterceptors);
      return datas;
   }

   public void setCallbackInterceptors(List<InterceptorData> callbackInterceptors) {
      this.beanCallbackInterceptors.clear();
      this.callbackInterceptors.clear();

      for(InterceptorData data : callbackInterceptors) {
         if (data.getInterceptorClass().isAssignableFrom(this.getManagedClass())) {
            this.beanCallbackInterceptors.add(data);
         } else {
            this.callbackInterceptors.add(data);
         }
      }

      this.instanceScopedInterceptors.addAll(callbackInterceptors);
   }

   public List<InterceptorData> getCdiInterceptors() {
      return new ArrayList(this.cdiInterceptors);
   }

   public void setCdiInterceptors(List<InterceptorData> cdiInterceptors) {
      this.cdiInterceptors.clear();
      this.cdiInterceptors.addAll(cdiInterceptors);
      this.instanceScopedInterceptors.addAll(cdiInterceptors);
   }

   public List<InterceptorData> getMethodInterceptors(Method method) {
      return this.getMethodContext(method).getInterceptors();
   }

   public List<InterceptorData> getInterceptorData() {
      List<InterceptorData> datas = new ArrayList(this.getUserAndSystemInterceptors().size());

      for(InterceptorInstance instance : this.getUserAndSystemInterceptors()) {
         datas.add(instance.getData());
      }

      return datas;
   }

   public void addCdiMethodInterceptor(Method method, InterceptorData interceptor) {
      this.getMethodContext(method).addCdiInterceptor(interceptor);
      this.instanceScopedInterceptors.add(interceptor);
   }

   public void setMethodInterceptors(Method method, List<InterceptorData> interceptors) {
      this.getMethodContext(method).setInterceptors(interceptors);
      this.instanceScopedInterceptors.addAll(interceptors);
   }

   public void createMethodMap() throws SystemException {
      if (this.getRemoteInterface() != null) {
         this.mapObjectInterface(this.getLegacyView().remoteInterface);
         this.mapHomeInterface(this.getLegacyView().homeInterface);
      }

      if (this.getLocalInterface() != null) {
         this.mapObjectInterface(this.getLegacyView().localInterface);
      }

      if (this.getLocalHomeInterface() != null) {
         this.mapHomeInterface(this.getLegacyView().localHomeInterface);
      }

      if (this.serviceEndpointInterface != null) {
         this.mapObjectInterface(this.serviceEndpointInterface);
      }

      for(Class businessLocal : this.businessLocals) {
         this.mapObjectInterface(businessLocal);
      }

      for(Class businessRemote : this.businessRemotes) {
         this.mapObjectInterface(businessRemote);
      }

      if (this.componentType == BeanType.MESSAGE_DRIVEN && MessageDrivenBean.class.isAssignableFrom(this.beanClass)) {
         try {
            this.getLegacyView().createMethod = this.beanClass.getMethod("ejbCreate");
         } catch (NoSuchMethodException var4) {
         }
      }

      try {
         if (this.componentType != BeanType.STATEFUL && this.componentType != BeanType.MANAGED) {
            if (this.componentType == BeanType.BMP_ENTITY || this.componentType == BeanType.CMP_ENTITY) {
               Method beanMethod = EntityBean.class.getDeclaredMethod("ejbRemove");
               Method clientMethod = EJBHome.class.getDeclaredMethod("remove", Handle.class);
               this.mapMethods(clientMethod, beanMethod);
               clientMethod = EJBHome.class.getDeclaredMethod("remove", Object.class);
               this.mapMethods(clientMethod, beanMethod);
               clientMethod = EJBObject.class.getDeclaredMethod("remove");
               this.mapMethods(clientMethod, beanMethod);
               clientMethod = EJBLocalObject.class.getDeclaredMethod("remove");
               this.mapMethods(clientMethod, beanMethod);
            }
         } else {
            Method beanMethod = null;
            if (SessionBean.class.isAssignableFrom(this.beanClass)) {
               beanMethod = SessionBean.class.getDeclaredMethod("ejbRemove");
            } else {
               Iterator clientMethod = this.getRemoveMethods().iterator();

               while(true) {
                  if (clientMethod.hasNext()) {
                     Method method = (Method)clientMethod.next();
                     if (method.getParameterTypes().length != 0) {
                        continue;
                     }

                     beanMethod = method;
                  }

                  if (beanMethod == null && (this.getHomeInterface() != null || this.getLocalHomeInterface() != null)) {
                     throw new IllegalStateException("Bean class has no @Remove methods to match EJBObject.remove() or EJBLocalObject.remove().  A no-arg remove method must be added: beanClass=" + this.beanClass.getName());
                  }
                  break;
               }
            }

            Method clientMethod = EJBHome.class.getDeclaredMethod("remove", Handle.class);
            this.mapMethods(clientMethod, beanMethod);
            clientMethod = EJBHome.class.getDeclaredMethod("remove", Object.class);
            this.mapMethods(clientMethod, beanMethod);
            clientMethod = EJBObject.class.getDeclaredMethod("remove");
            this.mapMethods(clientMethod, beanMethod);
            clientMethod = EJBLocalObject.class.getDeclaredMethod("remove");
            this.mapMethods(clientMethod, beanMethod);
         }
      } catch (NoSuchMethodException nsme) {
         throw new SystemException(nsme);
      }

      if (this.mdb != null && this.mdb.mdbInterface != null) {
         this.mapObjectInterface(this.getMdb().mdbInterface);
      }

   }

   private void mapHomeInterface(Class intrface) {
      Method[] homeMethods = intrface.getMethods();

      for(Method method : homeMethods) {
         Class owner = method.getDeclaringClass();
         if (owner != EJBHome.class && owner != EJBLocalHome.class) {
            try {
               Method beanMethod = null;
               if (method.getName().startsWith("create")) {
                  StringBuilder ejbCreateName = new StringBuilder(method.getName());
                  ejbCreateName.replace(0, 1, "ejbC");
                  beanMethod = this.beanClass.getMethod(ejbCreateName.toString(), method.getParameterTypes());
                  this.getLegacyView().createMethod = beanMethod;
                  if (this.componentType == BeanType.BMP_ENTITY || this.componentType == BeanType.CMP_ENTITY) {
                     ejbCreateName.insert(3, "Post");
                     Class clazz = this.beanClass;
                     if (this.getCmp().cmpImplClass != null) {
                        clazz = this.getCmp().cmpImplClass;
                     }

                     Method postCreateMethod = clazz.getMethod(ejbCreateName.toString(), method.getParameterTypes());
                     this.getCmp().postCreateMethodMap.put(this.getLegacyView().createMethod, postCreateMethod);
                  }
               } else if (method.getName().startsWith("find")) {
                  if (this.componentType == BeanType.BMP_ENTITY) {
                     String beanMethodName = "ejbF" + method.getName().substring(1);
                     beanMethod = this.beanClass.getMethod(beanMethodName, method.getParameterTypes());
                  }
               } else {
                  String beanMethodName = "ejbHome" + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
                  beanMethod = this.beanClass.getMethod(beanMethodName, method.getParameterTypes());
               }

               if (beanMethod != null) {
                  this.mapMethods(method, beanMethod);
               }
            } catch (NoSuchMethodException var12) {
            }
         }
      }

   }

   public void mapMethods(Method interfaceMethod, Method beanMethod) {
      this.methodMap.put(interfaceMethod, beanMethod);
   }

   private void mapObjectInterface(Class intrface) {
      if (intrface != BusinessLocalHome.class && intrface != BusinessRemoteHome.class && intrface != ServiceEndpoint.class) {
         Method[] interfaceMethods = intrface.getMethods();

         for(Method method : interfaceMethods) {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass != EJBObject.class && declaringClass != EJBLocalObject.class) {
               try {
                  Method beanMethod = this.beanClass.getMethod(method.getName(), method.getParameterTypes());
                  this.mapMethods(method, beanMethod);
               } catch (NoSuchMethodException var9) {
                  throw new OpenEJBRuntimeException("Invalid method [" + method + "]. Not declared by " + this.beanClass.getName() + " class");
               }
            }
         }

      }
   }

   public List<Class> getObjectInterface(Class homeInterface) {
      if (BusinessLocalHome.class.isAssignableFrom(homeInterface)) {
         return this.getBusinessLocalInterfaces();
      } else if (BusinessRemoteHome.class.isAssignableFrom(homeInterface)) {
         return this.getBusinessRemoteInterfaces();
      } else if (EJBLocalHome.class.isAssignableFrom(homeInterface)) {
         List<Class> classes = new ArrayList();
         classes.add(this.getLocalInterface());
         return classes;
      } else if (EJBHome.class.isAssignableFrom(homeInterface)) {
         List<Class> classes = new ArrayList();
         classes.add(this.getRemoteInterface());
         return classes;
      } else {
         throw new IllegalArgumentException("Cannot determine object interface for " + homeInterface);
      }
   }

   public Method getCreateMethod() {
      return this.getLegacyView().createMethod;
   }

   public Method getMatchingPostCreateMethod(Method createMethod) {
      return (Method)this.getCmp().postCreateMethodMap.get(createMethod);
   }

   public boolean isAsynchronous(Method method) {
      Method matchingBeanMethod = this.getMatchingBeanMethod(method);
      Class<?> returnType = matchingBeanMethod.getReturnType();
      if (returnType != Void.TYPE && returnType != Future.class) {
         return false;
      } else if (this.asynchronousClasses.contains(matchingBeanMethod.getDeclaringClass())) {
         return true;
      } else {
         MethodContext methodContext = (MethodContext)this.methodContextMap.get(matchingBeanMethod);
         return methodContext != null && methodContext.isAsynchronous();
      }
   }

   public boolean isCmp2() {
      return this.getCmp().cmp2;
   }

   public void setCmp2(boolean cmp2) {
      this.getCmp().cmp2 = cmp2;
   }

   public String getPrimaryKeyField() {
      return this.getCmp().primaryKeyField;
   }

   public void setPrimaryKeyField(String primaryKeyField) {
      this.getCmp().primaryKeyField = primaryKeyField;
   }

   public KeyGenerator getKeyGenerator() {
      return this.getCmp().keyGenerator;
   }

   public void setKeyGenerator(KeyGenerator keyGenerator) {
      this.getCmp().keyGenerator = keyGenerator;
   }

   public void setRemoteQueryResults(String methodSignature) {
      this.getCmp().remoteQueryResults.add(methodSignature);
   }

   public boolean isRemoteQueryResults(String methodSignature) {
      return this.getCmp().remoteQueryResults.contains(methodSignature);
   }

   public Class getCmpImplClass() {
      return this.getCmp().cmpImplClass;
   }

   public void setCmpImplClass(Class cmpImplClass) {
      this.getCmp().cmpImplClass = cmpImplClass;
   }

   public String getAbstractSchemaName() {
      return this.getCmp().abstractSchemaName;
   }

   public void setAbstractSchemaName(String abstractSchemaName) {
      this.getCmp().abstractSchemaName = abstractSchemaName;
   }

   public Method getEjbTimeout() {
      return this.ejbTimeout;
   }

   public void setEjbTimeout(Method ejbTimeout) {
      this.ejbTimeout = ejbTimeout;
   }

   public EjbTimerService getEjbTimerService() {
      return this.ejbTimerService;
   }

   public void setEjbTimerService(EjbTimerService ejbTimerService) {
      this.ejbTimerService = ejbTimerService;
   }

   public String getEjbName() {
      return this.ejbName;
   }

   public String getModuleID() {
      return this.getModuleContext().getModuleURI().toString();
   }

   public String getModuleName() {
      return this.getModuleContext().getId();
   }

   public String getRunAs() {
      return this.runAs;
   }

   public String getRunAsUser() {
      return this.runAsUser;
   }

   public void setEjbName(String ejbName) {
      this.ejbName = ejbName;
   }

   public void setRunAs(String runAs) {
      this.runAs = runAs;
      if (this.runAsUser == null) {
         this.runAsUser = runAs;
      }

   }

   public void setRunAsUser(String runAsUser) {
      if (runAsUser != null) {
         this.runAsUser = runAsUser;
      }

   }

   public String toString() {
      return "BeanContext(id=" + this.getDeploymentID() + ")";
   }

   public boolean isLoadOnStartup() {
      return this.getSingleton().loadOnStartup;
   }

   public void setLoadOnStartup(boolean loadOnStartup) {
      this.getSingleton().loadOnStartup = loadOnStartup;
   }

   public Set<String> getDependsOn() {
      return this.getSingleton().dependsOn;
   }

   public boolean isSessionSynchronized() {
      return !this.isBeanManagedTransaction();
   }

   public boolean isLocalbean() {
      return this.localbean;
   }

   public Class getBusinessLocalBeanInterface() {
      return this.isLocalbean() ? this.beanClass : null;
   }

   public Duration getAccessTimeout(Method beanMethod) {
      return this.getMethodContext(beanMethod).getAccessTimeout();
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Duration getStatefulTimeout() {
      return this.getStateful().statefulTimeout;
   }

   public void setStatefulTimeout(Duration statefulTimeout) {
      this.getStateful().statefulTimeout = statefulTimeout;
   }

   public Class<Object> getManagedClass() {
      return this.isDynamicallyImplemented() ? this.getProxyClass() : this.beanClass;
   }

   public InstanceContext newInstance() throws Exception {
      ThreadContext callContext = new ThreadContext(this, (Object)null, Operation.INJECTION);
      ThreadContext oldContext = ThreadContext.enter(callContext);
      boolean dynamicallyImplemented = this.isDynamicallyImplemented();
      WebBeansContext webBeansContext = this.getWebBeansContext();
      if (dynamicallyImplemented && !InvocationHandler.class.isAssignableFrom(this.getProxyClass())) {
         throw new OpenEJBException("proxy class can only be InvocationHandler");
      } else {
         InstanceContext var63;
         try {
            Context ctx = this.getJndiEnc();
            Class beanClass = this.getBeanClass();
            CurrentCreationalContext<Object> currentCreationalContext = (CurrentCreationalContext)this.get(CurrentCreationalContext.class);
            CreationalContext<Object> creationalContext = currentCreationalContext != null ? currentCreationalContext.get() : null;
            CdiEjbBean cdiEjbBean = (CdiEjbBean)this.get(CdiEjbBean.class);
            if (!CreationalContextImpl.class.isInstance(creationalContext) && webBeansContext != null) {
               if (creationalContext == null) {
                  creationalContext = webBeansContext.getCreationalContextFactory().getCreationalContext(cdiEjbBean);
               } else {
                  creationalContext = webBeansContext.getCreationalContextFactory().wrappedCreationalContext(creationalContext, cdiEjbBean);
               }
            }

            Object rootInstance;
            if (cdiEjbBean != null && !dynamicallyImplemented && CdiEjbBean.EjbInjectionTargetImpl.class.isInstance(cdiEjbBean.getInjectionTarget())) {
               rootInstance = ((CdiEjbBean.EjbInjectionTargetImpl)CdiEjbBean.EjbInjectionTargetImpl.class.cast(cdiEjbBean.getInjectionTarget())).createNewPojo(creationalContext);
            } else {
               rootInstance = this.getManagedClass().newInstance();
            }

            Object beanInstance;
            if (!dynamicallyImplemented) {
               InjectionProcessor injectionProcessor = new InjectionProcessor(rootInstance, this.getInjections(), InjectionProcessor.unwrap(ctx));
               beanInstance = injectionProcessor.createInstance();
               this.inject(beanInstance, creationalContext);
            } else {
               List<Injection> newInjections = new ArrayList();

               for(Injection injection : this.getInjections()) {
                  if (beanClass.equals(injection.getTarget())) {
                     Injection updated = new Injection(injection.getJndiName(), injection.getName(), this.proxyClass);
                     newInjections.add(updated);
                  } else {
                     newInjections.add(injection);
                  }
               }

               this.injections.clear();
               this.injections.addAll(newInjections);
               InjectionProcessor injectionProcessor = new InjectionProcessor(rootInstance, this.injections, InjectionProcessor.unwrap(ctx));
               InvocationHandler handler = (InvocationHandler)injectionProcessor.createInstance();
               beanInstance = DynamicProxyImplFactory.newProxy(this, handler);
               this.inject(handler, creationalContext);
            }

            Map<String, Object> interceptorInstances = new LinkedHashMap();

            for(InterceptorInstance interceptorInstance : this.getUserAndSystemInterceptors()) {
               Class clazz = interceptorInstance.getData().getInterceptorClass();
               interceptorInstances.put(clazz.getName(), interceptorInstance.getInterceptor());
            }

            Collection<DependentCreationalContext<?>> createdDependents = this.getDependents(creationalContext);

            for(InterceptorData interceptorData : this.getInstanceScopedInterceptors()) {
               if (!interceptorData.getInterceptorClass().equals(beanClass)) {
                  Class clazz = interceptorData.getInterceptorClass();
                  Object iInstance;
                  if (webBeansContext == null) {
                     iInstance = clazz.newInstance();
                  } else {
                     Object preInstantiated = null;
                     if (createdDependents != null) {
                        for(DependentCreationalContext<?> dcc : createdDependents) {
                           if (clazz.isInstance(dcc.getInstance())) {
                              preInstantiated = dcc.getInstance();
                              break;
                           }
                        }
                     }

                     if (preInstantiated != null) {
                        iInstance = preInstantiated;
                     } else {
                        ConstructorInjectionBean interceptorConstructor = (ConstructorInjectionBean)interceptorData.get(ConstructorInjectionBean.class);
                        if (interceptorConstructor == null) {
                           synchronized(this) {
                              interceptorConstructor = (ConstructorInjectionBean)interceptorData.get(ConstructorInjectionBean.class);
                              if (interceptorConstructor == null) {
                                 interceptorConstructor = new ConstructorInjectionBean(webBeansContext, clazz, webBeansContext.getAnnotatedElementFactory().newAnnotatedType(clazz));
                                 interceptorData.set(ConstructorInjectionBean.class, interceptorConstructor);
                              }
                           }
                        }

                        iInstance = interceptorConstructor.create(creationalContext);
                     }
                  }

                  InjectionProcessor interceptorInjector = new InjectionProcessor(iInstance, this.getInjections(), InjectionProcessor.unwrap(ctx));

                  try {
                     Object interceptorInstance = interceptorInjector.createInstance();
                     if (webBeansContext != null) {
                        try {
                           OWBInjector.inject(webBeansContext.getBeanManagerImpl(), interceptorInstance, creationalContext);
                        } catch (Throwable var43) {
                        }
                     }

                     interceptorInstances.put(clazz.getName(), interceptorInstance);
                  } catch (ConstructionException e) {
                     throw new Exception("Failed to create interceptor: " + clazz.getName(), e);
                  }
               }
            }

            interceptorInstances.put(beanClass.getName(), beanInstance);
            callContext.setCurrentOperation(Operation.POST_CONSTRUCT);
            List<InterceptorData> callbackInterceptors = this.getCallbackInterceptors();
            InterceptorStack postConstruct = new InterceptorStack(beanInstance, (Method)null, Operation.POST_CONSTRUCT, callbackInterceptors, interceptorInstances);
            TransactionType transactionType;
            if (this.componentType != BeanType.SINGLETON && this.componentType != BeanType.STATEFUL) {
               transactionType = this.isBeanManagedTransaction() ? TransactionType.BeanManaged : TransactionType.NotSupported;
            } else {
               Set<Method> callbacks = ((InterceptorData)callbackInterceptors.get(callbackInterceptors.size() - 1)).getPostConstruct();
               if (callbacks.isEmpty()) {
                  transactionType = TransactionType.RequiresNew;
               } else {
                  transactionType = this.getTransactionType((Method)callbacks.iterator().next());
                  if (transactionType == TransactionType.Required) {
                     transactionType = TransactionType.RequiresNew;
                  }
               }
            }

            TransactionPolicy transactionPolicy = EjbTransactionUtil.createTransactionPolicy(transactionType, callContext);

            try {
               if (cdiEjbBean != null) {
                  cdiEjbBean.getInjectionTarget().postConstruct(beanInstance);
               }

               postConstruct.invoke();
            } catch (Throwable e) {
               EjbTransactionUtil.handleSystemException(transactionPolicy, e, callContext);
            } finally {
               EjbTransactionUtil.afterInvoke(transactionPolicy, callContext);
            }

            if (cdiEjbBean != null) {
               Class<?> proxyClass = (Class)Class.class.cast(Reflections.get(cdiEjbBean.getInjectionTarget(), "proxyClass"));
               if (proxyClass != null) {
                  InterceptorResolutionService.BeanInterceptorInfo interceptorInfo = (InterceptorResolutionService.BeanInterceptorInfo)cdiEjbBean.getBeanContext().get(InterceptorResolutionService.BeanInterceptorInfo.class);
                  if (interceptorInfo.getDecorators() != null && !interceptorInfo.getDecorators().isEmpty()) {
                     InterceptorDecoratorProxyFactory pf = webBeansContext.getInterceptorDecoratorProxyFactory();
                     Object instance = beanInstance;
                     List<Decorator<?>> decorators = interceptorInfo.getDecorators();
                     Map<Decorator<?>, Object> instances = new HashMap();

                     for(int i = decorators.size(); i > 0; --i) {
                        Decorator<?> decorator = (Decorator)decorators.get(i - 1);
                        ((CreationalContextImpl)CreationalContextImpl.class.cast(creationalContext)).putDelegate(beanInstance);
                        Object decoratorInstance = decorator.create((CreationalContext)CreationalContext.class.cast(creationalContext));
                        instances.put(decorator, decoratorInstance);
                        beanInstance = pf.createProxyInstance(proxyClass, instance, new DecoratorHandler(interceptorInfo, decorators, instances, i - 1, instance, cdiEjbBean.getId()));
                     }
                  }
               }
            }

            var63 = new InstanceContext(this, beanInstance, interceptorInstances, creationalContext);
         } finally {
            ThreadContext.exit(oldContext);
         }

         return var63;
      }
   }

   private Collection<DependentCreationalContext<?>> getDependents(CreationalContext<Object> creationalContext) {
      try {
         return (Collection)Collection.class.cast(DEPENDENTS_OBJECTS.get(creationalContext));
      } catch (Exception var3) {
         return Collections.emptyList();
      }
   }

   private ConstructorInjectionBean<Object> createConstructorInjectionBean(WebBeansContext webBeansContext) {
      if (this.constructorInjectionBean != null) {
         return this.constructorInjectionBean;
      } else {
         synchronized(this) {
            if (this.constructorInjectionBean == null) {
               this.constructorInjectionBean = new ConstructorInjectionBean<Object>(webBeansContext, this.getManagedClass(), webBeansContext.getAnnotatedElementFactory().newAnnotatedType(this.getManagedClass()));
            }
         }

         return this.constructorInjectionBean;
      }
   }

   public <T> void inject(T instance, CreationalContext<T> ctx) {
      WebBeansContext webBeansContext = this.getWebBeansContext();
      if (webBeansContext != null) {
         InjectionTargetBean<T> beanDefinition = (InjectionTargetBean)this.get(CdiEjbBean.class);
         if (beanDefinition == null) {
            beanDefinition = (InjectionTargetBean)InjectionTargetBean.class.cast(this.createConstructorInjectionBean(webBeansContext));
         }

         if (!(ctx instanceof CreationalContextImpl)) {
            ctx = webBeansContext.getCreationalContextFactory().wrappedCreationalContext(ctx, beanDefinition);
         }

         beanDefinition.getInjectionTarget().inject(instance, ctx);
      }
   }

   public WebBeansContext getWebBeansContext() {
      CdiEjbBean<?> bean = (CdiEjbBean)this.get(CdiEjbBean.class);
      return bean != null ? bean.getWebBeansContext() : this.moduleContext.getAppContext().getWebBeansContext();
   }

   public Set<Class<?>> getAsynchronousClasses() {
      return this.asynchronousClasses;
   }

   public Set<String> getAsynchronousMethodSignatures() {
      return this.asynchronousMethodSignatures;
   }

   public void createAsynchronousMethodSet() {
      for(Map.Entry<Method, MethodContext> entry : this.methodContextMap.entrySet()) {
         if (((MethodContext)entry.getValue()).isAsynchronous()) {
            this.asynchronousMethodSignatures.add(this.generateMethodSignature((Method)entry.getKey()));
         }
      }

      for(Class<?> cls : this.asynchronousClasses) {
         for(Method method : cls.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
               this.asynchronousMethodSignatures.add(this.generateMethodSignature(method));
            }
         }
      }

   }

   private String generateMethodSignature(Method method) {
      StringBuilder buffer = new StringBuilder(method.getName());

      for(Class<?> parameterType : method.getParameterTypes()) {
         buffer.append(parameterType.getName());
      }

      return buffer.toString();
   }

   private MethodContext getViewMethodContext(Method method, String view) {
      ViewContext viewContext = (ViewContext)this.viewContextMap.get(view);
      return viewContext == null ? null : viewContext.getMethodContext(method);
   }

   private MethodContext initViewMethodContext(Method method, String view) {
      ViewContext viewContext = (ViewContext)this.viewContextMap.get(view);
      if (viewContext == null) {
         viewContext = new ViewContext();
         this.viewContextMap.put(view, viewContext);
      }

      return viewContext.initMethodContext(method);
   }

   public Class<?> getProxyClass() {
      return this.proxyClass;
   }

   public boolean isCdiCompatible() {
      return this.componentType.isCdiCompatible() && !Comp.class.equals(this.beanClass);
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public boolean isPassivatingScope() {
      CdiEjbBean<?> bean = (CdiEjbBean)this.get(CdiEjbBean.class);
      if (bean == null) {
         return true;
      } else if (ConversationScoped.class == bean.getScope()) {
         try {
            return !bean.getWebBeansContext().getConversationManager().getConversationBeanReference().isTransient();
         } catch (RuntimeException var3) {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean isPassivable() {
      return this.componentType == BeanType.STATEFUL && this.passivable;
   }

   public void stop() {
      if (this.ejbTimerService != null) {
         this.ejbTimerService.stop();
      }

   }

   public void addSecurityRoleReference(String roleName, String roleLink) {
      this.securityRoleReferences.put(roleName, roleLink);
   }

   public String getSecurityRoleReference(String roleName) {
      String roleLink = (String)this.securityRoleReferences.get(roleName);
      return roleLink != null ? roleLink : roleName;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, BeanContext.class);

      try {
         DEPENDENTS_OBJECTS = CreationalContextImpl.class.getDeclaredField("dependentObjects");
         DEPENDENTS_OBJECTS.setAccessible(true);
      } catch (NoSuchFieldException e) {
         throw new IllegalStateException("Invalid OpenWebBeans version", e);
      }
   }

   public static class Comp {
      public static String openejbCompName(String module) {
         return module + "." + Comp.class.getSimpleName();
      }
   }

   public class ViewContext {
      private final Map<Method, MethodContext> methodContextMap = new HashMap();

      public MethodContext getMethodContext(Method method) {
         return (MethodContext)this.methodContextMap.get(method);
      }

      public MethodContext initMethodContext(Method method) {
         MethodContext methodContext = (MethodContext)this.methodContextMap.get(method);
         if (methodContext != null) {
            return methodContext;
         } else {
            methodContext = new MethodContext(BeanContext.this, method);
            this.methodContextMap.put(method, methodContext);
            return methodContext;
         }
      }
   }

   private static class Cmp {
      private boolean cmp2;
      private KeyGenerator keyGenerator;
      private String primaryKeyField;
      private Class cmpImplClass;
      private String abstractSchemaName;
      private Class pkClass;
      private final Set<String> remoteQueryResults;
      private boolean isReentrant;
      private final Map<Method, Method> postCreateMethodMap;

      private Cmp() {
         this.remoteQueryResults = new TreeSet();
         this.postCreateMethodMap = new HashMap();
      }
   }

   private static class Mdb {
      private String destinationId;
      private final Map<String, String> activationProperties;
      private Class mdbInterface;

      private Mdb() {
         this.activationProperties = new HashMap();
      }
   }

   private static class Singleton {
      private LockType lockType;
      private boolean loadOnStartup;
      private final Set<String> dependsOn;

      private Singleton() {
         this.lockType = LockType.WRITE;
         this.dependsOn = new LinkedHashSet();
      }
   }

   private static class Stateful {
      private Index<EntityManagerFactory, EntityManagerConfiguration> extendedEntityManagerFactories;
      private Duration statefulTimeout;
      private final List<Method> removeMethods;

      private Stateful() {
         this.removeMethods = new ArrayList();
      }
   }

   private static class LegacyView {
      private EJBHome ejbHomeRef;
      private EJBLocalHome ejbLocalHomeRef;
      private Class homeInterface;
      private Class remoteInterface;
      private Class localHomeInterface;
      private Class localInterface;
      private Method createMethod;

      private LegacyView() {
      }
   }

   public static final class ProxyClass {
      private final Class<?> proxy;

      public ProxyClass(BeanContext beanContext, Class<?>[] interfaces) {
         Class<?> clazz;
         try {
            clazz = LocalBeanProxyFactory.createProxy(beanContext.getBeanClass(), beanContext.getClassLoader(), interfaces);
         } catch (Throwable e) {
            BeanContext.logger.debug(beanContext.getBeanClass().getName() + " is not proxiable", e);
            clazz = null;
         }

         this.proxy = clazz;
      }

      public Class<?> getProxy() {
         return this.proxy;
      }
   }

   public static class EntityManagerConfiguration {
      private final Map properties;
      private final SynchronizationType synchronizationType;

      public EntityManagerConfiguration(Map properties, SynchronizationType synchronizationType) {
         this.properties = properties;
         this.synchronizationType = synchronizationType;
      }

      public Map getProperties() {
         return this.properties;
      }

      public SynchronizationType getSynchronizationType() {
         return this.synchronizationType;
      }
   }

   public interface BusinessLocalBeanHome extends EJBLocalHome {
      Object create();
   }

   public interface BusinessLocalHome extends EJBLocalHome {
      Object create();
   }

   public interface BusinessRemoteHome extends EJBHome {
      Object create();
   }

   public interface Removable {
      void $$remove();
   }

   public interface ServiceEndpoint {
   }

   public interface Timeout {
   }
}
