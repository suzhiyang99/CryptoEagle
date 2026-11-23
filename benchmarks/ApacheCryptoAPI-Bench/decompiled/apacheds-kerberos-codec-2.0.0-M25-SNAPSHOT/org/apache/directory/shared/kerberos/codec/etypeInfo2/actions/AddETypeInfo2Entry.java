package org.apache.directory.shared.kerberos.codec.etypeInfo2.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.etypeInfo2.ETypeInfo2Container;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.ETypeInfo2EntryContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddETypeInfo2Entry extends GrammarAction<ETypeInfo2Container> {
   private static final Logger LOG = LoggerFactory.getLogger(AddETypeInfo2Entry.class);
   private static final boolean IS_DEBUG;

   public AddETypeInfo2Entry() {
      super("Add an ETypeInfo2Entry instance");
   }

   public void action(ETypeInfo2Container eTypeInfo2Container) throws DecoderException {
      TLV tlv = eTypeInfo2Container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder etypeInfo2EntryDecoder = new Asn1Decoder();
         ETypeInfo2EntryContainer etypeInfo2EntryContainer = new ETypeInfo2EntryContainer();
         etypeInfo2EntryContainer.setStream(eTypeInfo2Container.getStream());
         eTypeInfo2Container.rewind();

         try {
            etypeInfo2EntryDecoder.decode(eTypeInfo2Container.getStream(), etypeInfo2EntryContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         eTypeInfo2Container.updateParent();
         ETypeInfo2Entry etypeInfo2Entry = etypeInfo2EntryContainer.getETypeInfo2Entry();
         eTypeInfo2Container.addEtypeInfo2Entry(etypeInfo2Entry);
         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO2-ENTRY added : {}", etypeInfo2Entry);
         }

         eTypeInfo2Container.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
