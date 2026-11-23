package org.apache.openejb.core.timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.timer.quartz.PatchedStdJDBCDelegate;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.quartz.JobBuilder;
import org.apache.openejb.quartz.JobDataMap;
import org.apache.openejb.quartz.JobDetail;
import org.apache.openejb.quartz.Scheduler;
import org.apache.openejb.quartz.SchedulerException;
import org.apache.openejb.quartz.Trigger;
import org.apache.openejb.quartz.TriggerKey;
import org.apache.openejb.quartz.Trigger.TriggerState;
import org.apache.openejb.quartz.impl.StdSchedulerFactory;
import org.apache.openejb.quartz.impl.jdbcjobstore.JobStoreSupport;
import org.apache.openejb.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.apache.openejb.quartz.impl.triggers.AbstractTrigger;
import org.apache.openejb.quartz.listeners.SchedulerListenerSupport;
import org.apache.openejb.quartz.simpl.RAMJobStore;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.SetAccessible;

public class EjbTimerServiceImpl implements EjbTimerService, Serializable {
   private static final long serialVersionUID = 1L;
   private static final Logger log;
   public static final String QUARTZ_JMX = "org.apache.openejb.quartz.scheduler.jmx.export";
   public static final String QUARTZ_MAKE_SCHEDULER_THREAD_DAEMON = "org.apache.openejb.quartz.scheduler.makeSchedulerThreadDaemon";
   public static final String OPENEJB_TIMEOUT_JOB_NAME = "OPENEJB_TIMEOUT_JOB";
   public static final String OPENEJB_TIMEOUT_JOB_GROUP_NAME = "OPENEJB_TIMEOUT_GROUP";
   public static final String EJB_TIMER_RETRY_ATTEMPTS = "EjbTimer.RetryAttempts";
   public static final String OPENEJB_QUARTZ_USE_TCCL = "openejb.quartz.use-TCCL";
   private boolean transacted;
   private int retryAttempts;
   private transient TransactionManager transactionManager;
   private transient BeanContext deployment;
   private transient TimerStore timerStore;
   private transient Scheduler scheduler;

   public EjbTimerServiceImpl(BeanContext deployment, TimerStore timerStore) {
      this(deployment, getDefaultTransactionManager(), timerStore, -1);
      log.isDebugEnabled();
   }

   public static TransactionManager getDefaultTransactionManager() {
      return (TransactionManager)SystemInstance.get().getComponent(TransactionManager.class);
   }

   public EjbTimerServiceImpl(BeanContext deployment, TransactionManager transactionManager, TimerStore timerStore, int retryAttempts) {
      this.deployment = deployment;
      this.transactionManager = transactionManager;
      this.timerStore = timerStore;
      TransactionType transactionType = deployment.getTransactionType(deployment.getEjbTimeout());
      this.transacted = transactionType == TransactionType.Required || transactionType == TransactionType.RequiresNew;
      this.retryAttempts = retryAttempts;
      if (retryAttempts < 0) {
         this.retryAttempts = deployment.getOptions().get("EjbTimer.RetryAttempts", 1);
      }

   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeUTF(this.deployment.getDeploymentID().toString());
      out.writeBoolean(this.transacted);
      out.writeInt(this.retryAttempts);
   }

   private void readObject(ObjectInputStream in) throws IOException {
      String dId = in.readUTF();
      this.transacted = in.readBoolean();
      this.retryAttempts = in.readInt();
      this.deployment = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getBeanContext(dId);
      this.transactionManager = getDefaultTransactionManager();
      this.timerStore = this.deployment.getEjbTimerService().getTimerStore();
      this.scheduler = (Scheduler)Proxy.newProxyInstance(this.deployment.getClassLoader(), new Class[]{Scheduler.class}, new LazyScheduler(this.deployment));
   }

