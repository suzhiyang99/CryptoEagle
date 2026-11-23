package org.apache.openejb.config.rules;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.Init;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.interceptor.InvocationContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.AroundInvoke;
import org.apache.openejb.jee.AroundTimeout;
import org.apache.openejb.jee.CallbackMethod;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.InitMethod;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.Invokable;
import org.apache.openejb.jee.LifecycleCallback;
import org.apache.openejb.jee.RemoveMethod;
import org.apache.openejb.jee.Session;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.jee.Timer;
import org.apache.xbean.finder.ClassFinder;

public class CheckCallbacks extends ValidationBase {
   public void validate(EjbModule module) {
      for(EnterpriseBean bean : module.getEjbJar().getEnterpriseBeans()) {
         Class ejbClass = null;

         try {
            ejbClass = this.loadClass(bean.getEjbClass());
         } catch (OpenEJBException var12) {
            continue;
         }

         if (bean instanceof Invokable) {
            Invokable invokable = (Invokable)bean;

            for(AroundInvoke aroundInvoke : invokable.getAroundInvoke()) {
               this.checkAroundInvoke(ejbClass, aroundInvoke, bean.getEjbName());
            }

            for(AroundTimeout aroundTimeout : invokable.getAroundTimeout()) {
               this.checkAroundTimeout(ejbClass, aroundTimeout, bean.getEjbName());
            }
         }

         for(LifecycleCallback callback : bean.getPostConstruct()) {
            this.checkCallback(ejbClass, "PostConstruct", callback, bean);
         }

         for(LifecycleCallback callback : bean.getPreDestroy()) {
            this.checkCallback(ejbClass, "PreDestroy", callback, bean);
         }

         ClassFinder finder = new ClassFinder(new Class[]{ejbClass});
         if (bean instanceof Session) {
            SessionBean session = (SessionBean)bean;
            if (session.getSessionType() == SessionType.STATEFUL) {
               for(LifecycleCallback callback : session.getPrePassivate()) {
                  this.checkCallback(ejbClass, "PrePassivate", callback, bean);
               }

               for(LifecycleCallback callback : session.getPostActivate()) {
                  this.checkCallback(ejbClass, "PostActivate", callback, bean);
               }

               this.checkSessionSynchronization(ejbClass, session);

               for(LifecycleCallback callback : session.getAfterBegin()) {
                  this.checkCallback(ejbClass, "AfterBegin", callback, bean);
               }

               for(LifecycleCallback callback : session.getBeforeCompletion()) {
                  this.checkCallback(ejbClass, "BeforeCompletion", callback, bean);
               }

               for(LifecycleCallback callback : session.getAfterCompletion()) {
                  this.checkCallback(ejbClass, "AfterCompletion", callback, bean, Boolean.TYPE);
               }

               for(AroundTimeout aroundTimeout : session.getAroundTimeout()) {
                  this.ignoredMethodAnnotation("AroundTimeout", bean, bean.getEjbClass(), aroundTimeout.getMethodName(), SessionType.STATEFUL.getName());
               }

               for(Timer timer : session.getTimer()) {
                  this.ignoredMethodAnnotation("Schedule/Schedules", bean, bean.getEjbClass(), timer.getTimeoutMethod().getMethodName(), SessionType.STATEFUL.getName());
               }
            } else {
               for(LifecycleCallback callback : session.getAfterBegin()) {
                  this.ignoredMethodAnnotation("AfterBegin", bean, bean.getEjbClass(), callback.getMethodName(), session.getSessionType().getName());
               }

               for(LifecycleCallback callback : session.getBeforeCompletion()) {
                  this.ignoredMethodAnnotation("BeforeCompletion", bean, bean.getEjbClass(), callback.getMethodName(), session.getSessionType().getName());
               }

               for(LifecycleCallback callback : session.getAfterCompletion()) {
                  this.ignoredMethodAnnotation("AfterCompletion", bean, bean.getEjbClass(), callback.getMethodName(), session.getSessionType().getName());
               }

               for(LifecycleCallback callback : session.getPrePassivate()) {
                  this.ignoredMethodAnnotation("PrePassivate", bean, bean.getEjbClass(), callback.getMethodName(), session.getSessionType().getName());
               }

               for(LifecycleCallback callback : session.getPostActivate()) {
                  this.ignoredMethodAnnotation("PostActivate", bean, bean.getEjbClass(), callback.getMethodName(), session.getSessionType().getName());
               }

               for(RemoveMethod method : session.getRemoveMethod()) {
                  this.ignoredMethodAnnotation("Remove", bean, bean.getEjbClass(), method.getBeanMethod().getMethodName(), session.getSessionType().getName());
               }

               for(InitMethod method : session.getInitMethod()) {
                  this.ignoredMethodAnnotation("Init", bean, bean.getEjbClass(), method.getBeanMethod().getMethodName(), session.getSessionType().getName());
               }
            }
         } else {
            for(Method method : finder.findAnnotatedMethods(PrePassivate.class)) {
               this.ignoredMethodAnnotation("PrePassivate", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(PostActivate.class)) {
               this.ignoredMethodAnnotation("PostActivate", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(Remove.class)) {
               this.ignoredMethodAnnotation("Remove", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(Init.class)) {
               this.ignoredMethodAnnotation("Init", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(AfterBegin.class)) {
               this.ignoredMethodAnnotation("AfterBegin", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(BeforeCompletion.class)) {
               this.ignoredMethodAnnotation("BeforeCompletion", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }

            for(Method method : finder.findAnnotatedMethods(AfterCompletion.class)) {
               this.ignoredMethodAnnotation("AfterCompletion", bean, bean.getEjbClass(), method.getName(), bean.getClass().getSimpleName());
            }
         }
      }

      for(Interceptor interceptor : module.getEjbJar().getInterceptors()) {
         Class interceptorClass = null;

         try {
            interceptorClass = this.loadClass(interceptor.getInterceptorClass());
         } catch (OpenEJBException var11) {
            continue;
         }

         for(AroundInvoke aroundInvoke : interceptor.getAroundInvoke()) {
            this.checkAroundInvoke(interceptorClass, aroundInvoke, "Interceptor");
         }

         for(AroundTimeout aroundTimeout : interceptor.getAroundTimeout()) {
            this.checkAroundTimeout(interceptorClass, aroundTimeout, "Interceptor");
         }

         for(LifecycleCallback callback : interceptor.getPostConstruct()) {
            this.checkCallback(interceptorClass, "PostConstruct", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getPreDestroy()) {
            this.checkCallback(interceptorClass, "PreDestroy", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getPrePassivate()) {
            this.checkCallback(interceptorClass, "PrePassivate", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getPostActivate()) {
            this.checkCallback(interceptorClass, "PostActivate", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getAfterBegin()) {
            this.checkCallback(interceptorClass, "AfterBegin", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getBeforeCompletion()) {
            this.checkCallback(interceptorClass, "BeforeCompletion", callback, interceptor);
         }

         for(LifecycleCallback callback : interceptor.getAfterCompletion()) {
            this.checkCallback(interceptorClass, "AfterCompletion", callback, interceptor);
         }
      }

   }

   private void checkAroundTypeInvoke(String aroundType, Class ejbClass, String declaringClassName, String declaringMethodName, String componentName) {
      try {
         Class<?> declaringClass = null;

         try {
            declaringClass = declaringClassName == null ? ejbClass : this.loadClass(declaringClassName);
         } catch (OpenEJBException var14) {
            this.fail(componentName, "missing.class", new Object[]{declaringClassName, aroundType, ejbClass.getName()});
            return;
         }

         Method method = this.getMethod(declaringClass, declaringMethodName, InvocationContext.class);
         Class<?> returnType = method.getReturnType();
         if (!returnType.equals(Object.class)) {
            this.fail(componentName, "aroundInvoke.badReturnType", new Object[]{aroundType, declaringMethodName, returnType.getName(), declaringClassName});
         }

         boolean throwsException = false;

         for(Class<?> exceptionType : method.getExceptionTypes()) {
            if (exceptionType.getName().equals(Exception.class.getName())) {
               throwsException = true;
            }
         }

         if (!throwsException) {
            this.fail(componentName, "aroundInvoke.mustThrowException", new Object[]{aroundType, declaringMethodName, declaringClassName});
         }
      } catch (NoSuchMethodException var15) {
         List<Method> possibleMethods = this.getMethods(ejbClass, declaringMethodName);
         if (possibleMethods.size() == 0) {
            this.fail(componentName, "aroundInvoke.missing", new Object[]{aroundType, declaringMethodName, declaringClassName});
         } else if (possibleMethods.size() == 1) {
            this.fail(componentName, "aroundInvoke.invalidArguments", new Object[]{aroundType, declaringMethodName, this.getParameters((Method)possibleMethods.get(0)), declaringClassName});
            Class<?> returnType = ((Method)possibleMethods.get(0)).getReturnType();
            if (!returnType.equals(Object.class)) {
               this.fail(componentName, "aroundInvoke.badReturnType", new Object[]{aroundType, declaringMethodName, returnType.getName(), declaringClassName});
            }
         } else {
            this.fail(componentName, "aroundInvoke.missing.possibleTypo", new Object[]{aroundType, declaringMethodName, possibleMethods.size(), declaringClassName});
         }
      }

   }

   private void checkAroundInvoke(Class<?> ejbClass, AroundInvoke aroundInvoke, String componentName) {
      this.checkAroundTypeInvoke("AroundInvoke", ejbClass, aroundInvoke.getClassName(), aroundInvoke.getMethodName(), componentName);
   }

   private void checkAroundTimeout(Class<?> ejbClass, AroundTimeout aroundTimeout, String componentName) {
      this.checkAroundTypeInvoke("AroundTimeout", ejbClass, aroundTimeout.getClassName(), aroundTimeout.getMethodName(), componentName);
   }

   private void checkCallback(Class<?> ejbClass, String type, CallbackMethod callback, EnterpriseBean bean, Class... parameterTypes) {
      try {
         Class<?> delcaringClass = null;

         try {
            delcaringClass = callback.getClassName() == null ? ejbClass : this.loadClass(callback.getClassName());
         } catch (OpenEJBException var10) {
            this.fail(type, "missing.class", new Object[]{callback.getClassName(), type, bean.getEjbName()});
            return;
         }

         Method method = this.getMethod(delcaringClass, callback.getMethodName(), parameterTypes);
         if (this.implementsSessionBean(delcaringClass)) {
            if ("PreDestroy".equals(type)) {
               if (!callback.getMethodName().equals("ejbRemove")) {
                  this.fail(bean.getEjbName(), "callback.sessionbean.invalidusage", new Object[]{type, callback.getMethodName(), ejbClass});
               }
            } else if ("PostActivate".equals(type)) {
               if (!callback.getMethodName().equals("ejbActivate")) {
                  this.fail(bean.getEjbName(), "callback.sessionbean.invalidusage", new Object[]{type, callback.getMethodName(), ejbClass});
               }
            } else if ("PrePassivate".equals(type)) {
               if (!callback.getMethodName().equals("ejbPassivate")) {
                  this.fail(bean.getEjbName(), "callback.sessionbean.invalidusage", new Object[]{type, callback.getMethodName(), ejbClass});
               }
            } else if ("PostConstruct".equals(type) && !callback.getMethodName().equals("ejbCreate")) {
               this.fail(bean.getEjbName(), "callback.sessionbean.invalidusage", new Object[]{type, callback.getMethodName(), ejbClass});
            }
         }

         Class<?> returnType = method.getReturnType();
         if (!returnType.equals(Void.TYPE)) {
            this.fail(bean, "callback.badReturnType", new Object[]{type, callback.getMethodName(), returnType.getName(), callback.getClassName()});
         }

         int methodModifiers = method.getModifiers();
         if (Modifier.isFinal(methodModifiers) || Modifier.isStatic(methodModifiers)) {
            this.fail(bean, "callback.badModifier", new Object[]{type, callback.getMethodName(), callback.getClassName()});
         }
      } catch (NoSuchMethodException var11) {
         List<Method> possibleMethods = this.getMethods(ejbClass, callback.getMethodName());
         if (possibleMethods.size() == 0) {
            this.fail(bean, "callback.missing", new Object[]{type, callback.getMethodName(), callback.getClassName()});
         } else if (possibleMethods.size() == 1) {
            Class<?>[] parameters = ((Method)possibleMethods.get(0)).getParameterTypes();
            if (parameters.length == 1 && parameters[0].equals(InvocationContext.class)) {
               this.fail(bean.getEjbName(), "callback.invocationcontext.notallowed", new Object[]{type, callback.getMethodName()});
            } else {
               this.fail(bean, "callback.invalidArguments", new Object[]{type, callback.getMethodName(), this.getParameters((Method)possibleMethods.get(0)), callback.getClassName(), this.getParameters(parameterTypes)});
            }
         } else {
            this.fail(bean, "callback.missing.possibleTypo", new Object[]{type, callback.getMethodName(), possibleMethods.size(), callback.getClassName(), this.getParameters(parameterTypes)});
         }
      }

   }

   private boolean implementsSessionBean(Class<?> ejbClass) {
      Class<?>[] interfaces = ejbClass.getInterfaces();

      for(Class<?> interfce : interfaces) {
         if (interfce.equals(javax.ejb.SessionBean.class)) {
            return true;
         }
      }

      return false;
   }

   private void checkCallback(Class interceptorClass, String type, CallbackMethod callback, Interceptor interceptor) {
      try {
         Class<?> delcaringClass = null;

         try {
            delcaringClass = callback.getClassName() == null ? interceptorClass : this.loadClass(callback.getClassName());
         } catch (OpenEJBException var8) {
            this.fail(type, "missing.class", new Object[]{callback.getClassName(), type, interceptor.getInterceptorClass()});
            return;
         }

         Method method = this.getMethod(delcaringClass, callback.getMethodName(), InvocationContext.class);
         Class<?> returnType = method.getReturnType();
         if (!returnType.equals(Void.TYPE)) {
            this.fail("Interceptor", "interceptor.callback.badReturnType", new Object[]{interceptorClass, type, callback.getMethodName(), returnType.getName()});
         }
      } catch (NoSuchMethodException var9) {
         List<Method> possibleMethods = this.getMethods(interceptorClass, callback.getMethodName());
         if (possibleMethods.size() == 0) {
            this.fail("Interceptor", "interceptor.callback.missing", new Object[]{type, callback.getMethodName(), interceptorClass.getName()});
         } else if (possibleMethods.size() == 1) {
            this.fail("Interceptor", "interceptor.callback.invalidArguments", new Object[]{type, callback.getMethodName(), this.getParameters((Method)possibleMethods.get(0)), interceptorClass.getName()});
            Class<?> returnType = ((Method)possibleMethods.get(0)).getReturnType();
            if (!returnType.equals(Void.TYPE)) {
               this.fail("Interceptor", "interceptor.callback.badReturnType", new Object[]{interceptorClass, type, callback.getMethodName(), returnType.getName()});
            }
         } else {
            this.fail("Interceptor", "interceptor.callback.missing.possibleTypo", new Object[]{type, callback.getMethodName(), possibleMethods.size(), interceptorClass.getName()});
         }
      }

   }

   private void checkSessionSynchronization(Class ejbClass, SessionBean bean) {
      if (SessionSynchronization.class.isAssignableFrom(ejbClass)) {
         if (bean.getAfterBeginMethod() == null && bean.getBeforeCompletionMethod() == null && bean.getAfterCompletionMethod() == null) {
            ClassFinder classFinder = new ClassFinder(new Class[]{ejbClass});
            if (classFinder.findAnnotatedMethods(AfterBegin.class).size() > 0 || classFinder.findAnnotatedMethods(BeforeCompletion.class).size() > 0 || classFinder.findAnnotatedMethods(AfterCompletion.class).size() > 0) {
               this.fail(bean, "callback.sessionSynchronization.invalidUse", new Object[]{ejbClass.getName()});
            }
         } else {
            this.fail(bean, "callback.sessionSynchronization.invalidUse", new Object[]{ejbClass.getName()});
         }
      }

   }

   private Method getMethod(Class clazz, String methodName, Class... parameterTypes) throws NoSuchMethodException {
      NoSuchMethodException original;
      for(original = null; clazz != null; clazz = clazz.getSuperclass()) {
         try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
         } catch (NoSuchMethodException e) {
            if (original == null) {
               original = e;
            }
         }
      }

      throw original;
   }

   private List<Method> getMethods(Class clazz, String methodName) {
      List<Method> methods;
      for(methods = new ArrayList(); clazz != null; clazz = clazz.getSuperclass()) {
         for(Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
               methods.add(method);
            }
         }
      }

      return methods;
   }
}
