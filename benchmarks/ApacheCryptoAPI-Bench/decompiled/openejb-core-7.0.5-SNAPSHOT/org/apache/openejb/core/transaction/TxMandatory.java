package org.apache.openejb.core.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public class TxMandatory extends JtaTransactionPolicy {
   private final Transaction clientTx = this.getTransaction();

   public TxMandatory(TransactionManager transactionManager) throws SystemException, ApplicationException {
      super(TransactionType.Mandatory, transactionManager);
      if (this.clientTx == null) {
         throw new ApplicationException(new TransactionRequiredException());
      }
   }

   public boolean isNewTransaction() {
      return false;
   }

   public boolean isClientTransaction() {
      return true;
   }

   public Transaction getCurrentTransaction() {
      return this.clientTx;
   }

   public void commit() {
      this.fireNonTransactionalCompletion();
   }
}
