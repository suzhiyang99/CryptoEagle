package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;

public class ETypeInfo2EntryContainer extends AbstractContainer {
   private ETypeInfo2Entry etypeInfo2Entry;

   public ETypeInfo2EntryContainer() {
      this.setGrammar(ETypeInfo2EntryGrammar.getInstance());
      this.setTransition(ETypeInfo2EntryStatesEnum.START_STATE);
   }

   public ETypeInfo2Entry getETypeInfo2Entry() {
      return this.etypeInfo2Entry;
   }

   public void setETypeInfo2Entry(ETypeInfo2Entry etypeInfo2Entry) {
      this.etypeInfo2Entry = etypeInfo2Entry;
   }
}
