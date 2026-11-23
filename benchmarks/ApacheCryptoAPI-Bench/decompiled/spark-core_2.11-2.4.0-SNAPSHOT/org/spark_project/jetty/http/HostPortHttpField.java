package org.spark_project.jetty.http;

import org.spark_project.jetty.util.HostPort;

public class HostPortHttpField extends HttpField {
   final HostPort _hostPort;

   public HostPortHttpField(String authority) {
      this(HttpHeader.HOST, HttpHeader.HOST.asString(), authority);
   }

   protected HostPortHttpField(HttpHeader header, String name, String authority) {
      super(header, name, authority);

      try {
         this._hostPort = new HostPort(authority);
      } catch (Exception e) {
         throw new BadMessageException(400, "Bad HostPort", e);
      }
   }

   public String getHost() {
      return this._hostPort.getHost();
   }

   public int getPort() {
      return this._hostPort.getPort();
   }

   public int getPort(int defaultPort) {
      return this._hostPort.getPort(defaultPort);
   }
}
