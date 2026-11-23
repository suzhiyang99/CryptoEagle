package org.apache.directory.shared.kerberos.codec.etypeInfoEntry.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.ETypeInfoEntryContainer;

public class StoreSalt extends AbstractReadOctetString<ETypeInfoEntryContainer> {
   public StoreSalt() {
      super("ETYPE-INFO-ENTRY salt", true);
   }

   protected void setOctetString(byte[] data, ETypeInfoEntryContainer eTypeInfoEntryContainer) {
      eTypeInfoEntryContainer.getETypeInfoEntry().setSalt(data);
      eTypeInfoEntryContainer.setGrammarEndAllowed(true);
   }
}
