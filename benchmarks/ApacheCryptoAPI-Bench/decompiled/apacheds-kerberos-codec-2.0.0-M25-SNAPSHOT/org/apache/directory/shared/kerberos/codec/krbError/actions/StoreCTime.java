package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreCTime extends AbstractReadKerberosTime<KrbErrorContainer> {
   public StoreCTime() {
      super("Stores the CTime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setCTime(krbtime);
   }
}
