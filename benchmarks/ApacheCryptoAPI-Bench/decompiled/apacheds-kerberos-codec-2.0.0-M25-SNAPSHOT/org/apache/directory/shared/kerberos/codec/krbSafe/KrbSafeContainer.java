package org.apache.directory.shared.kerberos.codec.krbSafe;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.KrbSafe;

public class KrbSafeContainer extends AbstractContainer {
   private KrbSafe krbSafe;

   public KrbSafeContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KrbSafeGrammar.getInstance());
      this.setTransition(KrbSafeStatesEnum.START_STATE);
   }

   public KrbSafe getKrbSafe() {
      return this.krbSafe;
   }

   public void setKrbSafe(KrbSafe krbSafe) {
      this.krbSafe = krbSafe;
   }
}
