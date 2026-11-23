package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.codec.options.KdcOptions;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKdcOptions extends GrammarAction<KdcReqBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKdcOptions.class);
   private static final boolean IS_DEBUG;

   public StoreKdcOptions() {
      super("Stores the KDCOptions");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() != 5) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KdcReqBody kdcReqBody = kdcReqBodyContainer.getKdcReqBody();
         KdcOptions kdcOptions = new KdcOptions(tlv.getValue().getData());
         kdcReqBody.setKdcOptions(kdcOptions);
         if (IS_DEBUG) {
            LOG.debug("KDCOptions : {}", kdcOptions);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
