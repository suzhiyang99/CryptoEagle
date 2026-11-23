package org.apache.openejb.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Collection;

public final class NetworkUtil {
   private NetworkUtil() {
   }

   public static int getNextAvailablePort() {
      return getNextAvailablePort(new int[]{0});
   }

   public static int getNextAvailablePort(int[] portList) {
      ServerSocket s = null;

      int port;
      try {
         s = create(portList);
         port = s.getLocalPort();
      } catch (IOException var12) {
         port = -1;
      } finally {
         if (s != null) {
            try {
               s.close();
            } catch (Throwable var11) {
            }
         }

      }

      return port;
   }

   public static int getNextAvailablePort(int min, int max, Collection<Integer> excepted) {
      int port = -1;
      ServerSocket s = null;

      for(int i = min; i <= max; ++i) {
         try {
            s = create(new int[]{i});
            port = s.getLocalPort();
            if (excepted == null || !excepted.contains(port)) {
               break;
            }
         } catch (IOException var16) {
            port = -1;
         } finally {
            if (s != null) {
               try {
                  s.close();
               } catch (Throwable var15) {
               }
            }

         }
      }

      return port;
   }

   private static ServerSocket create(int[] ports) throws IOException {
      for(int port : ports) {
         try {
            return new ServerSocket(port);
         } catch (IOException var6) {
         }
      }

      throw new IOException("No free port found");
   }

   public static String getLocalAddress(String start, String end) {
      return start + "localhost:" + getNextAvailablePort() + end;
   }

   public static boolean isLocalAddress(String addr) {
      try {
         return isLocalAddress(InetAddress.getByName(addr));
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isLocalAddress(InetAddress addr) {
      if (!addr.isAnyLocalAddress() && !addr.isLoopbackAddress()) {
         try {
            return NetworkInterface.getByInetAddress(addr) != null;
         } catch (SocketException var2) {
            return false;
         }
      } else {
         return true;
      }
   }

   private static final class LastPort {
      private final int port;

      private LastPort(int port) {
         this.port = port;
      }
   }
}
