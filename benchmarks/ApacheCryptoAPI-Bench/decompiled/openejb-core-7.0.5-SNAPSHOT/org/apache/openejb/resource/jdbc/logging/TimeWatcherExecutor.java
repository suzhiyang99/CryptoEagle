package org.apache.openejb.resource.jdbc.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public final class TimeWatcherExecutor {
   private TimeWatcherExecutor() {
   }

   public static String inlineStack(String[] acceptedPackages) {
      if (acceptedPackages == null) {
         return "";
      } else {
         Throwable t = (new Exception()).fillInStackTrace();
         StringBuilder inlinedStack = new StringBuilder();

         for(StackTraceElement elt : t.getStackTrace()) {
            String className = elt.getClassName();

            for(String p : acceptedPackages) {
               if (className.startsWith(p)) {
                  inlinedStack.append(" -> ").append(className).append('.').append(elt.getMethodName()).append(':').append(elt.getLineNumber());
                  break;
               }
            }
         }

         return inlinedStack.toString();
      }
   }

   public static TimerWatcherResult execute(Method mtd, Object instance, Object[] args, boolean watch) {
      long start = watch ? System.nanoTime() : 0L;

      try {
         Object result = mtd.invoke(instance, args);
         return new TimerWatcherResult(start, result, (Throwable)null);
      } catch (InvocationTargetException ite) {
         return new TimerWatcherResult(start, (Object)null, ite.getCause());
      } catch (Throwable throwable) {
         return new TimerWatcherResult(start, (Object)null, throwable);
      }
   }

   public static class TimerWatcherResult {
      private final Object result;
      private final Throwable throwable;
      private final long duration;

      public TimerWatcherResult(long start, Object result, Throwable throwable) {
         this.duration = start == 0L ? 0L : TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
         this.result = result;
         this.throwable = throwable;
      }

      public String format(String query) {
         String message = query + " --> " + this.getDuration() + "ms";
         if (this.throwable != null) {
            message = message + " - FAILED";
         }

         return message;
      }

      public Object getResult() {
         return this.result;
      }

      public long getDuration() {
         return this.duration;
      }

      public Throwable getThrowable() {
         return this.throwable;
      }
   }
}
