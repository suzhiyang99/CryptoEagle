package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.ETypeInfo2EntryContainer;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreEType extends GrammarAction<ETypeInfo2EntryContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEType.class);
   private static final boolean IS_DEBUG;

   public StoreEType() {
      super("ETYPE-INFO2-ENTRY etype");
   }

   public void action(ETypeInfo2EntryContainer eTypeInfo2EntryContainer) throws DecoderException {
      TLV tlv = eTypeInfo2EntryContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         BerValue value = tlv.getValue();
         EncryptionType etype = null;
         ETypeInfo2Entry etypeInfo2Entry = eTypeInfo2EntryContainer.getETypeInfo2Entry();

         try {
            int eType = IntegerDecoder.parse(value);
            etype = EncryptionType.getTypeByValue(eType);
            etypeInfo2Entry.setEType(etype);
            if (IS_DEBUG) {
               LOG.debug("etype : " + etype);
            }

            eTypeInfo2EntryContainer.setGrammarEndAllowed(true);
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
