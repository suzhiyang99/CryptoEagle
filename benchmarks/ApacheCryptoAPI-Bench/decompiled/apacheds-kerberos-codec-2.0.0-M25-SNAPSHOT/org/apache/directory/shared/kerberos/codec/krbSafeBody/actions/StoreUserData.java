package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;

public class StoreUserData extends AbstractReadOctetString<KrbSafeBodyContainer> {
   public StoreUserData() {
      super("KRB-SAFE-BODY user-data", true);
   }

   protected void setOctetString(byte[] data, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setUserData(data);
   }
}
