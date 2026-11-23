package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class HttpChannel {
   protected static final Logger LOG = Log.getLogger(HttpChannel.class);
   private final HttpDestination _destination;
   private HttpExchange _exchange;

   protected HttpChannel(HttpDestination destination) {
      this._destination = destination;
   }

   public HttpDestination getHttpDestination() {
      return this._destination;
   }

   public boolean associate(HttpExchange exchange) {
      boolean result = false;
      boolean abort = true;
      synchronized(this) {
         if (this._exchange == null) {
            abort = false;
            result = exchange.associate(this);
            if (result) {
               this._exchange = exchange;
            }
         }
      }

      if (abort) {
         exchange.getRequest().abort(new UnsupportedOperationException("Pipelined requests not supported"));
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} associated {} to {}", exchange, result, this);
      }

      return result;
   }

   public boolean disassociate(HttpExchange exchange) {
      boolean result = false;
      synchronized(this) {
         HttpExchange existing = this._exchange;
         this._exchange = null;
         if (existing == exchange) {
            existing.disassociate(this);
            result = true;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} disassociated {} from {}", exchange, result, this);
      }

      return result;
   }

   public HttpExchange getHttpExchange() {
      synchronized(this) {
         return this._exchange;
      }
   }

   protected abstract HttpSender getHttpSender();

   protected abstract HttpReceiver getHttpReceiver();

   public abstract void send();

   public abstract void release();

   public void proceed(HttpExchange exchange, Throwable failure) {
      this.getHttpSender().proceed(exchange, failure);
   }

   public boolean abort(HttpExchange exchange, Throwable requestFailure, Throwable responseFailure) {
      boolean requestAborted = false;
      if (requestFailure != null) {
         requestAborted = this.getHttpSender().abort(exchange, requestFailure);
      }

      boolean responseAborted = false;
      if (responseFailure != null) {
         responseAborted = this.abortResponse(exchange, responseFailure);
      }

      return requestAborted || responseAborted;
   }

   public boolean abortResponse(HttpExchange exchange, Throwable failure) {
      return this.getHttpReceiver().abort(exchange, failure);
   }

   public void exchangeTerminated(HttpExchange exchange, Result result) {
      this.disassociate(exchange);
   }

   public String toString() {
      return String.format("%s@%x(exchange=%s)", this.getClass().getSimpleName(), this.hashCode(), this.getHttpExchange());
   }
}
