package org.apache.directory.shared.kerberos.codec.padata.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;
import org.apache.directory.shared.kerberos.codec.types.PaDataType;
import org.apache.directory.shared.kerberos.components.PaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreDataType extends GrammarAction<PaDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreDataType.class);
   private static final boolean IS_DEBUG;

   public StoreDataType() {
      super("PaDataData's padata-type");
   }

   public void action(PaDataContainer paDataContainer) throws DecoderException {
      TLV tlv = paDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         PaData paData = paDataContainer.getPaData();
         BerValue value = tlv.getValue();

         try {
            int paDataType = IntegerDecoder.parse(value);
            paData.setPaDataType(PaDataType.getTypeByValue(paDataType));
            if (IS_DEBUG) {
               LOG.debug("padata-type : {}", paDataType);
            }

         } catch (IntegerDecoderException ide) {
            LOG.error(I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData()), ide.getLocalizedMessage()}));
            throw new DecoderException(ide.getMessage());
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
