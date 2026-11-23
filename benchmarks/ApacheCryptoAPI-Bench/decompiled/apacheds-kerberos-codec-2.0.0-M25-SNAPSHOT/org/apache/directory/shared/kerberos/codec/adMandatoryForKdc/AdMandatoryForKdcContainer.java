package org.apache.directory.shared.kerberos.codec.adMandatoryForKdc;

import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;
import org.apache.directory.shared.kerberos.components.AdMandatoryForKdc;

public class AdMandatoryForKdcContainer extends AuthorizationDataContainer {
   public AdMandatoryForKdcContainer() {
      this.setAuthorizationData(new AdMandatoryForKdc());
   }

   public AdMandatoryForKdc getAdMandatoryForKdc() {
      return (AdMandatoryForKdc)this.getAuthorizationData();
   }
}
