package org.apache.directory.shared.kerberos.codec.encKrbCredPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncKrbCredPart;

public class EncKrbCredPartContainer extends AbstractContainer {
   private EncKrbCredPart encKrbCredPart;

   public EncKrbCredPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncKrbCredPartGrammar.getInstance());
      this.setTransition(EncKrbCredPartStatesEnum.START_STATE);
   }

   public EncKrbCredPart getEncKrbCredPart() {
      return this.encKrbCredPart;
   }

   public void setEncKrbCredPart(EncKrbCredPart encKrbCredPart) {
      this.encKrbCredPart = encKrbCredPart;
   }
}
