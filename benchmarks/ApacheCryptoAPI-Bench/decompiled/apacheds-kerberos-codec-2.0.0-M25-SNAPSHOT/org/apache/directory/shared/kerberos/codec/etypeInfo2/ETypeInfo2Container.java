package org.apache.directory.shared.kerberos.codec.etypeInfo2;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.ETypeInfo2;
import org.apache.directory.shared.kerberos.components.ETypeInfo2Entry;

public class ETypeInfo2Container extends AbstractContainer {
   private ETypeInfo2 etypeInfo2 = new ETypeInfo2();

   public ETypeInfo2Container() {
      this.setGrammar(ETypeInfo2Grammar.getInstance());
      this.setTransition(ETypeInfo2StatesEnum.START_STATE);
   }

   public ETypeInfo2 getETypeInfo2() {
      return this.etypeInfo2;
   }

   public void setETypeInfo2(ETypeInfo2 etypeInfo2) {
      this.etypeInfo2 = etypeInfo2;
   }

   public void addEtypeInfo2Entry(ETypeInfo2Entry etypeInfo2Entry) {
      this.etypeInfo2.addETypeInfo2Entry(etypeInfo2Entry);
   }
}
