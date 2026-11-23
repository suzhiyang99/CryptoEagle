package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;

public class StoreAuthTime extends AbstractReadKerberosTime<EncKdcRepPartContainer> {
   public StoreAuthTime() {
      super("EncKdcRepPart auth time");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setAuthTime(krbtime);
   }
}
