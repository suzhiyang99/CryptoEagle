package org.apache.openejb.config.rules;

import javax.inject.Inject;
import org.apache.openejb.config.EjbModule;
import org.apache.xbean.finder.IAnnotationFinder;

public class CheckCdiEnabled extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      if (ejbModule.getFinder() != null && !ejbModule.getProperties().containsKey("openejb.cdi.activated")) {
         if (ejbModule.getBeans() == null && hasAtInject(ejbModule.getFinder())) {
            this.warn(ejbModule.getModuleId(), "cdi.notEnabled", new Object[]{ejbModule.getModuleId()});
         }

      }
   }

   private static boolean hasAtInject(IAnnotationFinder finder) {
      return !finder.findAnnotatedMethods(Inject.class).isEmpty() || !finder.findAnnotatedFields(Inject.class).isEmpty();
   }
}
