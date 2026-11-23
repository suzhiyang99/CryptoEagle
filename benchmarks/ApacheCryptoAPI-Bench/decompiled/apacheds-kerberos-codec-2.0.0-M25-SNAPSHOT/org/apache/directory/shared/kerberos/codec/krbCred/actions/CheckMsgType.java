package org.apache.directory.shared.kerberos.codec.krbCred.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;

public class CheckMsgType extends AbstractReadMsgType<KrbCredContainer> {
   public CheckMsgType() {
      super("KRB-CRED msg-type", KerberosMessageType.KRB_CRED);
   }
}
