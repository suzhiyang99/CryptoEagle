package org.apache.openejb.util.executor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.openejb.util.Duration;

public class OfferRejectedExecutionHandler implements RejectedExecutionHandler {
   private long timeout;
   private TimeUnit seconds;

   public OfferRejectedExecutionHandler(Duration duration) {
      this(duration.getTime(), duration.getUnit() == null ? TimeUnit.SECONDS : duration.getUnit());
   }

   public OfferRejectedExecutionHandler(long timeout, TimeUnit timeUnit) {
      this.timeout = 30L;
      this.seconds = TimeUnit.SECONDS;
      if (timeout <= 0L) {
         throw new IllegalArgumentException("timeout must be greater than zero");
      } else if (timeUnit == null) {
         throw new IllegalArgumentException("TimeUnit must not be null");
      } else {
         this.timeout = timeout;
         this.seconds = timeUnit;
      }
   }

   public void rejectedExecution(Runnable r, ThreadPoolExecutor tpe) {
      if (null != r && null != tpe && !tpe.isShutdown() && !tpe.isTerminated() && !tpe.isTerminating()) {
         try {
            if (!tpe.getQueue().offer(r, this.timeout, this.seconds)) {
               throw new RejectedExecutionException("Timeout waiting for executor slot: waited " + this.timeout + " " + this.seconds.toString().toLowerCase());
            }
         } catch (InterruptedException var4) {
            throw new RejectedExecutionException("Interrupted waiting for executor slot");
         }
      }
   }
}