   public static synchronized Scheduler getDefaultScheduler(BeanContext deployment) {
      Scheduler scheduler = (Scheduler)deployment.get(Scheduler.class);
      if (scheduler != null) {
         boolean valid;
         try {
            valid = !scheduler.isShutdown();
         } catch (Exception var18) {
            valid = false;
         }

         if (valid) {
            return scheduler;
         }
      }

      synchronized(deployment.getId()) {
         scheduler = (Scheduler)deployment.get(Scheduler.class);
         if (scheduler != null) {
            return scheduler;
         } else {
            Properties properties = new Properties();
            int quartzProps = 0;
            quartzProps += putAll(properties, SystemInstance.get().getProperties());
            quartzProps += putAll(properties, deployment.getModuleContext().getAppContext().getProperties());
            quartzProps += putAll(properties, deployment.getModuleContext().getProperties());
            quartzProps += putAll(properties, deployment.getProperties());
            boolean newInstance = quartzProps > 0;
            SystemInstance systemInstance = SystemInstance.get();
            scheduler = (Scheduler)systemInstance.getComponent(Scheduler.class);
            Scheduler thisScheduler;
            if (scheduler != null && !newInstance) {
               thisScheduler = scheduler;
            } else {
               boolean useTccl = "true".equalsIgnoreCase(properties.getProperty("openejb.quartz.use-TCCL", "false"));
               defaultQuartzConfiguration(properties, deployment, newInstance, useTccl);

               try {
                  ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
                  if (useTccl) {
                     Thread.currentThread().setContextClassLoader(deployment.getClassLoader());
                  } else {
                     Thread.currentThread().setContextClassLoader(EjbTimerServiceImpl.class.getClassLoader());
                  }

                  try {
                     thisScheduler = (new StdSchedulerFactory(properties)).getScheduler();
                     thisScheduler.start();
                  } finally {
                     Thread.currentThread().setContextClassLoader(oldCl);
                  }

                  JobDetail job = JobBuilder.newJob(EjbTimeoutJob.class).withIdentity("OPENEJB_TIMEOUT_JOB", "OPENEJB_TIMEOUT_GROUP").storeDurably(true).requestRecovery(false).build();
                  thisScheduler.addJob(job, true);
               } catch (SchedulerException e) {
                  throw new OpenEJBRuntimeException("Fail to initialize the default scheduler", e);
               }

               if (!newInstance) {
                  systemInstance.setComponent(Scheduler.class, thisScheduler);
               }
            }

            deployment.set(Scheduler.class, thisScheduler);
            return thisScheduler;
         }
      }
   }

   private static void defaultQuartzConfiguration(Properties properties, BeanContext deployment, boolean newInstance, boolean tccl) {
      String defaultThreadPool = DefaultTimerThreadPoolAdapter.class.getName();
      if (!properties.containsKey("org.apache.openejb.quartz.threadPool.class")) {
         properties.put("org.apache.openejb.quartz.threadPool.class", defaultThreadPool);
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.instanceName")) {
         properties.put("org.apache.openejb.quartz.scheduler.instanceName", "OpenEJB-TimerService-Scheduler");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.skipUpdateCheck")) {
         properties.put("org.apache.openejb.quartz.scheduler.skipUpdateCheck", "true");
      }

      if (!properties.containsKey("org.terracotta.quartz.skipUpdateCheck")) {
         properties.put("org.terracotta.quartz.skipUpdateCheck", "true");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdown")) {
         properties.put("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdown", "true");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdownWithWait")) {
         properties.put("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdownWithWait", "true");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.makeSchedulerThreadDaemon")) {
         properties.put("org.apache.openejb.quartz.scheduler.makeSchedulerThreadDaemon", "true");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.jmx.export") && LocalMBeanServer.isJMXActive()) {
         properties.put("org.apache.openejb.quartz.scheduler.jmx.export", "true");
      }

      if (!properties.containsKey("org.apache.openejb.quartz.scheduler.instanceId")) {
         if (!newInstance) {
            properties.setProperty("org.apache.openejb.quartz.scheduler.instanceId", "OpenEJB");
         } else {
            properties.setProperty("org.apache.openejb.quartz.scheduler.instanceId", deployment.getDeploymentID().toString());
         }
      }

      if (!tccl) {
         String driverDelegate = properties.getProperty("org.apache.openejb.quartz.jobStore.driverDelegateClass");
         if (driverDelegate != null && StdJDBCDelegate.class.getName().equals(driverDelegate)) {
            properties.put("org.apache.openejb.quartz.jobStore.driverDelegateClass", PatchedStdJDBCDelegate.class.getName());
         } else if (driverDelegate != null) {
            log.info("You use " + driverDelegate + " driver delegate with quartz, ensure it doesn't use ObjectInputStream otherwise your custom TimerData can induce some issues");
         }

         if (properties.containsKey("org.apache.openejb.quartz.jobStore.class") && !properties.containsKey("org.apache.openejb.quartz.jobStore.driverDelegateInitString")) {
            try {
               Class<?> clazz = EjbTimerServiceImpl.class.getClassLoader().loadClass(properties.getProperty("org.apache.openejb.quartz.jobStore.class"));
               if (JobStoreSupport.class.isAssignableFrom(clazz)) {
                  properties.put("org.apache.openejb.quartz.jobStore.driverDelegateInitString", "triggerPersistenceDelegateClasses=" + EJBCronTriggerPersistenceDelegate.class.getName());
               }
            } catch (Throwable var7) {
            }
         }
      }

      if (defaultThreadPool.equals(properties.get("org.apache.openejb.quartz.threadPool.class"))) {
         if (properties.containsKey("org.apache.openejb.quartz.threadPool.threadCount") && !properties.containsKey("openejb.timer.pool.size")) {
            log.info("Found property 'org.apache.openejb.quartz.threadPool.threadCount' for default thread pool, please use 'openejb.timer.pool.size' instead");
            properties.put("openejb.timer.pool.size", properties.getProperty("org.apache.openejb.quartz.threadPool.threadCount"));
         }

         if (properties.containsKey("org.quartz.threadPool.threadCount") && !properties.containsKey("openejb.timer.pool.size")) {
            log.info("Found property 'org.quartz.threadPool.threadCount' for default thread pool, please use 'openejb.timer.pool.size' instead");
            properties.put("openejb.timer.pool.size", properties.getProperty("org.quartz.threadPool.threadCount"));
         }
      }

      if (!properties.getProperty("org.apache.openejb.quartz.jobStore.class", RAMJobStore.class.getName()).equals(RAMJobStore.class.getName())) {
         properties.put("org.apache.openejb.quartz.jobStore.makeThreadsDaemons", properties.getProperty("org.apache.openejb.quartz.jobStore.makeThreadsDaemon", "true"));
      }

   }

