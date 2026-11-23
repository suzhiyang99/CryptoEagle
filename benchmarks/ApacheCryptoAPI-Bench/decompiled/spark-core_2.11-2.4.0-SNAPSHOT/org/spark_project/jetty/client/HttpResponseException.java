package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Response;

public class HttpResponseException extends RuntimeException {
   private final Response response;

   public HttpResponseException(String message, Response response) {
      super(message);
      this.response = response;
   }

   public Response getResponse() {
      return this.response;
   }
}
