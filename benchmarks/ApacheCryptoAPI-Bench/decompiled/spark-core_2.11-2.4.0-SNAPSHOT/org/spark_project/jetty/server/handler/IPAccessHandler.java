package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.PathMap;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.server.HttpChannel;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.IPAddressMap;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

/** @deprecated */
public class IPAccessHandler extends HandlerWrapper {
   private static final Logger LOG = Log.getLogger(IPAccessHandler.class);
   PathMap<IPAddressMap<Boolean>> _white = new PathMap<IPAddressMap<Boolean>>(true);
   PathMap<IPAddressMap<Boolean>> _black = new PathMap<IPAddressMap<Boolean>>(true);
   boolean _whiteListByPath = false;

   public IPAccessHandler() {
   }

   public IPAccessHandler(String[] white, String[] black) {
      if (white != null && white.length > 0) {
         this.setWhite(white);
      }

      if (black != null && black.length > 0) {
         this.setBlack(black);
      }

   }

   public void addWhite(String entry) {
      this.add(entry, this._white);
   }

   public void addBlack(String entry) {
      this.add(entry, this._black);
   }

   public void setWhite(String[] entries) {
      this.set(entries, this._white);
   }

   public void setBlack(String[] entries) {
      this.set(entries, this._black);
   }

   public void setWhiteListByPath(boolean whiteListByPath) {
      this._whiteListByPath = whiteListByPath;
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      HttpChannel channel = baseRequest.getHttpChannel();
      if (channel != null) {
         EndPoint endp = channel.getEndPoint();
         if (endp != null) {
            InetSocketAddress address = endp.getRemoteAddress();
            if (address != null && !this.isAddrUriAllowed(address.getHostString(), baseRequest.getPathInfo())) {
               response.sendError(403);
               baseRequest.setHandled(true);
               return;
            }
         }
      }

      this.getHandler().handle(target, baseRequest, request, response);
   }

   protected void add(String entry, PathMap<IPAddressMap<Boolean>> patternMap) {
      if (entry != null && entry.length() > 0) {
         boolean deprecated = false;
         int idx;
         if (entry.indexOf(124) > 0) {
            idx = entry.indexOf(124);
         } else {
            idx = entry.indexOf(47);
            deprecated = idx >= 0;
         }

         String addr = idx > 0 ? entry.substring(0, idx) : entry;
         String path = idx > 0 ? entry.substring(idx) : "/*";
         if (addr.endsWith(".")) {
            deprecated = true;
         }

         if (path != null && (path.startsWith("|") || path.startsWith("/*."))) {
            path = path.substring(1);
         }

         IPAddressMap<Boolean> addrMap = (IPAddressMap)patternMap.get(path);
         if (addrMap == null) {
            addrMap = new IPAddressMap<Boolean>();
            patternMap.put((String)path, addrMap);
         }

         if (addr != null && !"".equals(addr)) {
            addrMap.put((String)addr, true);
         }

         if (deprecated) {
            LOG.debug(this.toString() + " - deprecated specification syntax: " + entry);
         }
      }

   }

   protected void set(String[] entries, PathMap<IPAddressMap<Boolean>> patternMap) {
      patternMap.clear();
      if (entries != null && entries.length > 0) {
         for(String addrPath : entries) {
            this.add(addrPath, patternMap);
         }
      }

   }

   protected boolean isAddrUriAllowed(String addr, String path) {
      if (this._white.size() > 0) {
         boolean match = false;
         boolean matchedByPath = false;

         for(Map.Entry<String, IPAddressMap<Boolean>> entry : this._white.getMatches(path)) {
            matchedByPath = true;
            IPAddressMap<Boolean> addrMap = (IPAddressMap)entry.getValue();
            if (addrMap != null && (addrMap.size() == 0 || addrMap.match(addr) != null)) {
               match = true;
               break;
            }
         }

         if (this._whiteListByPath) {
            if (matchedByPath && !match) {
               return false;
            }
         } else if (!match) {
            return false;
         }
      }

      if (this._black.size() > 0) {
         for(Map.Entry<String, IPAddressMap<Boolean>> entry : this._black.getMatches(path)) {
            IPAddressMap<Boolean> addrMap = (IPAddressMap)entry.getValue();
            if (addrMap != null && (addrMap.size() == 0 || addrMap.match(addr) != null)) {
               return false;
            }
         }
      }

      return true;
   }

   public String dump() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.toString());
      buf.append(" WHITELIST:\n");
      this.dump(buf, this._white);
      buf.append(this.toString());
      buf.append(" BLACKLIST:\n");
      this.dump(buf, this._black);
      return buf.toString();
   }

   protected void dump(StringBuilder buf, PathMap<IPAddressMap<Boolean>> patternMap) {
      for(String path : patternMap.keySet()) {
         for(String addr : ((IPAddressMap)patternMap.get(path)).keySet()) {
            buf.append("# ");
            buf.append(addr);
            buf.append("|");
            buf.append(path);
            buf.append("\n");
         }
      }

   }
}
