package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreRecipientAddress extends AbstractReadHostAddress<KrbSafeBodyContainer> {
   public StoreRecipientAddress() {
      super("KRB-SAFE-BODY r-address");
   }

   protected void setAddress(HostAddress hostAddress, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setRecipientAddress(hostAddress);
      krbSafeBodyContainer.setGrammarEndAllowed(true);
   }
}
