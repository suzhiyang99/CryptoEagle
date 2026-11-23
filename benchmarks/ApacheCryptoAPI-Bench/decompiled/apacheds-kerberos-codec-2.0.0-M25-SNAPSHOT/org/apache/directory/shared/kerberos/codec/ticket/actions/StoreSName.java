package org.apache.directory.shared.kerberos.codec.ticket.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreSName extends AbstractReadPrincipalName<TicketContainer> {
   public StoreSName() {
      super("Kerberos Ticket principalName");
   }

   protected void setPrincipalName(PrincipalName principalName, TicketContainer ticketContainer) {
      ticketContainer.getTicket().setSName(principalName);
   }
}
