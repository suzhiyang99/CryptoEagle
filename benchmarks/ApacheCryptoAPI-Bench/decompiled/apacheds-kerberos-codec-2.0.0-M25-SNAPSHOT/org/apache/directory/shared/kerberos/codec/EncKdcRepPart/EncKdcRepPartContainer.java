package org.apache.directory.shared.kerberos.codec.EncKdcRepPart;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;

public class EncKdcRepPartContainer extends AbstractContainer {
   private EncKdcRepPart encKdcRepPart;

   public EncKdcRepPartContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(EncKdcRepPartGrammar.getInstance());
      this.setTransition(EncKdcRepPartStatesEnum.START_STATE);
   }

   public EncKdcRepPart getEncKdcRepPart() {
      return this.encKdcRepPart;
   }

   public void setEncKdcRepPart(EncKdcRepPart encKdcRepPart) {
      this.encKdcRepPart = encKdcRepPart;
   }
}
