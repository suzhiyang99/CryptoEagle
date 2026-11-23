package org.spark_project.jetty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.Iterator;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ProxyConnectionFactory extends AbstractConnectionFactory {
   private static final Logger LOG = Log.getLogger(ProxyConnectionFactory.class);
   private final String _next;

   public ProxyConnectionFactory() {
      super("proxy");
      this._next = null;
   }

   public ProxyConnectionFactory(String nextProtocol) {
      super("proxy");
      this._next = nextProtocol;
   }

   public Connection newConnection(Connector connector, EndPoint endp) {
      String next = this._next;
      if (next == null) {
         Iterator<String> i = connector.getProtocols().iterator();

         while(i.hasNext()) {
            String p = (String)i.next();
            if (this.getProtocol().equalsIgnoreCase(p)) {
               next = (String)i.next();
               break;
            }
         }
      }

      return new ProxyConnection(endp, connector, next);
   }

   public static class ProxyConnection extends AbstractConnection {
      private final int[] __size = new int[]{29, 23, 21, 13, 5, 3, 1};
      private final Connector _connector;
      private final String _next;
      private final StringBuilder _builder = new StringBuilder();
      private final String[] _field = new String[6];
      private int _fields;
      private int _length;

      protected ProxyConnection(EndPoint endp, Connector connector, String next) {
         super(endp, connector.getExecutor());
         this._connector = connector;
         this._next = next;
      }

      public void onOpen() {
         super.onOpen();
         this.fillInterested();
      }

      public void onFillable() {
         try {
            ByteBuffer buffer = null;

            while(true) {
               int size = Math.max(1, this.__size[this._fields] - this._builder.length());
               if (buffer != null && buffer.capacity() == size) {
                  BufferUtil.clear(buffer);
               } else {
                  buffer = BufferUtil.allocate(size);
               }

               int fill = this.getEndPoint().fill(buffer);
               if (fill < 0) {
                  this.getEndPoint().shutdownOutput();
                  return;
               }

               if (fill == 0) {
                  this.fillInterested();
                  return;
               }

               this._length += fill;
               if (this._length >= 108) {
                  ProxyConnectionFactory.LOG.warn("PROXY line too long {} for {}", this._length, this.getEndPoint());
                  this.close();
                  return;
               }

               while(buffer.hasRemaining()) {
                  byte b = buffer.get();
                  if (this._fields >= 6) {
                     if (b != 10) {
                        ProxyConnectionFactory.LOG.warn("Bad CRLF for {}", this.getEndPoint());
                        this.close();
                        return;
                     }

                     if (!"PROXY".equals(this._field[0])) {
                        ProxyConnectionFactory.LOG.warn("Not PROXY protocol for {}", this.getEndPoint());
                        this.close();
                        return;
                     }

                     InetSocketAddress remote = new InetSocketAddress(this._field[2], Integer.parseInt(this._field[4]));
                     InetSocketAddress local = new InetSocketAddress(this._field[3], Integer.parseInt(this._field[5]));
                     ConnectionFactory connectionFactory = this._connector.getConnectionFactory(this._next);
                     if (connectionFactory == null) {
                        ProxyConnectionFactory.LOG.info("Next protocol '{}' for {}", this._next, this.getEndPoint());
                        this.close();
                        return;
                     }

                     EndPoint endPoint = new ProxyEndPoint(this.getEndPoint(), remote, local);
                     Connection newConnection = connectionFactory.newConnection(this._connector, endPoint);
                     endPoint.upgrade(newConnection);
                     return;
                  }

                  if (b != 32 && (b != 13 || this._fields != 5)) {
                     if (b < 32) {
                        ProxyConnectionFactory.LOG.warn("Bad character {} for {}", b & 255, this.getEndPoint());
                        this.close();
                        return;
                     }

                     this._builder.append((char)b);
                  } else {
                     this._field[this._fields++] = this._builder.toString();
                     this._builder.setLength(0);
                  }
               }
            }
         } catch (Throwable x) {
            ProxyConnectionFactory.LOG.warn("PROXY error for " + this.getEndPoint(), x);
            this.close();
         }
      }
   }

   public static class ProxyEndPoint implements EndPoint {
      private final EndPoint _endp;
      private final InetSocketAddress _remote;
      private final InetSocketAddress _local;

      public ProxyEndPoint(EndPoint endp, InetSocketAddress remote, InetSocketAddress local) {
         this._endp = endp;
         this._remote = remote;
         this._local = local;
      }

      public boolean isOptimizedForDirectBuffers() {
         return this._endp.isOptimizedForDirectBuffers();
      }

      public InetSocketAddress getLocalAddress() {
         return this._local;
      }

      public InetSocketAddress getRemoteAddress() {
         return this._remote;
      }

      public boolean isOpen() {
         return this._endp.isOpen();
      }

      public long getCreatedTimeStamp() {
         return this._endp.getCreatedTimeStamp();
      }

      public void shutdownOutput() {
         this._endp.shutdownOutput();
      }

      public boolean isOutputShutdown() {
         return this._endp.isOutputShutdown();
      }

      public boolean isInputShutdown() {
         return this._endp.isInputShutdown();
      }

      public void close() {
         this._endp.close();
      }

      public int fill(ByteBuffer buffer) throws IOException {
         return this._endp.fill(buffer);
      }

      public boolean flush(ByteBuffer... buffer) throws IOException {
         return this._endp.flush(buffer);
      }

      public Object getTransport() {
         return this._endp.getTransport();
      }

      public long getIdleTimeout() {
         return this._endp.getIdleTimeout();
      }

      public void setIdleTimeout(long idleTimeout) {
         this._endp.setIdleTimeout(idleTimeout);
      }

      public void fillInterested(Callback callback) throws ReadPendingException {
         this._endp.fillInterested(callback);
      }

      public boolean tryFillInterested(Callback callback) {
         return this._endp.tryFillInterested(callback);
      }

      public boolean isFillInterested() {
         return this._endp.isFillInterested();
      }

      public void write(Callback callback, ByteBuffer... buffers) throws WritePendingException {
         this._endp.write(callback, buffers);
      }

      public Connection getConnection() {
         return this._endp.getConnection();
      }

      public void setConnection(Connection connection) {
         this._endp.setConnection(connection);
      }

      public void onOpen() {
         this._endp.onOpen();
      }

      public void onClose() {
         this._endp.onClose();
      }

      public void upgrade(Connection newConnection) {
         this._endp.upgrade(newConnection);
      }
   }
}
