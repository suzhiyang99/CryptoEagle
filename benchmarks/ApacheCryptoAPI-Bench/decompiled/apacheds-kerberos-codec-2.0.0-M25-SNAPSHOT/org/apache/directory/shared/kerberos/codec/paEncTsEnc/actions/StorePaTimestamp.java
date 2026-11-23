package org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.PaEncTsEncContainer;

public class StorePaTimestamp extends AbstractReadKerberosTime<PaEncTsEncContainer> {
   public StorePaTimestamp() {
      super("Stores the CTime");
   }

   protected void setKerberosTime(KerberosTime krbtime, PaEncTsEncContainer paEncTsEncContainer) {
      paEncTsEncContainer.getPaEncTsEnc().setPaTimestamp(krbtime);
      paEncTsEncContainer.setGrammarEndAllowed(true);
   }
}
