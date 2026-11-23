package org.apache.directory.shared.kerberos.codec.authorizationData;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;

public class AuthorizationDataContainer extends AbstractContainer {
   private AuthorizationData authorizationData;

   public AuthorizationDataContainer() {
      this.setGrammar(AuthorizationDataGrammar.getInstance());
      this.setTransition(AuthorizationDataStatesEnum.START_STATE);
   }

   public AuthorizationData getAuthorizationData() {
      return this.authorizationData;
   }

   public void setAuthorizationData(AuthorizationData authorizationData) {
      this.authorizationData = authorizationData;
   }
}
