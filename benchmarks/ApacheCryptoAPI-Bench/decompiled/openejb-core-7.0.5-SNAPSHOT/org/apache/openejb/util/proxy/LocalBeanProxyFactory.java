package org.apache.openejb.util.proxy;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.ejb.EJBException;
import org.apache.openejb.util.Debug;
import org.apache.xbean.asm5.ClassWriter;
import org.apache.xbean.asm5.Label;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Opcodes;
import org.apache.xbean.asm5.Type;

public class LocalBeanProxyFactory implements Opcodes {
   public static final InvocationHandler NON_BUSINESS_HANDLER = new NonBusinessHandler();
   private static final String BUSSINESS_HANDLER_NAME = "businessHandler";
   private static final String NON_BUSINESS_HANDLER_NAME = "nonBusinessHandler";
   private static final ReentrantLock LOCK = new ReentrantLock();

   public static Object newProxyInstance(ClassLoader classLoader, InvocationHandler handler, Class classToSubclass, Class... interfaces) throws IllegalArgumentException {
      try {
         Class proxyClass = createProxy(classToSubclass, classLoader, interfaces);
         return constructProxy(proxyClass, handler);
      } catch (Throwable e) {
         throw new InternalError("LocalBeanProxyFactory.newProxyInstance: " + Debug.printStackTrace(e));
      }
   }

