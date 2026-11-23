package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StoreRenewtill extends AbstractReadKerberosTime<KrbCredInfoContainer> {
   public StoreRenewtill() {
      super("KrbCredInfo renewtill");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setRenewtill(krbtime);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
