package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.lastReq.LastReqContainer;
import org.apache.directory.shared.kerberos.components.LastReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreLastReq extends GrammarAction<EncKdcRepPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreLastReq.class);
   private static final boolean IS_DEBUG;

   public StoreLastReq() {
      super("Store the EncKDCRepPart LastReq");
   }

   public final void action(EncKdcRepPartContainer encKdcRepPartContainer) throws DecoderException {
      TLV tlv = encKdcRepPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder lastReqDecoder = new Asn1Decoder();
         LastReqContainer lastReqContainer = new LastReqContainer();

         try {
            lastReqDecoder.decode(encKdcRepPartContainer.getStream(), lastReqContainer);
         } catch (DecoderException de) {
            throw de;
         }

         LastReq lastReq = lastReqContainer.getLastReq();
         if (IS_DEBUG) {
            LOG.debug("LastReq : " + lastReq);
         }

         encKdcRepPartContainer.getEncKdcRepPart().setLastReq(lastReq);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         encKdcRepPartContainer.updateParent();
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
