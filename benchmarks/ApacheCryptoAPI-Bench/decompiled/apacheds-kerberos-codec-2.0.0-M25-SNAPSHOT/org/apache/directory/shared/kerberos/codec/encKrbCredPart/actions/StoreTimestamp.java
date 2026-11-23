package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;

public class StoreTimestamp extends AbstractReadKerberosTime<EncKrbCredPartContainer> {
   public StoreTimestamp() {
      super("EncKrbCredPart timemstamp");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKrbCredPartContainer encKrbCredPartContainer) {
      encKrbCredPartContainer.getEncKrbCredPart().setTimestamp(krbtime);
      encKrbCredPartContainer.setGrammarEndAllowed(true);
   }
}
