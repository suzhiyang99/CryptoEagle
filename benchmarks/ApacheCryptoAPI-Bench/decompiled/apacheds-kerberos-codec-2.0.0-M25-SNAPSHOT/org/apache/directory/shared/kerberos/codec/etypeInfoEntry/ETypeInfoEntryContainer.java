package org.apache.directory.shared.kerberos.codec.etypeInfoEntry;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfoEntry;

public class ETypeInfoEntryContainer extends AbstractContainer {
   private ETypeInfoEntry etypeInfoEntry;

   public ETypeInfoEntryContainer() {
      this.setGrammar(ETypeInfoEntryGrammar.getInstance());
      this.setTransition(ETypeInfoEntryStatesEnum.START_STATE);
   }

   public ETypeInfoEntry getETypeInfoEntry() {
      return this.etypeInfoEntry;
   }

   public void setETypeInfoEntry(ETypeInfoEntry etypeInfoEntry) {
      this.etypeInfoEntry = etypeInfoEntry;
   }
}
