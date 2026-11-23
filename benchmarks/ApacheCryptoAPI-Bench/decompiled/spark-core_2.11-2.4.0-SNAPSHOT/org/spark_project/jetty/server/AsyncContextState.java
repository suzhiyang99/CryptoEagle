package org.spark_project.jetty.server;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.spark_project.jetty.server.handler.ContextHandler;

public class AsyncContextState implements AsyncContext {
   private final HttpChannel _channel;
   volatile HttpChannelState _state;

   public AsyncContextState(HttpChannelState state) {
      this._state = state;
      this._channel = this._state.getHttpChannel();
   }

   public HttpChannel getHttpChannel() {
      return this._channel;
   }

   HttpChannelState state() {
      HttpChannelState state = this._state;
      if (state == null) {
         throw new IllegalStateException("AsyncContext completed and/or Request lifecycle recycled");
      } else {
         return state;
      }
   }

   public void addListener(final AsyncListener listener, final ServletRequest request, final ServletResponse response) {
      AsyncListener wrap = new AsyncListener() {
         public void onTimeout(AsyncEvent event) throws IOException {
            listener.onTimeout(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
         }

         public void onStartAsync(AsyncEvent event) throws IOException {
            listener.onStartAsync(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
         }

         public void onError(AsyncEvent event) throws IOException {
            listener.onError(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
         }

         public void onComplete(AsyncEvent event) throws IOException {
            listener.onComplete(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
         }
      };
      this.state().addListener(wrap);
   }

   public void addListener(AsyncListener listener) {
      this.state().addListener(listener);
   }

   public void complete() {
      this.state().complete();
   }

   public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
      ContextHandler contextHandler = this.state().getContextHandler();
      if (contextHandler != null) {
         return (T)(contextHandler.getServletContext().createListener(clazz));
      } else {
         try {
            return (T)(clazz.newInstance());
         } catch (Exception e) {
            throw new ServletException(e);
         }
      }
   }

   public void dispatch() {
      this.state().dispatch((ServletContext)null, (String)null);
   }

   public void dispatch(String path) {
      this.state().dispatch((ServletContext)null, path);
   }

   public void dispatch(ServletContext context, String path) {
      this.state().dispatch(context, path);
   }

   public ServletRequest getRequest() {
      return this.state().getAsyncContextEvent().getSuppliedRequest();
   }

   public ServletResponse getResponse() {
      return this.state().getAsyncContextEvent().getSuppliedResponse();
   }

   public long getTimeout() {
      return this.state().getTimeout();
   }

   public boolean hasOriginalRequestAndResponse() {
      HttpChannel channel = this.state().getHttpChannel();
      return channel.getRequest() == this.getRequest() && channel.getResponse() == this.getResponse();
   }

   public void setTimeout(long arg0) {
      this.state().setTimeout(arg0);
   }

   public void start(final Runnable task) {
      final HttpChannel channel = this.state().getHttpChannel();
      channel.execute(new Runnable() {
         public void run() {
            AsyncContextState.this.state().getAsyncContextEvent().getContext().getContextHandler().handle(channel.getRequest(), task);
         }
      });
   }

   public void reset() {
      this._state = null;
   }

   public HttpChannelState getHttpChannelState() {
      return this.state();
   }
}
