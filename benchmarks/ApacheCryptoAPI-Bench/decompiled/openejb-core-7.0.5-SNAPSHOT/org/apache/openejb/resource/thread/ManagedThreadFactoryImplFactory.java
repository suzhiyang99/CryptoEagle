package org.apache.openejb.resource.thread;

import javax.enterprise.concurrent.ManagedThreadFactory;
import org.apache.openejb.threads.impl.ManagedThreadFactoryImpl;

public class ManagedThreadFactoryImplFactory {
   private String prefix = "openejb-managed-thread-";

   public ManagedThreadFactory create() {
      return new ManagedThreadFactoryImpl(this.prefix);
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
