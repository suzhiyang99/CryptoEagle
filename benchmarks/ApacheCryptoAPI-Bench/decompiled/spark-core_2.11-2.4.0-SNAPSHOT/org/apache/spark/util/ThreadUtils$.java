package org.apache.spark.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.spark.SparkException;
import org.spark_project.guava.util.concurrent.MoreExecutors;
import org.spark_project.guava.util.concurrent.ThreadFactoryBuilder;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Awaitable;
import scala.concurrent.CanAwait;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.duration.Duration;
import scala.concurrent.forkjoin.ForkJoinPool;
import scala.concurrent.forkjoin.ForkJoinWorkerThread;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.VolatileObjectRef;
import scala.util.control.NonFatal$;

public final class ThreadUtils$ {
   public static final ThreadUtils$ MODULE$;
   private final ExecutionContextExecutorService sameThreadExecutionContext;

   static {
      new ThreadUtils$();
   }

   private ExecutionContextExecutorService sameThreadExecutionContext() {
      return this.sameThreadExecutionContext;
   }

   public ExecutionContextExecutor sameThread() {
      return this.sameThreadExecutionContext();
   }

   public ThreadFactory namedThreadFactory(String prefix) {
      return (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat((new StringBuilder()).append((Object)prefix).append((Object)"-%d").toString()).build();
   }

   public ThreadPoolExecutor newDaemonCachedThreadPool(String prefix) {
      ThreadFactory threadFactory = this.namedThreadFactory(prefix);
      return (ThreadPoolExecutor)Executors.newCachedThreadPool(threadFactory);
   }

   public ThreadPoolExecutor newDaemonCachedThreadPool(String prefix, int maxThreadNumber, int keepAliveSeconds) {
      ThreadFactory threadFactory = this.namedThreadFactory(prefix);
      ThreadPoolExecutor threadPool = new ThreadPoolExecutor(maxThreadNumber, maxThreadNumber, (long)keepAliveSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory);
      threadPool.allowCoreThreadTimeOut(true);
      return threadPool;
   }

   public int newDaemonCachedThreadPool$default$3() {
      return 60;
   }

   public ThreadPoolExecutor newDaemonFixedThreadPool(int nThreads, String prefix) {
      ThreadFactory threadFactory = this.namedThreadFactory(prefix);
      return (ThreadPoolExecutor)Executors.newFixedThreadPool(nThreads, threadFactory);
   }

   public ExecutorService newDaemonSingleThreadExecutor(String threadName) {
      ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat(threadName).build();
      return Executors.newSingleThreadExecutor(threadFactory);
   }

   public ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String threadName) {
      ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat(threadName).build();
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
      executor.setRemoveOnCancelPolicy(true);
      return executor;
   }

   public <T> T runInNewThread(String threadName, boolean isDaemon, Function0<T> body) {
      VolatileObjectRef exception = VolatileObjectRef.create(None$.MODULE$);
      VolatileObjectRef result = VolatileObjectRef.create((Object)null);
      Thread thread = new Thread(threadName, body, exception, result) {
         private final Function0 body$1;
         private final VolatileObjectRef exception$1;
         private final VolatileObjectRef result$1;

         public void run() {
            try {
               this.result$1.elem = (T)this.body$1.apply();
            } catch (Throwable var7) {
               Option var3 = NonFatal$.MODULE$.unapply(var7);
               if (var3.isEmpty()) {
                  throw var7;
               }

               Throwable e = (Throwable)var3.get();
               this.exception$1.elem = (T)(new Some(e));
               BoxedUnit var5 = BoxedUnit.UNIT;
               return;
            }

         }

         public {
            this.body$1 = body$1;
            this.exception$1 = exception$1;
            this.result$1 = result$1;
         }
      };
      thread.setDaemon(isDaemon);
      thread.start();
      thread.join();
      Option var7 = (Option)exception.elem;
      if (var7 instanceof Some) {
         Some var8 = (Some)var7;
         Throwable realException = (Throwable)var8.x();
         StackTraceElement[] baseStackTrace = (StackTraceElement[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])Thread.currentThread().getStackTrace()).dropWhile(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(StackTraceElement x$1) {
               return !x$1.getClassName().contains(ThreadUtils$.MODULE$.getClass().getSimpleName());
            }
         })).drop(1);
         StackTraceElement[] extraStackTrace = (StackTraceElement[])Predef$.MODULE$.refArrayOps((Object[])realException.getStackTrace()).takeWhile(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(StackTraceElement x$2) {
               return !x$2.getClassName().contains(ThreadUtils$.MODULE$.getClass().getSimpleName());
            }
         });
         StackTraceElement placeHolderStackElem = new StackTraceElement((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"... run in separate thread using ", " .."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(new StringOps(Predef$.MODULE$.augmentString(this.getClass().getName()))).stripSuffix("$")})), " ", "", -1);
         StackTraceElement[] finalStackTrace = (StackTraceElement[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])extraStackTrace).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new StackTraceElement[]{placeHolderStackElem}))), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(StackTraceElement.class)))).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])baseStackTrace), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(StackTraceElement.class)));
         realException.setStackTrace(finalStackTrace);
         throw realException;
      } else if (None$.MODULE$.equals(var7)) {
         Object var10 = result.elem;
         return (T)var10;
      } else {
         throw new MatchError(var7);
      }
   }

   public <T> boolean runInNewThread$default$2() {
      return true;
   }

   public ForkJoinPool newForkJoinPool(String prefix, int maxThreadNumber) {
      ForkJoinPool.ForkJoinWorkerThreadFactory factory = new ForkJoinPool.ForkJoinWorkerThreadFactory(prefix) {
         public final String prefix$1;

         public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(this, pool) {
               public {
                  this.setName((new StringBuilder()).append((Object)$outer.prefix$1).append((Object)"-").append((Object)super.getName()).toString());
               }
            };
         }

         public {
            this.prefix$1 = prefix$1;
         }
      };
      return new ForkJoinPool(maxThreadNumber, factory, (Thread.UncaughtExceptionHandler)null, false);
   }

   public <T> T awaitResult(Awaitable<T> awaitable, Duration atMost) throws SparkException {
      try {
         CanAwait awaitPermission = null;
         return (T)awaitable.result(atMost, awaitPermission);
      } catch (Throwable var10) {
         Option var5 = NonFatal$.MODULE$.unapply(var10);
         if (!var5.isEmpty()) {
            Throwable t = (Throwable)var5.get();
            if (!(t instanceof TimeoutException)) {
               throw new SparkException("Exception thrown in awaitResult: ", t);
            }
         }

         throw var10;
      }
   }

   public <T> Awaitable<T> awaitReady(Awaitable<T> awaitable, Duration atMost) throws SparkException {
      try {
         CanAwait awaitPermission = null;
         return awaitable.ready(atMost, awaitPermission);
      } catch (Throwable var10) {
         Option var5 = NonFatal$.MODULE$.unapply(var10);
         if (!var5.isEmpty()) {
            Throwable t = (Throwable)var5.get();
            if (!(t instanceof TimeoutException)) {
               throw new SparkException("Exception thrown in awaitResult: ", t);
            }
         }

         throw var10;
      }
   }

   private ThreadUtils$() {
      MODULE$ = this;
      this.sameThreadExecutionContext = ExecutionContext$.MODULE$.fromExecutorService(MoreExecutors.sameThreadExecutor());
   }
}
