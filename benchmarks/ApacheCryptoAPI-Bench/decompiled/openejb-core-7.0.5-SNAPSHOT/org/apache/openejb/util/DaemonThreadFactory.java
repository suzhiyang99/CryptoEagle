package org.apache.openejb.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.openejb.core.ParentClassLoaderFinder;

public class DaemonThreadFactory implements ThreadFactory {
   private final String name;
   private final ThreadGroup group;
   private final AtomicInteger ids;

   public DaemonThreadFactory(Object... name) {
      this.ids = new AtomicInteger(0);
      this.name = Join.join(" ", name).trim();
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
         this.group = securityManager.getThreadGroup();
      } else {
         this.group = Thread.currentThread().getThreadGroup();
      }

   }

   public DaemonThreadFactory(Class... clazz) {
      this(asStrings(clazz));
   }

   private static Object[] asStrings(Class[] clazz) {
      String[] strings = new String[clazz.length];
      int i = 0;

      for(Class c : clazz) {
         strings[i++] = c.getSimpleName();
      }

      return strings;
   }

   public Thread newThread(Runnable runnable) {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(ParentClassLoaderFinder.Helper.get());

      Thread var4;
      try {
         Thread thread = new Thread(this.group, runnable, this.name + " - " + this.ids.incrementAndGet());
         if (!thread.isDaemon()) {
            thread.setDaemon(true);
         }

         if (thread.getPriority() != 5) {
            thread.setPriority(5);
         }

         var4 = thread;
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }

      return var4;
   }
}
