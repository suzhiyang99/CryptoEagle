package org.apache.directory.shared.kerberos.codec.principalName.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.principalName.PrincipalNameContainer;
import org.apache.directory.shared.kerberos.codec.types.PrincipalNameType;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreNameType extends AbstractReadInteger<PrincipalNameContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreNameType.class);
   private static final boolean IS_DEBUG;

   public StoreNameType() {
      super("Store the PrincipalName type");
   }

   protected void setIntegerValue(int value, PrincipalNameContainer principalNameContainer) {
      PrincipalName principalName = principalNameContainer.getPrincipalName();
      PrincipalNameType principalNameType = PrincipalNameType.getTypeByValue(value);
      principalName.setNameType(principalNameType);
      if (IS_DEBUG) {
         LOG.debug("name-type : {}" + principalNameType);
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
