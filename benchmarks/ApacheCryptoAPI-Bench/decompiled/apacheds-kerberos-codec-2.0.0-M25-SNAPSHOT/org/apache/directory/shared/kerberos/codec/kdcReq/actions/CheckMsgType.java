package org.apache.directory.shared.kerberos.codec.kdcReq.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;

public class CheckMsgType extends AbstractReadMsgType<KdcReqContainer> {
   public CheckMsgType() {
      super("KDC-REQ msg-type");
   }
}
