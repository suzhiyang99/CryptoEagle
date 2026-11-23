package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;

public class StoreUserData extends AbstractReadOctetString<EncKrbPrivPartContainer> {
   public StoreUserData() {
      super("EncKrbPrivPart user-data", true);
   }

   protected void setOctetString(byte[] data, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setUserData(data);
   }
}
