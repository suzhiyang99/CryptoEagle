package org.apache.directory.shared.kerberos.codec.encryptionKey;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class EncryptionKeyContainer extends AbstractContainer {
   private EncryptionKey encryptionKey;

   public EncryptionKeyContainer() {
      this.setGrammar(EncryptionKeyGrammar.getInstance());
      this.setTransition(EncryptionKeyStatesEnum.START_STATE);
   }

   public EncryptionKey getEncryptionKey() {
      return this.encryptionKey;
   }

   public void setEncryptionKey(EncryptionKey encryptionKey) {
      this.encryptionKey = encryptionKey;
   }
}
