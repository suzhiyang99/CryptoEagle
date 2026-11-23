package org.apache.manifoldcf.core.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.manifoldcf.core.interfaces.ISSLSocketFactoryProducer;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class LDAPSSLSocketFactory extends SSLSocketFactory {
   protected static final ThreadLocal<ISSLSocketFactoryProducer> sslSocketFactoryProducer = new ThreadLocal();
   protected final SSLSocketFactory wrappedSocketFactory;

   public static void setSocketFactoryProducer(ISSLSocketFactoryProducer p) {
      sslSocketFactoryProducer.set(p);
   }

   public LDAPSSLSocketFactory() throws ManifoldCFException {
      this.wrappedSocketFactory = ((ISSLSocketFactoryProducer)sslSocketFactoryProducer.get()).getSecureSocketFactory();
   }

   public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
      return this.wrappedSocketFactory.createSocket(s, host, port, autoClose);
   }

   public Socket createSocket(InetAddress source, int port, InetAddress target, int targetPort) throws IOException {
      return this.wrappedSocketFactory.createSocket(source, port, target, targetPort);
   }

   public Socket createSocket(String source, int port, InetAddress target, int targetPort) throws IOException {
      return this.wrappedSocketFactory.createSocket(source, port, target, targetPort);
   }

   public Socket createSocket(InetAddress source, int port) throws IOException {
      return this.wrappedSocketFactory.createSocket(source, port);
   }

   public Socket createSocket(String source, int port) throws IOException {
      return this.wrappedSocketFactory.createSocket(source, port);
   }

   public String[] getDefaultCipherSuites() {
      return this.wrappedSocketFactory.getDefaultCipherSuites();
   }

   public String[] getSupportedCipherSuites() {
      return this.wrappedSocketFactory.getSupportedCipherSuites();
   }
}
