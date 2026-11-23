package org.spark_project.jetty.client.http;

import java.nio.channels.AsynchronousCloseException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.spark_project.jetty.client.HttpConnection;
import org.spark_project.jetty.client.HttpDestination;
import org.spark_project.jetty.client.HttpExchange;
import org.spark_project.jetty.client.SendFailure;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Sweeper;

public class HttpConnectionOverHTTP extends AbstractConnection implements Connection, Sweeper.Sweepable {
   private static final Logger LOG = Log.getLogger(HttpConnectionOverHTTP.class);
   private final AtomicBoolean closed = new AtomicBoolean();
   private final AtomicInteger sweeps = new AtomicInteger();
   private final Promise<Connection> promise;
   private final Delegate delegate;
   private final HttpChannelOverHTTP channel;
   private long idleTimeout;

   public HttpConnectionOverHTTP(EndPoint endPoint, HttpDestination destination, Promise<Connection> promise) {
      super(endPoint, destination.getHttpClient().getExecutor());
      this.promise = promise;
      this.delegate = new Delegate(destination);
      this.channel = this.newHttpChannel();
   }

   protected HttpChannelOverHTTP newHttpChannel() {
      return new HttpChannelOverHTTP(this);
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return this.channel;
   }

   public HttpDestinationOverHTTP getHttpDestination() {
      return (HttpDestinationOverHTTP)this.delegate.getHttpDestination();
   }

   public void send(Request request, Response.CompleteListener listener) {
      this.delegate.send(request, listener);
   }

   protected SendFailure send(HttpExchange exchange) {
      return this.delegate.send(exchange);
   }

   public void onOpen() {
      super.onOpen();
      this.fillInterested();
      this.promise.succeeded(this);
   }

   public boolean isClosed() {
      return this.closed.get();
   }

   public boolean onIdleExpired() {
      long idleTimeout = this.getEndPoint().getIdleTimeout();
      boolean close = this.delegate.onIdleTimeout(idleTimeout);
      if (close) {
         this.close(new TimeoutException("Idle timeout " + idleTimeout + " ms"));
      }

      return false;
   }

   public void onFillable() {
      HttpExchange exchange = this.channel.getHttpExchange();
      if (exchange != null) {
         this.channel.receive();
      } else {
         this.close();
      }

   }

   public void release() {
      this.getEndPoint().setIdleTimeout(this.idleTimeout);
      this.getHttpDestination().release(this);
   }

   public void close() {
      this.close(new AsynchronousCloseException());
   }

   protected void close(Throwable failure) {
      if (this.closed.compareAndSet(false, true)) {
         this.getHttpDestination().close(this);
         this.abort(failure);
         this.getEndPoint().shutdownOutput();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Shutdown {}", this);
         }

         this.getEndPoint().close();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Closed {}", this);
         }
      }

   }

   protected boolean abort(Throwable failure) {
      HttpExchange exchange = this.channel.getHttpExchange();
      return exchange != null && exchange.getRequest().abort(failure);
   }

   public boolean sweep() {
      if (!this.closed.get()) {
         return false;
      } else {
         return this.sweeps.incrementAndGet() >= 4;
      }
   }

   public String toString() {
      return String.format("%s@%h(l:%s <-> r:%s,closed=%b)[%s]", this.getClass().getSimpleName(), this, this.getEndPoint().getLocalAddress(), this.getEndPoint().getRemoteAddress(), this.closed.get(), this.channel);
   }

   private class Delegate extends HttpConnection {
      private Delegate(HttpDestination destination) {
         super(destination);
      }

      protected SendFailure send(HttpExchange exchange) {
         Request request = exchange.getRequest();
         this.normalizeRequest(request);
         EndPoint endPoint = HttpConnectionOverHTTP.this.getEndPoint();
         HttpConnectionOverHTTP.this.idleTimeout = endPoint.getIdleTimeout();
         endPoint.setIdleTimeout(request.getIdleTimeout());
         return this.send(HttpConnectionOverHTTP.this.channel, exchange);
      }

      public void close() {
         HttpConnectionOverHTTP.this.close();
      }

      public boolean isClosed() {
         return HttpConnectionOverHTTP.this.isClosed();
      }

      public String toString() {
         return HttpConnectionOverHTTP.this.toString();
      }
   }
}
