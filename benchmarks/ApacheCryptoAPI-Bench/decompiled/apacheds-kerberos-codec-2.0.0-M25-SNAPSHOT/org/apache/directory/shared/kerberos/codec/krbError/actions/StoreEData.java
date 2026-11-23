package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreEData extends AbstractReadOctetString<KrbErrorContainer> {
   public StoreEData() {
      super("KRB-ERROR edata", true);
   }

   protected void setOctetString(byte[] data, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setEData(data);
      krbErrorContainer.setGrammarEndAllowed(true);
   }
}
