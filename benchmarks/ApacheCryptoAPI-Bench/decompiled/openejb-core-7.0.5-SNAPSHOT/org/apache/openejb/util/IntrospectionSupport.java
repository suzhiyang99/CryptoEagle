package org.apache.openejb.util;

import java.beans.Introspector;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IntrospectionSupport {
   public static boolean getProperties(Object target, Map<String, String> props, String optionPrefix) {
      boolean rc = false;
      if (target == null) {
         throw new IllegalArgumentException("target was null.");
      } else if (props == null) {
         throw new IllegalArgumentException("props was null.");
      } else {
         if (optionPrefix == null) {
            optionPrefix = "";
         }

         Class clazz = target.getClass();
         Method[] methods = clazz.getMethods();

         for(int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            String name = method.getName();
            Class type = method.getReturnType();
            Class[] params = method.getParameterTypes();
            if (name.startsWith("get") && params.length == 0 && type != null && isSettableType(type)) {
               try {
                  Object value = method.invoke(target);
                  if (value != null) {
                     String strValue = convertToString(value, type);
                     if (strValue != null) {
                        name = name.substring(3, 4).toLowerCase() + name.substring(4);
                        props.put(optionPrefix + name, strValue);
                        rc = true;
                     }
                  }
               } catch (Throwable var13) {
               }
            }
         }

         return rc;
      }
   }

   public static boolean setProperties(Object target, Map props, String optionPrefix) {
      boolean rc = false;
      if (target == null) {
         throw new IllegalArgumentException("target was null.");
      } else if (props == null) {
         throw new IllegalArgumentException("props was null.");
      } else {
         Iterator iter = props.keySet().iterator();

         while(iter.hasNext()) {
            String name = (String)iter.next();
            if (name.startsWith(optionPrefix)) {
               Object value = props.get(name);
               name = name.substring(optionPrefix.length());
               if (setProperty(target, name, value)) {
                  iter.remove();
                  rc = true;
               }
            }
         }

         return rc;
      }
   }

   public static Map extractProperties(Map props, String optionPrefix) {
      if (props == null) {
         throw new IllegalArgumentException("props was null.");
      } else {
         HashMap<String, Object> rc = new HashMap(props.size());
         Iterator iter = props.keySet().iterator();

         while(iter.hasNext()) {
            String name = (String)iter.next();
            if (name.startsWith(optionPrefix)) {
               Object value = props.get(name);
               name = name.substring(optionPrefix.length());
               rc.put(name, value);
               iter.remove();
            }
         }

         return rc;
      }
   }

   public static boolean setProperties(Object target, Map props) {
      boolean rc = false;
      if (target == null) {
         throw new IllegalArgumentException("target was null.");
      } else if (props == null) {
         throw new IllegalArgumentException("props was null.");
      } else {
         Iterator iter = props.entrySet().iterator();

         while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (setProperty(target, (String)entry.getKey(), entry.getValue())) {
               iter.remove();
               rc = true;
            }
         }

         return rc;
      }
   }

   private static boolean setProperty(Object target, String name, Object value) {
      try {
         Class clazz = target.getClass();
         Method setter = findSetterMethod(clazz, name);
         if (setter == null) {
            return false;
         } else {
            if (value != null && value.getClass() != setter.getParameterTypes()[0]) {
               setter.invoke(target, convert(value, setter.getParameterTypes()[0]));
            } else {
               setter.invoke(target, value);
            }

            return true;
         }
      } catch (Throwable var5) {
         return false;
      }
   }

   private static Object convert(Object value, Class type) throws URISyntaxException {
      PropertyEditor editor = PropertyEditorManager.findEditor(type);
      if (editor != null) {
         editor.setAsText(value.toString());
         return editor.getValue();
      } else {
         return type == URI.class ? URLs.uri(value.toString()) : null;
      }
   }

   private static String convertToString(Object value, Class type) throws URISyntaxException {
      PropertyEditor editor = PropertyEditorManager.findEditor(type);
      if (editor != null) {
         editor.setValue(value);
         return editor.getAsText();
      } else {
         return type == URI.class ? ((URI)value).toString() : null;
      }
   }

   private static Method findSetterMethod(Class clazz, String name) {
      name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
      Method[] methods = clazz.getMethods();

      for(int i = 0; i < methods.length; ++i) {
         Method method = methods[i];
         Class[] params = method.getParameterTypes();
         if (method.getName().equals(name) && params.length == 1 && isSettableType(params[0])) {
            return method;
         }
      }

      return null;
   }

   private static boolean isSettableType(Class clazz) {
      if (PropertyEditorManager.findEditor(clazz) != null) {
         return true;
      } else if (clazz == URI.class) {
         return true;
      } else {
         return clazz == Boolean.class;
      }
   }

   public static String toString(Object target) {
      return toString(target, Object.class);
   }

   public static String toString(Object target, Class stopClass) {
      LinkedHashMap<String, Object> map = new LinkedHashMap();
      addFields(target, target.getClass(), stopClass, map);
      StringBuilder buffer = new StringBuilder(simpleName(target.getClass()));
      buffer.append(" {");
      Set entrySet = map.entrySet();
      boolean first = true;

      for(Map.Entry entry : entrySet) {
         if (first) {
            first = false;
         } else {
            buffer.append(", ");
         }

         buffer.append(entry.getKey());
         buffer.append(" = ");
         appendToString(buffer, entry.getValue());
      }

      buffer.append("}");
      return buffer.toString();
   }

   protected static void appendToString(StringBuilder buffer, Object value) {
      buffer.append(value);
   }

   public static String simpleName(Class clazz) {
      String name = clazz.getName();
      int p = name.lastIndexOf(".");
      if (p >= 0) {
         name = name.substring(p + 1);
      }

      return name;
   }

   private static void addFields(Object target, Class startClass, Class stopClass, LinkedHashMap<String, Object> map) {
      if (startClass != stopClass) {
         addFields(target, startClass.getSuperclass(), stopClass, map);
      }

      Field[] fields = startClass.getDeclaredFields();

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers())) {
            try {
               field.setAccessible(true);
               Object o = field.get(target);
               if (o != null && o.getClass().isArray()) {
                  try {
                     o = Arrays.asList(o);
                  } catch (Throwable var9) {
                  }
               }

               map.put(field.getName(), o);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }
      }

   }

   public static Class getPropertyType(Class clazz, String propertyName) throws NoSuchFieldException {
      while(true) {
         try {
            return clazz.getDeclaredField(propertyName).getType();
         } catch (NoSuchFieldException var8) {
            for(Method method : clazz.getDeclaredMethods()) {
               if (method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 1) {
                  String methodName = method.getName();
                  if (methodName.startsWith("set")) {
                     String type = Introspector.decapitalize(methodName.substring(3));
                     if (propertyName.equals(type)) {
                        return method.getParameterTypes()[0];
                     }
                  }
               }
            }

            clazz = clazz.getSuperclass();
            if (clazz == null) {
               throw new NoSuchFieldException(propertyName);
            }
         }
      }
   }
}
