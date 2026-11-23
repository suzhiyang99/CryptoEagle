package org.apache.directory.shared.kerberos.codec.etypeInfo;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo;
import org.apache.directory.shared.kerberos.components.ETypeInfoEntry;

public class ETypeInfoContainer extends AbstractContainer {
   private ETypeInfo etypeInfo = new ETypeInfo();

   public ETypeInfoContainer() {
      this.setGrammar(ETypeInfoGrammar.getInstance());
      this.setTransition(ETypeInfoStatesEnum.START_STATE);
   }

   public ETypeInfo getETypeInfo() {
      return this.etypeInfo;
   }

   public void setETypeInfo(ETypeInfo etypeInfo) {
      this.etypeInfo = etypeInfo;
   }

   public void addEtypeInfoEntry(ETypeInfoEntry etypeInfoEntry) {
      this.etypeInfo.addETypeInfoEntry(etypeInfoEntry);
   }
}
