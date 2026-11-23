package org.apache.directory.shared.kerberos.codec.apRep.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;
import org.apache.directory.shared.kerberos.messages.ApRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApRepInit extends GrammarAction<ApRepContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ApRepInit.class);

   public ApRepInit() {
      super("AP-REP initialization");
   }

   public void action(ApRepContainer apRepContainer) throws DecoderException {
      TLV tlv = apRepContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         ApRep apRep = new ApRep();
         apRepContainer.setApRep(apRep);
      }
   }
}
