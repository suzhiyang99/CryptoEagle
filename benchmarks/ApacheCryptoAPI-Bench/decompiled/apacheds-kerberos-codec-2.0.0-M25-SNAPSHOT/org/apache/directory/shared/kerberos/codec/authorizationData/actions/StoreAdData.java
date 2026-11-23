package org.apache.directory.shared.kerberos.codec.authorizationData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;

public class StoreAdData extends AbstractReadOctetString<AuthorizationDataContainer> {
   public StoreAdData() {
      super("AuthorizationData ad-data");
   }

   protected void setOctetString(byte[] data, AuthorizationDataContainer authorizationDataContainer) {
      authorizationDataContainer.getAuthorizationData().setCurrentAdData(data);
      authorizationDataContainer.setGrammarEndAllowed(true);
   }
}
