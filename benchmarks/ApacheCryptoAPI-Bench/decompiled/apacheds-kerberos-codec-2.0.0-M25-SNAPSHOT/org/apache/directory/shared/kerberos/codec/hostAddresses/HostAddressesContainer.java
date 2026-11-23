package org.apache.directory.shared.kerberos.codec.hostAddresses;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.apache.directory.shared.kerberos.components.HostAddresses;

public class HostAddressesContainer extends AbstractContainer {
   private HostAddresses hostAddresses = new HostAddresses();

   public HostAddressesContainer() {
      this.setGrammar(HostAddressesGrammar.getInstance());
      this.setTransition(HostAddressesStatesEnum.START_STATE);
   }

   public HostAddresses getHostAddresses() {
      return this.hostAddresses;
   }

   public void addHostAddress(HostAddress hostAddress) {
      this.hostAddresses.addHostAddress(hostAddress);
   }
}
