package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.ETypeInfo2EntryContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETypeInfo2EntryInit extends GrammarAction<ETypeInfo2EntryContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfo2EntryInit.class);
   private static final boolean IS_DEBUG;

   public ETypeInfo2EntryInit() {
      super("Creates a ETYPE-INFO2-ENTRY instance");
   }

   public void action(ETypeInfo2EntryContainer eTypeInfo2EntryContainer) throws DecoderException {
      TLV tlv = eTypeInfo2EntryContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         ETypeInfo2Entry etypeInfo2Entry = new ETypeInfo2Entry();
         eTypeInfo2EntryContainer.setETypeInfo2Entry(etypeInfo2Entry);
         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO2-ENTRY created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
