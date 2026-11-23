package org.spark_project.jetty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.net.ssl.SSLEngine;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.ssl.SslConnection;

public abstract class NegotiatingServerConnectionFactory extends AbstractConnectionFactory {
   private final List<String> negotiatedProtocols = new ArrayList();
   private String defaultProtocol;

   public static void checkProtocolNegotiationAvailable() {
      try {
         String javaVersion = System.getProperty("java.version");
         String alpnClassName = "org.spark_project.jetty.alpn.ALPN";
         if (javaVersion.startsWith("1.")) {
            Class<?> klass = ClassLoader.getSystemClassLoader().loadClass(alpnClassName);
            if (klass.getClassLoader() != null) {
               throw new IllegalStateException(alpnClassName + " must be on JVM boot classpath");
            }
         } else {
            NegotiatingServerConnectionFactory.class.getClassLoader().loadClass(alpnClassName);
         }

      } catch (ClassNotFoundException var3) {
         throw new IllegalStateException("No ALPN classes available");
      }
   }

   public NegotiatingServerConnectionFactory(String protocol, String... negotiatedProtocols) {
      super(protocol);
      if (negotiatedProtocols != null) {
         for(String p : negotiatedProtocols) {
            p = p.trim();
            if (!p.isEmpty()) {
               this.negotiatedProtocols.add(p.trim());
            }
         }
      }

   }

   public String getDefaultProtocol() {
      return this.defaultProtocol;
   }

   public void setDefaultProtocol(String defaultProtocol) {
      String dft = defaultProtocol == null ? "" : defaultProtocol.trim();
      this.defaultProtocol = dft.isEmpty() ? null : dft;
   }

   public List<String> getNegotiatedProtocols() {
      return this.negotiatedProtocols;
   }

   public Connection newConnection(Connector connector, EndPoint endPoint) {
      List<String> negotiated = this.negotiatedProtocols;
      if (negotiated.isEmpty()) {
         negotiated = (List)connector.getProtocols().stream().filter((p) -> {
            ConnectionFactory f = connector.getConnectionFactory(p);
            return !(f instanceof SslConnectionFactory) && !(f instanceof NegotiatingServerConnectionFactory);
         }).collect(Collectors.toList());
      }

      String dft = this.defaultProtocol;
      if (dft == null && !negotiated.isEmpty()) {
         if (negotiated.contains(HttpVersion.HTTP_1_1.asString())) {
            dft = HttpVersion.HTTP_1_1.asString();
         } else {
            dft = (String)negotiated.get(0);
         }
      }

      SSLEngine engine = null;
      EndPoint ep = endPoint;

      while(engine == null && ep != null) {
         if (ep instanceof SslConnection.DecryptedEndPoint) {
            engine = ((SslConnection.DecryptedEndPoint)ep).getSslConnection().getSSLEngine();
         } else {
            ep = null;
         }
      }

      return this.configure(this.newServerConnection(connector, endPoint, engine, negotiated, dft), connector, endPoint);
   }

   protected abstract AbstractConnection newServerConnection(Connector var1, EndPoint var2, SSLEngine var3, List<String> var4, String var5);

   public String toString() {
      return String.format("%s@%x{%s,%s,%s}", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols(), this.getDefaultProtocol(), this.getNegotiatedProtocols());
   }
}
