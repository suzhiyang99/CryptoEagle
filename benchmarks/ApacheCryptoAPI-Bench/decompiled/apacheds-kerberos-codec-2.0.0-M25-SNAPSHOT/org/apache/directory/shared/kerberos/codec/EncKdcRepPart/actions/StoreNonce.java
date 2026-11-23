package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;

public class StoreNonce extends AbstractReadInteger<EncKdcRepPartContainer> {
   public StoreNonce() {
      super("EncKdcRepPart nonce", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   public void setIntegerValue(int value, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setNonce(value);
   }
}
