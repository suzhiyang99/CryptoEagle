package org.apache.wicket.util.thread;

import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Task {
   private boolean isDaemon = true;
   private boolean isStarted = false;
   private Logger log = null;
   private final String name;
   private Time startTime = Time.now();
   private boolean stop;
   private Thread thread;

   public Task(String name) {
      this.name = name;
   }

   public final synchronized void run(final Duration frequency, final ICode code) {
      if (!this.isStarted) {
         Runnable runnable = new Runnable() {
            public void run() {
               Task.this.startTime.fromNow().sleep();
               Logger log = Task.this.getLog();

               try {
                  Time startOfPeriod;
                  try {
                     for(; !Task.this.stop; startOfPeriod.add(frequency).fromNow().sleep()) {
                        startOfPeriod = Time.now();
                        if (log.isTraceEnabled()) {
                           log.trace("Run the job: '{}'", code);
                        }

                        try {
                           code.run(Task.this.getLog());
                        } catch (Exception e) {
                           log.error("Unhandled exception thrown by user code in task " + Task.this.name, e);
                        }

                        if (log.isTraceEnabled()) {
                           log.trace("Finished with job: '{}'", code);
                        }
                     }
                  } catch (Exception x) {
                     log.error("Task '{}' terminated", Task.this.name, x);
                  }
               } finally {
                  Task.this.isStarted = false;
               }

            }
         };
         this.thread = new Thread(runnable, this.name + " Task");
         this.thread.setDaemon(this.isDaemon);
         this.thread.start();
         this.isStarted = true;
      } else {
         throw new IllegalStateException("Attempt to start task that has already been started");
      }
   }

   public synchronized void setDaemon(boolean daemon) {
      if (this.isStarted) {
         throw new IllegalStateException("Attempt to set daemon state of a task that has already been started");
      } else {
         this.isDaemon = daemon;
      }
   }

   public synchronized void setLog(Logger log) {
      this.log = log;
   }

   public synchronized void setStartTime(Time startTime) {
      if (this.isStarted) {
         throw new IllegalStateException("Attempt to set start time of task that has already been started");
      } else {
         this.startTime = startTime;
      }
   }

   public String toString() {
      return "[name=" + this.name + ", startTime=" + this.startTime + ", isDaemon=" + this.isDaemon + ", isStarted=" + this.isStarted + ", codeListener=" + this.log + "]";
   }

   protected synchronized Logger getLog() {
      if (this.log == null) {
         this.log = LoggerFactory.getLogger(Task.class);
      }

      return this.log;
   }

   public void stop() {
      this.stop = true;
   }

   public void interrupt() {
      this.stop();
      if (this.thread != null) {
         this.thread.interrupt();
      }

   }

   public void setPriority(int prio) {
      if (prio < 1) {
         prio = 1;
      } else if (prio > 10) {
         prio = 10;
      }

      this.thread.setPriority(prio);
   }

   public int getPriority() {
      return this.thread.getPriority();
   }
}
