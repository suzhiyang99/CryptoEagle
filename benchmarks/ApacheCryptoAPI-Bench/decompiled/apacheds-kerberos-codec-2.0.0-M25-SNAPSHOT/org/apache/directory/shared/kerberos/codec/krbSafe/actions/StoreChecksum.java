package org.apache.directory.shared.kerberos.codec.krbSafe.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadCheckSum;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;
import org.apache.directory.shared.kerberos.components.Checksum;

public class StoreChecksum extends AbstractReadCheckSum<KrbSafeContainer> {
   public StoreChecksum() {
      super("KRB-SAFE cksum");
   }

   protected void setChecksum(Checksum checksum, KrbSafeContainer krbSafeContainer) {
      krbSafeContainer.getKrbSafe().setChecksum(checksum);
      krbSafeContainer.setGrammarEndAllowed(true);
   }
}
