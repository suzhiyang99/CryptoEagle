package org.apache.directory.shared.kerberos.codec.krbSafeBody;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.KrbSafeBody;

public class KrbSafeBodyContainer extends AbstractContainer {
   private KrbSafeBody krbSafeBody;

   public KrbSafeBodyContainer() {
      this.setGrammar(KrbSafeBodyGrammar.getInstance());
      this.setTransition(KrbSafeBodyStatesEnum.START_STATE);
   }

   public KrbSafeBody getKrbSafeBody() {
      return this.krbSafeBody;
   }

   public void setKrbSafeBody(KrbSafeBody krbSafeBody) {
      this.krbSafeBody = krbSafeBody;
   }
}