   private static int putAll(Properties a, Properties b) {
      int number = 0;

      for(Map.Entry<Object, Object> entry : b.entrySet()) {
         String key = entry.getKey().toString();
         if (key.startsWith("org.quartz.") || key.startsWith("org.apache.openejb.quartz.") || key.startsWith("openejb.quartz.") || "openejb.timer.pool.size".equals(key) || "org.terracotta.quartz.skipUpdateCheck".equals(key)) {
            ++number;
         }

         Object value = entry.getValue();
         if (String.class.isInstance(value)) {
            if (!key.startsWith("org.quartz")) {
               a.put(key, value);
            } else {
               a.put("org.apache.openejb.quartz" + key.substring("org.quartz".length()), value);
            }
         }
      }

      return number;
   }

   public void stop() {
      this.cleanTimerData();
      this.shutdownMyScheduler();
   }

   private void cleanTimerData() {
      if (this.timerStore != null && this.scheduler != null && this.deployment != null) {
         Collection<TimerData> timerDatas = this.timerStore.getTimers(this.deployment.getDeploymentID().toString());
         if (timerDatas != null) {
            for(TimerData data : timerDatas) {
               Trigger trigger = data.getTrigger();
               if (trigger != null) {
                  TriggerKey key = trigger.getKey();

                  try {
                     data.stop();
                  } catch (EJBException var7) {
                     log.warning("An error occured deleting trigger '" + key + "' on bean " + this.deployment.getDeploymentID());
                  }
               }
            }

         }
      }
   }

   private void shutdownMyScheduler() {
      if (this.scheduler != null) {
         boolean defaultScheduler = false;
         Scheduler ds = (Scheduler)SystemInstance.get().getComponent(Scheduler.class);

         try {
            defaultScheduler = ds == this.scheduler || this.scheduler.getSchedulerName().equals(ds.getSchedulerName());
         } catch (Exception var4) {
         }

         if (!defaultScheduler) {
            shutdown(this.scheduler);
         }

      }
   }

   public static void shutdown() {
      shutdown((Scheduler)SystemInstance.get().getComponent(Scheduler.class));
   }

