package org.apache.openejb.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Join {
   public static final MethodCallback METHOD_CALLBACK = new MethodCallback();
   public static final ClassCallback CLASS_CALLBACK = new ClassCallback();

   public static String join(String delimiter, Collection collection) {
      if (collection.size() == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();

         for(Object obj : collection) {
            sb.append(obj).append(delimiter);
         }

         return sb.substring(0, sb.length() - delimiter.length());
      }
   }

   public static String join(String delimiter, Object... collection) {
      if (collection.length == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();

         for(Object obj : collection) {
            sb.append(obj).append(delimiter);
         }

         return sb.substring(0, sb.length() - delimiter.length());
      }
   }

   public static <T> String join(String delimiter, NameCallback<T> nameCallback, T... collection) {
      if (collection.length == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();

         for(T obj : collection) {
            sb.append(nameCallback.getName(obj)).append(delimiter);
         }

         return sb.substring(0, sb.length() - delimiter.length());
      }
   }

   public static <T> String join(String delimiter, NameCallback<T> nameCallback, Collection<T> collection) {
      if (collection.size() == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();

         for(T obj : collection) {
            sb.append(nameCallback.getName(obj)).append(delimiter);
         }

         return sb.substring(0, sb.length() - delimiter.length());
      }
   }

   public static <T> List<String> strings(Collection<T> collection, NameCallback<T> callback) {
      List<String> list = new ArrayList();

      for(T t : collection) {
         String name = callback.getName(t);
         list.add(name);
      }

      return list;
   }

   public static class FileCallback implements NameCallback<File> {
      public String getName(File file) {
         return file.getName();
      }
   }

   public static class MethodCallback implements NameCallback<Method> {
      public String getName(Method method) {
         return method.getName();
      }
   }

   public static class ClassCallback implements NameCallback<Class<?>> {
      public String getName(Class<?> cls) {
         return cls.getName();
      }
   }

   public interface NameCallback<T> {
      String getName(T var1);
   }
}
