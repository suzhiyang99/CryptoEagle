package org.spark_project.jetty.io;

import java.net.Socket;
import java.nio.ByteBuffer;

public interface NetworkTrafficListener {
   void opened(Socket var1);

   void incoming(Socket var1, ByteBuffer var2);

   void outgoing(Socket var1, ByteBuffer var2);

   void closed(Socket var1);

   public static class Adapter implements NetworkTrafficListener {
      public void opened(Socket socket) {
      }

      public void incoming(Socket socket, ByteBuffer bytes) {
      }

      public void outgoing(Socket socket, ByteBuffer bytes) {
      }

      public void closed(Socket socket) {
      }
   }
}
