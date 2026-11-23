package org.apache.directory.shared.kerberos.codec.typedData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.typedData.TypedDataContainer;

public class StoreDataValue extends AbstractReadOctetString<TypedDataContainer> {
   public StoreDataValue() {
      super("TypedData data-value");
   }

   protected void setOctetString(byte[] data, TypedDataContainer typedDataContainer) {
      typedDataContainer.getTypedData().setCurrentDataValue(data);
      typedDataContainer.setGrammarEndAllowed(true);
   }
}
