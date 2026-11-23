package org.spark_project.jetty.servlets;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.annotation.Name;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;

@ManagedObject("limits exposure to abuse from request flooding, whether malicious, or as a result of a misconfigured client")
public class DoSFilter implements Filter {
   private static final Logger LOG = Log.getLogger(DoSFilter.class);
   private static final String IPv4_GROUP = "(\\d{1,3})";
   private static final Pattern IPv4_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
   private static final String IPv6_GROUP = "(\\p{XDigit}{1,4})";
   private static final Pattern IPv6_PATTERN = Pattern.compile("(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4}):(\\p{XDigit}{1,4})");
   private static final Pattern CIDR_PATTERN = Pattern.compile("([^/]+)/(\\d+)");
   private static final String __TRACKER = "DoSFilter.Tracker";
   private static final String __THROTTLED = "DoSFilter.Throttled";
   private static final int __DEFAULT_MAX_REQUESTS_PER_SEC = 25;
   private static final int __DEFAULT_DELAY_MS = 100;
   private static final int __DEFAULT_THROTTLE = 5;
   private static final int __DEFAULT_MAX_WAIT_MS = 50;
   private static final long __DEFAULT_THROTTLE_MS = 30000L;
   private static final long __DEFAULT_MAX_REQUEST_MS_INIT_PARAM = 30000L;
   private static final long __DEFAULT_MAX_IDLE_TRACKER_MS_INIT_PARAM = 30000L;
   static final String MANAGED_ATTR_INIT_PARAM = "managedAttr";
   static final String MAX_REQUESTS_PER_S_INIT_PARAM = "maxRequestsPerSec";
   static final String DELAY_MS_INIT_PARAM = "delayMs";
   static final String THROTTLED_REQUESTS_INIT_PARAM = "throttledRequests";
   static final String MAX_WAIT_INIT_PARAM = "maxWaitMs";
   static final String THROTTLE_MS_INIT_PARAM = "throttleMs";
   static final String MAX_REQUEST_MS_INIT_PARAM = "maxRequestMs";
   static final String MAX_IDLE_TRACKER_MS_INIT_PARAM = "maxIdleTrackerMs";
   static final String INSERT_HEADERS_INIT_PARAM = "insertHeaders";
   static final String TRACK_SESSIONS_INIT_PARAM = "trackSessions";
   static final String REMOTE_PORT_INIT_PARAM = "remotePort";
   static final String IP_WHITELIST_INIT_PARAM = "ipWhitelist";
   static final String ENABLED_INIT_PARAM = "enabled";
   static final String TOO_MANY_CODE = "tooManyCode";
   private static final int USER_AUTH = 2;
   private static final int USER_SESSION = 2;
   private static final int USER_IP = 1;
   private static final int USER_UNKNOWN = 0;
   private final String _suspended = "DoSFilter@" + Integer.toHexString(this.hashCode()) + ".SUSPENDED";
   private final String _resumed = "DoSFilter@" + Integer.toHexString(this.hashCode()) + ".RESUMED";
   private final ConcurrentHashMap<String, RateTracker> _rateTrackers = new ConcurrentHashMap();
   private final List<String> _whitelist = new CopyOnWriteArrayList();
   private int _tooManyCode;
   private volatile long _delayMs;
   private volatile long _throttleMs;
   private volatile long _maxWaitMs;
   private volatile long _maxRequestMs;
   private volatile long _maxIdleTrackerMs;
   private volatile boolean _insertHeaders;
   private volatile boolean _trackSessions;
   private volatile boolean _remotePort;
   private volatile boolean _enabled;
   private Semaphore _passes;
   private volatile int _throttledRequests;
   private volatile int _maxRequestsPerSec;
   private Queue<AsyncContext>[] _queues;
   private AsyncListener[] _listeners;
   private Scheduler _scheduler;

