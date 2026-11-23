package org.spark_project.jetty.server;

import java.net.Socket;
import org.spark_project.jetty.io.ChannelEndPoint;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.ssl.SslConnection;

public class SocketCustomizationListener implements Connection.Listener {
   private final boolean _ssl;

   public SocketCustomizationListener() {
      this(true);
   }

   public SocketCustomizationListener(boolean ssl) {
      this._ssl = ssl;
   }

   public void onOpened(Connection connection) {
      EndPoint endp = connection.getEndPoint();
      boolean ssl = false;
      if (this._ssl && endp instanceof SslConnection.DecryptedEndPoint) {
         endp = ((SslConnection.DecryptedEndPoint)endp).getSslConnection().getEndPoint();
         ssl = true;
      }

      if (endp instanceof ChannelEndPoint) {
         Socket socket = ((ChannelEndPoint)endp).getSocket();
         this.customize(socket, connection.getClass(), ssl);
      }

   }

   protected void customize(Socket socket, Class<? extends Connection> connection, boolean ssl) {
   }

   public void onClosed(Connection connection) {
   }
}
