package org.apache.directory.shared.kerberos.codec.transitedEncoding.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.TransitedEncodingContainer;
import org.apache.directory.shared.kerberos.codec.types.TransitedEncodingType;

public class StoreTrType extends AbstractReadInteger<TransitedEncodingContainer> {
   public StoreTrType() {
      super("TransitedEncoding tr-type");
   }

   protected void setIntegerValue(int value, TransitedEncodingContainer transitedEncodingContainer) {
      TransitedEncodingType type = TransitedEncodingType.getTypeByOrdinal(value);
      transitedEncodingContainer.getTransitedEncoding().setTrType(type);
   }
}
