package org.apache.directory.shared.kerberos.codec.krbSafe.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.KrbSafeBodyContainer;
import org.apache.directory.shared.kerberos.components.KrbSafeBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreSafeBody extends GrammarAction<KrbSafeContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreSafeBody.class);
   private static final boolean IS_DEBUG;

   public StoreSafeBody() {
      super("KRB-SAFE safe-body");
   }

   public void action(KrbSafeContainer krbSafeContainer) throws DecoderException {
      TLV tlv = krbSafeContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder krbSafeBodyDecoder = new Asn1Decoder();
         KrbSafeBodyContainer krbSafeBodyContainer = new KrbSafeBodyContainer();
         krbSafeBodyContainer.setStream(krbSafeContainer.getStream());

         try {
            krbSafeBodyDecoder.decode(krbSafeContainer.getStream(), krbSafeBodyContainer);
         } catch (DecoderException de) {
            throw de;
         }

         KrbSafeBody krbSafeBody = krbSafeBodyContainer.getKrbSafeBody();
         if (IS_DEBUG) {
            LOG.debug("KrbSafeBody : {}", krbSafeBody);
         }

         krbSafeContainer.getKrbSafe().setSafeBody(krbSafeBody);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         krbSafeContainer.updateParent();
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
