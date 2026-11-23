package org.apache.openejb.core.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityBean;
import javax.ejb.NoSuchEntityException;
import javax.ejb.Timer;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.ContainerType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.timer.EjbTimerServiceImpl;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.ArrayEnumeration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class EntityContainer implements RpcContainer {
   private final EntityInstanceManager instanceManager;
   private final Map<String, BeanContext> deploymentRegistry = new HashMap();
   private final Object containerID;
   public static Logger logger;
   private final SecurityService securityService;
   protected EntrancyTracker entrancyTracker;

   public EntityContainer(Object id, SecurityService securityService, int poolSize) throws OpenEJBException {
      this.containerID = id;
      this.securityService = securityService;
      this.entrancyTracker = new EntrancyTracker((TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class));
      this.instanceManager = new EntityInstanceManager(this, securityService, poolSize);
   }

   public synchronized BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deploymentRegistry.values().toArray(new BeanContext[this.deploymentRegistry.size()]);
   }

   public synchronized BeanContext getBeanContext(Object deploymentID) {
      String id = (String)deploymentID;
      return (BeanContext)this.deploymentRegistry.get(id);
   }

   public ContainerType getContainerType() {
      return ContainerType.BMP_ENTITY;
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      synchronized(this) {
         this.deploymentRegistry.put((String)beanContext.getDeploymentID(), beanContext);
         beanContext.setContainer(this);
      }

      this.instanceManager.deploy(beanContext);
   }

   public void start(BeanContext info) throws OpenEJBException {
      EjbTimerService timerService = info.getEjbTimerService();
      if (timerService != null) {
         timerService.start();
      }

   }

   public void stop(BeanContext info) throws OpenEJBException {
      info.stop();
   }

   public void undeploy(BeanContext info) throws OpenEJBException {
      this.instanceManager.undeploy(info);
      synchronized(this) {
         String id = (String)info.getDeploymentID();
         this.deploymentRegistry.remove(id);
         info.setContainer((Container)null);
      }
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
            boolean authorized = type == InterfaceType.TIMEOUT || this.getSecurityService().isCallerAuthorized(callMethod, type);
            if (!authorized) {
               throw new ApplicationException(new EJBAccessException("Unauthorized Access by Principal Denied"));
            } else {
               Class declaringClass = callMethod.getDeclaringClass();
               String methodName = callMethod.getName();
               if (!EJBHome.class.isAssignableFrom(declaringClass) && !EJBLocalHome.class.isAssignableFrom(declaringClass)) {
                  if ((EJBObject.class == declaringClass || EJBLocalObject.class == declaringClass) && methodName.equals("remove")) {
                     this.removeEJBObject(callMethod, args, callContext, type);
                     Object var22 = null;
                     return var22;
                  }
               } else {
                  if (declaringClass != EJBHome.class && declaringClass != EJBLocalHome.class) {
                     if (!methodName.startsWith("create")) {
                        if (methodName.startsWith("find")) {
                           Object var20 = this.findMethod(callMethod, args, callContext, type);
                           return var20;
                        }

                        Object var19 = this.homeMethod(callMethod, args, callContext, type);
                        return var19;
                     }

                     ProxyInfo var18 = this.createEJBObject(callMethod, args, callContext, type);
                     return var18;
                  }

                  if (methodName.equals("remove")) {
                     this.removeEJBObject(callMethod, args, callContext, type);
                     Object var13 = null;
                     return var13;
                  }
               }

               callContext.setCurrentOperation(type == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS);
               Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
               callContext.set(Method.class, runMethod);
               Object var14 = this.invoke(type, callMethod, runMethod, args, callContext);
               return var14;
            }
         } finally {
            ThreadContext.exit(oldCallContext);
         }
      }
   }

   private SecurityService getSecurityService() {
      return this.securityService;
   }

   public EntityInstanceManager getInstanceManager() {
      return this.instanceManager;
   }

   protected Object invoke(InterfaceType type, Method callMethod, Method runMethod, Object[] args, ThreadContext callContext) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, type), callContext);
      EntityBean bean = null;
      Object returnValue = null;
      this.entrancyTracker.enter(callContext.getBeanContext(), callContext.getPrimaryKey());

      try {
         bean = this.instanceManager.obtainInstance(callContext);
         this.ejbLoad_If_No_Transaction(callContext, bean);
         returnValue = runMethod.invoke(bean, args);
         this.ejbStore_If_No_Transaction(callContext, bean);
         this.instanceManager.poolInstance(callContext, bean, callContext.getPrimaryKey());
      } catch (Throwable e) {
         this.handleException(txPolicy, e, callContext, bean);
      } finally {
         this.entrancyTracker.exit(callContext.getBeanContext(), callContext.getPrimaryKey());
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return returnValue;
   }

   public void ejbLoad_If_No_Transaction(ThreadContext callContext, EntityBean bean) throws Exception {
      Operation orginalOperation = callContext.getCurrentOperation();
      if (orginalOperation == Operation.BUSINESS || orginalOperation == Operation.REMOVE) {
         TransactionPolicy callerTxPolicy = callContext.getTransactionPolicy();
         if (callerTxPolicy != null && callerTxPolicy.isTransactionActive()) {
            return;
         }

         BeanContext beanContext = callContext.getBeanContext();
         TransactionPolicy txPolicy = beanContext.getTransactionPolicyFactory().createTransactionPolicy(TransactionType.Supports);

         try {
            if (!txPolicy.isTransactionActive()) {
               callContext.setCurrentOperation(Operation.LOAD);
               bean.ejbLoad();
            }
         } catch (NoSuchEntityException var12) {
            this.instanceManager.discardInstance(callContext, bean);
            throw new ApplicationException(new NoSuchObjectException("Entity not found: " + callContext.getPrimaryKey()));
         } catch (Exception e) {
            this.instanceManager.discardInstance(callContext, bean);
            throw e;
         } finally {
            callContext.setCurrentOperation(orginalOperation);
            txPolicy.commit();
         }
      }

   }

   public void ejbStore_If_No_Transaction(ThreadContext callContext, EntityBean bean) throws Exception {
      Operation currentOp = callContext.getCurrentOperation();
      if (currentOp == Operation.BUSINESS) {
         TransactionPolicy callerTxPolicy = callContext.getTransactionPolicy();
         if (callerTxPolicy != null && callerTxPolicy.isTransactionActive()) {
            return;
         }

         BeanContext beanContext = callContext.getBeanContext();
         TransactionPolicy txPolicy = beanContext.getTransactionPolicyFactory().createTransactionPolicy(TransactionType.Supports);

         try {
            if (!txPolicy.isTransactionActive()) {
               callContext.setCurrentOperation(Operation.STORE);
               bean.ejbStore();
            }
         } catch (Exception e) {
            this.instanceManager.discardInstance(callContext, bean);
            throw e;
         } finally {
            callContext.setCurrentOperation(currentOp);
            txPolicy.commit();
         }
      }

   }

   protected void didCreateBean(ThreadContext callContext, EntityBean bean) throws OpenEJBException {
   }

   protected ProxyInfo createEJBObject(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType type) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      callContext.setCurrentOperation(Operation.CREATE);
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, type), callContext);
      EntityBean bean = null;
      Object primaryKey = null;

      try {
         bean = this.instanceManager.obtainInstance(callContext);
         Method ejbCreateMethod = beanContext.getMatchingBeanMethod(callMethod);
         primaryKey = ejbCreateMethod.invoke(bean, args);
         this.didCreateBean(callContext, bean);
         Method ejbPostCreateMethod = beanContext.getMatchingPostCreateMethod(ejbCreateMethod);
         ThreadContext postCreateContext = new ThreadContext(beanContext, primaryKey);
         postCreateContext.setCurrentOperation(Operation.POST_CREATE);
         ThreadContext oldContext = ThreadContext.enter(postCreateContext);

         try {
            ejbPostCreateMethod.invoke(bean, args);
         } finally {
            ThreadContext.exit(oldContext);
         }

         this.instanceManager.poolInstance(callContext, bean, primaryKey);
      } catch (Throwable e) {
         this.handleException(txPolicy, e, callContext, bean);
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

      return new ProxyInfo(beanContext, primaryKey);
   }

   protected Object findMethod(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType type) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      callContext.setCurrentOperation(Operation.FIND);
      Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
      Object returnValue = this.invoke(type, callMethod, runMethod, args, callContext);
      if (returnValue instanceof Collection) {
         Iterator keys = ((Collection)returnValue).iterator();
         Vector<ProxyInfo> proxies = new Vector();

         while(keys.hasNext()) {
            Object primaryKey = keys.next();
            proxies.addElement(new ProxyInfo(beanContext, primaryKey));
         }

         returnValue = proxies;
      } else if (returnValue instanceof Enumeration) {
         Enumeration keys = (Enumeration)returnValue;
         Vector<ProxyInfo> proxies = new Vector();

         while(keys.hasMoreElements()) {
            Object primaryKey = keys.nextElement();
            proxies.addElement(new ProxyInfo(beanContext, primaryKey));
         }

         returnValue = new ArrayEnumeration(proxies);
      } else {
         returnValue = new ProxyInfo(beanContext, returnValue);
      }

      return returnValue;
   }

   protected Object homeMethod(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType type) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      callContext.setCurrentOperation(Operation.HOME);
      Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
      return this.invoke(type, callMethod, runMethod, args, callContext);
   }

   protected void didRemove(EntityBean bean, ThreadContext threadContext) throws OpenEJBException {
      this.cancelTimers(threadContext);
   }

   private void cancelTimers(ThreadContext threadContext) {
      BeanContext beanContext = threadContext.getBeanContext();
      Object primaryKey = threadContext.getPrimaryKey();
      if (primaryKey != null) {
         EjbTimerService timerService = beanContext.getEjbTimerService();
         if (timerService != null && timerService instanceof EjbTimerServiceImpl) {
            for(Timer timer : beanContext.getEjbTimerService().getTimers(primaryKey)) {
               timer.cancel();
            }
         }
      }

   }

   protected void removeEJBObject(Method callMethod, Object[] args, ThreadContext callContext, InterfaceType type) throws OpenEJBException {
      callContext.setCurrentOperation(Operation.REMOVE);
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, type), callContext);
      EntityBean bean = null;

      try {
         bean = this.instanceManager.obtainInstance(callContext);
         this.ejbLoad_If_No_Transaction(callContext, bean);
         bean.ejbRemove();
         this.didRemove(bean, callContext);
         this.instanceManager.poolInstance(callContext, bean, callContext.getPrimaryKey());
      } catch (Throwable e) {
         this.handleException(txPolicy, e, callContext, bean);
      } finally {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      }

   }

   private void handleException(TransactionPolicy txPolicy, Throwable e, ThreadContext callContext, EntityBean bean) throws OpenEJBException {
      ExceptionType type;
      if (e instanceof InvocationTargetException) {
         e = ((InvocationTargetException)e).getTargetException();
         type = callContext.getBeanContext().getExceptionType(e);
      } else if (e instanceof ApplicationException) {
         e = ((ApplicationException)e).getRootCause();
         type = ExceptionType.APPLICATION;
      } else if (e instanceof SystemException) {
         e = ((SystemException)e).getRootCause();
         type = ExceptionType.SYSTEM;
      } else {
         type = ExceptionType.SYSTEM;
      }

      if (type == ExceptionType.SYSTEM) {
         if (bean != null) {
            try {
               this.instanceManager.discardInstance(callContext, bean);
            } catch (SystemException var7) {
               logger.error("The instance manager encountered an unkown system exception while trying to discard the entity instance with primary key " + callContext.getPrimaryKey());
            }
         }

         EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
      } else {
         this.instanceManager.poolInstance(callContext, bean, callContext.getPrimaryKey());
         EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
