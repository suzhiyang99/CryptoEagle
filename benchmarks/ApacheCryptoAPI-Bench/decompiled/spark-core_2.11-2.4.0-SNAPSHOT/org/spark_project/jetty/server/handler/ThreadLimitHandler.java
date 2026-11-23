package org.spark_project.jetty.server.handler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HostPortHttpField;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.QuotedCSV;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.IncludeExcludeSet;
import org.spark_project.jetty.util.InetAddressSet;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.annotation.Name;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Locker;

public class ThreadLimitHandler extends HandlerWrapper {
   private static final Logger LOG = Log.getLogger(ThreadLimitHandler.class);
   private static final String REMOTE = "o.e.j.s.h.TLH.REMOTE";
   private static final String PERMIT = "o.e.j.s.h.TLH.PASS";
   private final boolean _rfc7239;
   private final String _forwardedHeader;
   private final IncludeExcludeSet<String, InetAddress> _includeExcludeSet;
   private final ConcurrentMap<String, Remote> _remotes;
   private volatile boolean _enabled;
   private int _threadLimit;

   public ThreadLimitHandler() {
      this((String)null, false);
   }

   public ThreadLimitHandler(@Name("forwardedHeader") String forwardedHeader) {
      this(forwardedHeader, HttpHeader.FORWARDED.is(forwardedHeader));
   }

   public ThreadLimitHandler(@Name("forwardedHeader") String forwardedHeader, @Name("rfc7239") boolean rfc7239) {
      this._includeExcludeSet = new IncludeExcludeSet<String, InetAddress>(InetAddressSet.class);
      this._remotes = new ConcurrentHashMap();
      this._threadLimit = 10;
      this._rfc7239 = rfc7239;
      this._forwardedHeader = forwardedHeader;
      this._enabled = true;
   }

   protected void doStart() throws Exception {
      super.doStart();
      LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d include=%s", this._enabled, this._threadLimit, this._includeExcludeSet));
   }

   @ManagedAttribute("true if this handler is enabled")
   public boolean isEnabled() {
      return this._enabled;
   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
      LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d include=%s", this._enabled, this._threadLimit, this._includeExcludeSet));
   }

   @ManagedAttribute("The maximum threads that can be dispatched per remote IP")
   public int getThreadLimit() {
      return this._threadLimit;
   }

   public void setThreadLimit(int threadLimit) {
      if (threadLimit <= 0) {
         throw new IllegalArgumentException("limit must be >0");
      } else {
         this._threadLimit = threadLimit;
      }
   }

   @ManagedOperation("Include IP in thread limits")
   public void include(String inetAddressPattern) {
      this._includeExcludeSet.include(inetAddressPattern);
   }

