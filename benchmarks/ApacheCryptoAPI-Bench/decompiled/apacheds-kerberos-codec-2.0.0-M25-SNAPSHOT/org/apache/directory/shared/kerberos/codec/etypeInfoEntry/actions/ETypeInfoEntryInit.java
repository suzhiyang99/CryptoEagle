package org.apache.directory.shared.kerberos.codec.etypeInfoEntry.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.ETypeInfoEntryContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfoEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETypeInfoEntryInit extends GrammarAction<ETypeInfoEntryContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfoEntryInit.class);
   private static final boolean IS_DEBUG;

   public ETypeInfoEntryInit() {
      super("Creates a ETYPE-INFO-ENTRY instance");
   }

   public void action(ETypeInfoEntryContainer eTypeInfoEntryContainer) throws DecoderException {
      TLV tlv = eTypeInfoEntryContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         ETypeInfoEntry etypeInfoEntry = new ETypeInfoEntry();
         eTypeInfoEntryContainer.setETypeInfoEntry(etypeInfoEntry);
         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO-ENTRY created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
