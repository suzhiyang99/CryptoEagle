package org.apache.directory.shared.kerberos.codec.encKrbPrivPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncKrbPrivPart;

public class EncKrbPrivPartContainer extends AbstractContainer {
   private EncKrbPrivPart encKrbPrivPart;

   public EncKrbPrivPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncKrbPrivPartGrammar.getInstance());
      this.setTransition(EncKrbPrivPartStatesEnum.START_STATE);
   }

   public EncKrbPrivPart getEncKrbPrivPart() {
      return this.encKrbPrivPart;
   }

   public void setEncKrbPrivPart(EncKrbPrivPart encKrbPrivPart) {
      this.encKrbPrivPart = encKrbPrivPart;
   }
}
