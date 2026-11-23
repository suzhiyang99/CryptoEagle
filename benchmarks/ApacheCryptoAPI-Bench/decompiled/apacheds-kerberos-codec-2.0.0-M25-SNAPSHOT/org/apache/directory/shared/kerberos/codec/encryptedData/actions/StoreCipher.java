package org.apache.directory.shared.kerberos.codec.encryptedData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;

public class StoreCipher extends AbstractReadOctetString<EncryptedDataContainer> {
   public StoreCipher() {
      super("EncryptedPart cipher");
   }

   protected void setOctetString(byte[] data, EncryptedDataContainer encryptedDataContainer) {
      encryptedDataContainer.getEncryptedData().setCipher(data);
      encryptedDataContainer.setGrammarEndAllowed(true);
   }
}
