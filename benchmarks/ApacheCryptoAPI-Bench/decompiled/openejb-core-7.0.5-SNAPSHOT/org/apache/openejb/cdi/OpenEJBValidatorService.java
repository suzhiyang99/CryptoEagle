package org.apache.openejb.cdi;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.openejb.bval.ValidatorUtil;
import org.apache.webbeans.spi.ValidatorService;

public class OpenEJBValidatorService implements ValidatorService {
   public ValidatorFactory getDefaultValidatorFactory() {
      return ValidatorUtil.validatorFactory();
   }

   public Validator getDefaultValidator() {
      return ValidatorUtil.validator();
   }
}
