package org.apache.directory.shared.kerberos.codec.krbPriv.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncPart extends AbstractReadEncryptedPart<KrbPrivContainer> {
   public StoreEncPart() {
      super("KRB-PRIV enc-part");
   }

   protected void setEncryptedData(EncryptedData encryptedData, KrbPrivContainer krbPrivContainer) {
      krbPrivContainer.getKrbPriv().setEncPart(encryptedData);
      krbPrivContainer.setGrammarEndAllowed(true);
   }
}
