package org.apache.directory.shared.kerberos.codec.ticket.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncPart extends AbstractReadEncryptedPart<TicketContainer> {
   public StoreEncPart() {
      super("Kerberos Ticket EncodedPart");
   }

   protected void setEncryptedData(EncryptedData encryptedData, TicketContainer ticketContainer) {
      ticketContainer.getTicket().setEncPart(encryptedData);
      ticketContainer.setGrammarEndAllowed(true);
   }
}
