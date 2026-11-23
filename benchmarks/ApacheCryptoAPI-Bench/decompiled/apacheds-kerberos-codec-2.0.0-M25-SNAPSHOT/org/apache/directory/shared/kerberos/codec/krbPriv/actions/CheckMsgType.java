package org.apache.directory.shared.kerberos.codec.krbPriv.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;

public class CheckMsgType extends AbstractReadMsgType<KrbPrivContainer> {
   public CheckMsgType() {
      super("KRB-PRIV msg-type", KerberosMessageType.KRB_PRIV);
   }
}
