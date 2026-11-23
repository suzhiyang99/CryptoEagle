package org.apache.openejb.resource.jdbc.dbcp;

import java.io.File;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DataSourceConnectionFactory;
import org.apache.commons.dbcp2.managed.DataSourceXAConnectionFactory;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.PasswordCipherFactory;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.BasicDataSourceUtil;
import org.apache.openejb.resource.jdbc.IsolationLevels;
import org.apache.openejb.resource.jdbc.plugin.DataSourcePlugin;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.reflection.Reflections;

public class BasicDataSource extends org.apache.commons.dbcp2.BasicDataSource implements Serializable {
   private volatile Logger logger;
   private volatile DataSource dsRef = null;
   private String passwordCipher;
   private String initialPassword;
   private JMXBasicDataSource jmxDs;
   private CommonDataSource delegate;
   private String name;

   public BasicDataSource() {
   }

   public BasicDataSource(String name) {
      this.setName(name);
   }

   public int getLoginTimeout() throws SQLException {
      return 0;
   }

   public void setDelegate(CommonDataSource delegate) {
      this.delegate = delegate;
   }

   protected ConnectionFactory createConnectionFactory() throws SQLException {
      if (this.delegate != null) {
         return (ConnectionFactory)(XADataSource.class.isInstance(this.delegate) ? new DataSourceXAConnectionFactory(OpenEJB.getTransactionManager(), (XADataSource)XADataSource.class.cast(this.delegate), this.getUsername(), this.getPassword()) : new DataSourceConnectionFactory((DataSource)DataSource.class.cast(this.delegate), this.getUsername(), this.getPassword()));
      } else {
         return super.createConnectionFactory();
      }
   }

   private void registerAsMbean(String name) {
      try {
         this.jmxDs = new JMXBasicDataSource(name, this);
      } catch (NoClassDefFoundError | Exception var3) {
         this.jmxDs = null;
      }

   }

   public synchronized String getPasswordCipher() {
      return this.passwordCipher;
   }

   public synchronized void setPasswordCipher(String passwordCipher) {
      this.passwordCipher = passwordCipher;
   }

   public synchronized void setPassword(String password) {
      this.initialPassword = password;
      super.setPassword(password);
   }

   public synchronized String getUserName() {
      return super.getUsername();
   }

   public synchronized void setUserName(String string) {
      super.setUsername(string);
   }

   public synchronized String getJdbcDriver() {
      return super.getDriverClassName();
   }

   public synchronized void setJdbcDriver(String string) {
      super.setDriverClassName(string);
   }

   public synchronized String getJdbcUrl() {
      return super.getUrl();
   }

   public void setJdbcUrl(String string) {
      super.setUrl(string);
   }

   public synchronized void setDefaultTransactionIsolation(String s) {
      if (s != null && !s.equals("")) {
         int level = IsolationLevels.getIsolationLevel(s);
         super.setDefaultTransactionIsolation(level);
      }
   }

   public synchronized void setMaxWait(int maxWait) {
      super.setMaxWaitMillis((long)maxWait);
   }

   protected DataSource createDataSource() throws SQLException {
      if (this.dsRef != null) {
         return this.dsRef;
      } else {
         synchronized(this) {
            if (this.dsRef != null) {
               return this.dsRef;
            } else {
               if (null != this.passwordCipher && !"PlainText".equals(this.passwordCipher)) {
                  PasswordCipher cipher = PasswordCipherFactory.getPasswordCipher(this.passwordCipher);
                  String plainPwd = cipher.decrypt(this.initialPassword.toCharArray());
                  super.setPassword(plainPwd);
               }

               DataSourcePlugin helper = BasicDataSourceUtil.getDataSourcePlugin(this.getUrl());
               if (helper != null) {
                  String currentUrl = this.getUrl();
                  String newUrl = helper.updatedUrl(currentUrl);
                  if (!currentUrl.equals(newUrl)) {
                     super.setUrl(newUrl);
                  }
               }

               if (helper != null && helper.enableUserDirHack()) {
                  Properties systemProperties = JavaSecurityManagers.getSystemProperties();
                  String userDir = systemProperties.getProperty("user.dir");

                  try {
                     File base = SystemInstance.get().getBase().getDirectory();
                     systemProperties.setProperty("user.dir", base.getAbsolutePath());

                     try {
                        super.createDataSource();
                     } catch (Throwable e) {
                        throw toSQLException(e);
                     }
                  } finally {
                     systemProperties.setProperty("user.dir", userDir);
                  }
               } else {
                  try {
                     super.createDataSource();
                  } catch (Throwable e) {
                     throw toSQLException(e);
                  }
               }

               return this.dsRef = (DataSource)DataSource.class.cast(Reflections.get(this, "dataSource"));
            }
         }
      }
   }

   public static SQLException toSQLException(Throwable e) {
      return e instanceof SQLException ? (SQLException)e : new SQLException("Failed to create DataSource", e);
   }

   public synchronized void close() throws SQLException {
      try {
         this.unregisterMBean();
      } catch (Exception var2) {
      }

      super.close();
      this.dsRef = null;
      this.logger = null;
   }

   private void unregisterMBean() {
      if (this.jmxDs != null) {
         this.jmxDs.unregister();
      }

   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      try {
         if (null == this.logger) {
            synchronized(this) {
               if (null == this.logger) {
                  this.logger = (Logger)Reflections.invokeByReflection(this.createDataSource(), "getParentLogger", new Class[0], (Object[])null);
               }
            }
         }

         return this.logger;
      } catch (Throwable var4) {
         throw new SQLFeatureNotSupportedException();
      }
   }

   public void setName(String name) {
      this.registerAsMbean(name);
      this.name = name;
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name) {
      return name;
   }

   Object writeReplace() throws ObjectStreamException {
      return new DataSourceSerialization(this.name);
   }
}
