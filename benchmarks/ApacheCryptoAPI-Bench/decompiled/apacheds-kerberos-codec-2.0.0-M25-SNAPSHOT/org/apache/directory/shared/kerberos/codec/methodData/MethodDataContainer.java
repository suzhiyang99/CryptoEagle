package org.apache.directory.shared.kerberos.codec.methodData;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.MethodData;
import org.apache.directory.shared.kerberos.components.PaData;

public class MethodDataContainer extends AbstractContainer {
   private MethodData methodData = new MethodData();

   public MethodDataContainer() {
      this.setGrammar(MethodDataGrammar.getInstance());
      this.setTransition(MethodDataStatesEnum.START_STATE);
   }

   public MethodData getMethodData() {
      return this.methodData;
   }

   public void setMethodData(MethodData methodData) {
      this.methodData = methodData;
   }

   public void addPaData(PaData paData) {
      this.methodData.addPaData(paData);
   }
}