   public static void setInvocationHandler(Object proxy, InvocationHandler invocationHandler) {
      try {
         Field field = proxy.getClass().getDeclaredField("businessHandler");
         field.setAccessible(true);

         try {
            field.set(proxy, invocationHandler);
         } finally {
            field.setAccessible(false);
         }

      } catch (IllegalAccessException | NoSuchFieldException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public static InvocationHandler getInvocationHandler(Object proxy) {
      try {
         Field field = proxy.getClass().getDeclaredField("businessHandler");
         field.setAccessible(true);

         InvocationHandler var2;
         try {
            var2 = (InvocationHandler)field.get(proxy);
         } finally {
            field.setAccessible(false);
         }

         return var2;
      } catch (IllegalAccessException | NoSuchFieldException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public static Object constructProxy(Class clazz, InvocationHandler handler) throws IllegalStateException {
      Object instance = LocalBeanProxyFactory.Unsafe.allocateInstance(clazz);
      LocalBeanProxyFactory.Unsafe.setValue(getDeclaredField(clazz, "businessHandler"), instance, handler);
      LocalBeanProxyFactory.Unsafe.setValue(getDeclaredField(clazz, "nonBusinessHandler"), instance, NON_BUSINESS_HANDLER);
      return instance;
   }

   private static Field getDeclaredField(Class clazz, String fieldName) {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
         String message = String.format("Proxy class does not contain expected field \"%s\": %s", fieldName, clazz.getName());
         throw new IllegalStateException(message, e);
      }
   }

   public static boolean isProxy(Class<?> clazz) {
      return clazz.isAnnotationPresent(Proxy.class);
   }

   public static Class createProxy(Class<?> classToProxy, ClassLoader cl, String proxyName, Class... interfaces) {
      String classFileName = proxyName.replace('.', '/');

      try {
         return cl.loadClass(proxyName);
      } catch (Exception var16) {
         ReentrantLock lock = LOCK;
         lock.lock();

         Class var7;
         try {
            try {
               Class var17 = cl.loadClass(proxyName);
               return var17;
            } catch (Exception var13) {
               byte[] proxyBytes = generateProxy(classToProxy, classFileName, interfaces);
               var7 = LocalBeanProxyFactory.Unsafe.defineClass(cl, classToProxy, proxyName, proxyBytes);
            }
         } catch (Exception e) {
            throw new InternalError("LocalBeanProxyFactory.createProxy: " + Debug.printStackTrace(e));
         } finally {
            lock.unlock();
         }

         return var7;
      }
   }

   public static Class createProxy(Class<?> classToProxy, ClassLoader cl, Class... interfaces) {
      return createProxy(classToProxy, cl, classToProxy.getName() + "$$LocalBeanProxy", interfaces);
   }

   public static byte[] generateProxy(Class<?> classToProxy, String proxyName, Class<?>... interfaces) throws ProxyGenerationException {
      ClassWriter cw = new ClassWriter(2);
      String proxyClassFileName = proxyName.replace('.', '/');
      String classFileName = classToProxy.getName().replace('.', '/');
      String[] interfaceNames = new String[interfaces.length];

      for(int i = 0; i < interfaces.length; ++i) {
         Class<?> anInterface = interfaces[i];
         interfaceNames[i] = anInterface.getName().replace('.', '/');
      }

      cw.visit(49, 33, proxyClassFileName, (String)null, classFileName, interfaceNames);
      cw.visitSource(classFileName + ".java", (String)null);
      cw.visitAnnotation("L" + Proxy.class.getName().replace('.', '/') + ";", true).visitEnd();
      cw.visitField(18, "businessHandler", "Ljava/lang/reflect/InvocationHandler;", (String)null, (Object)null).visitEnd();
      cw.visitField(18, "nonBusinessHandler", "Ljava/lang/reflect/InvocationHandler;", (String)null, (Object)null).visitEnd();
      Map<String, List<Method>> methodMap = new HashMap();
      getNonPrivateMethods(classToProxy, methodMap);

      for(Class<?> anInterface : interfaces) {
         getNonPrivateMethods(anInterface, methodMap);
      }

      for(Map.Entry<String, List<Method>> entry : methodMap.entrySet()) {
         for(Method method : (List)entry.getValue()) {
            String name = method.getName();
            if (!Modifier.isPublic(method.getModifiers()) && (method.getParameterTypes().length != 0 || !"finalize".equals(name) && !"clone".equals(name))) {
               processMethod(cw, method, proxyClassFileName, "nonBusinessHandler");
            } else {
               processMethod(cw, method, proxyClassFileName, "businessHandler");
            }
         }
      }

      return cw.toByteArray();
   }

   private static void getNonPrivateMethods(Class<?> clazz, Map<String, List<Method>> methodMap) {
      while(clazz != null) {
         for(Method method : clazz.getDeclaredMethods()) {
            if (!method.isBridge()) {
               int modifiers = method.getModifiers();
               if (!Modifier.isFinal(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)) {
                  List<Method> methods = (List)methodMap.get(method.getName());
                  if (methods == null) {
                     methods = new ArrayList();
                     methods.add(method);
                     methodMap.put(method.getName(), methods);
                  } else if (!isOverridden(methods, method)) {
                     methods.add(method);
                  }
               }
            }
         }

         clazz = clazz.getSuperclass();
      }

   }

   private static boolean isOverridden(List<Method> methods, Method method) {
      for(Method m : methods) {
         if (Arrays.equals(m.getParameterTypes(), method.getParameterTypes())) {
            return true;
         }
      }

      return false;
   }

   public static void processMethod(ClassWriter cw, Method method, String proxyName, String handlerName) throws ProxyGenerationException {
      if (!"<init>".equals(method.getName())) {
         visit(cw, method, proxyName, handlerName).visitEnd();
      }
   }

   public static MethodVisitor visit(ClassWriter cw, Method method, String proxyName, String handlerName) throws ProxyGenerationException {
      Class<?> returnType = method.getReturnType();
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?>[] exceptionTypes = method.getExceptionTypes();
      int modifiers = method.getModifiers();
      int modifier = 0;
      if (Modifier.isPublic(modifiers)) {
         modifier = 1;
      } else if (Modifier.isProtected(modifiers)) {
         modifier = 4;
      }

      MethodVisitor mv = cw.visitMethod(modifier, method.getName(), getMethodSignatureAsString(returnType, parameterTypes), (String)null, (String[])null);
      mv.visitCode();
      Label l0 = new Label();
      Label l1 = new Label();
      Label l2 = new Label();
      if (exceptionTypes.length > 0) {
         mv.visitTryCatchBlock(l0, l1, l2, "java/lang/reflect/InvocationTargetException");
      }

      mv.visitLabel(l0);
      String classNameToOverride = method.getDeclaringClass().getName().replace('.', '/');
      mv.visitLdcInsn(Type.getType("L" + classNameToOverride + ";"));
      mv.visitLdcInsn(method.getName());
      createArrayDefinition(mv, parameterTypes.length, Class.class);
      int length = 1;

      for(int i = 0; i < parameterTypes.length; ++i) {
         mv.visitInsn(89);
         Class<?> parameterType = parameterTypes[i];
         pushIntOntoStack(mv, i);
         if (parameterType.isPrimitive()) {
            String wrapperType = getWrapperType(parameterType);
            mv.visitFieldInsn(178, wrapperType, "TYPE", "Ljava/lang/Class;");
         } else {
            mv.visitLdcInsn(Type.getType(getAsmTypeAsString(parameterType, true)));
         }

         mv.visitInsn(83);
         if (!Long.TYPE.equals(parameterType) && !Double.TYPE.equals(parameterType)) {
            ++length;
         } else {
            length += 2;
         }
      }

      mv.visitMethodInsn(182, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
      mv.visitVarInsn(58, length);
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, proxyName, handlerName, "Ljava/lang/reflect/InvocationHandler;");
      mv.visitVarInsn(25, 0);
      mv.visitVarInsn(25, length);
      createArrayDefinition(mv, parameterTypes.length, Object.class);
      int index = 1;

      for(int i = 0; i < parameterTypes.length; ++i) {
         mv.visitInsn(89);
         Class<?> parameterType = parameterTypes[i];
         pushIntOntoStack(mv, i);
         if (parameterType.isPrimitive()) {
            String wrapperType = getWrapperType(parameterType);
            mv.visitVarInsn(getVarInsn(parameterType), index);
            mv.visitMethodInsn(184, wrapperType, "valueOf", "(" + getPrimitiveLetter(parameterType) + ")L" + wrapperType + ";", false);
            mv.visitInsn(83);
            if (!Long.TYPE.equals(parameterType) && !Double.TYPE.equals(parameterType)) {
               ++index;
            } else {
               index += 2;
            }
         } else {
            mv.visitVarInsn(25, index);
            mv.visitInsn(83);
            ++index;
         }
      }

      mv.visitMethodInsn(185, "java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
      mv.visitTypeInsn(192, getCastType(returnType));
      if (returnType.isPrimitive() && !Void.TYPE.equals(returnType)) {
         mv.visitMethodInsn(182, getWrapperType(returnType), getPrimitiveMethod(returnType), "()" + getPrimitiveLetter(returnType), false);
      }

      mv.visitLabel(l1);
      if (!Void.TYPE.equals(returnType)) {
         mv.visitInsn(getReturnInsn(returnType));
      } else {
         mv.visitInsn(87);
         mv.visitInsn(177);
      }

      if (exceptionTypes.length > 0) {
         mv.visitLabel(l2);
         mv.visitVarInsn(58, length);
         Label l5 = new Label();
         mv.visitLabel(l5);

         for(int i = 0; i < exceptionTypes.length; ++i) {
            Class<?> exceptionType = exceptionTypes[i];
            mv.visitLdcInsn(Type.getType("L" + exceptionType.getName().replace('.', '/') + ";"));
            mv.visitVarInsn(25, length);
            mv.visitMethodInsn(182, "java/lang/reflect/InvocationTargetException", "getCause", "()Ljava/lang/Throwable;", false);
            mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(182, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            Label l6 = new Label();
            mv.visitJumpInsn(153, l6);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(25, length);
            mv.visitMethodInsn(182, "java/lang/reflect/InvocationTargetException", "getCause", "()Ljava/lang/Throwable;", false);
            mv.visitTypeInsn(192, exceptionType.getName().replace('.', '/'));
            mv.visitInsn(191);
            mv.visitLabel(l6);
            if (i == exceptionTypes.length - 1) {
               mv.visitTypeInsn(187, "java/lang/reflect/UndeclaredThrowableException");
               mv.visitInsn(89);
               mv.visitVarInsn(25, length);
               mv.visitMethodInsn(183, "java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V", false);
               mv.visitInsn(191);
            }
         }
      }

      mv.visitMaxs(0, 0);
      return mv;
   }

   private static int getReturnInsn(Class<?> type) {
      if (type.isPrimitive()) {
         if (Integer.TYPE.equals(type)) {
            return 172;
         }

         if (Boolean.TYPE.equals(type)) {
            return 172;
         }

         if (Character.TYPE.equals(type)) {
            return 172;
         }

         if (Byte.TYPE.equals(type)) {
            return 172;
         }

         if (Short.TYPE.equals(type)) {
            return 172;
         }

         if (Float.TYPE.equals(type)) {
            return 174;
         }

         if (Long.TYPE.equals(type)) {
            return 173;
         }

         if (Double.TYPE.equals(type)) {
            return 175;
         }
      }

      return 176;
   }

   private static int getVarInsn(Class<?> type) {
      if (type.isPrimitive()) {
         if (Integer.TYPE.equals(type)) {
            return 21;
         }

         if (Boolean.TYPE.equals(type)) {
            return 21;
         }

         if (Character.TYPE.equals(type)) {
            return 21;
         }

         if (Byte.TYPE.equals(type)) {
            return 21;
         }

         if (Short.TYPE.equals(type)) {
            return 21;
         }

         if (Float.TYPE.equals(type)) {
            return 23;
         }

         if (Long.TYPE.equals(type)) {
            return 22;
         }

         if (Double.TYPE.equals(type)) {
            return 24;
         }
      }

      throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
   }

   private static String getPrimitiveMethod(Class<?> type) {
      if (Integer.TYPE.equals(type)) {
         return "intValue";
      } else if (Boolean.TYPE.equals(type)) {
         return "booleanValue";
      } else if (Character.TYPE.equals(type)) {
         return "charValue";
      } else if (Byte.TYPE.equals(type)) {
         return "byteValue";
      } else if (Short.TYPE.equals(type)) {
         return "shortValue";
      } else if (Float.TYPE.equals(type)) {
         return "floatValue";
      } else if (Long.TYPE.equals(type)) {
         return "longValue";
      } else if (Double.TYPE.equals(type)) {
         return "doubleValue";
      } else {
         throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
      }
   }

   static String getCastType(Class<?> returnType) {
      return returnType.isPrimitive() ? getWrapperType(returnType) : getAsmTypeAsString(returnType, false);
   }

   private static String getWrapperType(Class<?> type) {
      if (Integer.TYPE.equals(type)) {
         return Integer.class.getCanonicalName().replace('.', '/');
      } else if (Boolean.TYPE.equals(type)) {
         return Boolean.class.getCanonicalName().replace('.', '/');
      } else if (Character.TYPE.equals(type)) {
         return Character.class.getCanonicalName().replace('.', '/');
      } else if (Byte.TYPE.equals(type)) {
         return Byte.class.getCanonicalName().replace('.', '/');
      } else if (Short.TYPE.equals(type)) {
         return Short.class.getCanonicalName().replace('.', '/');
      } else if (Float.TYPE.equals(type)) {
         return Float.class.getCanonicalName().replace('.', '/');
      } else if (Long.TYPE.equals(type)) {
         return Long.class.getCanonicalName().replace('.', '/');
      } else if (Double.TYPE.equals(type)) {
         return Double.class.getCanonicalName().replace('.', '/');
      } else if (Void.TYPE.equals(type)) {
         return Void.class.getCanonicalName().replace('.', '/');
      } else {
         throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
      }
   }

   private static void pushIntOntoStack(MethodVisitor mv, int i) {
      if (i == 0) {
         mv.visitInsn(3);
      } else if (i == 1) {
         mv.visitInsn(4);
      } else if (i == 2) {
         mv.visitInsn(5);
      } else if (i == 3) {
         mv.visitInsn(6);
      } else if (i == 4) {
         mv.visitInsn(7);
      } else if (i == 5) {
         mv.visitInsn(8);
      } else if (i > 5 && i <= 255) {
         mv.visitIntInsn(16, i);
      } else {
         mv.visitIntInsn(17, i);
      }

   }

   private static void createArrayDefinition(MethodVisitor mv, int size, Class<?> type) throws ProxyGenerationException {
      if (size < 0) {
         throw new ProxyGenerationException("Array size cannot be less than zero");
      } else {
         pushIntOntoStack(mv, size);
         mv.visitTypeInsn(189, type.getCanonicalName().replace('.', '/'));
      }
   }

   static String getMethodSignatureAsString(Class<?> returnType, Class<?>[] parameterTypes) {
      StringBuilder builder = new StringBuilder();
      builder.append("(");

      for(Class<?> parameterType : parameterTypes) {
         builder.append(getAsmTypeAsString(parameterType, true));
      }

      builder.append(")");
      builder.append(getAsmTypeAsString(returnType, true));
      return builder.toString();
   }

   private static String getPrimitiveLetter(Class<?> type) {
      if (Integer.TYPE.equals(type)) {
         return "I";
      } else if (Void.TYPE.equals(type)) {
         return "V";
      } else if (Boolean.TYPE.equals(type)) {
         return "Z";
      } else if (Character.TYPE.equals(type)) {
         return "C";
      } else if (Byte.TYPE.equals(type)) {
         return "B";
      } else if (Short.TYPE.equals(type)) {
         return "S";
      } else if (Float.TYPE.equals(type)) {
         return "F";
      } else if (Long.TYPE.equals(type)) {
         return "J";
      } else if (Double.TYPE.equals(type)) {
         return "D";
      } else {
         throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
      }
   }

   public static String getAsmTypeAsString(Class<?> parameterType, boolean wrap) {
      if (parameterType.isArray()) {
         if (parameterType.getComponentType().isPrimitive()) {
            Class<?> componentType = parameterType.getComponentType();
            return "[" + getPrimitiveLetter(componentType);
         } else {
            return "[" + getAsmTypeAsString(parameterType.getComponentType(), true);
         }
      } else if (parameterType.isPrimitive()) {
         return getPrimitiveLetter(parameterType);
      } else {
         String className = parameterType.getCanonicalName();
         if (parameterType.isMemberClass()) {
            int lastDot = className.lastIndexOf(".");
            className = className.substring(0, lastDot) + "$" + className.substring(lastDot + 1);
         }

         return wrap ? "L" + className.replace('.', '/') + ";" : className.replace('.', '/');
      }
   }

   static class NonBusinessHandler implements InvocationHandler, Serializable {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         throw new EJBException("Calling non-public methods of a local bean without any interfaces is not allowed");
      }
   }

   public static class Unsafe {
      private static final Object unsafe;
      private static final Method defineClass;
      private static final Method allocateInstance;
      private static final Method putObject;
      private static final Method objectFieldOffset;

      public static Object allocateInstance(Class clazz) {
         try {
            return allocateInstance.invoke(unsafe, clazz);
         } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to allocateInstance of Proxy class " + clazz.getName(), e);
         } catch (InvocationTargetException e) {
            Throwable throwable = (Throwable)(e.getTargetException() != null ? e.getTargetException() : e);
            throw new IllegalStateException("Failed to allocateInstance of Proxy class " + clazz.getName(), throwable);
         }
      }

      private static void setValue(Field field, Object object, Object value) {
         long offset;
         try {
            offset = (Long)objectFieldOffset.invoke(unsafe, field);
         } catch (Exception e) {
            throw new IllegalStateException("Failed getting offset for: field=" + field.getName() + "  class=" + field.getDeclaringClass().getName(), e);
         }

         try {
            putObject.invoke(unsafe, object, offset, value);
         } catch (Exception e) {
            throw new IllegalStateException("Failed putting field=" + field.getName() + "  class=" + field.getDeclaringClass().getName(), e);
         }
      }

      public static Class defineClass(ClassLoader loader, Class<?> clsToProxy, String proxyName, byte[] proxyBytes) throws IllegalAccessException, InvocationTargetException {
         return (Class)defineClass.invoke(unsafe, proxyName, proxyBytes, 0, proxyBytes.length, loader, clsToProxy.getProtectionDomain());
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
         defineClass = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
            public Method run() {
               try {
                  Method mtd = unsafeClass.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
                  mtd.setAccessible(true);
                  return mtd;
               } catch (Exception e) {
                  throw new IllegalStateException("Cannot get sun.misc.Unsafe.defineClass", e);
               }
            }
         });
      }
   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   private @interface Proxy {
   }
}
