package org.apache.openejb.core;

import java.util.HashMap;
import java.util.Map;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

public class SimpleTransactionSynchronizationRegistry implements TransactionSynchronizationRegistry {
   private final TransactionManager transactionManager;
   private final Map<Transaction, Map<Object, Object>> transactionResources = new HashMap();

   public SimpleTransactionSynchronizationRegistry(TransactionManager transactionManager) {
      this.transactionManager = transactionManager;
   }

   public Transaction getTransactionKey() {
      try {
         return this.transactionManager.getTransaction();
      } catch (SystemException var2) {
         return null;
      }
   }

   public Object getResource(Object key) {
      Transaction transaction = this.getActiveTransaction();
      Map<Object, Object> resources = (Map)this.transactionResources.get(transaction);
      return resources == null ? null : resources.get(key);
   }

   public void putResource(Object key, Object value) {
      Transaction transaction = this.getActiveTransaction();
      Map<Object, Object> resources = (Map)this.transactionResources.get(transaction);
      if (resources == null) {
         try {
            transaction.registerSynchronization(new RemoveTransactionResources(transaction));
         } catch (Exception e) {
            throw new IllegalStateException("No transaction active", e);
         }

         resources = new HashMap();
         this.transactionResources.put(transaction, resources);
      }

      resources.put(key, value);
   }

   public int getTransactionStatus() {
      try {
         return this.transactionManager.getStatus();
      } catch (SystemException var2) {
         return 6;
      }
   }

   public void registerInterposedSynchronization(Synchronization synchronization) {
      if (synchronization == null) {
         throw new NullPointerException("synchronization is null");
      } else {
         Transaction transaction = this.getActiveTransaction();

         try {
            transaction.registerSynchronization(synchronization);
         } catch (Exception var4) {
         }

      }
   }

   public boolean getRollbackOnly() {
      Transaction transaction = this.getActiveTransaction();

      try {
         return transaction.getStatus() == 1;
      } catch (Exception e) {
         throw new IllegalStateException("No transaction active", e);
      }
   }

   public void setRollbackOnly() {
      Transaction transaction = this.getActiveTransaction();

      try {
         transaction.setRollbackOnly();
      } catch (Exception e) {
         throw new IllegalStateException("No transaction active", e);
      }
   }

   private Transaction getActiveTransaction() {
      try {
         Transaction transaction = this.transactionManager.getTransaction();
         if (transaction == null) {
            throw new IllegalStateException("No transaction active");
         } else {
            int status = transaction.getStatus();
            if (status != 0 && status != 1) {
               throw new IllegalStateException("No transaction active");
            } else {
               return transaction;
            }
         }
      } catch (SystemException e) {
         throw new IllegalStateException("No transaction active", e);
      }
   }

   private class RemoveTransactionResources implements Synchronization {
      private final Transaction transaction;

      public RemoveTransactionResources(Transaction transaction) {
         this.transaction = transaction;
      }

      public void beforeCompletion() {
      }

      public void afterCompletion(int i) {
         SimpleTransactionSynchronizationRegistry.this.transactionResources.remove(this.transaction);
      }
   }
}
