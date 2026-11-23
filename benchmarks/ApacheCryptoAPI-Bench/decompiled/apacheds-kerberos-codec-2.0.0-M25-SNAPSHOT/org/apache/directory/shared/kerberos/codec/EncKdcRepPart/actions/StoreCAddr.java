package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddresses;
import org.apache.directory.shared.kerberos.components.HostAddresses;

public class StoreCAddr extends AbstractReadHostAddresses<EncKdcRepPartContainer> {
   public StoreCAddr() {
      super("EncKdcRepPart caddr");
   }

   protected void setHostAddresses(HostAddresses hostAddresses, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setClientAddresses(hostAddresses);
      encKdcRepPartContainer.setGrammarEndAllowed(true);
   }
}
