package org.apache.openejb.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class PojoSerialization implements Serializable {
   private static final byte CLASS = 50;
   private static final byte FIELD = 51;
   private static final byte DONE = 52;
   private Object object;
   private static final Object unsafe;
   private static final Method allocateInstance;
   private static final Method objectFieldOffset;
   private static final Method putInt;
   private static final Method putLong;
   private static final Method putShort;
   private static final Method putChar;
   private static final Method putByte;
   private static final Method putFloat;
   private static final Method putDouble;
   private static final Method putBoolean;
   private static final Method putObject;

   public PojoSerialization() {
   }

   public PojoSerialization(Object object) {
      this.object = object;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      this.write(out);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.read(in);
   }

   protected Object readResolve() throws ObjectStreamException {
      return this.object;
   }

   protected void read(ObjectInput in) throws IOException, ClassNotFoundException {
      byte b = in.readByte();
      if (b != 50) {
         throw new IOException("Expected 'CLASS' byte 50, got: " + b);
      } else {
         Class clazz = (Class)in.readObject();

         Object object;
         try {
            object = allocateInstance.invoke(unsafe, clazz);
         } catch (Exception e) {
            throw (IOException)(new IOException("Cannot construct " + clazz.getName())).initCause(e);
         }

         while(b != 52) {
            b = in.readByte();
            switch (b) {
               case 50:
                  clazz = (Class)in.readObject();
                  break;
               case 51:
                  String fieldName = in.readUTF();
                  Object value = in.readObject();
                  Field field = null;

                  try {
                     field = clazz.getDeclaredField(fieldName);
                  } catch (NoSuchFieldException e) {
                     throw (IOException)(new IOException("Cannot find field " + fieldName)).initCause(e);
                  }

                  this.setValue(field, object, value);
            }
         }

         this.object = object;
      }
   }

   protected void write(ObjectOutput out) throws IOException {
      List<Class> classes = new ArrayList();

      for(Class c = this.object.getClass(); c != null && !c.equals(Object.class); c = c.getSuperclass()) {
         classes.add(c);
      }

      for(Class clazz : classes) {
         out.writeByte(50);
         out.writeObject(clazz);
         Field[] fields = clazz.getDeclaredFields();

         for(Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
               field.setAccessible(true);
               out.writeByte(51);
               out.writeUTF(field.getName());

               try {
                  out.writeObject(field.get(this.object));
               } catch (IllegalAccessException e) {
                  throw (IOException)(new IOException("Cannot write field " + field.getName())).initCause(e);
               }
            }
         }
      }

      out.writeByte(52);
   }

   private void setValue(Field field, Object object, Object value) {
      long offset;
      try {
         offset = (Long)objectFieldOffset.invoke(unsafe, field);
      } catch (Exception e) {
         throw new IllegalStateException("Failed getting offset for: field=" + field.getName() + "  class=" + field.getDeclaringClass().getName(), e);
      }

      Class type = field.getType();

      try {
         if (type.isPrimitive()) {
            if (type.equals(Integer.TYPE)) {
               putInt.invoke(unsafe, object, offset, (Integer)value);
            } else if (type.equals(Long.TYPE)) {
               putLong.invoke(unsafe, object, offset, (Long)value);
            } else if (type.equals(Short.TYPE)) {
               putShort.invoke(unsafe, object, offset, (Short)value);
            } else if (type.equals(Character.TYPE)) {
               putChar.invoke(unsafe, object, offset, (Character)value);
            } else if (type.equals(Byte.TYPE)) {
               putByte.invoke(unsafe, object, offset, (Byte)value);
            } else if (type.equals(Float.TYPE)) {
               putFloat.invoke(unsafe, object, offset, (Float)value);
            } else if (type.equals(Double.TYPE)) {
               putDouble.invoke(unsafe, object, offset, (Double)value);
            } else {
               if (!type.equals(Boolean.TYPE)) {
                  throw new IllegalStateException("Unknown primitive type: " + type.getName());
               }

               putBoolean.invoke(unsafe, object, offset, (Boolean)value);
            }
         } else {
            putObject.invoke(unsafe, object, offset, value);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   static {
      final Class<?> unsafeClass;
      try {
         unsafeClass = (Class)AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
            public Class<?> run() {
               try {
                  return Thread.currentThread().getContextClassLoader().loadClass("sun.misc.Unsafe");
               } catch (Exception e) {
                  try {
                     return ClassLoader.getSystemClassLoader().loadClass("sun.misc.Unsafe");
                  } catch (ClassNotFoundException var3) {
                     throw new IllegalStateException("Cannot get sun.misc.Unsafe", e);
                  }
               }
            }
         });
      } catch (Exception e) {
         throw new IllegalStateException("Cannot get sun.misc.Unsafe class", e);
      }

      unsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            try {
               Field field = unsafeClass.getDeclaredField("theUnsafe");
               field.setAccessible(true);
               return field.get((Object)null);
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe", e);
            }
         }
      });
      allocateInstance = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("allocateInstance", Class.class);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.allocateInstance", e);
            }
         }
      });
      objectFieldOffset = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("objectFieldOffset", Field.class);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.objectFieldOffset", e);
            }
         }
      });
      putInt = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putInt", Object.class, Long.TYPE, Integer.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putInt", e);
            }
         }
      });
      putLong = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putLong", e);
            }
         }
      });
      putShort = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putShort", Object.class, Long.TYPE, Short.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putShort", e);
            }
         }
      });
      putChar = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putChar", Object.class, Long.TYPE, Character.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putChar", e);
            }
         }
      });
      putByte = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putByte", Object.class, Long.TYPE, Byte.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putByte", e);
            }
         }
      });
      putFloat = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putFloat", Object.class, Long.TYPE, Float.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putFloat", e);
            }
         }
      });
      putDouble = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putDouble", Object.class, Long.TYPE, Double.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putDouble", e);
            }
         }
      });
      putBoolean = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putBoolean", e);
            }
         }
      });
      putObject = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method mtd = unsafeClass.getDeclaredMethod("putObject", Object.class, Long.TYPE, Object.class);
               mtd.setAccessible(true);
               return mtd;
            } catch (Exception e) {
               throw new IllegalStateException("Cannot get sun.misc.Unsafe.putObject", e);
            }
         }
      });
   }
}
