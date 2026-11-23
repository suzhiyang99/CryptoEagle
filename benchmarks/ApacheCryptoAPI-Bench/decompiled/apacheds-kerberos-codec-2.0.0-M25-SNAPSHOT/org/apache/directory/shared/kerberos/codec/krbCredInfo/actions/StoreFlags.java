package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.api.asn1.actions.AbstractReadBitString;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.flags.TicketFlags;

public class StoreFlags extends AbstractReadBitString<KrbCredInfoContainer> {
   public StoreFlags() {
      super("KrbCredInfo flags");
   }

   protected void setBitString(byte[] data, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setTicketFlags(new TicketFlags(data));
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
