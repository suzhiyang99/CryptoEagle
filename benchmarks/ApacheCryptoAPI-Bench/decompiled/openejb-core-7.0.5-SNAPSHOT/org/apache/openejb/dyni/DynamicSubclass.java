package org.apache.openejb.dyni;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.Debug;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;
import org.apache.openejb.util.proxy.ProxyGenerationException;
import org.apache.xbean.asm5.AnnotationVisitor;
import org.apache.xbean.asm5.ClassReader;
import org.apache.xbean.asm5.ClassVisitor;
import org.apache.xbean.asm5.ClassWriter;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Opcodes;
import org.apache.xbean.asm5.Type;

public class DynamicSubclass implements Opcodes {
   private static final ReentrantLock LOCK = new ReentrantLock();
   public static final String IMPL_SUFFIX = "$$Impl";

   public static boolean isDynamic(Class beanClass) {
      return Modifier.isAbstract(beanClass.getModifiers()) && InvocationHandler.class.isAssignableFrom(beanClass);
   }

   public static Class createSubclass(Class<?> abstractClass, ClassLoader cl) {
      return createSubclass(abstractClass, cl, false);
   }

   public static Class createSubclass(Class<?> abstractClass, ClassLoader cl, boolean proxyNonAbstractMethods) {
      String proxyName = getSubclassName(abstractClass);

      try {
         return cl.loadClass(proxyName);
      } catch (Exception var14) {
         ReentrantLock lock = LOCK;
         lock.lock();

         Class var5;
         try {
            try {
               var5 = cl.loadClass(proxyName);
               return var5;
            } catch (Exception var11) {
               var5 = LocalBeanProxyFactory.Unsafe.defineClass(cl, abstractClass, proxyName, generateBytes(abstractClass, proxyNonAbstractMethods));
            }
         } catch (Exception e) {
            throw new InternalError(DynamicSubclass.class.getSimpleName() + ".createSubclass: " + Debug.printStackTrace(e));
         } finally {
            lock.unlock();
         }

         return var5;
      }
   }

   public static void setHandler(Object instance, InvocationHandler handler) {
      try {
         Field thisHandler = instance.getClass().getDeclaredField("this$handler");
         if (!thisHandler.isAccessible()) {
            thisHandler.setAccessible(true);
         }

         thisHandler.set(instance, handler);
      } catch (NoSuchFieldException e) {
         throw new IllegalArgumentException(e);
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }
   }

   private static byte[] generateBytes(Class<?> classToProxy, boolean proxyNonAbstractMethods) throws ProxyGenerationException {
      Map<String, MethodVisitor> visitors = new HashMap();
      ClassWriter cw = new ClassWriter(2);
      String proxyClassFileName = getSubclassName(classToProxy).replace('.', '/');
      String classFileName = classToProxy.getName().replace('.', '/');
      cw.visit(49, 33, proxyClassFileName, (String)null, classFileName, (String[])null);
      cw.visitSource(classFileName + ".java", (String)null);
      cw.visitField(18, "this$handler", "Ljava/lang/reflect/InvocationHandler;", (String)null, (Object)null).visitEnd();

      for(Constructor<?> constructor : classToProxy.getConstructors()) {
         if (Modifier.isPublic(constructor.getModifiers())) {
            MethodVisitor mv = visitConstructor(cw, proxyClassFileName, classFileName, constructor);
            visitors.put("<init>" + Type.getConstructorDescriptor(constructor), mv);
         }
      }

      Map<String, List<Method>> methodMap = new HashMap();
      getNonPrivateMethods(classToProxy, methodMap);

      for(Map.Entry<String, List<Method>> entry : methodMap.entrySet()) {
         for(Method method : (List)entry.getValue()) {
            if (Modifier.isAbstract(method.getModifiers()) || proxyNonAbstractMethods && Modifier.isPublic(method.getModifiers())) {
               MethodVisitor visitor = LocalBeanProxyFactory.visit(cw, method, proxyClassFileName, "this$handler");
               visitors.put(method.getName() + Type.getMethodDescriptor(method), visitor);
            }
         }
      }

      copyClassAnnotations(classToProxy, cw);
      copyMethodAnnotations(classToProxy, visitors);

      for(MethodVisitor visitor : visitors.values()) {
         visitor.visitEnd();
      }

      return cw.toByteArray();
   }

