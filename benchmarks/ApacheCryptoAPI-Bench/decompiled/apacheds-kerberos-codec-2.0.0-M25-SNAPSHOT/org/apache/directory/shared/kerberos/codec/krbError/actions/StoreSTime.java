package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreSTime extends AbstractReadKerberosTime<KrbErrorContainer> {
   public StoreSTime() {
      super("Stores the STime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setSTime(krbtime);
   }
}
