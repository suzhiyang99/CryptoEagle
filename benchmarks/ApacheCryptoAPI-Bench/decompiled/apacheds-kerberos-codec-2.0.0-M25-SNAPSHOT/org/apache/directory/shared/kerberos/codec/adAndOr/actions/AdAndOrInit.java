package org.apache.directory.shared.kerberos.codec.adAndOr.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.adAndOr.AdAndOrContainer;
import org.apache.directory.shared.kerberos.components.AdAndOr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdAndOrInit extends GrammarAction<AdAndOrContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AdAndOrInit.class);
   private static final boolean IS_DEBUG;

   public AdAndOrInit() {
      super("Creates a AD-AND-OR instance");
   }

   public void action(AdAndOrContainer adAndOrContainer) throws DecoderException {
      TLV tlv = adAndOrContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         AdAndOr adAndor = new AdAndOr();
         adAndOrContainer.setAdAndOr(adAndor);
         if (IS_DEBUG) {
            LOG.debug("AdAndOr created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
