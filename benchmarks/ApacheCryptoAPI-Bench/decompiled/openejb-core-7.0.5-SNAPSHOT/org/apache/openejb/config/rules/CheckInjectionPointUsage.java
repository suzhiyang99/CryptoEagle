package org.apache.openejb.config.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import org.apache.openejb.config.EjbModule;

public class CheckInjectionPointUsage extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      if (ejbModule.getBeans() != null) {
         try {
            for(Field field : ejbModule.getFinder().findAnnotatedFields(Inject.class)) {
               if (field.getType().equals(InjectionPoint.class) && HttpServlet.class.isAssignableFrom(field.getDeclaringClass())) {
                  Annotation[] annotations = field.getAnnotations();
                  if (annotations.length == 1 || annotations.length == 2 && field.getAnnotation(Default.class) != null) {
                     throw new DefinitionException("Can't inject InjectionPoint in " + field.getDeclaringClass());
                  }
               }
            }
         } catch (NoClassDefFoundError var5) {
         }

      }
   }
}