   private static void shutdown(final Scheduler s) throws OpenEJBRuntimeException {
      try {
         if (null != s && !s.isShutdown() && s.isStarted()) {
            try {
               s.pauseAll();
            } catch (SchedulerException var13) {
            }

            long timeout = SystemInstance.get().getOptions().get("openejb.quartz.timeout", 10000L);
            if (timeout < 1000L) {
               timeout = 1000L;
            }

            final CountDownLatch shutdownWait = new CountDownLatch(1);
            final AtomicReference<Throwable> ex = new AtomicReference();
            String n = "Unknown";

            try {
               n = s.getSchedulerName();
            } catch (SchedulerException var12) {
               log.warning("EjbTimerService scheduler has no name");
            }

            final String name = n;
            Thread stopThread = new Thread(n + " shutdown wait") {
               public void run() {
                  try {
                     s.getListenerManager().addSchedulerListener(new SchedulerListenerSupport() {
                        public void schedulerShutdown() {
                           shutdownWait.countDown();
                        }
                     });
                     s.shutdown(true);
                  } catch (Throwable e) {
                     ex.set(e);
                     shutdownWait.countDown();
                  }

               }
            };
            stopThread.setDaemon(true);
            stopThread.start();
            boolean stopped = false;

            try {
               stopped = shutdownWait.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException var11) {
            }

            try {
               if (!stopped || !s.isShutdown()) {
                  stopThread = new Thread(name + " shutdown forced") {
                     public void run() {
                        try {
                           s.shutdown(false);
                           EjbTimerServiceImpl.log.warning("Forced " + name + " shutdown - Jobs may be incomplete");
                        } catch (Throwable e) {
                           ex.set(e);
                        }

                     }
                  };
                  stopThread.setDaemon(true);
                  stopThread.start();

                  try {
                     stopThread.join(timeout);
                  } catch (InterruptedException var10) {
                  }
               }
            } catch (Throwable e) {
               ex.set(e);
            }

            Throwable t = (Throwable)ex.get();
            if (null != t) {
               throw new OpenEJBRuntimeException("Unable to shutdown " + n + " scheduler", t);
            }
         }
      } catch (SchedulerException var15) {
      }

   }

   public void start() throws TimerStoreException {
      if (!this.isStarted()) {
         this.scheduler = getDefaultScheduler(this.deployment);

         for(TimerData timerData : this.timerStore.loadTimers(this, (String)this.deployment.getDeploymentID())) {
            this.initializeNewTimer(timerData);
         }

      }
   }

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public void schedule(TimerData timerData) throws TimerStoreException {
      this.start();
      if (this.scheduler == null) {
         throw new TimerStoreException("Scheduler is not configured properly");
      } else {
         timerData.setScheduler(this.scheduler);
         Trigger trigger = timerData.getTrigger();
         if (null == trigger) {
            try {
               if (!this.scheduler.isShutdown()) {
                  log.warning("Failed to schedule: " + timerData.getInfo());
               }
            } catch (SchedulerException var7) {
            }
         }

         if (trigger instanceof AbstractTrigger) {
            AbstractTrigger<?> atrigger = (AbstractTrigger)trigger;
            atrigger.setJobName("OPENEJB_TIMEOUT_JOB");
            atrigger.setJobGroup("OPENEJB_TIMEOUT_GROUP");
            JobDataMap triggerDataMap = trigger.getJobDataMap();
            triggerDataMap.put("EJB_TIMERS_SERVICE", this);
            triggerDataMap.put("TIMER_DATA", timerData);

            try {
               TriggerKey triggerKey = new TriggerKey(atrigger.getName(), atrigger.getGroup());
               if (!this.scheduler.checkExists(triggerKey)) {
                  this.scheduler.scheduleJob(trigger);
               } else if (TriggerState.PAUSED.equals(this.scheduler.getTriggerState(triggerKey))) {
                  this.scheduler.unscheduleJob(triggerKey);
                  this.scheduler.scheduleJob(trigger);
               }
            } catch (Exception e) {
               log.error("Could not schedule timer " + timerData, (Throwable)e);
            }

         } else {
            throw new OpenEJBRuntimeException("the trigger was not an AbstractTrigger - Should not be possible: " + trigger);
         }
      }
   }

   public void cancelled(TimerData timerData) {
      this.timerStore.removeTimer(timerData.getId());
   }

