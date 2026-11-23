package org.apache.directory.shared.kerberos.codec.ticket.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;

public class StoreTktVno extends AbstractReadInteger<TicketContainer> {
   public StoreTktVno() {
      super("Kerberos Ticket tktvno value", 5, 5);
   }

   protected void setIntegerValue(int value, TicketContainer ticketContainer) {
      ticketContainer.getTicket().setTktVno(value);
   }
}
