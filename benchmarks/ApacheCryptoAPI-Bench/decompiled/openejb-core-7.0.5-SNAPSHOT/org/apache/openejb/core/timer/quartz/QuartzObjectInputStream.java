package org.apache.openejb.core.timer.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.apache.openejb.core.rmi.BlacklistClassResolver;
import org.apache.openejb.quartz.spi.ClassLoadHelper;

public class QuartzObjectInputStream extends ObjectInputStream {
   private final ClassLoadHelper loader;

   public QuartzObjectInputStream(InputStream binaryInput, ClassLoadHelper classLoadHelper) throws IOException {
      super(binaryInput);
      this.loader = classLoadHelper;
   }

   protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException, IOException {
      return this.loader.loadClass(BlacklistClassResolver.DEFAULT.check(desc.getName()));
   }
}
