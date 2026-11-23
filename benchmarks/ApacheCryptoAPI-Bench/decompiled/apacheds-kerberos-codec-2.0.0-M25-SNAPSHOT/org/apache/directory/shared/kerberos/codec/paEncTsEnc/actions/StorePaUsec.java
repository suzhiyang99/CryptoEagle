package org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.PaEncTsEncContainer;

public class StorePaUsec extends AbstractReadInteger<PaEncTsEncContainer> {
   public StorePaUsec() {
      super("EncApRepPart pausec", 0, 999999);
   }

   protected void setIntegerValue(int value, PaEncTsEncContainer paEncTsEncContainer) {
      paEncTsEncContainer.getPaEncTsEnc().setPausec(value);
      paEncTsEncContainer.setGrammarEndAllowed(true);
   }
}
