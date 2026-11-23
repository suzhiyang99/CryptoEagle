package org.apache.directory.shared.kerberos.codec.padata.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;

public class StorePaDataValue extends AbstractReadOctetString<PaDataContainer> {
   public StorePaDataValue() {
      super("PaData's padata-value", true);
   }

   protected void setOctetString(byte[] data, PaDataContainer paDataContainer) {
      paDataContainer.getPaData().setPaDataValue(data);
      paDataContainer.setGrammarEndAllowed(true);
   }
}
