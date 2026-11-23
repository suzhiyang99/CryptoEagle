package org.apache.directory.shared.kerberos.codec.krbPriv.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;
import org.apache.directory.shared.kerberos.messages.KrbPriv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbPrivInit extends GrammarAction<KrbPrivContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KrbPrivInit.class);
   private static final boolean IS_DEBUG;

   public KrbPrivInit() {
      super("Creates a KrbPriv instance");
   }

   public void action(KrbPrivContainer krbPrivContainer) throws DecoderException {
      TLV tlv = krbPrivContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KrbPriv krbPriv = new KrbPriv();
         krbPrivContainer.setKrbPriv(krbPriv);
         if (IS_DEBUG) {
            LOG.debug("KrbPriv created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
