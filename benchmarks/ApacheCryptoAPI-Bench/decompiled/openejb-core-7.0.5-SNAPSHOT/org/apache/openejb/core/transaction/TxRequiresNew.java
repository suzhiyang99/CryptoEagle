package org.apache.openejb.core.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public class TxRequiresNew extends JtaTransactionPolicy {
   private final Transaction clientTx = this.suspendTransaction();
   private final Transaction currentTx = this.beginTransaction();

   public TxRequiresNew(TransactionManager transactionManager) throws SystemException {
      super(TransactionType.RequiresNew, transactionManager);
   }

   public boolean isNewTransaction() {
      return true;
   }

   public boolean isClientTransaction() {
      return false;
   }

   public Transaction getCurrentTransaction() {
      return this.currentTx;
   }

   public void commit() throws SystemException, ApplicationException {
      try {
         this.completeTransaction(this.currentTx);
      } finally {
         if (this.clientTx != null) {
            this.resumeTransaction(this.clientTx);
         } else {
            txLogger.debug("TX {0}: No transaction to resume", this.transactionType);
         }

      }

   }
}
