package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreSName extends AbstractReadPrincipalName<KdcReqBodyContainer> {
   public StoreSName() {
      super("Stores the SName");
   }

   protected void setPrincipalName(PrincipalName principalName, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setSName(principalName);
   }
}
