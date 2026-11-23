package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import org.apache.openejb.BeanContext;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.SetAccessible;

public class MethodInfoUtil {
   public static Method toMethod(Class clazz, NamedMethodInfo info) {
      List<Class> parameterTypes = new ArrayList();
      if (info.methodParams != null) {
         for(String paramType : info.methodParams) {
            try {
               parameterTypes.add(Classes.forName(paramType, clazz.getClassLoader()));
            } catch (ClassNotFoundException cnfe) {
               throw new IllegalStateException("Parameter class could not be loaded for type " + paramType, cnfe);
            }
         }
      }

      Class[] parameters = (Class[])parameterTypes.toArray(new Class[parameterTypes.size()]);

      IllegalStateException noSuchMethod;
      for(noSuchMethod = null; clazz != null; clazz = clazz.getSuperclass()) {
         try {
            Method method = clazz.getDeclaredMethod(info.methodName, parameters);
            return (Method)SetAccessible.on(method);
         } catch (NoSuchMethodException e) {
            if (noSuchMethod == null) {
               noSuchMethod = new IllegalStateException("Callback method does not exist: " + clazz.getName() + "." + info.methodName, e);
            }
         }
      }

      throw noSuchMethod;
   }

   public static List<Method> matchingMethods(Method signature, Class clazz) {
      List<Method> list = new ArrayList();

      label32:
      for(Method method : clazz.getMethods()) {
         if (method.getName().equals(signature.getName())) {
            Class<?>[] methodTypes = method.getParameterTypes();
            Class<?>[] signatureTypes = signature.getParameterTypes();
            if (methodTypes.length == signatureTypes.length) {
               for(int i = 0; i < methodTypes.length; ++i) {
                  if (!methodTypes[i].equals(signatureTypes[i])) {
                     continue label32;
                  }
               }

               list.add(method);
            }
         }
      }

      return list;
   }

   public static List<Method> matchingMethods(MethodInfo mi, Class clazz) {
      Method[] methods = clazz.getMethods();
      return matchingMethods(mi, methods);
   }

   public static List<Method> matchingMethods(MethodInfo mi, Method[] methods) {
      List<Method> filtered = filterByLevel(mi, methods);
      filtered = filterByView(mi, filtered);
      return filtered;
   }

   private static List<Method> filterByView(MethodInfo mi, List<Method> filtered) {
      View view = view(mi);
      switch (view) {
         case CLASS:
            return filterByClass(mi, filtered);
         default:
            return filtered;
      }
   }

   private static List<Method> filterByClass(MethodInfo mi, List<Method> methods) {
      ArrayList<Method> list = new ArrayList();

      for(Method method : methods) {
         String className = method.getDeclaringClass().getName();
         if (mi.className.equals(className)) {
            list.add(method);
         }
      }

      return list;
   }

   private static List<Method> filterByLevel(MethodInfo mi, Method[] methods) {
      Level level = level(mi);
      switch (level) {
         case BEAN:
         case PACKAGE:
            return Arrays.asList(methods);
         case OVERLOADED_METHOD:
            return filterByName(methods, mi.methodName);
         case EXACT_METHOD:
            return filterByNameAndParams(methods, mi);
         default:
            return Collections.EMPTY_LIST;
      }
   }

   public static Method getMethod(Class clazz, MethodInfo info) {
      ClassLoader cl = clazz.getClassLoader();
      List<Class> params = new ArrayList();

      for(String methodParam : info.methodParams) {
         try {
            params.add(getClassForParam(methodParam, cl));
         } catch (ClassNotFoundException var8) {
         }
      }

      Method method = null;

      try {
         method = clazz.getMethod(info.methodName, (Class[])params.toArray(new Class[params.size()]));
      } catch (NoSuchMethodException var7) {
         return null;
      }

      return !info.className.equals("*") && !method.getDeclaringClass().getName().equals(info.className) ? null : method;
   }

   private static List<Method> filterByName(Method[] methods, String methodName) {
      List<Method> list = new ArrayList();

      for(Method method : methods) {
         if (method.getName().equals(methodName)) {
            list.add(method);
         }
      }

      return list;
   }

   private static List<Method> filterByNameAndParams(Method[] methods, MethodInfo mi) {
      List<Method> list = new ArrayList();

      for(Method method : methods) {
         if (matches(method, mi)) {
            list.add(method);
         }
      }

      return list;
   }

