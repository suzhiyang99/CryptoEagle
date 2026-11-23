package org.apache.directory.shared.kerberos.codec.krbError;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.KrbError;

public class KrbErrorContainer extends AbstractContainer {
   private KrbError krbError;

   public KrbErrorContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KrbErrorGrammar.getInstance());
      this.setTransition(KrbErrorStatesEnum.START_STATE);
   }

   public KrbError getKrbError() {
      return this.krbError;
   }

   public void setKrbError(KrbError krbError) {
      this.krbError = krbError;
   }
}
