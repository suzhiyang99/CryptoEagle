package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.server.HttpChannel;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.IncludeExcludeSet;
import org.spark_project.jetty.util.InetAddressSet;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class InetAccessHandler extends HandlerWrapper {
   private static final Logger LOG = Log.getLogger(InetAccessHandler.class);
   private final IncludeExcludeSet<String, InetAddress> _set = new IncludeExcludeSet<String, InetAddress>(InetAddressSet.class);

   public void include(String pattern) {
      this._set.include(pattern);
   }

   public void include(String... patterns) {
      this._set.include(patterns);
   }

   public void exclude(String pattern) {
      this._set.exclude(pattern);
   }

   public void exclude(String... patterns) {
      this._set.exclude(patterns);
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      HttpChannel channel = baseRequest.getHttpChannel();
      if (channel != null) {
         EndPoint endp = channel.getEndPoint();
         if (endp != null) {
            InetSocketAddress address = endp.getRemoteAddress();
            if (address != null && !this.isAllowed(address.getAddress(), request)) {
               response.sendError(403);
               baseRequest.setHandled(true);
               return;
            }
         }
      }

      this.getHandler().handle(target, baseRequest, request, response);
   }

   protected boolean isAllowed(InetAddress address, HttpServletRequest request) {
      return this.isAllowed(address);
   }

   /** @deprecated */
   @Deprecated
   protected boolean isAllowed(InetAddress address) {
      boolean allowed = this._set.test(address);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} {} {}", this, allowed ? "allowed" : "denied", address);
      }

      return allowed;
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpBeans(out, indent, new Collection[]{this._set.getIncluded(), this._set.getExcluded()});
   }
}
