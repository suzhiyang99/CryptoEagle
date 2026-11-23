package org.apache.directory.shared.kerberos.codec.apReq.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreAuthenticator extends AbstractReadEncryptedPart<ApReqContainer> {
   public StoreAuthenticator() {
      super("Encrypted Authenticator");
   }

   protected void setEncryptedData(EncryptedData encryptedData, ApReqContainer apReqContainer) {
      apReqContainer.getApReq().setAuthenticator(encryptedData);
      apReqContainer.setGrammarEndAllowed(true);
   }
}
