package org.apache.directory.shared.kerberos.codec.krbCred;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.KrbCred;

public class KrbCredContainer extends AbstractContainer {
   private KrbCred krbCred;

   public KrbCredContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KrbCredGrammar.getInstance());
      this.setTransition(KrbCredStatesEnum.START_STATE);
   }

   public KrbCred getKrbCred() {
      return this.krbCred;
   }

   public void setKrbCred(KrbCred krbCred) {
      this.krbCred = krbCred;
   }
}
