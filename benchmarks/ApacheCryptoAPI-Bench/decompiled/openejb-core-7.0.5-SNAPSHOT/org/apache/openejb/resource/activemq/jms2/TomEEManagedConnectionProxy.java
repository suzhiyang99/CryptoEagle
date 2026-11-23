package org.apache.openejb.resource.activemq.jms2;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.resource.spi.ConnectionRequestInfo;
import org.apache.activemq.ra.ActiveMQManagedConnection;
import org.apache.activemq.ra.ManagedConnectionProxy;

public class TomEEManagedConnectionProxy extends ManagedConnectionProxy implements Connection, QueueConnection, TopicConnection, ExceptionListener {
   private volatile ActiveMQManagedConnection connection;

   public TomEEManagedConnectionProxy(ActiveMQManagedConnection managedConnection, ConnectionRequestInfo info) {
      super(managedConnection, info);
      this.connection = managedConnection;
   }

   public void cleanup() {
      super.cleanup();
      this.connection = null;
   }

   public Session createSession(int sessionMode) throws JMSException {
      return this.connection.getPhysicalConnection().createSession(sessionMode);
   }

   public Session createSession() throws JMSException {
      return this.connection.getPhysicalConnection().createSession();
   }

   public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
      return this.connection.getPhysicalConnection().createSharedDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
   }

   public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
      return this.connection.getPhysicalConnection().createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
   }
}
