package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadMsgType;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;

public class CheckMsgType extends AbstractReadMsgType<KdcRepContainer> {
   public CheckMsgType() {
      super("KDC-REP msg-type");
   }
}
