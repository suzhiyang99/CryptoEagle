package org.apache.openejb.core.managed;

import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.dgc.VMID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
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
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.InstanceContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorStack;
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

public class ManagedContainer implements RpcContainer {
   private static final Logger logger;
   private final Object containerID;
   private final SecurityService securityService;
   protected final JtaEntityManagerRegistry entityManagerRegistry = (JtaEntityManagerRegistry)SystemInstance.get().getComponent(JtaEntityManagerRegistry.class);
   protected final Map<Object, BeanContext> deploymentsById = new HashMap();
   protected final Cache<Object, Instance> cache = new SimpleCache<Object, Instance>((Cache.CacheListener)null, new SimplePassivater(), 1000, 50, new Duration("1 hour"));
   private final ConcurrentHashMap<Object, Instance> checkedOutInstances = new ConcurrentHashMap();
   private final SessionContext sessionContext;

   public ManagedContainer(Object id, SecurityService securityService) throws SystemException {
      this.containerID = id;
      this.securityService = securityService;
      this.cache.setListener(new StatefulCacheListener());
      this.sessionContext = new ManagedContext(securityService, new ManagedUserTransaction(new EjbUserTransaction(), this.entityManagerRegistry));
   }

   private Map<Method, MethodType> getLifecycleMethodsOfInterface(BeanContext beanContext) {
      Map<Method, MethodType> methods = new HashMap();

      try {
         methods.put(BeanContext.Removable.class.getDeclaredMethod("$$remove"), ManagedContainer.MethodType.REMOVE);
      } catch (NoSuchMethodException e) {
         throw new IllegalStateException("Internal code change: BeanContext.Removable.$$remove() method was deleted", e);
      }

      for(Method removeMethod : beanContext.getRemoveMethods()) {
         methods.put(removeMethod, ManagedContainer.MethodType.REMOVE);

         for(Class businessLocal : beanContext.getBusinessLocalInterfaces()) {
            try {
               Method method = businessLocal.getMethod(removeMethod.getName());
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            } catch (NoSuchMethodException var18) {
            }
         }

         for(Class businessRemote : beanContext.getBusinessRemoteInterfaces()) {
            try {
               Method method = businessRemote.getMethod(removeMethod.getName());
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            } catch (NoSuchMethodException var17) {
            }
         }
      }

      Class legacyRemote = beanContext.getRemoteInterface();
      if (legacyRemote != null) {
         try {
            Method method = legacyRemote.getMethod("remove");
            methods.put(method, ManagedContainer.MethodType.REMOVE);
         } catch (NoSuchMethodException var16) {
         }
      }

      Class legacyLocal = beanContext.getLocalInterface();
      if (legacyLocal != null) {
         try {
            Method method = legacyLocal.getMethod("remove");
            methods.put(method, ManagedContainer.MethodType.REMOVE);
         } catch (NoSuchMethodException var15) {
         }
      }

      Class businessLocalHomeInterface = beanContext.getBusinessLocalInterface();
      if (businessLocalHomeInterface != null) {
         for(Method method : BeanContext.BusinessLocalHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, ManagedContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            }
         }
      }

