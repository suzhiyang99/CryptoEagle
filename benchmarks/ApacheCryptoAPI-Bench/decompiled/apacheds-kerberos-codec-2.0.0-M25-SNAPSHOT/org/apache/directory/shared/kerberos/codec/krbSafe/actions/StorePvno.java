package org.apache.directory.shared.kerberos.codec.krbSafe.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;

public class StorePvno extends AbstractReadPvno<KrbSafeContainer> {
   public StorePvno() {
      super("KRB-SAFE pvno");
   }

   protected void setPvno(int pvno, KrbSafeContainer krbSafeContainer) {
      krbSafeContainer.getKrbSafe().setProtocolVersionNumber(pvno);
   }
}
