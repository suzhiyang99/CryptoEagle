package org.apache.directory.shared.kerberos.codec.apRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;

public class StorePvno extends AbstractReadPvno<ApRepContainer> {
   public StorePvno() {
      super("StorePvno value");
   }

   protected void setPvno(int pvno, ApRepContainer apRepContainer) {
      apRepContainer.getApRep().setProtocolVersionNumber(pvno);
   }
}
