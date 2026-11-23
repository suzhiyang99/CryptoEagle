package org.apache.openejb;

public enum BeanType {
   STATEFUL,
   STATELESS,
   SINGLETON,
   BMP_ENTITY,
   CMP_ENTITY,
   MESSAGE_DRIVEN,
   MANAGED;

   public boolean isEntity() {
      return this == CMP_ENTITY || this == BMP_ENTITY;
   }

   public boolean isSession() {
      return this == STATEFUL || this == STATELESS || this == SINGLETON || this == MANAGED;
   }

   public boolean isCdiCompatible() {
      return this.isSession() || this.isMessageDriven();
   }

   public boolean isMessageDriven() {
      return this == MESSAGE_DRIVEN;
   }
}
