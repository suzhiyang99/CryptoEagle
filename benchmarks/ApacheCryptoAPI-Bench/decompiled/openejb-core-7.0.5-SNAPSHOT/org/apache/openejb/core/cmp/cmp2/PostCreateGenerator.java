package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.xbean.asm5.ClassWriter;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Type;

public class PostCreateGenerator {
   private final Class beanClass;
   private final ClassWriter cw;

   public PostCreateGenerator(Class beanClass, ClassWriter cw) {
      this.beanClass = beanClass;
      this.cw = cw;
   }

   public void generate() {
      for(Method ejbCreate : this.beanClass.getMethods()) {
         if (ejbCreate.getName().startsWith("ejbCreate")) {
            StringBuilder ejbPostCreateName = new StringBuilder(ejbCreate.getName());
            ejbPostCreateName.replace(0, "ejbC".length(), "ejbPostC");
            if (!this.hasMethod(this.beanClass, ejbPostCreateName.toString(), ejbCreate.getParameterTypes())) {
               this.createEjbPostCreate(ejbPostCreateName.toString(), ejbCreate);
            }
         }
      }

   }

   private boolean hasMethod(Class beanClass, String name, Class... args) {
      try {
         Method method = beanClass.getMethod(name, args);
         return !Modifier.isAbstract(method.getModifiers());
      } catch (NoSuchMethodException var5) {
         return false;
      }
   }

   public void createEjbPostCreate(String ejbPostCreateName, Method ejbCreate) {
      String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getArgumentTypes(ejbCreate));
      MethodVisitor mv = this.cw.visitMethod(1, ejbPostCreateName, methodDescriptor, (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, ejbCreate.getParameterTypes().length + 1);
      mv.visitEnd();
   }
}