   public void addTimerData(TimerData timerData) {
      try {
         this.timerStore.addTimerData(timerData);
      } catch (Exception e) {
         log.warning("Could not add timer of type " + timerData.getType().name() + " due to " + e.getMessage());
      }

   }

   public Timer getTimer(long timerId) {
      TimerData timerData = this.timerStore.getTimer((String)this.deployment.getDeploymentID(), timerId);
      return timerData != null ? timerData.getTimer() : null;
   }

   public Collection<Timer> getTimers(Object primaryKey) throws IllegalStateException {
      this.checkState();
      Collection<Timer> timers = new ArrayList();

      for(TimerData timerData : this.timerStore.getTimers((String)this.deployment.getDeploymentID())) {
         timers.add(timerData.getTimer());
      }

      return timers;
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, long duration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      if (duration < 0L) {
         throw new IllegalArgumentException("duration is negative: " + duration);
      } else {
         this.checkState();
         Date expiration = new Date(System.currentTimeMillis() + duration);

         try {
            TimerData timerData = this.timerStore.createSingleActionTimer(this, (String)this.deployment.getDeploymentID(), primaryKey, timeoutMethod, expiration, timerConfig);
            this.initializeNewTimer(timerData);
            return timerData.getTimer();
         } catch (TimerStoreException e) {
            throw new EJBException(e);
         }
      }
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, long initialDuration, long intervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      if (initialDuration < 0L) {
         throw new IllegalArgumentException("initialDuration is negative: " + initialDuration);
      } else if (intervalDuration < 0L) {
         throw new IllegalArgumentException("intervalDuration is negative: " + intervalDuration);
      } else {
         this.checkState();
         Date initialExpiration = new Date(System.currentTimeMillis() + initialDuration);

         try {
            TimerData timerData = this.timerStore.createIntervalTimer(this, (String)this.deployment.getDeploymentID(), primaryKey, timeoutMethod, initialExpiration, intervalDuration, timerConfig);
            this.initializeNewTimer(timerData);
            return timerData.getTimer();
         } catch (TimerStoreException e) {
            throw new EJBException(e);
         }
      }
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, Date expiration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      if (expiration == null) {
         throw new IllegalArgumentException("expiration is null");
      } else if (expiration.getTime() < 0L) {
         throw new IllegalArgumentException("expiration is negative: " + expiration.getTime());
      } else {
         this.checkState();

         try {
            TimerData timerData = this.timerStore.createSingleActionTimer(this, (String)this.deployment.getDeploymentID(), primaryKey, timeoutMethod, expiration, timerConfig);
            this.initializeNewTimer(timerData);
            return timerData.getTimer();
         } catch (TimerStoreException e) {
            throw new EJBException(e);
         }
      }
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, Date initialExpiration, long intervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      if (initialExpiration == null) {
         throw new IllegalArgumentException("initialExpiration is null");
      } else if (initialExpiration.getTime() < 0L) {
         throw new IllegalArgumentException("initialExpiration is negative: " + initialExpiration.getTime());
      } else if (intervalDuration < 0L) {
         throw new IllegalArgumentException("intervalDuration is negative: " + intervalDuration);
      } else {
         this.checkState();

         try {
            TimerData timerData = this.timerStore.createIntervalTimer(this, (String)this.deployment.getDeploymentID(), primaryKey, timeoutMethod, initialExpiration, intervalDuration, timerConfig);
            this.initializeNewTimer(timerData);
            return timerData.getTimer();
         } catch (TimerStoreException e) {
            throw new EJBException(e);
         }
      }
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, ScheduleExpression scheduleExpression, TimerConfig timerConfig) {
      if (scheduleExpression == null) {
         throw new IllegalArgumentException("scheduleExpression is null");
      } else {
         this.checkState();

         try {
            TimerData timerData = this.timerStore.createCalendarTimer(this, (String)this.deployment.getDeploymentID(), primaryKey, timeoutMethod, scheduleExpression, timerConfig, false);
            this.initializeNewTimer(timerData);
            return timerData.getTimer();
         } catch (TimerStoreException e) {
            throw new EJBException(e);
         }
      }
   }

   public TimerStore getTimerStore() {
      return this.timerStore;
   }

