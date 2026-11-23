package org.spark_project.jetty.servlets;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Quality of Service Filter")
public class QoSFilter implements Filter {
   private static final Logger LOG = Log.getLogger(QoSFilter.class);
   static final int __DEFAULT_MAX_PRIORITY = 10;
   static final int __DEFAULT_PASSES = 10;
   static final int __DEFAULT_WAIT_MS = 50;
   static final long __DEFAULT_TIMEOUT_MS = -1L;
   static final String MANAGED_ATTR_INIT_PARAM = "managedAttr";
   static final String MAX_REQUESTS_INIT_PARAM = "maxRequests";
   static final String MAX_PRIORITY_INIT_PARAM = "maxPriority";
   static final String MAX_WAIT_INIT_PARAM = "waitMs";
   static final String SUSPEND_INIT_PARAM = "suspendMs";
   private final String _suspended = "QoSFilter@" + Integer.toHexString(this.hashCode()) + ".SUSPENDED";
   private final String _resumed = "QoSFilter@" + Integer.toHexString(this.hashCode()) + ".RESUMED";
   private long _waitMs;
   private long _suspendMs;
   private int _maxRequests;
   private Semaphore _passes;
   private Queue<AsyncContext>[] _queues;
   private AsyncListener[] _listeners;

   public void init(FilterConfig filterConfig) {
      int max_priority = 10;
      if (filterConfig.getInitParameter("maxPriority") != null) {
         max_priority = Integer.parseInt(filterConfig.getInitParameter("maxPriority"));
      }

      this._queues = new Queue[max_priority + 1];
      this._listeners = new AsyncListener[this._queues.length];

      for(int p = 0; p < this._queues.length; ++p) {
         this._queues[p] = new ConcurrentLinkedQueue();
         this._listeners[p] = new QoSAsyncListener(p);
      }

      int maxRequests = 10;
      if (filterConfig.getInitParameter("maxRequests") != null) {
         maxRequests = Integer.parseInt(filterConfig.getInitParameter("maxRequests"));
      }

      this._passes = new Semaphore(maxRequests, true);
      this._maxRequests = maxRequests;
      long wait = 50L;
      if (filterConfig.getInitParameter("waitMs") != null) {
         wait = (long)Integer.parseInt(filterConfig.getInitParameter("waitMs"));
      }

      this._waitMs = wait;
      long suspend = -1L;
      if (filterConfig.getInitParameter("suspendMs") != null) {
         suspend = (long)Integer.parseInt(filterConfig.getInitParameter("suspendMs"));
      }

      this._suspendMs = suspend;
      ServletContext context = filterConfig.getServletContext();
      if (context != null && Boolean.parseBoolean(filterConfig.getInitParameter("managedAttr"))) {
         context.setAttribute(filterConfig.getFilterName(), this);
      }

   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      boolean accepted = false;
      boolean var21 = false;

      label304: {
         label305: {
            try {
               var21 = true;
               Boolean suspended = (Boolean)request.getAttribute(this._suspended);
               if (suspended == null) {
                  accepted = this._passes.tryAcquire(this.getWaitMs(), TimeUnit.MILLISECONDS);
                  if (!accepted) {
                     request.setAttribute(this._suspended, Boolean.TRUE);
                     int priority = this.getPriority(request);
                     AsyncContext asyncContext = request.startAsync();
                     long suspendMs = this.getSuspendMs();
                     if (suspendMs > 0L) {
                        asyncContext.setTimeout(suspendMs);
                     }

                     asyncContext.addListener(this._listeners[priority]);
                     this._queues[priority].add(asyncContext);
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Suspended {}", request);
                        var21 = false;
                     } else {
                        var21 = false;
                     }
                     break label304;
                  }

                  request.setAttribute(this._suspended, Boolean.FALSE);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Accepted {}", request);
                  }
               } else if (suspended) {
                  request.setAttribute(this._suspended, Boolean.FALSE);
                  Boolean resumed = (Boolean)request.getAttribute(this._resumed);
                  if (resumed == Boolean.TRUE) {
                     this._passes.acquire();
                     accepted = true;
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Resumed {}", request);
                     }
                  } else {
                     accepted = this._passes.tryAcquire(this.getWaitMs(), TimeUnit.MILLISECONDS);
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Timeout {}", request);
                     }
                  }
               } else {
                  this._passes.acquire();
                  accepted = true;
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Passthrough {}", request);
                  }
               }

               if (accepted) {
                  chain.doFilter(request, response);
                  var21 = false;
               } else {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Rejected {}", request);
                  }

                  ((HttpServletResponse)response).sendError(503);
                  var21 = false;
               }
               break label305;
            } catch (InterruptedException var22) {
               ((HttpServletResponse)response).sendError(503);
               var21 = false;
            } finally {
               if (var21) {
                  if (accepted) {
                     for(int p = this._queues.length - 1; p >= 0; --p) {
                        AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                        if (asyncContext != null) {
                           ServletRequest candidate = asyncContext.getRequest();
                           Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                           if (suspended == Boolean.TRUE) {
                              candidate.setAttribute(this._resumed, Boolean.TRUE);
                              asyncContext.dispatch();
                              break;
                           }
                        }
                     }

                     this._passes.release();
                  }

               }
            }

            if (accepted) {
               for(int p = this._queues.length - 1; p >= 0; --p) {
                  AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                  if (asyncContext != null) {
                     ServletRequest candidate = asyncContext.getRequest();
                     Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                     if (suspended == Boolean.TRUE) {
                        candidate.setAttribute(this._resumed, Boolean.TRUE);
                        asyncContext.dispatch();
                        break;
                     }
                  }
               }

               this._passes.release();
            }

            return;
         }

         if (accepted) {
            for(int p = this._queues.length - 1; p >= 0; --p) {
               AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
               if (asyncContext != null) {
                  ServletRequest candidate = asyncContext.getRequest();
                  Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                  if (suspended == Boolean.TRUE) {
                     candidate.setAttribute(this._resumed, Boolean.TRUE);
                     asyncContext.dispatch();
                     break;
                  }
               }
            }

            this._passes.release();
         }

         return;
      }

      if (accepted) {
         for(int p = this._queues.length - 1; p >= 0; --p) {
            AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
            if (asyncContext != null) {
               ServletRequest candidate = asyncContext.getRequest();
               Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
               if (suspended == Boolean.TRUE) {
                  candidate.setAttribute(this._resumed, Boolean.TRUE);
                  asyncContext.dispatch();
                  break;
               }
            }
         }

         this._passes.release();
      }

   }

   protected int getPriority(ServletRequest request) {
      HttpServletRequest baseRequest = (HttpServletRequest)request;
      if (baseRequest.getUserPrincipal() != null) {
         return 2;
      } else {
         HttpSession session = baseRequest.getSession(false);
         return session != null && !session.isNew() ? 1 : 0;
      }
   }

   public void destroy() {
   }

   @ManagedAttribute("(short) amount of time filter will wait before suspending request (in ms)")
   public long getWaitMs() {
      return this._waitMs;
   }

   public void setWaitMs(long value) {
      this._waitMs = value;
   }

   @ManagedAttribute("amount of time filter will suspend a request for while waiting for the semaphore to become available (in ms)")
   public long getSuspendMs() {
      return this._suspendMs;
   }

   public void setSuspendMs(long value) {
      this._suspendMs = value;
   }

   @ManagedAttribute("maximum number of requests to allow processing of at the same time")
   public int getMaxRequests() {
      return this._maxRequests;
   }

   public void setMaxRequests(int value) {
      this._passes = new Semaphore(value - this.getMaxRequests() + this._passes.availablePermits(), true);
      this._maxRequests = value;
   }

   private class QoSAsyncListener implements AsyncListener {
      private final int priority;

      public QoSAsyncListener(int priority) {
         this.priority = priority;
      }

      public void onStartAsync(AsyncEvent event) throws IOException {
      }

      public void onComplete(AsyncEvent event) throws IOException {
      }

      public void onTimeout(AsyncEvent event) throws IOException {
         AsyncContext asyncContext = event.getAsyncContext();
         QoSFilter.this._queues[this.priority].remove(asyncContext);
         asyncContext.dispatch();
      }

      public void onError(AsyncEvent event) throws IOException {
      }
   }
}
