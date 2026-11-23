package org.apache.activemq.artemis.api.core;

public enum RoutingType {
   MULTICAST,
   ANYCAST;

   public byte getType() {
      switch (this) {
         case MULTICAST:
            return 0;
         case ANYCAST:
            return 1;
         default:
            return -1;
      }
   }

   public static RoutingType getType(byte type) {
      switch (type) {
         case 0:
            return MULTICAST;
         case 1:
            return ANYCAST;
         default:
            return null;
      }
   }
}
