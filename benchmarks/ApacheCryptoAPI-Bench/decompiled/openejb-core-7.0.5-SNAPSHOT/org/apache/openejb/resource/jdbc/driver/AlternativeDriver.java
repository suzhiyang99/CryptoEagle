package org.apache.openejb.resource.jdbc.driver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class AlternativeDriver implements Driver {
   private final Driver delegate;
   private final String url;
   private final Method getParentLogger;

   public AlternativeDriver(Driver driver, String url) {
      this.delegate = driver;
      this.url = url;
      Class<? extends Driver> clazz = this.delegate.getClass();
      this.getParentLogger = this.getMethod(clazz);
   }

   private Method getMethod(Class<? extends Driver> clazz) {
      try {
         return clazz.getMethod("getParentLogger");
      } catch (NoSuchMethodException var3) {
         return null;
      }
   }

   public Driver getDelegate() {
      return this.delegate;
   }

   public String getUrl() {
      return this.url;
   }

   public void register() throws SQLException {
      if (!this.isRegistered()) {
         DriverManager.registerDriver(this);
      }

      this.ensureFirst(new LinkedHashSet());
   }

   public void deregister() throws SQLException {
      if (this.isRegistered()) {
         DriverManager.deregisterDriver(this);
      }

   }

   private void ensureFirst(Set<Driver> seen) throws SQLException {
      Driver driver = DriverManager.getDriver(this.url);
      if (this != driver) {
         if (!seen.add(driver)) {
            throw new SQLException(String.format("Competing driver found for URL '%s' '%s'", this.url, driver));
         } else {
            DriverManager.deregisterDriver(driver);
            DriverManager.registerDriver(driver);
            this.ensureFirst(seen);
         }
      }
   }

   private boolean isRegistered() {
      Enumeration<Driver> drivers = DriverManager.getDrivers();

      while(drivers.hasMoreElements()) {
         Driver driver = (Driver)drivers.nextElement();
         if (driver == this) {
            return true;
         }
      }

      return false;
   }

   public Connection connect(String url, Properties info) throws SQLException {
      return this.acceptsURL(url) ? this.getDelegate().connect(url, info) : null;
   }

   public boolean acceptsURL(String url) throws SQLException {
      return this.url.equals(url);
   }

   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
      return this.getDelegate().getPropertyInfo(url, info);
   }

   public int getMajorVersion() {
      return this.getDelegate().getMajorVersion();
   }

   public int getMinorVersion() {
      return this.getDelegate().getMinorVersion();
   }

   public boolean jdbcCompliant() {
      return this.getDelegate().jdbcCompliant();
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      if (this.getParentLogger != null) {
         try {
            return (Logger)this.getParentLogger.invoke(this.delegate);
         } catch (IllegalAccessException e) {
            throw new SQLFeatureNotSupportedException(e);
         } catch (InvocationTargetException e) {
            if (e.getCause() instanceof SQLFeatureNotSupportedException) {
               throw (SQLFeatureNotSupportedException)e.getCause();
            } else if (e.getCause() instanceof RuntimeException) {
               throw (RuntimeException)e.getCause();
            } else {
               throw new SQLFeatureNotSupportedException(e.getCause());
            }
         }
      } else {
         throw new SQLFeatureNotSupportedException();
      }
   }
}
