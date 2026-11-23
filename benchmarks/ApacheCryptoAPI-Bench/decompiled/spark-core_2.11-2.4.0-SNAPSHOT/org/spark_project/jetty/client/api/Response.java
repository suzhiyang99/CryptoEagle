package org.spark_project.jetty.client.api;

import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.List;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.util.Callback;

public interface Response {
   Request getRequest();

   <T extends ResponseListener> List<T> getListeners(Class<T> var1);

   HttpVersion getVersion();

   int getStatus();

   String getReason();

   HttpFields getHeaders();

   boolean abort(Throwable var1);

   public interface AsyncContentListener extends ResponseListener {
      void onContent(Response var1, ByteBuffer var2, Callback var3);
   }

   public interface BeginListener extends ResponseListener {
      void onBegin(Response var1);
   }

   public interface CompleteListener extends ResponseListener {
      void onComplete(Result var1);
   }

   public interface ContentListener extends ResponseListener {
      void onContent(Response var1, ByteBuffer var2);
   }

   public interface FailureListener extends ResponseListener {
      void onFailure(Response var1, Throwable var2);
   }

   public interface HeaderListener extends ResponseListener {
      boolean onHeader(Response var1, HttpField var2);
   }

   public interface HeadersListener extends ResponseListener {
      void onHeaders(Response var1);
   }

   public interface Listener extends BeginListener, HeaderListener, HeadersListener, ContentListener, AsyncContentListener, SuccessListener, FailureListener, CompleteListener {
      public static class Adapter implements Listener {
         public void onBegin(Response response) {
         }

         public boolean onHeader(Response response, HttpField field) {
            return true;
         }

         public void onHeaders(Response response) {
         }

         public void onContent(Response response, ByteBuffer content) {
         }

         public void onContent(Response response, ByteBuffer content, Callback callback) {
            try {
               this.onContent(response, content);
               callback.succeeded();
            } catch (Throwable x) {
               callback.failed(x);
            }

         }

         public void onSuccess(Response response) {
         }

         public void onFailure(Response response, Throwable failure) {
         }

         public void onComplete(Result result) {
         }
      }
   }

   public interface ResponseListener extends EventListener {
   }

   public interface SuccessListener extends ResponseListener {
      void onSuccess(Response var1);
   }
}
