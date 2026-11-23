package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreRecipientAddress extends AbstractReadHostAddress<EncKrbPrivPartContainer> {
   public StoreRecipientAddress() {
      super("EncKrbPrivPart r-address");
   }

   protected void setAddress(HostAddress hostAddress, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setRecipientAddress(hostAddress);
      encKrbPrivPartContainer.setGrammarEndAllowed(true);
   }
}
