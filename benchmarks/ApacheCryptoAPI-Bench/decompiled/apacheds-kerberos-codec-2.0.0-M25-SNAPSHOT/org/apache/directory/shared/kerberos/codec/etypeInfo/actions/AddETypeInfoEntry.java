package org.apache.directory.shared.kerberos.codec.etypeInfo.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.etypeInfo.ETypeInfoContainer;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.ETypeInfoEntryContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfoEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddETypeInfoEntry extends GrammarAction<ETypeInfoContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddETypeInfoEntry.class);
   private static final boolean IS_DEBUG;

   public AddETypeInfoEntry() {
      super("Add an ETypeInfoEntry instance");
   }

   public void action(ETypeInfoContainer eTypeInfoContainer) throws DecoderException {
      TLV tlv = eTypeInfoContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder etypeInfoEntryDecoder = new Asn1Decoder();
         ETypeInfoEntryContainer etypeInfoEntryContainer = new ETypeInfoEntryContainer();
         etypeInfoEntryContainer.setStream(eTypeInfoContainer.getStream());
         eTypeInfoContainer.rewind();

         try {
            etypeInfoEntryDecoder.decode(eTypeInfoContainer.getStream(), etypeInfoEntryContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         eTypeInfoContainer.updateParent();
         ETypeInfoEntry etypeInfoEntry = etypeInfoEntryContainer.getETypeInfoEntry();
         eTypeInfoContainer.addEtypeInfoEntry(etypeInfoEntry);
         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO-ENTRY added : {}", etypeInfoEntry);
         }

         eTypeInfoContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
