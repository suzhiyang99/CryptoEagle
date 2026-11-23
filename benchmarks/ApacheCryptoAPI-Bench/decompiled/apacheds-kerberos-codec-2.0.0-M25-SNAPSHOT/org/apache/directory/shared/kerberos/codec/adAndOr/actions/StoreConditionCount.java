package org.apache.directory.shared.kerberos.codec.adAndOr.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.adAndOr.AdAndOrContainer;

public class StoreConditionCount extends AbstractReadInteger<AdAndOrContainer> {
   public StoreConditionCount() {
      super("AdAndOr condition count");
   }

   protected void setIntegerValue(int value, AdAndOrContainer adAndOrContainer) {
      adAndOrContainer.getAdAndOr().setConditionCount(value);
   }
}
