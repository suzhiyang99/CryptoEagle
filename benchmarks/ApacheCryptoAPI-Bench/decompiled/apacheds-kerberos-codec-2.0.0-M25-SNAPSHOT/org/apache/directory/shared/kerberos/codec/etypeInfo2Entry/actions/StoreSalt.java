package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.ETypeInfo2EntryContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreSalt extends GrammarAction<ETypeInfo2EntryContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreSalt.class);
   private static final boolean IS_DEBUG;

   public StoreSalt() {
      super("ETYPE-INFO2-ENTRY salt");
   }

   public void action(ETypeInfo2EntryContainer eTypeInfo2EntryContainer) throws DecoderException {
      TLV tlv = eTypeInfo2EntryContainer.getCurrentTLV();
      ETypeInfo2Entry etypeInfo2Entry = eTypeInfo2EntryContainer.getETypeInfo2Entry();
      if (tlv.getLength() != 0) {
         BerValue value = tlv.getValue();
         if (value.getData() != null) {
            String salt = Strings.utf8ToString(value.getData());
            etypeInfo2Entry.setSalt(salt);
         }
      }

      if (IS_DEBUG) {
         LOG.debug("salt : {}", etypeInfo2Entry.getSalt());
      }

      eTypeInfo2EntryContainer.setGrammarEndAllowed(true);
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
