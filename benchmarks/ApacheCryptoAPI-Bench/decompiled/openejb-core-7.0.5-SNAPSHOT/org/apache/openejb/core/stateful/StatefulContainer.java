package org.apache.openejb.core.stateful;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.dgc.VMID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.ejb.ConcurrentAccessTimeoutException;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.enterprise.context.Dependent;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.security.auth.login.LoginException;
import javax.transaction.Transaction;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.ContainerType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.InvalidateReferenceException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.SystemException;
import org.apache.openejb.cdi.CdiEjbBean;
import org.apache.openejb.cdi.CurrentCreationalContext;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.InstanceContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.security.AbstractSecurityService;
import org.apache.openejb.core.transaction.BeanTransactionPolicy;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.EjbUserTransaction;
import org.apache.openejb.core.transaction.JtaTransactionPolicy;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ManagedMBean;
import org.apache.openejb.monitoring.ObjectNameBuilder;
import org.apache.openejb.monitoring.StatsInterceptor;
import org.apache.openejb.persistence.EntityManagerAlreadyRegisteredException;
import org.apache.openejb.persistence.JtaEntityManagerRegistry;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Index;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class StatefulContainer implements RpcContainer {
   private static final Logger logger;
   private final Object containerID;
   private final SecurityService securityService;
   private final Duration accessTimeout;
   protected final JtaEntityManagerRegistry entityManagerRegistry;
   protected final Map<Object, BeanContext> deploymentsById;
   protected final Cache<Object, Instance> cache;
   protected final LockFactory lockFactory;
   private final ConcurrentMap<Object, Instance> checkedOutInstances;
   private final SessionContext sessionContext;
   private final boolean preventExtendedEntityManagerSerialization;

   public StatefulContainer(Object id, SecurityService securityService, Cache<Object, Instance> cache) {
      this(id, securityService, cache, new Duration(-1L, TimeUnit.MILLISECONDS), true, new DefaultLockFactory());
   }

   public StatefulContainer(Object id, SecurityService securityService, Cache<Object, Instance> cache, Duration accessTimeout, boolean preventExtendedEntityManagerSerialization, LockFactory lockFactory) {
      this.entityManagerRegistry = (JtaEntityManagerRegistry)SystemInstance.get().getComponent(JtaEntityManagerRegistry.class);
      this.deploymentsById = new HashMap();
      this.checkedOutInstances = new ConcurrentHashMap();
      this.containerID = id;
      this.securityService = securityService;
      this.cache = cache;
      cache.setListener(new StatefulCacheListener());
      this.accessTimeout = accessTimeout;
      this.sessionContext = new StatefulContext(this.securityService, new StatefulUserTransaction(new EjbUserTransaction(), this.entityManagerRegistry));
      this.preventExtendedEntityManagerSerialization = preventExtendedEntityManagerSerialization;
      this.lockFactory = lockFactory;
      this.lockFactory.setContainer(this);
   }

   private Map<Method, MethodType> getLifecycleMethodsOfInterface(BeanContext beanContext) {
      Map<Method, MethodType> methods = new HashMap();

      try {
         methods.put(BeanContext.Removable.class.getDeclaredMethod("$$remove"), StatefulContainer.MethodType.REMOVE);
      } catch (NoSuchMethodException e) {
         throw new IllegalStateException("Internal code change: BeanContext.Removable.$$remove() method was deleted", e);
      }

      for(Method removeMethod : beanContext.getRemoveMethods()) {
         methods.put(removeMethod, StatefulContainer.MethodType.REMOVE);

         for(Class businessLocal : beanContext.getBusinessLocalInterfaces()) {
            try {
               Method method = businessLocal.getMethod(removeMethod.getName(), removeMethod.getParameterTypes());
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            } catch (NoSuchMethodException var18) {
            }
         }

         for(Class businessRemote : beanContext.getBusinessRemoteInterfaces()) {
            try {
               Method method = businessRemote.getMethod(removeMethod.getName(), removeMethod.getParameterTypes());
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            } catch (NoSuchMethodException var17) {
            }
         }
      }

      Class legacyRemote = beanContext.getRemoteInterface();
      if (legacyRemote != null) {
         try {
            Method method = legacyRemote.getMethod("remove");
            methods.put(method, StatefulContainer.MethodType.REMOVE);
         } catch (NoSuchMethodException var16) {
         }
      }

      Class legacyLocal = beanContext.getLocalInterface();
      if (legacyLocal != null) {
         try {
            Method method = legacyLocal.getMethod("remove");
            methods.put(method, StatefulContainer.MethodType.REMOVE);
         } catch (NoSuchMethodException var15) {
         }
      }

      Class businessLocalHomeInterface = beanContext.getBusinessLocalInterface();
      if (businessLocalHomeInterface != null) {
         for(Method method : BeanContext.BusinessLocalHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, StatefulContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            }
         }
      }

      Class businessLocalBeanHomeInterface = beanContext.getBusinessLocalBeanInterface();
      if (businessLocalBeanHomeInterface != null) {
         for(Method method : BeanContext.BusinessLocalBeanHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, StatefulContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            }
         }
      }

      Class businessRemoteHomeInterface = beanContext.getBusinessRemoteInterface();
      if (businessRemoteHomeInterface != null) {
         for(Method method : BeanContext.BusinessRemoteHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, StatefulContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            }
         }
      }

      Class homeInterface = beanContext.getHomeInterface();
      if (homeInterface != null) {
         for(Method method : homeInterface.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, StatefulContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            }
         }
      }

      Class localHomeInterface = beanContext.getLocalHomeInterface();
      if (localHomeInterface != null) {
         for(Method method : localHomeInterface.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, StatefulContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, StatefulContainer.MethodType.REMOVE);
            }
         }
      }

      return methods;
   }

   public LockFactory getLockFactory() {
      return this.lockFactory;
   }

   public Cache<Object, Instance> getCache() {
      return this.cache;
   }

   public ContainerType getContainerType() {
      return ContainerType.STATEFUL;
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public synchronized BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deploymentsById.values().toArray(new BeanContext[this.deploymentsById.size()]);
   }

   public synchronized BeanContext getBeanContext(Object deploymentID) {
      return (BeanContext)this.deploymentsById.get(deploymentID);
   }

   public void start(BeanContext beanContext) throws OpenEJBException {
   }

   public void stop(BeanContext beanContext) throws OpenEJBException {
      beanContext.stop();
   }

   public synchronized void undeploy(BeanContext beanContext) throws OpenEJBException {
      Data data = (Data)beanContext.getContainerData();
      MBeanServer server = LocalMBeanServer.get();

      for(ObjectName objectName : data.jmxNames) {
         try {
            server.unregisterMBean(objectName);
         } catch (Exception var7) {
            logger.error("Unable to unregister MBean " + objectName);
         }
      }

      this.deploymentsById.remove(beanContext.getDeploymentID());
      beanContext.setContainer((Container)null);
      beanContext.setContainerData((Object)null);
      if (this.isPassivable(beanContext)) {
         this.cache.removeAll(new BeanContextFilter(beanContext.getId()));
      }

   }

   public synchronized void deploy(BeanContext beanContext) throws OpenEJBException {
      Map<Method, MethodType> methods = this.getLifecycleMethodsOfInterface(beanContext);
      this.deploymentsById.put(beanContext.getDeploymentID(), beanContext);
      beanContext.setContainer(this);
      Data data = new Data(new Index(methods));
      beanContext.setContainerData(data);
      if (StatsInterceptor.isStatsActivated()) {
         StatsInterceptor stats = new StatsInterceptor(beanContext.getBeanClass());
         beanContext.addFirstSystemInterceptor(stats);
         MBeanServer server = LocalMBeanServer.get();
         ObjectNameBuilder jmxName = new ObjectNameBuilder("openejb.management");
         jmxName.set("J2EEServer", "openejb");
         jmxName.set("J2EEApplication", (String)null);
         jmxName.set("EJBModule", beanContext.getModuleID());
         jmxName.set("StatefulSessionBean", beanContext.getEjbName());
         jmxName.set("j2eeType", "");
         jmxName.set("name", beanContext.getEjbName());

         try {
            ObjectName objectName = jmxName.set("j2eeType", "Invocations").build();
            if (server.isRegistered(objectName)) {
               server.unregisterMBean(objectName);
            }

            server.registerMBean(new ManagedMBean(stats), objectName);
            data.jmxNames.add(objectName);
         } catch (Exception e) {
            logger.error("Unable to register MBean ", (Throwable)e);
         }
      }

      try {
         Context context = beanContext.getJndiEnc();
         context.bind("comp/EJBContext", this.sessionContext);
      } catch (NamingException e) {
         throw new OpenEJBException("Failed to bind EJBContext", e);
      }

      beanContext.set(EJBContext.class, this.sessionContext);
   }

   public Object invoke(Object deployID, InterfaceType type, Class callInterface, Method callMethod, Object[] args, Object primKey) throws OpenEJBException {
      BeanContext beanContext = this.getBeanContext(deployID);
      if (beanContext == null) {
         throw new OpenEJBException("Deployment does not exist in this container. Deployment(id='" + deployID + "'), Container(id='" + this.containerID + "')");
      } else {
         if (type == null) {
            type = beanContext.getInterfaceType(callInterface);
         }

         Data data = (Data)beanContext.getContainerData();
         MethodType methodType = (MethodType)data.getMethodIndex().get(callMethod);
         methodType = methodType != null ? methodType : StatefulContainer.MethodType.BUSINESS;
         switch (methodType) {
            case CREATE:
               return this.createEJBObject(beanContext, callMethod, args, type);
            case REMOVE:
               return this.removeEJBObject(beanContext, primKey, callInterface, callMethod, args, type);
            default:
               return this.businessMethod(beanContext, primKey, callInterface, callMethod, args, type);
         }
      }
   }

   private boolean isPassivable(BeanContext beanContext) {
      if (this.preventExtendedEntityManagerSerialization) {
         return true;
      } else {
         Index<EntityManagerFactory, BeanContext.EntityManagerConfiguration> factories = beanContext.getExtendedEntityManagerFactories();
         return (factories == null || factories.size() <= 0) && beanContext.isPassivable();
      }
   }

   protected ProxyInfo createEJBObject(BeanContext beanContext, Method callMethod, Object[] args, InterfaceType interfaceType) throws OpenEJBException {
      Object primaryKey = this.newPrimaryKey();
      ThreadContext createContext = new ThreadContext(beanContext, primaryKey);
      ThreadContext oldCallContext = ThreadContext.enter(createContext);
      Object runAs = null;

      ProxyInfo e;
      try {
         if (oldCallContext != null) {
            BeanContext oldBc = oldCallContext.getBeanContext();
            if (oldBc.getRunAsUser() != null || oldBc.getRunAs() != null) {
               runAs = ((AbstractSecurityService)AbstractSecurityService.class.cast(this.securityService)).overrideWithRunAsContext(createContext, beanContext, oldBc);
            }
         }

         this.checkAuthorization(callMethod, interfaceType);
         Index<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> entityManagers = this.createEntityManagers(beanContext);
         if (entityManagers != null) {
            try {
               this.entityManagerRegistry.addEntityManagers((String)beanContext.getDeploymentID(), primaryKey, entityManagers);
            } catch (EntityManagerAlreadyRegisteredException e) {
               throw new EJBException(e);
            }
         }

         createContext.setCurrentOperation(Operation.CREATE);
         createContext.setCurrentAllowedStates((BaseContext.State[])null);
         TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(createContext.getBeanContext().getTransactionType(callMethod, interfaceType), createContext);
         Instance instance = null;

         try {
            try {
               InstanceContext context = beanContext.newInstance();
               instance = new Instance(beanContext, primaryKey, this.containerID, context.getBean(), context.getCreationalContext(), context.getInterceptors(), entityManagers, this.lockFactory.newLock(primaryKey.toString()));
            } catch (Throwable var31) {
               Throwable throwable = var31;
               ThreadContext callContext = ThreadContext.getThreadContext();
               EjbTransactionUtil.handleSystemException(callContext.getTransactionPolicy(), var31, callContext);
               throw new IllegalStateException(var31);
            }

            if (this.isPassivable(beanContext)) {
               this.cache.add(primaryKey, instance);
            }

            this.checkedOutInstances.put(primaryKey, instance);
            this.registerSessionSynchronization(instance, createContext);
            if (!callMethod.getDeclaringClass().equals(BeanContext.BusinessLocalHome.class) && !callMethod.getDeclaringClass().equals(BeanContext.BusinessRemoteHome.class) && !callMethod.getDeclaringClass().equals(BeanContext.BusinessLocalBeanHome.class)) {
               Method createOrInit = beanContext.getMatchingBeanMethod(callMethod);
               createContext.set(Method.class, createOrInit);
               InterceptorStack interceptorStack = new InterceptorStack(instance.bean, createOrInit, Operation.CREATE, new ArrayList(), new HashMap());
               if (args == null) {
                  interceptorStack.invoke();
               } else {
                  interceptorStack.invoke(args);
               }
            }
         } catch (Throwable e) {
            this.handleException(createContext, txPolicy, e);
         } finally {
            this.unregisterEntityManagers(instance, createContext);
            this.afterInvoke(createContext, txPolicy, instance);
         }

         e = new ProxyInfo(beanContext, primaryKey);
      } finally {
         if (runAs != null) {
            try {
               this.securityService.associate(runAs);
            } catch (LoginException var30) {
            }
         }

         ThreadContext.exit(oldCallContext);
      }

      return e;
   }

   protected Object newPrimaryKey() {
      return new VMID();
   }

   protected Object removeEJBObject(BeanContext beanContext, Object primKey, Class callInterface, Method callMethod, Object[] args, InterfaceType interfaceType) throws OpenEJBException {
      if (primKey == null) {
         throw new NullPointerException("primKey is null");
      } else {
         CdiEjbBean cdiEjbBean = (CdiEjbBean)beanContext.get(CdiEjbBean.class);
         if (cdiEjbBean != null) {
            Class scope = cdiEjbBean.getScope();
            if (callMethod.getDeclaringClass() != BeanContext.Removable.class && scope != Dependent.class) {
               throw new UnsupportedOperationException("Can not call EJB Stateful Bean Remove Method without scoped @Dependent.  Found scope: @" + scope.getSimpleName());
            }
         }

         boolean internalRemove = BeanContext.Removable.class == callMethod.getDeclaringClass();
         ThreadContext callContext = new ThreadContext(beanContext, primKey);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);
         Object runAs = null;

         Object unregisteredEntityManagers;
         try {
            if (oldCallContext != null) {
               BeanContext oldBc = oldCallContext.getBeanContext();
               if (oldBc.getRunAsUser() != null || oldBc.getRunAs() != null) {
                  runAs = ((AbstractSecurityService)AbstractSecurityService.class.cast(this.securityService)).overrideWithRunAsContext(callContext, beanContext, oldBc);
               }
            }

            if (!internalRemove) {
               this.checkAuthorization(callMethod, interfaceType);
            }

            if (interfaceType.isComponent()) {
               Instance instance = (Instance)this.checkedOutInstances.get(primKey);
               if (instance != null && instance.bean instanceof SessionBean) {
                  throw new ApplicationException(new RemoveException("A stateful EJB enrolled in a transaction can not be removed"));
               }
            }

            TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(callContext.getBeanContext().getTransactionType(callMethod, interfaceType), callContext);
            Object returnValue = null;
            boolean retain = false;
            Instance instance = null;
            Method runMethod = null;

            try {
               instance = this.obtainInstance(primKey, callContext, callMethod, beanContext.isPassivatingScope());
               if (txPolicy instanceof BeanTransactionPolicy) {
                  BeanTransactionPolicy.SuspendedTransaction suspendedTransaction = instance.getBeanTransaction();
                  if (suspendedTransaction != null) {
                     instance.setBeanTransaction((BeanTransactionPolicy.SuspendedTransaction)null);
                     BeanTransactionPolicy beanTxEnv = (BeanTransactionPolicy)txPolicy;
                     beanTxEnv.resumeUserTransaction(suspendedTransaction);
                  }
               }

               if (!internalRemove) {
                  this.registerEntityManagers(instance, callContext);
                  this.registerSessionSynchronization(instance, callContext);
                  callContext.setCurrentOperation(Operation.REMOVE);
                  callContext.setCurrentAllowedStates((BaseContext.State[])null);
                  callContext.setInvokedInterface(callInterface);
                  runMethod = beanContext.getMatchingBeanMethod(callMethod);
                  callContext.set(Method.class, runMethod);
                  Class<?> declaringClass = callMethod.getDeclaringClass();
                  if (declaringClass.equals(EJBHome.class) || declaringClass.equals(EJBLocalHome.class)) {
                     args = new Object[0];
                  }

                  List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
                  InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, Operation.REMOVE, interceptors, instance.interceptors);
                  CdiEjbBean<Object> bean = (CdiEjbBean)beanContext.get(CdiEjbBean.class);
                  if (bean != null) {
                     bean.getInjectionTarget().preDestroy(instance.bean);
                  }

                  if (args == null) {
                     returnValue = interceptorStack.invoke();
                  } else {
                     returnValue = interceptorStack.invoke(args);
                  }
               }
            } catch (InvalidateReferenceException var102) {
               InvalidateReferenceException e = var102;
               throw new ApplicationException(var102.getRootCause());
            } catch (Throwable var103) {
               Throwable e = var103;
               if (interfaceType.isBusiness()) {
                  retain = beanContext.retainIfExeption(runMethod);
                  this.handleException(callContext, txPolicy, var103);
               } else {
                  try {
                     this.handleException(callContext, txPolicy, e);
                  } catch (ApplicationException var99) {
                  }
               }
            } finally {
               if (runAs != null) {
                  try {
                     this.securityService.associate(runAs);
                  } catch (LoginException var98) {
                  }
               }

               if (!retain) {
                  try {
                     callContext.setCurrentOperation(Operation.PRE_DESTROY);
                     List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
                     if (instance != null) {
                        InterceptorStack interceptorStack = new InterceptorStack(instance.bean, (Method)null, Operation.PRE_DESTROY, callbackInterceptors, instance.interceptors);
                        interceptorStack.invoke();
                     }
                  } catch (Throwable t) {
                     String logMessage = "An unexpected exception occured while invoking the preDestroy method on the Stateful SessionBean instance: " + (null != instance ? instance.bean.getClass().getName() : beanContext.getBeanClass().getName());
                     logger.error(logMessage, t);
                  } finally {
                     callContext.setCurrentOperation(Operation.REMOVE);
                  }

                  this.discardInstance(primKey, instance);
               }

               Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> unregisteredEntityManagers = this.unregisterEntityManagers(instance, callContext);
               this.afterInvoke(callContext, txPolicy, instance);
               this.closeEntityManagers(unregisteredEntityManagers);
            }

            unregisteredEntityManagers = returnValue;
         } finally {
            ThreadContext.exit(oldCallContext);
         }

         return unregisteredEntityManagers;
      }
   }

   protected Object businessMethod(BeanContext beanContext, Object primKey, Class callInterface, Method callMethod, Object[] args, InterfaceType interfaceType) throws OpenEJBException {
      ThreadContext callContext = new ThreadContext(beanContext, primKey);
      ThreadContext oldCallContext = ThreadContext.enter(callContext);
      CurrentCreationalContext currentCreationalContext = (CurrentCreationalContext)beanContext.get(CurrentCreationalContext.class);
      Object runAs = null;

      Object e;
      try {
         if (oldCallContext != null) {
            BeanContext oldBc = oldCallContext.getBeanContext();
            if (oldBc.getRunAsUser() != null || oldBc.getRunAs() != null) {
               runAs = ((AbstractSecurityService)AbstractSecurityService.class.cast(this.securityService)).overrideWithRunAsContext(callContext, beanContext, oldBc);
            }
         }

         this.checkAuthorization(callMethod, interfaceType);
         TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(callContext.getBeanContext().getTransactionType(callMethod, interfaceType), callContext);
         Object returnValue = null;
         Instance instance = null;

         try {
            instance = this.obtainInstance(primKey, callContext, callMethod, true);
            if (txPolicy instanceof BeanTransactionPolicy) {
               BeanTransactionPolicy.SuspendedTransaction suspendedTransaction = instance.getBeanTransaction();
               if (suspendedTransaction != null) {
                  instance.setBeanTransaction((BeanTransactionPolicy.SuspendedTransaction)null);
                  BeanTransactionPolicy beanTxEnv = (BeanTransactionPolicy)txPolicy;
                  beanTxEnv.resumeUserTransaction(suspendedTransaction);
               }
            }

            this.registerEntityManagers(instance, callContext);
            this.registerSessionSynchronization(instance, callContext);
            callContext.setCurrentOperation(Operation.BUSINESS);
            callContext.setCurrentAllowedStates((BaseContext.State[])null);
            callContext.setInvokedInterface(callInterface);
            Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
            callContext.set(Method.class, runMethod);
            if (currentCreationalContext != null) {
               currentCreationalContext.set(instance.creationalContext);
            }

            List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, Operation.BUSINESS, interceptors, instance.interceptors);
            returnValue = interceptorStack.invoke(args);
         } catch (Throwable e) {
            this.handleException(callContext, txPolicy, e);
         } finally {
            this.unregisterEntityManagers(instance, callContext);
            this.afterInvoke(callContext, txPolicy, instance);
         }

         e = returnValue;
      } finally {
         if (runAs != null) {
            try {
               this.securityService.associate(runAs);
            } catch (LoginException var29) {
            }
         }

         ThreadContext.exit(oldCallContext);
         if (currentCreationalContext != null) {
            currentCreationalContext.remove();
         }

      }

      return e;
   }

   private Instance obtainInstance(Object primaryKey, ThreadContext callContext, Method callMethod, boolean checkOutIfNecessary) throws OpenEJBException {
      if (primaryKey == null) {
         throw new SystemException(new NullPointerException("Cannot obtain an instance of the stateful session bean with a null session id"));
      } else {
         Transaction currentTransaction = this.getTransaction(callContext);
         Instance instance;
         synchronized(this) {
            instance = (Instance)this.checkedOutInstances.get(primaryKey);
            if (instance == null) {
               try {
                  instance = this.cache.checkOut(primaryKey, checkOutIfNecessary);
               } catch (OpenEJBException e) {
                  throw e;
               } catch (Exception e) {
                  throw new SystemException("Unexpected load exception", e);
               }

               if (instance == null) {
                  throw new InvalidateReferenceException(new NoSuchObjectException("Not Found"));
               }

               this.checkedOutInstances.put(primaryKey, instance);
            }
         }

         Duration accessTimeout = this.getAccessTimeout(instance.beanContext, callMethod);
         LockFactory.StatefulLock currLock = instance.getLock();
         boolean lockAcquired;
         if (accessTimeout != null && accessTimeout.getTime() >= 0L) {
            if (accessTimeout.getTime() == 0L) {
               lockAcquired = currLock.tryLock();
            } else {
               try {
                  lockAcquired = currLock.tryLock(accessTimeout.getTime(), accessTimeout.getUnit());
               } catch (InterruptedException e) {
                  throw new ApplicationException("Unable to get lock.", e);
               }
            }
         } else {
            currLock.lock();
            lockAcquired = true;
         }

         if (!lockAcquired) {
            throw new ApplicationException(new ConcurrentAccessTimeoutException("Unable to get lock."));
         } else {
            if (instance.getTransaction() != null) {
               if (!instance.getTransaction().equals(currentTransaction) && !instance.getLock().tryLock()) {
                  throw new ApplicationException(new RemoteException("Instance is in a transaction and cannot be invoked outside that transaction.  See EJB 3.0 Section 4.4.4"));
               }
            } else {
               instance.setTransaction(currentTransaction);
            }

            instance.setInUse(true);
            return instance;
         }
      }
   }

   private Duration getAccessTimeout(BeanContext beanContext, Method callMethod) {
      callMethod = beanContext.getMatchingBeanMethod(callMethod);
      Duration accessTimeout = beanContext.getAccessTimeout(callMethod);
      if (accessTimeout == null) {
         accessTimeout = beanContext.getAccessTimeout();
         if (accessTimeout == null) {
            accessTimeout = this.accessTimeout;
         }
      }

      return accessTimeout;
   }

   private Transaction getTransaction(ThreadContext callContext) {
      TransactionPolicy policy = callContext.getTransactionPolicy();
      Transaction currentTransaction = null;
      if (policy instanceof JtaTransactionPolicy) {
         JtaTransactionPolicy jtaPolicy = (JtaTransactionPolicy)policy;
         currentTransaction = jtaPolicy.getCurrentTransaction();
      }

      return currentTransaction;
   }

   private void releaseInstance(Instance instance) {
      if (!instance.beanContext.isDestroyed()) {
         instance.setInUse(false);
         if (instance.getTransaction() == null && this.isPassivable(instance.beanContext) && null == instance.getBeanTransaction()) {
            synchronized(instance.primaryKey) {
               this.cache.checkIn(instance.primaryKey);
               this.checkedOutInstances.remove(instance.primaryKey);
            }
         }

      }
   }

   private void discardInstance(Object primaryKey, Instance instance) {
      if (primaryKey != null) {
         Instance i;
         if (instance == null) {
            i = (Instance)this.checkedOutInstances.remove(primaryKey);
         } else {
            this.checkedOutInstances.remove(primaryKey);
            i = instance;
         }

         if (i != null) {
            if (this.isPassivable(i.beanContext)) {
               this.cache.remove(primaryKey);
            }

            if (null != i.creationalContext) {
               i.creationalContext.release();
            }
         }

      }
   }

   private void checkAuthorization(Method callMethod, InterfaceType interfaceType) throws ApplicationException {
      boolean authorized = this.securityService.isCallerAuthorized(callMethod, interfaceType);
      if (!authorized) {
         throw new ApplicationException(new EJBAccessException("Unauthorized Access by Principal Denied"));
      }
   }

   private void handleException(ThreadContext callContext, TransactionPolicy txPolicy, Throwable e) throws ApplicationException {
      if (e instanceof ApplicationException) {
         throw (ApplicationException)e;
      } else {
         ExceptionType type = callContext.getBeanContext().getExceptionType(e);
         if (type == ExceptionType.SYSTEM) {
            this.discardInstance(callContext.getPrimaryKey(), (Instance)null);
            EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
         }

      }
   }

   private void afterInvoke(ThreadContext callContext, TransactionPolicy txPolicy, Instance instance) throws OpenEJBException {
      try {
         if (instance != null && txPolicy instanceof BeanTransactionPolicy) {
            BeanTransactionPolicy.SuspendedTransaction suspendedTransaction = null;

            try {
               BeanTransactionPolicy beanTxEnv = (BeanTransactionPolicy)txPolicy;
               suspendedTransaction = beanTxEnv.suspendUserTransaction();
            } catch (SystemException e) {
               EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
            } finally {
               instance.setBeanTransaction(suspendedTransaction);
            }
         }
      } finally {
         if (instance != null) {
            instance.setInUse(false);
         }

         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
         if (instance != null) {
            instance.releaseLock();
         }

      }

   }

   private Index<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> createEntityManagers(BeanContext beanContext) {
      Index<EntityManagerFactory, BeanContext.EntityManagerConfiguration> factories = beanContext.getExtendedEntityManagerFactories();
      Index<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> entityManagers = null;
      if (factories != null && factories.size() > 0) {
         entityManagers = new Index<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker>(new ArrayList(factories.keySet()));

         for(Map.Entry<EntityManagerFactory, BeanContext.EntityManagerConfiguration> entry : factories.entrySet()) {
            EntityManagerFactory entityManagerFactory = (EntityManagerFactory)entry.getKey();
            JtaEntityManagerRegistry.EntityManagerTracker entityManagerTracker = this.entityManagerRegistry.getInheritedEntityManager(entityManagerFactory);
            if (entityManagerTracker == null) {
               Map properties = ((BeanContext.EntityManagerConfiguration)entry.getValue()).getProperties();
               SynchronizationType synchronizationType = ((BeanContext.EntityManagerConfiguration)entry.getValue()).getSynchronizationType();
               EntityManager entityManager;
               if (synchronizationType != null) {
                  if (properties != null) {
                     entityManager = entityManagerFactory.createEntityManager(synchronizationType, properties);
                  } else {
                     entityManager = entityManagerFactory.createEntityManager(synchronizationType);
                  }
               } else if (properties != null) {
                  entityManager = entityManagerFactory.createEntityManager(properties);
               } else {
                  entityManager = entityManagerFactory.createEntityManager();
               }

               entityManagerTracker = new JtaEntityManagerRegistry.EntityManagerTracker(entityManager, synchronizationType != SynchronizationType.UNSYNCHRONIZED);
            } else {
               entityManagerTracker.incCounter();
            }

            entityManagers.put(entityManagerFactory, entityManagerTracker);
         }
      }

      return entityManagers;
   }

   private void registerEntityManagers(Instance instance, ThreadContext callContext) throws OpenEJBException {
      if (this.entityManagerRegistry != null) {
         BeanContext beanContext = callContext.getBeanContext();
         Index<EntityManagerFactory, BeanContext.EntityManagerConfiguration> factories = beanContext.getExtendedEntityManagerFactories();
         if (factories != null) {
            Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> entityManagers = instance.getEntityManagers(factories);
            if (entityManagers != null) {
               try {
                  this.entityManagerRegistry.addEntityManagers((String)beanContext.getDeploymentID(), instance.primaryKey, entityManagers);
               } catch (EntityManagerAlreadyRegisteredException e) {
                  throw new EJBException(e);
               }
            }
         }
      }
   }

   private Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> unregisterEntityManagers(Instance instance, ThreadContext callContext) {
      if (this.entityManagerRegistry == null) {
         return null;
      } else if (instance == null) {
         return null;
      } else {
         BeanContext beanContext = callContext.getBeanContext();
         return this.entityManagerRegistry.removeEntityManagers((String)beanContext.getDeploymentID(), instance.primaryKey);
      }
   }

   private void closeEntityManagers(Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> unregisteredEntityManagers) {
      if (unregisteredEntityManagers != null) {
         for(JtaEntityManagerRegistry.EntityManagerTracker entityManagerTracker : unregisteredEntityManagers.values()) {
            if (entityManagerTracker.decCounter() == 0) {
               entityManagerTracker.getEntityManager().close();
            }
         }

      }
   }

   private void registerSessionSynchronization(Instance instance, ThreadContext callContext) {
      TransactionPolicy txPolicy = callContext.getTransactionPolicy();
      if (txPolicy == null) {
         throw new IllegalStateException("ThreadContext does not contain a TransactionEnvironment");
      } else {
         SessionSynchronizationCoordinator coordinator = (SessionSynchronizationCoordinator)txPolicy.getResource(SessionSynchronizationCoordinator.class);
         if (coordinator == null) {
            coordinator = new SessionSynchronizationCoordinator(txPolicy);
            txPolicy.registerSynchronization(coordinator);
            txPolicy.putResource(SessionSynchronizationCoordinator.class, coordinator);
         }

         boolean synchronize = callContext.getCurrentOperation() != Operation.CREATE && callContext.getBeanContext().isSessionSynchronized() && txPolicy.isTransactionActive();
         coordinator.registerSessionSynchronization(instance, callContext.getBeanContext(), callContext.getPrimaryKey(), synchronize);
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }

   public static enum MethodType {
      CREATE,
      REMOVE,
      BUSINESS;
   }

   private final class SessionSynchronizationCoordinator implements TransactionPolicy.TransactionSynchronization {
      private final Map<Object, Synchronization> registry;
      private final TransactionPolicy txPolicy;

      private SessionSynchronizationCoordinator(TransactionPolicy txPolicy) {
         this.registry = new HashMap();
         this.txPolicy = txPolicy;
      }

      private void registerSessionSynchronization(Instance instance, BeanContext beanContext, Object primaryKey, boolean synchronize) {
         Synchronization synchronization = (Synchronization)this.registry.get(primaryKey);
         if (synchronization == null) {
            synchronization = new Synchronization(instance);
            this.registry.put(primaryKey, synchronization);
         }

         boolean wasSynchronized = synchronization.setCallSessionSynchronization(synchronize);
         if (!wasSynchronized && synchronize) {
            ThreadContext callContext = new ThreadContext(instance.beanContext, instance.primaryKey, Operation.AFTER_BEGIN);
            callContext.setCurrentAllowedStates((BaseContext.State[])null);
            ThreadContext oldCallContext = ThreadContext.enter(callContext);

            try {
               List<InterceptorData> interceptors = beanContext.getCallbackInterceptors();
               InterceptorStack interceptorStack = new InterceptorStack(instance.bean, (Method)null, Operation.AFTER_BEGIN, interceptors, instance.interceptors);
               interceptorStack.invoke();
            } catch (Exception e) {
               String message = "An unexpected system exception occured while invoking the afterBegin method on the SessionSynchronization object";
               StatefulContainer.logger.error("An unexpected system exception occured while invoking the afterBegin method on the SessionSynchronization object", (Throwable)e);
               throw new OpenEJBRuntimeException("An unexpected system exception occured while invoking the afterBegin method on the SessionSynchronization object", e);
            } finally {
               ThreadContext.exit(oldCallContext);
            }

         }
      }

      public void beforeCompletion() {
         for(Synchronization synchronization : this.registry.values()) {
            Instance instance = synchronization.instance;
            if (this.txPolicy.isRollbackOnly()) {
               return;
            }

            if (synchronization.isCallSessionSynchronization()) {
               ThreadContext callContext = new ThreadContext(instance.beanContext, instance.primaryKey, Operation.BEFORE_COMPLETION);
               callContext.setCurrentAllowedStates((BaseContext.State[])null);
               ThreadContext oldCallContext = ThreadContext.enter(callContext);

               try {
                  instance.setInUse(true);
                  BeanContext beanContext = instance.beanContext;
                  List<InterceptorData> interceptors = beanContext.getCallbackInterceptors();
                  InterceptorStack interceptorStack = new InterceptorStack(instance.bean, (Method)null, Operation.BEFORE_COMPLETION, interceptors, instance.interceptors);
                  interceptorStack.invoke();
                  instance.setInUse(false);
               } catch (InvalidateReferenceException var13) {
               } catch (Exception e) {
                  String message = "An unexpected system exception occured while invoking the beforeCompletion method on the SessionSynchronization object";
                  StatefulContainer.logger.error("An unexpected system exception occured while invoking the beforeCompletion method on the SessionSynchronization object", (Throwable)e);
                  this.txPolicy.setRollbackOnly(e);
                  StatefulContainer.this.discardInstance(callContext.getPrimaryKey(), instance);
                  throw new OpenEJBRuntimeException("An unexpected system exception occured while invoking the beforeCompletion method on the SessionSynchronization object", e);
               } finally {
                  ThreadContext.exit(oldCallContext);
               }
            }
         }

      }

      public void afterCompletion(TransactionPolicy.TransactionSynchronization.Status status) {
         Throwable firstException = null;

         for(Synchronization synchronization : this.registry.values()) {
            Instance instance = synchronization.instance;
            ThreadContext callContext = new ThreadContext(instance.beanContext, instance.primaryKey, Operation.AFTER_COMPLETION);
            callContext.setCurrentAllowedStates((BaseContext.State[])null);
            ThreadContext oldCallContext = ThreadContext.enter(callContext);

            try {
               instance.setInUse(true);
               if (synchronization.isCallSessionSynchronization()) {
                  BeanContext beanContext = instance.beanContext;
                  List<InterceptorData> interceptors = beanContext.getCallbackInterceptors();
                  InterceptorStack interceptorStack = new InterceptorStack(instance.bean, (Method)null, Operation.AFTER_COMPLETION, interceptors, instance.interceptors);
                  interceptorStack.invoke(status == TransactionPolicy.TransactionSynchronization.Status.COMMITTED);
               }

               instance.setTransaction((Transaction)null);
               StatefulContainer.this.releaseInstance(instance);
            } catch (InvalidateReferenceException var15) {
            } catch (Throwable e) {
               String message = "An unexpected system exception occured while invoking the afterCompletion method on the SessionSynchronization object";
               StatefulContainer.logger.error("An unexpected system exception occured while invoking the afterCompletion method on the SessionSynchronization object", e);
               StatefulContainer.this.discardInstance(callContext.getPrimaryKey(), instance);
               if (firstException == null) {
                  firstException = e;
               }
            } finally {
               ThreadContext.exit(oldCallContext);
            }
         }

         if (firstException != null) {
            throw new OpenEJBRuntimeException("An unexpected system exception occured while invoking the afterCompletion method on the SessionSynchronization object", firstException);
         }
      }

      public class Synchronization {
         private final Instance instance;
         private boolean callSessionSynchronization;

         public Synchronization(Instance instance) {
            this.instance = instance;
         }

         public synchronized boolean isCallSessionSynchronization() {
            return this.callSessionSynchronization;
         }

         public synchronized boolean setCallSessionSynchronization(boolean synchronize) {
            boolean oldValue = this.callSessionSynchronization;
            this.callSessionSynchronization = synchronize;
            return oldValue;
         }
      }
   }

   public class StatefulCacheListener implements Cache.CacheListener<Instance> {
      public void afterLoad(Instance instance) throws SystemException, ApplicationException {
         BeanContext beanContext = instance.beanContext;
         ThreadContext threadContext = new ThreadContext(instance.beanContext, instance.primaryKey, Operation.ACTIVATE);
         ThreadContext oldContext = ThreadContext.enter(threadContext);

         try {
            Method remove = instance.bean instanceof SessionBean ? SessionBean.class.getMethod("ejbActivate") : null;
            List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, remove, Operation.ACTIVATE, callbackInterceptors, instance.interceptors);
            interceptorStack.invoke();
         } catch (Throwable callbackException) {
            StatefulContainer.this.discardInstance(threadContext.getPrimaryKey(), instance);
            EjbTransactionUtil.handleSystemException(threadContext.getTransactionPolicy(), callbackException, threadContext);
         } finally {
            ThreadContext.exit(oldContext);
         }

      }

      public void beforeStore(Instance instance) {
         BeanContext beanContext = instance.beanContext;
         ThreadContext threadContext = new ThreadContext(beanContext, instance.primaryKey, Operation.PASSIVATE);
         ThreadContext oldContext = ThreadContext.enter(threadContext);

         try {
            Method passivate = instance.bean instanceof SessionBean ? SessionBean.class.getMethod("ejbPassivate") : null;
            List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, passivate, Operation.PASSIVATE, callbackInterceptors, instance.interceptors);
            interceptorStack.invoke();
         } catch (Throwable e) {
            StatefulContainer.logger.error("An unexpected exception occured while invoking the ejbPassivate method on the Stateful SessionBean instance", e);
         } finally {
            ThreadContext.exit(oldContext);
         }

      }

      public void timedOut(Instance instance) {
         BeanContext beanContext = instance.beanContext;
         ThreadContext threadContext = new ThreadContext(beanContext, instance.primaryKey, Operation.PRE_DESTROY);
         threadContext.setCurrentAllowedStates((BaseContext.State[])null);
         ThreadContext oldContext = ThreadContext.enter(threadContext);

         try {
            Method remove = instance.bean instanceof SessionBean ? SessionBean.class.getMethod("ejbRemove") : null;
            List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, remove, Operation.PRE_DESTROY, callbackInterceptors, instance.interceptors);
            interceptorStack.invoke();
         } catch (Throwable e) {
            StatefulContainer.logger.error("An unexpected exception occured while invoking the ejbRemove method on the timed-out Stateful SessionBean instance", e);
         } finally {
            StatefulContainer.logger.info("Removing the timed-out stateful session bean instance " + instance.primaryKey);
            ThreadContext.exit(oldContext);
         }

      }
   }

   private static final class Data {
      private final Index<Method, MethodType> methodIndex;
      private final List<ObjectName> jmxNames;

      private Data(Index<Method, MethodType> methodIndex) {
         this.jmxNames = new ArrayList();
         this.methodIndex = methodIndex;
      }

      public Index<Method, MethodType> getMethodIndex() {
         return this.methodIndex;
      }
   }

   public static class BeanContextFilter implements Cache.CacheFilter<Instance>, Serializable {
      private final String id;

      public BeanContextFilter(String id) {
         this.id = id;
      }

      public boolean matches(Instance instance) {
         return instance.beanContext.getId().equals(this.id);
      }
   }
}
