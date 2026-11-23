package org.apache.directory.shared.kerberos.codec.encryptedData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreKvno extends AbstractReadInteger<EncryptedDataContainer> {
   public StoreKvno() {
      super("EncryptedPart kvno", 0, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, EncryptedDataContainer encryptedDataContainer) {
      EncryptedData encryptedData = encryptedDataContainer.getEncryptedData();
      encryptedData.setKvno(value);
   }
}
