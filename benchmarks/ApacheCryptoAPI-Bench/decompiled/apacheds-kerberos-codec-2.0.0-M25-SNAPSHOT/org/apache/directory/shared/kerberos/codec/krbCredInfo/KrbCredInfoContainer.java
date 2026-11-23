package org.apache.directory.shared.kerberos.codec.krbCredInfo;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.KrbCredInfo;

public class KrbCredInfoContainer extends AbstractContainer {
   private KrbCredInfo krbCredInfo;

   public KrbCredInfoContainer() {
      this.setGrammar(KrbCredInfoGrammar.getInstance());
      this.setTransition(KrbCredInfoStatesEnum.START_STATE);
   }

   public KrbCredInfo getKrbCredInfo() {
      return this.krbCredInfo;
   }

   public void setKrbCredInfo(KrbCredInfo krbCredInfo) {
      this.krbCredInfo = krbCredInfo;
   }
}
