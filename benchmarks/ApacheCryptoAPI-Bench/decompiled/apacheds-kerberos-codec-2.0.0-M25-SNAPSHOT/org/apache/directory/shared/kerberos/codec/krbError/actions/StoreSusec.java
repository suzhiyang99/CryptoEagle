package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreSusec extends AbstractReadInteger<KrbErrorContainer> {
   public StoreSusec() {
      super("KRB-ERROR susec", 0, 999999);
   }

   protected void setIntegerValue(int value, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setSusec(value);
   }
}
