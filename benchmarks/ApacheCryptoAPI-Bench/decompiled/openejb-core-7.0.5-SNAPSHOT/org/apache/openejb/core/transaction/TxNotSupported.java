package org.apache.openejb.core.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.SystemException;

public class TxNotSupported extends JtaTransactionPolicy {
   private final Transaction clientTx = this.suspendTransaction();

   public TxNotSupported(TransactionManager transactionManager) throws SystemException {
      super(TransactionType.NotSupported, transactionManager);
   }

   public boolean isNewTransaction() {
      return false;
   }

   public boolean isClientTransaction() {
      return false;
   }

   public Transaction getCurrentTransaction() {
      return null;
   }

   public void commit() throws SystemException {
      this.fireNonTransactionalCompletion();
      if (this.clientTx != null) {
         this.resumeTransaction(this.clientTx);
      }

   }
}
