package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.HandlerContainer;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.ArrayUtil;
import org.spark_project.jetty.util.MultiException;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;

@ManagedObject("Handler of multiple handlers")
public class HandlerCollection extends AbstractHandlerContainer {
   private final boolean _mutableWhenRunning;
   private volatile Handler[] _handlers;

   public HandlerCollection() {
      this._mutableWhenRunning = false;
   }

   public HandlerCollection(boolean mutableWhenRunning) {
      this._mutableWhenRunning = mutableWhenRunning;
   }

   @ManagedAttribute(
      value = "Wrapped handlers",
      readonly = true
   )
   public Handler[] getHandlers() {
      return this._handlers;
   }

   public void setHandlers(Handler[] handlers) {
      if (!this._mutableWhenRunning && this.isStarted()) {
         throw new IllegalStateException("STARTED");
      } else {
         if (handlers != null) {
            for(Handler handler : handlers) {
               if (handler == this || handler instanceof HandlerContainer && Arrays.asList(((HandlerContainer)handler).getChildHandlers()).contains(this)) {
                  throw new IllegalStateException("setHandler loop");
               }
            }

            for(Handler handler : handlers) {
               if (handler.getServer() != this.getServer()) {
                  handler.setServer(this.getServer());
               }
            }
         }

         Handler[] old = this._handlers;
         this._handlers = handlers;
         this.updateBeans(old, handlers);
      }
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (this._handlers != null && this.isStarted()) {
         MultiException mex = null;

         for(int i = 0; i < this._handlers.length; ++i) {
            try {
               this._handlers[i].handle(target, baseRequest, request, response);
            } catch (IOException e) {
               throw e;
            } catch (RuntimeException e) {
               throw e;
            } catch (Exception e) {
               if (mex == null) {
                  mex = new MultiException();
               }

               mex.add(e);
            }
         }

         if (mex != null) {
            if (mex.size() == 1) {
               throw new ServletException(mex.getThrowable(0));
            }

            throw new ServletException(mex);
         }
      }

   }

   public void addHandler(Handler handler) {
      this.setHandlers((Handler[])ArrayUtil.addToArray(this.getHandlers(), handler, Handler.class));
   }

   public void removeHandler(Handler handler) {
      Handler[] handlers = this.getHandlers();
      if (handlers != null && handlers.length > 0) {
         this.setHandlers((Handler[])ArrayUtil.removeFromArray(handlers, handler));
      }

   }

   protected void expandChildren(List<Handler> list, Class<?> byClass) {
      if (this.getHandlers() != null) {
         for(Handler h : this.getHandlers()) {
            this.expandHandler(h, list, byClass);
         }
      }

   }

   public void destroy() {
      if (!this.isStopped()) {
         throw new IllegalStateException("!STOPPED");
      } else {
         Handler[] children = this.getChildHandlers();
         this.setHandlers((Handler[])null);

         for(Handler child : children) {
            child.destroy();
         }

         super.destroy();
      }
   }

   public String toString() {
      Handler[] handlers = this.getHandlers();
      return super.toString() + (handlers == null ? "[]" : Arrays.asList(this.getHandlers()).toString());
   }
}
