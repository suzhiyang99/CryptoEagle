package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.rules.CheckClassLoading;
import org.apache.openejb.config.rules.ValidationBase;
import org.apache.openejb.loader.SystemInstance;

public class ValidateModules implements DynamicDeployer {
   public static final String OPENEJB_CHECK_CLASSLOADER = "openejb.check.classloader";

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      AppValidator validator;
      if (!SystemInstance.get().getOptions().get("openejb.check.classloader", false)) {
         validator = new AppValidator();
      } else {
         validator = new AppValidator(new ValidationBase[]{new CheckClassLoading()});
      }

      return validator.validate(appModule);
   }
}
