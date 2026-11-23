package org.apache.openejb.resource.jdbc.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import javax.sql.CommonDataSource;
import org.apache.openejb.resource.jdbc.DelegatableHandler;

public class LoggingSqlDataSource implements DelegatableHandler {
   private static final Class<?>[] INTERFACES = new Class[]{Connection.class};
   private final CommonDataSource delegate;
   private final String[] packages;

   public LoggingSqlDataSource(CommonDataSource ds, String[] debugPackages) {
      this.delegate = ds;
      this.packages = debugPackages;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class == method.getDeclaringClass() && "toString".equals(method.getName())) {
         return "Logging[" + this.delegate.toString() + "]";
      } else {
         Object result;
         try {
            result = method.invoke(this.delegate, args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }

         return "getConnection".equals(method.getName()) ? Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(), INTERFACES, new LoggingSqlConnection((Connection)result, this.packages)) : result;
      }
   }

   public CommonDataSource getDelegate() {
      return this.delegate;
   }
}
