package org.apache.openejb.resource.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.enterprise.concurrent.ManagedThreadFactory;
import org.apache.openejb.threads.impl.ManagedScheduledExecutorServiceImpl;
import org.apache.openejb.threads.impl.ManagedThreadFactoryImpl;
import org.apache.openejb.threads.reject.CURejectHandler;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ManagedScheduledExecutorServiceImplFactory {
   private int core = 5;
   private String threadFactory = ManagedThreadFactoryImpl.class.getName();

   public ManagedScheduledExecutorServiceImpl create() {
      return new ManagedScheduledExecutorServiceImpl(this.createScheduledExecutorService());
   }

   private ScheduledExecutorService createScheduledExecutorService() {
      ManagedThreadFactory managedThreadFactory;
      try {
         managedThreadFactory = ThreadFactories.findThreadFactory(this.threadFactory);
      } catch (Exception e) {
         Logger.getInstance(LogCategory.OPENEJB, ManagedScheduledExecutorServiceImplFactory.class).warning("Unable to create configured thread factory: " + this.threadFactory, (Throwable)e);
         managedThreadFactory = new ManagedThreadFactoryImpl();
      }

      return new ScheduledThreadPoolExecutor(this.core, managedThreadFactory, CURejectHandler.INSTANCE);
   }

   public void setCore(int core) {
      this.core = core;
   }

   public void setThreadFactory(String threadFactory) {
      this.threadFactory = threadFactory;
   }
}
