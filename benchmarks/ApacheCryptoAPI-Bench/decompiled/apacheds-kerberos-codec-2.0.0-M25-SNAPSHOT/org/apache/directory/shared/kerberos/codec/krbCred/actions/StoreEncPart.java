package org.apache.directory.shared.kerberos.codec.krbCred.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncPart extends AbstractReadEncryptedPart<KrbCredContainer> {
   public StoreEncPart() {
      super("KRB-CRED enc-part");
   }

   protected void setEncryptedData(EncryptedData encryptedData, KrbCredContainer krbCredContainer) {
      krbCredContainer.getKrbCred().setEncPart(encryptedData);
      krbCredContainer.setGrammarEndAllowed(true);
   }
}
