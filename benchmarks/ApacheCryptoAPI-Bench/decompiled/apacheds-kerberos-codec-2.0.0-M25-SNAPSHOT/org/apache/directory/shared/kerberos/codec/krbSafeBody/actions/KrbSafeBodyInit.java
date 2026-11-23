package org.apache.directory.shared.kerberos.codec.krbSafeBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;
import org.apache.directory.shared.kerberos.components.KrbSafeBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbSafeBodyInit extends GrammarAction<KrbSafeBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KrbSafeBodyInit.class);
   private static final boolean IS_DEBUG;

   public KrbSafeBodyInit() {
      super("Creates a KrbSafeBody instance");
   }

   public void action(KrbSafeBodyContainer krbSafeBodyContainer) throws DecoderException {
      TLV tlv = krbSafeBodyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KrbSafeBody krbSafeBody = new KrbSafeBody();
         krbSafeBodyContainer.setKrbSafeBody(krbSafeBody);
         if (IS_DEBUG) {
            LOG.debug("KrbSafeBody created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
