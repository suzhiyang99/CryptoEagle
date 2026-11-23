package org.apache.directory.shared.kerberos.codec.encryptionKey.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.encryptionKey.EncryptionKeyContainer;

public class StoreKeyValue extends AbstractReadOctetString<EncryptionKeyContainer> {
   public StoreKeyValue() {
      super("EncryptionKey's keyvalue");
   }

   protected void setOctetString(byte[] data, EncryptionKeyContainer encryptionKeyContainer) {
      encryptionKeyContainer.getEncryptionKey().setKeyValue(data);
      encryptionKeyContainer.setGrammarEndAllowed(true);
   }
}
