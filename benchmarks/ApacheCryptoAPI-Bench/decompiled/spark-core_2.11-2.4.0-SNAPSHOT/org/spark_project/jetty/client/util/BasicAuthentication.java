package org.spark_project.jetty.client.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.spark_project.jetty.client.api.Authentication;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.util.Attributes;
import org.spark_project.jetty.util.B64Code;

public class BasicAuthentication extends AbstractAuthentication {
   private final String user;
   private final String password;

   public BasicAuthentication(URI uri, String realm, String user, String password) {
      super(uri, realm);
      this.user = user;
      this.password = password;
   }

   public String getType() {
      return "Basic";
   }

   public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
      return new BasicResult(this.getURI(), headerInfo.getHeader(), this.user, this.password);
   }

   public static class BasicResult implements Authentication.Result {
      private final URI uri;
      private final HttpHeader header;
      private final String value;

      public BasicResult(URI uri, String user, String password) {
         this(uri, HttpHeader.AUTHORIZATION, user, password);
      }

      public BasicResult(URI uri, HttpHeader header, String user, String password) {
         this.uri = uri;
         this.header = header;
         this.value = "Basic " + B64Code.encode(user + ":" + password, StandardCharsets.ISO_8859_1);
      }

      public URI getURI() {
         return this.uri;
      }

      public void apply(Request request) {
         request.header(this.header, this.value);
      }

      public String toString() {
         return String.format("Basic authentication result for %s", this.getURI());
      }
   }
}
