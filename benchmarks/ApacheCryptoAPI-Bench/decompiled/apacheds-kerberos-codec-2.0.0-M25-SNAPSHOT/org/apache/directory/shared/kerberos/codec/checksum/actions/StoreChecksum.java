package org.apache.directory.shared.kerberos.codec.checksum.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.checksum.ChecksumContainer;

public class StoreChecksum extends AbstractReadOctetString<ChecksumContainer> {
   public StoreChecksum() {
      super("Checksum's 'checksum' value");
   }

   protected void setOctetString(byte[] data, ChecksumContainer checksumContainer) {
      checksumContainer.getChecksum().setChecksumValue(data);
      checksumContainer.setGrammarEndAllowed(true);
   }
}
