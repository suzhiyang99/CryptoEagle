package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreCName extends AbstractReadPrincipalName<EncTicketPartContainer> {
   public StoreCName() {
      super("EncTicketPart cname");
   }

   protected void setPrincipalName(PrincipalName principalName, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setCName(principalName);
   }
}
