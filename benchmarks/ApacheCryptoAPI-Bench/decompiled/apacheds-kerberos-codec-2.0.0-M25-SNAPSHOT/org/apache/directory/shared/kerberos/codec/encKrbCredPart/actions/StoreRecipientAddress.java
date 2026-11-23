package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddress;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class StoreRecipientAddress extends AbstractReadHostAddress<EncKrbCredPartContainer> {
   public StoreRecipientAddress() {
      super("EncKrbCredPart r-address");
   }

   protected void setAddress(HostAddress hostAddress, EncKrbCredPartContainer encKrbCredPartContainer) {
      encKrbCredPartContainer.getEncKrbCredPart().setRecipientAddress(hostAddress);
      encKrbCredPartContainer.setGrammarEndAllowed(true);
   }
}
