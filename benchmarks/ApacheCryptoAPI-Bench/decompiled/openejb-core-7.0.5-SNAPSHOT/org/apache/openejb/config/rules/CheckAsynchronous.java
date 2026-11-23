package org.apache.openejb.config.rules;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import javax.ejb.Asynchronous;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.ApplicationException;
import org.apache.openejb.jee.AsyncMethod;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.MethodParams;
import org.apache.openejb.jee.SessionBean;
import org.apache.xbean.finder.ClassFinder;

public class CheckAsynchronous extends ValidationBase {
   public void validate(EjbModule module) {
      Set<String> applicationExceptions = new HashSet();

      for(ApplicationException applicationException : module.getEjbJar().getAssemblyDescriptor().getApplicationException()) {
         applicationExceptions.add(applicationException.getExceptionClass());
      }

      for(EnterpriseBean bean : module.getEjbJar().getEnterpriseBeans()) {
         Class<?> ejbClass = null;

         try {
            ejbClass = this.loadClass(bean.getEjbClass());
         } catch (OpenEJBException var17) {
            continue;
         }

         if (bean instanceof SessionBean) {
            SessionBean session = (SessionBean)bean;

            for(AsyncMethod asyncMethod : session.getAsyncMethod()) {
               Method method = this.getMethod(ejbClass, asyncMethod);
               if (method == null) {
                  this.fail(bean, "asynchronous.missing", new Object[]{asyncMethod.getMethodName(), ejbClass.getName(), this.getParameters(asyncMethod.getMethodParams())});
               } else {
                  this.checkAsynchronousMethod(session, ejbClass, method, applicationExceptions);
               }
            }

            for(String className : session.getAsynchronousClasses()) {
               try {
                  Class<?> cls = this.loadClass(className);

                  for(Method method : cls.getDeclaredMethods()) {
                     if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                        this.checkAsynchronousMethod(session, ejbClass, method, applicationExceptions);
                     }
                  }
               } catch (OpenEJBException var16) {
               }
            }
         } else {
            ClassFinder classFinder = new ClassFinder(new Class[]{ejbClass});

            for(Method method : classFinder.findAnnotatedMethods(Asynchronous.class)) {
               this.ignoredMethodAnnotation("Asynchronous", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            if (ejbClass.getAnnotation(Asynchronous.class) != null) {
               this.ignoredClassAnnotation("Asynchronous", bean, bean.getEjbClass(), bean.getClass().getSimpleName());
            }
         }
      }

   }

   private void checkAsynchronousMethod(SessionBean bean, Class<?> ejbClass, Method method, Set<String> applicationExceptions) {
      Class<?> retType = method.getReturnType();
      if (retType != Void.TYPE && retType != Future.class) {
         this.fail(bean, "asynchronous.badReturnType", new Object[]{method.getName(), retType.getName(), ejbClass.getName()});
      }

      if (retType == Void.TYPE) {
         String invalidThrowCauses = this.checkThrowCauses(method.getExceptionTypes(), applicationExceptions);
         if (invalidThrowCauses != null) {
            this.fail(bean, "asynchronous.badExceptionType", new Object[]{method.getName(), ejbClass.getName(), invalidThrowCauses});
         }
      }

   }

   private String checkThrowCauses(Class<?>[] exceptionTypes, Set<String> applicationExceptions) {
      StringBuilder buffer = null;

      for(Class<?> exceptionType : exceptionTypes) {
         if (!applicationExceptions.contains(exceptionType.getName()) && Exception.class.isAssignableFrom(exceptionType) && !RuntimeException.class.isAssignableFrom(exceptionType)) {
            if (buffer == null) {
               buffer = new StringBuilder(exceptionType.getName());
            } else {
               buffer.append(",").append(exceptionType.getName());
            }
         }
      }

      return buffer == null ? null : buffer.toString();
   }

   private Method getMethod(Class<?> clazz, AsyncMethod asyncMethod) {
      try {
         MethodParams methodParams = asyncMethod.getMethodParams();
         Class<?>[] parameterTypes;
         if (methodParams != null) {
            parameterTypes = new Class[methodParams.getMethodParam().size()];
            int arrayIndex = 0;

            for(String parameterType : methodParams.getMethodParam()) {
               parameterTypes[arrayIndex++] = this.loadClass(parameterType);
            }
         } else {
            parameterTypes = new Class[0];
         }

         return clazz.getMethod(asyncMethod.getMethodName(), parameterTypes);
      } catch (NoSuchMethodException var8) {
         return null;
      } catch (OpenEJBException e) {
         throw new OpenEJBRuntimeException(e);
      }
   }
}