   public boolean isStarted() {
      return this.scheduler != null;
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   private void initializeNewTimer(TimerData timerData) {
      timerData.newTimer();
   }

   private void checkState() throws IllegalStateException {
      BaseContext context = (BaseContext)this.deployment.get(EJBContext.class);
      context.doCheck(BaseContext.Call.timerMethod);
   }

   public void ejbTimeout(TimerData timerData) {
      Thread thread = Thread.currentThread();
      ClassLoader loader = thread.getContextClassLoader();

      try {
         Timer timer = this.getTimer(timerData.getId());
         if (timer == null && this.timerStore instanceof MemoryTimerStore && timerData.getTimer() != null) {
            try {
               this.timerStore.addTimerData(timerData);
               timer = timerData.getTimer();
            } catch (TimerStoreException var47) {
            }
         }

         for(int tries = 0; tries < 1 + this.retryAttempts; ++tries) {
            boolean retry = false;
            if (this.transacted) {
               try {
                  this.transactionManager.begin();
               } catch (Exception e) {
                  log.warning("Exception occured while starting container transaction", (Throwable)e);
                  return;
               }
            }

            try {
               RpcContainer container = (RpcContainer)this.deployment.getContainer();
               if (container == null) {
                  return;
               }

               Method ejbTimeout = timerData.getTimeoutMethod();
               if (ejbTimeout == null) {
                  return;
               }

               thread.setContextClassLoader(this.deployment.getClassLoader() != null ? this.deployment.getClassLoader() : loader);
               SetAccessible.on(ejbTimeout);
               container.invoke(this.deployment.getDeploymentID(), InterfaceType.TIMEOUT, ejbTimeout.getDeclaringClass(), ejbTimeout, new Object[]{timer}, timerData.getPrimaryKey());
            } catch (RuntimeException e) {
               retry = true;
               log.warning("RuntimeException from ejbTimeout on " + this.deployment.getDeploymentID(), (Throwable)e);

               try {
                  this.transactionManager.setRollbackOnly();
               } catch (SystemException e1) {
                  log.warning("Exception occured while setting RollbackOnly for container transaction", (Throwable)e1);
               }
            } catch (OpenEJBException e) {
               retry = true;
               if (ApplicationException.class.isInstance(e)) {
                  log.debug("Exception from ejbTimeout on " + this.deployment.getDeploymentID(), (Throwable)e);
               } else {
                  log.warning("Exception from ejbTimeout on " + this.deployment.getDeploymentID(), (Throwable)e);
               }

               if (this.transacted) {
                  try {
                     this.transactionManager.setRollbackOnly();
                  } catch (SystemException e1) {
                     log.warning("Exception occured while setting RollbackOnly for container transaction", (Throwable)e1);
                  }
               }
            } finally {
               try {
                  if (!this.transacted) {
                     if (!retry) {
                        return;
                     }
                  } else {
                     if (this.transactionManager.getStatus() == 0) {
                        this.transactionManager.commit();
                        return;
                     }

                     this.transactionManager.rollback();
                  }
               } catch (Exception e) {
                  log.warning("Exception occured while completing container transaction", (Throwable)e);
               }

            }
         }

         log.warning("Failed to execute ejbTimeout on " + timerData.getDeploymentId() + " successfully within " + this.retryAttempts + " attempts");
      } catch (RuntimeException e) {
         log.warning("RuntimeException occured while calling ejbTimeout", (Throwable)e);
         throw e;
      } catch (Error e) {
         log.warning("Error occured while calling ejbTimeout", (Throwable)e);
         throw e;
      } finally {
         thread.setContextClassLoader(loader);
         if (timerData.getType() == TimerType.SingleAction) {
            this.timerStore.removeTimer(timerData.getId());
            timerData.setExpired(true);
         } else if (timerData.getType() == TimerType.Calendar && timerData.getNextTimeout() == null) {
            this.timerStore.removeTimer(timerData.getId());
            timerData.setExpired(true);
         } else {
            this.timerStore.updateIntervalTimer(timerData);
         }

      }
   }

   static {
      log = Logger.getInstance(LogCategory.TIMER, "org.apache.openejb.util.resources");
   }

   private static class LazyScheduler implements InvocationHandler {
      private final BeanContext ejb;

      public LazyScheduler(BeanContext deployment) {
         this.ejb = deployment;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return method.invoke(EjbTimerServiceImpl.getDefaultScheduler(this.ejb), args);
      }
   }
}
