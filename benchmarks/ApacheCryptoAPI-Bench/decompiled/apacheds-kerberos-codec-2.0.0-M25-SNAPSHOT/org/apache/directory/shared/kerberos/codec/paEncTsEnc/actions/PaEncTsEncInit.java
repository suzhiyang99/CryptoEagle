package org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.PaEncTsEncContainer;
import org.apache.directory.shared.kerberos.components.PaEncTsEnc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaEncTsEncInit extends GrammarAction<PaEncTsEncContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(PaEncTsEncInit.class);
   private static final boolean IS_DEBUG;

   public PaEncTsEncInit() {
      super("Creates an PaEncTsEncInit instance");
   }

   public void action(PaEncTsEncContainer paEncTsEncContainer) throws DecoderException {
      TLV tlv = paEncTsEncContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         PaEncTsEnc paEncTsEnc = new PaEncTsEnc();
         paEncTsEncContainer.setPaEncTsEnc(paEncTsEnc);
         if (IS_DEBUG) {
            LOG.debug("PaEncTsEnc created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
