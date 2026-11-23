package org.apache.openejb.config.rules;

import java.util.ArrayList;
import java.util.List;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.JndiReference;

public class CheckInjectionTargets extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         List<JndiReference> entries = new ArrayList();
         entries.addAll(bean.getEjbLocalRef());
         entries.addAll(bean.getEjbRef());
         entries.addAll(bean.getEnvEntry());
         entries.addAll(bean.getMessageDestinationRef());
         entries.addAll(bean.getPersistenceContextRef());
         entries.addAll(bean.getPersistenceUnitRef());
         entries.addAll(bean.getResourceEnvRef());
         entries.addAll(bean.getResourceRef());
         entries.addAll(bean.getServiceRef());

         for(JndiReference reference : entries) {
            for(InjectionTarget target : reference.getInjectionTarget()) {
               boolean classPrefix = false;
               String name = target.getInjectionTargetName();
               if (name.startsWith(target.getInjectionTargetClass() + "/")) {
                  classPrefix = true;
                  name = name.substring(target.getInjectionTargetClass().length() + 1);
               }

               if (name.startsWith("set") && name.length() >= 4 && Character.isUpperCase(name.charAt(3))) {
                  StringBuilder correctName = new StringBuilder(name);
                  correctName.delete(0, 3);
                  correctName.setCharAt(0, Character.toLowerCase(correctName.charAt(0)));
                  String shortNameCorrect = correctName.toString();
                  if (classPrefix) {
                     correctName.insert(0, target.getInjectionTargetClass() + "/");
                  }

                  this.warn(bean, "injectionTarget.nameContainsSet", new Object[]{target.getInjectionTargetName(), name, shortNameCorrect, correctName, reference.getName(), reference.getClass().getSimpleName()});
                  target.setInjectionTargetName(correctName.toString());
               }
            }
         }
      }

   }
}
