package org.apache.openejb.threads.impl;

import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.concurrent.ManageableThread;
import javax.enterprise.concurrent.ManagedThreadFactory;

public class ManagedThreadFactoryImpl implements ManagedThreadFactory {
   private static final AtomicInteger ID = new AtomicInteger();
   private final String prefix;

   public ManagedThreadFactoryImpl() {
      this.prefix = "managed-thread-";
   }

   public ManagedThreadFactoryImpl(String prefix) {
      this.prefix = prefix;
   }

   public Thread newThread(Runnable r) {
      Thread thread = new ManagedThread(r);
      thread.setDaemon(true);
      thread.setName(this.prefix + ID.incrementAndGet());
      thread.setContextClassLoader(ManagedThreadFactoryImpl.class.getClassLoader());
      return thread;
   }

   public static class ManagedThread extends Thread implements ManageableThread {
      public ManagedThread(Runnable r) {
         super(r);
      }

      public boolean isShutdown() {
         return !this.isAlive();
      }
   }
}
