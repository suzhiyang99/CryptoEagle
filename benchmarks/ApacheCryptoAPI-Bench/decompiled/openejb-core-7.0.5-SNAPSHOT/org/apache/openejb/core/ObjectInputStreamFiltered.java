package org.apache.openejb.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.apache.openejb.core.rmi.BlacklistClassResolver;

public class ObjectInputStreamFiltered extends ObjectInputStream {
   public ObjectInputStreamFiltered(InputStream in) throws IOException {
      super(in);
   }

   protected Class resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
      return super.resolveClass(BlacklistClassResolver.DEFAULT.check(classDesc));
   }
}
