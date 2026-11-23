package org.apache.openejb.resource.jdbc.managed.local;

import java.io.ObjectStreamException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

public class ManagedDataSource implements DataSource, Serializable {
   private static final Class<?>[] CONNECTION_CLASS = new Class[]{Connection.class};
   protected final CommonDataSource delegate;
   protected final TransactionManager transactionManager;
   protected final TransactionSynchronizationRegistry registry;
   protected final int hashCode;

   protected ManagedDataSource(CommonDataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry txRegistry, int hc) {
      this.delegate = ds;
      this.hashCode = hc;
      this.transactionManager = txMgr;
      this.registry = txRegistry;
   }

   public ManagedDataSource(DataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry txRegistry) {
      this(ds, txMgr, txRegistry, ds.hashCode());
   }

   public Connection getConnection() throws SQLException {
      return this.managed((String)null, (String)null);
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return this.managed(username, password);
   }

   public PrintWriter getLogWriter() throws SQLException {
      return this.delegate.getLogWriter();
   }

   public void setLogWriter(PrintWriter out) throws SQLException {
      this.delegate.setLogWriter(out);
   }

   public void setLoginTimeout(int seconds) throws SQLException {
      this.delegate.setLoginTimeout(seconds);
   }

   public int getLoginTimeout() throws SQLException {
      return this.delegate.getLoginTimeout();
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      return (T)(DataSource.class.isInstance(this.delegate) ? ((DataSource)DataSource.class.cast(this.delegate)).unwrap(iface) : null);
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return DataSource.class.isInstance(this.delegate) && ((DataSource)DataSource.class.cast(this.delegate)).isWrapperFor(iface);
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.delegate.getParentLogger();
   }

   private Connection managed(String u, String p) {
      return (Connection)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), CONNECTION_CLASS, new ManagedConnection(this.delegate, this.transactionManager, this.registry, u, p));
   }

   public CommonDataSource getDelegate() {
      return this.delegate;
   }

   public boolean equals(Object o) {
      return CommonDataSource.class.isInstance(o) && this.hashCode == o.hashCode();
   }

   public int hashCode() {
      return this.hashCode;
   }

   Object writeReplace() throws ObjectStreamException {
      if (Serializable.class.isInstance(this.delegate)) {
         return this.delegate;
      } else {
         throw new ObjectStreamException(this.delegate + " not serializable") {
         };
      }
   }
}
