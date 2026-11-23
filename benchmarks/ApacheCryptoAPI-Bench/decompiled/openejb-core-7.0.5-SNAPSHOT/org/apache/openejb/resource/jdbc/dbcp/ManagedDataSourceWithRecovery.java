package org.apache.openejb.resource.jdbc.dbcp;

import javax.transaction.TransactionManager;
import org.apache.openejb.resource.TransactionManagerWrapper;
import org.apache.openejb.resource.XAResourceWrapper;

public class ManagedDataSourceWithRecovery extends BasicManagedDataSource {
   private TransactionManager suppliedTransactionManager;
   private final XAResourceWrapper xaResourceWrapper;

   public ManagedDataSourceWithRecovery(String name, XAResourceWrapper xaResourceWrapper) {
      super(name);
      this.xaResourceWrapper = xaResourceWrapper;
   }

   public void setTransactionManager(TransactionManager transactionManager) {
      this.suppliedTransactionManager = transactionManager;
   }

   protected void wrapTransactionManager() {
      if (this.suppliedTransactionManager != null) {
         super.setTransactionManager(new TransactionManagerWrapper(this.suppliedTransactionManager, this.getUrl(), this.xaResourceWrapper));
      }

   }
}
