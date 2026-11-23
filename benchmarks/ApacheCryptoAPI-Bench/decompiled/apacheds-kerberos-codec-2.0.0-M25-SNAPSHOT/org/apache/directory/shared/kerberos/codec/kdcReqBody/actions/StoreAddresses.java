package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddresses;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.HostAddresses;

public class StoreAddresses extends AbstractReadHostAddresses<KdcReqBodyContainer> {
   public StoreAddresses() {
      super("KDC-REQ-BODY addresses");
   }

   protected void setHostAddresses(HostAddresses hostAddresses, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setAddresses(hostAddresses);
      kdcReqBodyContainer.setGrammarEndAllowed(true);
   }
}
