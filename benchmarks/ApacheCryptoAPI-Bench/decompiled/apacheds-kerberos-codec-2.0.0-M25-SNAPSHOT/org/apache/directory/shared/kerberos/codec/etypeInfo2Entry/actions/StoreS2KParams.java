package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.ETypeInfo2EntryContainer;

public class StoreS2KParams extends AbstractReadOctetString<ETypeInfo2EntryContainer> {
   public StoreS2KParams() {
      super("ETYPE-INFO2-ENTRY s2kparams", true);
   }

   protected void setOctetString(byte[] data, ETypeInfo2EntryContainer eTypeInfo2EntryContainer) {
      eTypeInfo2EntryContainer.getETypeInfo2Entry().setS2kparams(data);
      eTypeInfo2EntryContainer.setGrammarEndAllowed(true);
   }
}
