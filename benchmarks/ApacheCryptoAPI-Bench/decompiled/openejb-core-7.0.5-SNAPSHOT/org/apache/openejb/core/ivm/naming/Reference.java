package org.apache.openejb.core.ivm.naming;

import org.apache.xbean.naming.reference.SimpleReference;

public abstract class Reference extends SimpleReference {
   public Object getContent() throws javax.naming.NamingException {
      return this.getObject();
   }

   public abstract Object getObject() throws javax.naming.NamingException;
}
