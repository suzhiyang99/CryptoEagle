package org.apache.directory.shared.kerberos.codec.encTgsRepPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.EncTgsRepPart;

public class EncTgsRepPartContainer extends AbstractContainer {
   private EncTgsRepPart encTgsRepPart = new EncTgsRepPart();

   public EncTgsRepPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncTgsRepPartGrammar.getInstance());
      this.setTransition(EncTgsRepPartStatesEnum.START_STATE);
   }

   public EncTgsRepPart getEncTgsRepPart() {
      return this.encTgsRepPart;
   }

   public void setEncTgsRepPart(EncTgsRepPart encTgsRepPart) {
      this.encTgsRepPart = encTgsRepPart;
   }
}
