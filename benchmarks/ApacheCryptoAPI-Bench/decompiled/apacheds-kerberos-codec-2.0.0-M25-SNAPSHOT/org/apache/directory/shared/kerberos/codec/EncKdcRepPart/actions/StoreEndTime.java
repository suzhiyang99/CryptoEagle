package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;

public class StoreEndTime extends AbstractReadKerberosTime<EncKdcRepPartContainer> {
   public StoreEndTime() {
      super("EncKdcRepPart end time");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setEndTime(krbtime);
   }
}
