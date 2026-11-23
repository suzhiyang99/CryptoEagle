package org.apache.directory.shared.kerberos.codec.encryptedData;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class EncryptedDataContainer extends AbstractContainer {
   private EncryptedData encryptedData;

   public EncryptedDataContainer() {
      this.setGrammar(EncryptedDataGrammar.getInstance());
      this.setTransition(EncryptedDataStatesEnum.START_STATE);
   }

   public EncryptedData getEncryptedData() {
      return this.encryptedData;
   }

   public void setEncryptedData(EncryptedData encryptedData) {
      this.encryptedData = encryptedData;
   }
}
