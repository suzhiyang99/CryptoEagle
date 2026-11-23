package org.apache.directory.shared.kerberos.codec.encApRepPart.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;

public class StoreCusec extends AbstractReadInteger<EncApRepPartContainer> {
   public StoreCusec() {
      super("EncApRepPart cusec", 0, 999999);
   }

   protected void setIntegerValue(int value, EncApRepPartContainer encApRepPartContainer) {
      encApRepPartContainer.getEncApRepPart().setCusec(value);
      encApRepPartContainer.setGrammarEndAllowed(true);
   }
}
