package org.apache.openejb.resource.quartz;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.quartz.Job;
import org.apache.openejb.quartz.JobDataMap;
import org.apache.openejb.quartz.JobExecutionContext;
import org.apache.openejb.quartz.JobExecutionException;
import org.apache.openejb.quartz.Scheduler;
import org.apache.openejb.quartz.SchedulerException;
import org.apache.openejb.quartz.impl.StdSchedulerFactory;
import org.apache.openejb.quartz.listeners.SchedulerListenerSupport;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class QuartzResourceAdapter implements ResourceAdapter {
   public static final String OPENEJB_QUARTZ_TIMEOUT = "openejb.quartz.timeout";
   private final AtomicReference<Throwable> ex = new AtomicReference();
   private final AtomicReference<Scheduler> scheduler = new AtomicReference();
   private final AtomicReference<BootstrapContext> bootstrapContext = new AtomicReference();
   private final AtomicReference<Thread> startThread = new AtomicReference();

   public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
      if (null != this.bootstrapContext.getAndSet(bootstrapContext)) {
         Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").warning("QuartzResourceAdapter is already starting");
      } else {
         final CountDownLatch signal = new CountDownLatch(1);
         long timeout = SystemInstance.get().getOptions().get("openejb.quartz.timeout", 10000L);
         if (timeout < 1000L) {
            timeout = 1000L;
         }

         if (timeout > 60000L) {
            timeout = 60000L;
         }

         JavaSecurityManagers.setSystemProperty("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdown", JavaSecurityManagers.getSystemProperty("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdown", "true"));
         JavaSecurityManagers.setSystemProperty("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdownWithWait", JavaSecurityManagers.getSystemProperty("org.apache.openejb.quartz.scheduler.interruptJobsOnShutdownWithWait", "true"));
         JavaSecurityManagers.setSystemProperty("org.apache.openejb.quartz.scheduler.skipUpdateCheck", JavaSecurityManagers.getSystemProperty("org.apache.openejb.quartz.scheduler.skipUpdateCheck", "true"));
         JavaSecurityManagers.setSystemProperty("org.terracotta.quartz.skipUpdateCheck", JavaSecurityManagers.getSystemProperty("org.terracotta.quartz.skipUpdateCheck", "true"));
         this.startThread.set(new Thread("Quartz Scheduler Start") {
            public void run() {
               try {
                  QuartzResourceAdapter.this.scheduler.set(StdSchedulerFactory.getDefaultScheduler());
               } catch (Exception e) {
                  QuartzResourceAdapter.this.ex.set(e);
                  return;
               }

               try {
                  ((Scheduler)QuartzResourceAdapter.this.scheduler.get()).getListenerManager().addSchedulerListener(new SchedulerListenerSupport() {
                     public void schedulerStarted() {
                        signal.countDown();
                     }
                  });
                  ((Scheduler)QuartzResourceAdapter.this.scheduler.get()).start();
               } catch (Throwable e) {
                  QuartzResourceAdapter.this.ex.set(e);
                  signal.countDown();
               }

            }
         });
         ((Thread)this.startThread.get()).setDaemon(true);
         ((Thread)this.startThread.get()).start();
         boolean started = false;

         try {
            started = signal.await(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var8) {
         }

         Throwable exception = (Throwable)this.ex.get();
         if (null != exception) {
            String err = "Error creating Quartz Scheduler";
            Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").error("Error creating Quartz Scheduler", exception);
            throw new ResourceAdapterInternalException("Error creating Quartz Scheduler", exception);
         } else {
            if (started) {
               Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").info("Started Quartz Scheduler");
            } else {
               Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").warning("Failed to start Quartz Scheduler within defined timeout, status unknown");
            }

         }
      }
   }

   public Scheduler getScheduler() {
      return (Scheduler)this.scheduler.get();
   }

   public BootstrapContext getBootstrapContext() {
      return (BootstrapContext)this.bootstrapContext.get();
   }

   public void stop() {
      final Scheduler s = (Scheduler)this.scheduler.getAndSet((Object)null);
      if (null != s) {
         if (null != this.startThread.get()) {
            ((Thread)this.startThread.get()).interrupt();
         }

         long timeout = SystemInstance.get().getOptions().get("openejb.quartz.timeout", 10000L);
         if (timeout < 1000L) {
            timeout = 1000L;
         }

         if (timeout > 60000L) {
            timeout = 60000L;
         }

         final CountDownLatch shutdownWait = new CountDownLatch(1);
         Thread stopThread = new Thread("Quartz Scheduler Requested Stop") {
            public void run() {
               try {
                  s.getListenerManager().addSchedulerListener(new SchedulerListenerSupport() {
                     public void schedulerShutdown() {
                        shutdownWait.countDown();
                     }
                  });
                  s.shutdown(true);
               } catch (Throwable e) {
                  QuartzResourceAdapter.this.ex.set(e);
                  shutdownWait.countDown();
               }

            }
         };
         stopThread.setDaemon(true);
         stopThread.start();
         boolean stopped = false;

         try {
            stopped = shutdownWait.await(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var9) {
         }

         try {
            if (!stopped || !s.isShutdown()) {
               stopThread = new Thread("Quartz Scheduler Forced Stop") {
                  public void run() {
                     try {
                        s.shutdown(false);
                        Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").warning("Forced Quartz stop - Jobs may be incomplete");
                     } catch (Throwable e) {
                        QuartzResourceAdapter.this.ex.set(e);
                     }

                  }
               };
               stopThread.setDaemon(true);
               stopThread.start();

               try {
                  stopThread.join(timeout);
               } catch (InterruptedException var8) {
               }
            }
         } catch (Throwable e) {
            this.ex.set(e);
         }
      }

      this.bootstrapContext.set((Object)null);
      if (null != this.ex.get()) {
         Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").warning("Error stopping Quartz Scheduler", (Throwable)this.ex.get());
      } else {
         Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").info("Stopped Quartz Scheduler");
      }

   }

   public void endpointActivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) throws ResourceException {
      Scheduler s = (Scheduler)this.scheduler.get();
      if (null == s) {
         throw new ResourceException("Quartz Scheduler is not available");
      } else {
         try {
            JobSpec spec = (JobSpec)activationSpec;
            MessageEndpoint endpoint = messageEndpointFactory.createEndpoint((XAResource)null);
            spec.setEndpoint(endpoint);
            Job job = (Job)endpoint;
            JobDataMap jobDataMap = spec.getDetail().getJobDataMap();
            jobDataMap.put(Data.class.getName(), new Data(job));
            s.scheduleJob(spec.getDetail(), spec.getTrigger());
         } catch (SchedulerException e) {
            throw new ResourceException("Failed to schedule job", e);
         }
      }
   }

   public void endpointDeactivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) {
      Scheduler s = (Scheduler)this.scheduler.get();
      if (null == s) {
         throw new IllegalStateException("Quartz Scheduler is not available");
      } else {
         JobSpec spec = null;

         try {
            spec = (JobSpec)activationSpec;
            s.deleteJob(spec.jobKey());
         } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to delete job", e);
         } finally {
            if (null != spec) {
               spec.getEndpoint().release();
            }

         }

      }
   }

   public XAResource[] getXAResources(ActivationSpec[] activationSpecs) throws ResourceException {
      return new XAResource[0];
   }

   public static class JobEndpoint implements Job {
      private static Method method;

      public void execute(JobExecutionContext execution) throws JobExecutionException {
         MessageEndpoint endpoint = null;
         JobExecutionException ex = null;

         try {
            JobDataMap jobDataMap = execution.getJobDetail().getJobDataMap();
            Data data = (Data)Data.class.cast(jobDataMap.get(Data.class.getName()));
            Job job = data.job;
            if (null == method) {
               method = job.getClass().getMethod("execute", JobExecutionContext.class);
            }

            endpoint = (MessageEndpoint)job;
            endpoint.beforeDelivery(method);
            job.execute(execution);
         } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
         } catch (ResourceException e) {
            ex = new JobExecutionException(e);
         } catch (Throwable t) {
            ex = new JobExecutionException(new Exception(t));
         } finally {
            if (null != endpoint) {
               try {
                  endpoint.afterDelivery();
               } catch (ResourceException e) {
                  ex = new JobExecutionException(e);
               }
            }

         }

         if (null != ex) {
            throw ex;
         }
      }
   }

   private static final class Data {
      private final Job job;

      private Data(Job job) {
         this.job = job;
      }
   }
}
