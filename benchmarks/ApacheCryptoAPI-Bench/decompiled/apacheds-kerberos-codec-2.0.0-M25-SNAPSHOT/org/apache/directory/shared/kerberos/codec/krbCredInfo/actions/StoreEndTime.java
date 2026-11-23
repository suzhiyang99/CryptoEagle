package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StoreEndTime extends AbstractReadKerberosTime<KrbCredInfoContainer> {
   public StoreEndTime() {
      super("KrbCredInfo endtime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setEndTime(krbtime);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
