package org.apache.directory.shared.kerberos.codec.checksum;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.Checksum;

public class ChecksumContainer extends AbstractContainer {
   private Checksum checksum;

   public ChecksumContainer() {
      this.setGrammar(ChecksumGrammar.getInstance());
      this.setTransition(ChecksumStatesEnum.START_STATE);
   }

   public Checksum getChecksum() {
      return this.checksum;
   }

   public void setChecksum(Checksum checksum) {
      this.checksum = checksum;
   }
}
