package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class StoreCRealm extends AbstractReadRealm<KrbErrorContainer> {
   public StoreCRealm() {
      super("KRB-ERROR crealm value");
   }

   protected void setRealm(String realm, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setCRealm(realm);
   }
}
