package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncPart extends AbstractReadEncryptedPart<KdcRepContainer> {
   public StoreEncPart() {
      super("Kerberos Ticket EncodedPart");
   }

   protected void setEncryptedData(EncryptedData encryptedData, KdcRepContainer kdcRepContainer) {
      kdcRepContainer.getKdcRep().setEncPart(encryptedData);
      kdcRepContainer.setGrammarEndAllowed(true);
   }
}
