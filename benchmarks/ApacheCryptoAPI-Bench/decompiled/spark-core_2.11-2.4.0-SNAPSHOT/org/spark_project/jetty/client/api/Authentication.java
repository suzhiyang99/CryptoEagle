package org.spark_project.jetty.client.api;

import java.net.URI;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.util.Attributes;

public interface Authentication {
   String ANY_REALM = "<<ANY_REALM>>";

   boolean matches(String var1, URI var2, String var3);

   Result authenticate(Request var1, ContentResponse var2, HeaderInfo var3, Attributes var4);

   public static class HeaderInfo {
      private final String type;
      private final String realm;
      private final String params;
      private final HttpHeader header;

      public HeaderInfo(String type, String realm, String params, HttpHeader header) {
         this.type = type;
         this.realm = realm;
         this.params = params;
         this.header = header;
      }

      public String getType() {
         return this.type;
      }

      public String getRealm() {
         return this.realm;
      }

      public String getParameters() {
         return this.params;
      }

      public HttpHeader getHeader() {
         return this.header;
      }
   }

   public interface Result {
      URI getURI();

      void apply(Request var1);
   }
}
