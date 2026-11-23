package org.apache.openejb.resource.jdbc.managed.local;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ManagedConnection implements InvocationHandler {
   private final TransactionManager transactionManager;
   private final Key key;
   private final TransactionSynchronizationRegistry registry;
   protected XAResource xaResource;
   protected Connection delegate;
   protected XAConnection xaConnection;
   private Transaction currentTransaction;
   private boolean closed;

   public ManagedConnection(CommonDataSource ds, TransactionManager txMgr, TransactionSynchronizationRegistry txRegistry, String user, String password) {
      this.transactionManager = txMgr;
      this.registry = txRegistry;
      this.closed = false;
      this.key = new Key(ds, user, password);
   }

   public XAResource getXAResource() throws SQLException {
      if (this.xaResource == null) {
         this.newConnection();
      }

      return this.xaResource;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String mtdName = method.getName();
      if ("toString".equals(mtdName)) {
         return "ManagedConnection{" + this.delegate + "}";
      } else if ("hashCode".equals(mtdName)) {
         return this.hashCode();
      } else if ("equals".equals(mtdName)) {
         InvocationHandler handler;
         return args[0] == this || (handler = this.unwrapHandler(args[0])) == this || this.delegate != null && this.delegate.equals(this.unwrapDelegate(args[0], handler));
      } else {
         if (Wrapper.class == method.getDeclaringClass() && args.length == 1 && Connection.class == args[0]) {
            if ("isWrapperFor".equals(mtdName)) {
               return true;
            }

            if ("unwrap".equals(mtdName)) {
               return this.delegate;
            }
         }

         try {
            Transaction transaction = this.transactionManager.getTransaction();
            if (transaction == null) {
               if ("close".equals(mtdName)) {
                  if (this.delegate == null) {
                     return this.close();
                  } else {
                     this.closeConnection(true);
                     return null;
                  }
               } else if ("isClosed".equals(mtdName) && this.closed) {
                  return true;
               } else {
                  if (this.delegate == null) {
                     this.newConnection();
                  }

                  return invoke(method, this.delegate, args);
               }
            } else if (this.currentTransaction != null && isUnderTransaction(this.currentTransaction.getStatus())) {
               if (!this.currentTransaction.equals(transaction)) {
                  throw new SQLException("Connection can not be used while enlisted in another transaction");
               } else {
                  return this.invokeUnderTransaction(method, args);
               }
            } else {
               int transactionStatus = transaction.getStatus();
               if (!isUnderTransaction(transactionStatus)) {
                  if ("isClosed".equals(mtdName) && this.closed) {
                     return true;
                  } else if ("close".equals(mtdName)) {
                     return this.close();
                  } else {
                     if (this.delegate == null) {
                        this.newConnection();
                     }

                     return invoke(method, this.delegate, args);
                  }
               } else {
                  Connection connection = (Connection)Connection.class.cast(this.registry.getResource(this.key));
                  if (connection == null && this.delegate == null) {
                     this.newConnection();
                     this.currentTransaction = transaction;

                     try {
                        if (!transaction.enlistResource(this.getXAResource())) {
                           this.closeConnection(true);
                           throw new SQLException("Unable to enlist connection in transaction: enlistResource returns 'false'.");
                        }
                     } catch (RollbackException var11) {
                     } catch (SystemException e) {
                        throw new SQLException("Unable to enlist connection the transaction", e);
                     }

                     this.registry.putResource(this.key, this.delegate);
                     transaction.registerSynchronization(new ClosingSynchronization());
                     if (this.xaConnection == null) {
                        try {
                           this.setAutoCommit(false);
                        } catch (SQLException xae) {
                           String message = "Can't set auto commit to false cause the XA datasource doesn't support it, this is likely an issue";
                           Logger logger = Logger.getInstance(LogCategory.OPENEJB_RESOURCE_JDBC, ManagedConnection.class);
                           if (logger.isDebugEnabled()) {
                              logger.warning("Can't set auto commit to false cause the XA datasource doesn't support it, this is likely an issue", (Throwable)xae);
                           } else {
                              logger.warning("Can't set auto commit to false cause the XA datasource doesn't support it, this is likely an issue");
                           }
                        }
                     }
                  } else if (this.delegate == null) {
                     this.delegate = connection;
                  }

                  return this.invokeUnderTransaction(method, args);
               }
            }
         } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
         }
      }
   }

   private InvocationHandler unwrapHandler(Object arg) {
      return arg != null && Proxy.isProxyClass(arg.getClass()) ? Proxy.getInvocationHandler(arg) : null;
   }

   private Object unwrapDelegate(Object arg, InvocationHandler handler) {
      return handler != null && ManagedConnection.class.isInstance(handler) ? ((ManagedConnection)ManagedConnection.class.cast(handler)).delegate : arg;
   }

   protected Object newConnection() throws SQLException {
      Object connection = DataSource.class.isInstance(this.key.ds) ? (this.key.user == null ? ((DataSource)DataSource.class.cast(this.key.ds)).getConnection() : ((DataSource)DataSource.class.cast(this.key.ds)).getConnection(this.key.user, this.key.pwd)) : (this.key.user == null ? ((XADataSource)XADataSource.class.cast(this.key.ds)).getXAConnection() : ((XADataSource)XADataSource.class.cast(this.key.ds)).getXAConnection(this.key.user, this.key.pwd));
      if (XAConnection.class.isInstance(connection)) {
         this.xaConnection = (XAConnection)XAConnection.class.cast(connection);
         this.xaResource = this.xaConnection.getXAResource();
         this.delegate = this.xaConnection.getConnection();
      } else {
         this.delegate = (Connection)Connection.class.cast(connection);
         this.xaResource = new LocalXAResource(this.delegate);
      }

      return connection;
   }

   protected void setAutoCommit(boolean value) throws SQLException {
      if (this.delegate == null) {
         this.newConnection();
      }

      this.delegate.setAutoCommit(value);
   }

   private static Object invoke(Method method, Connection delegate, Object[] args) throws Throwable {
      try {
         return method.invoke(delegate, args);
      } catch (InvocationTargetException ite) {
         throw ite.getCause();
      }
   }

   private Object invokeUnderTransaction(Method method, Object[] args) throws Exception {
      String mtdName = method.getName();
      if (!"setAutoCommit".equals(mtdName) && !"commit".equals(mtdName) && !"rollback".equals(mtdName) && !"setSavepoint".equals(mtdName) && !"setReadOnly".equals(mtdName)) {
         if ("close".equals(mtdName)) {
            return this.close();
         } else {
            return "isClosed".equals(mtdName) && this.closed ? true : method.invoke(this.delegate, args);
         }
      } else {
         throw forbiddenCall(mtdName);
      }
   }

   private Object close() {
      this.closed = true;
      return null;
   }

   private static boolean isUnderTransaction(int status) {
      return status == 0 || status == 1;
   }

   private static SQLException forbiddenCall(String mtdName) {
      return new SQLException("can't call " + mtdName + " when the connection is JtaManaged");
   }

   private void closeConnection(boolean force) {
      if (force || !this.closed) {
         try {
            if (this.xaConnection != null) {
               this.xaConnection.close();
            } else if (this.delegate != null && !this.delegate.isClosed()) {
               this.delegate.close();
            }
         } catch (SQLException var6) {
         } finally {
            this.close();
         }

      }
   }

   private class ClosingSynchronization implements Synchronization {
      private ClosingSynchronization() {
      }

      public void beforeCompletion() {
      }

      public void afterCompletion(int status) {
         ManagedConnection.this.closeConnection(true);
      }
   }

   private static final class Key {
      private final CommonDataSource ds;
      private final String user;
      private final String pwd;
      private final int hash;

      private Key(CommonDataSource ds, String user, String pwd) {
         this.ds = ds;
         this.user = user;
         this.pwd = pwd;
         int result = ds.hashCode();
         result = 31 * result + (user != null ? user.hashCode() : 0);
         result = 31 * result + (pwd != null ? pwd.hashCode() : 0);
         this.hash = result;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            boolean var10000;
            label52: {
               Key key = (Key)Key.class.cast(o);
               if (this.ds == key.ds || this.ds.equals(key.ds)) {
                  label46: {
                     if (this.user != null) {
                        if (!this.user.equals(key.user)) {
                           break label46;
                        }
                     } else if (key.user != null) {
                        break label46;
                     }

                     if (this.pwd != null) {
                        if (this.pwd.equals(key.pwd)) {
                           break label52;
                        }
                     } else if (key.pwd == null) {
                        break label52;
                     }
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }
}
