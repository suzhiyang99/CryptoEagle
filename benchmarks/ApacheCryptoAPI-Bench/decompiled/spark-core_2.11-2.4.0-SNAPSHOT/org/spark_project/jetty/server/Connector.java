package org.spark_project.jetty.server;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.Graceful;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.thread.Scheduler;

@ManagedObject("Connector Interface")
public interface Connector extends LifeCycle, Graceful {
   Server getServer();

   Executor getExecutor();

   Scheduler getScheduler();

   ByteBufferPool getByteBufferPool();

   ConnectionFactory getConnectionFactory(String var1);

   <T> T getConnectionFactory(Class<T> var1);

   ConnectionFactory getDefaultConnectionFactory();

   Collection<ConnectionFactory> getConnectionFactories();

   List<String> getProtocols();

   @ManagedAttribute("maximum time a connection can be idle before being closed (in ms)")
   long getIdleTimeout();

   Object getTransport();

   Collection<EndPoint> getConnectedEndPoints();

   String getName();
}
