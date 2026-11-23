package org.apache.openejb.resource.jdbc.managed.xa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;

public class CommonDataSourceAdapter implements InvocationHandler {
   private final CommonDataSource delegate;

   public CommonDataSourceAdapter(CommonDataSource ds) {
      this.delegate = ds;
   }

   public static DataSource wrap(CommonDataSource ds) {
      return (DataSource)DataSource.class.cast(Proxy.newProxyInstance(ds.getClass().getClassLoader(), new Class[]{DataSource.class}, new CommonDataSourceAdapter(ds)));
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return Object.class == method.getDeclaringClass() && "equals".equals(method.getName()) && this.delegate == args[0] ? true : method.invoke(this.delegate, args);
   }

   public CommonDataSource getDelegate() {
      return this.delegate;
   }
}
