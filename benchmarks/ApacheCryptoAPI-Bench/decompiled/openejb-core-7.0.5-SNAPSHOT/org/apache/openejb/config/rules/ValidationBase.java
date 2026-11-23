package org.apache.openejb.config.rules;

import java.lang.reflect.Method;
import java.util.Collection;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ClientModule;
import org.apache.openejb.config.DeploymentModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.ValidationContext;
import org.apache.openejb.config.ValidationRule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.MethodParams;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Messages;

public abstract class ValidationBase implements ValidationRule {
   DeploymentModule module;

   public void validate(AppModule appModule) {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try {
         for(EjbModule ejbModule : appModule.getEjbModules()) {
            Thread.currentThread().setContextClassLoader(ejbModule.getClassLoader());
            this.module = ejbModule;
            this.validate(ejbModule);
         }

         for(ClientModule clientModule : appModule.getClientModules()) {
            Thread.currentThread().setContextClassLoader(clientModule.getClassLoader());
            this.module = clientModule;
            this.validate(clientModule);
         }
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }

   }

   public void validate(ClientModule appModule) {
   }

   public void validate(EjbModule appModule) {
   }

   public void error(EnterpriseBean bean, String key, Object... details) {
      this.error(bean.getEjbName(), key, details);
   }

   private void error(String componentName, String key, Object... details) {
      this.module.getValidation().error(componentName, key, details);
   }

   public void fail(EnterpriseBean bean, String key, Object... details) {
      this.fail(bean.getEjbName(), key, details);
   }

   public void fail(String component, String key, Object... details) {
      this.module.getValidation().fail(component, key, details);
   }

   public void warn(EnterpriseBean bean, String key, Object... details) {
      this.warn(bean.getEjbName(), key, details);
   }

   protected void warn(String componentName, String key, Object... details) {
      this.module.getValidation().warn(componentName, key, details);
   }

   public void missingMethod(ValidationContext set, EnterpriseBean bean, String key, String methodName, Class returnType, Class... paramTypes) {
      this.fail(bean, key, methodName, returnType.getName(), this.getParameters(paramTypes));
   }

   public void ignoredMethodAnnotation(String annotationType, EnterpriseBean bean, String className, String methodName, String beanType) {
      this.warn(bean, "ignoredMethodAnnotation", annotationType, beanType, className, methodName);
   }

   public void ignoredClassAnnotation(String annotationType, EnterpriseBean bean, String className, String beanType) {
      this.warn(bean, "ignoredClassAnnotation", annotationType, beanType, className);
   }

   public static boolean paramsMatch(Method methodA, Method methodB) {
      if (methodA.getParameterTypes().length != methodB.getParameterTypes().length) {
         return false;
      } else {
         for(int i = 0; i < methodA.getParameterTypes().length; ++i) {
            Class<?> a = methodA.getParameterTypes()[i];
            Class<?> b = methodB.getParameterTypes()[i];
            if (!a.equals(b)) {
               return false;
            }
         }

         return true;
      }
   }

   public String getParameters(Method method) {
      Class[] params = method.getParameterTypes();
      return this.getParameters(params);
   }

   public String getParameters(Class... params) {
      StringBuilder paramString = new StringBuilder(512);
      if (params.length > 0) {
         paramString.append(params[0].getName());
      }

      for(int i = 1; i < params.length; ++i) {
         paramString.append(", ");
         paramString.append(params[i]);
      }

      return paramString.toString();
   }

   public String getParameters(MethodParams methodParams) {
      return methodParams == null ? "" : Join.join(",", (Collection)methodParams.getMethodParam());
   }

   protected Class loadClass(String clazz) throws OpenEJBException {
      ClassLoader cl = this.module.getClassLoader();

      try {
         return Classes.forName(clazz, cl);
      } catch (ClassNotFoundException cnfe) {
         throw new OpenEJBException(this.messages().format("cl0007", clazz, this.module.getJarLocation()), cnfe);
      }
   }

   private Messages messages() {
      return new Messages("org.apache.openejb.util.resources");
   }

   public boolean isCmp(EnterpriseBean b) {
      if (b instanceof EntityBean) {
         EntityBean entityBean = (EntityBean)b;
         PersistenceType persistenceType = entityBean.getPersistenceType();
         return persistenceType == PersistenceType.CONTAINER;
      } else {
         return false;
      }
   }
}
