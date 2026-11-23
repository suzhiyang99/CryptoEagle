package org.apache.openejb.core.ivm.naming;

public class ObjectReference extends Reference {
   private final Object obj;

   public ObjectReference(Object obj) {
      this.obj = obj;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.obj;
   }
}
