package org.apache.openejb.core.cmp.cmp2;

import org.apache.xbean.asm5.Type;

public class CmrField {
   private final String name;
   private final CmrStyle cmrStyle;
   private final Type type;
   private final Type proxyType;
   private final String relatedName;
   private final boolean synthetic;

   public CmrField(String fieldName, String fieldType, String cmpImplClass, String local, String relatedName, boolean synthetic) {
      this.synthetic = synthetic;
      this.name = fieldName;
      if (fieldType == null) {
         this.cmrStyle = CmrStyle.SINGLE;
      } else if ("java.util.Collection".equals(fieldType)) {
         this.cmrStyle = CmrStyle.COLLECTION;
      } else {
         if (!"java.util.Set".equals(fieldType)) {
            throw new IllegalArgumentException("Unsupported fieldType " + fieldType);
         }

         this.cmrStyle = CmrStyle.SET;
      }

      this.type = Type.getType("L" + cmpImplClass.replace('.', '/') + ";");
      if (local != null) {
         this.proxyType = Type.getType("L" + local.replace('.', '/') + ";");
      } else {
         this.proxyType = null;
      }

      this.relatedName = relatedName;
   }

   public String getName() {
      return this.name;
   }

   public boolean isSynthetic() {
      return this.synthetic;
   }

   public CmrStyle getCmrStyle() {
      return this.cmrStyle;
   }

   public Type getType() {
      return this.type;
   }

   public Type getProxyType() {
      return this.proxyType;
   }

   public Type getInitialValueType() {
      return this.cmrStyle.getIntiCollectionType();
   }

   public String getRelatedName() {
      return this.relatedName;
   }

   public String getDescriptor() {
      Type collectionType = this.cmrStyle.getCollectionType();
      return collectionType == null ? this.type.getDescriptor() : collectionType.getDescriptor();
   }

   public String getGenericSignature() {
      Type collectionType = this.cmrStyle.getCollectionType();
      return collectionType == null ? null : createSignature(collectionType, this.type);
   }

   public String getProxyDescriptor() {
      Type collectionType = this.cmrStyle.getCollectionType();
      return collectionType == null ? this.proxyType.getDescriptor() : collectionType.getDescriptor();
   }

   public String getAccessorInternalName() {
      return this.cmrStyle.getAccessorType().getInternalName();
   }

   public String getAccessorDescriptor() {
      return this.cmrStyle.getAccessorType().getDescriptor();
   }

   public String getAccessorGenericSignature() {
      Type collectionType = this.cmrStyle.getCollectionType();
      return collectionType == null ? null : createSignature(this.cmrStyle.getAccessorType(), this.type, this.proxyType);
   }

   private static String createSignature(Type type, Type... genericTypes) {
      StringBuilder builder = new StringBuilder();
      builder.append("L").append(type.getInternalName());
      if (genericTypes.length > 0) {
         builder.append("<");

         for(Type genericType : genericTypes) {
            builder.append(genericType.getDescriptor());
         }

         builder.append(">");
      }

      builder.append(";");
      return builder.toString();
   }
}