   public void init(FilterConfig filterConfig) throws ServletException {
      this._queues = new Queue[this.getMaxPriority() + 1];
      this._listeners = new AsyncListener[this._queues.length];

      for(int p = 0; p < this._queues.length; ++p) {
         this._queues[p] = new ConcurrentLinkedQueue();
         this._listeners[p] = new DoSAsyncListener(p);
      }

      this._rateTrackers.clear();
      int maxRequests = 25;
      String parameter = filterConfig.getInitParameter("maxRequestsPerSec");
      if (parameter != null) {
         maxRequests = Integer.parseInt(parameter);
      }

      this.setMaxRequestsPerSec(maxRequests);
      long delay = 100L;
      parameter = filterConfig.getInitParameter("delayMs");
      if (parameter != null) {
         delay = Long.parseLong(parameter);
      }

      this.setDelayMs(delay);
      int throttledRequests = 5;
      parameter = filterConfig.getInitParameter("throttledRequests");
      if (parameter != null) {
         throttledRequests = Integer.parseInt(parameter);
      }

      this.setThrottledRequests(throttledRequests);
      long maxWait = 50L;
      parameter = filterConfig.getInitParameter("maxWaitMs");
      if (parameter != null) {
         maxWait = Long.parseLong(parameter);
      }

      this.setMaxWaitMs(maxWait);
      long throttle = 30000L;
      parameter = filterConfig.getInitParameter("throttleMs");
      if (parameter != null) {
         throttle = Long.parseLong(parameter);
      }

      this.setThrottleMs(throttle);
      long maxRequestMs = 30000L;
      parameter = filterConfig.getInitParameter("maxRequestMs");
      if (parameter != null) {
         maxRequestMs = Long.parseLong(parameter);
      }

      this.setMaxRequestMs(maxRequestMs);
      long maxIdleTrackerMs = 30000L;
      parameter = filterConfig.getInitParameter("maxIdleTrackerMs");
      if (parameter != null) {
         maxIdleTrackerMs = Long.parseLong(parameter);
      }

      this.setMaxIdleTrackerMs(maxIdleTrackerMs);
      String whiteList = "";
      parameter = filterConfig.getInitParameter("ipWhitelist");
      if (parameter != null) {
         whiteList = parameter;
      }

      this.setWhitelist(whiteList);
      parameter = filterConfig.getInitParameter("insertHeaders");
      this.setInsertHeaders(parameter == null || Boolean.parseBoolean(parameter));
      parameter = filterConfig.getInitParameter("trackSessions");
      this.setTrackSessions(parameter == null || Boolean.parseBoolean(parameter));
      parameter = filterConfig.getInitParameter("remotePort");
      this.setRemotePort(parameter != null && Boolean.parseBoolean(parameter));
      parameter = filterConfig.getInitParameter("enabled");
      this.setEnabled(parameter == null || Boolean.parseBoolean(parameter));
      parameter = filterConfig.getInitParameter("tooManyCode");
      this.setTooManyCode(parameter == null ? 429 : Integer.parseInt(parameter));
      this._scheduler = this.startScheduler();
      ServletContext context = filterConfig.getServletContext();
      if (context != null && Boolean.parseBoolean(filterConfig.getInitParameter("managedAttr"))) {
         context.setAttribute(filterConfig.getFilterName(), this);
      }

   }

   protected Scheduler startScheduler() throws ServletException {
      try {
         Scheduler result = new ScheduledExecutorScheduler();
         result.start();
         return result;
      } catch (Exception x) {
         throw new ServletException(x);
      }
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
   }

   protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      if (!this.isEnabled()) {
         filterChain.doFilter(request, response);
      } else {
         RateTracker tracker = (RateTracker)request.getAttribute("DoSFilter.Tracker");
         if (tracker == null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Filtering {}", request);
            }

            tracker = this.getRateTracker(request);
            boolean overRateLimit = tracker.isRateExceeded(System.currentTimeMillis());
            if (!overRateLimit) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Allowing {}", request);
               }

