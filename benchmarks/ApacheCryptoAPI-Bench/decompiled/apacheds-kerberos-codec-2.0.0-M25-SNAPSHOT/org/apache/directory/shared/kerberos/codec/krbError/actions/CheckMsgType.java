package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;

public class CheckMsgType extends AbstractReadMsgType<KrbErrorContainer> {
   public CheckMsgType() {
      super("KRB-ERROR msg-type", KerberosMessageType.KRB_ERROR);
   }
}
