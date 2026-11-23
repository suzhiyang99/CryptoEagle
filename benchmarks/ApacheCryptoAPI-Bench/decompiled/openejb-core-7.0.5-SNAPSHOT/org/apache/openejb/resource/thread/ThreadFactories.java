package org.apache.openejb.resource.thread;

import java.util.concurrent.ThreadFactory;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.NamingException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

final class ThreadFactories {
   private ThreadFactories() {
   }

   public static ManagedThreadFactory findThreadFactory(String threadFactory) throws InstantiationException, IllegalAccessException, NamingException {
      try {
         Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(threadFactory);
         return (ManagedThreadFactory)(!ManagedThreadFactory.class.isAssignableFrom(aClass) && ThreadFactory.class.isAssignableFrom(aClass) ? new ManageMyThreadFactory((ThreadFactory)ThreadFactory.class.cast(aClass.newInstance())) : (ManagedThreadFactory)ManagedThreadFactory.class.cast(aClass.newInstance()));
      } catch (ClassNotFoundException var2) {
         return (ManagedThreadFactory)ManagedThreadFactory.class.cast(((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb:Resource/" + threadFactory));
      }
   }

   private static final class ManageMyThreadFactory implements ManagedThreadFactory {
      private final ThreadFactory delegate;

      private ManageMyThreadFactory(ThreadFactory delegate) {
         this.delegate = delegate;
      }

      public Thread newThread(Runnable r) {
         return this.delegate.newThread(r);
      }
   }
}
