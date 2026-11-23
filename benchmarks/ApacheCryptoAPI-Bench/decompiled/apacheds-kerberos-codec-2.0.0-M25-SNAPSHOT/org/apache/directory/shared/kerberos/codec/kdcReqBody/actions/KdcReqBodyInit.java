package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KdcReqBodyInit extends GrammarAction<KdcReqBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(KdcReqBodyInit.class);
   private static final boolean IS_DEBUG;

   public KdcReqBodyInit() {
      super("Creates a KdcReqBody instance");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KdcReqBody kdcReqBody = new KdcReqBody();
         kdcReqBodyContainer.setKdcReqBody(kdcReqBody);
         if (IS_DEBUG) {
            LOG.debug("KdcReqBody created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
