package org.apache.directory.shared.kerberos.codec.changePwdData.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.changePwdData.ChangePasswdDataContainer;

public class StoreTargRealm extends AbstractReadRealm<ChangePasswdDataContainer> {
   public StoreTargRealm() {
      super("Kerberos changepassword target realm value");
   }

   protected void setRealm(String realm, ChangePasswdDataContainer container) {
      container.getChngPwdData().setTargRealm(realm);
      container.setGrammarEndAllowed(true);
   }
}
