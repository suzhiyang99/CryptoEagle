package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EntityContext;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.xbean.asm5.ClassWriter;
import org.apache.xbean.asm5.FieldVisitor;
import org.apache.xbean.asm5.Label;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Opcodes;
import org.apache.xbean.asm5.Type;

public class Cmp2Generator implements Opcodes {
   private static final String UNKNOWN_PK_NAME = "OpenEJB_pk";
   private static final Type UNKNOWN_PK_TYPE = Type.getType(Long.class);
   private final String implClassName;
   private final String beanClassName;
   private final ClassWriter cw;
   private final Map<String, CmpField> cmpFields = new LinkedHashMap();
   private final Collection<CmrField> cmrFields = new ArrayList();
   private final CmpField pkField;
   private final Class primKeyClass;
   private final List<Method> selectMethods = new ArrayList();
   private final Class beanClass;
   private final PostCreateGenerator postCreateGenerator;
   private static final String DELETED = "openejb_deleted";

   public Cmp2Generator(String cmpImplClass, Class beanClass, String pkField, Class<?> primKeyClass, String[] cmpFields) {
      this.beanClass = beanClass;
      this.beanClassName = Type.getInternalName(beanClass);
      this.implClassName = cmpImplClass.replace('.', '/');
      if (pkField == null && primKeyClass == null) {
         throw new NullPointerException("Both pkField and primKeyClass are null for bean " + this.beanClassName);
      } else {
         this.primKeyClass = primKeyClass;
         String[] var6 = cmpFields;
         int var7 = cmpFields.length;
         int var8 = 0;

         String cmpFieldName;
         while(true) {
            if (var8 >= var7) {
               if (pkField != null) {
                  this.pkField = (CmpField)this.cmpFields.get(pkField);
                  if (this.pkField == null) {
                     throw new IllegalArgumentException("No such property " + pkField + " defined on bean class " + this.beanClassName);
                  }
               } else {
                  this.pkField = null;
               }

               for(Method method : beanClass.getMethods()) {
                  if (Modifier.isAbstract(method.getModifiers()) && method.getName().startsWith("ejbSelect")) {
                     this.addSelectMethod(method);
                  }
               }

               this.cw = new ClassWriter(1);
               this.postCreateGenerator = new PostCreateGenerator(beanClass, this.cw);
               return;
            }

            cmpFieldName = var6[var8];
            Method getter = this.getterMethod(cmpFieldName);
            if (getter == null) {
               throw new IllegalArgumentException("No such property " + cmpFieldName + " defined on bean class " + this.beanClassName);
            }

            if (Modifier.isAbstract(getter.getModifiers())) {
               Type type = Type.getType(getter.getReturnType());
               CmpField cmpField = new CmpField(cmpFieldName, type, getter);
               this.cmpFields.put(cmpFieldName, cmpField);
            } else {
               try {
                  Field field = getter.getDeclaringClass().getDeclaredField(cmpFieldName);
                  if (!Modifier.isPrivate(field.getModifiers())) {
                     break;
                  }
               } catch (NoSuchFieldException var13) {
                  break;
               }
            }

            ++var8;
         }

         throw new IllegalArgumentException("No such property " + cmpFieldName + " defined on bean class " + this.beanClassName);
      }
   }

   public void addCmrField(CmrField cmrField) {
      if (this.cmpFields.get(cmrField.getName()) != null) {
         this.cmpFields.remove(cmrField.getName());
      }

      this.cmrFields.add(cmrField);
   }

   public void addSelectMethod(Method selectMethod) {
      this.selectMethods.add(selectMethod);
   }

