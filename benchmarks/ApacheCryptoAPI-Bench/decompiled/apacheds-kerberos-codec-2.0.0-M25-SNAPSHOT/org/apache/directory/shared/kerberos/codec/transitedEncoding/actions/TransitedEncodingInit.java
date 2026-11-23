package org.apache.directory.shared.kerberos.codec.transitedEncoding.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.TransitedEncodingContainer;
import org.apache.directory.shared.kerberos.components.TransitedEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransitedEncodingInit extends GrammarAction<TransitedEncodingContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(TransitedEncodingInit.class);
   private static final boolean IS_DEBUG;

   public TransitedEncodingInit() {
      super("Creates a TransitedEncoding instance");
   }

   public void action(TransitedEncodingContainer transitedEncodingContainer) throws DecoderException {
      TLV tlv = transitedEncodingContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         TransitedEncoding transitedEncoding = new TransitedEncoding();
         transitedEncodingContainer.setTransitedEncoding(transitedEncoding);
         if (IS_DEBUG) {
            LOG.debug("TransitedEncoding created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
