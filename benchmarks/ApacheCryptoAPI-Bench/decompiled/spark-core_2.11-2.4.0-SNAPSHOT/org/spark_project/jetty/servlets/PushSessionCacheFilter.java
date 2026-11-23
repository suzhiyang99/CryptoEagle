package org.spark_project.jetty.servlets;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.server.PushBuilder;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Response;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class PushSessionCacheFilter implements Filter {
   private static final String TARGET_ATTR = "PushCacheFilter.target";
   private static final String TIMESTAMP_ATTR = "PushCacheFilter.timestamp";
   private static final Logger LOG = Log.getLogger(PushSessionCacheFilter.class);
   private final ConcurrentMap<String, Target> _cache = new ConcurrentHashMap();
   private long _associateDelay = 5000L;

   public void init(FilterConfig config) throws ServletException {
      if (config.getInitParameter("associateDelay") != null) {
         this._associateDelay = Long.valueOf(config.getInitParameter("associateDelay"));
      }

      config.getServletContext().addListener(new ServletRequestListener() {
         public void requestDestroyed(ServletRequestEvent sre) {
            Request request = Request.getBaseRequest(sre.getServletRequest());
            Target target = (Target)request.getAttribute("PushCacheFilter.target");
            if (target != null) {
               Response response = request.getResponse();
               target._etag = response.getHttpFields().get(HttpHeader.ETAG);
               target._lastModified = response.getHttpFields().get(HttpHeader.LAST_MODIFIED);
               if (request.isPush()) {
                  if (PushSessionCacheFilter.LOG.isDebugEnabled()) {
                     PushSessionCacheFilter.LOG.debug("Pushed {} for {}", request.getResponse().getStatus(), request.getRequestURI());
                  }

               } else {
                  if (PushSessionCacheFilter.LOG.isDebugEnabled()) {
                     PushSessionCacheFilter.LOG.debug("Served {} for {}", request.getResponse().getStatus(), request.getRequestURI());
                  }

                  String referer = request.getHttpFields().get(HttpHeader.REFERER);
                  if (referer != null) {
                     HttpURI referer_uri = new HttpURI(referer);
                     if (request.getServerName().equals(referer_uri.getHost())) {
                        Target referer_target = (Target)PushSessionCacheFilter.this._cache.get(referer_uri.getPath());
                        if (referer_target != null) {
                           HttpSession session = request.getSession();
                           ConcurrentHashMap<String, Long> timestamps = (ConcurrentHashMap)session.getAttribute("PushCacheFilter.timestamp");
                           Long last = (Long)timestamps.get(referer_target._path);
                           if (last != null && System.currentTimeMillis() - last < PushSessionCacheFilter.this._associateDelay && referer_target._associated.putIfAbsent(target._path, target) == null && PushSessionCacheFilter.LOG.isDebugEnabled()) {
                              PushSessionCacheFilter.LOG.debug("ASSOCIATE {}->{}", referer_target._path, target._path);
                           }
                        }
                     }
                  }

               }
            }
         }

         public void requestInitialized(ServletRequestEvent sre) {
         }
      });
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      Request baseRequest = Request.getBaseRequest(request);
      String uri = baseRequest.getRequestURI();
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} {} push={}", baseRequest.getMethod(), uri, baseRequest.isPush());
      }

      HttpSession session = baseRequest.getSession(true);
      Target target = (Target)this._cache.get(uri);
      if (target == null) {
         Target t = new Target(uri);
         target = (Target)this._cache.putIfAbsent(uri, t);
         target = target == null ? t : target;
      }

      request.setAttribute("PushCacheFilter.target", target);
      ConcurrentHashMap<String, Long> timestamps = (ConcurrentHashMap)session.getAttribute("PushCacheFilter.timestamp");
      if (timestamps == null) {
         timestamps = new ConcurrentHashMap();
         session.setAttribute("PushCacheFilter.timestamp", timestamps);
      }

      timestamps.put(uri, System.currentTimeMillis());
      if (baseRequest.isPushSupported() && !baseRequest.isPush() && !target._associated.isEmpty()) {
         Queue<Target> queue = new ArrayDeque();
         queue.offer(target);

         while(!queue.isEmpty()) {
            Target parent = (Target)queue.poll();
            PushBuilder builder = baseRequest.getPushBuilder();
            builder.addHeader("X-Pusher", PushSessionCacheFilter.class.toString());

            for(Target child : parent._associated.values()) {
               queue.offer(child);
               String path = child._path;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("PUSH {} <- {}", path, uri);
               }

               builder.path(path).etag(child._etag).lastModified(child._lastModified).push();
            }
         }
      }

      chain.doFilter(request, response);
   }

   public void destroy() {
      this._cache.clear();
   }

   private static class Target {
      private final String _path;
      private final ConcurrentMap<String, Target> _associated;
      private volatile String _etag;
      private volatile String _lastModified;

      private Target(String path) {
         this._associated = new ConcurrentHashMap();
         this._path = path;
      }

      public String toString() {
         return String.format("Target{p=%s,e=%s,m=%s,a=%d}", this._path, this._etag, this._lastModified, this._associated.size());
      }
   }
}
