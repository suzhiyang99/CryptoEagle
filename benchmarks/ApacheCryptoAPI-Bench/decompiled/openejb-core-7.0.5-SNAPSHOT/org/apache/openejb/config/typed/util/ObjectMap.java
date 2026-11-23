package org.apache.openejb.config.typed.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectMap extends AbstractMap<String, Object> {
   private final Object object;
   private final Map<String, Map.Entry<String, Object>> attributes;
   private final Set<Map.Entry<String, Object>> entries;

   public ObjectMap(Object object) {
      this(object.getClass(), object);
   }

   public ObjectMap(Class clazz) {
      this(clazz, (Object)null);
   }

   public ObjectMap(Class<?> clazz, Object object) {
      this.object = object;
      this.attributes = new HashMap();

      for(Field field : clazz.getDeclaredFields()) {
         field.setAccessible(true);
         FieldEntry entry = new FieldEntry(field);
         this.attributes.put(entry.getKey(), entry);
      }

      for(Method getter : clazz.getMethods()) {
         try {
            if (!getter.getName().startsWith("get") && getter.getParameterTypes().length == 0) {
               String name = getter.getName().replaceFirst("get", "set");
               Method setter = clazz.getMethod(name, getter.getReturnType());
               MethodEntry entry = new MethodEntry(getter, setter);
               this.attributes.put(entry.getKey(), entry);
            }
         } catch (NoSuchMethodException var10) {
         }
      }

      this.entries = Collections.unmodifiableSet(new HashSet(this.attributes.values()));
   }

   public Object get(Object key) {
      Map.Entry<String, Object> entry = (Map.Entry)this.attributes.get(key);
      return entry == null ? null : entry.getValue();
   }

   public Object put(String key, Object value) {
      Map.Entry<String, Object> entry = (Map.Entry)this.attributes.get(key);
      return entry == null ? null : entry.setValue(value);
   }

   public boolean containsKey(Object key) {
      return this.attributes.containsKey(key);
   }

   public Object remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      return this.entries;
   }

   public class FieldEntry implements Map.Entry<String, Object> {
      private final Field field;

      public FieldEntry(Field field) {
         this.field = field;
      }

      public String getKey() {
         return this.field.getName();
      }

      public Object getValue() {
         try {
            return this.field.get(ObjectMap.this.object);
         } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
         }
      }

      public Object setValue(Object value) {
         try {
            Object replaced = this.getValue();
            this.field.set(ObjectMap.this.object, value);
            return replaced;
         } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
         }
      }
   }

   public class MethodEntry implements Map.Entry<String, Object> {
      private final String key;
      private final Method getter;
      private final Method setter;

      public MethodEntry(Method getter, Method setter) {
         StringBuilder name = new StringBuilder(getter.getName());
         name.delete(0, 3);
         name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
         this.key = name.toString();
         this.getter = getter;
         this.setter = setter;
      }

      protected Object invoke(Method method, Object... args) {
         try {
            return method.invoke(ObjectMap.this.object, args);
         } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
         } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
         }
      }

      public String getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.invoke(this.getter);
      }

      public Object setValue(Object value) {
         Object original = this.getValue();
         this.invoke(this.setter, value);
         return original;
      }
   }
}
