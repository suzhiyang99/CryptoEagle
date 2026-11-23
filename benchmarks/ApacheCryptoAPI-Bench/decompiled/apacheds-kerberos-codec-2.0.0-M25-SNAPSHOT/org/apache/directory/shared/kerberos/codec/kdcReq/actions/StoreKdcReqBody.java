package org.apache.directory.shared.kerberos.codec.kdcReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.KdcReq;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKdcReqBody extends GrammarAction<KdcReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKdcReqBody.class);
   private static final boolean IS_DEBUG;

   public StoreKdcReqBody() {
      super("Stores the KDC-REQ-BODY");
   }

   public void action(KdcReqContainer kdcReqContainer) throws DecoderException {
      TLV tlv = kdcReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder kdcReqBodyDecoder = new Asn1Decoder();
         KdcReqBodyContainer kdcReqBodyContainer = new KdcReqBodyContainer(kdcReqContainer.getStream());

         try {
            kdcReqBodyDecoder.decode(kdcReqContainer.getStream(), kdcReqBodyContainer);
         } catch (DecoderException de) {
            throw de;
         }

         KdcReqBody kdcReqBody = kdcReqBodyContainer.getKdcReqBody();
         KdcReq kdcReq = kdcReqContainer.getKdcReq();
         kdcReq.setKdcReqBody(kdcReqBody);
         kdcReqContainer.updateParent();
         kdcReqContainer.setGrammarEndAllowed(true);
         if (IS_DEBUG) {
            LOG.debug("KDC-REQ-BODY : {}", kdcReqBody);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
