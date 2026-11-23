package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;

public class StoreRealm extends AbstractReadRealm<KdcReqBodyContainer> {
   public StoreRealm() {
      super("Stores the Realm");
   }

   protected void setRealm(String srealm, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setRealm(srealm);
   }
}
