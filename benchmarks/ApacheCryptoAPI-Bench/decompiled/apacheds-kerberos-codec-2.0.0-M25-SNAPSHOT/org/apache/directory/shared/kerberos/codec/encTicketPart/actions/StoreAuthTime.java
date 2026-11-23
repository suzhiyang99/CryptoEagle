package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;

public class StoreAuthTime extends AbstractReadKerberosTime<EncTicketPartContainer> {
   public StoreAuthTime() {
      super("Stores the authtime");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setAuthTime(krbtime);
   }
}
