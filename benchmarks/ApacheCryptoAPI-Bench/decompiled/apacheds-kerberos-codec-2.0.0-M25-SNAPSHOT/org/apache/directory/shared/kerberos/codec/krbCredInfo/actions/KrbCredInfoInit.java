package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.KrbCredInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbCredInfoInit extends GrammarAction<KrbCredInfoContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KrbCredInfoInit.class);
   private static final boolean IS_DEBUG;

   public KrbCredInfoInit() {
      super("Creates a KrbCredInfo instance");
   }

   public void action(KrbCredInfoContainer krbCredInfoContainer) throws DecoderException {
      TLV tlv = krbCredInfoContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KrbCredInfo krbCredInfo = new KrbCredInfo();
         krbCredInfoContainer.setKrbCredInfo(krbCredInfo);
         if (IS_DEBUG) {
            LOG.debug("KrbCredInfo created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