               this.doFilterChain(filterChain, request, response);
               return;
            }

            long delayMs = this.getDelayMs();
            boolean insertHeaders = this.isInsertHeaders();
            switch ((int)delayMs) {
               case -1:
                  LOG.warn("DOS ALERT: Request rejected ip={}, session={}, user={}", request.getRemoteAddr(), request.getRequestedSessionId(), request.getUserPrincipal());
                  if (insertHeaders) {
                     response.addHeader("DoSFilter", "unavailable");
                  }

                  response.sendError(this.getTooManyCode());
                  return;
               case 0:
                  LOG.warn("DOS ALERT: Request throttled ip={}, session={}, user={}", request.getRemoteAddr(), request.getRequestedSessionId(), request.getUserPrincipal());
                  request.setAttribute("DoSFilter.Tracker", tracker);
                  break;
               default:
                  LOG.warn("DOS ALERT: Request delayed={}ms, ip={}, session={}, user={}", delayMs, request.getRemoteAddr(), request.getRequestedSessionId(), request.getUserPrincipal());
                  if (insertHeaders) {
                     response.addHeader("DoSFilter", "delayed");
                  }

                  request.setAttribute("DoSFilter.Tracker", tracker);
                  AsyncContext asyncContext = request.startAsync();
                  if (delayMs > 0L) {
                     asyncContext.setTimeout(delayMs);
                  }

                  asyncContext.addListener(new DoSTimeoutAsyncListener());
                  return;
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Throttling {}", request);
         }

         boolean accepted = false;
         boolean var54 = false;

         label1277: {
            label1278: {
               try {
                  var54 = true;
                  accepted = this._passes.tryAcquire(this.getMaxWaitMs(), TimeUnit.MILLISECONDS);
                  if (!accepted) {
                     Boolean throttled = (Boolean)request.getAttribute("DoSFilter.Throttled");
                     long throttleMs = this.getThrottleMs();
                     if (throttled != Boolean.TRUE && throttleMs > 0L) {
                        int priority = this.getPriority(request, tracker);
                        request.setAttribute("DoSFilter.Throttled", Boolean.TRUE);
                        if (this.isInsertHeaders()) {
                           response.addHeader("DoSFilter", "throttled");
                        }

                        AsyncContext asyncContext = request.startAsync();
                        request.setAttribute(this._suspended, Boolean.TRUE);
                        if (throttleMs > 0L) {
                           asyncContext.setTimeout(throttleMs);
                        }

                        asyncContext.addListener(this._listeners[priority]);
                        this._queues[priority].add(asyncContext);
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Throttled {}, {}ms", request, throttleMs);
                           var54 = false;
                        } else {
                           var54 = false;
                        }
                        break label1277;
                     }

                     Boolean resumed = (Boolean)request.getAttribute(this._resumed);
                     if (resumed == Boolean.TRUE) {
                        this._passes.acquire();
                        accepted = true;
                     }
                  }

                  if (accepted) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Allowing {}", request);
                     }

                     this.doFilterChain(filterChain, request, response);
                     var54 = false;
                  } else {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Rejecting {}", request);
                     }

                     if (this.isInsertHeaders()) {
                        response.addHeader("DoSFilter", "unavailable");
                     }

                     response.sendError(this.getTooManyCode());
                     var54 = false;
                  }
                  break label1278;
               } catch (InterruptedException e) {
                  LOG.ignore(e);
                  response.sendError(this.getTooManyCode());
                  var54 = false;
               } finally {
                  if (var54) {
                     if (accepted) {
                        try {
                           for(int p = this._queues.length - 1; p >= 0; --p) {
                              AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                              if (asyncContext != null) {
                                 ServletRequest candidate = asyncContext.getRequest();
                                 Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                                 if (suspended == Boolean.TRUE) {
                                    if (LOG.isDebugEnabled()) {
                                       LOG.debug("Resuming {}", request);
                                    }

                                    candidate.setAttribute(this._resumed, Boolean.TRUE);
                                    asyncContext.dispatch();
                                    break;
                                 }
                              }
                           }
                        } finally {
                           this._passes.release();
                        }
                     }

                  }
               }

               if (accepted) {
                  try {
                     for(int p = this._queues.length - 1; p >= 0; --p) {
                        AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                        if (asyncContext != null) {
                           ServletRequest candidate = asyncContext.getRequest();
                           Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                           if (suspended == Boolean.TRUE) {
                              if (LOG.isDebugEnabled()) {
                                 LOG.debug("Resuming {}", request);
                              }

                              candidate.setAttribute(this._resumed, Boolean.TRUE);
                              asyncContext.dispatch();
                              return;
                           }
                        }
                     }
                  } finally {
                     this._passes.release();
                  }
               }

               return;
            }

            if (accepted) {
               try {
                  for(int p = this._queues.length - 1; p >= 0; --p) {
                     AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                     if (asyncContext != null) {
                        ServletRequest candidate = asyncContext.getRequest();
                        Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                        if (suspended == Boolean.TRUE) {
                           if (LOG.isDebugEnabled()) {
                              LOG.debug("Resuming {}", request);
                           }

                           candidate.setAttribute(this._resumed, Boolean.TRUE);
                           asyncContext.dispatch();
                           return;
                        }
                     }
                  }
               } finally {
                  this._passes.release();
               }
            }

            return;
         }

         if (accepted) {
            try {
               for(int p = this._queues.length - 1; p >= 0; --p) {
                  AsyncContext asyncContext = (AsyncContext)this._queues[p].poll();
                  if (asyncContext != null) {
                     ServletRequest candidate = asyncContext.getRequest();
                     Boolean suspended = (Boolean)candidate.getAttribute(this._suspended);
                     if (suspended == Boolean.TRUE) {
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Resuming {}", request);
                        }

                        candidate.setAttribute(this._resumed, Boolean.TRUE);
                        asyncContext.dispatch();
                        break;
                     }
                  }
               }
            } finally {
               this._passes.release();
            }
         }

      }
   }

   protected void doFilterChain(FilterChain chain, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
      final Thread thread = Thread.currentThread();
      Runnable requestTimeout = new Runnable() {
         public void run() {
            DoSFilter.this.closeConnection(request, response, thread);
         }
      };
      Scheduler.Task task = this._scheduler.schedule(requestTimeout, this.getMaxRequestMs(), TimeUnit.MILLISECONDS);

      try {
         chain.doFilter(request, response);
      } finally {
         task.cancel();
      }

   }

   protected void onRequestTimeout(HttpServletRequest request, HttpServletResponse response, Thread handlingThread) {
      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Timing out {}", request);
         }

         response.sendError(503);
      } catch (Throwable x) {
         LOG.info(x);
      }

      handlingThread.interrupt();
   }

   /** @deprecated */
   @Deprecated
   protected void closeConnection(HttpServletRequest request, HttpServletResponse response, Thread thread) {
      this.onRequestTimeout(request, response, thread);
   }

   protected int getPriority(HttpServletRequest request, RateTracker tracker) {
      if (this.extractUserId(request) != null) {
         return 2;
      } else {
         return tracker != null ? tracker.getType() : 0;
      }
   }

   protected int getMaxPriority() {
      return 2;
   }

   public RateTracker getRateTracker(ServletRequest request) {
      HttpSession session = ((HttpServletRequest)request).getSession(false);
      String loadId = this.extractUserId(request);
      int type;
      if (loadId != null) {
         type = 2;
      } else if (this.isTrackSessions() && session != null && !session.isNew()) {
         loadId = session.getId();
         type = 2;
      } else {
         loadId = this.isRemotePort() ? request.getRemoteAddr() + request.getRemotePort() : request.getRemoteAddr();
         type = 1;
      }

      RateTracker tracker = (RateTracker)this._rateTrackers.get(loadId);
      if (tracker == null) {
         boolean allowed = this.checkWhitelist(request.getRemoteAddr());
         int maxRequestsPerSec = this.getMaxRequestsPerSec();
         tracker = (RateTracker)(allowed ? new FixedRateTracker(loadId, type, maxRequestsPerSec) : new RateTracker(loadId, type, maxRequestsPerSec));
         RateTracker existing = (RateTracker)this._rateTrackers.putIfAbsent(loadId, tracker);
         if (existing != null) {
            tracker = existing;
         }

         if (type == 1) {
            this._scheduler.schedule(tracker, this.getMaxIdleTrackerMs(), TimeUnit.MILLISECONDS);
         } else if (session != null) {
            session.setAttribute("DoSFilter.Tracker", tracker);
         }
      }

      return tracker;
   }

   protected boolean checkWhitelist(String candidate) {
      for(String address : this._whitelist) {
         if (address.contains("/")) {
            if (this.subnetMatch(address, candidate)) {
               return true;
            }
         } else if (address.equals(candidate)) {
            return true;
         }
      }

      return false;
   }

   /** @deprecated */
   @Deprecated
   protected boolean checkWhitelist(List<String> whitelist, String candidate) {
      for(String address : whitelist) {
         if (address.contains("/")) {
            if (this.subnetMatch(address, candidate)) {
               return true;
            }
         } else if (address.equals(candidate)) {
            return true;
         }
      }

      return false;
   }

   protected boolean subnetMatch(String subnetAddress, String address) {
      Matcher cidrMatcher = CIDR_PATTERN.matcher(subnetAddress);
      if (!cidrMatcher.matches()) {
         return false;
      } else {
         String subnet = cidrMatcher.group(1);

         int prefix;
         try {
            prefix = Integer.parseInt(cidrMatcher.group(2));
         } catch (NumberFormatException var11) {
            LOG.info("Ignoring malformed CIDR address {}", subnetAddress);
            return false;
         }

         byte[] subnetBytes = this.addressToBytes(subnet);
         if (subnetBytes == null) {
            LOG.info("Ignoring malformed CIDR address {}", subnetAddress);
            return false;
         } else {
            byte[] addressBytes = this.addressToBytes(address);
            if (addressBytes == null) {
               LOG.info("Ignoring malformed remote address {}", address);
               return false;
            } else {
               int length = subnetBytes.length;
               if (length != addressBytes.length) {
                  return false;
               } else {
                  byte[] mask = this.prefixToBytes(prefix, length);

                  for(int i = 0; i < length; ++i) {
                     if ((subnetBytes[i] & mask[i]) != (addressBytes[i] & mask[i])) {
                        return false;
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   private byte[] addressToBytes(String address) {
      Matcher ipv4Matcher = IPv4_PATTERN.matcher(address);
      if (ipv4Matcher.matches()) {
         byte[] result = new byte[4];

         for(int i = 0; i < result.length; ++i) {
            result[i] = Integer.valueOf(ipv4Matcher.group(i + 1)).byteValue();
         }

         return result;
      } else {
         Matcher ipv6Matcher = IPv6_PATTERN.matcher(address);
         if (!ipv6Matcher.matches()) {
            return null;
         } else {
            byte[] result = new byte[16];

            for(int i = 0; i < result.length; i += 2) {
               int word = Integer.valueOf(ipv6Matcher.group(i / 2 + 1), 16);
               result[i] = (byte)((word & '\uff00') >>> 8);
               result[i + 1] = (byte)(word & 255);
            }

            return result;
         }
      }
   }

   private byte[] prefixToBytes(int prefix, int length) {
      byte[] result = new byte[length];

      int index;
      for(index = 0; prefix / 8 > 0; ++index) {
         result[index] = -1;
         prefix -= 8;
      }

      if (index == result.length) {
         return result;
      } else {
         result[index] = (byte)(~((1 << 8 - prefix) - 1));
         return result;
      }
   }

   public void destroy() {
      LOG.debug("Destroy {}", this);
      this.stopScheduler();
      this._rateTrackers.clear();
      this._whitelist.clear();
   }

   protected void stopScheduler() {
      try {
         this._scheduler.stop();
      } catch (Exception x) {
         LOG.ignore(x);
      }

   }

   protected String extractUserId(ServletRequest request) {
      return null;
   }

   @ManagedAttribute("maximum number of requests allowed from a connection per second")
   public int getMaxRequestsPerSec() {
      return this._maxRequestsPerSec;
   }

   public void setMaxRequestsPerSec(int value) {
      this._maxRequestsPerSec = value;
   }

   @ManagedAttribute("delay applied to all requests over the rate limit (in ms)")
   public long getDelayMs() {
      return this._delayMs;
   }

   public void setDelayMs(long value) {
      this._delayMs = value;
   }

   @ManagedAttribute("maximum time the filter will block waiting throttled connections, (0 for no delay, -1 to reject requests)")
   public long getMaxWaitMs() {
      return this._maxWaitMs;
   }

   public void setMaxWaitMs(long value) {
      this._maxWaitMs = value;
   }

   @ManagedAttribute("number of requests over rate limit")
   public int getThrottledRequests() {
      return this._throttledRequests;
   }

   public void setThrottledRequests(int value) {
      int permits = this._passes == null ? 0 : this._passes.availablePermits();
      this._passes = new Semaphore(value - this._throttledRequests + permits, true);
      this._throttledRequests = value;
   }

   @ManagedAttribute("amount of time to async wait for semaphore")
   public long getThrottleMs() {
      return this._throttleMs;
   }

   public void setThrottleMs(long value) {
      this._throttleMs = value;
   }

   @ManagedAttribute("maximum time to allow requests to process (in ms)")
   public long getMaxRequestMs() {
      return this._maxRequestMs;
   }

   public void setMaxRequestMs(long value) {
      this._maxRequestMs = value;
   }

   @ManagedAttribute("maximum time to track of request rates for connection before discarding")
   public long getMaxIdleTrackerMs() {
      return this._maxIdleTrackerMs;
   }

   public void setMaxIdleTrackerMs(long value) {
      this._maxIdleTrackerMs = value;
   }

   @ManagedAttribute("inser DoSFilter headers in response")
   public boolean isInsertHeaders() {
      return this._insertHeaders;
   }

   public void setInsertHeaders(boolean value) {
      this._insertHeaders = value;
   }

   @ManagedAttribute("usage rate is tracked by session if one exists")
   public boolean isTrackSessions() {
      return this._trackSessions;
   }

   public void setTrackSessions(boolean value) {
      this._trackSessions = value;
   }

   @ManagedAttribute("usage rate is tracked by IP+port is session tracking not used")
   public boolean isRemotePort() {
      return this._remotePort;
   }

   public void setRemotePort(boolean value) {
      this._remotePort = value;
   }

   @ManagedAttribute("whether this filter is enabled")
   public boolean isEnabled() {
      return this._enabled;
   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
   }

   public int getTooManyCode() {
      return this._tooManyCode;
   }

   public void setTooManyCode(int tooManyCode) {
      this._tooManyCode = tooManyCode;
   }

   @ManagedAttribute("list of IPs that will not be rate limited")
   public String getWhitelist() {
      StringBuilder result = new StringBuilder();
      Iterator<String> iterator = this._whitelist.iterator();

      while(iterator.hasNext()) {
         String address = (String)iterator.next();
         result.append(address);
         if (iterator.hasNext()) {
            result.append(",");
         }
      }

      return result.toString();
   }

   public void setWhitelist(String commaSeparatedList) {
      List<String> result = new ArrayList();

      for(String address : StringUtil.csvSplit(commaSeparatedList)) {
         this.addWhitelistAddress(result, address);
      }

      this.clearWhitelist();
      this._whitelist.addAll(result);
      LOG.debug("Whitelisted IP addresses: {}", result);
   }

   @ManagedOperation("clears the list of IP addresses that will not be rate limited")
   public void clearWhitelist() {
      this._whitelist.clear();
   }

   @ManagedOperation("adds an IP address that will not be rate limited")
   public boolean addWhitelistAddress(@Name("address") String address) {
      return this.addWhitelistAddress(this._whitelist, address);
   }

   private boolean addWhitelistAddress(List<String> list, String address) {
      address = address.trim();
      return address.length() > 0 && list.add(address);
   }

   @ManagedOperation("removes an IP address that will not be rate limited")
   public boolean removeWhitelistAddress(@Name("address") String address) {
      return this._whitelist.remove(address);
   }

   class RateTracker implements Runnable, HttpSessionBindingListener, HttpSessionActivationListener, Serializable {
      private static final long serialVersionUID = 3534663738034577872L;
      protected final String _id;
      protected final int _type;
      protected final long[] _timestamps;
      protected int _next;

      public RateTracker(String id, int type, int maxRequestsPerSecond) {
         this._id = id;
         this._type = type;
         this._timestamps = new long[maxRequestsPerSecond];
         this._next = 0;
      }

      public boolean isRateExceeded(long now) {
         long last;
         synchronized(this) {
            last = this._timestamps[this._next];
            this._timestamps[this._next] = now;
            this._next = (this._next + 1) % this._timestamps.length;
         }

         return last != 0L && now - last < 1000L;
      }

      public String getId() {
         return this._id;
      }

      public int getType() {
         return this._type;
      }

      public void valueBound(HttpSessionBindingEvent event) {
         if (DoSFilter.LOG.isDebugEnabled()) {
            DoSFilter.LOG.debug("Value bound: {}", this.getId());
         }

      }

      public void valueUnbound(HttpSessionBindingEvent event) {
         DoSFilter.this._rateTrackers.remove(this._id);
         if (DoSFilter.LOG.isDebugEnabled()) {
            DoSFilter.LOG.debug("Tracker removed: {}", this.getId());
         }

      }

      public void sessionWillPassivate(HttpSessionEvent se) {
         DoSFilter.this._rateTrackers.remove(this._id);
      }

      public void sessionDidActivate(HttpSessionEvent se) {
         RateTracker tracker = (RateTracker)se.getSession().getAttribute("DoSFilter.Tracker");
         if (tracker != null) {
            DoSFilter.this._rateTrackers.put(tracker.getId(), tracker);
         }

      }

      public void run() {
         int latestIndex = this._next == 0 ? this._timestamps.length - 1 : this._next - 1;
         long last = this._timestamps[latestIndex];
         boolean hasRecentRequest = last != 0L && System.currentTimeMillis() - last < 1000L;
         if (hasRecentRequest) {
            DoSFilter.this._scheduler.schedule(this, DoSFilter.this.getMaxIdleTrackerMs(), TimeUnit.MILLISECONDS);
         } else {
            DoSFilter.this._rateTrackers.remove(this._id);
         }

      }

      public String toString() {
         return "RateTracker/" + this._id + "/" + this._type;
      }
   }

   class FixedRateTracker extends RateTracker {
      public FixedRateTracker(String id, int type, int numRecentRequestsTracked) {
         super(id, type, numRecentRequestsTracked);
      }

      public boolean isRateExceeded(long now) {
         synchronized(this) {
            this._timestamps[this._next] = now;
            this._next = (this._next + 1) % this._timestamps.length;
            return false;
         }
      }

      public String toString() {
         return "Fixed" + super.toString();
      }
   }

   private class DoSTimeoutAsyncListener implements AsyncListener {
      private DoSTimeoutAsyncListener() {
      }

      public void onStartAsync(AsyncEvent event) throws IOException {
      }

      public void onComplete(AsyncEvent event) throws IOException {
      }

      public void onTimeout(AsyncEvent event) throws IOException {
         event.getAsyncContext().dispatch();
      }

      public void onError(AsyncEvent event) throws IOException {
      }
   }

   private class DoSAsyncListener extends DoSTimeoutAsyncListener {
      private final int priority;

      public DoSAsyncListener(int priority) {
         this.priority = priority;
      }

      public void onTimeout(AsyncEvent event) throws IOException {
         DoSFilter.this._queues[this.priority].remove(event.getAsyncContext());
         super.onTimeout(event);
      }
   }
}