   public static List<MethodPermissionInfo> normalizeMethodPermissionInfos(List<MethodPermissionInfo> infos) {
      List<MethodPermissionInfo> normalized = new ArrayList();

      for(MethodPermissionInfo oldInfo : infos) {
         for(MethodInfo methodInfo : oldInfo.methods) {
            MethodPermissionInfo newInfo = new MethodPermissionInfo();
            newInfo.description = oldInfo.description;
            newInfo.methods.add(methodInfo);
            newInfo.roleNames.addAll(oldInfo.roleNames);
            newInfo.unchecked = oldInfo.unchecked;
            newInfo.excluded = oldInfo.excluded;
            normalized.add(newInfo);
         }
      }

      Collections.sort(normalized, new MethodPermissionComparator());
      return normalized;
   }

   private static Class getClassForParam(String className, ClassLoader cl) throws ClassNotFoundException {
      if (className.equals("int")) {
         return Integer.TYPE;
      } else if (className.equals("double")) {
         return Double.TYPE;
      } else if (className.equals("long")) {
         return Long.TYPE;
      } else if (className.equals("boolean")) {
         return Boolean.TYPE;
      } else if (className.equals("float")) {
         return Float.TYPE;
      } else if (className.equals("char")) {
         return Character.TYPE;
      } else if (className.equals("short")) {
         return Short.TYPE;
      } else {
         return className.equals("byte") ? Byte.TYPE : Class.forName(className, false, cl);
      }
   }

