package org.apache.directory.shared.kerberos.codec.tgsReq;

import java.nio.ByteBuffer;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.messages.TgsReq;

public class TgsReqContainer extends KdcReqContainer {
   private TgsReq tgsReq;

   public TgsReqContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(TgsReqGrammar.getInstance());
      this.setTransition(TgsReqStatesEnum.START_STATE);
   }

   public TgsReq getTgsReq() {
      return this.tgsReq;
   }

   public void setTgsReq(TgsReq tgsReq) {
      this.tgsReq = tgsReq;
   }
}
