package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;

public class StoreUsec extends AbstractReadInteger<EncKrbPrivPartContainer> {
   public StoreUsec() {
      super("EncKrbPrivPart usec", 0, 999999);
   }

   protected void setIntegerValue(int value, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setUsec(value);
   }
}
