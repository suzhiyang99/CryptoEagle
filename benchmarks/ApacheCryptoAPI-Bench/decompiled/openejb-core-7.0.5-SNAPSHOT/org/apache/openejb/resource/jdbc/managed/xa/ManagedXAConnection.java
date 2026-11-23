package org.apache.openejb.resource.jdbc.managed.xa;

import java.sql.SQLException;
import javax.sql.CommonDataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.openejb.resource.jdbc.managed.local.ManagedConnection;

public class ManagedXAConnection extends ManagedConnection {
   public ManagedXAConnection(CommonDataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry txRegistry, String user, String password) throws SQLException {
      super(ds, txMgr, txRegistry, user, password);
   }

   public XAResource getXAResource() throws SQLException {
      if (this.xaResource == null) {
         this.newConnection();
      }

      return this.xaResource;
   }

   protected void setAutoCommit(boolean value) throws SQLException {
   }
}
