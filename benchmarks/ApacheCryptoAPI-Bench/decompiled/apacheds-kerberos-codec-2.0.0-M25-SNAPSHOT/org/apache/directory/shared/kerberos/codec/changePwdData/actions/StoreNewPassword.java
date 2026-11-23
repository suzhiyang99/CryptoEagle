package org.apache.directory.shared.kerberos.codec.changePwdData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.changePwdData.ChangePasswdDataContainer;

public class StoreNewPassword extends AbstractReadOctetString<ChangePasswdDataContainer> {
   public StoreNewPassword() {
      super("Kerberos changepassword's new password value");
   }

   protected void setOctetString(byte[] newPasswd, ChangePasswdDataContainer container) {
      container.getChngPwdData().setNewPasswd(newPasswd);
      container.setGrammarEndAllowed(true);
   }
}
