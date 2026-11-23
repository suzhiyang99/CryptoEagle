package org.apache.openejb.core.transaction;

import javax.transaction.UserTransaction;
import org.apache.openejb.SystemException;

public interface BeanTransactionPolicy extends TransactionPolicy {
   UserTransaction getUserTransaction();

   SuspendedTransaction suspendUserTransaction() throws SystemException;

   void resumeUserTransaction(SuspendedTransaction var1) throws SystemException;

   public interface SuspendedTransaction {
      void destroy();
   }
}
