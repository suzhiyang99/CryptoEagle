package org.apache.openejb.resource.jdbc.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class LoggingSqlConnection implements InvocationHandler {
   private static final Class<?>[] INTERFACES_STATEMENT = new Class[]{Statement.class};
   private static final Class<?>[] INTERFACES_PREPARED = new Class[]{PreparedStatement.class};
   private static final Class<?>[] INTERFACES_CALLABLE = new Class[]{CallableStatement.class};
   private final Connection delegate;
   private final String[] packages;

   public LoggingSqlConnection(Connection connection, String[] debugPackages) {
      this.delegate = connection;
      this.packages = debugPackages;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object result;
      try {
         result = method.invoke(this.delegate, args);
      } catch (InvocationTargetException ite) {
         throw ite.getCause();
      }

      String mtd = method.getName();
      if ("createStatement".equals(mtd)) {
         return Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(), INTERFACES_STATEMENT, new LoggingSqlStatement((Statement)result, this.packages));
      } else if ("prepareStatement".equals(mtd)) {
         return Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(), INTERFACES_PREPARED, new LoggingPreparedSqlStatement((PreparedStatement)result, (String)args[0], this.packages));
      } else {
         return "prepareCall".equals(mtd) ? Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(), INTERFACES_CALLABLE, new LoggingCallableSqlStatement((CallableStatement)result, (String)args[0], this.packages)) : result;
      }
   }
}
