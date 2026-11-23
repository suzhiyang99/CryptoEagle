package org.apache.directory.shared.kerberos.codec.hostAddress;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;

public class HostAddressContainer extends AbstractContainer {
   private HostAddress hostAddress;

   public HostAddressContainer() {
      this.setGrammar(HostAddressGrammar.getInstance());
      this.setTransition(HostAddressStatesEnum.START_STATE);
   }

   public HostAddress getHostAddress() {
      return this.hostAddress;
   }

   public void setHostAddress(HostAddress hostAddress) {
      this.hostAddress = hostAddress;
   }
}
