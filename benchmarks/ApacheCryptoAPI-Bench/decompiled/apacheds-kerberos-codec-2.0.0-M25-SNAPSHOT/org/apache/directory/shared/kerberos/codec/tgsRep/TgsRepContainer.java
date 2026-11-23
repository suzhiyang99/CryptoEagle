package org.apache.directory.shared.kerberos.codec.tgsRep;

import java.nio.ByteBuffer;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.messages.TgsRep;

public class TgsRepContainer extends KdcRepContainer {
   private TgsRep tgsRep;

   public TgsRepContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(TgsRepGrammar.getInstance());
      this.setTransition(TgsRepStatesEnum.START_STATE);
   }

   public TgsRep getTgsRep() {
      return this.tgsRep;
   }

   public void setTgsRep(TgsRep tgsRep) {
      this.tgsRep = tgsRep;
   }
}
