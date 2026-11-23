package org.apache.directory.shared.kerberos.codec.adAndOr.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadAuthorizationData;
import org.apache.directory.shared.kerberos.codec.adAndOr.AdAndOrContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;

public class StoreElements extends AbstractReadAuthorizationData<AdAndOrContainer> {
   public StoreElements() {
      super("AdAndOr 'elements' value");
   }

   protected void setAuthorizationData(AuthorizationData authorizationData, AdAndOrContainer adAndOrContainer) {
      adAndOrContainer.getAdAndOr().setElements(authorizationData);
      adAndOrContainer.setGrammarEndAllowed(true);
   }
}
