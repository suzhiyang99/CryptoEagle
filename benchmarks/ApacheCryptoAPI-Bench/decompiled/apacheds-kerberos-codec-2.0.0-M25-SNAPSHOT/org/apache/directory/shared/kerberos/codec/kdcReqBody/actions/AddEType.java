package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddEType extends GrammarAction<KdcReqBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddEType.class);
   private static final boolean IS_DEBUG;

   public AddEType() {
      super("KDC-REQ-BODY AddEType");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         KdcReqBody kdcReqBody = kdcReqBodyContainer.getKdcReqBody();
         BerValue value = tlv.getValue();

         try {
            int etype = IntegerDecoder.parse(value);
            EncryptionType encryptionType = EncryptionType.getTypeByValue(etype);
            kdcReqBody.addEType(encryptionType);
            if (IS_DEBUG) {
               LOG.debug("EncryptionType : {}", encryptionType);
            }
         } catch (IntegerDecoderException ide) {
            LOG.error(I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData()), ide.getLocalizedMessage()}));
            throw new DecoderException(ide.getMessage());
         }

         kdcReqBodyContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
