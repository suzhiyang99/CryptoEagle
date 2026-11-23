package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadHostAddresses;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.HostAddresses;

public class StoreCaddr extends AbstractReadHostAddresses<KrbCredInfoContainer> {
   public StoreCaddr() {
      super("KrbCredInfo caddr");
   }

   protected void setHostAddresses(HostAddresses hostAddresses, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setClientAddresses(hostAddresses);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