   private static MethodVisitor visitConstructor(ClassWriter cw, String proxyClassFileName, String classFileName, Constructor<?> constructor) {
      String descriptor = Type.getConstructorDescriptor(constructor);
      String[] exceptions = new String[constructor.getExceptionTypes().length];

      for(int i = 0; i < exceptions.length; ++i) {
         exceptions[i] = Type.getInternalName(constructor.getExceptionTypes()[i]);
      }

      MethodVisitor mv = cw.visitMethod(1, "<init>", descriptor, (String)null, exceptions);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      int index = 1;

      for(Type type : Type.getArgumentTypes(descriptor)) {
         mv.visitVarInsn(type.getOpcode(21), index);
         index += size(type);
      }

      mv.visitMethodInsn(183, classFileName, "<init>", descriptor, false);
      mv.visitVarInsn(25, 0);
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(181, proxyClassFileName, "this$handler", "Ljava/lang/reflect/InvocationHandler;");
      mv.visitInsn(177);
      mv.visitMaxs(2, 1);
      return mv;
   }

   private static String getSubclassName(Class<?> classToProxy) {
      return classToProxy.getName() + "$$Impl";
   }

   private static void getNonPrivateMethods(Class<?> impl, Map<String, List<Method>> methodMap) {
      Class<?>[] interfaces = impl.getInterfaces();
      Collection<Class<?>> api = new ArrayList(interfaces.length + 1);
      api.add(impl);
      api.addAll(Arrays.asList(interfaces));
      Iterator var4 = api.iterator();

      while(var4.hasNext()) {
         for(Class<?> clazz = (Class)var4.next(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for(Method method : clazz.getDeclaredMethods()) {
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

   public static int size(Type type) {
      if (Type.VOID_TYPE.equals(type)) {
         return 0;
      } else {
         return !Type.LONG_TYPE.equals(type) && !Type.DOUBLE_TYPE.equals(type) ? 1 : 2;
      }
   }

   public static byte[] readClassFile(Class clazz) throws IOException {
      return readClassFile(clazz.getClassLoader(), clazz);
   }

   public static byte[] readClassFile(ClassLoader classLoader, Class clazz) throws IOException {
      String internalName = clazz.getName().replace('.', '/') + ".class";
      URL resource = classLoader.getResource(internalName);
      InputStream in = IO.read(resource);

      ByteArrayOutputStream out;
      try {
         out = new ByteArrayOutputStream();
         IO.copy(in, out);
      } finally {
         IO.close(in);
      }

      return out.toByteArray();
   }

   private static void copyMethodAnnotations(Class<?> classToProxy, Map<String, MethodVisitor> visitors) throws ProxyGenerationException {
      for(Class clazz = classToProxy; clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
         try {
            ClassReader classReader = new ClassReader(readClassFile(clazz));
            ClassVisitor copyMethodAnnotations = new CopyMethodAnnotations(visitors);
            classReader.accept(copyMethodAnnotations, 1);
         } catch (IOException e) {
            throw new ProxyGenerationException(e);
         }
      }

   }

   private static void copyClassAnnotations(Class<?> clazz, ClassVisitor newClass) throws ProxyGenerationException {
      try {
         ClassReader classReader = new ClassReader(readClassFile(clazz));
         ClassVisitor visitor = new CopyClassAnnotations(newClass);
         classReader.accept(visitor, 1);
      } catch (IOException e) {
         throw new ProxyGenerationException(e);
      }
   }

   public static class MoveAnnotationsVisitor extends MethodVisitor {
      private final MethodVisitor newMethod;

      public MoveAnnotationsVisitor(MethodVisitor movedMethod, MethodVisitor newMethod) {
         super(327680, movedMethod);
         this.newMethod = newMethod;
      }

      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
         return this.newMethod.visitAnnotation(desc, visible);
      }

      public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
         return this.newMethod.visitParameterAnnotation(parameter, desc, visible);
      }

      public void visitEnd() {
         this.newMethod.visitEnd();
         super.visitEnd();
      }
   }

   private static class CopyClassAnnotations extends ClassVisitor {
      private final ClassVisitor newClass;

      public CopyClassAnnotations(ClassVisitor newClass) {
         super(327680);
         this.newClass = newClass;
      }

      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
         return this.newClass.visitAnnotation(desc, visible);
      }
   }

   private static class CopyMethodAnnotations extends ClassVisitor {
      private final Map<String, MethodVisitor> visitors;

      public CopyMethodAnnotations(Map<String, MethodVisitor> visitors) {
         super(327680);
         this.visitors = visitors;
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor newMethod = (MethodVisitor)this.visitors.remove(name + desc);
         if (newMethod == null) {
            return null;
         } else {
            MethodVisitor oldMethod = super.visitMethod(access, name, desc, signature, exceptions);
            return new MoveAnnotationsVisitor(oldMethod, newMethod);
         }
      }
   }
}
