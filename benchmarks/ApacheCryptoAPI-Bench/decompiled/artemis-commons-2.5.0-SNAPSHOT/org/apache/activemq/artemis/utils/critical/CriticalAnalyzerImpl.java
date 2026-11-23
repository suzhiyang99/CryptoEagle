package org.apache.activemq.artemis.utils.critical;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.artemis.core.server.ActiveMQScheduledComponent;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.jboss.logging.Logger;

public class CriticalAnalyzerImpl implements CriticalAnalyzer {
   private final Logger logger = Logger.getLogger(CriticalAnalyzer.class);
   private volatile long timeoutNanoSeconds;
   private volatile long checkTimeNanoSeconds;
   private final ActiveMQScheduledComponent scheduledComponent;
   private final AtomicBoolean running;
   private CopyOnWriteArrayList<CriticalAction> actions;
   private final ConcurrentHashSet<CriticalComponent> components;

   public CriticalAnalyzerImpl() {
      this.checkTimeNanoSeconds = TimeUnit.SECONDS.toNanos(60L);
      this.running = new AtomicBoolean(false);
      this.actions = new CopyOnWriteArrayList();
      this.components = new ConcurrentHashSet<CriticalComponent>();
      this.scheduledComponent = new ActiveMQScheduledComponent((ScheduledExecutorService)null, (Executor)null, this.checkTimeNanoSeconds, TimeUnit.NANOSECONDS, false) {
         public void run() {
            CriticalAnalyzerImpl.this.logger.trace("Checking critical analyzer");
            CriticalAnalyzerImpl.this.check();
         }
      };
   }

   public void clear() {
      this.actions.clear();
      this.components.clear();
   }

   public boolean isMeasuring() {
      return true;
   }

   public void add(CriticalComponent component) {
      this.components.add(component);
   }

   public void remove(CriticalComponent component) {
      this.components.remove(component);
   }

   public CriticalAnalyzer setCheckTime(long timeout, TimeUnit unit) {
      this.checkTimeNanoSeconds = unit.toNanos(timeout);
      this.scheduledComponent.setPeriod(timeout, unit);
      return this;
   }

   public long getCheckTimeNanoSeconds() {
      if (this.checkTimeNanoSeconds == 0L) {
         this.checkTimeNanoSeconds = this.getTimeout(TimeUnit.NANOSECONDS) / 2L;
      }

      return this.checkTimeNanoSeconds;
   }

   public CriticalAnalyzer setTimeout(long timeout, TimeUnit unit) {
      if (this.checkTimeNanoSeconds <= 0L) {
         this.setCheckTime(timeout / 2L, unit);
      }

      this.timeoutNanoSeconds = unit.toNanos(timeout);
      return this;
   }

   public long getTimeout(TimeUnit unit) {
      if (this.timeoutNanoSeconds == 0L) {
         this.timeoutNanoSeconds = TimeUnit.MINUTES.toNanos(2L);
      }

      return unit.convert(this.timeoutNanoSeconds, TimeUnit.NANOSECONDS);
   }

   public CriticalAnalyzer addAction(CriticalAction action) {
      this.actions.add(action);
      return this;
   }

   public void check() {
      boolean retry = true;

      while(retry) {
         try {
            for(CriticalComponent component : this.components) {
               if (component.isExpired(this.timeoutNanoSeconds)) {
                  this.fireAction(component);
                  return;
               }
            }

            retry = false;
         } catch (ConcurrentModificationException var4) {
         }
      }

   }

   private void fireAction(CriticalComponent component) {
      for(CriticalAction action : this.actions) {
         try {
            action.run(component);
         } catch (Throwable e) {
            this.logger.warn(e.getMessage(), e);
         }
      }

      this.actions.clear();
   }

   public void start() {
      this.scheduledComponent.start();
   }

   public void stop() {
      this.scheduledComponent.stop();
   }

   public boolean isStarted() {
      return this.scheduledComponent.isStarted();
   }
}
