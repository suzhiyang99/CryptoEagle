package org.apache.directory.shared.kerberos.codec.krbSafe.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;

public class CheckMsgType extends AbstractReadMsgType<KrbSafeContainer> {
   public CheckMsgType() {
      super("KRB-SAFE msg-type", KerberosMessageType.KRB_SAFE);
   }
}
