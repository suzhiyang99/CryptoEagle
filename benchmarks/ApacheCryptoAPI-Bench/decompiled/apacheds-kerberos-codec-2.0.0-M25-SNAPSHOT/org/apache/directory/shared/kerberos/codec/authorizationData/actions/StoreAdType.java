package org.apache.directory.shared.kerberos.codec.authorizationData.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;
import org.apache.directory.shared.kerberos.codec.encryptedData.actions.StoreEType;
import org.apache.directory.shared.kerberos.codec.types.AuthorizationType;
import org.apache.directory.shared.kerberos.components.AuthorizationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreAdType extends AbstractReadInteger<AuthorizationDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEType.class);
   private static final boolean IS_DEBUG;

   public StoreAdType() {
      super("AuthorizationData adType");
   }

   protected void setIntegerValue(int value, AuthorizationDataContainer authorizationDataContainer) {
      AuthorizationType authType = AuthorizationType.getTypeByValue(value);
      AuthorizationData authorizationData = authorizationDataContainer.getAuthorizationData();
      authorizationData.createNewAD();
      authorizationData.setCurrentAdType(authType);
      if (IS_DEBUG) {
         LOG.debug("zdType : {}", authType);
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
