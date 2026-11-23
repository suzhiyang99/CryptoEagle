package org.apache.openejb.core.transaction;

import java.rmi.RemoteException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.CoreUserTransaction;

public class TxBeanManaged extends JtaTransactionPolicy implements BeanTransactionPolicy {
   private final Transaction clientTx = this.suspendTransaction();

   public TxBeanManaged(TransactionManager transactionManager) throws SystemException {
      super(TransactionType.BeanManaged, transactionManager);
   }

   public boolean isNewTransaction() {
      return false;
   }

   public boolean isClientTransaction() {
      return false;
   }

   public Transaction getCurrentTransaction() {
      try {
         return this.getTransaction();
      } catch (SystemException var2) {
         throw new IllegalStateException("Exception getting current transaction");
      }
   }

   public UserTransaction getUserTransaction() {
      return new CoreUserTransaction(this.transactionManager);
   }

   public BeanTransactionPolicy.SuspendedTransaction suspendUserTransaction() throws SystemException {
      Transaction currentTx = this.suspendTransaction();
      return currentTx == null ? null : new JtaSuspendedTransaction(currentTx);
   }

   public void resumeUserTransaction(BeanTransactionPolicy.SuspendedTransaction suspendedTransaction) throws SystemException {
      if (suspendedTransaction == null) {
         throw new NullPointerException("suspendedTransaction is null");
      } else {
         Transaction beanTransaction = ((JtaSuspendedTransaction)suspendedTransaction).transaction;
         if (beanTransaction == null) {
            throw new SystemException("Bean transaction has already been resumed or destroyed");
         } else {
            try {
               this.resumeTransaction(beanTransaction);
            } catch (SystemException e) {
               suspendedTransaction.destroy();
               throw e;
            }
         }
      }
   }

   public void commit() throws ApplicationException, SystemException {
      try {
         Transaction currentTx = this.getTransaction();
         if (currentTx != null) {
            String message = "The EJB started a transaction but did not complete it.";
            logger.error("The EJB started a transaction but did not complete it.");

            try {
               this.rollbackTransaction(currentTx);
            } catch (Throwable var7) {
            }

            throw new ApplicationException(new RemoteException("The EJB started a transaction but did not complete it."));
         }

         this.fireNonTransactionalCompletion();
      } finally {
         this.resumeTransaction(this.clientTx);
      }

   }

   private static class JtaSuspendedTransaction implements BeanTransactionPolicy.SuspendedTransaction {
      private Transaction transaction;

      public JtaSuspendedTransaction(Transaction transaction) {
         if (transaction == null) {
            throw new NullPointerException("transaction is null");
         } else {
            this.transaction = transaction;
         }
      }

      public void destroy() {
         Transaction beanTransaction = this.transaction;
         this.transaction = null;
         if (beanTransaction != null) {
            try {
               beanTransaction.rollback();
            } catch (Exception var3) {
               JtaTransactionPolicy.logger.error("Error rolling back suspended transaction for discarded stateful session bean instance");
            }

         }
      }
   }
}
