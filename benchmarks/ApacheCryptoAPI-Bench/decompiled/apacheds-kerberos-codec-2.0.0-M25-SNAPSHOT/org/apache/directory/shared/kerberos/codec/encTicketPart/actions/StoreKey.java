package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptionKey;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class StoreKey extends AbstractReadEncryptionKey<EncTicketPartContainer> {
   public StoreKey() {
      super("EncTicketPart key");
   }

   protected void setEncryptionKey(EncryptionKey encryptionKey, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setKey(encryptionKey);
   }
}
