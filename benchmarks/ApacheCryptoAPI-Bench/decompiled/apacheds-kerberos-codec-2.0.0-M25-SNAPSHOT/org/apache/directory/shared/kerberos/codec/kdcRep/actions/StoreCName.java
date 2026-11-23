package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreCName extends AbstractReadPrincipalName<KdcRepContainer> {
   public StoreCName() {
      super("Stores the CName");
   }

   protected void setPrincipalName(PrincipalName principalName, KdcRepContainer kdcRepContainer) {
      kdcRepContainer.getKdcRep().setCName(principalName);
   }
}
