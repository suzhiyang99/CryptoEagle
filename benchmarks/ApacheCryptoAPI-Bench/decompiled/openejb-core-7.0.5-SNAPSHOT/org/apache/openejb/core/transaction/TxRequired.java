package org.apache.openejb.core.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public class TxRequired extends JtaTransactionPolicy {
   private final Transaction clientTx = this.getTransaction();
   private final Transaction currentTx;

   public TxRequired(TransactionManager transactionManager) throws SystemException {
      super(TransactionType.Required, transactionManager);
      if (this.clientTx == null) {
         this.currentTx = this.beginTransaction();
      } else {
         this.currentTx = this.clientTx;
      }

   }

   public boolean isNewTransaction() {
      return this.clientTx == null;
   }

   public boolean isClientTransaction() {
      return !this.isNewTransaction();
   }

   public Transaction getCurrentTransaction() {
      return this.currentTx;
   }

   public void commit() throws ApplicationException, SystemException {
      if (this.clientTx == null) {
         this.completeTransaction(this.currentTx);
      } else {
         this.fireNonTransactionalCompletion();
      }

   }
}
