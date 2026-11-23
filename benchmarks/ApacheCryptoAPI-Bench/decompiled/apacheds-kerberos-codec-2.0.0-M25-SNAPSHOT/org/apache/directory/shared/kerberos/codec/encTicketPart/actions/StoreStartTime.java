package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;

public class StoreStartTime extends AbstractReadKerberosTime<EncTicketPartContainer> {
   public StoreStartTime() {
      super("Stores the starttime");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setStartTime(krbtime);
   }
}
