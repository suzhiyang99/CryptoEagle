package org.spark_project.jetty.client;

import java.util.ArrayList;
import java.util.List;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpVersion;

public class HttpResponse implements Response {
   private final HttpFields headers = new HttpFields();
   private final Request request;
   private final List<Response.ResponseListener> listeners;
   private HttpVersion version;
   private int status;
   private String reason;

   public HttpResponse(Request request, List<Response.ResponseListener> listeners) {
      this.request = request;
      this.listeners = listeners;
   }

   public Request getRequest() {
      return this.request;
   }

   public HttpVersion getVersion() {
      return this.version;
   }

   public HttpResponse version(HttpVersion version) {
      this.version = version;
      return this;
   }

   public int getStatus() {
      return this.status;
   }

   public HttpResponse status(int status) {
      this.status = status;
      return this;
   }

   public String getReason() {
      return this.reason;
   }

   public HttpResponse reason(String reason) {
      this.reason = reason;
      return this;
   }

   public HttpFields getHeaders() {
      return this.headers;
   }

   public <T extends Response.ResponseListener> List<T> getListeners(Class<T> type) {
      ArrayList<T> result = new ArrayList();

      for(Response.ResponseListener listener : this.listeners) {
         if (type == null || type.isInstance(listener)) {
            result.add(listener);
         }
      }

      return result;
   }

   public boolean abort(Throwable cause) {
      return this.request.abort(cause);
   }

   public String toString() {
      return String.format("%s[%s %d %s]@%x", HttpResponse.class.getSimpleName(), this.getVersion(), this.getStatus(), this.getReason(), this.hashCode());
   }
}
