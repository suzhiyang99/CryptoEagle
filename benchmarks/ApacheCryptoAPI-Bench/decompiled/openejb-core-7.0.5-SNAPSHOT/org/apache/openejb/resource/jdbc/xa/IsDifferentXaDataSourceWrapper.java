package org.apache.openejb.resource.jdbc.xa;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

public class IsDifferentXaDataSourceWrapper implements XADataSource {
   private static final Class<?>[] API_CONNECTION = new Class[]{XAConnection.class};
   private static final Class<?>[] API_RESOURCE = new Class[]{XAResource.class};
   private final XADataSource delegate;

   public IsDifferentXaDataSourceWrapper(XADataSource delegate) {
      this.delegate = delegate;
   }

   public XAConnection getXAConnection() throws SQLException {
      return this.wrap(this.delegate.getXAConnection());
   }

   public XAConnection getXAConnection(String user, String password) throws SQLException {
      return this.wrap(this.delegate.getXAConnection(user, password));
   }

   private XAConnection wrap(final XAConnection xaConnection) {
      return (XAConnection)XAConnection.class.cast(Proxy.newProxyInstance(xaConnection.getClass().getClassLoader(), API_CONNECTION, new InvocationHandler() {
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getXAResource".equals(method.getName())) {
               try {
                  final Object xaResource = method.invoke(xaConnection, args);
                  return Proxy.newProxyInstance(xaResource.getClass().getClassLoader(), IsDifferentXaDataSourceWrapper.API_RESOURCE, new InvocationHandler() {
                     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("isSameRM".equals(method.getName())) {
                           return false;
                        } else {
                           try {
                              return method.invoke(xaResource, args);
                           } catch (InvocationTargetException ite) {
                              throw ite.getCause();
                           }
                        }
                     }
                  });
               } catch (InvocationTargetException ite) {
                  throw ite.getCause();
               }
            } else {
               try {
                  return method.invoke(xaConnection, args);
               } catch (InvocationTargetException ite) {
                  throw ite.getCause();
               }
            }
         }
      }));
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

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.delegate.getParentLogger();
   }
}
