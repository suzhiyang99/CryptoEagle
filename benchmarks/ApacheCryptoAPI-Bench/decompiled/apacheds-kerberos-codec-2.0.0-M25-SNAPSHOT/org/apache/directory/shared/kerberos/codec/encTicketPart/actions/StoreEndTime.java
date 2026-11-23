package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;

public class StoreEndTime extends AbstractReadKerberosTime<EncTicketPartContainer> {
   public StoreEndTime() {
      super("Stores the endtime");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setEndTime(krbtime);
      encTicketPartContainer.setGrammarEndAllowed(true);
   }
}
