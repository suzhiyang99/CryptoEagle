package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;

public class StoreRTime extends AbstractReadKerberosTime<KdcReqBodyContainer> {
   public StoreRTime() {
      super("Stores the RTime");
   }

   protected void setKerberosTime(KerberosTime krbtime, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setRtime(krbtime);
   }
}
