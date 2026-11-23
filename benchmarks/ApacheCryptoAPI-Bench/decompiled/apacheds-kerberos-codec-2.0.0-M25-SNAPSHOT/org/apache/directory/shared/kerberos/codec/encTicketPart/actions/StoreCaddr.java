package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddresses;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.components.HostAddresses;

public class StoreCaddr extends AbstractReadHostAddresses<EncTicketPartContainer> {
   public StoreCaddr() {
      super("EncTicketPart caddr");
   }

   protected void setHostAddresses(HostAddresses hostAddresses, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setClientAddresses(hostAddresses);
      encTicketPartContainer.setGrammarEndAllowed(true);
   }
}
