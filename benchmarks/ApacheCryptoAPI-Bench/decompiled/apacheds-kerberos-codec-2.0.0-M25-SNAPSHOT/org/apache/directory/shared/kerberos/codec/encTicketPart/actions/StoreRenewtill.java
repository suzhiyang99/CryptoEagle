package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;

public class StoreRenewtill extends AbstractReadKerberosTime<EncTicketPartContainer> {
   public StoreRenewtill() {
      super("Stores the renew-till");
   }

   protected void setKerberosTime(KerberosTime krbtime, EncTicketPartContainer encTicketPartContainer) {
      encTicketPartContainer.getEncTicketPart().setRenewTill(krbtime);
      encTicketPartContainer.setGrammarEndAllowed(true);
   }
}
