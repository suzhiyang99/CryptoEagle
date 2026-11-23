package org.apache.directory.shared.kerberos.codec.checksum.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.checksum.ChecksumContainer;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreCksumType extends GrammarAction<ChecksumContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreCksumType.class);
   private static final boolean IS_DEBUG;

   public StoreCksumType() {
      super("Checksum cksumtype");
   }

   public void action(ChecksumContainer checksumContainer) throws DecoderException {
      TLV tlv = checksumContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Checksum checksum = checksumContainer.getChecksum();
         BerValue value = tlv.getValue();

         try {
            int cksumType = IntegerDecoder.parse(value);
            checksum.setChecksumType(ChecksumType.getTypeByValue(cksumType));
            if (IS_DEBUG) {
               LOG.debug("cksumType : " + cksumType);
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
