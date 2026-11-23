package org.apache.openejb.config.rules;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import javax.jws.WebService;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.finder.IAnnotationFinder;

public class CheckAnnotations extends ValidationBase {
   Logger logger;

   public CheckAnnotations() {
      this.logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_VALIDATION, CheckAnnotations.class);
   }

   public void validate(AppModule appModule) {
      try {
         for(EjbModule ejbModule : appModule.getEjbModules()) {
            this.module = ejbModule;
            this.findClassesAnnotatedWithWebService(ejbModule);
         }

         for(WebModule webModule : appModule.getWebModules()) {
            this.module = webModule;
            this.findClassesAnnotatedWithWebService(webModule);
         }
      } catch (Exception e) {
         this.logger.error("Error while validating @WebService annotation", (Throwable)e);
      }

   }

   private void findClassesAnnotatedWithWebService(EjbModule ejbModule) {
      IAnnotationFinder finder = ejbModule.getFinder();
      if (finder != null) {
         this.findIncorrectAnnotationAndWarn(finder, ejbModule.toString());
      }

   }

   private void findClassesAnnotatedWithWebService(WebModule webModule) {
      IAnnotationFinder finder = webModule.getFinder();
      if (finder != null) {
         this.findIncorrectAnnotationAndWarn(finder, webModule.toString());
      }

   }

   private void findIncorrectAnnotationAndWarn(IAnnotationFinder finder, String component) {
      for(Class clazz : finder.findAnnotatedClasses(WebService.class)) {
         Annotation[] annotations = clazz.getDeclaredAnnotations();

         for(Annotation declaredAnn : Arrays.asList(annotations)) {
            if (declaredAnn.annotationType().getName().equals("javax.ejb.Stateful")) {
               this.warn(component, "annotation.invalid.stateful.webservice", new Object[]{clazz.getName()});
            }

            if (declaredAnn.annotationType().getName().equals("javax.annotation.ManagedBean")) {
               this.warn(component, "annotation.invalid.managedbean.webservice", new Object[]{clazz.getName()});
            }

            if (declaredAnn.annotationType().getName().equals("javax.ejb.MessageDriven")) {
               this.warn(component, "annotation.invalid.messagedriven.webservice", new Object[]{clazz.getName()});
            }
         }
      }

   }
}
