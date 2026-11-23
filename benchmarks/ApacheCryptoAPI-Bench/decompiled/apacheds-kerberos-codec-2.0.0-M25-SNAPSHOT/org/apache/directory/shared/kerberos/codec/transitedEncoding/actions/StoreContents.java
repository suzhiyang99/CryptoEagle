package org.apache.directory.shared.kerberos.codec.transitedEncoding.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.TransitedEncodingContainer;

public class StoreContents extends AbstractReadOctetString<TransitedEncodingContainer> {
   public StoreContents() {
      super("TransitedEncoding contents", true);
   }

   protected void setOctetString(byte[] data, TransitedEncodingContainer transitedEncodingContainer) {
      transitedEncodingContainer.getTransitedEncoding().setContents(data);
      transitedEncodingContainer.setGrammarEndAllowed(true);
   }
}
