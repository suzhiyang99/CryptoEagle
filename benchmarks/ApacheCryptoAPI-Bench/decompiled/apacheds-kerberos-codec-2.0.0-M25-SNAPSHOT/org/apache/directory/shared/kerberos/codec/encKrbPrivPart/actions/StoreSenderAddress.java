package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreSenderAddress extends AbstractReadHostAddress<EncKrbPrivPartContainer> {
   public StoreSenderAddress() {
      super("EncKrbPrivPart s-address");
   }

   protected void setAddress(HostAddress hostAddress, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setSenderAddress(hostAddress);
      encKrbPrivPartContainer.setGrammarEndAllowed(true);
   }
}
