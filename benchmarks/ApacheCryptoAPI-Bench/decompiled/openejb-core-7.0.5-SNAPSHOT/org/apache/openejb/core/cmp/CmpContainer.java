package org.apache.openejb.core.cmp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.ejb.Timer;
import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.ContainerType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.entity.EntityContext;
import org.apache.openejb.core.entity.EntrancyTracker;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.timer.EjbTimerServiceImpl;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Enumerator;

public class CmpContainer implements RpcContainer {
   protected final Object containerID;
   protected final SecurityService securityService;
   protected final Map<Object, BeanContext> deploymentsById = new HashMap();
   protected final Map<Class, BeanContext> beansByClass = new HashMap();
   protected final CmpEngine cmpEngine;
   protected EntrancyTracker entrancyTracker;
   protected TransactionSynchronizationRegistry synchronizationRegistry;
   private static final Object ENTITIES_TO_STORE = new Object() {
      public String toString() {
         return "EntitiesToStore";
      }
   };

   public CmpContainer(Object id, TransactionManager transactionManager, SecurityService securityService, String cmpEngineFactory) throws OpenEJBException {
      this.containerID = id;
      this.securityService = securityService;
      this.synchronizationRegistry = (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class);
      this.entrancyTracker = new EntrancyTracker(this.synchronizationRegistry);
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
         classLoader = this.getClass().getClassLoader();
      }

      CmpEngineFactory factory;
      try {
         Class<?> cmpEngineFactoryClass = classLoader.loadClass(cmpEngineFactory);
         factory = (CmpEngineFactory)cmpEngineFactoryClass.newInstance();
      } catch (Exception e) {
         throw new OpenEJBException("Unable to create cmp engine factory " + cmpEngineFactory, e);
      }

      factory.setTransactionManager(transactionManager);
      factory.setTransactionSynchronizationRegistry(this.synchronizationRegistry);
      factory.setCmpCallback(new ContainerCmpCallback());
      this.cmpEngine = factory.create();
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public ContainerType getContainerType() {
      return ContainerType.CMP_ENTITY;
   }

