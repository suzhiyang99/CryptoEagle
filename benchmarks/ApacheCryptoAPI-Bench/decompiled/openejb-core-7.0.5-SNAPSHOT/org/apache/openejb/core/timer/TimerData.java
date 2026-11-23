package org.apache.openejb.core.timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import org.apache.openejb.BeanContext;
import org.apache.openejb.MethodContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.quartz.Calendar;
import org.apache.openejb.quartz.Scheduler;
import org.apache.openejb.quartz.SchedulerException;
import org.apache.openejb.quartz.Trigger;
import org.apache.openejb.quartz.TriggerKey;
import org.apache.openejb.quartz.impl.triggers.AbstractTrigger;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public abstract class TimerData implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String OPEN_EJB_TIMEOUT_TRIGGER_NAME_PREFIX = "OPEN_EJB_TIMEOUT_TRIGGER_";
   public static final String OPEN_EJB_TIMEOUT_TRIGGER_GROUP_NAME = "OPEN_EJB_TIMEOUT_TRIGGER_GROUP";
   private static final Logger log;
   private long id;
   private EjbTimerServiceImpl timerService;
   private String deploymentId;
   private Object primaryKey;
   private Method timeoutMethod;
   private Object info;
   private boolean persistent;
   private boolean autoScheduled;
   protected AbstractTrigger<?> trigger;
   protected Scheduler scheduler;
   private Timer timer;
   private boolean newTimer;
   private boolean cancelled;
   private boolean stopped;
   private boolean synchronizationRegistered;
   private boolean expired;

   public void setScheduler(Scheduler scheduler) {
      this.scheduler = scheduler;
   }

   public TimerData(long id, EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, TimerConfig timerConfig) {
      this.id = id;
      this.timerService = timerService;
      this.deploymentId = deploymentId;
      this.primaryKey = primaryKey;
      this.info = timerConfig == null ? null : timerConfig.getInfo();
      this.persistent = timerConfig == null || timerConfig.isPersistent();
      this.timer = new TimerImpl(this);
      this.timeoutMethod = timeoutMethod;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      this.doWriteObject(out);
   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeLong(this.id);
      out.writeUTF(this.deploymentId);
      out.writeBoolean(this.persistent);
      out.writeBoolean(this.autoScheduled);
      out.writeObject(this.timer);
      out.writeObject(this.primaryKey);
      out.writeObject(this.timerService);
      out.writeObject(this.info);
      out.writeObject(this.trigger);
      out.writeUTF(this.timeoutMethod.getName());
   }

   private void readObject(ObjectInputStream in) throws IOException {
      this.doReadObject(in);
   }

   protected void doReadObject(ObjectInputStream in) throws IOException {
      this.id = in.readLong();
      this.deploymentId = in.readUTF();
      this.persistent = in.readBoolean();
      this.autoScheduled = in.readBoolean();

      try {
         this.timer = (Timer)in.readObject();
         this.primaryKey = in.readObject();
         this.timerService = (EjbTimerServiceImpl)in.readObject();
         this.info = in.readObject();
         this.trigger = (AbstractTrigger)AbstractTrigger.class.cast(in.readObject());
      } catch (ClassNotFoundException e) {
         throw new IOException(e);
      }

      String mtd = in.readUTF();
      BeanContext beanContext = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getBeanContext(this.deploymentId);
      this.scheduler = this.timerService.getScheduler();
      Iterator<Map.Entry<Method, MethodContext>> it = beanContext.iteratorMethodContext();

      while(it.hasNext()) {
         MethodContext methodContext = (MethodContext)((Map.Entry)it.next()).getValue();
         Method method = methodContext.getBeanMethod();
         if (method != null && method.getName().equals(mtd)) {
            this.setTimeoutMethod(method);
            break;
         }
      }

   }

   public void stop() {
      if (this.trigger != null) {
         try {
            Scheduler s = this.timerService.getScheduler();
            if (!s.isShutdown()) {
               if (!this.isPersistent()) {
                  s.unscheduleJob(this.trigger.getKey());
               } else {
                  s.pauseTrigger(this.trigger.getKey());
               }
            }
         } catch (SchedulerException e) {
            throw new EJBException("fail to cancel the timer", e);
         }
      }

      this.cancelled = true;
      this.stopped = true;
   }

   public long getId() {
      return this.id;
   }

   public String getDeploymentId() {
      return this.deploymentId;
   }

   public Object getPrimaryKey() {
      return this.primaryKey;
   }

   public Object getInfo() {
      return this.info;
   }

   public Timer getTimer() {
      return this.timer;
   }

   public boolean isNewTimer() {
      return this.newTimer;
   }

   public void newTimer() {
      this.trigger = this.initializeTrigger();
      this.trigger.computeFirstFireTime((Calendar)null);
      this.trigger.setGroup("OPEN_EJB_TIMEOUT_TRIGGER_GROUP");
      this.trigger.setName("OPEN_EJB_TIMEOUT_TRIGGER_" + this.deploymentId + "_" + this.id);
      this.newTimer = true;

      try {
         this.registerTimerDataSynchronization();
      } catch (TimerStoreException e) {
         throw new EJBException("Failed to register new timer data synchronization", e);
      }
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void cancel() {
      if (!this.stopped) {
         this.timerService.cancelled(this);
         if (this.trigger != null) {
            try {
               Scheduler s = this.timerService.getScheduler();
               if (!s.isShutdown()) {
                  s.unscheduleJob(this.trigger.getKey());
               }
            } catch (SchedulerException e) {
               throw new EJBException("fail to cancel the timer", e);
            }
         }

         this.cancelled = true;

         try {
            this.registerTimerDataSynchronization();
         } catch (TimerStoreException e) {
            throw new EJBException("Failed to register timer data synchronization on cancel", e);
         }
      }
   }

   private void setTimeoutMethod(Method timeoutMethod) {
      this.timeoutMethod = timeoutMethod;
   }

   public Method getTimeoutMethod() {
      return this.timeoutMethod;
   }

   private void transactionComplete(boolean committed) throws TimerStoreException {
      if (this.newTimer) {
         this.newTimer = false;
         if (!this.isCancelled() && committed) {
            this.timerService.schedule(this);
         }
      } else if (!committed) {
         this.cancelled = false;
         this.timerService.addTimerData(this);
         this.timerService.schedule(this);
      }

   }

   private void registerTimerDataSynchronization() throws TimerStoreException {
      if (!this.synchronizationRegistered) {
         try {
            Transaction transaction = this.timerService.getTransactionManager().getTransaction();
            int status = transaction == null ? 6 : transaction.getStatus();
            if (transaction != null && status == 0 || status == 1) {
               transaction.registerSynchronization(new TimerDataSynchronization());
               this.synchronizationRegistered = true;
               return;
            }
         } catch (Exception e) {
            log.warning("Unable to register timer data transaction synchronization", (Throwable)e);
         }

         this.transactionComplete(true);
      }
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   public Trigger getTrigger() {
      if (this.scheduler != null) {
         try {
            TriggerKey key = new TriggerKey(this.trigger.getName(), this.trigger.getGroup());
            if (this.scheduler.checkExists(key)) {
               return this.scheduler.getTrigger(key);
            }
         } catch (SchedulerException var2) {
            return null;
         }
      }

      return this.trigger;
   }

   public Date getNextTimeout() {
      try {
         Thread.sleep(1L);
      } catch (InterruptedException e) {
         log.warning("Interrupted exception when waiting 1ms for the trigger to init", (Throwable)e);
      }

      Date nextTimeout = null;
      if (this.getTrigger() != null) {
         nextTimeout = this.getTrigger().getNextFireTime();
      }

      return nextTimeout;
   }

   public long getTimeRemaining() {
      Date nextTimeout = this.getNextTimeout();
      return nextTimeout.getTime() - System.currentTimeMillis();
   }

   public boolean isExpired() {
      return this.expired;
   }

   public void setExpired(boolean expired) {
      this.expired = expired;
   }

   public abstract TimerType getType();

   protected abstract AbstractTrigger<?> initializeTrigger();

   static {
      log = Logger.getInstance(LogCategory.TIMER, "org.apache.openejb.util.resources");
   }

   private class TimerDataSynchronization implements Synchronization {
      private TimerDataSynchronization() {
      }

      public void beforeCompletion() {
      }

      public void afterCompletion(int status) {
         TimerData.this.synchronizationRegistered = false;

         try {
            TimerData.this.transactionComplete(status == 3);
         } catch (TimerStoreException e) {
            throw new EJBException("Failed on afterCompletion", e);
         }
      }
   }
}
