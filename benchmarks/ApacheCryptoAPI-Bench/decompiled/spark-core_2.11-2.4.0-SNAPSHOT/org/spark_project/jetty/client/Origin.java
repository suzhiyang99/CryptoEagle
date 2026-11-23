package org.spark_project.jetty.client;

import java.util.Objects;
import org.spark_project.jetty.util.URIUtil;

public class Origin {
   private final String scheme;
   private final Address address;

   public Origin(String scheme, String host, int port) {
      this(scheme, new Address(host, port));
   }

   public Origin(String scheme, Address address) {
      this.scheme = (String)Objects.requireNonNull(scheme);
      this.address = address;
   }

   public String getScheme() {
      return this.scheme;
   }

   public Address getAddress() {
      return this.address;
   }

   public String asString() {
      StringBuilder result = new StringBuilder();
      URIUtil.appendSchemeHostPort(result, this.scheme, this.address.host, this.address.port);
      return result.toString();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Origin that = (Origin)obj;
         return this.scheme.equals(that.scheme) && this.address.equals(that.address);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.scheme.hashCode();
      result = 31 * result + this.address.hashCode();
      return result;
   }

   public static class Address {
      private final String host;
      private final int port;

      public Address(String host, int port) {
         this.host = (String)Objects.requireNonNull(host);
         this.port = port;
      }

      public String getHost() {
         return this.host;
      }

      public int getPort() {
         return this.port;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            Address that = (Address)obj;
            return this.host.equals(that.host) && this.port == that.port;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.host.hashCode();
         result = 31 * result + this.port;
         return result;
      }

      public String asString() {
         return String.format("%s:%d", this.host, this.port);
      }

      public String toString() {
         return this.asString();
      }
   }
}
