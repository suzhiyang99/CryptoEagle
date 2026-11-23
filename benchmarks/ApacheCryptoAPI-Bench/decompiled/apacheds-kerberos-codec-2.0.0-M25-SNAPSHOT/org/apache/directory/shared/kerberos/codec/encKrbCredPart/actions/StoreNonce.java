package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;

public class StoreNonce extends AbstractReadInteger<EncKrbCredPartContainer> {
   public StoreNonce() {
      super("EncKrbCredPart nonce", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   public void setIntegerValue(int value, EncKrbCredPartContainer encKrbCredPartContainer) {
      encKrbCredPartContainer.getEncKrbCredPart().setNonce(value);
      encKrbCredPartContainer.setGrammarEndAllowed(true);
   }
}
