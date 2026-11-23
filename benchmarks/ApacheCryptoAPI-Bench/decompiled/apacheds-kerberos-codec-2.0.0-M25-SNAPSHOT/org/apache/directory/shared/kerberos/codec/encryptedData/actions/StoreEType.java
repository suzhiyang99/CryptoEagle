package org.apache.directory.shared.kerberos.codec.encryptedData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreEType extends AbstractReadInteger<EncryptedDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEType.class);
   private static final boolean IS_DEBUG;

   public StoreEType() {
      super("EncryptedPart Etype");
   }

   protected void setIntegerValue(int value, EncryptedDataContainer encryptedDataContainer) {
      EncryptionType encryptionType = EncryptionType.getTypeByValue(value);
      EncryptedData encryptedData = encryptedDataContainer.getEncryptedData();
      encryptedData.setEType(encryptionType);
      if (IS_DEBUG) {
         LOG.debug("e-type : {}", encryptionType);
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
