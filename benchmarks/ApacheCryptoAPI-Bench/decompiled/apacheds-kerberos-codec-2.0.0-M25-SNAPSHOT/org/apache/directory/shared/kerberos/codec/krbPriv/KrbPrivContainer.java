package org.apache.directory.shared.kerberos.codec.krbPriv;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.KrbPriv;

public class KrbPrivContainer extends AbstractContainer {
   private KrbPriv krbPriv;

   public KrbPrivContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KrbPrivGrammar.getInstance());
      this.setTransition(KrbPrivStatesEnum.START_STATE);
   }

   public KrbPriv getKrbPriv() {
      return this.krbPriv;
   }

   public void setKrbPriv(KrbPriv krbPriv) {
      this.krbPriv = krbPriv;
   }
}
