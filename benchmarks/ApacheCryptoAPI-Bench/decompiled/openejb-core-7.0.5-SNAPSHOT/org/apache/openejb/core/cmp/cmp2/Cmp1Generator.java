package org.apache.openejb.core.cmp.cmp2;

import org.apache.xbean.asm5.ClassWriter;
import org.apache.xbean.asm5.FieldVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Opcodes;
import org.apache.xbean.asm5.Type;

public class Cmp1Generator implements Opcodes {
   private final String implClassName;
   private final String beanClassName;
   private final ClassWriter cw;
   private boolean unknownPk;
   private final PostCreateGenerator postCreateGenerator;

   public Cmp1Generator(String cmpImplClass, Class beanClass) {
      this.beanClassName = Type.getInternalName(beanClass);
      this.implClassName = cmpImplClass.replace('.', '/');
      this.cw = new ClassWriter(1);
      this.postCreateGenerator = new PostCreateGenerator(beanClass, this.cw);
   }

   public byte[] generate() {
      this.cw.visit(49, 33, this.implClassName, (String)null, this.beanClassName, new String[]{"javax/ejb/EntityBean"});
      if (this.unknownPk) {
         FieldVisitor fv = this.cw.visitField(1, "OpenEJB_pk", "Ljava/lang/Long;", (String)null, (Object)null);
         fv.visitEnd();
      }

      this.createConstructor();
      this.postCreateGenerator.generate();
      this.cw.visitEnd();
      return this.cw.toByteArray();
   }

   private void createConstructor() {
      MethodVisitor mv = this.cw.visitMethod(1, "<init>", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(183, this.beanClassName, "<init>", "()V", false);
      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   public boolean isUnknownPk() {
      return this.unknownPk;
   }

   public void setUnknownPk(boolean unknownPk) {
      this.unknownPk = unknownPk;
   }
}
