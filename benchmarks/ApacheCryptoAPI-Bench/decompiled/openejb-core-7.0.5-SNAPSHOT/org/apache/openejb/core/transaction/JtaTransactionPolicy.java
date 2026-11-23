package org.apache.openejb.core.transaction;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public abstract class JtaTransactionPolicy implements TransactionPolicy {
   protected static final Logger logger;
   protected static final Logger txLogger;
   protected final TransactionType transactionType;
   protected final TransactionManager transactionManager;
   private final TransactionSynchronizationRegistry synchronizationRegistry;
   private Map<Object, Object> resources;
   private final List<TransactionPolicy.TransactionSynchronization> synchronizations = new LinkedList();
   private boolean rollbackOnly;

   public JtaTransactionPolicy(TransactionType transactionType, TransactionManager transactionManager) {
      this.transactionType = transactionType;
      this.transactionManager = transactionManager;
      this.synchronizationRegistry = (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class);
   }

   public TransactionType getTransactionType() {
      return this.transactionType;
   }

   public abstract Transaction getCurrentTransaction();

   public boolean isTransactionActive() {
      Transaction trasaction = this.getCurrentTransaction();
      if (trasaction == null) {
         return false;
      } else {
         try {
            int status = trasaction.getStatus();
            return status == 0 || status == 1;
         } catch (SystemException var3) {
            return false;
         }
      }
   }

   public boolean isRollbackOnly() {
      Transaction trasaction = this.getCurrentTransaction();
      if (trasaction != null) {
         try {
            int status = trasaction.getStatus();
            return status == 1;
         } catch (SystemException var3) {
            return false;
         }
      } else {
         return this.rollbackOnly;
      }
   }

   public void setRollbackOnly() {
      this.setRollbackOnly((Throwable)null);
   }

   public void setRollbackOnly(Throwable reason) {
      Transaction trasaction = this.getCurrentTransaction();
      if (trasaction != null) {
         this.setRollbackOnly(trasaction, reason);
      } else {
         this.rollbackOnly = true;
      }

   }

   public Object getResource(Object key) {
      if (this.isTransactionActive()) {
         return this.synchronizationRegistry.getResource(key);
      } else {
         return this.resources == null ? null : this.resources.get(key);
      }
   }

   public void putResource(Object key, Object value) {
      if (this.isTransactionActive()) {
         this.synchronizationRegistry.putResource(key, value);
      }

      if (this.resources == null) {
         this.resources = new LinkedHashMap();
      }

      this.resources.put(key, value);
   }

   public Object removeResource(Object key) {
      if (this.isTransactionActive()) {
         Object value = this.synchronizationRegistry.getResource(key);
         this.synchronizationRegistry.putResource(key, (Object)null);
         return value;
      } else {
         return this.resources == null ? null : this.resources.remove(key);
      }
   }

   public void registerSynchronization(final TransactionPolicy.TransactionSynchronization synchronization) {
      if (this.isTransactionActive()) {
         this.synchronizationRegistry.registerInterposedSynchronization(new Synchronization() {
            public void beforeCompletion() {
               synchronization.beforeCompletion();
            }

            public void afterCompletion(int s) {
               TransactionPolicy.TransactionSynchronization.Status status;
               if (s == 3) {
                  status = TransactionPolicy.TransactionSynchronization.Status.COMMITTED;
               } else if (s == 4) {
                  status = TransactionPolicy.TransactionSynchronization.Status.ROLLEDBACK;
               } else {
                  status = TransactionPolicy.TransactionSynchronization.Status.UNKNOWN;
               }

               synchronization.afterCompletion(status);
            }
         });
      } else {
         this.synchronizations.add(synchronization);
      }

   }

   protected void fireNonTransactionalCompletion() {
      for(TransactionPolicy.TransactionSynchronization synchronization : new ArrayList(this.synchronizations)) {
         try {
            synchronization.beforeCompletion();
         } catch (Throwable var6) {
            logger.error("Exception thrown from beforeCompletion() of TransactionSynchronization " + synchronization);
         }
      }

      TransactionPolicy.TransactionSynchronization.Status status = this.isRollbackOnly() ? TransactionPolicy.TransactionSynchronization.Status.ROLLEDBACK : TransactionPolicy.TransactionSynchronization.Status.COMMITTED;

      for(TransactionPolicy.TransactionSynchronization synchronization : new ArrayList(this.synchronizations)) {
         try {
            synchronization.afterCompletion(status);
         } catch (Exception var5) {
            logger.error("Exception thrown from afterCompletion(" + status + ") of TransactionSynchronization " + synchronization);
         }
      }

   }

   public void enlistResource(XAResource xaResource) throws org.apache.openejb.SystemException {
      Transaction transaction = this.getCurrentTransaction();
      if (transaction != null) {
         try {
            if (transaction.enlistResource(xaResource)) {
               return;
            }
         } catch (Exception e) {
            throw new org.apache.openejb.SystemException("Unable to enlist xa resource in the transaction", e);
         }
      }

      throw new org.apache.openejb.SystemException("Unable to enlist xa resource in the transaction");
   }

   public String toString() {
      return this.transactionType.toString();
   }

   protected Transaction getTransaction() throws org.apache.openejb.SystemException {
      try {
         return this.transactionManager.getTransaction();
      } catch (SystemException e) {
         txLogger.error("The Transaction Manager has encountered an unexpected error condition while attempting to obtain current transaction: {0}", e.getMessage());
         throw new org.apache.openejb.SystemException(e);
      }
   }

   protected void setRollbackOnly(Transaction tx, Throwable reason) {
      try {
         if (tx != null && tx.getStatus() == 0) {
            if (reason == null) {
               tx.setRollbackOnly();
            } else {
               Method setRollbackOnly = this.setRollbackOnlyMethod(tx);
               if (setRollbackOnly != null) {
                  setRollbackOnly.invoke(tx, reason);
               } else {
                  tx.setRollbackOnly();
               }
            }

            txLogger.debug("TX {0}: setRollbackOnly() on transaction {1}", this.transactionType, tx);
         }
      } catch (Exception e) {
         txLogger.error("Exception during setRollbackOnly()", (Throwable)e);
         throw new IllegalStateException("No transaction active", e);
      }
   }

   private Method setRollbackOnlyMethod(Transaction tx) {
      try {
         return tx.getClass().getMethod("setRollbackOnly", Throwable.class);
      } catch (Throwable var3) {
         return null;
      }
   }

   protected Transaction beginTransaction() throws org.apache.openejb.SystemException {
      Transaction transaction;
      try {
         this.transactionManager.begin();
         transaction = this.transactionManager.getTransaction();
      } catch (Exception e) {
         txLogger.error("The Transaction Manager has encountered an unexpected error condition while attempting to begin a new transaction: {0}", e.getMessage());
         throw new org.apache.openejb.SystemException(e);
      }

      if (transaction == null) {
         throw new org.apache.openejb.SystemException("Failed to begin a new transaction");
      } else {
         txLogger.debug("TX {0}: Started transaction {1}", this.transactionType, transaction);
         return transaction;
      }
   }

   protected Transaction suspendTransaction() throws org.apache.openejb.SystemException {
      try {
         Transaction tx = this.transactionManager.suspend();
         if (tx == null) {
            txLogger.debug("TX {0}: No transaction to suspend", this.transactionType);
         } else {
            txLogger.debug("TX {0}: Suspended transaction {1}", this.transactionType, tx);
         }

         return tx;
      } catch (SystemException se) {
         txLogger.error("Exception during suspend()", (Throwable)se);
         throw new org.apache.openejb.SystemException(se);
      }
   }

   protected void resumeTransaction(Transaction tx) throws org.apache.openejb.SystemException {
      try {
         if (tx == null) {
            txLogger.debug("TX {0}: No transaction to resume", this.transactionType);
         } else {
            txLogger.debug("TX {0}: Resuming transaction {1}", this.transactionType, tx);
            this.transactionManager.resume(tx);
         }

      } catch (InvalidTransactionException ite) {
         txLogger.error("Could not resume the client's transaction, the transaction is no longer valid: {0}", ite.getMessage());
         throw new org.apache.openejb.SystemException(ite);
      } catch (IllegalStateException e) {
         txLogger.error("Could not resume the client's transaction: {0}", e.getMessage());
         throw new org.apache.openejb.SystemException(e);
      } catch (SystemException e) {
         txLogger.error("Could not resume the client's transaction: The transaction reported a system exception: {0}", e.getMessage());
         throw new org.apache.openejb.SystemException(e);
      }
   }

   protected void completeTransaction(Transaction tx) throws org.apache.openejb.SystemException, ApplicationException {
      boolean shouldRollback;
      try {
         shouldRollback = tx.getStatus() != 0;
      } catch (SystemException e) {
         txLogger.error("The Transaction Manager has encountered an unexpected error condition while attempting to obtain transaction status: {0}", e.getMessage());
         throw new org.apache.openejb.SystemException(e);
      }

      if (shouldRollback) {
         this.rollbackTransaction(tx);
      } else {
         try {
            txLogger.debug("TX {0}: Committing transaction {1}", this.transactionType, tx);
            if (tx.equals(this.transactionManager.getTransaction())) {
               this.transactionManager.commit();
            } else {
               tx.commit();
            }

         } catch (RollbackException e) {
            txLogger.debug("The transaction has been rolled back rather than commited: {0}", e.getMessage());
            Throwable txe = (new TransactionRolledbackException("Transaction was rolled back, presumably because setRollbackOnly was called during a synchronization")).initCause(e);
            throw new ApplicationException(txe);
         } catch (HeuristicMixedException e) {
            txLogger.debug("A heuristic decision was made, some relevant updates have been committed while others have been rolled back: {0}", e.getMessage());
            throw new ApplicationException(new RemoteException("A heuristic decision was made, some relevant updates have been committed while others have been rolled back", e));
         } catch (HeuristicRollbackException e) {
            txLogger.debug("A heuristic decision was made while commiting the transaction, some relevant updates have been rolled back: {0}", e.getMessage());
            throw new ApplicationException(new RemoteException("A heuristic decision was made while commiting the transaction, some relevant updates have been rolled back", e));
         } catch (SecurityException e) {
            txLogger.error("The current thread is not allowed to commit the transaction: {0}", e.getMessage());
            throw new org.apache.openejb.SystemException(e);
         } catch (IllegalStateException e) {
            txLogger.error("The current thread is not associated with a transaction: {0}", e.getMessage());
            throw new org.apache.openejb.SystemException(e);
         } catch (SystemException e) {
            txLogger.error("The Transaction Manager has encountered an unexpected error condition while attempting to commit the transaction: {0}", e.getMessage());
            throw new org.apache.openejb.SystemException(e);
         }
      }
   }

   protected void rollbackTransaction(Transaction tx) throws org.apache.openejb.SystemException {
      try {
         txLogger.debug("TX {0}: Rolling back transaction {1}", this.transactionType, tx);
         if (tx.equals(this.transactionManager.getTransaction())) {
            this.transactionManager.rollback();
         } else {
            tx.rollback();
         }

      } catch (SystemException | IllegalStateException e) {
         logger.error("The TransactionManager reported an exception while attempting to rollback the transaction: " + ((Exception)e).getMessage());
         throw new org.apache.openejb.SystemException(e);
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
      txLogger = Logger.getInstance(LogCategory.TRANSACTION, "org.apache.openejb.util.resources");
   }
}
