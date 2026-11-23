package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StoreStartTime extends AbstractReadKerberosTime<KrbCredInfoContainer> {
   public StoreStartTime() {
      super("KrbCredInfo starttime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setStartTime(krbtime);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
