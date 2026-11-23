package org.apache.directory.shared.kerberos.codec.kdcReq.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;

public class StorePvno extends AbstractReadPvno<KdcReqContainer> {
   public StorePvno() {
      super("KDC-REQ pvno");
   }

   public void setPvno(int pvno, KdcReqContainer kdcReqContainer) {
      kdcReqContainer.getKdcReq().setPvno(pvno);
   }
}
