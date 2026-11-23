package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;

public class ETypeSequence extends GrammarAction<KdcReqBodyContainer> {
   public ETypeSequence() {
      super("KDC-REQ-BODY EType sequence");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         kdcReqBodyContainer.setGrammarEndAllowed(true);
      }

   }
}
