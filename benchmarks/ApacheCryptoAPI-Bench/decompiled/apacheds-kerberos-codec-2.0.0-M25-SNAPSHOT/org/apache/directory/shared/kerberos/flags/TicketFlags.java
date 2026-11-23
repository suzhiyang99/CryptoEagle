package org.apache.directory.shared.kerberos.flags;

public class TicketFlags extends AbstractKerberosFlags {
   public TicketFlags() {
   }

   public TicketFlags(int flags) {
      super(flags);
   }

   public TicketFlags(byte[] flags) {
      super(flags);
   }

   public boolean isReserved() {
      return this.isFlagSet(TicketFlag.RESERVED);
   }

   public boolean isForwardable() {
      return this.isFlagSet(TicketFlag.FORWARDABLE);
   }

   public boolean isForwarded() {
      return this.isFlagSet(TicketFlag.FORWARDED);
   }

   public boolean isProxiable() {
      return this.isFlagSet(TicketFlag.PROXIABLE);
   }

   public boolean isProxy() {
      return this.isFlagSet(TicketFlag.PROXY);
   }

   public boolean isMayPosdate() {
      return this.isFlagSet(TicketFlag.MAY_POSTDATE);
   }

   public boolean isPostdated() {
      return this.isFlagSet(TicketFlag.POSTDATED);
   }

   public boolean isInvalid() {
      return this.isFlagSet(TicketFlag.INVALID);
   }

   public boolean isRenewable() {
      return this.isFlagSet(TicketFlag.RENEWABLE);
   }

   public boolean isInitial() {
      return this.isFlagSet(TicketFlag.INITIAL);
   }

   public boolean isPreAuth() {
      return this.isFlagSet(TicketFlag.PRE_AUTHENT);
   }

   public boolean isHwAuthent() {
      return this.isFlagSet(TicketFlag.HW_AUTHENT);
   }

   public boolean isTransitedPolicyChecked() {
      return this.isFlagSet(TicketFlag.TRANSITED_POLICY_CHECKED);
   }

   public boolean isOkAsDelegate() {
      return this.isFlagSet(TicketFlag.OK_AS_DELEGATE);
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      if (this.isFlagSet(TicketFlag.RESERVED)) {
         result.append("RESERVED(0) ");
      }

      if (this.isFlagSet(TicketFlag.FORWARDABLE)) {
         result.append("FORWARDABLE(1) ");
      }

      if (this.isFlagSet(TicketFlag.FORWARDED)) {
         result.append("FORWARDED(2) ");
      }

      if (this.isFlagSet(TicketFlag.PROXIABLE)) {
         result.append("PROXIABLE(3) ");
      }

      if (this.isFlagSet(TicketFlag.PROXY)) {
         result.append("PROXY(4) ");
      }

      if (this.isFlagSet(TicketFlag.MAY_POSTDATE)) {
         result.append("MAY_POSTDATE(5) ");
      }

      if (this.isFlagSet(TicketFlag.POSTDATED)) {
         result.append("POSTDATED(6) ");
      }

      if (this.isFlagSet(TicketFlag.INVALID)) {
         result.append("INVALID(7) ");
      }

      if (this.isFlagSet(TicketFlag.RENEWABLE)) {
         result.append("RENEWABLE(8) ");
      }

      if (this.isFlagSet(TicketFlag.INITIAL)) {
         result.append("INITIAL(9) ");
      }

      if (this.isFlagSet(TicketFlag.PRE_AUTHENT)) {
         result.append("PRE_AUTHENT(10) ");
      }

      if (this.isFlagSet(TicketFlag.HW_AUTHENT)) {
         result.append("HW_AUTHENT(11) ");
      }

      if (this.isFlagSet(TicketFlag.TRANSITED_POLICY_CHECKED)) {
         result.append("TRANSITED_POLICY_CHECKED(12) ");
      }

      if (this.isFlagSet(TicketFlag.OK_AS_DELEGATE)) {
         result.append("OK_AS_DELEGATE(13) ");
      }

      return result.toString().trim();
   }
}
