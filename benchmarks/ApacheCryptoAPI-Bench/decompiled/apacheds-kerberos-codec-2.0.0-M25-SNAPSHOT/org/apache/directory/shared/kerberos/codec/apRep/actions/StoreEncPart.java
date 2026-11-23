package org.apache.directory.shared.kerberos.codec.apRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncPart extends AbstractReadEncryptedPart<ApRepContainer> {
   public StoreEncPart() {
      super("AP-REP enc-part");
   }

   protected void setEncryptedData(EncryptedData encryptedData, ApRepContainer apRepContainer) {
      apRepContainer.getApRep().setEncPart(encryptedData);
      apRepContainer.setGrammarEndAllowed(true);
   }
}
