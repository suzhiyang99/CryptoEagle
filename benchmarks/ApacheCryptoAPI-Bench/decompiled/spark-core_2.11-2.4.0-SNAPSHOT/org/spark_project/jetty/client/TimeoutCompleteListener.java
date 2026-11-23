package org.spark_project.jetty.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public class TimeoutCompleteListener implements Response.CompleteListener, Runnable {
   private static final Logger LOG = Log.getLogger(TimeoutCompleteListener.class);
   private final AtomicReference<Scheduler.Task> task = new AtomicReference();
   private final Request request;

   public TimeoutCompleteListener(Request request) {
      this.request = request;
   }

   public void onComplete(Result result) {
      this.cancel();
   }

   public boolean schedule(Scheduler scheduler) {
      long timeout = this.request.getTimeout();
      Scheduler.Task task = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
      Scheduler.Task existing = (Scheduler.Task)this.task.getAndSet(task);
      if (existing != null) {
         existing.cancel();
         this.cancel();
         throw new IllegalStateException();
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduled timeout task {} in {} ms for {}", task, timeout, this.request);
         }

         return true;
      }
   }

   public void run() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Executing timeout task {} for {}", this.task, this.request);
      }

      this.request.abort(new TimeoutException("Total timeout " + this.request.getTimeout() + " ms elapsed"));
   }

   public void cancel() {
      Scheduler.Task task = (Scheduler.Task)this.task.getAndSet((Object)null);
      if (task != null) {
         boolean cancelled = task.cancel();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Cancelled (successfully: {}) timeout task {}", cancelled, task);
         }
      }

   }
}
