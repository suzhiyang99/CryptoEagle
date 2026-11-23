package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptedPart;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;

public class StoreEncAuthorizationData extends AbstractReadEncryptedPart<KdcReqBodyContainer> {
   public StoreEncAuthorizationData() {
      super("KDC-REQ-BODY enc-authorization-data");
   }

   protected void setEncryptedData(EncryptedData encryptedData, KdcReqBodyContainer kdcReqBodyContainer) {
      kdcReqBodyContainer.getKdcReqBody().setEncAuthorizationData(encryptedData);
      kdcReqBodyContainer.setGrammarEndAllowed(true);
   }
}
