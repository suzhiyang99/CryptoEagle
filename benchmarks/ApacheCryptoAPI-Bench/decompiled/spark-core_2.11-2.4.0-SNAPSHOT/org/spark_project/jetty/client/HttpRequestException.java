package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Request;

public class HttpRequestException extends RuntimeException {
   private final Request request;

   public HttpRequestException(String message, Request request) {
      super(message);
      this.request = request;
   }

   public Request getRequest() {
      return this.request;
   }
}
