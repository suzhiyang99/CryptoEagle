package org.apache.activemq.artemis.utils;

import com.google.common.net.InetAddresses;

public class IPV6Util {
   public static String encloseHost(String host) {
      if (host != null && host.contains(":")) {
         String hostToCheck = host;
         if (host.contains("%")) {
            hostToCheck = host.substring(0, host.indexOf("%"));
         }

         if (InetAddresses.isInetAddress(hostToCheck)) {
            return "[" + host + "]";
         }
      }

      return host;
   }
}
