package org.apache.openejb.resource.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.router.Router;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.reflection.Reflections;

public class RoutedDataSource implements DataSource {
   private static final String OPENEJB_RESOURCE_PREFIX = "openejb:Resource/";
   protected Router delegate;

   public RoutedDataSource() {
   }

   public RoutedDataSource(Router router) {
      this.delegate = router;
   }

   public RoutedDataSource(String router) {
      this.setRouter(router);
   }

   public void setRouter(String router) {
      Object o;
      try {
         o = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb:Resource/" + router);
      } catch (NamingException e) {
         throw new IllegalArgumentException("Can't find router [" + router + "]", e);
      }

      if (Router.class.isInstance(o)) {
         this.delegate = (Router)Router.class.cast(o);
      } else {
         throw new IllegalArgumentException(o + " is not a router");
      }
   }

   public PrintWriter getLogWriter() throws SQLException {
      return this.getTargetDataSource() == null ? null : this.getTargetDataSource().getLogWriter();
   }

   public void setLogWriter(PrintWriter out) throws SQLException {
      if (this.getTargetDataSource() != null) {
         this.getTargetDataSource().setLogWriter(out);
      }

   }

   public void setLoginTimeout(int seconds) throws SQLException {
      if (this.getTargetDataSource() != null) {
         this.getTargetDataSource().setLoginTimeout(seconds);
      }

   }

   public int getLoginTimeout() throws SQLException {
      return this.getTargetDataSource() == null ? -1 : this.getTargetDataSource().getLoginTimeout();
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      return (T)(this.getTargetDataSource() == null ? null : Reflections.invokeByReflection(this.getTargetDataSource(), "unwrap", new Class[]{Class.class}, new Object[]{iface}));
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.getTargetDataSource() == null ? null : (Logger)Reflections.invokeByReflection(this.getTargetDataSource(), "getParentLogger", new Class[0], (Object[])null);
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return this.getTargetDataSource() == null ? false : (Boolean)Reflections.invokeByReflection(this.getTargetDataSource(), "isWrapperFor", new Class[]{Class.class}, new Object[]{iface});
   }

   public Connection getConnection() throws SQLException {
      return this.getTargetDataSource().getConnection();
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return this.getTargetDataSource().getConnection(username, password);
   }

   public Router getDelegate() {
      if (this.delegate == null) {
         throw new IllegalStateException("a router has to be defined");
      } else {
         return this.delegate;
      }
   }

   private DataSource getTargetDataSource() {
      return this.getDelegate().getDataSource();
   }
}
