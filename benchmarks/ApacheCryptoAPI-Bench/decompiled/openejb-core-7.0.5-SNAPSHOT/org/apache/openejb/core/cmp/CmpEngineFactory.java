package org.apache.openejb.core.cmp;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJBException;

public interface CmpEngineFactory {
   TransactionManager getTransactionManager();

   void setTransactionManager(TransactionManager var1);

   void setTransactionSynchronizationRegistry(TransactionSynchronizationRegistry var1);

   void setCmpCallback(CmpCallback var1);

   CmpEngine create() throws OpenEJBException;
}
