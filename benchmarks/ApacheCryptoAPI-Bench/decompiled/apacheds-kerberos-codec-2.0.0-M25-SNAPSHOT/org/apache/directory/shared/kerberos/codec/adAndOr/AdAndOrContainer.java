package org.apache.directory.shared.kerberos.codec.adAndOr;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.AdAndOr;

public class AdAndOrContainer extends AbstractContainer {
   private AdAndOr adAndOr = new AdAndOr();

   public AdAndOrContainer() {
      this.setGrammar(AdAndOrGrammar.getInstance());
      this.setTransition(AdAndOrStatesEnum.START_STATE);
   }

   public AdAndOr getAdAndOr() {
      return this.adAndOr;
   }

   public void setAdAndOr(AdAndOr adAndOr) {
      this.adAndOr = adAndOr;
   }
}
