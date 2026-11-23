package org.apache.directory.shared.kerberos.codec.transitedEncoding;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.TransitedEncoding;

public class TransitedEncodingContainer extends AbstractContainer {
   private TransitedEncoding transitedEncoding;

   public TransitedEncodingContainer() {
      this.setGrammar(TransitedEncodingGrammar.getInstance());
      this.setTransition(TransitedEncodingStatesEnum.START_STATE);
   }

   public TransitedEncoding getTransitedEncoding() {
      return this.transitedEncoding;
   }

   public void setTransitedEncoding(TransitedEncoding transitedEncoding) {
      this.transitedEncoding = transitedEncoding;
   }
}
