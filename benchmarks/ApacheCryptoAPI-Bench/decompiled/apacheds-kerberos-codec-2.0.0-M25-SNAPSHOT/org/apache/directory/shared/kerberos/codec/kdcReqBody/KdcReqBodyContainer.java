package org.apache.directory.shared.kerberos.codec.kdcReqBody;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.KdcReqBody;

public class KdcReqBodyContainer extends AbstractContainer {
   private KdcReqBody kdcReqBody;

   public KdcReqBodyContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(KdcReqBodyGrammar.getInstance());
      this.setTransition(KdcReqBodyStatesEnum.START_STATE);
   }

   public KdcReqBody getKdcReqBody() {
      return this.kdcReqBody;
   }

   public void setKdcReqBody(KdcReqBody kdcReqBody) {
      this.kdcReqBody = kdcReqBody;
   }
}