   @ManagedOperation("Exclude IP from thread limits")
   public void exclude(String inetAddressPattern) {
      this._includeExcludeSet.exclude(inetAddressPattern);
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (!this._enabled) {
         super.handle(target, baseRequest, request, response);
      } else {
         Remote remote = this.getRemote(baseRequest);
         if (remote == null) {
            super.handle(target, baseRequest, request, response);
         } else {
            Closeable permit = (Closeable)baseRequest.getAttribute("o.e.j.s.h.TLH.PASS");

            try {
               if (permit != null) {
                  baseRequest.removeAttribute("o.e.j.s.h.TLH.PASS");
               } else {
                  CompletableFuture<Closeable> future_permit = remote.acquire();
                  if (!future_permit.isDone()) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Threadlimited {} {}", remote, target);
                     }

                     AsyncContext async = baseRequest.startAsync();
                     async.setTimeout(0L);
                     future_permit.thenAccept((c) -> {
                        baseRequest.setAttribute("o.e.j.s.h.TLH.PASS", c);
                        async.dispatch();
                     });
                     return;
                  }

                  permit = (Closeable)future_permit.get();
               }

               super.handle(target, baseRequest, request, response);
               return;
            } catch (ExecutionException | InterruptedException e) {
               throw new ServletException(e);
            } finally {
               if (permit != null) {
                  permit.close();
               }

            }
         }
      }

   }

   protected int getThreadLimit(String ip) {
      if (!this._includeExcludeSet.isEmpty()) {
         try {
            if (!this._includeExcludeSet.test(InetAddress.getByName(ip))) {
               LOG.debug("excluded {}", ip);
               return 0;
            }
         } catch (Exception e) {
            LOG.ignore(e);
         }
      }

      return this._threadLimit;
   }

   protected Remote getRemote(Request baseRequest) {
      Remote remote = (Remote)baseRequest.getAttribute("o.e.j.s.h.TLH.REMOTE");
      if (remote != null) {
         return remote;
      } else {
         String ip = this.getRemoteIP(baseRequest);
         LOG.debug("ip={}", ip);
         if (ip == null) {
            return null;
         } else {
            int limit = this.getThreadLimit(ip);
            if (limit <= 0) {
               return null;
            } else {
               remote = (Remote)this._remotes.get(ip);
               if (remote == null) {
                  Remote r = new Remote(ip, limit);
                  remote = (Remote)this._remotes.putIfAbsent(ip, r);
                  if (remote == null) {
                     remote = r;
                  }
               }

               baseRequest.setAttribute("o.e.j.s.h.TLH.REMOTE", remote);
               return remote;
            }
         }
      }
   }

   protected String getRemoteIP(Request baseRequest) {
      if (this._forwardedHeader != null && !this._forwardedHeader.isEmpty()) {
         String remote = this._rfc7239 ? this.getForwarded(baseRequest) : this.getXForwardedFor(baseRequest);
         if (remote != null && !remote.isEmpty()) {
            return remote;
         }
      }

      InetSocketAddress inet_addr = baseRequest.getHttpChannel().getRemoteAddress();
      return inet_addr != null && inet_addr.getAddress() != null ? inet_addr.getAddress().getHostAddress() : null;
   }

   private String getForwarded(Request request) {
      RFC7239 rfc7239 = new RFC7239();

      for(HttpField field : request.getHttpFields()) {
         if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
            rfc7239.addValue(field.getValue());
         }
      }

      if (rfc7239.getFor() != null) {
         return (new HostPortHttpField(rfc7239.getFor())).getHost();
      } else {
         return null;
      }
   }

   private String getXForwardedFor(Request request) {
      String forwarded_for = null;

      for(HttpField field : request.getHttpFields()) {
         if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
            forwarded_for = field.getValue();
         }
      }

      if (forwarded_for != null && !forwarded_for.isEmpty()) {
         int comma = forwarded_for.lastIndexOf(44);
         return comma >= 0 ? forwarded_for.substring(comma + 1).trim() : forwarded_for;
      } else {
         return null;
      }
   }

   private final class Remote implements Closeable {
      private final String _ip;
      private final int _limit;
      private final Locker _locker = new Locker();
      private int _permits;
      private Deque<CompletableFuture<Closeable>> _queue = new ArrayDeque();
      private final CompletableFuture<Closeable> _permitted = CompletableFuture.completedFuture(this);

      public Remote(String ip, int limit) {
         this._ip = ip;
         this._limit = limit;
      }

      public CompletableFuture<Closeable> acquire() {
         CompletableFuture<Closeable> pass;
         try (Locker.Lock lock = this._locker.lock()) {
            if (this._permits >= this._limit) {
               pass = new CompletableFuture();
               this._queue.addLast(pass);
               CompletableFuture var4 = pass;
               return var4;
            }

            ++this._permits;
            pass = this._permitted;
         }

         return pass;
      }

      public void close() throws IOException {
         try (Locker.Lock lock = this._locker.lock()) {
            --this._permits;

            while(true) {
               CompletableFuture<Closeable> permit = (CompletableFuture)this._queue.pollFirst();
               if (permit == null) {
                  break;
               }

               if (permit.complete(this)) {
                  ++this._permits;
                  break;
               }
            }
         }

      }

      public String toString() {
         String var3;
         try (Locker.Lock lock = this._locker.lock()) {
            var3 = String.format("R[ip=%s,p=%d,l=%d,q=%d]", this._ip, this._permits, this._limit, this._queue.size());
         }

         return var3;
      }
   }

   private final class RFC7239 extends QuotedCSV {
      String _for;

      private RFC7239() {
         super(false);
      }

      String getFor() {
         return this._for;
      }

      protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
         if (valueLength == 0 && paramValue > paramName) {
            String name = StringUtil.asciiToLowerCase(buffer.substring(paramName, paramValue - 1));
            if ("for".equalsIgnoreCase(name)) {
               String value = buffer.substring(paramValue);
               if ("unknown".equalsIgnoreCase(value)) {
                  this._for = null;
               } else {
                  this._for = value;
               }
            }
         }

      }
   }
}