      Class businessLocalBeanHomeInterface = beanContext.getBusinessLocalBeanInterface();
      if (businessLocalBeanHomeInterface != null) {
         for(Method method : BeanContext.BusinessLocalBeanHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, ManagedContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            }
         }
      }

      Class businessRemoteHomeInterface = beanContext.getBusinessRemoteInterface();
      if (businessRemoteHomeInterface != null) {
         for(Method method : BeanContext.BusinessRemoteHome.class.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, ManagedContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            }
         }
      }

      Class homeInterface = beanContext.getHomeInterface();
      if (homeInterface != null) {
         for(Method method : homeInterface.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, ManagedContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            }
         }
      }

      Class localHomeInterface = beanContext.getLocalHomeInterface();
      if (localHomeInterface != null) {
         for(Method method : localHomeInterface.getMethods()) {
            if (method.getName().startsWith("create")) {
               methods.put(method, ManagedContainer.MethodType.CREATE);
            } else if (method.getName().equals("remove")) {
               methods.put(method, ManagedContainer.MethodType.REMOVE);
            }
         }
      }

      return methods;
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

   public synchronized void undeploy(final BeanContext bean) throws OpenEJBException {
      Data data = (Data)bean.getContainerData();
      if (data != null) {
         MBeanServer server = LocalMBeanServer.get();

         for(ObjectName objectName : data.jmxNames) {
            try {
               server.unregisterMBean(objectName);
            } catch (Exception var7) {
               logger.error("Unable to unregister MBean " + objectName);
            }
         }
      }

      this.deploymentsById.remove(bean.getDeploymentID());
      bean.setContainer((Container)null);
      bean.setContainerData((Object)null);
      this.cache.removeAll(new Cache.CacheFilter<Instance>() {
         public boolean matches(Instance instance) {
            return bean == instance.beanContext;
         }
      });
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
         jmxName.set("StatelessSessionBean", beanContext.getEjbName());
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
         methodType = methodType != null ? methodType : ManagedContainer.MethodType.BUSINESS;
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

   protected ProxyInfo createEJBObject(BeanContext beanContext, Method callMethod, Object[] args, InterfaceType interfaceType) throws OpenEJBException {
      Object primaryKey = this.newPrimaryKey();
      ThreadContext createContext = new ThreadContext(beanContext, primaryKey);
      ThreadContext oldCallContext = ThreadContext.enter(createContext);

      ProxyInfo e;
      try {
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
               instance = new Instance(beanContext, primaryKey, context.getBean(), context.getInterceptors(), context.getCreationalContext(), entityManagers);
            } catch (Throwable var25) {
               Throwable throwable = var25;
               ThreadContext callContext = ThreadContext.getThreadContext();
               EjbTransactionUtil.handleSystemException(callContext.getTransactionPolicy(), var25, callContext);
               throw new IllegalStateException(var25);
            }

            this.cache.add(primaryKey, instance);
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
            this.afterInvoke(createContext, txPolicy, instance);
         }

         e = new ProxyInfo(beanContext, primaryKey);
      } finally {
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
         ThreadContext callContext = new ThreadContext(beanContext, primKey);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         Object e;
         try {
            boolean internalRemove = BeanContext.Removable.class == callMethod.getDeclaringClass();
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
               instance = this.obtainInstance(primKey, callContext);
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
                  if (args == null) {
                     returnValue = interceptorStack.invoke();
                  } else {
                     returnValue = interceptorStack.invoke(args);
                  }
               }
            } catch (InvalidateReferenceException var83) {
               InvalidateReferenceException e = var83;
               throw new ApplicationException(var83.getRootCause());
            } catch (Throwable var84) {
               Throwable e = var84;
               if (interfaceType.isBusiness()) {
                  retain = beanContext.retainIfExeption(runMethod);
                  this.handleException(callContext, txPolicy, var84);
               } else {
                  try {
                     this.handleException(callContext, txPolicy, e);
                  } catch (ApplicationException var82) {
                  }
               }
            } finally {
               if (!retain) {
                  try {
                     callContext.setCurrentOperation(Operation.PRE_DESTROY);
                     List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
                     InterceptorStack interceptorStack = new InterceptorStack(instance.bean, (Method)null, Operation.PRE_DESTROY, callbackInterceptors, instance.interceptors);
                     interceptorStack.invoke();
                  } catch (Throwable callbackException) {
                     String logMessage = "An unexpected exception occured while invoking the preDestroy method on the removed Stateful SessionBean instance; " + callbackException.getClass().getName() + " " + callbackException.getMessage();
                     logger.error(logMessage);
                  } finally {
                     callContext.setCurrentOperation(Operation.REMOVE);
                  }

                  this.discardInstance(callContext);
               }

               this.afterInvoke(callContext, txPolicy, instance);
            }

            e = returnValue;
         } finally {
            ThreadContext.exit(oldCallContext);
         }

         return e;
      }
   }

   protected Object businessMethod(BeanContext beanContext, Object primKey, Class callInterface, Method callMethod, Object[] args, InterfaceType interfaceType) throws OpenEJBException {
      ThreadContext callContext = new ThreadContext(beanContext, primKey);
      ThreadContext oldCallContext = ThreadContext.enter(callContext);

      Object e;
      try {
         this.checkAuthorization(callMethod, interfaceType);
         TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(callContext.getBeanContext().getTransactionType(callMethod, interfaceType), callContext);
         Object returnValue = null;
         Instance instance = null;

         try {
            instance = this.obtainInstance(primKey, callContext);
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
            List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, Operation.BUSINESS, interceptors, instance.interceptors);
            returnValue = interceptorStack.invoke(args);
         } catch (Throwable e) {
            this.handleException(callContext, txPolicy, e);
         } finally {
            this.afterInvoke(callContext, txPolicy, instance);
         }

         e = returnValue;
      } finally {
         ThreadContext.exit(oldCallContext);
      }

      return e;
   }

   private Instance obtainInstance(Object primaryKey, ThreadContext callContext) throws OpenEJBException {
      if (primaryKey == null) {
         throw new SystemException(new NullPointerException("Cannot obtain an instance of the stateful session bean with a null session id"));
      } else {
         Transaction currentTransaction = this.getTransaction(callContext);
         Instance instance = (Instance)this.checkedOutInstances.get(primaryKey);
         if (instance == null) {
            try {
               instance = this.cache.checkOut(primaryKey);
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

         synchronized(this) {
            if (instance.isInUse()) {
               Operation currentOperation = callContext.getCurrentOperation();
               if (currentOperation != Operation.AFTER_COMPLETION && currentOperation != Operation.BEFORE_COMPLETION) {
                  throw new ApplicationException(new RemoteException("Concurrent calls not allowed."));
               }
            }

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
         if (instance.getBeanTransaction() != null) {
            throw new IllegalStateException("Instance has an active bean-managed transaction");
         } else {
            instance.setInUse(false);
            if (instance.getTransaction() == null) {
               this.cache.checkIn(instance.primaryKey);
               this.checkedOutInstances.remove(instance.primaryKey);
            }

         }
      }
   }

   private void discardInstance(ThreadContext threadContext) {
      Object primaryKey = threadContext.getPrimaryKey();
      if (primaryKey != null) {
         Instance instance = (Instance)this.checkedOutInstances.remove(primaryKey);
         this.cache.remove(primaryKey);
         if (instance.creationalContext != null) {
            instance.creationalContext.release();
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
            this.discardInstance(callContext);
            EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
         }

      }
   }

   private void afterInvoke(ThreadContext callContext, TransactionPolicy txPolicy, Instance instance) throws OpenEJBException {
      try {
         this.unregisterEntityManagers(instance, callContext);
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
               SynchronizationType synchronizationType = ((BeanContext.EntityManagerConfiguration)entry.getValue()).getSynchronizationType();
               Map properties = ((BeanContext.EntityManagerConfiguration)entry.getValue()).getProperties();
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

   private void unregisterEntityManagers(Instance instance, ThreadContext callContext) {
      if (this.entityManagerRegistry != null) {
         if (instance != null) {
            BeanContext beanContext = callContext.getBeanContext();
            this.entityManagerRegistry.removeEntityManagers((String)beanContext.getDeploymentID(), instance.primaryKey);
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
               ManagedContainer.logger.error("An unexpected system exception occured while invoking the afterBegin method on the SessionSynchronization object", (Throwable)e);
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
                  ManagedContainer.logger.error("An unexpected system exception occured while invoking the beforeCompletion method on the SessionSynchronization object", (Throwable)e);
                  this.txPolicy.setRollbackOnly(e);
                  ManagedContainer.this.discardInstance(callContext);
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
               ManagedContainer.this.releaseInstance(instance);
            } catch (InvalidateReferenceException var15) {
            } catch (Throwable e) {
               String message = "An unexpected system exception occured while invoking the afterCompletion method on the SessionSynchronization object";
               ManagedContainer.logger.error("An unexpected system exception occured while invoking the afterCompletion method on the SessionSynchronization object", e);
               ManagedContainer.this.discardInstance(callContext);
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
            ManagedContainer.this.discardInstance(threadContext);
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
            ManagedContainer.logger.error("An unexpected exception occured while invoking the ejbPassivate method on the Stateful SessionBean instance", e);
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
            ManagedContainer.logger.error("An unexpected exception occured while invoking the ejbRemove method on the timed-out Stateful SessionBean instance", e);
         } finally {
            ManagedContainer.logger.info("Removing the timed-out stateful session bean instance " + instance.primaryKey);
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
}
