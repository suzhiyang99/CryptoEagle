package org.apache.openejb.resource.jdbc.dbcp;

import java.io.File;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.managed.ManagedConnection;
import org.apache.commons.dbcp2.managed.ManagedDataSource;
import org.apache.commons.dbcp2.managed.TransactionRegistry;
import org.apache.commons.pool2.ObjectPool;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.PasswordCipherFactory;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.BasicDataSourceUtil;
import org.apache.openejb.resource.jdbc.IsolationLevels;
import org.apache.openejb.resource.jdbc.plugin.DataSourcePlugin;
import org.apache.openejb.resource.jdbc.pool.XADataSourceResource;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.reflection.Reflections;

public class BasicManagedDataSource extends org.apache.commons.dbcp2.managed.BasicManagedDataSource implements Serializable {
   private static final ReentrantLock lock = new ReentrantLock();
   private final String name;
   private Logger logger;
   private String passwordCipher;
   private String initialPassword;
   private JMXBasicDataSource jmxDs;

   public BasicManagedDataSource(String name) {
      this.registerAsMbean(name);
      this.name = name;
   }

   protected DataSource createDataSourceInstance() throws SQLException {
      final TransactionRegistry transactionRegistry = this.getTransactionRegistry();
      if (transactionRegistry == null) {
         throw new IllegalStateException("TransactionRegistry has not been set");
      } else if (this.getConnectionPool() == null) {
         throw new IllegalStateException("Pool has not been set");
      } else {
         PoolingDataSource<PoolableConnection> pds = new ManagedDataSource<PoolableConnection>(this.getConnectionPool(), transactionRegistry) {
            public Connection getConnection() throws SQLException {
               return new ManagedConnection<PoolableConnection>(this.getPool(), transactionRegistry, this.isAccessToUnderlyingConnectionAllowed()) {
                  public void close() throws SQLException {
                     if (!this.isClosedInternal()) {
                        try {
                           if (null != this.getDelegateInternal()) {
                              super.close();
                           }
                        } finally {
                           this.setClosedInternal(true);
                        }
                     }

                  }

                  public boolean isClosed() throws SQLException {
                     return this.isClosedInternal() || null != this.getDelegateInternal() && ((PoolableConnection)this.getDelegateInternal()).isClosed();
                  }
               };
            }
         };
         pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
         return pds;
      }
   }

   public int getLoginTimeout() throws SQLException {
      return 0;
   }

   protected ConnectionFactory createConnectionFactory() throws SQLException {
      String xaDataSource = this.getXADataSource();
      if (xaDataSource != null & this.getXaDataSourceInstance() == null) {
         try {
            try {
               Thread.currentThread().getContextClassLoader().loadClass(xaDataSource);
            } catch (NoClassDefFoundError | ClassNotFoundException var3) {
               this.setJndiXaDataSource(xaDataSource);
            }
         } catch (Throwable var4) {
         }
      }

      return super.createConnectionFactory();
   }

   private void setJndiXaDataSource(String xaDataSource) {
      this.setXaDataSourceInstance(XADataSourceResource.proxy(this.getDriverClassLoader() != null ? this.getDriverClassLoader() : Thread.currentThread().getContextClassLoader(), xaDataSource));
      if (this.getTransactionManager() == null) {
         this.setTransactionManager(OpenEJB.getTransactionManager());
      }

   }

   private void registerAsMbean(String name) {
      try {
         this.jmxDs = new JMXBasicDataSource(name, this);
      } catch (NoClassDefFoundError | Exception var3) {
         this.jmxDs = null;
      }

   }

   public String getPasswordCipher() {
      ReentrantLock l = lock;
      l.lock();

      String var2;
      try {
         var2 = this.passwordCipher;
      } finally {
         l.unlock();
      }

      return var2;
   }

   public void setPasswordCipher(String passwordCipher) {
      ReentrantLock l = lock;
      l.lock();

      try {
         this.passwordCipher = passwordCipher;
      } finally {
         l.unlock();
      }

   }

