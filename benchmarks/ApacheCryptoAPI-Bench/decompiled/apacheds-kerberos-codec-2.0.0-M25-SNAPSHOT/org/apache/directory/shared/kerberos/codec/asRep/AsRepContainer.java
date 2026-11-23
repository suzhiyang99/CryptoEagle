package org.apache.directory.shared.kerberos.codec.asRep;

import java.nio.ByteBuffer;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.messages.AsRep;

public class AsRepContainer extends KdcRepContainer {
   private AsRep asRep;

   public AsRepContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(AsRepGrammar.getInstance());
      this.setTransition(AsRepStatesEnum.START_STATE);
   }

   public AsRep getAsRep() {
      return this.asRep;
   }

   public void setAsRep(AsRep asRep) {
      this.asRep = asRep;
   }
}
