package org.apache.directory.shared.kerberos.codec.lastReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.lastReq.LastReqContainer;
import org.apache.directory.shared.kerberos.components.LastReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastReqInit extends GrammarAction<LastReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(LastReqInit.class);
   private static final boolean IS_DEBUG;

   public LastReqInit() {
      super("Creates a LastReq instance");
   }

   public void action(LastReqContainer lastReqContainer) throws DecoderException {
      TLV tlv = lastReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         LastReq lastReq = new LastReq();
         lastReqContainer.setLastReq(lastReq);
         if (IS_DEBUG) {
            LOG.debug("LastReq created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
