package org.spark_project.jetty.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.IteratingNestedCallback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ResponseNotifier {
   private static final Logger LOG = Log.getLogger(ResponseNotifier.class);

   public void notifyBegin(List<Response.ResponseListener> listeners, Response response) {
      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.BeginListener) {
            this.notifyBegin((Response.BeginListener)listener, response);
         }
      }

   }

   private void notifyBegin(Response.BeginListener listener, Response response) {
      try {
         listener.onBegin(response);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public boolean notifyHeader(List<Response.ResponseListener> listeners, Response response, HttpField field) {
      boolean result = true;

      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.HeaderListener) {
            result &= this.notifyHeader((Response.HeaderListener)listener, response, field);
         }
      }

      return result;
   }

   private boolean notifyHeader(Response.HeaderListener listener, Response response, HttpField field) {
      try {
         return listener.onHeader(response, field);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
         return false;
      }
   }

   public void notifyHeaders(List<Response.ResponseListener> listeners, Response response) {
      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.HeadersListener) {
            this.notifyHeaders((Response.HeadersListener)listener, response);
         }
      }

   }

   private void notifyHeaders(Response.HeadersListener listener, Response response) {
      try {
         listener.onHeaders(response);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public void notifyContent(List<Response.ResponseListener> listeners, Response response, ByteBuffer buffer, Callback callback) {
      ContentCallback contentCallback = new ContentCallback(listeners, response, buffer, callback);
      contentCallback.iterate();
   }

   private void notifyContent(Response.AsyncContentListener listener, Response response, ByteBuffer buffer, Callback callback) {
      try {
         listener.onContent(response, buffer, callback);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public void notifySuccess(List<Response.ResponseListener> listeners, Response response) {
      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.SuccessListener) {
            this.notifySuccess((Response.SuccessListener)listener, response);
         }
      }

   }

   private void notifySuccess(Response.SuccessListener listener, Response response) {
      try {
         listener.onSuccess(response);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public void notifyFailure(List<Response.ResponseListener> listeners, Response response, Throwable failure) {
      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.FailureListener) {
            this.notifyFailure((Response.FailureListener)listener, response, failure);
         }
      }

   }

   private void notifyFailure(Response.FailureListener listener, Response response, Throwable failure) {
      try {
         listener.onFailure(response, failure);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public void notifyComplete(List<Response.ResponseListener> listeners, Result result) {
      for(int i = 0; i < listeners.size(); ++i) {
         Response.ResponseListener listener = (Response.ResponseListener)listeners.get(i);
         if (listener instanceof Response.CompleteListener) {
            this.notifyComplete((Response.CompleteListener)listener, result);
         }
      }

   }

   private void notifyComplete(Response.CompleteListener listener, Result result) {
      try {
         listener.onComplete(result);
      } catch (Throwable x) {
         LOG.info("Exception while notifying listener " + listener, x);
      }

   }

   public void forwardSuccess(List<Response.ResponseListener> listeners, Response response) {
      this.notifyBegin(listeners, response);
      Iterator<HttpField> iterator = response.getHeaders().iterator();

      while(iterator.hasNext()) {
         HttpField field = (HttpField)iterator.next();
         if (!this.notifyHeader(listeners, response, field)) {
            iterator.remove();
         }
      }

      this.notifyHeaders(listeners, response);
      if (response instanceof ContentResponse) {
         this.notifyContent(listeners, response, ByteBuffer.wrap(((ContentResponse)response).getContent()), Callback.NOOP);
      }

      this.notifySuccess(listeners, response);
   }

   public void forwardSuccessComplete(List<Response.ResponseListener> listeners, Request request, Response response) {
      this.forwardSuccess(listeners, response);
      this.notifyComplete(listeners, new Result(request, response));
   }

   public void forwardFailure(List<Response.ResponseListener> listeners, Response response, Throwable failure) {
      this.notifyBegin(listeners, response);
      Iterator<HttpField> iterator = response.getHeaders().iterator();

      while(iterator.hasNext()) {
         HttpField field = (HttpField)iterator.next();
         if (!this.notifyHeader(listeners, response, field)) {
            iterator.remove();
         }
      }

      this.notifyHeaders(listeners, response);
      if (response instanceof ContentResponse) {
         this.notifyContent(listeners, response, ByteBuffer.wrap(((ContentResponse)response).getContent()), Callback.NOOP);
      }

      this.notifyFailure(listeners, response, failure);
   }

   public void forwardFailureComplete(List<Response.ResponseListener> listeners, Request request, Throwable requestFailure, Response response, Throwable responseFailure) {
      this.forwardFailure(listeners, response, responseFailure);
      this.notifyComplete(listeners, new Result(request, requestFailure, response, responseFailure));
   }

   private class ContentCallback extends IteratingNestedCallback {
      private final List<Response.ResponseListener> listeners;
      private final Response response;
      private final ByteBuffer buffer;
      private int index;

      private ContentCallback(List<Response.ResponseListener> listeners, Response response, ByteBuffer buffer, Callback callback) {
         super(callback);
         this.listeners = listeners;
         this.response = response;
         this.buffer = buffer.slice();
      }

      protected IteratingCallback.Action process() throws Exception {
         if (this.index == this.listeners.size()) {
            return IteratingCallback.Action.SUCCEEDED;
         } else {
            Response.ResponseListener listener = (Response.ResponseListener)this.listeners.get(this.index);
            if (listener instanceof Response.AsyncContentListener) {
               this.buffer.clear();
               ResponseNotifier.this.notifyContent((Response.AsyncContentListener)listener, this.response, this.buffer, this);
               return IteratingCallback.Action.SCHEDULED;
            } else {
               this.succeeded();
               return IteratingCallback.Action.SCHEDULED;
            }
         }
      }

      public void succeeded() {
         ++this.index;
         super.succeeded();
      }
   }
}
