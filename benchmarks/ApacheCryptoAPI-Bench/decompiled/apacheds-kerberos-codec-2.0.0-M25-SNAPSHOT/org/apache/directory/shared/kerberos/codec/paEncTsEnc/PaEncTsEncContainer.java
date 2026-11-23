package org.apache.directory.shared.kerberos.codec.paEncTsEnc;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.PaEncTsEnc;

public class PaEncTsEncContainer extends AbstractContainer {
   private PaEncTsEnc paEncTsEnc = new PaEncTsEnc();

   public PaEncTsEncContainer() {
      this.setGrammar(PaEncTsEncGrammar.getInstance());
      this.setTransition(PaEncTsEncStatesEnum.START_STATE);
   }

   public PaEncTsEnc getPaEncTsEnc() {
      return this.paEncTsEnc;
   }

   public void setPaEncTsEnc(PaEncTsEnc paEncTsEnc) {
      this.paEncTsEnc = paEncTsEnc;
   }
}
