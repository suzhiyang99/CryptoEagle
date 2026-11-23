package org.apache.directory.shared.kerberos.codec.adKdcIssued.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.AdKdcIssuedContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreISName extends AbstractReadPrincipalName<AdKdcIssuedContainer> {
   public StoreISName() {
      super("AD-KDCIssued i-sname");
   }

   protected void setPrincipalName(PrincipalName principalName, AdKdcIssuedContainer adKdcIssuedContainer) {
      adKdcIssuedContainer.getAdKdcIssued().setISName(principalName);
   }
}
