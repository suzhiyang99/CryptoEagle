package org.apache.directory.shared.kerberos.codec.apReq.actions;

import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;

public class CheckMsgType extends AbstractReadMsgType<ApReqContainer> {
   public CheckMsgType() {
      super("AP-REQ msg-type", KerberosMessageType.AP_REQ);
   }
}
