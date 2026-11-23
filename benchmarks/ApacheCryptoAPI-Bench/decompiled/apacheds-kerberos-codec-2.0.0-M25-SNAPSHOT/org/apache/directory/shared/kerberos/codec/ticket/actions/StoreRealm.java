package org.apache.directory.shared.kerberos.codec.ticket.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;

public class StoreRealm extends AbstractReadRealm<TicketContainer> {
   public StoreRealm() {
      super("Kerberos Ticket realm value");
   }

   protected void setRealm(String realm, TicketContainer ticketContainer) {
      ticketContainer.getTicket().setRealm(realm);
   }
}
