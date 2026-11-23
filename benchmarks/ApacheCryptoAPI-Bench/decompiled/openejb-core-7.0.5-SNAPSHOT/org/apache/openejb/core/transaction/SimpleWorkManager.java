package org.apache.openejb.core.transaction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkAdapter;
import javax.resource.spi.work.WorkCompletedException;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.WorkRejectedException;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class SimpleWorkManager implements WorkManager {
   private static final Logger logger;
   private final Executor executor;

   public SimpleWorkManager(Executor executor) {
      if (executor == null) {
         throw new NullPointerException("executor is null");
      } else {
         this.executor = executor;
      }
   }

   public void doWork(Work work) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         this.doWork(work, Long.MAX_VALUE, (ExecutionContext)null, (WorkListener)null);
      }
   }

   public void doWork(Work work, long startTimeout, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         this.executeWork(SimpleWorkManager.WorkType.DO, work, startTimeout, executionContext, workListener);
      }
   }

   public long startWork(Work work) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         return this.startWork(work, Long.MAX_VALUE, (ExecutionContext)null, (WorkListener)null);
      }
   }

   public long startWork(Work work, long startTimeout, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         return this.executeWork(SimpleWorkManager.WorkType.START, work, startTimeout, executionContext, workListener);
      }
   }

   public void scheduleWork(Work work) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         this.scheduleWork(work, Long.MAX_VALUE, (ExecutionContext)null, (WorkListener)null);
      }
   }

   public void scheduleWork(Work work, long startTimeout, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
      if (work == null) {
         throw new NullPointerException("work is null");
      } else {
         this.executeWork(SimpleWorkManager.WorkType.SCHEDULE, work, startTimeout, executionContext, workListener);
      }
   }

   private long executeWork(WorkType workType, Work work, long startTimeout, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
      if (workListener == null) {
         workListener = new LoggingWorkListener(workType);
      }

      if (executionContext != null && executionContext.getXid() != null) {
         WorkRejectedException workRejectedException = new WorkRejectedException("SimpleWorkManager can not import an XID", "3");
         workListener.workRejected(new WorkEvent(this, 2, work, workRejectedException));
         throw workRejectedException;
      } else {
         workListener.workAccepted(new WorkEvent(this, 1, work, (WorkException)null));
         Worker worker = new Worker(work, workListener, startTimeout);
         this.executor.execute(worker);
         if (workType == SimpleWorkManager.WorkType.DO) {
            try {
               worker.waitForCompletion();
            } catch (InterruptedException e) {
               WorkException workException = new WorkException("Work submission thread was interrupted", e);
               workException.setErrorCode("-1");
               throw workException;
            }

            WorkException workCompletedException = worker.getWorkException();
            if (workCompletedException != null) {
               throw workCompletedException;
            }
         } else if (workType == SimpleWorkManager.WorkType.START) {
            try {
               worker.waitForStart();
            } catch (InterruptedException e) {
               WorkException workException = new WorkException("Work submission thread was interrupted", e);
               workException.setErrorCode("-1");
               throw workException;
            }

            WorkException workCompletedException = worker.getWorkException();
            if (workCompletedException instanceof WorkRejectedException) {
               throw workCompletedException;
            }
         }

         return worker.getStartDelay();
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, SimpleWorkManager.class);
   }

   public static enum WorkType {
      DO,
      START,
      SCHEDULE;
   }

   private class Worker implements Runnable {
      private final Work work;
      private final WorkListener workListener;
      private final long startTimeout;
      private final long created = System.currentTimeMillis();
      private final CountDownLatch started = new CountDownLatch(1);
      private final CountDownLatch completed = new CountDownLatch(1);
      private long startDelay = -1L;
      private WorkException workException;

      public Worker(Work work, WorkListener workListener, long startTimeout) {
         this.work = work;
         this.workListener = workListener;
         if (startTimeout <= 0L) {
            this.startTimeout = Long.MAX_VALUE;
         } else {
            this.startTimeout = startTimeout;
         }

      }

      public void run() {
         try {
            this.startDelay = System.currentTimeMillis() - this.created;
            if (this.startDelay <= this.startTimeout) {
               this.workListener.workStarted(new WorkEvent(SimpleWorkManager.this, 3, this.work, (WorkException)null));
               this.started.countDown();
               this.workException = null;

               try {
                  try {
                     this.work.run();
                  } catch (Throwable e) {
                     this.workException = new WorkCompletedException(e);
                  }

                  return;
               } finally {
                  this.workListener.workCompleted(new WorkEvent(SimpleWorkManager.this, 4, this.work, this.workException));
               }
            }

            this.workException = new WorkRejectedException("Work not started within specified timeout " + this.startTimeout + "ms", "1");
            this.workListener.workRejected(new WorkEvent(this, 2, this.work, this.workException, this.startTimeout));
         } finally {
            this.started.countDown();
            this.completed.countDown();
         }

      }

      public long getStartDelay() {
         return this.startDelay;
      }

      public WorkException getWorkException() {
         return this.workException;
      }

      public void waitForStart() throws InterruptedException {
         this.started.await();
      }

      public void waitForCompletion() throws InterruptedException {
         this.completed.await();
      }
   }

   private static final class LoggingWorkListener extends WorkAdapter {
      private final WorkType workType;

      private LoggingWorkListener(WorkType workType) {
         this.workType = workType;
      }

      public void workRejected(WorkEvent event) {
         if (this.workType != SimpleWorkManager.WorkType.DO && this.workType != SimpleWorkManager.WorkType.START) {
            WorkException exception = event.getException();
            if (exception != null && "1".equals(exception.getErrorCode())) {
               SimpleWorkManager.logger.error(exception.getMessage());
            }

         }
      }

      public void workCompleted(WorkEvent event) {
         if (this.workType != SimpleWorkManager.WorkType.DO) {
            Throwable cause = event.getException();
            if (cause != null && cause.getCause() != null) {
               cause = cause.getCause();
            }

            if (cause != null) {
               SimpleWorkManager.logger.error(event.getWork().toString(), cause);
            }

         }
      }
   }
}