   public synchronized BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deploymentsById.values().toArray(new BeanContext[this.deploymentsById.size()]);
   }

   public synchronized BeanContext getBeanContext(Object deploymentID) {
      return (BeanContext)this.deploymentsById.get(deploymentID);
   }

   private BeanContext getBeanContextByClass(Class type) {
      BeanContext beanContext;
      for(beanContext = null; type != null && beanContext == null; type = type.getSuperclass()) {
         beanContext = (BeanContext)this.beansByClass.get(type);
      }

      return beanContext;
   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      synchronized(this) {
         Object deploymentId = beanContext.getDeploymentID();
         this.cmpEngine.deploy(beanContext);
         beanContext.setContainerData(this.cmpEngine);
         beanContext.set(EJBContext.class, new EntityContext(this.securityService));

         try {
            Field field = beanContext.getCmpImplClass().getField("deploymentInfo");
            field.set((Object)null, beanContext);
         } catch (Exception var6) {
         }

         this.deploymentsById.put(deploymentId, beanContext);
         this.beansByClass.put(beanContext.getCmpImplClass(), beanContext);
         beanContext.setContainer(this);
      }
   }

   public void start(BeanContext beanContext) throws OpenEJBException {
      EjbTimerService timerService = beanContext.getEjbTimerService();
      if (timerService != null) {
         timerService.start();
      }

   }

   public void stop(BeanContext beanContext) throws OpenEJBException {
      beanContext.stop();
   }

   public void undeploy(BeanContext beanContext) throws OpenEJBException {
      synchronized(this) {
         this.deploymentsById.remove(beanContext.getDeploymentID());
         this.beansByClass.remove(beanContext.getCmpImplClass());

         try {
            Field field = beanContext.getCmpImplClass().getField("deploymentInfo");
            field.set((Object)null, (Object)null);
         } catch (Exception var5) {
         }

         beanContext.setContainer((Container)null);
         beanContext.setContainerData((Object)null);
      }
   }

   public Object getEjbInstance(BeanContext beanContext, Object primaryKey) {
      ThreadContext callContext = new ThreadContext(beanContext, primaryKey);
      ThreadContext oldCallContext = ThreadContext.enter(callContext);

      Object var5;
      try {
         var5 = this.cmpEngine.loadBean(callContext, primaryKey);
      } finally {
         ThreadContext.exit(oldCallContext);
      }

      return var5;
   }

   public Object invoke(Object deployID, InterfaceType type, Class callInterface, Method callMethod, Object[] args, Object primKey) throws OpenEJBException {
      BeanContext beanContext = this.getBeanContext(deployID);
      if (beanContext == null) {
         throw new OpenEJBException("Deployment does not exist in this container. Deployment(id='" + deployID + "'), Container(id='" + this.containerID + "')");
      } else {
         if (type == null) {
            type = beanContext.getInterfaceType(callInterface);
         }

         ThreadContext callContext = new ThreadContext(beanContext, primKey);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            boolean authorized = this.securityService.isCallerAuthorized(callMethod, type);
            if (!authorized) {
               throw new ApplicationException(new EJBAccessException("Unauthorized Access by Principal Denied"));
            } else {
               Class declaringClass = callMethod.getDeclaringClass();
               String methodName = callMethod.getName();
               if (!EJBHome.class.isAssignableFrom(declaringClass) && !EJBLocalHome.class.isAssignableFrom(declaringClass)) {
                  if ((EJBObject.class == declaringClass || EJBLocalObject.class == declaringClass) && methodName.equals("remove")) {
                     this.removeEJBObject(callMethod, callContext, type);
                     Object var23 = null;
                     return var23;
                  }
               } else {
                  if (declaringClass != EJBHome.class && declaringClass != EJBLocalHome.class) {
                     if (!methodName.startsWith("create")) {
                        if (methodName.equals("findByPrimaryKey")) {
                           Object var21 = this.findByPrimaryKey(callMethod, args, callContext, type);
                           return var21;
                        }

                        if (methodName.startsWith("find")) {
                           Object var20 = this.findEJBObject(callMethod, args, callContext, type);
                           return var20;
                        }

                        Object var19 = this.homeMethod(callMethod, args, callContext, type);
                        return var19;
                     }

                     ProxyInfo var18 = this.createEJBObject(callMethod, args, callContext, type);
                     return var18;
                  }

                  if (methodName.equals("remove")) {
                     this.removeEJBObject(callMethod, callContext, type);
                     Object var13 = null;
                     return var13;
                  }
               }

               callContext.setCurrentOperation(Operation.BUSINESS);
               Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
               callContext.set(Method.class, runMethod);
               Object var14 = this.businessMethod(callMethod, runMethod, args, callContext, type);
               return var14;
            }
         } finally {
            ThreadContext.exit(oldCallContext);
         }
      }
   }

   private EntityBean createNewInstance(ThreadContext callContext) {
      BeanContext beanContext = callContext.getBeanContext();

      try {
         return (EntityBean)beanContext.getCmpImplClass().newInstance();
      } catch (Exception e) {
         throw new EJBException("Unable to create new entity bean instance " + beanContext.getCmpImplClass(), e);
      }
   }

   private ThreadContext createThreadContext(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         BeanContext beanContext = this.getBeanContextByClass(entityBean.getClass());
         KeyGenerator keyGenerator = beanContext.getKeyGenerator();
         Object primaryKey = keyGenerator.getPrimaryKey(entityBean);
         return new ThreadContext(beanContext, primaryKey);
      }
   }

   private void setEntityContext(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         BeanContext beanContext = this.getBeanContextByClass(entityBean.getClass());
         ThreadContext callContext = new ThreadContext(beanContext, (Object)null);
         callContext.setCurrentOperation(Operation.SET_CONTEXT);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.setEntityContext(new EntityContext(this.securityService));
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private void unsetEntityContext(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.UNSET_CONTEXT);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.unsetEntityContext();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private void ejbLoad(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.LOAD);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.ejbLoad();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

         try {
            Set<EntityBean> registeredEntities = (LinkedHashSet)this.synchronizationRegistry.getResource(ENTITIES_TO_STORE);
            if (registeredEntities == null) {
               registeredEntities = new LinkedHashSet();
               this.synchronizationRegistry.putResource(ENTITIES_TO_STORE, registeredEntities);
               this.synchronizationRegistry.registerInterposedSynchronization(new Synchronization() {
                  public void beforeCompletion() {
                     Set<EntityBean> registeredEntities = (LinkedHashSet)CmpContainer.this.synchronizationRegistry.getResource(CmpContainer.ENTITIES_TO_STORE);
                     if (registeredEntities != null) {
                        for(EntityBean entityBean : registeredEntities) {
                           CmpContainer.this.ejbStore(entityBean);
                        }

                     }
                  }

                  public void afterCompletion(int i) {
                  }
               });
            }

            registeredEntities.add(entityBean);
         } catch (Exception var9) {
         }

      }
   }

   private void ejbStore(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.STORE);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.ejbStore();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private void ejbRemove(EntityBean entityBean) throws RemoveException {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else if (!this.isDeleted(entityBean)) {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.REMOVE);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.ejbRemove();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            try {
               entityBean.getClass().getMethod("OpenEJB_deleted").invoke(entityBean);
            } catch (Exception var11) {
            }

            this.cancelTimers(callContext);
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private boolean isDeleted(EntityBean entityBean) {
      try {
         return (Boolean)entityBean.getClass().getMethod("OpenEJB_isDeleted").invoke(entityBean);
      } catch (NoSuchMethodException var3) {
         return false;
      } catch (Exception e) {
         throw new EJBException(e);
      }
   }

   private void ejbActivate(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.ACTIVATE);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.ejbActivate();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private void ejbPassivate(EntityBean entityBean) {
      if (entityBean == null) {
         throw new NullPointerException("entityBean is null");
      } else {
         ThreadContext callContext = this.createThreadContext(entityBean);
         callContext.setCurrentOperation(Operation.PASSIVATE);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);

         try {
            entityBean.ejbPassivate();
         } catch (RemoteException e) {
            throw new EJBException(e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }
   }

   private Object businessMethod(Method callMethod, Method runMethod, Object[] args, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);
      Object returnValue = null;
      this.entrancyTracker.enter(beanContext, callContext.getPrimaryKey());

      try {
         EntityBean bean = (EntityBean)this.cmpEngine.loadBean(callContext, callContext.getPrimaryKey());
         if (bean == null) {
            throw new NoSuchObjectException(beanContext.getDeploymentID() + " : " + callContext.getPrimaryKey());
         }

         returnValue = runMethod.invoke(bean, args);
         this.cmpEngine.storeBeanIfNoTx(callContext, bean);
      } catch (NoSuchObjectException e) {
         EjbTransactionUtil.handleApplicationException(txPolicy, e, false);
      } catch (Throwable var17) {
         Throwable e = var17;
         if (var17 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var17).getTargetException();
         }

         ExceptionType type = callContext.getBeanContext().getExceptionType(e);
         if (type == ExceptionType.SYSTEM) {
            EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
         }
      } finally {
         this.entrancyTracker.exit(beanContext, callContext.getPrimaryKey());
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return returnValue;
   }

   private Object homeMethod(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);
      Object returnValue = null;

      try {
         EntityBean bean = this.createNewInstance(callContext);
         this.setEntityContext(bean);

         try {
            callContext.setCurrentOperation(Operation.HOME);
            Method runMethod = beanContext.getMatchingBeanMethod(callMethod);

            try {
               returnValue = runMethod.invoke(bean, args);
            } catch (IllegalArgumentException e) {
               System.out.println("********************************************************");
               System.out.println("callMethod = " + callMethod);
               System.out.println("runMethod = " + runMethod);
               System.out.println("bean = " + bean.getClass().getName());
               throw e;
            }
         } finally {
            this.unsetEntityContext(bean);
         }
      } catch (Throwable var23) {
         Throwable e = var23;
         if (var23 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var23).getTargetException();
         }

         ExceptionType type = callContext.getBeanContext().getExceptionType(e);
         if (type == ExceptionType.SYSTEM) {
            EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
         }
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return returnValue;
   }

   private ProxyInfo createEJBObject(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);
      Object primaryKey = null;

      try {
         EntityBean bean = this.createNewInstance(callContext);
         this.setEntityContext(bean);
         Method ejbCreateMethod = beanContext.getMatchingBeanMethod(callMethod);
         callContext.setCurrentOperation(Operation.CREATE);
         ejbCreateMethod.invoke(bean, args);
         primaryKey = this.cmpEngine.createBean(bean, callContext);
         Method ejbPostCreateMethod = beanContext.getMatchingPostCreateMethod(ejbCreateMethod);
         ThreadContext postCreateContext = new ThreadContext(beanContext, primaryKey);
         postCreateContext.setCurrentOperation(Operation.POST_CREATE);
         ThreadContext oldContext = ThreadContext.enter(postCreateContext);

         try {
            ejbPostCreateMethod.invoke(bean, args);
         } finally {
            ThreadContext.exit(oldContext);
         }

         this.cmpEngine.storeBeanIfNoTx(callContext, bean);
      } catch (Throwable var22) {
         Throwable e = var22;
         if (var22 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var22).getTargetException();
         }

         ExceptionType type = callContext.getBeanContext().getExceptionType(e);
         if (type == ExceptionType.SYSTEM) {
            EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
         }
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return new ProxyInfo(beanContext, primaryKey);
   }

   private Object findByPrimaryKey(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);

      ProxyInfo var10;
      try {
         EntityBean bean = (EntityBean)this.cmpEngine.loadBean(callContext, args[0]);
         if (bean == null) {
            throw new ObjectNotFoundException(beanContext.getDeploymentID() + " : " + args[0]);
         }

         KeyGenerator kg = beanContext.getKeyGenerator();
         Object primaryKey = kg.getPrimaryKey(bean);
         var10 = new ProxyInfo(beanContext, primaryKey);
      } catch (FinderException fe) {
         EjbTransactionUtil.handleApplicationException(txPolicy, fe, false);
         throw new AssertionError("Should not get here");
      } catch (Throwable e) {
         EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         throw new AssertionError("Should not get here");
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return var10;
   }

   private Object findEJBObject(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);

      Object primaryKey;
      try {
         List<Object> results = this.cmpEngine.queryBeans(callContext, callMethod, args);
         KeyGenerator kg = beanContext.getKeyGenerator();
         if (callMethod.getReturnType() != Collection.class && callMethod.getReturnType() != Enumeration.class) {
            if (results.size() != 1) {
               throw new ObjectNotFoundException("A Enteprise bean with deployment_id = " + beanContext.getDeploymentID() + (args != null && args.length >= 1 ? " and primarykey = " + args[0] : "") + " Does not exist");
            }

            EntityBean bean = (EntityBean)results.get(0);
            if (bean == null) {
               primaryKey = null;
               return primaryKey;
            }

            primaryKey = kg.getPrimaryKey(bean);
            ProxyInfo var26 = new ProxyInfo(beanContext, primaryKey);
            return var26;
         }

         List<ProxyInfo> proxies = new ArrayList();

         for(Object value : results) {
            EntityBean bean = (EntityBean)value;
            if (value == null) {
               proxies.add((Object)null);
            } else {
               Object primaryKey = kg.getPrimaryKey(bean);
               proxies.add(new ProxyInfo(beanContext, primaryKey));
            }
         }

         if (callMethod.getReturnType() != Enumeration.class) {
            primaryKey = proxies;
            return primaryKey;
         }

         primaryKey = new Enumerator(proxies);
      } catch (FinderException fe) {
         EjbTransactionUtil.handleApplicationException(txPolicy, fe, false);
         throw new AssertionError("Should not get here");
      } catch (Throwable e) {
         EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
         throw new AssertionError("Should not get here");
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return primaryKey;
   }

   public Object select(BeanContext beanContext, String methodSignature, String returnType, Object... args) throws FinderException {
      String signature = beanContext.getAbstractSchemaName() + "." + methodSignature;

      try {
         Collection<Object> results = this.cmpEngine.queryBeans(beanContext, signature, args);
         Collection<Object> proxies;
         if (returnType.equals("java.util.Set")) {
            proxies = new LinkedHashSet();
         } else {
            proxies = new ArrayList();
         }

         boolean isSingleValued = !returnType.equals("java.util.Collection") && !returnType.equals("java.util.Set");
         ProxyFactory proxyFactory = null;

         for(Object value : results) {
            if (isSingleValued && !proxies.isEmpty()) {
               throw new FinderException("The single valued query " + methodSignature + "returned more than one item");
            }

            if (value instanceof EntityBean) {
               EntityBean entityBean = (EntityBean)value;
               if (proxyFactory == null) {
                  BeanContext result = this.getBeanContextByClass(entityBean.getClass());
                  if (result != null) {
                     proxyFactory = new ProxyFactory(result);
                  }
               }

               if (proxyFactory != null) {
                  if (beanContext.isRemoteQueryResults(methodSignature)) {
                     value = proxyFactory.createRemoteProxy(entityBean, this);
                  } else {
                     value = proxyFactory.createLocalProxy(entityBean, this);
                  }
               }
            }

            proxies.add(value);
         }

         if (!isSingleValued) {
            return proxies;
         } else if (proxies.isEmpty()) {
            throw new ObjectNotFoundException();
         } else {
            return proxies.iterator().next();
         }
      } catch (RuntimeException e) {
         throw new EJBException(e);
      }
   }

   public int update(BeanContext beanContext, String methodSignature, Object... args) throws FinderException {
      String signature = beanContext.getAbstractSchemaName() + "." + methodSignature;
      return this.cmpEngine.executeUpdateQuery(beanContext, signature, args);
   }

   private void removeEJBObject(Method callMethod, ThreadContext callContext, InterfaceType interfaceType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, interfaceType), callContext);

      try {
         EntityBean entityBean = (EntityBean)this.cmpEngine.loadBean(callContext, callContext.getPrimaryKey());
         if (entityBean == null) {
            throw new NoSuchObjectException(callContext.getBeanContext().getDeploymentID() + " " + callContext.getPrimaryKey());
         }

         this.ejbRemove(entityBean);
         this.cmpEngine.removeBean(callContext);
      } catch (NoSuchObjectException e) {
         EjbTransactionUtil.handleApplicationException(txPolicy, e, false);
      } catch (Throwable e) {
         EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

   }

   private void cancelTimers(ThreadContext threadContext) {
      BeanContext beanContext = threadContext.getBeanContext();
      Object primaryKey = threadContext.getPrimaryKey();
      if (primaryKey != null && beanContext.getEjbTimerService() != null) {
         EjbTimerService timerService = beanContext.getEjbTimerService();
         if (timerService != null && timerService instanceof EjbTimerServiceImpl) {
            for(Timer timer : beanContext.getEjbTimerService().getTimers(primaryKey)) {
               timer.cancel();
            }
         }
      }

   }

   private class ContainerCmpCallback implements CmpCallback {
      private ContainerCmpCallback() {
      }

      public void setEntityContext(EntityBean entity) {
         CmpContainer.this.setEntityContext(entity);
      }

      public void unsetEntityContext(EntityBean entity) {
         CmpContainer.this.unsetEntityContext(entity);
      }

      public void ejbActivate(EntityBean entity) {
         CmpContainer.this.ejbActivate(entity);
      }

      public void ejbPassivate(EntityBean entity) {
         CmpContainer.this.ejbPassivate(entity);
      }

      public void ejbLoad(EntityBean entity) {
         CmpContainer.this.ejbLoad(entity);
      }

      public void ejbStore(EntityBean entity) {
         CmpContainer.this.ejbStore(entity);
      }

      public void ejbRemove(EntityBean entity) throws RemoveException {
         CmpContainer.this.ejbRemove(entity);
      }
   }
}
