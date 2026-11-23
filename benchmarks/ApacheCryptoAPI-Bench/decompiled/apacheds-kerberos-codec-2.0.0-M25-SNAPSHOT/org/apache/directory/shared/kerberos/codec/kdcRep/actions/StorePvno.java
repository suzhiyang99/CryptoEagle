package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;

public class StorePvno extends AbstractReadPvno<KdcRepContainer> {
   public StorePvno() {
      super("KDC-REP pvno");
   }

   protected void setPvno(int pvno, KdcRepContainer kdcRepContainer) {
      kdcRepContainer.getKdcRep().setPvno(pvno);
   }
}
