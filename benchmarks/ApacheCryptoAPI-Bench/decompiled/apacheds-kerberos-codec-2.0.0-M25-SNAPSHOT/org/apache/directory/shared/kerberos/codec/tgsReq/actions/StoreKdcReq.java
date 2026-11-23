package org.apache.directory.shared.kerberos.codec.tgsReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.codec.tgsReq.TgsReqContainer;
import org.apache.directory.shared.kerberos.messages.TgsReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKdcReq extends GrammarAction<TgsReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKdcReq.class);
   private static final boolean IS_DEBUG;

   public StoreKdcReq() {
      super("Add an KDC-REQ instance");
   }

   public void action(TgsReqContainer tgsReqContainer) throws DecoderException {
      TLV tlv = tgsReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder kdcReqDecoder = new Asn1Decoder();
         KdcReqContainer kdcReqContainer = new KdcReqContainer(tgsReqContainer.getStream());
         TgsReq tgsReq = new TgsReq();
         kdcReqContainer.setKdcReq(tgsReq);

         try {
            kdcReqDecoder.decode(tgsReqContainer.getStream(), kdcReqContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         tgsReqContainer.updateParent();
         if (tgsReq.getMessageType() != KerberosMessageType.TGS_REQ) {
            throw new DecoderException("Bad message type");
         } else {
            tgsReqContainer.setTgsReq(tgsReq);
            if (IS_DEBUG) {
               LOG.debug("TGS-REQ : {}", tgsReq);
            }

            tgsReqContainer.setGrammarEndAllowed(true);
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
