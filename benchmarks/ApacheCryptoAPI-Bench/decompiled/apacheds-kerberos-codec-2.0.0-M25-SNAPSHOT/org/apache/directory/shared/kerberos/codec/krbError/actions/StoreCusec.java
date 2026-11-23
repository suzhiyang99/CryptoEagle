package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreCusec extends AbstractReadInteger<KrbErrorContainer> {
   public StoreCusec() {
      super("KRB-ERROR cusec", 0, 999999);
   }

   protected void setIntegerValue(int value, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setCusec(value);
   }
}
