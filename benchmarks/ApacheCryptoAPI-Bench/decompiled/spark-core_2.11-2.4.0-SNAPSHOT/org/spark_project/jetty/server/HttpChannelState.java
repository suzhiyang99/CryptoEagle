package org.spark_project.jetty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Locker;
import org.spark_project.jetty.util.thread.Scheduler;

public class HttpChannelState {
   private static final Logger LOG = Log.getLogger(HttpChannelState.class);
   private static final long DEFAULT_TIMEOUT = Long.getLong("org.spark_project.jetty.server.HttpChannelState.DEFAULT_TIMEOUT", 30000L);
   private final boolean DEBUG;
   private final Locker _locker;
   private final HttpChannel _channel;
   private List<AsyncListener> _asyncListeners;
   private State _state;
   private Async _async;
   private boolean _initial;
   private boolean _asyncReadPossible;
   private Interest _asyncRead;
   private boolean _asyncWritePossible;
   private long _timeoutMs;
   private AsyncContextEvent _event;

   protected HttpChannelState(HttpChannel channel) {
      this.DEBUG = LOG.isDebugEnabled();
      this._locker = new Locker();
      this._asyncRead = HttpChannelState.Interest.NONE;
      this._timeoutMs = DEFAULT_TIMEOUT;
      this._channel = channel;
      this._state = HttpChannelState.State.IDLE;
      this._async = HttpChannelState.Async.NOT_ASYNC;
      this._initial = true;
   }

   public State getState() {
      State var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._state;
      }

