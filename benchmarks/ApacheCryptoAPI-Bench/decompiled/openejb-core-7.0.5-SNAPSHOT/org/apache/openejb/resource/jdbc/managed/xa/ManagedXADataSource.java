package org.apache.openejb.resource.jdbc.managed.xa;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.CommonDataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;

public class ManagedXADataSource extends ManagedDataSource {
   private static final Class<?>[] CONNECTION_CLASS = new Class[]{Connection.class};
   private final TransactionManager txMgr;

   public ManagedXADataSource(CommonDataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry registry) {
      super(ds, txMgr, registry, ds.hashCode());
      this.txMgr = txMgr;
   }

   public Connection getConnection() throws SQLException {
      return this.managedXA((String)null, (String)null);
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return this.managedXA(username, password);
   }

   private Connection managedXA(String u, String p) throws SQLException {
      return (Connection)Connection.class.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), CONNECTION_CLASS, new ManagedXAConnection(this.delegate, this.txMgr, this.registry, u, p)));
   }
}
