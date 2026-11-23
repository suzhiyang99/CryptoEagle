package org.apache.directory.shared.kerberos.codec.apReq.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;

public class StorePvno extends AbstractReadPvno<ApReqContainer> {
   public StorePvno() {
      super("StorePvno value");
   }

   protected void setPvno(int pvno, ApReqContainer apReqContainer) {
      apReqContainer.getApReq().setProtocolVersionNumber(pvno);
   }
}
