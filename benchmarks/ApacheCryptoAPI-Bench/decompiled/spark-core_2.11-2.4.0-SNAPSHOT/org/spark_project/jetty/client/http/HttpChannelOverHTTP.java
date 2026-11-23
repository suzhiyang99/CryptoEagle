package org.spark_project.jetty.client.http;

import org.spark_project.jetty.client.HttpChannel;
import org.spark_project.jetty.client.HttpExchange;
import org.spark_project.jetty.client.HttpReceiver;
import org.spark_project.jetty.client.HttpSender;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpVersion;

public class HttpChannelOverHTTP extends HttpChannel {
   private final HttpConnectionOverHTTP connection;
   private final HttpSenderOverHTTP sender;
   private final HttpReceiverOverHTTP receiver;

   public HttpChannelOverHTTP(HttpConnectionOverHTTP connection) {
      super(connection.getHttpDestination());
      this.connection = connection;
      this.sender = this.newHttpSender();
      this.receiver = this.newHttpReceiver();
   }

   protected HttpSenderOverHTTP newHttpSender() {
      return new HttpSenderOverHTTP(this);
   }

   protected HttpReceiverOverHTTP newHttpReceiver() {
      return new HttpReceiverOverHTTP(this);
   }

   protected HttpSender getHttpSender() {
      return this.sender;
   }

   protected HttpReceiver getHttpReceiver() {
      return this.receiver;
   }

   public HttpConnectionOverHTTP getHttpConnection() {
      return this.connection;
   }

   public void send() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         this.sender.send(exchange);
      }

   }

   public void release() {
      this.connection.release();
   }

   public void receive() {
      this.receiver.receive();
   }

   public void exchangeTerminated(HttpExchange exchange, Result result) {
      super.exchangeTerminated(exchange, result);
      Response response = result.getResponse();
      HttpFields responseHeaders = response.getHeaders();
      String closeReason = null;
      if (result.isFailed()) {
         closeReason = "failure";
      } else if (this.receiver.isShutdown()) {
         closeReason = "server close";
      } else if (this.sender.isShutdown()) {
         closeReason = "client close";
      }

      if (closeReason == null) {
         if (response.getVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            boolean keepAlive = responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.asString());
            boolean connect = HttpMethod.CONNECT.is(exchange.getRequest().getMethod());
            if (!keepAlive && !connect) {
               closeReason = "http/1.0";
            }
         } else if (responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString())) {
            closeReason = "http/1.1";
         }
      }

      if (closeReason != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Closing, reason: {} - {}", closeReason, this.connection);
         }

         this.connection.close();
      } else {
         this.release();
      }

   }

   public String toString() {
      return String.format("%s[send=%s,recv=%s]", super.toString(), this.sender, this.receiver);
   }
}
