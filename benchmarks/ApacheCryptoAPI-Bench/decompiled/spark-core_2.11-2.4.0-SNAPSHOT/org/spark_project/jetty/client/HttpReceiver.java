package org.spark_project.jetty.client;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.CountingCallback;
import org.spark_project.jetty.util.component.Destroyable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class HttpReceiver {
   protected static final Logger LOG = Log.getLogger(HttpReceiver.class);
   private final AtomicReference<ResponseState> responseState;
   private final HttpChannel channel;
   private ContentDecoder decoder;
   private Throwable failure;

   protected HttpReceiver(HttpChannel channel) {
      this.responseState = new AtomicReference(HttpReceiver.ResponseState.IDLE);
      this.channel = channel;
   }

   protected HttpChannel getHttpChannel() {
      return this.channel;
   }

   protected HttpExchange getHttpExchange() {
      return this.channel.getHttpExchange();
   }

   protected HttpDestination getHttpDestination() {
      return this.channel.getHttpDestination();
   }

   protected boolean responseBegin(HttpExchange exchange) {
      if (!this.updateResponseState(HttpReceiver.ResponseState.IDLE, HttpReceiver.ResponseState.TRANSIENT)) {
         return false;
      } else {
         HttpConversation conversation = exchange.getConversation();
         HttpResponse response = exchange.getResponse();
         HttpDestination destination = this.getHttpDestination();
         HttpClient client = destination.getHttpClient();
         ProtocolHandler protocolHandler = client.findProtocolHandler(exchange.getRequest(), response);
         Response.Listener handlerListener = null;
         if (protocolHandler != null) {
            handlerListener = protocolHandler.getResponseListener();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Found protocol handler {}", protocolHandler);
            }
         }

         exchange.getConversation().updateResponseListeners(handlerListener);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Response begin {}", response);
         }

         ResponseNotifier notifier = destination.getResponseNotifier();
         notifier.notifyBegin(conversation.getResponseListeners(), response);
         if (this.updateResponseState(HttpReceiver.ResponseState.TRANSIENT, HttpReceiver.ResponseState.BEGIN)) {
            return true;
         } else {
            this.terminateResponse(exchange);
            return false;
         }
      }
   }

   protected boolean responseHeader(HttpExchange exchange, HttpField field) {
      while(true) {
         ResponseState current = (ResponseState)this.responseState.get();
         switch (current) {
            case BEGIN:
            case HEADER:
               if (!this.updateResponseState(current, HttpReceiver.ResponseState.TRANSIENT)) {
                  break;
               }

               HttpResponse response = exchange.getResponse();
               ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
               boolean process = notifier.notifyHeader(exchange.getConversation().getResponseListeners(), response, field);
               if (process) {
                  response.getHeaders().add(field);
                  HttpHeader fieldHeader = field.getHeader();
                  if (fieldHeader != null) {
                     switch (fieldHeader) {
                        case SET_COOKIE:
                        case SET_COOKIE2:
                           URI uri = exchange.getRequest().getURI();
                           if (uri != null) {
                              this.storeCookie(uri, field);
                           }
                     }
                  }
               }

               if (this.updateResponseState(HttpReceiver.ResponseState.TRANSIENT, HttpReceiver.ResponseState.HEADER)) {
                  return true;
               }

               this.terminateResponse(exchange);
               return false;
            default:
               return false;
         }
      }
   }

   protected void storeCookie(URI uri, HttpField field) {
      try {
         String value = field.getValue();
         if (value != null) {
            Map<String, List<String>> header = new HashMap(1);
            header.put(field.getHeader().asString(), Collections.singletonList(value));
            this.getHttpDestination().getHttpClient().getCookieManager().put(uri, header);
         }
      } catch (IOException x) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(x);
         }
      }

   }

   protected boolean responseHeaders(HttpExchange exchange) {
      while(true) {
         ResponseState current = (ResponseState)this.responseState.get();
         switch (current) {
            case BEGIN:
            case HEADER:
               if (!this.updateResponseState(current, HttpReceiver.ResponseState.TRANSIENT)) {
                  break;
               }

               HttpResponse response = exchange.getResponse();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Response headers {}{}{}", response, System.lineSeparator(), response.getHeaders().toString().trim());
               }

               ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
               notifier.notifyHeaders(exchange.getConversation().getResponseListeners(), response);
               Enumeration<String> contentEncodings = response.getHeaders().getValues(HttpHeader.CONTENT_ENCODING.asString(), ",");
               if (contentEncodings != null) {
                  for(ContentDecoder.Factory factory : this.getHttpDestination().getHttpClient().getContentDecoderFactories()) {
                     if (contentEncodings.hasMoreElements() && factory.getEncoding().equalsIgnoreCase((String)contentEncodings.nextElement())) {
                        this.decoder = factory.newContentDecoder();
                     }
                  }
               }

               if (this.updateResponseState(HttpReceiver.ResponseState.TRANSIENT, HttpReceiver.ResponseState.HEADERS)) {
                  return true;
               }

               this.terminateResponse(exchange);
               return false;
            default:
               return false;
         }
      }
   }

   protected boolean responseContent(HttpExchange exchange, ByteBuffer buffer, Callback callback) {
      while(true) {
         ResponseState current = (ResponseState)this.responseState.get();
         switch (current) {
            case HEADERS:
            case CONTENT:
               if (!this.updateResponseState(current, HttpReceiver.ResponseState.TRANSIENT)) {
                  break;
               }

               HttpResponse response = exchange.getResponse();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Response content {}{}{}", response, System.lineSeparator(), BufferUtil.toDetailString(buffer));
               }

               ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
               List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
               ContentDecoder decoder = this.decoder;
               if (decoder == null) {
                  notifier.notifyContent(listeners, response, buffer, callback);
               } else {
                  try {
                     List<ByteBuffer> decodeds = new ArrayList(2);

                     while(buffer.hasRemaining()) {
                        ByteBuffer decoded = decoder.decode(buffer);
                        if (decoded.hasRemaining()) {
                           decodeds.add(decoded);
                           if (LOG.isDebugEnabled()) {
                              LOG.debug("Response content decoded ({}) {}{}{}", decoder, response, System.lineSeparator(), BufferUtil.toDetailString(decoded));
                           }
                        }
                     }

                     if (decodeds.isEmpty()) {
                        callback.succeeded();
                     } else {
                        int size = decodeds.size();
                        CountingCallback counter = new CountingCallback(callback, size);

                        for(int i = 0; i < size; ++i) {
                           notifier.notifyContent(listeners, response, (ByteBuffer)decodeds.get(i), counter);
                        }
                     }
                  } catch (Throwable x) {
                     callback.failed(x);
                  }
               }

               if (this.updateResponseState(HttpReceiver.ResponseState.TRANSIENT, HttpReceiver.ResponseState.CONTENT)) {
                  return true;
               }

               this.terminateResponse(exchange);
               return false;
            default:
               callback.failed(new IllegalStateException("Invalid response state " + current));
               return false;
         }
      }
   }

   protected boolean responseSuccess(HttpExchange exchange) {
      if (!exchange.responseComplete((Throwable)null)) {
         return false;
      } else {
         this.responseState.set(HttpReceiver.ResponseState.IDLE);
         this.reset();
         HttpResponse response = exchange.getResponse();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Response success {}", response);
         }

         List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
         ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
         notifier.notifySuccess(listeners, response);
         if (exchange.getResponse().getStatus() == 100) {
            return true;
         } else {
            Result result = exchange.terminateResponse();
            this.terminateResponse(exchange, result);
            return true;
         }
      }
   }

   protected boolean responseFailure(Throwable failure) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         return exchange.responseComplete(failure) ? this.abort(exchange, failure) : false;
      }
   }

   private void terminateResponse(HttpExchange exchange) {
      Result result = exchange.terminateResponse();
      this.terminateResponse(exchange, result);
   }

   private void terminateResponse(HttpExchange exchange, Result result) {
      HttpResponse response = exchange.getResponse();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Response complete {}", response);
      }

      if (result != null) {
         boolean ordered = this.getHttpDestination().getHttpClient().isStrictEventOrdering();
         if (!ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Request/Response {}: {}", this.failure == null ? "succeeded" : "failed", result);
         }

         List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
         ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
         notifier.notifyComplete(listeners, result);
         if (ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }
      }

   }

   protected void reset() {
      destroyDecoder(this.decoder);
      this.decoder = null;
   }

   protected void dispose() {
      destroyDecoder(this.decoder);
      this.decoder = null;
   }

   private static void destroyDecoder(ContentDecoder decoder) {
      if (decoder instanceof Destroyable) {
         ((Destroyable)decoder).destroy();
      }

   }

   public boolean abort(HttpExchange exchange, Throwable failure) {
      while(true) {
         ResponseState current = (ResponseState)this.responseState.get();
         switch (current) {
            case FAILURE:
               return false;
            default:
               if (this.updateResponseState(current, HttpReceiver.ResponseState.FAILURE)) {
                  boolean terminate = current != HttpReceiver.ResponseState.TRANSIENT;
                  this.failure = failure;
                  this.dispose();
                  HttpResponse response = exchange.getResponse();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Response failure {} {} on {}: {}", response, exchange, this.getHttpChannel(), failure);
                  }

                  List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
                  ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
                  notifier.notifyFailure(listeners, response, failure);
                  if (terminate) {
                     Result result = exchange.terminateResponse();
                     this.terminateResponse(exchange, result);
                  } else if (LOG.isDebugEnabled()) {
                     LOG.debug("Concurrent failure: response termination skipped, performed by helpers");
                  }

                  return true;
               }
         }
      }
   }

   private boolean updateResponseState(ResponseState from, ResponseState to) {
      boolean updated = this.responseState.compareAndSet(from, to);
      if (!updated && LOG.isDebugEnabled()) {
         LOG.debug("State update failed: {} -> {}: {}", from, to, this.responseState.get());
      }

      return updated;
   }

   public String toString() {
      return String.format("%s@%x(rsp=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.responseState, this.failure);
   }

   private static enum ResponseState {
      TRANSIENT,
      IDLE,
      BEGIN,
      HEADER,
      HEADERS,
      CONTENT,
      FAILURE;
   }
}
