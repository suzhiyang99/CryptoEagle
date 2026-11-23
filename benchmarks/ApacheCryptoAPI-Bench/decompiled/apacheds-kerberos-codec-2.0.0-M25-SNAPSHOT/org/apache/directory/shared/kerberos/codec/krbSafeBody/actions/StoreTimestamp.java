package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;

public class StoreTimestamp extends AbstractReadKerberosTime<KrbSafeBodyContainer> {
   public StoreTimestamp() {
      super("KRB-SAFE-BODY timemstamp");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setTimestamp(krbtime);
   }
}
