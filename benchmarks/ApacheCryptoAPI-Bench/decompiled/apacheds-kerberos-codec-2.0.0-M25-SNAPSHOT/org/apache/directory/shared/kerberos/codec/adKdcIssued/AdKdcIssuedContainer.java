package org.apache.directory.shared.kerberos.codec.adKdcIssued;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.AdKdcIssued;

public class AdKdcIssuedContainer extends AbstractContainer {
   private AdKdcIssued adKdcIssued = new AdKdcIssued();

   public AdKdcIssuedContainer() {
      this.setGrammar(AdKDCIssuedGrammar.getInstance());
      this.setTransition(AdKDCIssuedStatesEnum.START_STATE);
   }

   public AdKdcIssued getAdKdcIssued() {
      return this.adKdcIssued;
   }

   public void setAdKdcIssued(AdKdcIssued adKdcIssued) {
      this.adKdcIssued = adKdcIssued;
   }
}