   public byte[] generate() {
      this.cw.visit(49, 33, this.implClassName, (String)null, this.beanClassName, new String[]{"org/apache/openejb/core/cmp/cmp2/Cmp2Entity", "javax/ejb/EntityBean"});
      FieldVisitor fv = this.cw.visitField(9, "deploymentInfo", "Ljava/lang/Object;", (String)null, (Object)null);
      fv.visitEnd();
      fv = this.cw.visitField(194, "openejb_deleted", "Z", (String)null, (Object)null);
      fv.visitEnd();
      if (Object.class.equals(this.primKeyClass)) {
         fv = this.cw.visitField(2, "OpenEJB_pk", UNKNOWN_PK_TYPE.getDescriptor(), (String)null, (Object)null);
         fv.visitEnd();
      }

      for(CmpField cmpField : this.cmpFields.values()) {
         this.createField(cmpField);
      }

      for(CmrField cmrField : this.cmrFields) {
         this.createCmrFields(cmrField);
      }

      this.createConstructor();

      for(CmpField cmpField : this.cmpFields.values()) {
         this.createGetter(cmpField);
         this.createSetter(cmpField);
      }

      for(CmrField cmrField : this.cmrFields) {
         this.createCmrGetter(cmrField);
         this.createCmrSetter(cmrField);
      }

      this.createSimplePrimaryKeyGetter();
      this.createOpenEJB_isDeleted();
      this.createOpenEJB_deleted();
      this.createOpenEJB_addCmr();
      this.createOpenEJB_removeCmr();

      for(Method selectMethod : this.selectMethods) {
         this.createSelectMethod(selectMethod);
      }

      if (!this.hasMethod(this.beanClass, "ejbActivate")) {
         this.createEjbActivate();
      }

      if (!this.hasMethod(this.beanClass, "ejbPassivate")) {
         this.createEjbPassivate();
      }

      if (!this.hasMethod(this.beanClass, "ejbLoad")) {
         this.createEjbLoad();
      }

      if (!this.hasMethod(this.beanClass, "ejbStore")) {
         this.createEjbStore();
      }

      if (!this.hasMethod(this.beanClass, "ejbRemove")) {
         this.createEjbRemove();
      }

      if (!this.hasMethod(this.beanClass, "setEntityContext", EntityContext.class)) {
         this.createSetEntityContext();
      }

      if (!this.hasMethod(this.beanClass, "unsetEntityContext")) {
         this.createUnsetEntityContext();
      }

      this.postCreateGenerator.generate();
      this.cw.visitEnd();
      return this.cw.toByteArray();
   }

   private boolean hasMethod(Class beanClass, String name, Class... args) {
      try {
         Method method = beanClass.getMethod(name, args);
         return !Modifier.isAbstract(method.getModifiers());
      } catch (NoSuchMethodException var5) {
         return false;
      }
   }

