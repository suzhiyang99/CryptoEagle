package org.apache.openejb.resource.jdbc.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import javax.naming.InitialContext;
import javax.sql.XADataSource;

public final class XADataSourceResource {
   private static final Class<?>[] XA_DATASOURCE_API = new Class[]{XADataSource.class};

   public static XADataSource proxy(ClassLoader loader, String xaDataSource) {
      return (XADataSource)XADataSource.class.cast(Proxy.newProxyInstance(loader, XA_DATASOURCE_API, new LazyXADataSourceHandler(xaDataSource)));
   }

   private XADataSourceResource() {
   }

   private static class LazyXADataSourceHandler implements InvocationHandler {
      private final String name;
      private final AtomicReference<XADataSource> ref = new AtomicReference();

      public LazyXADataSourceHandler(String xaDataSource) {
         if (!xaDataSource.startsWith("openejb:") && !xaDataSource.startsWith("java:global")) {
            this.name = "openejb:Resource/" + xaDataSource;
         } else {
            this.name = xaDataSource;
         }

      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         XADataSource instance = (XADataSource)this.ref.get();
         if (instance == null) {
            synchronized(this) {
               instance = (XADataSource)this.ref.get();
               if (instance == null) {
                  instance = (XADataSource)XADataSource.class.cast((new InitialContext()).lookup(this.name));
                  this.ref.set(instance);
               }
            }
         }

         return method.invoke(instance, args);
      }
   }
}
