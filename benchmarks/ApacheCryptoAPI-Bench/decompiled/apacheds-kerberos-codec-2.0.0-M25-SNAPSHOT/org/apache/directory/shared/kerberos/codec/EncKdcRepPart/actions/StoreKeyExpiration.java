package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;

public class StoreKeyExpiration extends AbstractReadKerberosTime<EncKdcRepPartContainer> {
   public StoreKeyExpiration() {
      super("EncKdcRepPart key expiration");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setKeyExpiration(krbtime);
   }
}
