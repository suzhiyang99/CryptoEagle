package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreCName extends AbstractReadPrincipalName<KdcReqBodyContainer> {
   public StoreCName() {
      super("Stores the CName");
   }

   protected void setPrincipalName(PrincipalName principalName, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setCName(principalName);
   }
}
