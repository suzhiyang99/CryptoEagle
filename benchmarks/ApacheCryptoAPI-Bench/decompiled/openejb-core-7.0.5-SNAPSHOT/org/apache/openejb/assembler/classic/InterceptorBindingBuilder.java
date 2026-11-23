package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.interceptor.InvocationContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.SetAccessible;

public class InterceptorBindingBuilder {
   public static final Logger logger;
   private final List<InterceptorBindingInfo> packageAndClassBindings;
   private final ArrayList<InterceptorBindingInfo> bindings;
   private final Map<String, InterceptorData> interceptors = new HashMap();

   public InterceptorBindingBuilder(ClassLoader cl, EjbJarInfo ejbJarInfo) throws OpenEJBException {
      this.bindings = new ArrayList(ejbJarInfo.interceptorBindings);
      Collections.sort(this.bindings, new IntercpetorBindingComparator());
      Collections.reverse(this.bindings);
      this.packageAndClassBindings = new ArrayList();

      for(InterceptorBindingInfo binding : this.bindings) {
         Level level = level(binding);
         if (level == InterceptorBindingBuilder.Level.PACKAGE || level == InterceptorBindingBuilder.Level.CLASS || level == InterceptorBindingBuilder.Level.ANNOTATION_CLASS) {
            this.packageAndClassBindings.add(binding);
         }
      }

      for(InterceptorInfo info : ejbJarInfo.interceptors) {
         Class<?> clazz = null;

         try {
            clazz = Class.forName(info.clazz, true, cl);
         } catch (ClassNotFoundException var7) {
            throw new OpenEJBException("Interceptor class cannot be loaded: " + info.clazz);
         }

         InterceptorData interceptor = new InterceptorData(clazz);
         this.toMethods(clazz, info.aroundInvoke, interceptor.getAroundInvoke());
         this.toMethods(clazz, info.postActivate, interceptor.getPostActivate());
         this.toMethods(clazz, info.prePassivate, interceptor.getPrePassivate());
         this.toMethods(clazz, info.postConstruct, interceptor.getPostConstruct());
         this.toMethods(clazz, info.preDestroy, interceptor.getPreDestroy());
         this.toMethods(clazz, info.afterBegin, interceptor.getAfterBegin());
         this.toMethods(clazz, info.beforeCompletion, interceptor.getBeforeCompletion());
         this.toMethods(clazz, info.afterCompletion, interceptor.getAfterCompletion());
         this.toMethods(clazz, info.aroundTimeout, interceptor.getAroundTimeout());
         this.interceptors.put(info.clazz, interceptor);
      }

   }

   public void build(BeanContext beanContext, EnterpriseBeanInfo beanInfo) {
      Class<?> clazz = beanContext.getBeanClass();
      InterceptorData beanAsInterceptor = new InterceptorData(clazz);
      if (beanInfo instanceof StatelessBeanInfo || beanInfo instanceof MessageDrivenBeanInfo) {
         NamedMethodInfo info = new NamedMethodInfo();
         info.className = clazz.getName();
         Method createMethod = beanContext.getCreateMethod();
         info.methodName = createMethod != null ? createMethod.getName() : "ejbCreate";
         info.methodParams = new ArrayList();

         try {
            Method ejbcreate = MethodInfoUtil.toMethod(clazz, info);
            if (ejbcreate != null) {
               CallbackInfo ejbcreateAsPostConstruct = new CallbackInfo();
               ejbcreateAsPostConstruct.className = ejbcreate.getDeclaringClass().getName();
               ejbcreateAsPostConstruct.method = ejbcreate.getName();
               beanInfo.postConstruct.add(ejbcreateAsPostConstruct);
            }
         } catch (IllegalStateException var10) {
         }
      }

      this.toMethods(clazz, beanInfo.aroundInvoke, beanAsInterceptor.getAroundInvoke());
      this.toCallback(clazz, beanInfo.postConstruct, beanAsInterceptor.getPostConstruct());
      this.toCallback(clazz, beanInfo.preDestroy, beanAsInterceptor.getPreDestroy());
      if (beanInfo instanceof StatefulBeanInfo) {
         StatefulBeanInfo stateful = (StatefulBeanInfo)beanInfo;
         this.toCallback(clazz, stateful.postActivate, beanAsInterceptor.getPostActivate());
         this.toCallback(clazz, stateful.prePassivate, beanAsInterceptor.getPrePassivate());
         this.toCallback(clazz, stateful.afterBegin, beanAsInterceptor.getAfterBegin());
         this.toCallback(clazz, stateful.beforeCompletion, beanAsInterceptor.getBeforeCompletion());
         this.toCallback(clazz, stateful.afterCompletion, beanAsInterceptor.getAfterCompletion(), Boolean.TYPE);
      } else {
         this.toMethods(clazz, beanInfo.aroundTimeout, beanAsInterceptor.getAroundTimeout());
      }

      while(clazz != null && clazz != Object.class) {
         for(Method method : clazz.getDeclaredMethods()) {
            List<InterceptorData> methodInterceptors = this.createInterceptorDatas(method, beanInfo.ejbName, this.bindings);
            beanContext.setMethodInterceptors(method, methodInterceptors);
            beanContext.getMethodContext(method).setSelfInterception(beanAsInterceptor);
         }

         clazz = clazz.getSuperclass();
      }

      List<InterceptorData> callbackInterceptorDatas = this.createInterceptorDatas((Method)null, beanInfo.ejbName, this.packageAndClassBindings);
      callbackInterceptorDatas.add(beanAsInterceptor);
      beanContext.setCallbackInterceptors(callbackInterceptorDatas);
   }

