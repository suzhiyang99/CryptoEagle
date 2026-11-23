package org.apache.activemq.artemis.core.server;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.artemis.utils.ActiveMQThreadFactory;
import org.jboss.logging.Logger;

public abstract class ActiveMQScheduledComponent implements ActiveMQComponent, Runnable {
   private static final Logger logger = Logger.getLogger(ActiveMQScheduledComponent.class);
   private ScheduledExecutorService scheduledExecutorService;
   private boolean startedOwnScheduler;
   private long initialDelay;
   private long period;
   private long millisecondsPeriod;
   private TimeUnit timeUnit;
   private final Executor executor;
   private volatile ScheduledFuture future;
   private final boolean onDemand;
   long lastTime;
   private final AtomicInteger delayed;
   final Runnable runForExecutor;
   final Runnable runForScheduler;

   public ActiveMQScheduledComponent(ScheduledExecutorService scheduledExecutorService, Executor executor, long initialDelay, long checkPeriod, TimeUnit timeUnit, boolean onDemand) {
      this.lastTime = 0L;
      this.delayed = new AtomicInteger(0);
      this.runForExecutor = new Runnable() {
         public void run() {
            if (ActiveMQScheduledComponent.this.onDemand && ActiveMQScheduledComponent.this.delayed.get() > 0) {
               ActiveMQScheduledComponent.this.delayed.decrementAndGet();
            }

            if (!ActiveMQScheduledComponent.this.onDemand && ActiveMQScheduledComponent.this.lastTime > 0L && System.currentTimeMillis() - ActiveMQScheduledComponent.this.lastTime < ActiveMQScheduledComponent.this.millisecondsPeriod) {
               ActiveMQScheduledComponent.logger.trace("Execution ignored due to too many simultaneous executions, probably a previous delayed execution");
            } else {
               ActiveMQScheduledComponent.this.lastTime = System.currentTimeMillis();
               ActiveMQScheduledComponent.this.run();
            }
         }
      };
      this.runForScheduler = new Runnable() {
         public void run() {
            if (ActiveMQScheduledComponent.this.executor != null) {
               ActiveMQScheduledComponent.this.executor.execute(ActiveMQScheduledComponent.this.runForExecutor);
            } else {
               ActiveMQScheduledComponent.this.runForExecutor.run();
            }

         }
      };
      this.executor = executor;
      this.scheduledExecutorService = scheduledExecutorService;
      this.initialDelay = initialDelay;
      this.period = checkPeriod;
      this.timeUnit = timeUnit;
      this.onDemand = onDemand;
   }

   public ActiveMQScheduledComponent(ScheduledExecutorService scheduledExecutorService, Executor executor, long checkPeriod, TimeUnit timeUnit, boolean onDemand) {
      this(scheduledExecutorService, executor, -1L, checkPeriod, timeUnit, onDemand);
   }

   public ActiveMQScheduledComponent(long initialDelay, long checkPeriod, TimeUnit timeUnit, boolean onDemand) {
      this((ScheduledExecutorService)null, (Executor)null, initialDelay, checkPeriod, timeUnit, onDemand);
   }

   public ActiveMQScheduledComponent(long checkPeriod, TimeUnit timeUnit, boolean onDemand) {
      this((ScheduledExecutorService)null, (Executor)null, checkPeriod, checkPeriod, timeUnit, onDemand);
   }

   public synchronized void start() {
      if (this.future == null) {
         if (this.scheduledExecutorService == null) {
            this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1, this.getThreadFactory());
            this.startedOwnScheduler = true;
         }

         if (!this.onDemand) {
            this.millisecondsPeriod = this.timeUnit.convert(this.period, TimeUnit.MILLISECONDS);
            if (this.period >= 0L) {
               this.future = this.scheduledExecutorService.scheduleWithFixedDelay(this.runForScheduler, this.initialDelay >= 0L ? this.initialDelay : this.period, this.period, this.timeUnit);
            } else {
               logger.tracef("did not start scheduled executor on %s because period was configured as %d", this, this.period);
            }

         }
      }
   }

   protected ActiveMQThreadFactory getThreadFactory() {
      return new ActiveMQThreadFactory(this.getClass().getSimpleName() + "-scheduled-threads", false, this.getThisClassLoader());
   }

   private ClassLoader getThisClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ActiveMQScheduledComponent.this.getClass().getClassLoader();
         }
      });
   }

   public void delay() {
      int value = this.delayed.incrementAndGet();
      if (value > 10) {
         this.delayed.decrementAndGet();
      } else {
         this.scheduledExecutorService.schedule(this.runForScheduler, Math.min(this.period, this.period * (long)value), this.timeUnit);
      }

   }

   public long getPeriod() {
      return this.period;
   }

   public synchronized ActiveMQScheduledComponent setPeriod(long period) {
      this.period = period;
      this.restartIfNeeded();
      return this;
   }

   public synchronized ActiveMQScheduledComponent setPeriod(long period, TimeUnit unit) {
      this.period = period;
      this.timeUnit = unit;
      this.restartIfNeeded();
      return this;
   }

   public long getInitialDelay() {
      return this.initialDelay;
   }

   public synchronized ActiveMQScheduledComponent setInitialDelay(long initialDelay) {
      this.initialDelay = initialDelay;
      this.restartIfNeeded();
      return this;
   }

   public synchronized ActiveMQScheduledComponent setInitialDelayAndPeriod(long initialDelay, long period) {
      this.period = period;
      this.initialDelay = initialDelay;
      this.restartIfNeeded();
      return this;
   }

   public synchronized ActiveMQScheduledComponent setInitialDelayAndPeriod(long initialDelay, long period, TimeUnit timeUnit) {
      this.period = period;
      this.initialDelay = initialDelay;
      this.timeUnit = timeUnit;
      this.restartIfNeeded();
      return this;
   }

   public TimeUnit getTimeUnit() {
      return this.timeUnit;
   }

   public synchronized ActiveMQScheduledComponent setTimeUnit(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
      this.restartIfNeeded();
      return this;
   }

   public void stop() {
      if (this.future != null) {
         this.future.cancel(false);
         this.future = null;
      }

      if (this.startedOwnScheduler) {
         this.scheduledExecutorService.shutdownNow();
         this.scheduledExecutorService = null;
         this.startedOwnScheduler = false;
      }

   }

   public synchronized boolean isStarted() {
      return this.future != null;
   }

   private void restartIfNeeded() {
      if (this.isStarted()) {
         this.stop();
         this.start();
      }

   }
}
