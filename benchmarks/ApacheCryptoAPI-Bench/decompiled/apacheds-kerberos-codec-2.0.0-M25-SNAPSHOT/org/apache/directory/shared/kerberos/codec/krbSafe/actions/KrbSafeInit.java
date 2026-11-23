package org.apache.directory.shared.kerberos.codec.krbSafe.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;
import org.apache.directory.shared.kerberos.messages.KrbSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbSafeInit extends GrammarAction<KrbSafeContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KrbSafeInit.class);
   private static final boolean IS_DEBUG;

   public KrbSafeInit() {
      super("Creates a KrbSafe instance");
   }

   public void action(KrbSafeContainer krbSafeContainer) throws DecoderException {
      TLV tlv = krbSafeContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KrbSafe krbSafe = new KrbSafe();
         krbSafeContainer.setKrbSafe(krbSafe);
         if (IS_DEBUG) {
            LOG.debug("KrbSafe created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
