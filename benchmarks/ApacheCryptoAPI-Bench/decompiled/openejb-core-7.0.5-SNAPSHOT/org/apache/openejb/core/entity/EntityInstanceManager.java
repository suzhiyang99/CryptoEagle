package org.apache.openejb.core.entity;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJBContext;
import javax.ejb.EntityBean;
import javax.ejb.NoSuchEntityException;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InvalidateReferenceException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.NoSuchObjectException;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionRolledbackException;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LinkedListStack;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Stack;

public class EntityInstanceManager {
   private static final Logger logger;
   private final int poolsize;
   private final Map<Object, LinkedListStack> poolMap;
   private final SecurityService securityService;

   public EntityInstanceManager(EntityContainer container, SecurityService securityService, int poolSize) {
      this.securityService = securityService;
      this.poolsize = poolSize;
      this.poolMap = new HashMap();
      BeanContext[] beanContexts = container.getBeanContexts();

      for(BeanContext beanContext : beanContexts) {
         this.deploy(beanContext);
      }

   }

   public void deploy(BeanContext beanContext) {
      this.poolMap.put(beanContext.getDeploymentID(), new LinkedListStack(this.poolsize / 2));
      beanContext.set(EJBContext.class, this.createEntityContext());
   }

   public void undeploy(BeanContext beanContext) {
      this.poolMap.remove(beanContext.getDeploymentID());
   }

   public EntityBean obtainInstance(ThreadContext callContext) throws OpenEJBException {
      Object primaryKey = callContext.getPrimaryKey();
      TransactionPolicy txPolicy = callContext.getTransactionPolicy();
      if (callContext.getPrimaryKey() != null && txPolicy != null && txPolicy.isTransactionActive()) {
         Key key = new Key(callContext.getBeanContext().getDeploymentID(), primaryKey);
         SynchronizationWrapper wrapper = (SynchronizationWrapper)txPolicy.getResource(key);
         if (wrapper != null) {
            if (!wrapper.isAssociated()) {
               throw new InvalidateReferenceException(new NoSuchObjectException("Entity not found: " + primaryKey));
            } else {
               if (callContext.getCurrentOperation() == Operation.REMOVE) {
                  wrapper.disassociate();
               }

               return !wrapper.isAvailable() && !wrapper.primaryKey.equals(primaryKey) ? wrapper.getEntityBean() : wrapper.getEntityBean();
            }
         } else {
            EntityBean bean = this.getPooledInstance(callContext);
            wrapper = new SynchronizationWrapper(callContext.getBeanContext(), primaryKey, bean, false, key, txPolicy);
            if (callContext.getCurrentOperation() == Operation.REMOVE) {
               wrapper.disassociate();
            }

            txPolicy.registerSynchronization(wrapper);
            this.loadingBean(bean, callContext);
            Operation orginalOperation = callContext.getCurrentOperation();
            callContext.setCurrentOperation(Operation.LOAD);

            try {
               bean.ejbLoad();
            } catch (NoSuchEntityException e) {
               wrapper.disassociate();
               throw new InvalidateReferenceException(new NoSuchObjectException("Entity not found: " + primaryKey, e));
            } catch (Exception e) {
               logger.error("Exception encountered during ejbLoad():", (Throwable)e);
               wrapper.disassociate();
               throw new OpenEJBException(e);
            } finally {
               callContext.setCurrentOperation(orginalOperation);
            }

            txPolicy.putResource(key, wrapper);
            return bean;
         }
      } else {
         return this.getPooledInstance(callContext);
      }
   }

   protected void loadingBean(EntityBean bean, ThreadContext callContext) throws OpenEJBException {
   }

   protected void reusingBean(EntityBean bean, ThreadContext callContext) throws OpenEJBException {
   }

