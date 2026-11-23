package org.apache.openejb.core.ivm.naming;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.openejb.core.ivm.EjbObjectInputStream;
import org.apache.openejb.core.ivm.IntraVmCopyMonitor;

public class CrossClassLoaderJndiReference extends IntraVmJndiReference {
   public CrossClassLoaderJndiReference(String jndiName) {
      super(jndiName);
   }

   public Object getObject() throws javax.naming.NamingException {
      Object o = super.getObject();

      try {
         o = copy(o);
         return o;
      } catch (Exception e) {
         throw new NamingException("Error copying object into local class loader", e);
      }
   }

   private static Object copy(Object source) throws Exception {
      IntraVmCopyMonitor.preCrossClassLoaderOperation();

      Object var5;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
         ObjectOutputStream out = new ObjectOutputStream(baos);
         out.writeObject(source);
         out.close();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream in = new EjbObjectInputStream(bais);
         var5 = in.readObject();
      } finally {
         IntraVmCopyMonitor.postCrossClassLoaderOperation();
      }

      return var5;
   }
}
