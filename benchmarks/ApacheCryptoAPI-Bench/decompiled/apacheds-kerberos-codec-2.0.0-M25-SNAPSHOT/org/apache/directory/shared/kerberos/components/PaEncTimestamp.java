package org.apache.directory.shared.kerberos.components;

public class PaEncTimestamp extends EncryptedData {
   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("PA-ENC-TIMESTAMP : {\n");
      sb.append(tabs).append(super.toString("    " + tabs)).append('\n');
      sb.append(tabs + "}\n");
      return sb.toString();
   }
}
