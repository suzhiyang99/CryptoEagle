package org.apache.directory.shared.kerberos.codec.krbCred.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;
import org.apache.directory.shared.kerberos.messages.KrbCred;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbCredInit extends GrammarAction<KrbCredContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KrbCredInit.class);
   private static final boolean IS_DEBUG;

   public KrbCredInit() {
      super("Creates a KrbCred instance");
   }

   public void action(KrbCredContainer krbCredContainer) throws DecoderException {
      TLV tlv = krbCredContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KrbCred krbCred = new KrbCred();
         krbCredContainer.setKrbCred(krbCred);
         if (IS_DEBUG) {
            LOG.debug("KrbCred created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
