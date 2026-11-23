package org.apache.directory.shared.kerberos.codec.types;

import org.apache.directory.api.ldap.model.constants.AuthenticationLevel;

public enum LastReqType {
   NONE(0, AuthenticationLevel.NONE.toString()),
   TIME_OF_INITIAL_TGT(1, "time of initial ticket"),
   TIME_OF_INITIAL_REQ(2, "time of initial request"),
   TIME_OF_NEWEST_TGT(3, "time of newest ticket"),
   TIME_OF_LAST_RENEWAL(4, "time of last renewal"),
   TIME_OF_LAST_REQ(5, "time of last request"),
   TIME_OF_PASSWORD_EXP(6, "time of password expiration");

   private String name;
   private int value;

   private LastReqType(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public static LastReqType getTypeByValue(int type) {
      for(LastReqType lrt : values()) {
         if (type == lrt.getValue()) {
            return lrt;
         }
      }

      return NONE;
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      return this.name + " (" + this.value + ")";
   }
}
