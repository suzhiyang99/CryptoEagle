package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;

public class StorePRealm extends AbstractReadRealm<KrbCredInfoContainer> {
   public StorePRealm() {
      super("KrbCredInfo prealm value");
   }

   protected void setRealm(String realm, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setpRealm(realm);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
