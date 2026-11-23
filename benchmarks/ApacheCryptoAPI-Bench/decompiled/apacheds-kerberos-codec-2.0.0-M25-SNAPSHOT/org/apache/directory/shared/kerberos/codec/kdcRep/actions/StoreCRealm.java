package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;

public class StoreCRealm extends AbstractReadRealm<KdcRepContainer> {
   public StoreCRealm() {
      super("KDC-REP realm value");
   }

   protected void setRealm(String realm, KdcRepContainer kdcRepContainer) {
      kdcRepContainer.getKdcRep().setCRealm(realm);
   }
}
