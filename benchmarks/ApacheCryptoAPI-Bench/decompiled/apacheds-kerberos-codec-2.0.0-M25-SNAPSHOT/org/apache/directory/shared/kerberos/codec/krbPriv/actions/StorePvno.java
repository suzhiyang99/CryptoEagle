package org.apache.directory.shared.kerberos.codec.krbPriv.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;

public class StorePvno extends AbstractReadPvno<KrbPrivContainer> {
   public StorePvno() {
      super("KRB-PRIV pvno");
   }

   protected void setPvno(int pvno, KrbPrivContainer krbPrivContainer) {
      krbPrivContainer.getKrbPriv().setProtocolVersionNumber(pvno);
   }
}
