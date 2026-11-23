package org.spark_project.jetty.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.ArrayUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.ssl.SslContextFactory;

@ManagedObject
public abstract class AbstractConnectionFactory extends ContainerLifeCycle implements ConnectionFactory {
   private final String _protocol;
   private final List<String> _protocols;
   private int _inputbufferSize = 8192;

   protected AbstractConnectionFactory(String protocol) {
      this._protocol = protocol;
      this._protocols = Collections.unmodifiableList(Arrays.asList(protocol));
   }

   protected AbstractConnectionFactory(String... protocols) {
      this._protocol = protocols[0];
      this._protocols = Collections.unmodifiableList(Arrays.asList(protocols));
   }

   @ManagedAttribute(
      value = "The protocol name",
      readonly = true
   )
   public String getProtocol() {
      return this._protocol;
   }

   public List<String> getProtocols() {
      return this._protocols;
   }

   @ManagedAttribute("The buffer size used to read from the network")
   public int getInputBufferSize() {
      return this._inputbufferSize;
   }

   public void setInputBufferSize(int size) {
      this._inputbufferSize = size;
   }

   protected AbstractConnection configure(AbstractConnection connection, Connector connector, EndPoint endPoint) {
      connection.setInputBufferSize(this.getInputBufferSize());
      if (connector instanceof ContainerLifeCycle) {
         ContainerLifeCycle aggregate = (ContainerLifeCycle)connector;

         for(Connection.Listener listener : aggregate.getBeans(Connection.Listener.class)) {
            connection.addListener(listener);
         }
      }

      for(Connection.Listener listener : this.getBeans(Connection.Listener.class)) {
         connection.addListener(listener);
      }

      return connection;
   }

   public String toString() {
      return String.format("%s@%x%s", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols());
   }

   public static ConnectionFactory[] getFactories(SslContextFactory sslContextFactory, ConnectionFactory... factories) {
      factories = (ConnectionFactory[])ArrayUtil.removeNulls(factories);
      if (sslContextFactory == null) {
         return factories;
      } else {
         for(ConnectionFactory factory : factories) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
               HttpConfiguration config = ((HttpConfiguration.ConnectionFactory)factory).getHttpConfiguration();
               if (config.getCustomizer(SecureRequestCustomizer.class) == null) {
                  config.addCustomizer(new SecureRequestCustomizer());
               }
            }
         }

         return (ConnectionFactory[])ArrayUtil.prependToArray(new SslConnectionFactory(sslContextFactory, factories[0].getProtocol()), factories, ConnectionFactory.class);
      }
   }
}
