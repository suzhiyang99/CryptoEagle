package org.apache.directory.shared.kerberos.codec.typedData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.typedData.TypedDataContainer;
import org.apache.directory.shared.kerberos.components.TypedData;

public class StoreTdType extends AbstractReadInteger<TypedDataContainer> {
   public StoreTdType() {
      super("TypedData data-type");
   }

   protected void setIntegerValue(int value, TypedDataContainer typedDataContainer) {
      TypedData typedData = typedDataContainer.getTypedData();
      typedData.createNewTD();
      typedData.setCurrentDataType(value);
      typedDataContainer.setGrammarEndAllowed(true);
   }
}
