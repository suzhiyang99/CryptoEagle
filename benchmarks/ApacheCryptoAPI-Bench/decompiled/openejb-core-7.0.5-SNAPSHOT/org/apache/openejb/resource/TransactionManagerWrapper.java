package org.apache.openejb.resource;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

public class TransactionManagerWrapper implements TransactionManager {
   private final String name;
   private final TransactionManager delegate;
   private final XAResourceWrapper xaResourceWrapper;

   public TransactionManagerWrapper(TransactionManager delegate, String name, XAResourceWrapper xaResourceWrapper) {
      this.delegate = delegate;
      this.name = name;
      this.xaResourceWrapper = xaResourceWrapper;
   }

   public void begin() throws NotSupportedException, SystemException {
      this.delegate.begin();
   }

   public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
      this.delegate.commit();
   }

   public int getStatus() throws SystemException {
      return this.delegate.getStatus();
   }

   public Transaction getTransaction() throws SystemException {
      Transaction tx = this.delegate.getTransaction();
      return tx == null ? null : new TransactionWrapper(this.delegate.getTransaction(), this.name, this.xaResourceWrapper);
   }

   public void resume(Transaction transaction) throws IllegalStateException, InvalidTransactionException, SystemException {
      this.delegate.resume(((TransactionWrapper)transaction).transaction);
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException {
      this.delegate.rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException {
      this.delegate.setRollbackOnly();
   }

   public void setTransactionTimeout(int i) throws SystemException {
      this.delegate.setTransactionTimeout(i);
   }

   public Transaction suspend() throws SystemException {
      return new TransactionWrapper(this.delegate.suspend(), this.name, this.xaResourceWrapper);
   }

   private static final class TransactionWrapper implements Transaction {
      private final Transaction transaction;
      private final String name;
      private final XAResourceWrapper xaResourceWrapper;

      private TransactionWrapper(Transaction transaction, String name, XAResourceWrapper xaResourceWrapper) {
         this.transaction = transaction;
         this.name = name;
         this.xaResourceWrapper = xaResourceWrapper;
      }

      public void commit() throws HeuristicMixedException, HeuristicRollbackException, RollbackException, SecurityException, SystemException {
         this.transaction.commit();
      }

      public boolean delistResource(XAResource xaResource, int i) throws IllegalStateException, SystemException {
         XAResource wrapper = this.xaResourceWrapper.wrap(xaResource, this.name);
         return this.transaction.delistResource(wrapper, i);
      }

      public boolean enlistResource(XAResource xaResource) throws IllegalStateException, RollbackException, SystemException {
         XAResource wrapper = this.xaResourceWrapper.wrap(xaResource, this.name);
         return this.transaction.enlistResource(wrapper);
      }

      public int getStatus() throws SystemException {
         return this.transaction.getStatus();
      }

      public void registerSynchronization(Synchronization synchronization) throws IllegalStateException, RollbackException, SystemException {
         this.transaction.registerSynchronization(synchronization);
      }

      public void rollback() throws IllegalStateException, SystemException {
         this.transaction.rollback();
      }

      public void setRollbackOnly() throws IllegalStateException, SystemException {
         this.transaction.setRollbackOnly();
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            TransactionWrapper that = (TransactionWrapper)o;
            return this.transaction.equals(that.transaction);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.transaction.hashCode();
      }
   }
}
