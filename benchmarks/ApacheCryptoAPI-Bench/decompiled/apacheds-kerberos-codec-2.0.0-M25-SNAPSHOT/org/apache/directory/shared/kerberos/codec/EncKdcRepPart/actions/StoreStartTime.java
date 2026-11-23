package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;

public class StoreStartTime extends AbstractReadKerberosTime<EncKdcRepPartContainer> {
   public StoreStartTime() {
      super("EncKdcRepPart start time");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setStartTime(krbtime);
   }
}
