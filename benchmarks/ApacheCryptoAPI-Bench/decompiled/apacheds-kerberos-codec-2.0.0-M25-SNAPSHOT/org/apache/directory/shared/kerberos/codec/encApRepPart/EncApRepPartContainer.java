package org.apache.directory.shared.kerberos.codec.encApRepPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.EncApRepPart;

public class EncApRepPartContainer extends AbstractContainer {
   private EncApRepPart encApRepPart = new EncApRepPart();

   public EncApRepPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncApRepPartGrammar.getInstance());
      this.setTransition(EncApRepPartStatesEnum.START_STATE);
   }

   public EncApRepPart getEncApRepPart() {
      return this.encApRepPart;
   }

   public void setEncApRepPart(EncApRepPart encApRepPart) {
      this.encApRepPart = encApRepPart;
   }
}
