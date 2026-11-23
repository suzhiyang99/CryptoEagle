package org.apache.directory.shared.kerberos.codec.apRep;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.ApRep;

public class ApRepContainer extends AbstractContainer {
   private ApRep apRep;

   public ApRepContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(ApRepGrammar.getInstance());
      this.setTransition(ApRepStatesEnum.START_STATE);
   }

   public ApRep getApRep() {
      return this.apRep;
   }

   public void setApRep(ApRep apRep) {
      this.apRep = apRep;
   }
}
