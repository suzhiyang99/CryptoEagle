package org.apache.activemq.artemis.utils;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ActiveMQThreadFactory implements ThreadFactory {
   private String groupName;
   private final AtomicInteger threadCount;
   private final int threadPriority;
   private final boolean daemon;
   private final ClassLoader tccl;
   private final AccessControlContext acc;
   private final String prefix;

   public ActiveMQThreadFactory(String groupName, boolean daemon, ClassLoader tccl) {
      this(groupName, "Thread-", daemon, tccl);
   }

   public ActiveMQThreadFactory(String groupName, String prefix, boolean daemon, ClassLoader tccl) {
      this.threadCount = new AtomicInteger(0);
      this.groupName = groupName;
      this.prefix = prefix;
      this.threadPriority = 5;
      this.tccl = tccl;
      this.daemon = daemon;
      this.acc = AccessController.getContext();
   }

   public Thread newThread(Runnable command) {
      return this.acc != null ? (Thread)AccessController.doPrivileged(new ThreadCreateAction(command), this.acc) : this.createThread(command);
   }

   private Thread createThread(Runnable command) {
      Thread t = new Thread(command, this.prefix + this.threadCount.getAndIncrement() + " (" + this.groupName + ")");
      t.setDaemon(this.daemon);
      t.setPriority(this.threadPriority);
      t.setContextClassLoader(this.tccl);
      return t;
   }

   public static ActiveMQThreadFactory defaultThreadFactory() {
      String callerClassName = Thread.currentThread().getStackTrace()[2].getClassName();
      return new ActiveMQThreadFactory(callerClassName, false, (ClassLoader)null);
   }

   private final class ThreadCreateAction implements PrivilegedAction<Thread> {
      private final Runnable target;

      private ThreadCreateAction(Runnable target) {
         this.target = target;
      }

      public Thread run() {
         return ActiveMQThreadFactory.this.createThread(this.target);
      }
   }
}
