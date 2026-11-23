package org.apache.openejb.config.rules;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.ValidationContext;
import org.apache.openejb.config.ValidationRule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.SessionBean;

public class CheckRestMethodArePublic implements ValidationRule {
   public void validate(AppModule appModule) {
      Collection<String> standAloneClasses = new ArrayList();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try {
         for(EjbModule ejb : appModule.getEjbModules()) {
            Thread.currentThread().setContextClassLoader(ejb.getClassLoader());

            for(EnterpriseBean bean : ejb.getEjbJar().getEnterpriseBeans()) {
               if (bean instanceof SessionBean && ((SessionBean)bean).isRestService()) {
                  standAloneClasses.add(bean.getEjbClass());
                  this.valid(ejb.getValidation(), ejb.getClassLoader(), bean.getEjbClass());
               }
            }
         }

         label203:
         for(WebModule web : appModule.getWebModules()) {
            Thread.currentThread().setContextClassLoader(web.getClassLoader());
            Collection<String> classes = new ArrayList();
            classes.addAll(web.getRestClasses());
            classes.addAll(web.getEjbRestServices());
            Iterator<String> it = web.getRestApplications().iterator();

            while(true) {
               Application appInstance;
               while(true) {
                  Class<?> clazz;
                  while(true) {
                     if (!it.hasNext()) {
                        it = classes.iterator();

                        while(it.hasNext()) {
                           String current = (String)it.next();
                           if (standAloneClasses.contains(current)) {
                              it.remove();
                           }
                        }

                        for(String classname : classes) {
                           this.valid(web.getValidation(), web.getClassLoader(), classname);
                        }

                        classes.clear();
                        continue label203;
                     }

                     String app = (String)it.next();

                     try {
                        clazz = web.getClassLoader().loadClass(app);
                        break;
                     } catch (ClassNotFoundException var18) {
                     }
                  }

                  try {
                     appInstance = (Application)clazz.newInstance();
                     break;
                  } catch (Exception var19) {
                  }
               }

               try {
                  for(Class<?> rsClass : appInstance.getClasses()) {
                     classes.add(rsClass.getName());
                  }
               } catch (RuntimeException npe) {
                  if (appInstance == null) {
                     throw npe;
                  }
               }
            }
         }
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }

      standAloneClasses.clear();
   }

   private void valid(ValidationContext validation, ClassLoader classLoader, String classname) {
      Class<?> clazz;
      try {
         clazz = classLoader.loadClass(classname);
      } catch (ClassNotFoundException var13) {
         return;
      }

      int publicMethodNumber = 0;

      int nonPublicMethods;
      for(nonPublicMethods = 0; !Object.class.equals(clazz) && clazz != null; clazz = clazz.getSuperclass()) {
         for(Method mtd : clazz.getDeclaredMethods()) {
            boolean isPublic = Modifier.isPublic(mtd.getModifiers());
            if (mtd.getAnnotation(Path.class) != null && !isPublic) {
               String name = mtd.toGenericString();
               validation.warn(name, "rest.method.visibility", name);
            }

            if (isPublic) {
               ++publicMethodNumber;
            } else {
               ++nonPublicMethods;
            }
         }
      }

      if (publicMethodNumber == 0 && nonPublicMethods > 0) {
         validation.warn(classname, "no.method.in.rest.class", classname);
      } else if (publicMethodNumber == 0 && nonPublicMethods == 0) {
         validation.warn(classname, "no.rest.resource.method", classname);
      }

   }
}
