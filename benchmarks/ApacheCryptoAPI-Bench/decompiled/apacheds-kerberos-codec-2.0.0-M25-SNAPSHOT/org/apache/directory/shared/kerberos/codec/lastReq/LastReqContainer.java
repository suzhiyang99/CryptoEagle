package org.apache.directory.shared.kerberos.codec.lastReq;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.LastReq;

public class LastReqContainer extends AbstractContainer {
   private LastReq lastReq;

   public LastReqContainer() {
      this.setGrammar(LastReqGrammar.getInstance());
      this.setTransition(LastReqStatesEnum.START_STATE);
   }

   public LastReq getLastReq() {
      return this.lastReq;
   }

   public void setLastReq(LastReq lastReq) {
      this.lastReq = lastReq;
   }
}