   private List<InterceptorData> createInterceptorDatas(Method method, String ejbName, List<InterceptorBindingInfo> bindings) {
      List<InterceptorBindingInfo> methodBindings = this.processBindings(method, ejbName, bindings);
      Collections.reverse(methodBindings);
      List<InterceptorData> methodInterceptors = new ArrayList();

      for(InterceptorBindingInfo info : methodBindings) {
         for(String interceptorClassName : info.interceptorOrder.size() > 0 ? info.interceptorOrder : info.interceptors) {
            InterceptorData interceptorData = (InterceptorData)this.interceptors.get(interceptorClassName);
            if (interceptorData == null) {
               logger.warning("InterceptorBinding references non-existent (undeclared) interceptor: " + interceptorClassName);
            } else {
               methodInterceptors.add(interceptorData);
            }
         }
      }

      return methodInterceptors;
   }

   private List<InterceptorBindingInfo> processBindings(Method method, String ejbName, List<InterceptorBindingInfo> bindings) {
      List<InterceptorBindingInfo> methodBindings = new ArrayList();
      Set<Level> excludes = new HashSet();

      for(InterceptorBindingInfo info : bindings) {
         Level level = level(info);
         if (this.implies(method, ejbName, level, info)) {
            Type type = type(level, info);
            if (type == InterceptorBindingBuilder.Type.EXPLICIT_ORDERING && !excludes.contains(level)) {
               methodBindings.add(info);
               return methodBindings;
            }

            if (type == InterceptorBindingBuilder.Type.SAME_AND_LOWER_EXCLUSION) {
               return methodBindings;
            }

            if (type == InterceptorBindingBuilder.Type.SAME_LEVEL_EXCLUSION) {
               excludes.add(level);
            }

            if (!excludes.contains(level)) {
               methodBindings.add(info);
            }

            if (info.excludeClassInterceptors) {
               excludes.add(InterceptorBindingBuilder.Level.CLASS);
               excludes.add(InterceptorBindingBuilder.Level.ANNOTATION_CLASS);
            }

            if (info.excludeDefaultInterceptors) {
               excludes.add(InterceptorBindingBuilder.Level.PACKAGE);
            }
         }
      }

      return methodBindings;
   }

   private boolean implies(Method method, String ejbName, Level level, InterceptorBindingInfo info) {
      if (level == InterceptorBindingBuilder.Level.PACKAGE) {
         return true;
      } else if (!ejbName.equals(info.ejbName)) {
         return false;
      } else if (level != InterceptorBindingBuilder.Level.CLASS && level != InterceptorBindingBuilder.Level.ANNOTATION_CLASS) {
         NamedMethodInfo methodInfo = info.method;
         return MethodInfoUtil.matches(method, methodInfo);
      } else {
         return true;
      }
   }

   private void toMethods(Class<?> clazz, List<CallbackInfo> callbackInfos, Set<Method> callbacks) {
      List<Method> methods = new ArrayList();

      for(CallbackInfo callbackInfo : callbackInfos) {
         try {
            Method method = this.getMethod(clazz, callbackInfo.method, InvocationContext.class);
            if (callbackInfo.className == null && method.getDeclaringClass().equals(clazz) && !methods.contains(method)) {
               methods.add(method);
            }

            if (method.getDeclaringClass().getName().equals(callbackInfo.className) && !methods.contains(method)) {
               methods.add(method);
            } else {
               Class<?> c;
               for(c = clazz; c != null && !c.getName().equals(callbackInfo.className); c = c.getSuperclass()) {
               }

               if (c != null) {
                  try {
                     method = this.getMethod(c, callbackInfo.method, InvocationContext.class);
                     if (Modifier.isPrivate(method.getModifiers()) && !methods.contains(method)) {
                        SetAccessible.on(method);
                        methods.add(method);
                     }
                  } catch (NoSuchMethodException var10) {
                  }
               }
            }
         } catch (NoSuchMethodException var11) {
            logger.warning("Interceptor method not found (skipping): public Object " + callbackInfo.method + "(InvocationContext); in class " + clazz.getName());
         }
      }

      Collections.sort(methods, new MethodCallbackComparator());
      callbacks.addAll(methods);
   }

