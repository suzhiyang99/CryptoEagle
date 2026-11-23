package org.apache.directory.shared.kerberos.codec.ticket;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class TicketContainer extends AbstractContainer {
   private Ticket ticket;

   public TicketContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(TicketGrammar.getInstance());
      this.setTransition(TicketStatesEnum.START_STATE);
   }

   public Ticket getTicket() {
      return this.ticket;
   }

   public void setTicket(Ticket ticket) {
      this.ticket = ticket;
   }
}
