package org.apache.directory.shared.kerberos.codec.encApRepPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptionKey;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class StoreSubKey extends AbstractReadEncryptionKey<EncApRepPartContainer> {
   public StoreSubKey() {
      super("EncApRepPart subkey");
   }

   protected void setEncryptionKey(EncryptionKey encryptionKey, EncApRepPartContainer encApRepPartContainer) {
      encApRepPartContainer.getEncApRepPart().setSubkey(encryptionKey);
      encApRepPartContainer.setGrammarEndAllowed(true);
   }
}
