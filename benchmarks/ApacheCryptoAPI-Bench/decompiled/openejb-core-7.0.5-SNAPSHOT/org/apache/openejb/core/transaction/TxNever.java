package org.apache.openejb.core.transaction;

import java.rmi.RemoteException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public class TxNever extends JtaTransactionPolicy {
   public TxNever(TransactionManager transactionManager) throws SystemException, ApplicationException {
      super(TransactionType.Never, transactionManager);
      if (this.getTransaction() != null) {
         throw new ApplicationException(new RemoteException("Transactions not supported"));
      }
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

   public void commit() {
      this.fireNonTransactionalCompletion();
   }
}
