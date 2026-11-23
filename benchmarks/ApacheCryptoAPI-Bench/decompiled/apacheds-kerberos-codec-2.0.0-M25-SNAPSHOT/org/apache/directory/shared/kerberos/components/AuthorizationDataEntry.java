package org.apache.directory.shared.kerberos.components;

import java.util.Arrays;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.types.AuthorizationType;

public class AuthorizationDataEntry {
   private AuthorizationType adType;
   private byte[] adData;

   public AuthorizationDataEntry() {
   }

   public AuthorizationDataEntry(AuthorizationType adType, byte[] adData) {
      this.adType = adType;
      if (adData != null) {
         this.adData = new byte[adData.length];
         System.arraycopy(adData, 0, this.adData, 0, adData.length);
      }

   }

   public AuthorizationType getAdType() {
      return this.adType;
   }

   public void setAdType(AuthorizationType adType) {
      this.adType = adType;
   }

   public byte[] getAdData() {
      if (Strings.isEmpty(this.adData)) {
         return Strings.EMPTY_BYTES;
      } else {
         byte[] copy = new byte[this.adData.length];
         System.arraycopy(this.adData, 0, copy, 0, this.adData.length);
         return copy;
      }
   }

   public byte[] getAdDataRef() {
      return this.adData;
   }

   public void setAdData(byte[] adData) {
      if (Strings.isEmpty(adData)) {
         this.adData = Strings.EMPTY_BYTES;
      } else {
         this.adData = new byte[adData.length];
         System.arraycopy(adData, 0, this.adData, 0, adData.length);
      }

   }

   public int hashCode() {
      int prime = 31;
      int result = 17;
      result = 31 * result + Arrays.hashCode(this.adData);
      result = 31 * result + (this.adType == null ? 0 : this.adType.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         AuthorizationDataEntry other = (AuthorizationDataEntry)obj;
         if (!Arrays.equals(this.adData, other.adData)) {
            return false;
         } else {
            return this.adType == other.adType;
         }
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("AuthorizationDataEntry : {\n");
      sb.append(tabs).append("    adType : ").append(this.adType).append("\n");
      sb.append(tabs).append("    adData : ").append(Strings.dumpBytes(this.adData)).append("\n");
      sb.append(tabs).append("}");
      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }
}
