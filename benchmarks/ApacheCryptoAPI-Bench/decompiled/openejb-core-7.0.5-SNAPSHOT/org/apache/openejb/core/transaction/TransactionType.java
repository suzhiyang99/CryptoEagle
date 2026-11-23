package org.apache.openejb.core.transaction;

import javax.ejb.TransactionAttributeType;

public enum TransactionType {
   Mandatory,
   Never,
   NotSupported,
   Required,
   RequiresNew,
   Supports,
   BeanManaged;

   public static TransactionType get(TransactionAttributeType type) {
      switch (type) {
         case REQUIRED:
            return Required;
         case REQUIRES_NEW:
            return RequiresNew;
         case MANDATORY:
            return Mandatory;
         case NEVER:
            return Never;
         case NOT_SUPPORTED:
            return NotSupported;
         case SUPPORTS:
            return Supports;
         default:
            throw new IllegalArgumentException("Uknown TransactionAttributeType." + type);
      }
   }

   public static TransactionType get(String name) {
      for(TransactionType type : values()) {
         if (type.name().equalsIgnoreCase(name)) {
            return type;
         }
      }

      throw new IllegalArgumentException("Uknown TransactionType " + name);
   }
}
