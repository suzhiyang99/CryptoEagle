package org.spark_project.jetty.client;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class HttpSender implements AsyncContentProvider.Listener {
   protected static final Logger LOG = Log.getLogger(HttpSender.class);
   private final AtomicReference<RequestState> requestState;
   private final AtomicReference<SenderState> senderState;
   private final Callback commitCallback;
   private final IteratingCallback contentCallback;
   private final Callback lastCallback;
   private final HttpChannel channel;
   private HttpContent content;
   private Throwable failure;

   protected HttpSender(HttpChannel channel) {
      this.requestState = new AtomicReference(HttpSender.RequestState.QUEUED);
      this.senderState = new AtomicReference(HttpSender.SenderState.IDLE);
      this.commitCallback = new CommitCallback();
      this.contentCallback = new ContentCallback();
      this.lastCallback = new LastContentCallback();
      this.channel = channel;
   }

   protected HttpChannel getHttpChannel() {
      return this.channel;
   }

   protected HttpExchange getHttpExchange() {
      return this.channel.getHttpExchange();
   }

   public void onContent() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         while(true) {
            SenderState current = (SenderState)this.senderState.get();
            switch (current) {
               case IDLE:
                  SenderState newSenderState = HttpSender.SenderState.SENDING;
                  if (this.updateSenderState(current, newSenderState)) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Deferred content available, {} -> {}", current, newSenderState);
                     }

                     this.contentCallback.iterate();
                     return;
                  }
                  break;
               case SENDING:
                  SenderState newSenderState = HttpSender.SenderState.SENDING_WITH_CONTENT;
                  if (this.updateSenderState(current, newSenderState)) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Deferred content available, {} -> {}", current, newSenderState);
                     }

                     return;
                  }
                  break;
               case EXPECTING:
                  SenderState newSenderState = HttpSender.SenderState.EXPECTING_WITH_CONTENT;
                  if (this.updateSenderState(current, newSenderState)) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Deferred content available, {} -> {}", current, newSenderState);
                     }

                     return;
                  }
                  break;
               case PROCEEDING:
                  SenderState newSenderState = HttpSender.SenderState.PROCEEDING_WITH_CONTENT;
                  if (this.updateSenderState(current, newSenderState)) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Deferred content available, {} -> {}", current, newSenderState);
                     }

                     return;
                  }
                  break;
               case SENDING_WITH_CONTENT:
               case EXPECTING_WITH_CONTENT:
               case PROCEEDING_WITH_CONTENT:
               case WAITING:
               case COMPLETED:
               case FAILED:
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Deferred content available, {}", current);
                  }

                  return;
               default:
                  this.illegalSenderState(current);
                  return;
            }
         }
      }
   }

   public void send(HttpExchange exchange) {
      if (this.queuedToBegin(exchange)) {
         Request request = exchange.getRequest();
         ContentProvider contentProvider = request.getContent();
         HttpContent content = this.content = new HttpContent(contentProvider);
         SenderState newSenderState = HttpSender.SenderState.SENDING;
         if (this.expects100Continue(request)) {
            newSenderState = content.hasContent() ? HttpSender.SenderState.EXPECTING_WITH_CONTENT : HttpSender.SenderState.EXPECTING;
         }

         label30:
         while(true) {
            SenderState current = (SenderState)this.senderState.get();
            switch (current) {
               case IDLE:
               case COMPLETED:
                  if (this.updateSenderState(current, newSenderState)) {
                     break label30;
                  }
                  break;
               default:
                  this.illegalSenderState(current);
                  return;
            }
         }

         if (contentProvider instanceof AsyncContentProvider) {
            ((AsyncContentProvider)contentProvider).setListener(this);
         }

         if (this.beginToHeaders(exchange)) {
            this.sendHeaders(exchange, content, this.commitCallback);
         }
      }
   }

   protected boolean expects100Continue(Request request) {
      return request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
   }

   protected boolean queuedToBegin(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.QUEUED, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         Request request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request begin {}", request);
         }

         RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
         notifier.notifyBegin(request);
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.BEGIN)) {
            return true;
         } else {
            this.terminateRequest(exchange);
            return false;
         }
      }
   }

   protected boolean beginToHeaders(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.BEGIN, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         Request request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request headers {}{}{}", request, System.lineSeparator(), request.getHeaders().toString().trim());
         }

         RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
         notifier.notifyHeaders(request);
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.HEADERS)) {
            return true;
         } else {
            this.terminateRequest(exchange);
            return false;
         }
      }
   }

   protected boolean headersToCommit(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.HEADERS, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         Request request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request committed {}", request);
         }

         RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
         notifier.notifyCommit(request);
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.COMMIT)) {
            return true;
         } else {
            this.terminateRequest(exchange);
            return false;
         }
      }
   }

   protected boolean someToContent(HttpExchange exchange, ByteBuffer content) {
      RequestState current = (RequestState)this.requestState.get();
      switch (current) {
         case COMMIT:
         case CONTENT:
            if (!this.updateRequestState(current, HttpSender.RequestState.TRANSIENT)) {
               return false;
            } else {
               Request request = exchange.getRequest();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Request content {}{}{}", request, System.lineSeparator(), BufferUtil.toDetailString(content));
               }

               RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
               notifier.notifyContent(request, content);
               if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.CONTENT)) {
                  return true;
               }

               this.terminateRequest(exchange);
               return false;
            }
         default:
            return false;
      }
   }

   protected boolean someToSuccess(HttpExchange exchange) {
      RequestState current = (RequestState)this.requestState.get();
      switch (current) {
         case COMMIT:
         case CONTENT:
            if (!exchange.requestComplete((Throwable)null)) {
               return false;
            }

            this.requestState.set(HttpSender.RequestState.QUEUED);
            this.reset();
            Request request = exchange.getRequest();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Request success {}", request);
            }

            HttpDestination destination = this.getHttpChannel().getHttpDestination();
            destination.getRequestNotifier().notifySuccess(exchange.getRequest());
            Result result = exchange.terminateRequest();
            this.terminateRequest(exchange, (Throwable)null, result);
            return true;
         default:
            return false;
      }
   }

   protected boolean anyToFailure(Throwable failure) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         return exchange.requestComplete(failure) ? this.abort(exchange, failure) : false;
      }
   }

   private void terminateRequest(HttpExchange exchange) {
      Throwable failure = this.failure;
      if (failure == null) {
         failure = new HttpRequestException("Concurrent failure", exchange.getRequest());
      }

      Result result = exchange.terminateRequest();
      this.terminateRequest(exchange, failure, result);
   }

   private void terminateRequest(HttpExchange exchange, Throwable failure, Result result) {
      Request request = exchange.getRequest();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminating request {}", request);
      }

      if (result == null) {
         if (failure != null && exchange.responseComplete(failure)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Response failure from request {} {}", request, exchange);
            }

            this.getHttpChannel().abortResponse(exchange, failure);
         }
      } else {
         HttpDestination destination = this.getHttpChannel().getHttpDestination();
         boolean ordered = destination.getHttpClient().isStrictEventOrdering();
         if (!ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Request/Response {}: {}", failure == null ? "succeeded" : "failed", result);
         }

         HttpConversation conversation = exchange.getConversation();
         destination.getResponseNotifier().notifyComplete(conversation.getResponseListeners(), result);
         if (ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }
      }

   }

   protected abstract void sendHeaders(HttpExchange var1, HttpContent var2, Callback var3);

   protected abstract void sendContent(HttpExchange var1, HttpContent var2, Callback var3);

   protected void reset() {
      HttpContent content = this.content;
      this.content = null;
      content.close();
      this.senderState.set(HttpSender.SenderState.COMPLETED);
   }

   protected void dispose() {
      HttpContent content = this.content;
      this.content = null;
      if (content != null) {
         content.close();
      }

      this.senderState.set(HttpSender.SenderState.FAILED);
   }

   public void proceed(HttpExchange exchange, Throwable failure) {
      if (this.expects100Continue(exchange.getRequest())) {
         if (failure != null) {
            this.anyToFailure(failure);
         } else {
            while(true) {
               SenderState current = (SenderState)this.senderState.get();
               switch (current) {
                  case EXPECTING:
                     if (this.updateSenderState(current, HttpSender.SenderState.PROCEEDING)) {
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Proceeding while expecting");
                        }

                        return;
                     }
                     break;
                  case PROCEEDING:
                  case SENDING_WITH_CONTENT:
                  case PROCEEDING_WITH_CONTENT:
                  case COMPLETED:
                  default:
                     this.illegalSenderState(current);
                     return;
                  case EXPECTING_WITH_CONTENT:
                     if (this.updateSenderState(current, HttpSender.SenderState.PROCEEDING_WITH_CONTENT)) {
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Proceeding while scheduled");
                        }

                        return;
                     }
                     break;
                  case WAITING:
                     if (this.updateSenderState(current, HttpSender.SenderState.SENDING)) {
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Proceeding while waiting");
                        }

                        this.contentCallback.iterate();
                        return;
                     }
                     break;
                  case FAILED:
                     return;
               }
            }
         }
      }
   }

   public boolean abort(HttpExchange exchange, Throwable failure) {
      while(true) {
         RequestState current = (RequestState)this.requestState.get();
         switch (current) {
            case FAILURE:
               return false;
            default:
               if (this.updateRequestState(current, HttpSender.RequestState.FAILURE)) {
                  boolean terminate = current != HttpSender.RequestState.TRANSIENT;
                  this.failure = failure;
                  this.dispose();
                  HttpRequest request = exchange.getRequest();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Request failure {} {} on {}: {}", request, exchange, this.getHttpChannel(), failure);
                  }

                  HttpDestination destination = this.getHttpChannel().getHttpDestination();
                  destination.getRequestNotifier().notifyFailure(request, failure);
                  if (terminate) {
                     Result result = exchange.terminateRequest();
                     this.terminateRequest(exchange, failure, result);
                  } else if (LOG.isDebugEnabled()) {
                     LOG.debug("Concurrent failure: request termination skipped, performed by helpers");
                  }

                  return true;
               }
         }
      }
   }

   private boolean updateRequestState(RequestState from, RequestState to) {
      boolean updated = this.requestState.compareAndSet(from, to);
      if (!updated && LOG.isDebugEnabled()) {
         LOG.debug("RequestState update failed: {} -> {}: {}", from, to, this.requestState.get());
      }

      return updated;
   }

   private boolean updateSenderState(SenderState from, SenderState to) {
      boolean updated = this.senderState.compareAndSet(from, to);
      if (!updated && LOG.isDebugEnabled()) {
         LOG.debug("SenderState update failed: {} -> {}: {}", from, to, this.senderState.get());
      }

      return updated;
   }

   private void illegalSenderState(SenderState current) {
      this.anyToFailure(new IllegalStateException("Expected " + current + " found " + this.senderState.get() + " instead"));
   }

   public String toString() {
      return String.format("%s@%x(req=%s,snd=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.requestState, this.senderState, this.failure);
   }

   private static enum RequestState {
      TRANSIENT,
      QUEUED,
      BEGIN,
      HEADERS,
      COMMIT,
      CONTENT,
      FAILURE;
   }

   private static enum SenderState {
      IDLE,
      SENDING,
      SENDING_WITH_CONTENT,
      EXPECTING,
      EXPECTING_WITH_CONTENT,
      WAITING,
      PROCEEDING,
      PROCEEDING_WITH_CONTENT,
      COMPLETED,
      FAILED;
   }

   private class CommitCallback implements Callback {
      private CommitCallback() {
      }

      public boolean isNonBlocking() {
         return HttpSender.this.content.isNonBlocking();
      }

      public void succeeded() {
         try {
            HttpContent content = HttpSender.this.content;
            if (content == null) {
               return;
            }

            content.succeeded();
            this.process();
         } catch (Throwable x) {
            HttpSender.this.anyToFailure(x);
         }

      }

      public void failed(Throwable failure) {
         HttpContent content = HttpSender.this.content;
         if (content != null) {
            content.failed(failure);
            HttpSender.this.anyToFailure(failure);
         }
      }

      private void process() throws Exception {
         HttpExchange exchange = HttpSender.this.getHttpExchange();
         if (exchange != null) {
            if (HttpSender.this.headersToCommit(exchange)) {
               HttpContent content = HttpSender.this.content;
               if (content != null) {
                  if (!content.hasContent()) {
                     HttpSender.this.someToSuccess(exchange);
                  } else {
                     ByteBuffer contentBuffer = content.getContent();
                     if (contentBuffer == null || HttpSender.this.someToContent(exchange, contentBuffer)) {
                        while(true) {
                           SenderState current = (SenderState)HttpSender.this.senderState.get();
                           switch (current) {
                              case SENDING:
                                 HttpSender.this.contentCallback.iterate();
                                 return;
                              case EXPECTING:
                                 if (HttpSender.this.updateSenderState(current, HttpSender.SenderState.WAITING)) {
                                    return;
                                 }
                                 break;
                              case PROCEEDING:
                                 if (HttpSender.this.updateSenderState(current, HttpSender.SenderState.IDLE)) {
                                    return;
                                 }
                                 break;
                              case SENDING_WITH_CONTENT:
                                 HttpSender.this.updateSenderState(current, HttpSender.SenderState.SENDING);
                                 break;
                              case EXPECTING_WITH_CONTENT:
                                 if (HttpSender.this.updateSenderState(current, HttpSender.SenderState.WAITING)) {
                                    return;
                                 }
                                 break;
                              case PROCEEDING_WITH_CONTENT:
                                 HttpSender.this.updateSenderState(current, HttpSender.SenderState.SENDING);
                                 break;
                              case WAITING:
                              case COMPLETED:
                              default:
                                 HttpSender.this.illegalSenderState(current);
                                 return;
                              case FAILED:
                                 return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private class ContentCallback extends IteratingCallback {
      private ContentCallback() {
      }

      protected IteratingCallback.Action process() throws Exception {
         HttpExchange exchange = HttpSender.this.getHttpExchange();
         if (exchange == null) {
            return IteratingCallback.Action.IDLE;
         } else {
            HttpContent content = HttpSender.this.content;
            if (content == null) {
               return IteratingCallback.Action.IDLE;
            } else {
               while(true) {
                  boolean advanced = content.advance();
                  boolean lastContent = content.isLast();
                  if (HttpSender.LOG.isDebugEnabled()) {
                     HttpSender.LOG.debug("Content present {}, last {}, consumed {} for {}", advanced, lastContent, content.isConsumed(), exchange.getRequest());
                  }

                  if (advanced) {
                     HttpSender.this.sendContent(exchange, content, this);
                     return IteratingCallback.Action.SCHEDULED;
                  }

                  if (lastContent) {
                     HttpSender.this.sendContent(exchange, content, HttpSender.this.lastCallback);
                     return IteratingCallback.Action.IDLE;
                  }

                  SenderState current = (SenderState)HttpSender.this.senderState.get();
                  switch (current) {
                     case SENDING:
                        if (HttpSender.this.updateSenderState(current, HttpSender.SenderState.IDLE)) {
                           if (HttpSender.LOG.isDebugEnabled()) {
                              HttpSender.LOG.debug("Content is deferred for {}", exchange.getRequest());
                           }

                           return IteratingCallback.Action.IDLE;
                        }
                        break;
                     case SENDING_WITH_CONTENT:
                        HttpSender.this.updateSenderState(current, HttpSender.SenderState.SENDING);
                        break;
                     default:
                        HttpSender.this.illegalSenderState(current);
                        return IteratingCallback.Action.IDLE;
                  }
               }
            }
         }
      }

      public void succeeded() {
         HttpExchange exchange = HttpSender.this.getHttpExchange();
         if (exchange != null) {
            HttpContent content = HttpSender.this.content;
            if (content != null) {
               content.succeeded();
               ByteBuffer buffer = content.getContent();
               HttpSender.this.someToContent(exchange, buffer);
               super.succeeded();
            }
         }
      }

      public void onCompleteFailure(Throwable failure) {
         HttpContent content = HttpSender.this.content;
         if (content != null) {
            content.failed(failure);
            HttpSender.this.anyToFailure(failure);
         }
      }

      protected void onCompleteSuccess() {
      }
   }

   private class LastContentCallback implements Callback {
      private LastContentCallback() {
      }

      public boolean isNonBlocking() {
         return HttpSender.this.content.isNonBlocking();
      }

      public void succeeded() {
         HttpExchange exchange = HttpSender.this.getHttpExchange();
         if (exchange != null) {
            HttpContent content = HttpSender.this.content;
            if (content != null) {
               content.succeeded();
               HttpSender.this.someToSuccess(exchange);
            }
         }
      }

      public void failed(Throwable failure) {
         HttpContent content = HttpSender.this.content;
         if (content != null) {
            content.failed(failure);
            HttpSender.this.anyToFailure(failure);
         }
      }
   }
}
