package org.apache.directory.shared.kerberos.codec.types;

public enum HostAddrType {
   NULL(0),
   ADDRTYPE_INET(2),
   ADDRTYPE_IMPLINK(3),
   ADDRTYPE_CHAOS(5),
   ADDRTYPE_XNS(6),
   ADDRTYPE_OSI(7),
   ADDRTYPE_DECNET(12),
   ADDRTYPE_APPLETALK(16),
   ADDRTYPE_NETBIOS(20),
   ADDRTYPE_INET6(24);

   private final int value;

   private HostAddrType(int value) {
      this.value = value;
   }

   public static HostAddrType getTypeByOrdinal(int type) {
      switch (type) {
         case 0:
            return NULL;
         case 1:
         case 4:
         case 8:
         case 9:
         case 10:
         case 11:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 21:
         case 22:
         case 23:
         default:
            return NULL;
         case 2:
            return ADDRTYPE_INET;
         case 3:
            return ADDRTYPE_IMPLINK;
         case 5:
            return ADDRTYPE_CHAOS;
         case 6:
            return ADDRTYPE_XNS;
         case 7:
            return ADDRTYPE_OSI;
         case 12:
            return ADDRTYPE_DECNET;
         case 16:
            return ADDRTYPE_APPLETALK;
         case 20:
            return ADDRTYPE_NETBIOS;
         case 24:
            return ADDRTYPE_INET6;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      switch (this.value) {
         case 2:
            return "Internet(" + this.value + ")";
         case 3:
            return "Arpanet(" + this.value + ")";
         case 4:
         case 8:
         case 9:
         case 10:
         case 11:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 21:
         case 22:
         case 23:
         default:
            return "null(" + this.value + ")";
         case 5:
            return "CHAOS(" + this.value + ")";
         case 6:
            return "XEROX Network Services(" + this.value + ")";
         case 7:
            return "OSI(" + this.value + ")";
         case 12:
            return "DECnet(" + this.value + ")";
         case 20:
            return "NetBios(" + this.value + ")";
         case 24:
            return "Internet Protocol V6(" + this.value + ")";
      }
   }
}
