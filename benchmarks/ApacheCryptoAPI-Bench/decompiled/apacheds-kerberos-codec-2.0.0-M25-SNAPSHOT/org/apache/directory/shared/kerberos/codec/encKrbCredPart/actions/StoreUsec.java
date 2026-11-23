package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;

public class StoreUsec extends AbstractReadInteger<EncKrbCredPartContainer> {
   public StoreUsec() {
      super("EncKrbCredPart usec", 0, 999999);
   }

   protected void setIntegerValue(int value, EncKrbCredPartContainer encKrbCredPartContainer) {
      encKrbCredPartContainer.getEncKrbCredPart().setUsec(value);
      encKrbCredPartContainer.setGrammarEndAllowed(true);
   }
}
