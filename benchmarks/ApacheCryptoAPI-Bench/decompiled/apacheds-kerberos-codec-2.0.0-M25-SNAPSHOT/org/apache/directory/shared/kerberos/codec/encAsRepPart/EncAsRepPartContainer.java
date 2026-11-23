package org.apache.directory.shared.kerberos.codec.encAsRepPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.EncAsRepPart;

public class EncAsRepPartContainer extends AbstractContainer {
   private EncAsRepPart encAsRepPart = new EncAsRepPart();

   public EncAsRepPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncAsRepPartGrammar.getInstance());
      this.setTransition(EncAsRepPartStatesEnum.START_STATE);
   }

   public EncAsRepPart getEncAsRepPart() {
      return this.encAsRepPart;
   }

   public void setEncAsRepPart(EncAsRepPart encAsRepPart) {
      this.encAsRepPart = encAsRepPart;
   }
}
