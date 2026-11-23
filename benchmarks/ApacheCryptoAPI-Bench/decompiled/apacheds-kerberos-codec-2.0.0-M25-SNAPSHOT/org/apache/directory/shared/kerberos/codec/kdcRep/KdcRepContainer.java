package org.apache.directory.shared.kerberos.codec.kdcRep;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.KdcRep;

public class KdcRepContainer extends AbstractContainer {
   private KdcRep kdcRep;

   public KdcRepContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KdcRepGrammar.getInstance());
      this.setTransition(KdcRepStatesEnum.START_STATE);
   }

   public KdcRep getKdcRep() {
      return this.kdcRep;
   }

   public void setKdcRep(KdcRep kdcRep) {
      this.kdcRep = kdcRep;
   }
}