   protected EntityBean getPooledInstance(ThreadContext callContext) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      Stack methodReadyPool = (Stack)this.poolMap.get(beanContext.getDeploymentID());
      if (methodReadyPool == null) {
         throw new SystemException("Invalid deployment id " + beanContext.getDeploymentID() + " for this container");
      } else {
         EntityBean bean = (EntityBean)methodReadyPool.pop();
         if (bean == null) {
            try {
               bean = (EntityBean)beanContext.getBeanClass().newInstance();
            } catch (Exception e) {
               logger.error("Bean instantiation failed for class " + beanContext.getBeanClass(), (Throwable)e);
               throw new SystemException(e);
            }

            Operation currentOp = callContext.getCurrentOperation();
            callContext.setCurrentOperation(Operation.SET_CONTEXT);

            try {
               bean.setEntityContext(this.createEntityContext());
            } catch (Exception e) {
               logger.error("Bean callback method failed ", (Throwable)e);
               throw new ApplicationException(e);
            } finally {
               callContext.setCurrentOperation(currentOp);
            }
         } else {
            this.reusingBean(bean, callContext);
         }

         if (callContext.getCurrentOperation() == Operation.BUSINESS || callContext.getCurrentOperation() == Operation.REMOVE) {
            Operation currentOp = callContext.getCurrentOperation();
            callContext.setCurrentOperation(Operation.ACTIVATE);

            try {
               bean.ejbActivate();
            } catch (Throwable e) {
               logger.error("Encountered exception during call to ejbActivate()", e);
               TransactionPolicy txPolicy = callContext.getTransactionPolicy();
               if (txPolicy != null && txPolicy.isTransactionActive()) {
                  txPolicy.setRollbackOnly(e);
                  throw new ApplicationException(new TransactionRolledbackException("Reflection exception thrown while attempting to call ejbActivate() on the instance", e));
               }

               throw new ApplicationException(new RemoteException("Exception thrown while attempting to call ejbActivate() on the instance. Exception message = " + e.getMessage(), e));
            } finally {
               callContext.setCurrentOperation(currentOp);
            }
         }

         return bean;
      }
   }

   private EntityContext createEntityContext() {
      return new EntityContext(this.securityService);
   }

   public void poolInstance(ThreadContext callContext, EntityBean bean, Object primaryKey) throws OpenEJBException {
      if (bean != null) {
         TransactionPolicy txPolicy = callContext.getTransactionPolicy();
         if (primaryKey != null && txPolicy != null && txPolicy.isTransactionActive()) {
            Key key = new Key(callContext.getBeanContext().getDeploymentID(), primaryKey);
            SynchronizationWrapper wrapper = (SynchronizationWrapper)txPolicy.getResource(key);
            if (wrapper != null) {
               if (callContext.getCurrentOperation() == Operation.REMOVE) {
                  wrapper.disassociate();
                  Stack methodReadyPool = (Stack)this.poolMap.get(callContext.getBeanContext().getDeploymentID());
                  methodReadyPool.push(bean);
               } else {
                  if (callContext.getCurrentOperation() == Operation.CREATE) {
                     wrapper.associate();
                  }

                  wrapper.setEntityBean(bean);
               }
            } else {
               wrapper = new SynchronizationWrapper(callContext.getBeanContext(), primaryKey, bean, true, key, txPolicy);
               txPolicy.registerSynchronization(wrapper);
               txPolicy.putResource(key, wrapper);
            }
         } else {
            if (primaryKey != null && callContext.getCurrentOperation() != Operation.REMOVE) {
               Operation currentOp = callContext.getCurrentOperation();
               callContext.setCurrentOperation(Operation.PASSIVATE);

               try {
                  bean.ejbPassivate();
               } catch (Throwable e) {
                  if (txPolicy.isTransactionActive()) {
                     txPolicy.setRollbackOnly(e);
                     throw new ApplicationException(new TransactionRolledbackException("Reflection exception thrown while attempting to call ejbPassivate() on the instance", e));
                  }

                  throw new ApplicationException(new RemoteException("Reflection exception thrown while attempting to call ejbPassivate() on the instance. Exception message = " + e.getMessage(), e));
               } finally {
                  callContext.setCurrentOperation(currentOp);
               }
            }

            Stack methodReadyPool = (Stack)this.poolMap.get(callContext.getBeanContext().getDeploymentID());
            methodReadyPool.push(bean);
         }

      }
   }

   public void freeInstance(ThreadContext callContext, EntityBean bean) throws SystemException {
      this.discardInstance(callContext, bean);
      Operation currentOp = callContext.getCurrentOperation();
      callContext.setCurrentOperation(Operation.UNSET_CONTEXT);

      try {
         bean.unsetEntityContext();
      } catch (Exception e) {
         logger.info(this.getClass().getName() + ".freeInstance: ignoring exception " + e + " on bean instance " + bean);
      } finally {
         callContext.setCurrentOperation(currentOp);
      }

   }

   public void discardInstance(ThreadContext callContext, EntityBean bean) throws SystemException {
      Object primaryKey = callContext.getPrimaryKey();
      TransactionPolicy txPolicy = callContext.getTransactionPolicy();
      if (primaryKey != null && txPolicy != null && txPolicy.isTransactionActive()) {
         Key key = new Key(callContext.getBeanContext().getDeploymentID(), primaryKey);
         SynchronizationWrapper wrapper = (SynchronizationWrapper)txPolicy.getResource(key);
         if (wrapper != null) {
            wrapper.disassociate();
         }

      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }

   private static class Key {
      private final Object deploymentId;
      private final Object primaryKey;

      public Key(Object deploymentId, Object primaryKey) {
         if (deploymentId == null) {
            throw new NullPointerException("deploymentId is null");
         } else if (primaryKey == null) {
            throw new NullPointerException("primaryKey is null");
         } else {
            this.deploymentId = deploymentId;
            this.primaryKey = primaryKey;
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)o;
            return this.deploymentId.equals(key.deploymentId) && this.primaryKey.equals(key.primaryKey);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.deploymentId.hashCode();
         result = 31 * result + this.primaryKey.hashCode();
         return result;
      }
   }

   protected class SynchronizationWrapper implements TransactionPolicy.TransactionSynchronization {
      private EntityBean bean;
      private boolean available;
      private boolean associated;
      private final Key readyPoolKey;
      private final BeanContext beanContext;
      private final Object primaryKey;
      private final TransactionPolicy txPolicy;

      public SynchronizationWrapper(BeanContext beanContext, Object primaryKey, EntityBean bean, boolean available, Key readyPoolKey, TransactionPolicy txPolicy) {
         if (bean == null) {
            throw new IllegalArgumentException("bean is null");
         } else if (readyPoolKey == null) {
            throw new IllegalArgumentException("key is null");
         } else if (beanContext == null) {
            throw new IllegalArgumentException("deploymentInfo is null");
         } else if (primaryKey == null) {
            throw new IllegalArgumentException("primaryKey is null");
         } else if (txPolicy == null) {
            throw new IllegalArgumentException("txEnv is null");
         } else {
            this.beanContext = beanContext;
            this.bean = bean;
            this.primaryKey = primaryKey;
            this.available = available;
            this.readyPoolKey = readyPoolKey;
            this.txPolicy = txPolicy;
            this.associated = true;
         }
      }

      public void associate() {
         this.associated = true;
      }

      public void disassociate() {
         this.associated = false;
      }

      public boolean isAssociated() {
         return this.associated;
      }

      public synchronized boolean isAvailable() {
         return this.available;
      }

      public synchronized void setEntityBean(EntityBean ebean) {
         this.available = true;
         this.bean = ebean;
      }

      public synchronized EntityBean getEntityBean() {
         this.available = false;
         return this.bean;
      }

      public void beforeCompletion() {
         if (this.associated) {
            EntityBean bean;
            synchronized(this) {
               bean = this.bean;
            }

            ThreadContext callContext = new ThreadContext(this.beanContext, this.primaryKey);
            callContext.setCurrentOperation(Operation.STORE);
            ThreadContext oldCallContext = ThreadContext.enter(callContext);

            try {
               bean.ejbStore();
            } catch (Exception re) {
               EntityInstanceManager.logger.error("Exception occured during ejbStore()", (Throwable)re);
               this.txPolicy.setRollbackOnly(re);
            } finally {
               ThreadContext.exit(oldCallContext);
            }
         }

      }

      public void afterCompletion(TransactionPolicy.TransactionSynchronization.Status status) {
         this.txPolicy.removeResource(this.readyPoolKey);
      }
   }
}
