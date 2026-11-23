package org.apache.directory.shared.kerberos.codec.apReq;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.ApReq;

public class ApReqContainer extends AbstractContainer {
   private ApReq apReq;

   public ApReqContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(ApReqGrammar.getInstance());
      this.setTransition(ApReqStatesEnum.START_STATE);
   }

   public ApReq getApReq() {
      return this.apReq;
   }

   public void setApReq(ApReq apReq) {
      this.apReq = apReq;
   }
}
