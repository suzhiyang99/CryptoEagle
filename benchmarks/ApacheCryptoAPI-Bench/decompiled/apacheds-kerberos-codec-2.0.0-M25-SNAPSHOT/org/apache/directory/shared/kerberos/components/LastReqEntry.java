package org.apache.directory.shared.kerberos.components;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.LastReqType;

public class LastReqEntry {
   private LastReqType lrType;
   private KerberosTime lrValue;

   public LastReqEntry() {
   }

   public LastReqEntry(LastReqType lrType, KerberosTime lrValue) {
      this.lrType = lrType;
      this.lrValue = lrValue;
   }

   public LastReqType getLrType() {
      return this.lrType;
   }

   public void setLrType(LastReqType lrType) {
      this.lrType = lrType;
   }

   public KerberosTime getLrValue() {
      return this.lrValue;
   }

   public void setLrValue(KerberosTime lrValue) {
      this.lrValue = lrValue;
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("LastRequestEntry : {\n");
      sb.append(tabs).append("    lrType : ").append(this.lrType).append("\n");
      sb.append(tabs).append("    lrValue : ").append(this.lrValue).append("\n");
      sb.append(tabs).append("}");
      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }
}
