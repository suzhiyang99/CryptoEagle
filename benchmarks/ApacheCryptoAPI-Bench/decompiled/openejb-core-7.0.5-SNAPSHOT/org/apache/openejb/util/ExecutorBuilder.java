package org.apache.openejb.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.Options;
import org.apache.openejb.util.executor.OfferRejectedExecutionHandler;

public class ExecutorBuilder {
   private int size = 10;
   private String prefix = "Pool";
   private ThreadFactory threadFactory;
   private RejectedExecutionHandler rejectedExecutionHandler;

   public ExecutorBuilder size(int size) {
      this.size = size;
      return this;
   }

   public ExecutorBuilder prefix(String prefix) {
      this.prefix = prefix;
      return this;
   }

   public ExecutorBuilder threadFactory(ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
      return this;
   }

   public ExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
      this.rejectedExecutionHandler = rejectedExecutionHandler;
      return this;
   }

   public ThreadPoolExecutor build(Options options) {
      int corePoolSize = options.get(this.prefix + ".CorePoolSize", this.size);
      if (corePoolSize < 1) {
         corePoolSize = 1;
      }

      int maximumPoolSize = Math.max(options.get(this.prefix + ".MaximumPoolSize", corePoolSize), corePoolSize);
      if (maximumPoolSize < corePoolSize) {
         maximumPoolSize = corePoolSize;
      }

      int qsize = options.get(this.prefix + ".QueueSize", corePoolSize);
      Duration keepAliveTime = (Duration)options.get(this.prefix + ".KeepAliveTime", new Duration(60L, TimeUnit.SECONDS));
      boolean allowCoreThreadTimeout = options.get(this.prefix + ".AllowCoreThreadTimeOut", true);
      QueueType defaultQueueType = qsize < 1 ? ExecutorBuilder.QueueType.SYNCHRONOUS : ExecutorBuilder.QueueType.LINKED;
      BlockingQueue<Runnable> queue = ((QueueType)options.get(this.prefix + ".QueueType", defaultQueueType)).create(options, this.prefix, qsize);
      ThreadFactory factory = this.threadFactory;
      if (factory == null) {
         factory = new DaemonThreadFactory(new Object[]{this.prefix});
      }

      RejectedExecutionHandler handler = this.rejectedExecutionHandler;
      if (handler == null) {
         String rejectedExecutionHandlerClass = options.get(this.prefix + ".RejectedExecutionHandlerClass", (String)null);
         if (rejectedExecutionHandlerClass == null) {
            Duration duration = (Duration)options.get(this.prefix + ".OfferTimeout", new Duration(30L, TimeUnit.SECONDS));
            handler = new OfferRejectedExecutionHandler(duration);
         } else {
            try {
               handler = (RejectedExecutionHandler)RejectedExecutionHandler.class.cast(Thread.currentThread().getContextClassLoader().loadClass(rejectedExecutionHandlerClass).newInstance());
            } catch (Exception e) {
               throw new OpenEJBRuntimeException(e);
            }
         }
      }

      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime.getTime(), keepAliveTime.getUnit() != null ? keepAliveTime.getUnit() : TimeUnit.SECONDS, queue, factory, handler);
      threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeout);
      return threadPoolExecutor;
   }

   public static enum QueueType {
      ARRAY,
      LINKED,
      PRIORITY,
      SYNCHRONOUS;

      public BlockingQueue<Runnable> create(Options options, String prefix, int queueSize) {
         switch (this) {
            case ARRAY:
               return new ArrayBlockingQueue(queueSize > 0 ? queueSize : 1);
            case LINKED:
               return new LinkedBlockingQueue(queueSize > 0 ? queueSize : 1);
            case PRIORITY:
               return new PriorityBlockingQueue();
            case SYNCHRONOUS:
               return new SynchronousQueue(options.get(prefix + ".QueueFair", false));
            default:
               throw new IllegalArgumentException("Unknown QueueType type: " + this);
         }
      }
   }
}
