package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StorePvno extends AbstractReadPvno<KrbErrorContainer> {
   public StorePvno() {
      super("KRB-ERROR pvno");
   }

   protected void setPvno(int pvno, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setProtocolVersionNumber(pvno);
   }
}
