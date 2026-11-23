package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptionKey;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class StoreKey extends AbstractReadEncryptionKey<EncKdcRepPartContainer> {
   public StoreKey() {
      super("EncKdcRepPart key");
   }

   protected void setEncryptionKey(EncryptionKey encryptionKey, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setKey(encryptionKey);
   }
}
