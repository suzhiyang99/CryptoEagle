package org.spark_project.jetty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.DispatcherType;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.http.BadMessageException;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpGenerator;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.HttpStatus;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.ChannelEndPoint;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ErrorHandler;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.SharedBlockingCallback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public class HttpChannel implements Runnable, HttpOutput.Interceptor {
   private static final Logger LOG = Log.getLogger(HttpChannel.class);
   private final AtomicBoolean _committed = new AtomicBoolean();
   private final AtomicInteger _requests = new AtomicInteger();
   private final Connector _connector;
   private final Executor _executor;
   private final HttpConfiguration _configuration;
   private final EndPoint _endPoint;
   private final HttpTransport _transport;
   private final HttpChannelState _state;
   private final Request _request;
   private final Response _response;
   private MetaData.Response _committedMetaData;
   private RequestLog _requestLog;
   private long _oldIdleTimeout;
   private long _written;

   public HttpChannel(Connector connector, HttpConfiguration configuration, EndPoint endPoint, HttpTransport transport) {
      this._connector = connector;
      this._configuration = configuration;
      this._endPoint = endPoint;
      this._transport = transport;
      this._state = new HttpChannelState(this);
      this._request = new Request(this, this.newHttpInput(this._state));
      this._response = new Response(this, this.newHttpOutput());
      this._executor = connector == null ? null : connector.getServer().getThreadPool();
      this._requestLog = connector == null ? null : connector.getServer().getRequestLog();
      if (LOG.isDebugEnabled()) {
         LOG.debug("new {} -> {},{},{}", this, this._endPoint, this._endPoint.getConnection(), this._state);
      }

   }

   protected HttpInput newHttpInput(HttpChannelState state) {
      return new HttpInput(state);
   }

   protected HttpOutput newHttpOutput() {
      return new HttpOutput(this);
   }

   public HttpChannelState getState() {
      return this._state;
   }

   public long getBytesWritten() {
      return this._written;
   }

   public int getRequests() {
      return this._requests.get();
   }

   public Connector getConnector() {
      return this._connector;
   }

   public HttpTransport getHttpTransport() {
      return this._transport;
   }

   public RequestLog getRequestLog() {
      return this._requestLog;
   }

   public void setRequestLog(RequestLog requestLog) {
      this._requestLog = requestLog;
   }

   public void addRequestLog(RequestLog requestLog) {
      if (this._requestLog == null) {
         this._requestLog = requestLog;
      } else if (this._requestLog instanceof RequestLogCollection) {
         ((RequestLogCollection)this._requestLog).add(requestLog);
      } else {
         this._requestLog = new RequestLogCollection(new RequestLog[]{this._requestLog, requestLog});
      }

   }

   public MetaData.Response getCommittedMetaData() {
      return this._committedMetaData;
   }

   public long getIdleTimeout() {
      return this._endPoint.getIdleTimeout();
   }

   public void setIdleTimeout(long timeoutMs) {
      this._endPoint.setIdleTimeout(timeoutMs);
   }

   public ByteBufferPool getByteBufferPool() {
      return this._connector.getByteBufferPool();
   }

   public HttpConfiguration getHttpConfiguration() {
      return this._configuration;
   }

   public boolean isOptimizedForDirectBuffers() {
      return this.getHttpTransport().isOptimizedForDirectBuffers();
   }

   public Server getServer() {
      return this._connector.getServer();
   }

   public Request getRequest() {
      return this._request;
   }

   public Response getResponse() {
      return this._response;
   }

   public EndPoint getEndPoint() {
      return this._endPoint;
   }

   public InetSocketAddress getLocalAddress() {
      return this._endPoint.getLocalAddress();
   }

   public InetSocketAddress getRemoteAddress() {
      return this._endPoint.getRemoteAddress();
   }

   public void continue100(int available) throws IOException {
      throw new UnsupportedOperationException();
   }

   public void recycle() {
      this._committed.set(false);
      this._request.recycle();
      this._response.recycle();
      this._committedMetaData = null;
      this._requestLog = this._connector == null ? null : this._connector.getServer().getRequestLog();
      this._written = 0L;
   }

   public void asyncReadFillInterested() {
   }

   public void run() {
      this.handle();
   }

   public boolean handle() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} handle {} ", this, this._request.getHttpURI());
      }

      HttpChannelState.Action action = this._state.handling();

      while(true) {
         label866: {
            if (!this.getServer().isStopped()) {
               try {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("{} action {}", this, action);
                  }

                  switch (action) {
                     case TERMINATED:
                     case WAIT:
                        break;
                     case DISPATCH:
                        if (!this._request.hasMetaData()) {
                           throw new IllegalStateException("state=" + this._state);
                        }

                        this._request.setHandled(false);
                        this._response.getHttpOutput().reopen();

                        try {
                           this._request.setDispatcherType(DispatcherType.REQUEST);
                           List<HttpConfiguration.Customizer> customizers = this._configuration.getCustomizers();
                           if (!customizers.isEmpty()) {
                              for(HttpConfiguration.Customizer customizer : customizers) {
                                 customizer.customize(this.getConnector(), this._configuration, this._request);
                                 if (this._request.isHandled()) {
                                    break;
                                 }
                              }
                           }

                           if (!this._request.isHandled()) {
                              this.getServer().handle(this);
                           }
                           break label866;
                        } finally {
                           this._request.setDispatcherType((DispatcherType)null);
                        }
                     case ASYNC_DISPATCH:
                        this._request.setHandled(false);
                        this._response.getHttpOutput().reopen();

                        try {
                           this._request.setDispatcherType(DispatcherType.ASYNC);
                           this.getServer().handleAsync(this);
                           break label866;
                        } finally {
                           this._request.setDispatcherType((DispatcherType)null);
                        }
                     case ERROR_DISPATCH:
                        Throwable ex = this._state.getAsyncContextEvent().getThrowable();
                        Integer loop_detect = (Integer)this._request.getAttribute("org.spark_project.jetty.server.ERROR_DISPATCH");
                        if (loop_detect == null) {
                           loop_detect = 1;
                        } else {
                           loop_detect = loop_detect + 1;
                        }

                        this._request.setAttribute("org.spark_project.jetty.server.ERROR_DISPATCH", loop_detect);
                        if (loop_detect <= this.getHttpConfiguration().getMaxErrorDispatches()) {
                           if (this._response.isCommitted()) {
                              if (LOG.isDebugEnabled()) {
                                 LOG.debug("Could not perform Error Dispatch because the response is already committed, aborting");
                              }

                              this._transport.abort(ex);
                           } else {
                              this._request.setHandled(false);
                              this._response.resetBuffer();
                              this._response.getHttpOutput().reopen();
                              String reason;
                              if (ex != null && !(ex instanceof TimeoutException)) {
                                 reason = HttpStatus.Code.INTERNAL_SERVER_ERROR.getMessage();
                                 this._request.setAttribute("javax.servlet.error.exception", ex);
                              } else {
                                 reason = "Async Timeout";
                              }

                              this._request.setAttribute("javax.servlet.error.status_code", 500);
                              this._request.setAttribute("javax.servlet.error.message", reason);
                              this._request.setAttribute("javax.servlet.error.request_uri", this._request.getRequestURI());
                              this._response.setStatusWithReason(500, reason);
                              ErrorHandler eh = ErrorHandler.getErrorHandler(this.getServer(), this._state.getContextHandler());
                              if (eh instanceof ErrorHandler.ErrorPageMapper) {
                                 String error_page = ((ErrorHandler.ErrorPageMapper)eh).getErrorPage((HttpServletRequest)this._state.getAsyncContextEvent().getSuppliedRequest());
                                 if (error_page != null) {
                                    this._state.getAsyncContextEvent().setDispatchPath(error_page);
                                 }
                              }

                              try {
                                 this._request.setDispatcherType(DispatcherType.ERROR);
                                 this.getServer().handleAsync(this);
                              } finally {
                                 this._request.setDispatcherType((DispatcherType)null);
                              }
                           }
                           break label866;
                        }

                        LOG.warn("ERROR_DISPATCH loop detected on {} {}", this._request, ex);

                        try {
                           this._response.sendError(500);
                           break;
                        } finally {
                           this._state.errorComplete();
                        }
                     case READ_CALLBACK:
                        ContextHandler handler = this._state.getContextHandler();
                        if (handler != null) {
                           handler.handle(this._request, this._request.getHttpInput());
                        } else {
                           this._request.getHttpInput().run();
                        }
                        break label866;
                     case WRITE_CALLBACK:
                        ContextHandler handler = this._state.getContextHandler();
                        if (handler != null) {
                           handler.handle(this._request, this._response.getHttpOutput());
                        } else {
                           this._response.getHttpOutput().run();
                        }
                        break label866;
                     case ASYNC_ERROR:
                        this._state.onError();
                        break label866;
                     case COMPLETE:
                        if (!this._response.isCommitted() && !this._request.isHandled()) {
                           this._response.sendError(404);
                        } else {
                           this._response.closeOutput();
                        }

                        this._request.setHandled(true);
                        this._state.onComplete();
                        this.onCompleted();
                        break;
                     default:
                        throw new IllegalStateException("state=" + this._state);
                  }
               } catch (QuietServletException | BadMessageException | EofException var37) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug(var37);
                  }

                  this.handleException(var37);
                  break label866;
               } catch (Throwable var38) {
                  if ("ContinuationThrowable".equals(var38.getClass().getSimpleName())) {
                     LOG.ignore(var38);
                  } else {
                     if (this._connector.isStarted()) {
                        LOG.warn(String.valueOf(this._request.getHttpURI()), var38);
                     } else {
                        LOG.debug(String.valueOf(this._request.getHttpURI()), var38);
                     }

                     this.handleException(var38);
                  }
                  break label866;
               }
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("{} handle exit, result {}", this, action);
            }

            boolean suspended = action == HttpChannelState.Action.WAIT;
            return !suspended;
         }

         action = this._state.unhandle();
      }
   }

   protected void handleException(Throwable x) {
      if (this._state.isAsyncStarted()) {
         Throwable root = this._state.getAsyncContextEvent().getThrowable();
         if (root == null) {
            this._state.error(x);
         } else {
            root.addSuppressed(x);
            LOG.warn("Error while handling async error: ", root);
            this.abort(x);
            this._state.errorComplete();
         }
      } else {
         try {
            this._request.setHandled(true);
            this._request.setAttribute("javax.servlet.error.exception", x);
            this._request.setAttribute("javax.servlet.error.exception_type", x.getClass());
            if (this.isCommitted()) {
               this.abort(x);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Could not send response error 500, already committed", x);
               }
            } else {
               this._response.setHeader(HttpHeader.CONNECTION.asString(), HttpHeaderValue.CLOSE.asString());
               if (x instanceof BadMessageException) {
                  BadMessageException bme = (BadMessageException)x;
                  this._response.sendError(bme.getCode(), bme.getReason());
               } else if (x instanceof UnavailableException) {
                  if (((UnavailableException)x).isPermanent()) {
                     this._response.sendError(404);
                  } else {
                     this._response.sendError(503);
                  }
               } else {
                  this._response.sendError(500);
               }
            }
         } catch (Throwable e) {
            this.abort(e);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Could not commit response error 500", e);
            }
         }
      }

   }

   public boolean isExpecting100Continue() {
      return false;
   }

   public boolean isExpecting102Processing() {
      return false;
   }

   public String toString() {
      return String.format("%s@%x{r=%s,c=%b,a=%s,uri=%s}", this.getClass().getSimpleName(), this.hashCode(), this._requests, this._committed.get(), this._state.getState(), this._request.getHttpURI());
   }

   public void onRequest(MetaData.Request request) {
      this._requests.incrementAndGet();
      this._request.setTimeStamp(System.currentTimeMillis());
      HttpFields fields = this._response.getHttpFields();
      if (this._configuration.getSendDateHeader() && !fields.contains(HttpHeader.DATE)) {
         fields.put(this._connector.getServer().getDateField());
      }

      long idleTO = this._configuration.getIdleTimeout();
      this._oldIdleTimeout = this.getIdleTimeout();
      if (idleTO >= 0L && this._oldIdleTimeout != idleTO) {
         this.setIdleTimeout(idleTO);
      }

      this._request.setMetaData(request);
      if (LOG.isDebugEnabled()) {
         LOG.debug("REQUEST for {} on {}{}{} {} {}{}{}", request.getURIString(), this, System.lineSeparator(), request.getMethod(), request.getURIString(), request.getHttpVersion(), System.lineSeparator(), request.getFields());
      }

   }

   public boolean onContent(HttpInput.Content content) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} content {}", this, content);
      }

      return this._request.getHttpInput().addContent(content);
   }

   public boolean onContentComplete() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} onContentComplete", this);
      }

      return false;
   }

   public boolean onRequestComplete() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} onRequestComplete", this);
      }

      return this._request.getHttpInput().eof();
   }

   public void onCompleted() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("COMPLETE for {} written={}", this.getRequest().getRequestURI(), this.getBytesWritten());
      }

      if (this._requestLog != null) {
         this._requestLog.log(this._request, this._response);
      }

      long idleTO = this._configuration.getIdleTimeout();
      if (idleTO >= 0L && this.getIdleTimeout() != this._oldIdleTimeout) {
         this.setIdleTimeout(this._oldIdleTimeout);
      }

      this._transport.onCompleted();
   }

   public boolean onEarlyEOF() {
      return this._request.getHttpInput().earlyEOF();
   }

   public void onBadMessage(int status, String reason) {
      if (status < 400 || status > 599) {
         status = 400;
      }

      HttpChannelState.Action action;
      try {
         action = this._state.handling();
      } catch (IllegalStateException e) {
         this.abort(e);
         throw new BadMessageException(status, reason);
      }

      try {
         if (action == HttpChannelState.Action.DISPATCH) {
            ByteBuffer content = null;
            HttpFields fields = new HttpFields();
            ErrorHandler handler = (ErrorHandler)this.getServer().getBean(ErrorHandler.class);
            if (handler != null) {
               content = handler.badMessageError(status, reason, fields);
            }

            this.sendResponse(new MetaData.Response(HttpVersion.HTTP_1_1, status, reason, fields, (long)BufferUtil.length(content)), content, true);
         }
      } catch (IOException e) {
         LOG.debug(e);
      } finally {
         if (this._state.unhandle() != HttpChannelState.Action.COMPLETE) {
            throw new IllegalStateException();
         }

         this._state.onComplete();
         this.onCompleted();
      }

   }

   protected boolean sendResponse(MetaData.Response info, ByteBuffer content, boolean complete, Callback callback) {
      boolean committing = this._committed.compareAndSet(false, true);
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendResponse info={} content={} complete={} committing={} callback={}", info, BufferUtil.toDetailString(content), complete, committing, callback);
      }

      if (committing) {
         if (info == null) {
            info = this._response.newResponseMetaData();
         }

         this.commit(info);
         int status = info.getStatus();
         Callback committed = (Callback)(status < 200 && status >= 100 ? new Commit100Callback(callback) : new CommitCallback(callback));
         this._transport.send(info, this._request.isHead(), content, complete, committed);
      } else if (info == null) {
         this._transport.send((MetaData.Response)null, this._request.isHead(), content, complete, callback);
      } else {
         callback.failed(new IllegalStateException("committed"));
      }

      return committing;
   }

   protected boolean sendResponse(MetaData.Response info, ByteBuffer content, boolean complete) throws IOException {
      try {
         SharedBlockingCallback.Blocker blocker = this._response.getHttpOutput().acquireWriteBlockingCallback();
         Throwable var5 = null;

         boolean var7;
         try {
            boolean committing = this.sendResponse(info, content, complete, blocker);
            blocker.block();
            var7 = committing;
         } catch (Throwable var17) {
            var5 = var17;
            throw var17;
         } finally {
            if (blocker != null) {
               if (var5 != null) {
                  try {
                     blocker.close();
                  } catch (Throwable var16) {
                     var5.addSuppressed(var16);
                  }
               } else {
                  blocker.close();
               }
            }

         }

         return var7;
      } catch (Throwable var19) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var19);
         }

         this.abort(var19);
         throw var19;
      }
   }

   protected void commit(MetaData.Response info) {
      this._committedMetaData = info;
      if (LOG.isDebugEnabled()) {
         LOG.debug("COMMIT for {} on {}{}{} {} {}{}{}", this.getRequest().getRequestURI(), this, System.lineSeparator(), info.getStatus(), info.getReason(), info.getHttpVersion(), System.lineSeparator(), info.getFields());
      }

   }

   public boolean isCommitted() {
      return this._committed.get();
   }

   public void write(ByteBuffer content, boolean complete, Callback callback) {
      this._written += (long)BufferUtil.length(content);
      this.sendResponse((MetaData.Response)null, content, complete, callback);
   }

   public void resetBuffer() {
      if (this.isCommitted()) {
         throw new IllegalStateException("Committed");
      }
   }

   public HttpOutput.Interceptor getNextInterceptor() {
      return null;
   }

   protected void execute(Runnable task) {
      this._executor.execute(task);
   }

   public Scheduler getScheduler() {
      return this._connector.getScheduler();
   }

   public boolean useDirectBuffers() {
      return this.getEndPoint() instanceof ChannelEndPoint;
   }

   public void abort(Throwable failure) {
      this._transport.abort(failure);
   }

   private class CommitCallback extends Callback.Nested {
      private CommitCallback(Callback callback) {
         super(callback);
      }

      public void failed(final Throwable x) {
         if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("Commit failed", x);
         }

         if (x instanceof BadMessageException) {
            HttpChannel.this._transport.send(HttpGenerator.RESPONSE_500_INFO, false, (ByteBuffer)null, true, new Callback.Nested(this) {
               public void succeeded() {
                  super.failed(x);
                  HttpChannel.this._response.getHttpOutput().closed();
               }

               public void failed(Throwable th) {
                  HttpChannel.this._transport.abort(x);
                  super.failed(x);
               }
            });
         } else {
            HttpChannel.this._transport.abort(x);
            super.failed(x);
         }

      }
   }

   private class Commit100Callback extends CommitCallback {
      private Commit100Callback(Callback callback) {
         super(callback, null);
      }

      public void succeeded() {
         if (HttpChannel.this._committed.compareAndSet(true, false)) {
            super.succeeded();
         } else {
            super.failed(new IllegalStateException());
         }

      }
   }
}
