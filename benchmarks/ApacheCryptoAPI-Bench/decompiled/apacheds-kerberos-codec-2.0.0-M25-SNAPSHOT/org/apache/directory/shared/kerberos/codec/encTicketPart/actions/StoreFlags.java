package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadBitString;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.flags.TicketFlags;

public class StoreFlags extends AbstractReadBitString<EncTicketPartContainer> {
   public StoreFlags() {
      super("Store EncTicketPart's flags");
   }

   protected void setBitString(byte[] data, EncTicketPartContainer encTicketPartContainer) {
      TicketFlags flags = new TicketFlags(data);
      encTicketPartContainer.getEncTicketPart().setFlags(flags);
   }
}
