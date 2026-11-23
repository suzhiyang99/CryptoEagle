package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;

public class StoreCRealm extends AbstractReadRealm<EncTicketPartContainer> {
   public StoreCRealm() {
      super("EncTicketPart crealm value");
   }

   protected void setRealm(String realm, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setCRealm(realm);
   }
}
