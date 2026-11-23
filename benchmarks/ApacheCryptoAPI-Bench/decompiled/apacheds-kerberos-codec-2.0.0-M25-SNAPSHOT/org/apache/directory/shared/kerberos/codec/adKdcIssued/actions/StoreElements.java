package org.apache.directory.shared.kerberos.codec.adKdcIssued.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadAuthorizationData;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.AdKdcIssuedContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;

public class StoreElements extends AbstractReadAuthorizationData<AdKdcIssuedContainer> {
   public StoreElements() {
      super("AdKdcIssued 'elements' value");
   }

   protected void setAuthorizationData(AuthorizationData authorizationData, AdKdcIssuedContainer adKdcIssuedContainer) {
      adKdcIssuedContainer.getAdKdcIssued().setElements(authorizationData);
      adKdcIssuedContainer.setGrammarEndAllowed(true);
   }
}
