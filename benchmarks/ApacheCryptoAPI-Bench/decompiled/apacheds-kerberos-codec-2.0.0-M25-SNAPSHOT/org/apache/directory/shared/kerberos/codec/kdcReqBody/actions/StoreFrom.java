package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreFrom extends GrammarAction<KdcReqBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreFrom.class);
   private static final boolean IS_DEBUG;

   public StoreFrom() {
      super("Stores the From");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() != 15) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KdcReqBody kdcReqBody = kdcReqBodyContainer.getKdcReqBody();
         BerValue value = tlv.getValue();
         String date = Strings.utf8ToString(value.getData());

         try {
            KerberosTime from = new KerberosTime(date);
            kdcReqBody.setFrom(from);
            if (IS_DEBUG) {
               LOG.debug("From : {}", from);
            }

         } catch (IllegalArgumentException var7) {
            LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
            throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
