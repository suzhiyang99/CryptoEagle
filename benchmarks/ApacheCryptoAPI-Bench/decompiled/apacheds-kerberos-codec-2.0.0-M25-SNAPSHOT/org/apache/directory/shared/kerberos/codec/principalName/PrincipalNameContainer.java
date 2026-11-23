package org.apache.directory.shared.kerberos.codec.principalName;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class PrincipalNameContainer extends AbstractContainer {
   private PrincipalName principalName;

   public PrincipalNameContainer() {
      this.setGrammar(PrincipalNameGrammar.getInstance());
      this.setTransition(PrincipalNameStatesEnum.START_STATE);
   }

   public PrincipalName getPrincipalName() {
      return this.principalName;
   }

   public void setPrincipalName(PrincipalName principalName) {
      this.principalName = principalName;
   }
}
