package org.spark_project.jetty.servlets;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.server.Dispatcher;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Push cache based on the HTTP 'Referer' header")
public class PushCacheFilter implements Filter {
   private static final Logger LOG = Log.getLogger(PushCacheFilter.class);
   private final Set<Integer> _ports = new HashSet();
   private final Set<String> _hosts = new HashSet();
   private final ConcurrentMap<String, PrimaryResource> _cache = new ConcurrentHashMap();
   private long _associatePeriod = 4000L;
   private int _maxAssociations = 16;
   private long _renew = System.nanoTime();
   private boolean _useQueryInKey;

   public void init(FilterConfig config) throws ServletException {
      String associatePeriod = config.getInitParameter("associatePeriod");
      if (associatePeriod != null) {
         this._associatePeriod = Long.parseLong(associatePeriod);
      }

      String maxAssociations = config.getInitParameter("maxAssociations");
      if (maxAssociations != null) {
         this._maxAssociations = Integer.parseInt(maxAssociations);
      }

      String hosts = config.getInitParameter("hosts");
      if (hosts != null) {
         Collections.addAll(this._hosts, StringUtil.csvSplit(hosts));
      }

      String ports = config.getInitParameter("ports");
      if (ports != null) {
         for(String p : StringUtil.csvSplit(ports)) {
            this._ports.add(Integer.parseInt(p));
         }
      }

      this._useQueryInKey = Boolean.parseBoolean(config.getInitParameter("useQueryInKey"));
      config.getServletContext().setAttribute(config.getFilterName(), this);
      if (LOG.isDebugEnabled()) {
         LOG.debug("period={} max={} hosts={} ports={}", this._associatePeriod, this._maxAssociations, this._hosts, this._ports);
      }

   }

   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)req;
      if (HttpVersion.fromString(req.getProtocol()).getVersion() >= 20 && HttpMethod.GET.is(request.getMethod())) {
         long now = System.nanoTime();
         Request jettyRequest = Request.getBaseRequest(request);
         HttpFields fields = jettyRequest.getHttpFields();
         boolean conditional = false;
         String referrer = null;

         label154:
         for(int i = 0; i < fields.size(); ++i) {
            HttpField field = fields.getField(i);
            HttpHeader header = field.getHeader();
            if (header != null) {
               switch (header) {
                  case IF_MATCH:
                  case IF_MODIFIED_SINCE:
                  case IF_NONE_MATCH:
                  case IF_UNMODIFIED_SINCE:
                     conditional = true;
                     break label154;
                  case REFERER:
                     referrer = field.getValue();
               }
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} {} referrer={} conditional={} synthetic={}", request.getMethod(), request.getRequestURI(), referrer, conditional, this.isPushRequest(request));
         }

         String key = URIUtil.addPaths(request.getServletPath(), request.getPathInfo());
         if (this._useQueryInKey) {
            String query = request.getQueryString();
            if (query != null) {
               key = key + "?" + query;
            }
         }

         if (referrer != null) {
            HttpURI referrerURI = new HttpURI(referrer);
            String host = referrerURI.getHost();
            int port = referrerURI.getPort();
            if (port <= 0) {
               port = request.isSecure() ? 443 : 80;
            }

            boolean referredFromHere = this._hosts.size() > 0 ? this._hosts.contains(host) : host.equals(request.getServerName());
            referredFromHere &= this._ports.size() > 0 ? this._ports.contains(port) : port == request.getServerPort();
            if (referredFromHere) {
               if (HttpMethod.GET.is(request.getMethod())) {
                  String referrerPath = this._useQueryInKey ? referrerURI.getPathQuery() : referrerURI.getPath();
                  if (referrerPath == null) {
                     referrerPath = "/";
                  }

                  if (referrerPath.startsWith(request.getContextPath())) {
                     String referrerPathNoContext = referrerPath.substring(request.getContextPath().length());
                     if (!referrerPathNoContext.equals(key)) {
                        PrimaryResource primaryResource = (PrimaryResource)this._cache.get(referrerPathNoContext);
                        if (primaryResource != null) {
                           long primaryTimestamp = primaryResource._timestamp.get();
                           if (primaryTimestamp != 0L) {
                              RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(key);
                              if (now - primaryTimestamp < TimeUnit.MILLISECONDS.toNanos(this._associatePeriod)) {
                                 ConcurrentMap<String, RequestDispatcher> associated = primaryResource._associated;
                                 if (associated.size() <= this._maxAssociations) {
                                    if (associated.putIfAbsent(key, dispatcher) == null && LOG.isDebugEnabled()) {
                                       LOG.debug("Associated {} to {}", key, referrerPathNoContext);
                                    }
                                 } else if (LOG.isDebugEnabled()) {
                                    LOG.debug("Not associated {} to {}, exceeded max associations of {}", key, referrerPathNoContext, this._maxAssociations);
                                 }
                              } else if (LOG.isDebugEnabled()) {
                                 LOG.debug("Not associated {} to {}, outside associate period of {}ms", key, referrerPathNoContext, this._associatePeriod);
                              }
                           }
                        }
                     } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Not associated {} to {}, referring to self", key, referrerPathNoContext);
                     }
                  }
               }
            } else if (LOG.isDebugEnabled()) {
               LOG.debug("External referrer {}", referrer);
            }
         }

         PrimaryResource primaryResource = (PrimaryResource)this._cache.get(key);
         if (primaryResource == null) {
            PrimaryResource r = new PrimaryResource();
            PrimaryResource var27 = (PrimaryResource)this._cache.putIfAbsent(key, r);
            primaryResource = var27 == null ? r : var27;
            primaryResource._timestamp.compareAndSet(0L, now);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Cached primary resource {}", key);
            }
         } else {
            long last = primaryResource._timestamp.get();
            if (last < this._renew && primaryResource._timestamp.compareAndSet(last, now)) {
               primaryResource._associated.clear();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Clear associated resources for {}", key);
               }
            }
         }

         if (!this.isPushRequest(request) && !conditional && !primaryResource._associated.isEmpty()) {
            Queue<PrimaryResource> queue = new ArrayDeque();
            queue.offer(primaryResource);

            while(!queue.isEmpty()) {
               PrimaryResource parent = (PrimaryResource)queue.poll();

               for(Map.Entry<String, RequestDispatcher> entry : parent._associated.entrySet()) {
                  PrimaryResource child = (PrimaryResource)this._cache.get(entry.getKey());
                  if (child != null) {
                     queue.offer(child);
                  }

                  Dispatcher dispatcher = (Dispatcher)entry.getValue();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Pushing {} for {}", dispatcher, key);
                  }

                  dispatcher.push(request);
               }
            }
         }

         chain.doFilter(request, resp);
      } else {
         chain.doFilter(req, resp);
      }
   }

   private boolean isPushRequest(HttpServletRequest request) {
      return Boolean.TRUE.equals(request.getAttribute("org.spark_project.jetty.pushed"));
   }

   public void destroy() {
      this.clearPushCache();
   }

   @ManagedAttribute("The push cache contents")
   public Map<String, String> getPushCache() {
      Map<String, String> result = new HashMap();

      for(Map.Entry<String, PrimaryResource> entry : this._cache.entrySet()) {
         PrimaryResource resource = (PrimaryResource)entry.getValue();
         String value = String.format("size=%d: %s", resource._associated.size(), new TreeSet(resource._associated.keySet()));
         result.put(entry.getKey(), value);
      }

      return result;
   }

   @ManagedOperation(
      value = "Renews the push cache contents",
      impact = "ACTION"
   )
   public void renewPushCache() {
      this._renew = System.nanoTime();
   }

   @ManagedOperation(
      value = "Clears the push cache contents",
      impact = "ACTION"
   )
   public void clearPushCache() {
      this._cache.clear();
   }

   private static class PrimaryResource {
      private final ConcurrentMap<String, RequestDispatcher> _associated;
      private final AtomicLong _timestamp;

      private PrimaryResource() {
         this._associated = new ConcurrentHashMap();
         this._timestamp = new AtomicLong();
      }
   }
}
