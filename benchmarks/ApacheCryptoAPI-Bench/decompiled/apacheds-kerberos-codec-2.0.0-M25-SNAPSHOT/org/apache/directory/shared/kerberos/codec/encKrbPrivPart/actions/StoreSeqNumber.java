package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;

public class StoreSeqNumber extends AbstractReadInteger<EncKrbPrivPartContainer> {
   public StoreSeqNumber() {
      super("EncKrbPrivPart seq-number", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, EncKrbPrivPartContainer encKrbPrivPartContainer) {
      encKrbPrivPartContainer.getEncKrbPrivPart().setSeqNumber(value);
   }
}
