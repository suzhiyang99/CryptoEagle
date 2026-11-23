package org.apache.openejb.resource.jdbc.managed.xa;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.CommonDataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;

public class DataSourceXADataSource extends ManagedDataSource {
   private final XADataSource xaDataSource;

   public DataSourceXADataSource(CommonDataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry registry) {
      super(CommonDataSourceAdapter.wrap(ds), txMgr, registry, ds.hashCode());
      this.xaDataSource = (XADataSource)XADataSource.class.cast(ds);
   }

   public Connection getConnection() throws SQLException {
      XAConnection xaConnection = this.xaDataSource.getXAConnection();
      return xaConnection.getConnection();
   }

   public Connection getConnection(String username, String password) throws SQLException {
      XAConnection xaConnection = this.xaDataSource.getXAConnection(username, password);
      return xaConnection.getConnection();
   }
}
