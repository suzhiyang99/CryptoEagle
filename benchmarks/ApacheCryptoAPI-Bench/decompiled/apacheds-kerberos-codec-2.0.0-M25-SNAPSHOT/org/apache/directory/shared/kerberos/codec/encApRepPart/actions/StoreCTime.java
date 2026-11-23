package org.apache.directory.shared.kerberos.codec.encApRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;

public class StoreCTime extends AbstractReadKerberosTime<EncApRepPartContainer> {
   public StoreCTime() {
      super("Stores the CTime");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncApRepPartContainer encApRepPartContainer) {
      encApRepPartContainer.getEncApRepPart().setCTime(krbtime);
   }
}