   private void createConstructor() {
      MethodVisitor mv = this.cw.visitMethod(1, "<init>", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(183, this.beanClassName, "<init>", "()V", false);

      for(CmrField cmrField : this.cmrFields) {
         this.initCmrFields(mv, cmrField);
      }

      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createOpenEJB_isDeleted() {
      MethodVisitor mv = this.cw.visitMethod(1, "OpenEJB_isDeleted", "()Z", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, "openejb_deleted", "Z");
      mv.visitInsn(172);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createOpenEJB_deleted() {
      MethodVisitor mv = this.cw.visitMethod(1, "OpenEJB_deleted", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, "openejb_deleted", "Z");
      Label notDeleted = new Label();
      mv.visitJumpInsn(153, notDeleted);
      mv.visitInsn(177);
      mv.visitLabel(notDeleted);
      mv.visitVarInsn(25, 0);
      mv.visitInsn(4);
      mv.visitFieldInsn(181, this.implClassName, "openejb_deleted", "Z");

      for(CmrField cmrField : this.cmrFields) {
         this.createOpenEJB_deleted(mv, cmrField);
      }

      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createOpenEJB_addCmr() {
      MethodVisitor mv = this.cw.visitMethod(1, "OpenEJB_addCmr", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, "openejb_deleted", "Z");
      Label notDeleted = new Label();
      mv.visitJumpInsn(153, notDeleted);
      mv.visitInsn(1);
      mv.visitInsn(176);
      mv.visitLabel(notDeleted);

      for(CmrField cmrField : this.cmrFields) {
         this.createOpenEJB_addCmr(mv, cmrField);
      }

      mv.visitTypeInsn(187, "java/lang/IllegalArgumentException");
      mv.visitInsn(89);
      mv.visitTypeInsn(187, "java/lang/StringBuilder");
      mv.visitInsn(89);
      mv.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "()V", false);
      mv.visitLdcInsn("Unknown cmr field ");
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(25, 1);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitLdcInsn(" on entity bean of type ");
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
      mv.visitMethodInsn(182, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
      mv.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
      mv.visitInsn(191);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createOpenEJB_removeCmr() {
      MethodVisitor mv = this.cw.visitMethod(1, "OpenEJB_removeCmr", "(Ljava/lang/String;Ljava/lang/Object;)V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, "openejb_deleted", "Z");
      Label notDeleted = new Label();
      mv.visitJumpInsn(153, notDeleted);
      mv.visitInsn(177);
      mv.visitLabel(notDeleted);

      for(CmrField cmrField : this.cmrFields) {
         this.createOpenEJB_removeCmr(mv, cmrField);
      }

      mv.visitTypeInsn(187, "java/lang/IllegalArgumentException");
      mv.visitInsn(89);
      mv.visitTypeInsn(187, "java/lang/StringBuilder");
      mv.visitInsn(89);
      mv.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "()V", false);
      mv.visitLdcInsn("Unknown cmr field ");
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(25, 1);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitLdcInsn(" on entity bean of type ");
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
      mv.visitMethodInsn(182, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
      mv.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
      mv.visitInsn(191);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createField(CmpField cmpField) {
      FieldVisitor fv = this.cw.visitField(2, cmpField.getName(), cmpField.getDescriptor(), (String)null, (Object)null);
      fv.visitEnd();
   }

   private void createGetter(CmpField cmpField) {
      String methodName = cmpField.getGetterMethod().getName();
      MethodVisitor mv = this.cw.visitMethod(1, methodName, "()" + cmpField.getDescriptor(), (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, cmpField.getName(), cmpField.getDescriptor());
      mv.visitInsn(cmpField.getType().getOpcode(172));
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private static String getterName(String propertyName) {
      return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
   }

   private Method getterMethod(String propertyName) {
      String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

      try {
         return this.beanClass.getMethod(getterName);
      } catch (NoSuchMethodException var5) {
         getterName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

         try {
            return this.beanClass.getMethod(getterName);
         } catch (NoSuchMethodException var4) {
            return null;
         }
      }
   }

   private void createSetter(CmpField cmpField) {
      String methodName = this.setterName(cmpField.getName());
      MethodVisitor mv = this.cw.visitMethod(1, methodName, "(" + cmpField.getDescriptor() + ")V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitVarInsn(cmpField.getType().getOpcode(21), 1);
      mv.visitFieldInsn(181, this.implClassName, cmpField.getName(), cmpField.getDescriptor());
      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private String setterName(String propertyName) {
      return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
   }

   private void createSimplePrimaryKeyGetter() {
      MethodVisitor mv = this.cw.visitMethod(1, "OpenEJB_getPrimaryKey", "()Ljava/lang/Object;", (String)null, (String[])null);
      mv.visitCode();
      if (this.pkField != null) {
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, this.pkField.getName(), this.pkField.getDescriptor());
         mv.visitInsn(this.pkField.getType().getOpcode(172));
      } else if (Object.class.equals(this.primKeyClass)) {
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, "OpenEJB_pk", UNKNOWN_PK_TYPE.getDescriptor());
         mv.visitInsn(UNKNOWN_PK_TYPE.getOpcode(172));
      } else {
         String pkImplName = this.primKeyClass.getName().replace('.', '/');
         mv.visitTypeInsn(187, pkImplName);
         mv.visitInsn(89);
         mv.visitMethodInsn(183, pkImplName, "<init>", "()V", false);
         mv.visitVarInsn(58, 1);
         mv.visitVarInsn(25, 1);

         for(Field field : this.primKeyClass.getFields()) {
            CmpField cmpField = (CmpField)this.cmpFields.get(field.getName());
            if (cmpField != null) {
               if (!cmpField.getType().getClassName().equals(field.getType().getName())) {
                  throw new IllegalArgumentException("Primary key " + cmpField.getName() + " is type " + cmpField.getType().getClassName() + " but CMP field is type " + field.getType().getName());
               }

               mv.visitVarInsn(25, 0);
               mv.visitFieldInsn(180, this.implClassName, cmpField.getName(), cmpField.getDescriptor());
               mv.visitFieldInsn(181, pkImplName, cmpField.getName(), cmpField.getDescriptor());
               mv.visitVarInsn(25, 1);
            }
         }

         mv.visitInsn(176);
      }

      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private void createCmrFields(CmrField cmrField) {
      FieldVisitor fv = this.cw.visitField(2, cmrField.getName(), cmrField.getDescriptor(), cmrField.getGenericSignature(), (Object)null);
      fv.visitEnd();
      fv = this.cw.visitField(130, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor(), cmrField.getAccessorGenericSignature(), (Object)null);
      fv.visitEnd();
   }

   private void initCmrFields(MethodVisitor mv, CmrField cmrField) {
      Type initialValueType = cmrField.getInitialValueType();
      if (initialValueType != null) {
         mv.visitVarInsn(25, 0);
         mv.visitTypeInsn(187, initialValueType.getInternalName());
         mv.visitInsn(89);
         mv.visitMethodInsn(183, initialValueType.getInternalName(), "<init>", "()V", false);
         mv.visitFieldInsn(181, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
      }

      mv.visitVarInsn(25, 0);
      mv.visitTypeInsn(187, cmrField.getAccessorInternalName());
      mv.visitInsn(89);
      mv.visitVarInsn(25, 0);
      mv.visitLdcInsn(cmrField.getName());
      mv.visitLdcInsn(cmrField.getType());
      if (cmrField.getRelatedName() != null) {
         mv.visitLdcInsn(cmrField.getRelatedName());
      } else {
         mv.visitInsn(1);
      }

      mv.visitMethodInsn(183, cmrField.getAccessorInternalName(), "<init>", "(Ljavax/ejb/EntityBean;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;)V", false);
      mv.visitFieldInsn(181, this.implClassName, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor());
   }

   private void createCmrGetter(CmrField cmrField) {
      if (!cmrField.isSynthetic()) {
         String methodName = getterName(cmrField.getName());
         MethodVisitor mv = this.cw.visitMethod(1, methodName, "()" + cmrField.getProxyDescriptor(), (String)null, (String[])null);
         mv.visitCode();
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor());
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitMethodInsn(182, cmrField.getAccessorInternalName(), "get", cmrField.getCmrStyle().getGetterDescriptor(), false);
         if (cmrField.getCmrStyle() == CmrStyle.SINGLE) {
            mv.visitTypeInsn(192, cmrField.getProxyType().getInternalName());
         }

         mv.visitInsn(176);
         mv.visitMaxs(0, 0);
         mv.visitEnd();
      }
   }

   private void createCmrSetter(CmrField cmrField) {
      if (!cmrField.isSynthetic()) {
         String methodName = this.setterName(cmrField.getName());
         MethodVisitor mv = this.cw.visitMethod(1, methodName, "(" + cmrField.getProxyDescriptor() + ")V", (String)null, (String[])null);
         mv.visitCode();
         if (cmrField.getCmrStyle() != CmrStyle.SINGLE) {
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, this.implClassName, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor());
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
            mv.visitVarInsn(25, 1);
            mv.visitMethodInsn(182, cmrField.getAccessorInternalName(), "set", cmrField.getCmrStyle().getSetterDescriptor(), false);
            mv.visitInsn(177);
         } else {
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, this.implClassName, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor());
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
            mv.visitVarInsn(25, 1);
            mv.visitMethodInsn(182, cmrField.getAccessorInternalName(), "set", cmrField.getCmrStyle().getSetterDescriptor(), false);
            mv.visitTypeInsn(192, cmrField.getType().getInternalName());
            mv.visitFieldInsn(181, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
            mv.visitInsn(177);
         }

         mv.visitMaxs(0, 0);
         mv.visitEnd();
      }
   }

   private void createOpenEJB_deleted(MethodVisitor mv, CmrField cmrField) {
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, cmrField.getName() + "Cmr", cmrField.getAccessorDescriptor());
      mv.visitVarInsn(25, 0);
      mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
      mv.visitMethodInsn(182, cmrField.getAccessorInternalName(), "deleted", cmrField.getCmrStyle().getDeletedDescriptor(), false);
   }

   private void createOpenEJB_addCmr(MethodVisitor mv, CmrField cmrField) {
      mv.visitLdcInsn(cmrField.getName());
      mv.visitVarInsn(25, 1);
      mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
      Label end = new Label();
      mv.visitJumpInsn(153, end);
      if (cmrField.getCmrStyle() != CmrStyle.SINGLE) {
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitVarInsn(58, 3);
         mv.visitVarInsn(25, 3);
         Label fieldNotNull = new Label();
         mv.visitJumpInsn(199, fieldNotNull);
         mv.visitTypeInsn(187, cmrField.getInitialValueType().getInternalName());
         mv.visitInsn(89);
         mv.visitMethodInsn(183, cmrField.getInitialValueType().getInternalName(), "<init>", "()V", false);
         mv.visitVarInsn(58, 3);
         mv.visitLabel(fieldNotNull);
         mv.visitVarInsn(25, 3);
         mv.visitVarInsn(25, 2);
         mv.visitMethodInsn(185, cmrField.getCmrStyle().getCollectionType().getInternalName(), "add", "(Ljava/lang/Object;)Z", true);
         mv.visitInsn(87);
         mv.visitVarInsn(25, 0);
         mv.visitVarInsn(25, 3);
         mv.visitFieldInsn(181, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitInsn(1);
         mv.visitInsn(176);
      } else {
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitVarInsn(25, 0);
         mv.visitVarInsn(25, 2);
         mv.visitTypeInsn(192, cmrField.getType().getInternalName());
         mv.visitFieldInsn(181, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitInsn(176);
      }

      mv.visitLabel(end);
   }

   private void createOpenEJB_removeCmr(MethodVisitor mv, CmrField cmrField) {
      mv.visitLdcInsn(cmrField.getName());
      mv.visitVarInsn(25, 1);
      mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
      Label end = new Label();
      mv.visitJumpInsn(153, end);
      if (cmrField.getCmrStyle() != CmrStyle.SINGLE) {
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitVarInsn(25, 2);
         mv.visitMethodInsn(185, cmrField.getCmrStyle().getCollectionType().getInternalName(), "remove", "(Ljava/lang/Object;)Z", true);
         mv.visitInsn(87);
         mv.visitInsn(177);
      } else {
         mv.visitVarInsn(25, 0);
         mv.visitInsn(1);
         mv.visitFieldInsn(181, this.implClassName, cmrField.getName(), cmrField.getDescriptor());
         mv.visitInsn(177);
      }

      mv.visitLabel(end);
   }

   private void createSelectMethod(Method selectMethod) {
      Class<?> returnType = selectMethod.getReturnType();
      Method executeMethod = EjbSelect.getSelectMethod(returnType);
      MethodVisitor mv = this.cw.visitMethod(1, selectMethod.getName(), Type.getMethodDescriptor(selectMethod), (String)null, getExceptionTypes(selectMethod));
      mv.visitCode();
      mv.visitFieldInsn(178, this.implClassName, "deploymentInfo", "Ljava/lang/Object;");
      mv.visitLdcInsn(this.getSelectMethodSignature(selectMethod));
      if (!returnType.isPrimitive()) {
         mv.visitLdcInsn(returnType.getName());
      }

      mv.visitIntInsn(16, selectMethod.getParameterTypes().length);
      mv.visitTypeInsn(189, "java/lang/Object");
      int i = 0;

      for(Class<?> parameterType : selectMethod.getParameterTypes()) {
         mv.visitInsn(89);
         bipush(mv, i);
         mv.visitVarInsn(Type.getType(parameterType).getOpcode(21), i + 1);
         Cmp2Generator.Convert.toObjectFrom(mv, parameterType);
         mv.visitInsn(83);
         if (!Long.TYPE.equals(parameterType) && !Double.TYPE.equals(parameterType)) {
            ++i;
         } else {
            i += 2;
         }
      }

      mv.visitMethodInsn(184, Type.getInternalName(executeMethod.getDeclaringClass()), executeMethod.getName(), Type.getMethodDescriptor(executeMethod), false);
      if (!Void.TYPE.equals(returnType)) {
         if (!returnType.isPrimitive()) {
            Cmp2Generator.Convert.fromObjectTo(mv, returnType);
         }

         mv.visitInsn(Type.getReturnType(selectMethod).getOpcode(172));
      } else {
         mv.visitInsn(177);
      }

      mv.visitMaxs(0, 0);
      mv.visitEnd();
   }

   private String getSelectMethodSignature(Method selectMethod) {
      StringBuilder signature = new StringBuilder();
      signature.append(selectMethod.getName());
      if (selectMethod.getParameterTypes().length > 0) {
         signature.append('(');
         boolean first = true;

         for(Class<?> parameterType : selectMethod.getParameterTypes()) {
            if (!first) {
               signature.append(',');
            }

            signature.append(parameterType.getCanonicalName());
            first = false;
         }

         signature.append(')');
      }

      return signature.toString();
   }

   private static String[] getExceptionTypes(Method method) {
      List<String> types = new ArrayList(method.getExceptionTypes().length);

      for(Class<?> exceptionType : method.getExceptionTypes()) {
         types.add(Type.getInternalName(exceptionType));
      }

      return (String[])types.toArray(new String[types.size()]);
   }

   private static void bipush(MethodVisitor mv, int i) {
      switch (i) {
         case -1:
            mv.visitInsn(2);
            break;
         case 0:
            mv.visitInsn(3);
            break;
         case 1:
            mv.visitInsn(4);
            break;
         case 2:
            mv.visitInsn(5);
            break;
         case 3:
            mv.visitInsn(6);
            break;
         case 4:
            mv.visitInsn(7);
            break;
         case 5:
            mv.visitInsn(8);
            break;
         default:
            mv.visitIntInsn(16, i);
      }

   }

   public void createEjbActivate() {
      MethodVisitor mv = this.cw.visitMethod(1, "ejbActivate", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   public void createEjbLoad() {
      MethodVisitor mv = this.cw.visitMethod(1, "ejbLoad", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   public void createEjbPassivate() {
      MethodVisitor mv = this.cw.visitMethod(1, "ejbPassivate", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   public void createEjbRemove() {
      MethodVisitor mv = this.cw.visitMethod(1, "ejbRemove", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   public void createEjbStore() {
      MethodVisitor mv = this.cw.visitMethod(1, "ejbStore", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   public void createSetEntityContext() {
      MethodVisitor mv = this.cw.visitMethod(1, "setEntityContext", "(Ljavax/ejb/EntityContext;)V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 2);
      mv.visitEnd();
   }

   public void createUnsetEntityContext() {
      MethodVisitor mv = this.cw.visitMethod(1, "unsetEntityContext", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitInsn(177);
      mv.visitMaxs(0, 1);
      mv.visitEnd();
   }

   private static final class Convert {
      private static final Map<Class, Convert> conversionsByPrimitive = new HashMap();
      public static final Convert BOOLEAN;
      public static final Convert CHAR;
      public static final Convert BYTE;
      public static final Convert SHORT;
      public static final Convert INT;
      public static final Convert LONG;
      public static final Convert FLOAT;
      public static final Convert DOUBLE;
      private final Type objectType;
      private final Method toPrimitive;
      private final Method toObject;

      public static void toObjectFrom(MethodVisitor mv, Class from) {
         if (from.isPrimitive()) {
            Convert conversion = getConversion(from);
            if (conversion == null) {
               throw new NullPointerException("conversion is null " + from.getName() + " " + from.isPrimitive());
            }

            conversion.primitiveToObject(mv);
         }

      }

      public static void fromObjectTo(MethodVisitor mv, Class to) {
         if (!to.equals(Object.class)) {
            if (!to.isPrimitive()) {
               mv.visitTypeInsn(192, Type.getInternalName(to));
            } else {
               Convert conversion = getConversion(to);
               if (conversion == null) {
                  throw new NullPointerException("unsupported conversion for EJB select return type " + to.getName());
               }

               conversion.objectToPrimitive(mv);
            }
         }

      }

      public static Convert getConversion(Class primitive) {
         if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException(primitive.getName() + " is not a primitive class");
         } else {
            return (Convert)conversionsByPrimitive.get(primitive);
         }
      }

      private Convert(Class primitiveClass, Class objectClass, String toPrimitiveMethodName) {
         this.objectType = Type.getType(objectClass);

         try {
            this.toObject = objectClass.getMethod("valueOf", primitiveClass);
            this.toPrimitive = objectClass.getMethod(toPrimitiveMethodName);
         } catch (NoSuchMethodException e) {
            throw new OpenEJBRuntimeException(e);
         }

         conversionsByPrimitive.put(primitiveClass, this);
      }

      public void primitiveToObject(MethodVisitor mv) {
         mv.visitMethodInsn(184, this.objectType.getInternalName(), this.toObject.getName(), Type.getMethodDescriptor(this.toObject), false);
      }

      public void objectToPrimitive(MethodVisitor mv) {
         mv.visitTypeInsn(192, this.objectType.getInternalName());
         mv.visitMethodInsn(182, this.objectType.getInternalName(), this.toPrimitive.getName(), Type.getMethodDescriptor(this.toPrimitive), false);
      }

      static {
         BOOLEAN = new Convert(Boolean.TYPE, Boolean.class, "booleanValue");
         CHAR = new Convert(Character.TYPE, Character.class, "charValue");
         BYTE = new Convert(Byte.TYPE, Byte.class, "byteValue");
         SHORT = new Convert(Short.TYPE, Short.class, "shortValue");
         INT = new Convert(Integer.TYPE, Integer.class, "intValue");
         LONG = new Convert(Long.TYPE, Long.class, "longValue");
         FLOAT = new Convert(Float.TYPE, Float.class, "floatValue");
         DOUBLE = new Convert(Double.TYPE, Double.class, "doubleValue");
      }
   }
}
