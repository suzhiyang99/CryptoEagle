package org.apache.openejb.core.stateful;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.enterprise.context.spi.CreationalContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transaction;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.transaction.BeanTransactionPolicy;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.JtaEntityManagerRegistry;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Index;
import org.apache.openejb.util.PojoSerialization;

public class Instance implements Serializable, Cache.TimeOut {
   private static final long serialVersionUID = 2862563626506556542L;
   public final BeanContext beanContext;
   public final Object primaryKey;
   public final Object bean;
   public final Object containerId;
   public CreationalContext creationalContext;
   public final Map<String, Object> interceptors;
   private boolean inUse;
   private BeanTransactionPolicy.SuspendedTransaction beanTransaction;
   private final Stack<Transaction> transaction = new Stack();
   private final LockFactory.StatefulLock lock;
   private Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> entityManagers;
   private final JtaEntityManagerRegistry.EntityManagerTracker[] entityManagerArray;

   public Instance(BeanContext beanContext, Object primaryKey, Object containerId, Object bean, CreationalContext creationalContext, Map<String, Object> interceptors, Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> entityManagers, LockFactory.StatefulLock lock) {
      this.beanContext = beanContext;
      this.primaryKey = primaryKey;
      this.containerId = containerId;
      this.bean = bean;
      this.interceptors = interceptors;
      this.creationalContext = creationalContext;
      this.entityManagers = entityManagers;
      this.entityManagerArray = null;
      this.lock = lock;
   }

   public Instance(Object deploymentId, Object primaryKey, Object containerId, Object bean, CreationalContext creationalContext, Map<String, Object> interceptors, JtaEntityManagerRegistry.EntityManagerTracker[] entityManagerArray, LockFactory.StatefulLock lock) {
      this.beanContext = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getBeanContext(deploymentId);
      if (this.beanContext == null) {
         throw new IllegalArgumentException("Unknown deployment " + deploymentId);
      } else {
         this.primaryKey = primaryKey;
         this.bean = bean;
         this.creationalContext = creationalContext;
         this.interceptors = interceptors;
         this.entityManagerArray = entityManagerArray;
         this.lock = lock;
         this.containerId = containerId;
      }
   }

   public Duration getTimeOut() {
      return this.beanContext.getStatefulTimeout();
   }

   public synchronized boolean isInUse() {
      return this.inUse;
   }

   public synchronized void setInUse(boolean inUse) {
      this.inUse = inUse;
   }

   public synchronized BeanTransactionPolicy.SuspendedTransaction getBeanTransaction() {
      return this.beanTransaction;
   }

   public synchronized void setBeanTransaction(BeanTransactionPolicy.SuspendedTransaction beanTransaction) {
      this.beanTransaction = beanTransaction;
   }

   public synchronized Transaction getTransaction() {
      return this.transaction.size() > 0 ? (Transaction)this.transaction.peek() : null;
   }

   public LockFactory.StatefulLock getLock() {
      return this.lock;
   }

   public synchronized void setTransaction(Transaction transaction) {
      if (this.transaction.size() == 0 && transaction != null) {
         this.lock.lock();
         this.transaction.push(transaction);
      } else if (this.transaction.size() != 0 && transaction == null) {
         this.transaction.pop();
         this.lock.unlock();
      } else if (transaction != null) {
         this.transaction.push(transaction);
      }

   }

   public synchronized void releaseLock() {
      if (this.lock.isHeldByCurrentThread()) {
         this.lock.unlock();
      }

   }

   public synchronized Map<EntityManagerFactory, JtaEntityManagerRegistry.EntityManagerTracker> getEntityManagers(Index<EntityManagerFactory, BeanContext.EntityManagerConfiguration> factories) {
      if (this.entityManagers == null && this.entityManagerArray != null) {
         this.entityManagers = new HashMap();

         for(int i = 0; i < this.entityManagerArray.length; ++i) {
            EntityManagerFactory entityManagerFactory = factories.getKey(i);
            JtaEntityManagerRegistry.EntityManagerTracker entityManager = this.entityManagerArray[i];
            this.entityManagers.put(entityManagerFactory, entityManager);
         }
      }

      return this.entityManagers;
   }

   protected Object writeReplace() throws ObjectStreamException {
      if (this.inUse) {
         throw new IllegalStateException("Bean is still in use");
      } else if (this.beanTransaction != null) {
         throw new IllegalStateException("Bean is associated with a bean-managed transaction");
      } else {
         return new Serialization(this);
      }
   }

   private static class Serialization implements Serializable {
      private static final long serialVersionUID = 6002078080752564395L;
      public final Object deploymentId;
      public final Object primaryKey;
      public final Object containerId;
      public final Object bean;
      public final CreationalContext creationalContext;
      public final Map<String, Object> interceptors;
      public final JtaEntityManagerRegistry.EntityManagerTracker[] entityManagerArray;

      public Serialization(Instance i) {
         this.deploymentId = i.beanContext.getDeploymentID();
         this.primaryKey = i.primaryKey;
         this.bean = toSerializable(i.bean);
         this.creationalContext = i.creationalContext;
         this.interceptors = new HashMap(i.interceptors.size());

         for(Map.Entry<String, Object> e : i.interceptors.entrySet()) {
            if (e.getValue() == i.bean) {
               this.interceptors.put(e.getKey(), this.bean);
            } else {
               this.interceptors.put(e.getKey(), toSerializable(e.getValue()));
            }
         }

         if (i.entityManagerArray != null) {
            this.entityManagerArray = i.entityManagerArray;
         } else if (i.entityManagers != null) {
            this.entityManagerArray = (JtaEntityManagerRegistry.EntityManagerTracker[])i.entityManagers.values().toArray(new JtaEntityManagerRegistry.EntityManagerTracker[i.entityManagers.values().size()]);
         } else {
            this.entityManagerArray = null;
         }

         this.containerId = toSerializable(i.containerId);
      }

      private static Object toSerializable(Object obj) {
         return obj instanceof Serializable ? obj : new PojoSerialization(obj);
      }

      protected Object readResolve() throws ObjectStreamException {
         StatefulContainer statefulContainer = (StatefulContainer)StatefulContainer.class.cast(((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getContainer(this.containerId));
         return new Instance(this.deploymentId, this.primaryKey, this.containerId, this.bean, this.creationalContext, this.interceptors, this.entityManagerArray, statefulContainer.getLockFactory().newLock(this.deploymentId.toString()));
      }
   }
}
