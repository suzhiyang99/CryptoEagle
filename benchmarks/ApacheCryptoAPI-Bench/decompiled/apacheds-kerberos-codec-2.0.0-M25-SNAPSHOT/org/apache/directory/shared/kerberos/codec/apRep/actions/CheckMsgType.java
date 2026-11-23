package org.apache.directory.shared.kerberos.codec.apRep.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;

public class CheckMsgType extends AbstractReadMsgType<ApRepContainer> {
   public CheckMsgType() {
      super("AP-REP msg-type", KerberosMessageType.AP_REP);
   }
}
