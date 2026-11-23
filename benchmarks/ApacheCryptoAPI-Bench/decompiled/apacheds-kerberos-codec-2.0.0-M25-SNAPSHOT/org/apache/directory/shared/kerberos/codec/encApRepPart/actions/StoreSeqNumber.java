package org.apache.directory.shared.kerberos.codec.encApRepPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;

public class StoreSeqNumber extends AbstractReadInteger<EncApRepPartContainer> {
   public StoreSeqNumber() {
      super("EncApRepPart seq-number", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, EncApRepPartContainer encApRepPartContainer) {
      encApRepPartContainer.getEncApRepPart().setSeqNumber(value);
      encApRepPartContainer.setGrammarEndAllowed(true);
   }
}
