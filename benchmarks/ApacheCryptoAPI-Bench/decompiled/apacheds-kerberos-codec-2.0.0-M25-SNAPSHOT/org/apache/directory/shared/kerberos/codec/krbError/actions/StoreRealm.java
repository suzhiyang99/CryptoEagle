package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreRealm extends AbstractReadRealm<KrbErrorContainer> {
   public StoreRealm() {
      super("KRB-ERROR realm value");
   }

   protected void setRealm(String realm, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setRealm(realm);
   }
}
