package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;

public class StoreErrorCode extends AbstractReadInteger<KrbErrorContainer> {
   public StoreErrorCode() {
      super("KRB-ERROR errorCode");
   }

   protected void setIntegerValue(int value, KrbErrorContainer krbErrorContainer) {
      ErrorType errorCode = ErrorType.getTypeByValue(value);
      krbErrorContainer.getKrbError().setErrorCode(errorCode);
   }
}
