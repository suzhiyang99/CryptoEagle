package org.apache.openejb.core.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.SystemException;

public class TxSupports extends JtaTransactionPolicy {
   private final Transaction clientTx = this.getTransaction();

   public TxSupports(TransactionManager transactionManager) throws SystemException {
      super(TransactionType.Supports, transactionManager);
   }

   public boolean isNewTransaction() {
      return false;
   }

   public boolean isClientTransaction() {
      return this.clientTx != null;
   }

   public Transaction getCurrentTransaction() {
      return this.clientTx;
   }

   public void commit() {
      this.fireNonTransactionalCompletion();
   }
}
