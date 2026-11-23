package org.apache.directory.shared.kerberos.codec.apReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;
import org.apache.directory.shared.kerberos.messages.ApReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApReqInit extends GrammarAction<ApReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ApReqInit.class);

   public ApReqInit() {
      super("AP-REQ initialization");
   }

   public void action(ApReqContainer apReqContainer) throws DecoderException {
      TLV tlv = apReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         ApReq apReq = new ApReq();
         apReqContainer.setApReq(apReq);
      }
   }
}
