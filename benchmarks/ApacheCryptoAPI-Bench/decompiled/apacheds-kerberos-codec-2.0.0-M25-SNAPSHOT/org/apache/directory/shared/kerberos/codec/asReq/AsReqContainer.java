package org.apache.directory.shared.kerberos.codec.asReq;

import java.nio.ByteBuffer;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.messages.AsReq;

public class AsReqContainer extends KdcReqContainer {
   private AsReq asReq;

   public AsReqContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(AsReqGrammar.getInstance());
      this.setTransition(AsReqStatesEnum.START_STATE);
   }

   public AsReq getAsReq() {
      return this.asReq;
   }

   public void setAsReq(AsReq asReq) {
      this.asReq = asReq;
   }
}
