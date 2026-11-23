package org.apache.openejb.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.openejb.OpenEJBRuntimeException;

public final class Reflections {
   private Reflections() {
   }

   public static Method findMethod(String name, Class<?> type, Class<?>... args) {
      try {
         return type.getMethod(name, args);
      } catch (NoSuchMethodException var4) {
         throw new IllegalArgumentException("Can't find public method " + name + " in " + type.getName());
      }
   }

   public static Object invokeByReflection(Object obj, String mtdName, Class<?>[] paramTypes, Object[] args) {
      for(Class<?> clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
         boolean acc = true;
         Method mtd = null;

         try {
            mtd = clazz.getDeclaredMethod(mtdName, paramTypes);
            acc = mtd.isAccessible();
            mtd.setAccessible(true);
            Object var7 = mtd.invoke(obj, args);
            return var7;
         } catch (NoSuchMethodException var12) {
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         } finally {
            if (mtd != null) {
               mtd.setAccessible(acc);
            }

         }
      }

      throw new IllegalArgumentException(new NoSuchMethodException(obj.getClass().getName() + " ." + mtdName));
   }

   public static void set(Object instance, String field, Object value) {
      set(instance.getClass(), instance, field, value);
   }

   public static void set(Class<?> inClazz, Object instance, String field, Object value) {
      Class<?> clazz = inClazz;

      while(clazz != null) {
         try {
            Field f = clazz.getDeclaredField(field);
            boolean acc = f.isAccessible();
            f.setAccessible(true);
            Field modifiersField = null;
            int modifiers = f.getModifiers();
            boolean isFinal = Modifier.isFinal(modifiers);

            try {
               if (isFinal) {
                  modifiersField = Field.class.getDeclaredField("modifiers");
                  modifiersField.setAccessible(true);
                  modifiersField.setInt(f, modifiers & -17);
               }

               f.set(instance, value);
            } finally {
               if (isFinal && modifiersField != null) {
                  modifiersField.setInt(f, modifiers);
               }

               f.setAccessible(acc);
            }

            return;
         } catch (NoSuchFieldException var15) {
            clazz = clazz.getSuperclass();
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         }
      }

   }

   public static Object get(Object instance, String field) {
      return get(instance.getClass(), instance, field);
   }

   public static Object get(Class<?> aClass, Object instance, String field) {
      Class<?> clazz = aClass;

      while(clazz != null) {
         try {
            Field f = clazz.getDeclaredField(field);
            if (!f.isAccessible()) {
               f.setAccessible(true);
            }

            return f.get(instance);
         } catch (NoSuchFieldException var5) {
            clazz = clazz.getSuperclass();
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         }
      }

      throw new OpenEJBRuntimeException(new NoSuchFieldException(field));
   }
}
