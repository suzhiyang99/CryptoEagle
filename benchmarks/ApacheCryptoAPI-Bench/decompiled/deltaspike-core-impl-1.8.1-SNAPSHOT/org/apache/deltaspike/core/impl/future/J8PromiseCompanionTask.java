package org.apache.deltaspike.core.impl.future;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.apache.deltaspike.core.util.ExceptionUtils;

class J8PromiseCompanionTask<T> implements Runnable {
   private static final Method COMPLETABLE_FUTURE_COMPLETE;
   private static final Method COMPLETABLE_FUTURE_COMPLETE_ERROR;
   private Object dep;
   private Callable<T> fn;

   J8PromiseCompanionTask(Object dep, Callable<T> fn) {
      this.dep = dep;
      this.fn = fn;
   }

   public void run() {
      try {
         COMPLETABLE_FUTURE_COMPLETE.invoke(this.dep, this.fn.call());
      } catch (InvocationTargetException var7) {
         InvocationTargetException e = var7;

         try {
            COMPLETABLE_FUTURE_COMPLETE_ERROR.invoke(this.dep, e.getCause());
         } catch (IllegalAccessException e1) {
            throw ExceptionUtils.throwAsRuntimeException(e1);
         } catch (InvocationTargetException e1) {
            throw ExceptionUtils.throwAsRuntimeException(e1.getCause());
         }
      } catch (Exception var8) {
         Exception e = var8;

         try {
            COMPLETABLE_FUTURE_COMPLETE_ERROR.invoke(this.dep, e);
         } catch (IllegalAccessException e1) {
            throw ExceptionUtils.throwAsRuntimeException(e1);
         } catch (InvocationTargetException e1) {
            throw ExceptionUtils.throwAsRuntimeException(e1.getCause());
         }
      }

   }

   static {
      Class<?> completableFutureClass = null;
      Method completableFutureComplete = null;
      Method completableFutureCompleteError = null;

      try {
         ClassLoader classLoader = ClassLoader.getSystemClassLoader();
         completableFutureClass = classLoader.loadClass("java.util.concurrent.CompletableFuture");
         completableFutureComplete = completableFutureClass.getMethod("complete", Object.class);
         completableFutureCompleteError = completableFutureClass.getMethod("completeExceptionally", Throwable.class);
      } catch (Exception var4) {
      }

      COMPLETABLE_FUTURE_COMPLETE = completableFutureComplete;
      COMPLETABLE_FUTURE_COMPLETE_ERROR = completableFutureCompleteError;
   }
}
