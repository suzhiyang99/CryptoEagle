package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

public class ProxyInterfaceResolver {
   public static List<Class> getInterfaces(Class implementation, Class mainInterface, List<Class> interfaces) {
      List<Class> valid = new ArrayList();
      if (mainInterface != null) {
         valid.add(mainInterface);
      }

      for(Class interfce : interfaces) {
         if (interfce.isAssignableFrom(implementation)) {
            valid.add(interfce);
         }
      }

      List<Class> remotes = new ArrayList();
      List<Class> nonremotes = new ArrayList();

      for(Class interfce : valid) {
         if (Remote.class.isAssignableFrom(interfce)) {
            remotes.add(interfce);
         } else {
            nonremotes.add(interfce);
         }
      }

      if (remotes.size() == 0) {
         return valid;
      } else {
         valid.clear();
         remotes.remove(mainInterface);
         nonremotes.remove(mainInterface);
         valid.add(mainInterface);
         List<Signature> proxySignatures = getSignatures(mainInterface);
         if (Remote.class.isAssignableFrom(mainInterface)) {
            for(Class interfce : remotes) {
               addIfNotConflicting(interfce, valid, proxySignatures);
            }

            for(Class interfce : nonremotes) {
               addIfNotConflicting(interfce, valid, proxySignatures);
            }
         } else {
            for(Class interfce : nonremotes) {
               addIfNotConflicting(interfce, valid, proxySignatures);
            }

            for(Class interfce : remotes) {
               addIfNotConflicting(interfce, valid, proxySignatures);
            }
         }

         return valid;
      }
   }

   private static void addIfNotConflicting(Class interfce, List<Class> valid, List<Signature> proxySignatures) {
      List<Signature> interfaceSigs = getSignatures(interfce);

      for(Signature sig : interfaceSigs) {
         if (proxySignatures.contains(sig)) {
            return;
         }
      }

      valid.add(interfce);
      proxySignatures.addAll(interfaceSigs);
   }

   private static List<Signature> getSignatures(Class mainInterface) {
      List<Signature> sigs = new ArrayList();

      for(Method method : mainInterface.getMethods()) {
         sigs.add(new Signature(mainInterface, method));
      }

      return sigs;
   }

   public static class Signature {
      private final Class clazz;
      private final Method method;
      private final String sig;

      public Signature(Class clazz, Method method) {
         this.clazz = clazz;
         this.method = method;
         StringBuilder sb = new StringBuilder();
         sb.append(method.getName());
         sb.append('(');

         for(Class<?> type : method.getParameterTypes()) {
            sb.append(type.getName());
            sb.append(',');
         }

         sb.append(')');
         this.sig = sb.toString();
      }

      public Method getMethod() {
         return this.method;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Signature signature = (Signature)o;
            if (!this.sig.equals(signature.sig)) {
               return false;
            } else {
               boolean aIsRemote = Remote.class.isAssignableFrom(this.clazz);
               boolean bIsRemote = Remote.class.isAssignableFrom(signature.clazz);
               return aIsRemote != bIsRemote;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.sig.hashCode();
      }

      public String toString() {
         return this.method.toString();
      }
   }
}
