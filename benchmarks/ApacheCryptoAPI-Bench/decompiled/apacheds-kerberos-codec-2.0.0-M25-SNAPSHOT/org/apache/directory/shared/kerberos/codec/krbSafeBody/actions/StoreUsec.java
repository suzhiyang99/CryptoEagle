package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;

public class StoreUsec extends AbstractReadInteger<KrbSafeBodyContainer> {
   public StoreUsec() {
      super("KRB-SAFE-BODY usec", 0, 999999);
   }

   protected void setIntegerValue(int value, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setUsec(value);
   }
}
