package org.apache.directory.shared.kerberos.codec.adKdcIssued.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.AdKdcIssuedContainer;

public class StoreIRealm extends AbstractReadRealm<AdKdcIssuedContainer> {
   public StoreIRealm() {
      super("Authenticator i-realm value");
   }

   protected void setRealm(String realm, AdKdcIssuedContainer adKdcIssuedContainer) {
      adKdcIssuedContainer.getAdKdcIssued().setIRealm(realm);
   }
}