   public void setPassword(String password) {
      ReentrantLock l = lock;
      l.lock();

      try {
         this.initialPassword = password;
         super.setPassword(password);
      } finally {
         l.unlock();
      }

   }

   public String getUserName() {
      ReentrantLock l = lock;
      l.lock();

      String var2;
      try {
         var2 = super.getUsername();
      } finally {
         l.unlock();
      }

      return var2;
   }

   public void setUserName(String string) {
      ReentrantLock l = lock;
      l.lock();

      try {
         super.setUsername(string);
      } finally {
         l.unlock();
      }

   }

   public String getJdbcDriver() {
      ReentrantLock l = lock;
      l.lock();

      String var2;
      try {
         var2 = super.getDriverClassName();
      } finally {
         l.unlock();
      }

      return var2;
   }

   public void setJdbcDriver(String string) {
      ReentrantLock l = lock;
      l.lock();

      try {
         super.setDriverClassName(string);
      } finally {
         l.unlock();
      }

   }

   public String getJdbcUrl() {
      ReentrantLock l = lock;
      l.lock();

      String var2;
      try {
         var2 = super.getUrl();
      } finally {
         l.unlock();
      }

      return var2;
   }

   public void setJdbcUrl(String string) {
      ReentrantLock l = lock;
      l.lock();

      try {
         super.setUrl(string);
      } finally {
         l.unlock();
      }

   }

   public void setDefaultTransactionIsolation(String s) {
      ReentrantLock l = lock;
      l.lock();

      try {
         if (s != null && !s.equals("")) {
            int level = IsolationLevels.getIsolationLevel(s);
            super.setDefaultTransactionIsolation(level);
            return;
         }
      } finally {
         l.unlock();
      }

   }

   public void setMaxWait(int maxWait) {
      ReentrantLock l = lock;
      l.lock();

      try {
         super.setMaxWaitMillis((long)maxWait);
      } finally {
         l.unlock();
      }

   }

   protected DataSource createDataSource() throws SQLException {
      ReentrantLock l = lock;
      l.lock();

      DataSource var3;
      try {
         Object dataSource = Reflections.get(this, "dataSource");
         if (dataSource == null) {
            if (null != this.passwordCipher) {
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

            this.wrapTransactionManager();
            if (helper != null && helper.enableUserDirHack()) {
               Properties systemProperties = JavaSecurityManagers.getSystemProperties();
               String userDir = systemProperties.getProperty("user.dir");

               try {
                  File base = SystemInstance.get().getBase().getDirectory();
                  systemProperties.setProperty("user.dir", base.getAbsolutePath());

                  Throwable e;
                  try {
                     e = super.createDataSource();
                     return e;
                  } catch (Throwable var18) {
                     e = var18;
                     throw BasicDataSource.toSQLException(var18);
                  }
               } finally {
                  systemProperties.setProperty("user.dir", userDir);
               }
            }

            try {
               DataSource e = super.createDataSource();
               return e;
            } catch (Throwable e) {
               throw BasicDataSource.toSQLException(e);
            }
         }

         var3 = (DataSource)DataSource.class.cast(dataSource);
      } finally {
         l.unlock();
      }

      return var3;
   }

   protected void wrapTransactionManager() {
   }

   public void close() throws SQLException {
      ReentrantLock l = lock;
      l.lock();

      try {
         try {
            this.unregisterMBean();
         } catch (Exception var6) {
         }

         super.close();
      } finally {
         l.unlock();
      }

   }

   private void unregisterMBean() {
      if (this.jmxDs != null) {
         this.jmxDs.unregister();
      }

   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      ReentrantLock l = lock;
      l.lock();

      Logger var2;
      try {
         if (null == this.logger) {
            this.logger = (Logger)DataSource.class.getDeclaredMethod("getParentLogger").invoke(this.createDataSource());
         }

         var2 = this.logger;
      } catch (Throwable var6) {
         throw new SQLFeatureNotSupportedException();
      } finally {
         l.unlock();
      }

      return var2;
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name) {
      return name;
   }

   Object writeReplace() throws ObjectStreamException {
      return new DataSourceSerialization(this.name);
   }
}
