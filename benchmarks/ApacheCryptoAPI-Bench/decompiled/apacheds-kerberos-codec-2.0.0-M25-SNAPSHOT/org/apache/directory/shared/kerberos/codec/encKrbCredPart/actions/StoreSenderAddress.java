package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreSenderAddress extends AbstractReadHostAddress<EncKrbCredPartContainer> {
   public StoreSenderAddress() {
      super("EncKrbCredPart s-address");
   }

   protected void setAddress(HostAddress hostAddress, EncKrbCredPartContainer encKrbCredPartContainer) {
      encKrbCredPartContainer.getEncKrbCredPart().setSenderAddress(hostAddress);
      encKrbCredPartContainer.setGrammarEndAllowed(true);
   }
}
