package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StoreSRealm extends AbstractReadRealm<KrbCredInfoContainer> {
   public StoreSRealm() {
      super("KrbCredInfo srealm value");
   }

   protected void setRealm(String realm, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setsRealm(realm);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
