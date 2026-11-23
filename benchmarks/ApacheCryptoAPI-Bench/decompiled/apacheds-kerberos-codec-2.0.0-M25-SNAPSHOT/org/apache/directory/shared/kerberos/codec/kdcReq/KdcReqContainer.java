package org.apache.directory.shared.kerberos.codec.kdcReq;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.KdcReq;

public class KdcReqContainer extends AbstractContainer {
   private KdcReq kdcReq;

   public KdcReqContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KdcReqGrammar.getInstance());
      this.setTransition(KdcReqStatesEnum.START_STATE);
   }

   public KdcReq getKdcReq() {
      return this.kdcReq;
   }

   public void setKdcReq(KdcReq kdcReq) {
      this.kdcReq = kdcReq;
   }
}
