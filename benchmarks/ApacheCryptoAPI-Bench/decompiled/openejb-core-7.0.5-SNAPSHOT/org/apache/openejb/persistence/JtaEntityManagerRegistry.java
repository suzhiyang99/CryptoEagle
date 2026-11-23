package org.apache.openejb.persistence;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class JtaEntityManagerRegistry {
   private static final Logger logger;
   private final TransactionSynchronizationRegistry transactionRegistry;
   private final ThreadLocal<ExtendedRegistry> extendedRegistry = new ThreadLocal<ExtendedRegistry>() {
      protected ExtendedRegistry initialValue() {
         return JtaEntityManagerRegistry.this.new ExtendedRegistry();
      }
   };

   public JtaEntityManagerRegistry(TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
      this.transactionRegistry = transactionSynchronizationRegistry;
   }

   public EntityManager getEntityManager(EntityManagerFactory entityManagerFactory, Map properties, boolean extended, String unitName, SynchronizationType synchronizationType) throws IllegalStateException {
      if (entityManagerFactory == null) {
         throw new NullPointerException("entityManagerFactory is null");
      } else {
         EntityManagerTxKey txKey = new EntityManagerTxKey(entityManagerFactory);
         boolean transactionActive = this.isTransactionActive();
         if (transactionActive) {
            EntityManager entityManager = (EntityManager)this.transactionRegistry.getResource(txKey);
            if (entityManager != null) {
               return entityManager;
            }
         }

         if (extended) {
            EntityManagerTracker entityManagerTracker = this.getInheritedEntityManager(entityManagerFactory);
            if (entityManagerTracker != null && entityManagerTracker.getEntityManager() != null) {
               EntityManager entityManager = entityManagerTracker.getEntityManager();
               if (transactionActive) {
                  if (entityManagerTracker.autoJoinTx) {
                     entityManager.joinTransaction();
                  }

                  this.transactionRegistry.putResource(txKey, entityManager);
               }

               return entityManager;
            } else {
               throw new IllegalStateException("InternalError: an entity manager should already be registered for this extended persistence unit");
            }
         } else {
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

            logger.debug("Created EntityManager(unit=" + unitName + ", hashCode=" + entityManager.hashCode() + ")");
            if (transactionActive) {
               this.transactionRegistry.registerInterposedSynchronization(new CloseEntityManager(entityManager, unitName));
               this.transactionRegistry.putResource(txKey, entityManager);
            }

            return entityManager;
         }
      }
   }

   public void addEntityManagers(String deploymentId, Object primaryKey, Map<EntityManagerFactory, EntityManagerTracker> entityManagers) throws EntityManagerAlreadyRegisteredException {
      ((ExtendedRegistry)this.extendedRegistry.get()).addEntityManagers(new InstanceId(deploymentId, primaryKey), entityManagers);
   }

   public Map<EntityManagerFactory, EntityManagerTracker> removeEntityManagers(String deploymentId, Object primaryKey) {
      return ((ExtendedRegistry)this.extendedRegistry.get()).removeEntityManagers(new InstanceId(deploymentId, primaryKey));
   }

   public EntityManagerTracker getInheritedEntityManager(EntityManagerFactory entityManagerFactory) {
      return ((ExtendedRegistry)this.extendedRegistry.get()).getInheritedEntityManager(entityManagerFactory);
   }

   public void transactionStarted(String deploymentId, Object primaryKey) {
      ((ExtendedRegistry)this.extendedRegistry.get()).transactionStarted(new InstanceId(deploymentId, primaryKey));
   }

   public boolean isTransactionActive() {
      int txStatus = this.transactionRegistry.getTransactionStatus();
      boolean transactionActive = txStatus == 0 || txStatus == 1;
      return transactionActive;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB.createChild("persistence"), JtaEntityManager.class);
   }

   private class ExtendedRegistry {
      private final Map<InstanceId, Map<EntityManagerFactory, EntityManagerTracker>> entityManagersByDeploymentId;

      private ExtendedRegistry() {
         this.entityManagersByDeploymentId = new HashMap();
      }

      private void addEntityManagers(InstanceId instanceId, Map<EntityManagerFactory, EntityManagerTracker> entityManagers) throws EntityManagerAlreadyRegisteredException {
         if (instanceId == null) {
            throw new NullPointerException("instanceId is null");
         } else if (entityManagers == null) {
            throw new NullPointerException("entityManagers is null");
         } else {
            if (JtaEntityManagerRegistry.this.isTransactionActive()) {
               for(Map.Entry<EntityManagerFactory, EntityManagerTracker> entry : entityManagers.entrySet()) {
                  EntityManagerFactory entityManagerFactory = (EntityManagerFactory)entry.getKey();
                  EntityManagerTracker tracker = (EntityManagerTracker)entry.getValue();
                  EntityManager entityManager = tracker.getEntityManager();
                  EntityManagerTxKey txKey = new EntityManagerTxKey(entityManagerFactory);
                  EntityManager oldEntityManager = (EntityManager)JtaEntityManagerRegistry.this.transactionRegistry.getResource(txKey);
                  if (entityManager == oldEntityManager) {
                     break;
                  }

                  if (oldEntityManager != null) {
                     throw new EntityManagerAlreadyRegisteredException("Another entity manager is already registered for this persistence unit");
                  }

                  if (tracker.autoJoinTx) {
                     entityManager.joinTransaction();
                  }

                  JtaEntityManagerRegistry.this.transactionRegistry.putResource(txKey, entityManager);
               }
            }

            this.entityManagersByDeploymentId.put(instanceId, entityManagers);
         }
      }

      private Map<EntityManagerFactory, EntityManagerTracker> removeEntityManagers(InstanceId instanceId) {
         if (instanceId == null) {
            throw new NullPointerException("InstanceId is null");
         } else {
            return (Map)this.entityManagersByDeploymentId.remove(instanceId);
         }
      }

      private EntityManagerTracker getInheritedEntityManager(EntityManagerFactory entityManagerFactory) {
         if (entityManagerFactory == null) {
            throw new NullPointerException("entityManagerFactory is null");
         } else {
            for(Map<EntityManagerFactory, EntityManagerTracker> entityManagers : this.entityManagersByDeploymentId.values()) {
               EntityManagerTracker entityManagerTracker = (EntityManagerTracker)entityManagers.get(entityManagerFactory);
               if (entityManagerTracker != null) {
                  return entityManagerTracker;
               }
            }

            return null;
         }
      }

      private void transactionStarted(InstanceId instanceId) {
         if (instanceId == null) {
            throw new NullPointerException("instanceId is null");
         } else if (!JtaEntityManagerRegistry.this.isTransactionActive()) {
            throw new TransactionRequiredException();
         } else {
            Map<EntityManagerFactory, EntityManagerTracker> entityManagers = (Map)this.entityManagersByDeploymentId.get(instanceId);
            if (entityManagers != null) {
               for(Map.Entry<EntityManagerFactory, EntityManagerTracker> entry : entityManagers.entrySet()) {
                  EntityManagerFactory entityManagerFactory = (EntityManagerFactory)entry.getKey();
                  EntityManagerTracker value = (EntityManagerTracker)entry.getValue();
                  EntityManager entityManager = value.getEntityManager();
                  if (value.autoJoinTx) {
                     entityManager.joinTransaction();
                  }

                  EntityManagerTxKey txKey = new EntityManagerTxKey(entityManagerFactory);
                  JtaEntityManagerRegistry.this.transactionRegistry.putResource(txKey, entityManager);
               }

            }
         }
      }
   }

   private static class InstanceId {
      private final String deploymentId;
      private final Object primaryKey;

      public InstanceId(String deploymentId, Object primaryKey) {
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
            InstanceId that = (InstanceId)o;
            return this.deploymentId.equals(that.deploymentId) && this.primaryKey.equals(that.primaryKey);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.deploymentId.hashCode();
         result = 29 * result + this.primaryKey.hashCode();
         return result;
      }
   }

   public static class EntityManagerTracker {
      private transient int counter;
      private final EntityManager entityManager;
      private final boolean autoJoinTx;

      public EntityManagerTracker(EntityManager entityManager, boolean autoJoinTx) {
         if (entityManager == null) {
            throw new NullPointerException("entityManager is null.");
         } else {
            this.counter = 0;
            this.entityManager = entityManager;
            this.autoJoinTx = autoJoinTx;
         }
      }

      public int incCounter() {
         return this.counter++;
      }

      public int decCounter() {
         return this.counter--;
      }

      public EntityManager getEntityManager() {
         return this.entityManager;
      }
   }

   private static class CloseEntityManager implements Synchronization {
      private final EntityManager entityManager;
      private final String unitName;

      public CloseEntityManager(EntityManager entityManager, String unitName) {
         this.entityManager = entityManager;
         this.unitName = unitName;
      }

      public void beforeCompletion() {
      }

      public void afterCompletion(int i) {
         this.entityManager.close();
         JtaEntityManagerRegistry.logger.debug("Closed EntityManager(unit=" + this.unitName + ", hashCode=" + this.entityManager.hashCode() + ")");
      }
   }
}
