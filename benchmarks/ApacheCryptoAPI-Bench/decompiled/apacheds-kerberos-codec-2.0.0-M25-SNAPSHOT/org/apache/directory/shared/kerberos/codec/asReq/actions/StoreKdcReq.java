package org.apache.directory.shared.kerberos.codec.asReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.asReq.AsReqContainer;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.messages.AsReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKdcReq extends GrammarAction<AsReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKdcReq.class);
   private static final boolean IS_DEBUG;

   public StoreKdcReq() {
      super("Add an KDC-REQ instance");
   }

   public void action(AsReqContainer asReqContainer) throws DecoderException {
      TLV tlv = asReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder kdcReqDecoder = new Asn1Decoder();
         KdcReqContainer kdcReqContainer = new KdcReqContainer(asReqContainer.getStream());
         AsReq asReq = new AsReq();
         kdcReqContainer.setKdcReq(asReq);

         try {
            kdcReqDecoder.decode(asReqContainer.getStream(), kdcReqContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         asReqContainer.updateParent();
         if (asReq.getMessageType() != KerberosMessageType.AS_REQ) {
            throw new DecoderException("Bad message type");
         } else {
            asReqContainer.setAsReq(asReq);
            if (IS_DEBUG) {
               LOG.debug("AS-REQ : {}", asReq);
            }

            asReqContainer.setGrammarEndAllowed(true);
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
