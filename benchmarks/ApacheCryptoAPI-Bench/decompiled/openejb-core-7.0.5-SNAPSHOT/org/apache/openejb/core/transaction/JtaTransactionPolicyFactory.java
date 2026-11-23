package org.apache.openejb.core.transaction;

import javax.transaction.TransactionManager;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public class JtaTransactionPolicyFactory implements TransactionPolicyFactory {
   private final TransactionManager transactionManager;

   public JtaTransactionPolicyFactory(TransactionManager transactionManager) {
      if (transactionManager == null) {
         throw new NullPointerException("transactionManager is null");
      } else {
         this.transactionManager = transactionManager;
      }
   }

   public TransactionPolicy createTransactionPolicy(TransactionType type) throws SystemException, ApplicationException {
      switch (type) {
         case Required:
            return new TxRequired(this.transactionManager);
         case RequiresNew:
            return new TxRequiresNew(this.transactionManager);
         case Supports:
            return new TxSupports(this.transactionManager);
         case NotSupported:
            return new TxNotSupported(this.transactionManager);
         case Mandatory:
            return new TxMandatory(this.transactionManager);
         case Never:
            return new TxNever(this.transactionManager);
         case BeanManaged:
            return new TxBeanManaged(this.transactionManager);
         default:
            throw new SystemException(new IllegalArgumentException("Unknown transaction type " + type));
      }
   }
}
