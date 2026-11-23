package org.apache.openejb.util;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class AsynchronousRunner {
   private final Executor executor;

   public AsynchronousRunner(Executor executor) {
      this.executor = executor;
   }

   public Future<Object> runAsync(Object object, Method method, Object... arguments) {
      Callable<Object> callable = new MethodInvoker(object, method, arguments);
      FutureTask<Object> futureTask = new FutureTask(callable);
      this.executor.execute(futureTask);
      return futureTask;
   }

   private class MethodInvoker implements Callable<Object> {
      private final Object object;
      private final Method method;
      private final Object[] arguments;

      public MethodInvoker(Object object, Method method, Object[] arguments) {
         this.object = object;
         this.method = method;
         this.arguments = arguments;
      }

      public Object call() throws Exception {
         return this.method.invoke(this.object, this.arguments);
      }
   }
}
