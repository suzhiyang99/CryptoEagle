package org.apache.openejb.core.ivm.naming;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.naming.Context;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;

public class JndiEncArtifact implements Serializable {
   String path = new String();

   public JndiEncArtifact(IvmContext context) {
      NameNode node = context.mynode;

      do {
         this.path = node.getAtomicName() + "/" + this.path;
         node = node.getParent();
      } while(node != null);

   }

   public Object readResolve() throws ObjectStreamException {
      ThreadContext thrdCntx = ThreadContext.getThreadContext();
      BeanContext deployment = thrdCntx.getBeanContext();
      Context cntx = deployment.getJndiEnc();

      try {
         Object obj = cntx.lookup(this.path);
         if (obj == null) {
            throw new InvalidObjectException("JNDI ENC context reference could not be properly resolved when bean instance was activated");
         } else {
            return obj;
         }
      } catch (javax.naming.NamingException e) {
         throw (InvalidObjectException)(new InvalidObjectException("JNDI ENC context reference could not be properly resolved due to a JNDI exception, when bean instance was activated")).initCause(e);
      }
   }
}
