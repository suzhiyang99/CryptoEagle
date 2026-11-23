package org.apache.directory.shared.kerberos.codec.krbCred.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;

public class StorePvno extends AbstractReadPvno<KrbCredContainer> {
   public StorePvno() {
      super("KRB-CRED pvno");
   }

   protected void setPvno(int pvno, KrbCredContainer krbCredContainer) {
      krbCredContainer.getKrbCred().setProtocolVersionNumber(pvno);
   }
}
