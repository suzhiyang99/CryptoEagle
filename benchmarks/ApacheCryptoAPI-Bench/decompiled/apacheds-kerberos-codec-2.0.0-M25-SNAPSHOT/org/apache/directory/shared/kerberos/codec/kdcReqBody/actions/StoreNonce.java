package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;

public class StoreNonce extends AbstractReadInteger<KdcReqBodyContainer> {
   public StoreNonce() {
      super("KDC-REQ-BODY nonce", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   public void setIntegerValue(int value, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setNonce(value);
   }
}
