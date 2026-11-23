package org.apache.openejb.core.ivm;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import org.apache.openejb.core.rmi.BlacklistClassResolver;

public class EjbObjectInputStream extends ObjectInputStream {
   public EjbObjectInputStream(InputStream in) throws IOException {
      super(in);
   }

   protected Class resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
      String checkedName = BlacklistClassResolver.DEFAULT.check(classDesc.getName());

      try {
         return Class.forName(checkedName, false, this.getClassloader());
      } catch (ClassNotFoundException var5) {
         String n = classDesc.getName();
         if (n.equals("boolean")) {
            return Boolean.TYPE;
         } else if (n.equals("byte")) {
            return Byte.TYPE;
         } else if (n.equals("char")) {
            return Character.TYPE;
         } else if (n.equals("short")) {
            return Short.TYPE;
         } else if (n.equals("int")) {
            return Integer.TYPE;
         } else if (n.equals("long")) {
            return Long.TYPE;
         } else if (n.equals("float")) {
            return Float.TYPE;
         } else {
            return n.equals("double") ? Double.TYPE : this.getClass().getClassLoader().loadClass(checkedName);
         }
      }
   }

   protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
      Class[] cinterfaces = new Class[interfaces.length];

      for(int i = 0; i < interfaces.length; ++i) {
         cinterfaces[i] = this.getClassloader().loadClass(interfaces[i]);
      }

      try {
         return Proxy.getProxyClass(this.getClassloader(), cinterfaces);
      } catch (IllegalArgumentException e) {
         throw new ClassNotFoundException((String)null, e);
      }
   }

   ClassLoader getClassloader() {
      return Thread.currentThread().getContextClassLoader();
   }
}
