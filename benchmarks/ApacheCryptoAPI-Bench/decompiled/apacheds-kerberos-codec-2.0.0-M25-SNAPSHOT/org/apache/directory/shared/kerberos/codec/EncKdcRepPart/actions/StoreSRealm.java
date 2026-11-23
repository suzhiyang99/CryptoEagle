package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;

public class StoreSRealm extends AbstractReadRealm<EncKdcRepPartContainer> {
   public StoreSRealm() {
      super("EncKdcRepPart srealm value");
   }

   protected void setRealm(String srealm, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setSRealm(srealm);
   }
}
