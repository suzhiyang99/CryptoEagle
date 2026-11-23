package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptionKey;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class StoreKey extends AbstractReadEncryptionKey<KrbCredInfoContainer> {
   public StoreKey() {
      super("KrbCredInfo key");
   }

   protected void setEncryptionKey(EncryptionKey encryptionKey, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setKey(encryptionKey);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
