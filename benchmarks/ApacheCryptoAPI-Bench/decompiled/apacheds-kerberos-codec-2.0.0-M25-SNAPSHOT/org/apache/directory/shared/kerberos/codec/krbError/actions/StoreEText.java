package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.messages.KrbError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreEText extends GrammarAction<KrbErrorContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEText.class);
   private static final boolean IS_DEBUG;

   public StoreEText() {
      super("KRB-ERROR etext");
   }

   public final void action(KrbErrorContainer krbErrorContainer) throws DecoderException {
      TLV tlv = krbErrorContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         BerValue value = tlv.getValue();
         String eText = Strings.utf8ToString(value.getData());
         KrbError krbError = krbErrorContainer.getKrbError();
         krbError.setEText(eText);
         if (IS_DEBUG) {
            LOG.debug("eText: " + eText);
         }

         krbErrorContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
