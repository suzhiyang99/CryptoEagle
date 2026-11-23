package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;

public class StoreRenewTill extends AbstractReadKerberosTime<EncKdcRepPartContainer> {
   public StoreRenewTill() {
      super("EncKdcRepPart renew till");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setRenewTill(krbtime);
   }
}
