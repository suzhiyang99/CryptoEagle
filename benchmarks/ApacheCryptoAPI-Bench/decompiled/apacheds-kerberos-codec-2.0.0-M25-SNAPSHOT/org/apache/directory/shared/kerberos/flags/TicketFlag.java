package org.apache.directory.shared.kerberos.flags;

public enum TicketFlag implements KerberosFlag {
   RESERVED(0),
   FORWARDABLE(1),
   FORWARDED(2),
   PROXIABLE(3),
   PROXY(4),
   MAY_POSTDATE(5),
   POSTDATED(6),
   INVALID(7),
   RENEWABLE(8),
   INITIAL(9),
   PRE_AUTHENT(10),
   HW_AUTHENT(11),
   TRANSITED_POLICY_CHECKED(12),
   OK_AS_DELEGATE(13),
   MAX_VALUE(32);

   private int value;

   private TicketFlag(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
