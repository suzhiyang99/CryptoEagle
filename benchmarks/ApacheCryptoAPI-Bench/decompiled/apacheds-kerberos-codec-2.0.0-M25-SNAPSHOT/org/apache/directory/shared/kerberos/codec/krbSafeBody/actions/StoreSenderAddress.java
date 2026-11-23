package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreSenderAddress extends AbstractReadHostAddress<KrbSafeBodyContainer> {
   public StoreSenderAddress() {
      super("KRB-SAFE-BODY s-address");
   }

   protected void setAddress(HostAddress hostAddress, KrbSafeBodyContainer krbSafeBodyContainer) {
      krbSafeBodyContainer.getKrbSafeBody().setSenderAddress(hostAddress);
      krbSafeBodyContainer.setGrammarEndAllowed(true);
   }
}
