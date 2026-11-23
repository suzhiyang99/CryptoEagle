package org.apache.openejb.resource.jdbc;

import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.CommonDataSource;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ResettableDataSourceHandler implements DelegatableHandler {
   private static final Logger LOGGER;
   private final AtomicReference<CommonDataSource> delegate = new AtomicReference();
   private final RetryStrategy strategy;
   private Set<String> retryMethods = new HashSet();

   public ResettableDataSourceHandler(CommonDataSource ds, String value, String methods) {
      this.delegate.set(ds);
      if (!"*".equals(methods)) {
         this.retryMethods.addAll(Arrays.asList(methods == null ? new String[]{"getConnection", "getXAConnection"} : methods.split(" *, *")));
      }

      Runnable recreate = new Runnable() {
         public void run() {
            try {
               ((Flushable)Flushable.class.cast(ResettableDataSourceHandler.this.delegate.get())).flush();
            } catch (IOException ioe) {
               ResettableDataSourceHandler.LOGGER.error("Can't flush connection pool: " + ioe.getMessage());
            }

         }
      };
      RetryStrategy tmp;
      if (value.equals("true")) {
         tmp = new CountRetryStrategy(recreate, 1);
      } else if (value.startsWith("retry(") && value.endsWith(")")) {
         tmp = new CountRetryStrategy(recreate, Integer.parseInt(value.substring("retry(".length(), value.length() - 1)));
      } else {
         try {
            tmp = new CountRetryStrategy(recreate, Integer.parseInt(value.trim()));
         } catch (NumberFormatException var9) {
            try {
               tmp = (RetryStrategy)RetryStrategy.class.cast(Thread.currentThread().getContextClassLoader().loadClass(value).getConstructor(Runnable.class, String.class).newInstance(recreate, value));
            } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
               throw new IllegalArgumentException("Unknown retry strategy: " + value, e);
            }
         }
      }

      this.strategy = tmp;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class == method.getDeclaringClass() && "toString".equals(method.getName())) {
         return "Resettable[" + this.getDelegate().toString() + "]";
      } else {
         Result retry = null;

         while(true) {
            try {
               return method.invoke(this.getDelegate(), args);
            } catch (InvocationTargetException ite) {
               Throwable cause = ite.getCause();
               if (!SQLException.class.isInstance(cause) || !this.isRetryMethod(method)) {
                  throw cause;
               }

               retry = this.strategy.shouldRetry(cause, retry);
               if (!retry.status) {
                  throw cause;
               }
            }
         }
      }
   }

   private boolean isRetryMethod(Method method) {
      return this.retryMethods.isEmpty() || this.retryMethods.contains(method.getName());
   }

   public CommonDataSource getDelegate() {
      return (CommonDataSource)this.delegate.get();
   }

   public void updateDelegate(CommonDataSource ds) {
      this.delegate.set(ds);
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, ResettableDataSourceHandler.class.getName());
   }

   public static class Result {
      private final boolean status;

      public Result(boolean status) {
         this.status = status;
      }
   }

   private static class CountRetryStrategy implements RetryStrategy {
      private final Runnable task;
      private final int max;

      public CountRetryStrategy(Runnable recreate, int max) {
         this.task = recreate;
         this.max = max;
      }

      public Result shouldRetry(Throwable cause, Result previous) {
         ResettableDataSourceHandler.LOGGER.error("SQLException, resetting the connection pool.", cause);
         Integer count = previous == null ? 1 : ((CountResult)CountResult.class.cast(previous)).count + 1;
         this.task.run();
         return new CountResult(count <= this.max, count);
      }
   }

   private static class CountResult extends Result {
      private int count;

      public CountResult(boolean status, int count) {
         super(status);
         this.count = count;
      }
   }

   public interface RetryStrategy {
      Result shouldRetry(Throwable var1, Result var2);
   }
}
