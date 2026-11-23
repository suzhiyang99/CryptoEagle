package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.xbean.asm5.Type;

public enum CmrStyle {
   SINGLE(SingleValuedCmr.class, (Class)null, (Class)null),
   COLLECTION(SetValuedCmr.class, Collection.class, HashSet.class),
   SET(SetValuedCmr.class, Set.class, HashSet.class);

   private final Type accessorType;
   private final Type collectionType;
   private final Type intiCollectionType;
   private final String getterDescriptor;
   private final String setterDescriptor;
   private final String deletedDescriptor;

   private CmrStyle(Class accessorClass, Class collectionClass, Class initCollectionClass) {
      this.accessorType = Type.getType(accessorClass);
      if (collectionClass != null) {
         this.collectionType = Type.getType(collectionClass);
         this.intiCollectionType = Type.getType(initCollectionClass);
      } else {
         this.collectionType = null;
         this.intiCollectionType = null;
      }

      String getterDescriptor = null;
      String setterDescriptor = null;
      String deletedDescriptor = null;

      for(Method method : accessorClass.getMethods()) {
         if ("get".equals(method.getName())) {
            getterDescriptor = Type.getMethodDescriptor(method);
         }

         if ("set".equals(method.getName())) {
            setterDescriptor = Type.getMethodDescriptor(method);
         }

         if ("deleted".equals(method.getName())) {
            deletedDescriptor = Type.getMethodDescriptor(method);
         }
      }

      if (getterDescriptor == null) {
         throw new AssertionError("No get method found in cmr accessor class " + accessorClass.getName());
      } else if (setterDescriptor == null) {
         throw new AssertionError("No set method found in cmr accessor class " + accessorClass.getName());
      } else if (deletedDescriptor == null) {
         throw new AssertionError("No deleted method found in cmr accessor class " + accessorClass.getName());
      } else {
         this.getterDescriptor = getterDescriptor;
         this.setterDescriptor = setterDescriptor;
         this.deletedDescriptor = deletedDescriptor;
      }
   }

   public String getCmrFieldDescriptor(Type relatedType) {
      String relatedDescriptor = relatedType.getDescriptor();
      return this.collectionType != null ? this.collectionType.getDescriptor() + "<" + relatedDescriptor + ">" : relatedDescriptor;
   }

   public Type getAccessorType() {
      return this.accessorType;
   }

   public Type getCollectionType() {
      return this.collectionType;
   }

   public Type getIntiCollectionType() {
      return this.intiCollectionType;
   }

   public String getGetterDescriptor() {
      return this.getterDescriptor;
   }

   public String getSetterDescriptor() {
      return this.setterDescriptor;
   }

   public String getDeletedDescriptor() {
      return this.deletedDescriptor;
   }
}
