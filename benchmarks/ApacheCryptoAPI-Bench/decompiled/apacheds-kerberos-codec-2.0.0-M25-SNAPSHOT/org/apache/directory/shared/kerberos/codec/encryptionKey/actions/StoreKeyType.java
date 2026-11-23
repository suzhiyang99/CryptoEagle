package org.apache.directory.shared.kerberos.codec.encryptionKey.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encryptionKey.EncryptionKeyContainer;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKeyType extends AbstractReadInteger<EncryptionKeyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKeyType.class);
   private static final boolean IS_DEBUG;

   public StoreKeyType() {
      super("EncryptionKey's keytype");
   }

   protected void setIntegerValue(int value, EncryptionKeyContainer encryptionKeyContainer) {
      EncryptionKey encKey = encryptionKeyContainer.getEncryptionKey();
      EncryptionType encryptionType = EncryptionType.getTypeByValue(value);
      encKey.setKeyType(encryptionType);
      if (IS_DEBUG) {
         LOG.debug("keytype : {}", encryptionType);
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
