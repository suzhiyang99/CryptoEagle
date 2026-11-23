package org.apache.directory.shared.kerberos.codec.lastReq.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.lastReq.LastReqContainer;

public class StoreLrValue extends AbstractReadKerberosTime<LastReqContainer> {
   public StoreLrValue() {
      super("LastReq lr-value");
   }

   protected void setKerberosTime(KerberosTime krbtime, LastReqContainer lastReqContainer) {
      lastReqContainer.getLastReq().setCurrentLrValue(krbtime);
      lastReqContainer.setGrammarEndAllowed(true);
   }
}
