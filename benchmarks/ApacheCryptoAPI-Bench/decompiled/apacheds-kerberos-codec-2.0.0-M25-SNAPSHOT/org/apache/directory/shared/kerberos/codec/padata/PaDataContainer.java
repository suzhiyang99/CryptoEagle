package org.apache.directory.shared.kerberos.codec.padata;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.PaData;

public class PaDataContainer extends AbstractContainer {
   private PaData paData;

   public PaDataContainer() {
      this.setGrammar(PaDataGrammar.getInstance());
      this.setTransition(PaDataStatesEnum.START_STATE);
   }

   public PaData getPaData() {
      return this.paData;
   }

   public void setPaData(PaData paData) {
      this.paData = paData;
   }
}