   public static Map<Method, MethodAttributeInfo> resolveAttributes(List<? extends MethodAttributeInfo> infos, BeanContext beanContext) {
      Map<Method, MethodAttributeInfo> attributes = new LinkedHashMap();
      Method[] wildCardView = (Method[])getWildCardView(beanContext).toArray(new Method[0]);

      for(MethodAttributeInfo attributeInfo : infos) {
         for(MethodInfo methodInfo : attributeInfo.methods) {
            if (methodInfo.ejbName == null || methodInfo.ejbName.equals("*") || methodInfo.ejbName.equals(beanContext.getEjbName())) {
               List<Method> methods = new ArrayList();
               if (methodInfo.methodIntf == null) {
                  methods.addAll(matchingMethods(methodInfo, wildCardView));
               } else if (methodInfo.methodIntf.equals("Home")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getHomeInterface()));
               } else if (methodInfo.methodIntf.equals("Remote")) {
                  if (beanContext.getRemoteInterface() != null) {
                     methods.addAll(matchingMethods(methodInfo, beanContext.getRemoteInterface()));
                  }

                  for(Class intf : beanContext.getBusinessRemoteInterfaces()) {
                     methods.addAll(matchingMethods(methodInfo, intf));
                  }
               } else if (methodInfo.methodIntf.equals("LocalHome")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getLocalHomeInterface()));
               } else if (methodInfo.methodIntf.equals("Local")) {
                  if (beanContext.getLocalInterface() != null) {
                     methods.addAll(matchingMethods(methodInfo, beanContext.getLocalInterface()));
                  }

                  for(Class intf : beanContext.getBusinessRemoteInterfaces()) {
                     methods.addAll(matchingMethods(methodInfo, intf));
                  }
               } else if (methodInfo.methodIntf.equals("ServiceEndpoint")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getServiceEndpointInterface()));
               }

               for(Method method : methods) {
                  if (!containerMethod(method)) {
                     attributes.put(method, attributeInfo);
                  }
               }
            }
         }
      }

      return attributes;
   }

   public static Map<ViewMethod, MethodAttributeInfo> resolveViewAttributes(List<? extends MethodAttributeInfo> infos, BeanContext beanContext) {
      Map<ViewMethod, MethodAttributeInfo> attributes = new LinkedHashMap();
      Method[] wildCardView = (Method[])getWildCardView(beanContext).toArray(new Method[0]);

      for(MethodAttributeInfo attributeInfo : infos) {
         for(MethodInfo methodInfo : attributeInfo.methods) {
            if (methodInfo.ejbName == null || methodInfo.ejbName.equals("*") || methodInfo.ejbName.equals(beanContext.getEjbName())) {
               List<Method> methods = new ArrayList();
               if (methodInfo.methodIntf == null) {
                  methods.addAll(matchingMethods(methodInfo, wildCardView));
               } else if (methodInfo.methodIntf.equals("Home")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getHomeInterface()));
               } else if (methodInfo.methodIntf.equals("Remote")) {
                  if (beanContext.getRemoteInterface() != null) {
                     methods.addAll(matchingMethods(methodInfo, beanContext.getRemoteInterface()));
                  }

                  for(Class intf : beanContext.getBusinessRemoteInterfaces()) {
                     methods.addAll(matchingMethods(methodInfo, intf));
                  }
               } else if (methodInfo.methodIntf.equals("LocalHome")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getLocalHomeInterface()));
               } else if (methodInfo.methodIntf.equals("Local")) {
                  if (beanContext.getLocalInterface() != null) {
                     methods.addAll(matchingMethods(methodInfo, beanContext.getLocalInterface()));
                  }

                  for(Class intf : beanContext.getBusinessRemoteInterfaces()) {
                     methods.addAll(matchingMethods(methodInfo, intf));
                  }
               } else if (methodInfo.methodIntf.equals("ServiceEndpoint")) {
                  methods.addAll(matchingMethods(methodInfo, beanContext.getServiceEndpointInterface()));
               }

               for(Method method : methods) {
                  if (!containerMethod(method)) {
                     ViewMethod viewMethod = new ViewMethod(methodInfo.methodIntf, method);
                     attributes.put(viewMethod, attributeInfo);
                  }
               }
            }
         }
      }

      return attributes;
   }

   private static boolean containerMethod(Method method) {
      return (method.getDeclaringClass() == EJBObject.class || method.getDeclaringClass() == EJBHome.class || method.getDeclaringClass() == EJBLocalObject.class || method.getDeclaringClass() == EJBLocalHome.class) && !method.getName().equals("remove");
   }

   private static List<Method> getWildCardView(BeanContext info) {
      List<Method> methods = new ArrayList();
      List<Method> beanMethods = Arrays.asList(info.getBeanClass().getMethods());
      methods.addAll(beanMethods);
      if (info.getRemoteInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getRemoteInterface().getMethods()));
      }

      if (info.getHomeInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getHomeInterface().getMethods()));
      }

      if (info.getLocalInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getLocalInterface().getMethods()));
      }

      if (info.getLocalHomeInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getLocalHomeInterface().getMethods()));
      }

      if (info.getMdbInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getMdbInterface().getMethods()));
      }

      if (info.getServiceEndpointInterface() != null) {
         methods.addAll(exclude(beanMethods, info.getServiceEndpointInterface().getMethods()));
      }

      for(Class intf : info.getBusinessRemoteInterfaces()) {
         methods.addAll(exclude(beanMethods, intf.getMethods()));
      }

      for(Class intf : info.getBusinessLocalInterfaces()) {
         methods.addAll(exclude(beanMethods, intf.getMethods()));
      }

      Iterator<Method> iterator = methods.iterator();

      while(iterator.hasNext()) {
         Method method = (Method)iterator.next();
         if (containerMethod(method)) {
            iterator.remove();
         }
      }

      return methods;
   }

   private static List<Method> exclude(List<Method> excludes, Method[] methods) {
      ArrayList<Method> list = new ArrayList();

      for(Method method : methods) {
         if (!matches(excludes, method)) {
            list.add(method);
         }
      }

      return list;
   }

   private static boolean matches(List<Method> excludes, Method method) {
      for(Method excluded : excludes) {
         boolean match = match(method, excluded);
         if (match) {
            return true;
         }
      }

      return false;
   }

   public static boolean match(Method methodA, Method methodB) {
      if (!methodA.getName().equals(methodB.getName())) {
         return false;
      } else if (methodA.getParameterTypes().length != methodB.getParameterTypes().length) {
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

   public static boolean matches(Method method, MethodInfo methodInfo) {
      return matches(method, methodInfo.methodName, methodInfo.methodParams);
   }

   public static boolean matches(Method method, NamedMethodInfo methodInfo) {
      return matches(method, methodInfo.methodName, methodInfo.methodParams);
   }

   public static boolean matches(Method method, String methodName, List<String> methodParams) {
      if (!methodName.equals(method.getName())) {
         return false;
      } else if (methodParams == null) {
         return true;
      } else if (methodParams.size() != method.getParameterTypes().length) {
         return false;
      } else {
         Class<?>[] parameterTypes = method.getParameterTypes();

         for(int i = 0; i < parameterTypes.length; ++i) {
            Class<?> parameterType = parameterTypes[i];
            String methodParam = (String)methodParams.get(i);
            if (!methodParam.equals(getName(parameterType)) && !methodParam.equals(parameterType.getName())) {
               return false;
            }
         }

         return true;
      }
   }

   private static String getName(Class<?> type) {
      return type.isArray() ? getName(type.getComponentType()) + "[]" : type.getName();
   }

   public static View view(MethodInfo methodInfo) {
      if (methodInfo.className != null && !methodInfo.className.equals("*")) {
         return MethodInfoUtil.View.CLASS;
      } else {
         return methodInfo.methodIntf != null && !methodInfo.methodIntf.equals("*") ? MethodInfoUtil.View.INTERFACE : MethodInfoUtil.View.ANY;
      }
   }

   public static Level level(MethodInfo methodInfo) {
      if (methodInfo.ejbName != null && methodInfo.ejbName.equals("*")) {
         return MethodInfoUtil.Level.PACKAGE;
      } else if (methodInfo.methodName.equals("*")) {
         return MethodInfoUtil.Level.BEAN;
      } else {
         return methodInfo.methodParams == null ? MethodInfoUtil.Level.OVERLOADED_METHOD : MethodInfoUtil.Level.EXACT_METHOD;
      }
   }

   public static String toString(MethodInfo i) {
      String s = i.ejbName;
      s = s + " : ";
      s = s + (i.methodIntf == null ? "*" : i.methodIntf);
      s = s + " : ";
      s = s + i.className;
      s = s + " : ";
      s = s + i.methodName;
      s = s + "(";
      if (i.methodParams != null) {
         s = s + Join.join(", ", (Collection)i.methodParams);
      } else {
         s = s + "*";
      }

      s = s + ")";
      return s;
   }

   public static String toString(MethodPermissionInfo i) {
      String s = toString((MethodInfo)i.methods.get(0));
      if (i.unchecked) {
         s = s + " Unchecked";
      } else if (i.excluded) {
         s = s + " Excluded";
      } else {
         s = s + " " + Join.join(", ", (Collection)i.roleNames);
      }

      return s;
   }

   public static String toString(MethodTransactionInfo i) {
      String s = toString((MethodInfo)i.methods.get(0));
      s = s + " " + i.transAttribute;
      return s;
   }

   public static String toString(MethodConcurrencyInfo i) {
      String s = toString((MethodInfo)i.methods.get(0));
      s = s + " " + i.concurrencyAttribute;
      return s;
   }

   public static class ViewMethod {
      private final String view;
      private final Method method;

      public ViewMethod(String view, Method method) {
         this.view = view;
         this.method = method;
      }

      public String getView() {
         return this.view;
      }

      public Method getMethod() {
         return this.method;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ViewMethod that = (ViewMethod)o;
            if (!this.method.equals(that.method)) {
               return false;
            } else {
               if (this.view != null) {
                  if (!this.view.equals(that.view)) {
                     return false;
                  }
               } else if (that.view != null) {
                  return false;
               }

               return true;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.view != null ? this.view.hashCode() : 0;
         result = 31 * result + this.method.hashCode();
         return result;
      }

      public String toString() {
         return String.format("%s : %s(%s)", this.view, this.method.getName(), Join.join(", ", (Collection)Classes.getSimpleNames(this.method.getParameterTypes())));
      }
   }

   public static enum Level {
      PACKAGE,
      BEAN,
      OVERLOADED_METHOD,
      EXACT_METHOD;
   }

   public static enum View {
      CLASS,
      ANY,
      INTERFACE;
   }

   public static class MethodPermissionComparator extends BaseComparator<MethodPermissionInfo> {
      public int compare(MethodPermissionInfo a, MethodPermissionInfo b) {
         return this.compare((MethodInfo)((MethodInfo)a.methods.get(0)), (MethodInfo)((MethodInfo)b.methods.get(0)));
      }
   }

   public abstract static class BaseComparator<T> implements Comparator<T> {
      public int compare(MethodInfo am, MethodInfo bm) {
         Level levelA = MethodInfoUtil.level(am);
         Level levelB = MethodInfoUtil.level(bm);
         return levelA != levelB ? levelA.ordinal() - levelB.ordinal() : MethodInfoUtil.view(am).ordinal() - MethodInfoUtil.view(bm).ordinal();
      }
   }
}
