package org.spark_project.jetty.client;

import java.util.List;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HttpExchange {
   private static final Logger LOG = Log.getLogger(HttpExchange.class);
   private final HttpDestination destination;
   private final HttpRequest request;
   private final List<Response.ResponseListener> listeners;
   private final HttpResponse response;
   private State requestState;
   private State responseState;
   private HttpChannel _channel;
   private Throwable requestFailure;
   private Throwable responseFailure;

   public HttpExchange(HttpDestination destination, HttpRequest request, List<Response.ResponseListener> listeners) {
      this.requestState = HttpExchange.State.PENDING;
      this.responseState = HttpExchange.State.PENDING;
      this.destination = destination;
      this.request = request;
      this.listeners = listeners;
      this.response = new HttpResponse(request, listeners);
      HttpConversation conversation = request.getConversation();
      conversation.getExchanges().offer(this);
      conversation.updateResponseListeners((Response.ResponseListener)null);
   }

   public HttpConversation getConversation() {
      return this.request.getConversation();
   }

   public HttpRequest getRequest() {
      return this.request;
   }

   public Throwable getRequestFailure() {
      synchronized(this) {
         return this.requestFailure;
      }
   }

   public List<Response.ResponseListener> getResponseListeners() {
      return this.listeners;
   }

   public HttpResponse getResponse() {
      return this.response;
   }

   public Throwable getResponseFailure() {
      synchronized(this) {
         return this.responseFailure;
      }
   }

   boolean associate(HttpChannel channel) {
      boolean result = false;
      boolean abort = false;
      synchronized(this) {
         if (this.requestState == HttpExchange.State.PENDING && this.responseState == HttpExchange.State.PENDING) {
            abort = this._channel != null;
            if (!abort) {
               this._channel = channel;
               result = true;
            }
         }
      }

      if (abort) {
         this.request.abort(new IllegalStateException(this.toString()));
      }

      return result;
   }

   void disassociate(HttpChannel channel) {
      boolean abort = false;
      synchronized(this) {
         if (this._channel != channel || this.requestState != HttpExchange.State.TERMINATED || this.responseState != HttpExchange.State.TERMINATED) {
            abort = true;
         }

         this._channel = null;
      }

      if (abort) {
         this.request.abort(new IllegalStateException(this.toString()));
      }

   }

   private HttpChannel getHttpChannel() {
      synchronized(this) {
         return this._channel;
      }
   }

   public boolean requestComplete(Throwable failure) {
      synchronized(this) {
         return this.completeRequest(failure);
      }
   }

   private boolean completeRequest(Throwable failure) {
      if (this.requestState == HttpExchange.State.PENDING) {
         this.requestState = HttpExchange.State.COMPLETED;
         this.requestFailure = failure;
         return true;
      } else {
         return false;
      }
   }

   public boolean responseComplete(Throwable failure) {
      synchronized(this) {
         return this.completeResponse(failure);
      }
   }

   private boolean completeResponse(Throwable failure) {
      if (this.responseState == HttpExchange.State.PENDING) {
         this.responseState = HttpExchange.State.COMPLETED;
         this.responseFailure = failure;
         return true;
      } else {
         return false;
      }
   }

   public Result terminateRequest() {
      Result result = null;
      synchronized(this) {
         if (this.requestState == HttpExchange.State.COMPLETED) {
            this.requestState = HttpExchange.State.TERMINATED;
         }

         if (this.requestState == HttpExchange.State.TERMINATED && this.responseState == HttpExchange.State.TERMINATED) {
            result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminated request for {}, result: {}", this, result);
      }

      return result;
   }

   public Result terminateResponse() {
      Result result = null;
      synchronized(this) {
         if (this.responseState == HttpExchange.State.COMPLETED) {
            this.responseState = HttpExchange.State.TERMINATED;
         }

         if (this.requestState == HttpExchange.State.TERMINATED && this.responseState == HttpExchange.State.TERMINATED) {
            result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminated response for {}, result: {}", this, result);
      }

      return result;
   }

   public boolean abort(Throwable failure) {
      boolean abortRequest;
      boolean abortResponse;
      synchronized(this) {
         abortRequest = this.completeRequest(failure);
         abortResponse = this.completeResponse(failure);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Failed {}: req={}/rsp={} {}", this, abortRequest, abortResponse, failure);
      }

      if (!abortRequest && !abortResponse) {
         return false;
      } else if (this.destination.remove(this)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Aborting while queued {}: {}", this, failure);
         }

         this.notifyFailureComplete(failure);
         return true;
      } else {
         HttpChannel channel = this.getHttpChannel();
         if (channel == null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborted before association {}: {}", this, failure);
            }

            this.notifyFailureComplete(failure);
            return true;
         } else {
            boolean aborted = channel.abort(this, abortRequest ? failure : null, abortResponse ? failure : null);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborted ({}) while active {}: {}", aborted, this, failure);
            }

            return aborted;
         }
      }
   }

   private void notifyFailureComplete(Throwable failure) {
      this.destination.getRequestNotifier().notifyFailure(this.request, failure);
      List<Response.ResponseListener> listeners = this.getConversation().getResponseListeners();
      ResponseNotifier responseNotifier = this.destination.getResponseNotifier();
      responseNotifier.notifyFailure(listeners, this.response, failure);
      responseNotifier.notifyComplete(listeners, new Result(this.request, failure, this.response, failure));
   }

   public void resetResponse() {
      synchronized(this) {
         this.responseState = HttpExchange.State.PENDING;
         this.responseFailure = null;
      }
   }

   public void proceed(Throwable failure) {
      HttpChannel channel = this.getHttpChannel();
      if (channel != null) {
         channel.proceed(this, failure);
      }

   }

   public String toString() {
      synchronized(this) {
         return String.format("%s@%x req=%s/%s@%h res=%s/%s@%h", HttpExchange.class.getSimpleName(), this.hashCode(), this.requestState, this.requestFailure, this.requestFailure, this.responseState, this.responseFailure, this.responseFailure);
      }
   }

   private static enum State {
      PENDING,
      COMPLETED,
      TERMINATED;
   }
}
