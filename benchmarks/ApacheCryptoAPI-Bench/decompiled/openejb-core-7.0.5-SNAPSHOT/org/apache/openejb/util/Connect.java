package org.apache.openejb.util;

import java.net.Socket;

public class Connect {
   public static boolean connect(int tries, String host, int port) {
      Socket s = null;

      boolean e2;
      try {
         try {
            s = new Socket(host, port);
            s.getOutputStream().close();
            return true;
         } catch (Exception e) {
            if (tries >= 2) {
               try {
                  Thread.sleep(2000L);
               } catch (Exception var17) {
                  e.printStackTrace();
               }

               --tries;
               e2 = connect(tries, host, port);
               return e2;
            }
         }

         e2 = false;
      } finally {
         if (s != null) {
            try {
               s.close();
            } catch (Throwable var16) {
            }
         }

      }

      return e2;
   }
}
