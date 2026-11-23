package org.apache.directory.shared.kerberos.codec.paEncTimestamp;

import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.components.PaEncTimestamp;

public class PaEncTimestampContainer extends EncryptedDataContainer {
   public PaEncTimestampContainer() {
      this.setEncryptedData(new PaEncTimestamp());
   }

   public PaEncTimestamp getPaEncTimestamp() {
      return (PaEncTimestamp)this.getEncryptedData();
   }
}
