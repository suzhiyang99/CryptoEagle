package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;

public class StoreSeqNumber extends AbstractReadInteger<KrbSafeBodyContainer> {
   public StoreSeqNumber() {
      super("KRB-SAFE-BODY seq-number", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setSeqNumber(value);
   }
}
