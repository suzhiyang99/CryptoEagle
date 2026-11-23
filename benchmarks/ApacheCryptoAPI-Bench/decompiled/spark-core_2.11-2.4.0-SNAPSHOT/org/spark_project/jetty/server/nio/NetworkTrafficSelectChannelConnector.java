package org.spark_project.jetty.server.nio;

import java.util.concurrent.Executor;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.server.ConnectionFactory;
import org.spark_project.jetty.server.NetworkTrafficServerConnector;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.Scheduler;

/** @deprecated */
@Deprecated
public class NetworkTrafficSelectChannelConnector extends NetworkTrafficServerConnector {
   public NetworkTrafficSelectChannelConnector(Server server) {
      super(server);
   }

   public NetworkTrafficSelectChannelConnector(Server server, ConnectionFactory connectionFactory, SslContextFactory sslContextFactory) {
      super(server, connectionFactory, sslContextFactory);
   }

   public NetworkTrafficSelectChannelConnector(Server server, ConnectionFactory connectionFactory) {
      super(server, connectionFactory);
   }

   public NetworkTrafficSelectChannelConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool pool, int acceptors, int selectors, ConnectionFactory... factories) {
      super(server, executor, scheduler, pool, acceptors, selectors, factories);
   }

   public NetworkTrafficSelectChannelConnector(Server server, SslContextFactory sslContextFactory) {
      super(server, sslContextFactory);
   }
}
