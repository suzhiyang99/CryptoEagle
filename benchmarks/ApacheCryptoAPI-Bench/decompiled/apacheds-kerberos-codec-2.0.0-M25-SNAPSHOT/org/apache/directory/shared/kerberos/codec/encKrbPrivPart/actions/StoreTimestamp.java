package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;

public class StoreTimestamp extends AbstractReadKerberosTime<EncKrbPrivPartContainer> {
   public StoreTimestamp() {
      super("EncKrbPrivPart timemstamp");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setTimestamp(krbtime);
   }
}