      return var3;
   }

   public void addListener(AsyncListener listener) {
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._asyncListeners == null) {
            this._asyncListeners = new ArrayList();
         }

         this._asyncListeners.add(listener);
      }

   }

   public void setTimeout(long ms) {
      try (Locker.Lock lock = this._locker.lock()) {
         this._timeoutMs = ms;
      }

   }

   public long getTimeout() {
      long var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._timeoutMs;
      }

      return var3;
   }

   public AsyncContextEvent getAsyncContextEvent() {
      AsyncContextEvent var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._event;
      }

      return var3;
   }

   public String toString() {
      String var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = String.format("%s@%x{s=%s a=%s i=%b r=%s/%s w=%b}", this.getClass().getSimpleName(), this.hashCode(), this._state, this._async, this._initial, this._asyncRead, this._asyncReadPossible, this._asyncWritePossible);
      }

      return var3;
   }

   private String getStatusStringLocked() {
      return String.format("s=%s i=%b a=%s", this._state, this._initial, this._async);
   }

   public String getStatusString() {
      String var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this.getStatusStringLocked();
      }

      return var3;
   }

   protected Action handling() {
      if (this.DEBUG) {
         LOG.debug("{} handling {}", this, this._state);
      }

      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case IDLE:
               this._initial = true;
               this._state = HttpChannelState.State.DISPATCHED;
               Action var31 = HttpChannelState.Action.DISPATCH;
               return var31;
            case COMPLETING:
            case COMPLETED:
               Action var30 = HttpChannelState.Action.TERMINATED;
               return var30;
            case ASYNC_WOKEN:
               if (this._asyncRead.isInterested() && this._asyncReadPossible) {
                  this._state = HttpChannelState.State.ASYNC_IO;
                  this._asyncRead = HttpChannelState.Interest.NONE;
                  Action var29 = HttpChannelState.Action.READ_CALLBACK;
                  return var29;
               } else if (this._asyncWritePossible) {
                  this._state = HttpChannelState.State.ASYNC_IO;
                  this._asyncWritePossible = false;
                  Action var28 = HttpChannelState.Action.WRITE_CALLBACK;
                  return var28;
               } else {
                  switch (this._async) {
                     case COMPLETE:
                        this._state = HttpChannelState.State.COMPLETING;
                        Action var27 = HttpChannelState.Action.COMPLETE;
                        return var27;
                     case DISPATCH:
                        this._state = HttpChannelState.State.DISPATCHED;
                        this._async = HttpChannelState.Async.NOT_ASYNC;
                        Action var26 = HttpChannelState.Action.ASYNC_DISPATCH;
                        return var26;
                     case EXPIRING:
                     case NOT_ASYNC:
                        Action var25 = HttpChannelState.Action.WAIT;
                        return var25;
                     case EXPIRED:
                        this._state = HttpChannelState.State.DISPATCHED;
                        this._async = HttpChannelState.Async.NOT_ASYNC;
                        Action var24 = HttpChannelState.Action.ERROR_DISPATCH;
                        return var24;
                     case STARTED:
                        Action var23 = HttpChannelState.Action.WAIT;
                        return var23;
                     case ERRORING:
                        this._state = HttpChannelState.State.DISPATCHED;
                        Action var3 = HttpChannelState.Action.ASYNC_ERROR;
                        return var3;
                     default:
                        throw new IllegalStateException(this.getStatusStringLocked());
                  }
               }
            case ASYNC_IO:
            case ASYNC_WAIT:
            case DISPATCHED:
            case UPGRADED:
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }
      }
   }

   public void startAsync(final AsyncContextEvent event) {
      final List<AsyncListener> lastAsyncListeners;
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._state != HttpChannelState.State.DISPATCHED || this._async != HttpChannelState.Async.NOT_ASYNC) {
            throw new IllegalStateException(this.getStatusStringLocked());
         }

         this._async = HttpChannelState.Async.STARTED;
         this._event = event;
         lastAsyncListeners = this._asyncListeners;
         this._asyncListeners = null;
      }

      if (lastAsyncListeners != null) {
         Runnable callback = new Runnable() {
            public void run() {
               for(AsyncListener listener : lastAsyncListeners) {
                  try {
                     listener.onStartAsync(event);
                  } catch (Throwable e) {
                     HttpChannelState.LOG.warn(e);
                  }
               }

            }

            public String toString() {
               return "startAsync";
            }
         };
         this.runInContext(event, callback);
      }

   }

   protected void error(Throwable th) {
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._event != null) {
            this._event.addThrowable(th);
         }

         this._async = HttpChannelState.Async.ERRORING;
      }

   }

   public void asyncError(Throwable failure) {
      AsyncContextEvent event = null;

      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case IDLE:
            case COMPLETING:
            case COMPLETED:
            case ASYNC_WOKEN:
            case ASYNC_IO:
            case DISPATCHED:
            case UPGRADED:
               break;
            case ASYNC_WAIT:
               this._event.addThrowable(failure);
               this._state = HttpChannelState.State.ASYNC_WOKEN;
               this._async = HttpChannelState.Async.ERRORING;
               event = this._event;
               break;
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }
      }

      if (event != null) {
         this.cancelTimeout(event);
         this.runInContext(event, this._channel);
      }

   }

   protected Action unhandle() {
      boolean read_interested = false;
      if (this.DEBUG) {
         LOG.debug("{} unhandle {}", this, this._state);
      }

      Action action;
      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case COMPLETING:
            case COMPLETED:
               Action var17 = HttpChannelState.Action.TERMINATED;
               return var17;
            case ASYNC_WOKEN:
            case ASYNC_WAIT:
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
            case ASYNC_IO:
            case DISPATCHED:
               this._initial = false;
               switch (this._async) {
                  case COMPLETE:
                     this._state = HttpChannelState.State.COMPLETING;
                     this._async = HttpChannelState.Async.NOT_ASYNC;
                     action = HttpChannelState.Action.COMPLETE;
                     break;
                  case DISPATCH:
                     this._state = HttpChannelState.State.DISPATCHED;
                     this._async = HttpChannelState.Async.NOT_ASYNC;
                     action = HttpChannelState.Action.ASYNC_DISPATCH;
                     break;
                  case EXPIRING:
                     this._state = HttpChannelState.State.ASYNC_WAIT;
                     action = HttpChannelState.Action.WAIT;
                     break;
                  case EXPIRED:
                     this._state = HttpChannelState.State.DISPATCHED;
                     this._async = HttpChannelState.Async.NOT_ASYNC;
                     action = HttpChannelState.Action.ERROR_DISPATCH;
                     break;
                  case STARTED:
                     if (this._asyncRead.isInterested() && this._asyncReadPossible) {
                        this._state = HttpChannelState.State.ASYNC_IO;
                        this._asyncRead = HttpChannelState.Interest.NONE;
                        action = HttpChannelState.Action.READ_CALLBACK;
                     } else if (this._asyncWritePossible) {
                        this._state = HttpChannelState.State.ASYNC_IO;
                        this._asyncWritePossible = false;
                        action = HttpChannelState.Action.WRITE_CALLBACK;
                     } else {
                        this._state = HttpChannelState.State.ASYNC_WAIT;
                        action = HttpChannelState.Action.WAIT;
                        if (this._asyncRead == HttpChannelState.Interest.NEEDED) {
                           this._asyncRead = HttpChannelState.Interest.REGISTERED;
                           read_interested = true;
                        }

                        Scheduler scheduler = this._channel.getScheduler();
                        if (scheduler != null && this._timeoutMs > 0L) {
                           this._event.setTimeoutTask(scheduler.schedule(this._event, this._timeoutMs, TimeUnit.MILLISECONDS));
                        }
                     }
                     break;
                  case ERRORING:
                     this._state = HttpChannelState.State.DISPATCHED;
                     action = HttpChannelState.Action.ASYNC_ERROR;
                     break;
                  case NOT_ASYNC:
                     this._state = HttpChannelState.State.COMPLETING;
                     action = HttpChannelState.Action.COMPLETE;
                     break;
                  case ERRORED:
                     this._state = HttpChannelState.State.DISPATCHED;
                     action = HttpChannelState.Action.ERROR_DISPATCH;
                     this._async = HttpChannelState.Async.NOT_ASYNC;
                     break;
                  default:
                     this._state = HttpChannelState.State.COMPLETING;
                     action = HttpChannelState.Action.COMPLETE;
               }
         }
      }

      if (read_interested) {
         this._channel.asyncReadFillInterested();
      }

      return action;
   }

   public void dispatch(ServletContext context, String path) {
      boolean dispatch = false;
      AsyncContextEvent event = null;

      try (Locker.Lock lock = this._locker.lock()) {
         boolean started = false;
         event = this._event;
         switch (this._async) {
            case EXPIRING:
            case ERRORED:
               break;
            case STARTED:
               started = true;
               break;
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }

         this._async = HttpChannelState.Async.DISPATCH;
         if (context != null) {
            this._event.setDispatchContext(context);
         }

         if (path != null) {
            this._event.setDispatchPath(path);
         }

         if (started) {
            switch (this._state) {
               case ASYNC_WOKEN:
               case ASYNC_IO:
               case DISPATCHED:
                  break;
               case ASYNC_WAIT:
                  this._state = HttpChannelState.State.ASYNC_WOKEN;
                  dispatch = true;
                  break;
               default:
                  LOG.warn("async dispatched when complete {}", this);
            }
         }
      }

      this.cancelTimeout(event);
      if (dispatch) {
         this.scheduleDispatch();
      }

   }

   protected void onTimeout() {
      final List<AsyncListener> listeners;
      final AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._async != HttpChannelState.Async.STARTED) {
            return;
         }

         this._async = HttpChannelState.Async.EXPIRING;
         event = this._event;
         listeners = this._asyncListeners;
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Async timeout {}", this);
      }

      if (listeners != null) {
         Runnable callback = new Runnable() {
            public void run() {
               for(AsyncListener listener : listeners) {
                  try {
                     listener.onTimeout(event);
                  } catch (Throwable e) {
                     HttpChannelState.LOG.debug(e);
                     event.addThrowable(e);
                     HttpChannelState.this._channel.getRequest().setAttribute("javax.servlet.error.exception", event.getThrowable());
                     break;
                  }
               }

            }

            public String toString() {
               return "onTimeout";
            }
         };
         this.runInContext(event, callback);
      }

      boolean dispatch = false;

      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._async) {
            case COMPLETE:
            case DISPATCH:
               break;
            case EXPIRING:
               if (event.getThrowable() == null) {
                  this._async = HttpChannelState.Async.EXPIRED;
                  this._event.addThrowable(new TimeoutException("Async API violation"));
               } else {
                  this._async = HttpChannelState.Async.ERRORING;
               }
               break;
            default:
               throw new IllegalStateException();
         }

         if (this._state == HttpChannelState.State.ASYNC_WAIT) {
            this._state = HttpChannelState.State.ASYNC_WOKEN;
            dispatch = true;
         }
      }

      if (dispatch) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Dispatch after async timeout {}", this);
         }

         this.scheduleDispatch();
      }

   }

   public void complete() {
      boolean handle = false;
      AsyncContextEvent event = null;

      try (Locker.Lock lock = this._locker.lock()) {
         boolean started = false;
         event = this._event;
         switch (this._async) {
            case EXPIRING:
            case ERRORED:
               break;
            case STARTED:
               started = true;
               break;
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }

         this._async = HttpChannelState.Async.COMPLETE;
         if (started && this._state == HttpChannelState.State.ASYNC_WAIT) {
            handle = true;
            this._state = HttpChannelState.State.ASYNC_WOKEN;
         }
      }

      this.cancelTimeout(event);
      if (handle) {
         this.runInContext(event, this._channel);
      }

   }

   public void errorComplete() {
      try (Locker.Lock lock = this._locker.lock()) {
         this._async = HttpChannelState.Async.COMPLETE;
         this._event.setDispatchContext((ServletContext)null);
         this._event.setDispatchPath((String)null);
      }

      this.cancelTimeout();
   }

   protected void onError() {
      List<AsyncListener> aListeners;
      AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._state != HttpChannelState.State.DISPATCHED) {
            throw new IllegalStateException(this.getStatusStringLocked());
         }

         aListeners = this._asyncListeners;
         event = this._event;
         this._async = HttpChannelState.Async.ERRORED;
      }

      if (event != null && aListeners != null) {
         event.getSuppliedRequest().setAttribute("javax.servlet.error.exception", event.getThrowable());
         event.getSuppliedRequest().setAttribute("javax.servlet.error.message", event.getThrowable().getMessage());

         for(AsyncListener listener : aListeners) {
            try {
               listener.onError(event);
            } catch (Throwable x) {
               LOG.info("Exception while invoking listener " + listener, x);
            }
         }
      }

   }

   protected void onComplete() {
      final List<AsyncListener> aListeners;
      final AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case COMPLETING:
               aListeners = this._asyncListeners;
               event = this._event;
               this._state = HttpChannelState.State.COMPLETED;
               this._async = HttpChannelState.Async.NOT_ASYNC;
               break;
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }
      }

      if (event != null) {
         if (aListeners != null) {
            Runnable callback = new Runnable() {
               public void run() {
                  for(AsyncListener listener : aListeners) {
                     try {
                        listener.onComplete(event);
                     } catch (Throwable e) {
                        HttpChannelState.LOG.warn(e);
                     }
                  }

               }

               public String toString() {
                  return "onComplete";
               }
            };
            this.runInContext(event, callback);
         }

         event.completed();
      }

   }

   protected void recycle() {
      this.cancelTimeout();

      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case ASYNC_IO:
            case DISPATCHED:
               throw new IllegalStateException(this.getStatusStringLocked());
            case ASYNC_WAIT:
            default:
               this._asyncListeners = null;
               this._state = HttpChannelState.State.IDLE;
               this._async = HttpChannelState.Async.NOT_ASYNC;
               this._initial = true;
               this._asyncReadPossible = false;
               this._asyncRead = HttpChannelState.Interest.NONE;
               this._asyncWritePossible = false;
               this._timeoutMs = DEFAULT_TIMEOUT;
               this._event = null;
               return;
            case UPGRADED:
         }
      }

   }

   public void upgrade() {
      this.cancelTimeout();

      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case IDLE:
            case COMPLETED:
               this._asyncListeners = null;
               this._state = HttpChannelState.State.UPGRADED;
               this._async = HttpChannelState.Async.NOT_ASYNC;
               this._initial = true;
               this._asyncReadPossible = false;
               this._asyncRead = HttpChannelState.Interest.NONE;
               this._asyncWritePossible = false;
               this._timeoutMs = DEFAULT_TIMEOUT;
               this._event = null;
               return;
            default:
               throw new IllegalStateException(this.getStatusStringLocked());
         }
      }
   }

   protected void scheduleDispatch() {
      this._channel.execute(this._channel);
   }

   protected void cancelTimeout() {
      AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         event = this._event;
      }

      this.cancelTimeout(event);
   }

   protected void cancelTimeout(AsyncContextEvent event) {
      if (event != null) {
         event.cancelTimeoutTask();
      }

   }

   public boolean isIdle() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._state == HttpChannelState.State.IDLE;
      }

      return var3;
   }

   public boolean isExpired() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._async == HttpChannelState.Async.EXPIRED;
      }

      return var3;
   }

   public boolean isInitial() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._initial;
      }

      return var3;
   }

   public boolean isSuspended() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._state == HttpChannelState.State.ASYNC_WAIT || this._state == HttpChannelState.State.DISPATCHED && this._async == HttpChannelState.Async.STARTED;
      }

      return var3;
   }

   boolean isCompleting() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._state == HttpChannelState.State.COMPLETING;
      }

      return var3;
   }

   boolean isCompleted() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._state == HttpChannelState.State.COMPLETED;
      }

      return var3;
   }

   public boolean isAsyncStarted() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._state != HttpChannelState.State.DISPATCHED) {
            var3 = this._async == HttpChannelState.Async.STARTED || this._async == HttpChannelState.Async.EXPIRING;
            return var3;
         }

         var3 = this._async != HttpChannelState.Async.NOT_ASYNC;
      }

      return var3;
   }

   public boolean isAsync() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = !this._initial || this._async != HttpChannelState.Async.NOT_ASYNC;
      }

      return var3;
   }

   public Request getBaseRequest() {
      return this._channel.getRequest();
   }

   public HttpChannel getHttpChannel() {
      return this._channel;
   }

   public ContextHandler getContextHandler() {
      AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         event = this._event;
      }

      return this.getContextHandler(event);
   }

   ContextHandler getContextHandler(AsyncContextEvent event) {
      if (event != null) {
         ContextHandler.Context context = (ContextHandler.Context)event.getServletContext();
         if (context != null) {
            return context.getContextHandler();
         }
      }

      return null;
   }

   public ServletResponse getServletResponse() {
      AsyncContextEvent event;
      try (Locker.Lock lock = this._locker.lock()) {
         event = this._event;
      }

      return this.getServletResponse(event);
   }

   public ServletResponse getServletResponse(AsyncContextEvent event) {
      return (ServletResponse)(event != null && event.getSuppliedResponse() != null ? event.getSuppliedResponse() : this._channel.getResponse());
   }

   void runInContext(AsyncContextEvent event, Runnable runnable) {
      ContextHandler contextHandler = this.getContextHandler(event);
      if (contextHandler == null) {
         runnable.run();
      } else {
         contextHandler.handle(this._channel.getRequest(), runnable);
      }

   }

   public Object getAttribute(String name) {
      return this._channel.getRequest().getAttribute(name);
   }

   public void removeAttribute(String name) {
      this._channel.getRequest().removeAttribute(name);
   }

   public void setAttribute(String name, Object attribute) {
      this._channel.getRequest().setAttribute(name, attribute);
   }

   public void onReadUnready() {
      boolean interested = false;

      try (Locker.Lock lock = this._locker.lock()) {
         if (this._asyncRead != HttpChannelState.Interest.REGISTERED) {
            this._asyncReadPossible = false;
            if (this._state == HttpChannelState.State.ASYNC_WAIT) {
               interested = true;
               this._asyncRead = HttpChannelState.Interest.REGISTERED;
            } else {
               this._asyncRead = HttpChannelState.Interest.NEEDED;
            }
         }
      }

      if (interested) {
         this._channel.asyncReadFillInterested();
      }

   }

   public boolean onReadPossible() {
      boolean woken = false;

      try (Locker.Lock lock = this._locker.lock()) {
         this._asyncReadPossible = true;
         if (this._state == HttpChannelState.State.ASYNC_WAIT && this._asyncRead.isInterested()) {
            woken = true;
            this._state = HttpChannelState.State.ASYNC_WOKEN;
         }
      }

      return woken;
   }

   public boolean onReadReady() {
      boolean woken = false;

      try (Locker.Lock lock = this._locker.lock()) {
         this._asyncRead = HttpChannelState.Interest.REGISTERED;
         this._asyncReadPossible = true;
         if (this._state == HttpChannelState.State.ASYNC_WAIT) {
            woken = true;
            this._state = HttpChannelState.State.ASYNC_WOKEN;
         }
      }

      return woken;
   }

   public boolean isReadPossible() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._asyncReadPossible;
      }

      return var3;
   }

   public boolean onWritePossible() {
      boolean handle = false;

      try (Locker.Lock lock = this._locker.lock()) {
         this._asyncWritePossible = true;
         if (this._state == HttpChannelState.State.ASYNC_WAIT) {
            this._state = HttpChannelState.State.ASYNC_WOKEN;
            handle = true;
         }
      }

      return handle;
   }

   public static enum State {
      IDLE,
      DISPATCHED,
      ASYNC_WAIT,
      ASYNC_WOKEN,
      ASYNC_IO,
      COMPLETING,
      COMPLETED,
      UPGRADED;
   }

   public static enum Action {
      DISPATCH,
      ASYNC_DISPATCH,
      ERROR_DISPATCH,
      ASYNC_ERROR,
      WRITE_CALLBACK,
      READ_CALLBACK,
      COMPLETE,
      TERMINATED,
      WAIT;
   }

   public static enum Async {
      NOT_ASYNC,
      STARTED,
      DISPATCH,
      COMPLETE,
      EXPIRING,
      EXPIRED,
      ERRORING,
      ERRORED;
   }

   public static enum Interest {
      NONE(false),
      NEEDED(true),
      REGISTERED(true);

      final boolean _interested;

      boolean isInterested() {
         return this._interested;
      }

      private Interest(boolean interest) {
         this._interested = interest;
      }
   }
}