   private void toCallback(Class<?> clazz, List<CallbackInfo> callbackInfos, Set<Method> callbacks, Class<?>... parameterTypes) {
      List<Method> methods = new ArrayList();

      for(CallbackInfo callbackInfo : callbackInfos) {
         Class<?> usedClazz = clazz;
         if (clazz.isInterface() && !callbackInfo.className.equals(clazz.getName())) {
            try {
               usedClazz = clazz.getClassLoader().loadClass(callbackInfo.className);
            } catch (ClassNotFoundException var12) {
            }
         }

         try {
            Method method = this.getMethod(usedClazz, callbackInfo.method, parameterTypes);
            if (callbackInfo.className == null && !methods.contains(method)) {
               methods.add(method);
            } else if (method.getDeclaringClass().getName().equals(callbackInfo.className) && !methods.contains(method)) {
               methods.add(method);
            } else {
               Class<?> c;
               for(c = clazz; c != null && !c.getName().equals(callbackInfo.className); c = c.getSuperclass()) {
               }

               if (c != null) {
                  try {
                     method = c.getDeclaredMethod(callbackInfo.method);
                     if (Modifier.isPrivate(method.getModifiers()) && !methods.contains(method)) {
                        SetAccessible.on(method);
                        methods.add(method);
                     }
                  } catch (NoSuchMethodException var13) {
                  }
               }
            }
         } catch (NoSuchMethodException e) {
            String message = "Bean Callback method not found (skipping): public void " + callbackInfo.method + "(); in class " + clazz.getName();
            logger.warning(message);
            throw new IllegalStateException(message, e);
         }
      }

      Collections.sort(methods, new MethodCallbackComparator());
      callbacks.addAll(methods);
   }

   private Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
      NoSuchMethodException original;
      for(original = null; clazz != null; clazz = clazz.getSuperclass()) {
         try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return (Method)SetAccessible.on(method);
         } catch (NoSuchMethodException e) {
            if (original == null) {
               original = e;
            }
         }
      }

      throw original;
   }

   private static Level level(InterceptorBindingInfo info) {
      if (info.ejbName.equals("*")) {
         return InterceptorBindingBuilder.Level.PACKAGE;
      } else if (info.method == null) {
         return info.className == null ? InterceptorBindingBuilder.Level.CLASS : InterceptorBindingBuilder.Level.ANNOTATION_CLASS;
      } else if (info.method.methodParams == null) {
         return InterceptorBindingBuilder.Level.OVERLOADED_METHOD;
      } else {
         return info.className == null ? InterceptorBindingBuilder.Level.EXACT_METHOD : InterceptorBindingBuilder.Level.ANNOTATION_METHOD;
      }
   }

   private static Type type(Level level, InterceptorBindingInfo info) {
      if (info.interceptorOrder.size() > 0) {
         return InterceptorBindingBuilder.Type.EXPLICIT_ORDERING;
      } else if ((level == InterceptorBindingBuilder.Level.CLASS || level == InterceptorBindingBuilder.Level.ANNOTATION_CLASS) && info.excludeClassInterceptors && info.excludeDefaultInterceptors) {
         return InterceptorBindingBuilder.Type.SAME_AND_LOWER_EXCLUSION;
      } else {
         return (level == InterceptorBindingBuilder.Level.CLASS || level == InterceptorBindingBuilder.Level.ANNOTATION_CLASS) && info.excludeClassInterceptors ? InterceptorBindingBuilder.Type.SAME_LEVEL_EXCLUSION : InterceptorBindingBuilder.Type.ADDITION_OR_LOWER_EXCLUSION;
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, InterceptorBindingBuilder.class.getPackage().getName());
   }

   public static enum Level {
      PACKAGE,
      ANNOTATION_CLASS,
      CLASS,
      ANNOTATION_METHOD,
      OVERLOADED_METHOD,
      EXACT_METHOD;
   }

   public static enum Type {
      ADDITION_OR_LOWER_EXCLUSION,
      SAME_LEVEL_EXCLUSION,
      SAME_AND_LOWER_EXCLUSION,
      EXPLICIT_ORDERING;
   }

   public static class IntercpetorBindingComparator implements Comparator<InterceptorBindingInfo> {
      public int compare(InterceptorBindingInfo a, InterceptorBindingInfo b) {
         Level levelA = InterceptorBindingBuilder.level(a);
         Level levelB = InterceptorBindingBuilder.level(b);
         return levelA != levelB ? levelA.ordinal() - levelB.ordinal() : InterceptorBindingBuilder.type(levelA, a).ordinal() - InterceptorBindingBuilder.type(levelB, b).ordinal();
      }
   }

   public static class MethodCallbackComparator implements Comparator<Method> {
      public int compare(Method m1, Method m2) {
         Class<?> c1 = m1.getDeclaringClass();
         Class<?> c2 = m2.getDeclaringClass();
         if (c1.equals(c2)) {
            return 0;
         } else {
            return c1.isAssignableFrom(c2) ? -1 : 1;
         }
      }
   }
}
