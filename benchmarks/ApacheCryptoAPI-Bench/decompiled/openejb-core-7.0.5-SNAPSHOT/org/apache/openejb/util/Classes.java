package org.apache.openejb.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classes {
   private static final Map<Class<?>, Class<?>> primitiveWrappers = new HashMap();
   private static final HashMap<String, Class> primitives = new HashMap();

   public static Class forName(String string, ClassLoader classLoader) throws ClassNotFoundException {
      int arrayDimentions;
      for(arrayDimentions = 0; string.endsWith("[]"); ++arrayDimentions) {
         string = string.substring(0, string.length() - 2);
      }

      Class clazz = (Class)primitives.get(string);
      if (clazz == null) {
         clazz = Class.forName(string, true, classLoader);
      }

      return arrayDimentions == 0 ? clazz : Array.newInstance(clazz, new int[arrayDimentions]).getClass();
   }

   public static String packageName(Class clazz) {
      return packageName(clazz.getName());
   }

   public static String packageName(String clazzName) {
      int i = clazzName.lastIndexOf(46);
      return i > 0 ? clazzName.substring(0, i) : "";
   }

   public static List<String> getSimpleNames(Class... classes) {
      List<String> list = new ArrayList();

      for(Class aClass : classes) {
         list.add(aClass.getSimpleName());
      }

      return list;
   }

   public static Class<?> deprimitivize(Class<?> fieldType) {
      return fieldType.isPrimitive() ? (Class)primitiveWrappers.get(fieldType) : fieldType;
   }

   public static List<Class<?>> ancestors(Class clazz) {
      ArrayList<Class<?>> ancestors;
      for(ancestors = new ArrayList(); clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
         ancestors.add(clazz);
      }

      return ancestors;
   }

   static {
      primitives.put("boolean", Boolean.TYPE);
      primitives.put("byte", Byte.TYPE);
      primitives.put("char", Character.TYPE);
      primitives.put("short", Short.TYPE);
      primitives.put("int", Integer.TYPE);
      primitives.put("long", Long.TYPE);
      primitives.put("float", Float.TYPE);
      primitives.put("double", Double.TYPE);
      primitiveWrappers.put(Boolean.TYPE, Boolean.class);
      primitiveWrappers.put(Byte.TYPE, Byte.class);
      primitiveWrappers.put(Character.TYPE, Character.class);
      primitiveWrappers.put(Double.TYPE, Double.class);
      primitiveWrappers.put(Float.TYPE, Float.class);
      primitiveWrappers.put(Integer.TYPE, Integer.class);
      primitiveWrappers.put(Long.TYPE, Long.class);
      primitiveWrappers.put(Short.TYPE, Short.class);
   }
}
