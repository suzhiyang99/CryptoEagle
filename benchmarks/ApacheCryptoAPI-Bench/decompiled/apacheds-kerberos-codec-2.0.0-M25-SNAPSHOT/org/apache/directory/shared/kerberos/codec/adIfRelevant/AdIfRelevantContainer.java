package org.apache.directory.shared.kerberos.codec.adIfRelevant;

import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;
import org.apache.directory.shared.kerberos.components.AdIfRelevant;

public class AdIfRelevantContainer extends AuthorizationDataContainer {
   public AdIfRelevantContainer() {
      this.setAuthorizationData(new AdIfRelevant());
   }

   public AdIfRelevant getAdIfRelevant() {
      return (AdIfRelevant)this.getAuthorizationData();
   }
}
