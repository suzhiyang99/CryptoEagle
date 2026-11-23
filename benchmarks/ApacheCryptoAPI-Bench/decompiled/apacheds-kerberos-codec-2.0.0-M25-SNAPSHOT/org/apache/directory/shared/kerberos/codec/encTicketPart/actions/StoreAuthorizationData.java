package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadAuthorizationData;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;

public class StoreAuthorizationData extends AbstractReadAuthorizationData<EncTicketPartContainer> {
   public StoreAuthorizationData() {
      super("EncTicketPart authorization-data");
   }

   protected void setAuthorizationData(AuthorizationData authorizationData, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setAuthorizationData(authorizationData);
      encTicketPartContainer.setGrammarEndAllowed(true);
   }
}
