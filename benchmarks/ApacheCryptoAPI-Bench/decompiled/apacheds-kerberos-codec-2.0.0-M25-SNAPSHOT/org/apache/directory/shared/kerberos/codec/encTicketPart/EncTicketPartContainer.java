package org.apache.directory.shared.kerberos.codec.encTicketPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncTicketPart;

public class EncTicketPartContainer extends AbstractContainer {
   private EncTicketPart encTicketPart = new EncTicketPart();

   public EncTicketPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncTicketPartGrammar.getInstance());
      this.setTransition(EncTicketPartStatesEnum.START_STATE);
   }

   public EncTicketPart getEncTicketPart() {
      return this.encTicketPart;
   }

   public void setEncTicketPart(EncTicketPart encTicketPart) {
      this.encTicketPart = encTicketPart;
   }
}
