package org.apache.directory.shared.kerberos.codec.typedData;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.TypedData;

public class TypedDataContainer extends AbstractContainer {
   private TypedData typedData;

   public TypedDataContainer() {
      this.setGrammar(TypedDataGrammar.getInstance());
      this.setTransition(TypedDataStatesEnum.START_STATE);
   }

   public TypedData getTypedData() {
      return this.typedData;
   }

   public void setTypedData(TypedData typedData) {
      this.typedData = typedData;
   }
}
