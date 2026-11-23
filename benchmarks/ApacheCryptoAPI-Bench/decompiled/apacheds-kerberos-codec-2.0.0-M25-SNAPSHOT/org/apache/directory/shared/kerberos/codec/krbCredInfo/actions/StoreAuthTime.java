package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StoreAuthTime extends AbstractReadKerberosTime<KrbCredInfoContainer> {
   public StoreAuthTime() {
      super("KrbCredInfo authtime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setAuthTime(krbtime);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
