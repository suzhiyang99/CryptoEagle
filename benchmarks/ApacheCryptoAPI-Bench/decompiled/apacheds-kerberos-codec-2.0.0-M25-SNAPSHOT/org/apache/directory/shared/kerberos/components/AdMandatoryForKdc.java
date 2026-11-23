package org.apache.directory.shared.kerberos.components;

public class AdMandatoryForKdc extends AuthorizationData {
   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("AD-MANDATORY-FOR-KDC : {\n");
      sb.append(tabs).append(super.toString("    " + tabs)).append('\n');
      sb.append(tabs + "}\n");
      return sb.toString();
   }
}
