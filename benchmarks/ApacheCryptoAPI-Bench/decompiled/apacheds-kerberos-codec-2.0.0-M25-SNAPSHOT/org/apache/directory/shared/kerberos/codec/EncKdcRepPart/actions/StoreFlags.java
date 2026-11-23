package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadBitString;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;
import org.apache.directory.shared.kerberos.flags.TicketFlags;

public class StoreFlags extends AbstractReadBitString<EncKdcRepPartContainer> {
   public StoreFlags() {
      super("Stores the flags");
   }

   protected void setBitString(byte[] data, EncKdcRepPartContainer encKdcRepPartContainer) {
      EncKdcRepPart encKdcRepPart = encKdcRepPartContainer.getEncKdcRepPart();
      TicketFlags flags = new TicketFlags(data);
      encKdcRepPart.setFlags(flags);
   }
}
