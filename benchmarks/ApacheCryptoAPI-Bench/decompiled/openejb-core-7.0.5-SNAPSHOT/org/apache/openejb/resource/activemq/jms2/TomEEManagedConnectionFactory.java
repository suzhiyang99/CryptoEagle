package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ra.ActiveMQConnectionRequestInfo;
import org.apache.activemq.ra.ActiveMQManagedConnectionFactory;
import org.apache.activemq.ra.MessageActivationSpec;
import org.apache.activemq.ra.SimpleConnectionManager;

public class TomEEManagedConnectionFactory extends ActiveMQManagedConnectionFactory {
   public Object createConnectionFactory(ConnectionManager manager) throws ResourceException {
      return new TomEERAConnectionFactory(this, manager, this.getInfo());
   }

   public Object createConnectionFactory() throws ResourceException {
      return this.createConnectionFactory(new SimpleConnectionManager());
   }

   protected ActiveMQConnectionFactory createConnectionFactory(ActiveMQConnectionRequestInfo connectionRequestInfo, MessageActivationSpec activationSpec) {
      TomEEConnectionFactory connectionFactory = new TomEEConnectionFactory();
      connectionRequestInfo.configure(connectionFactory, activationSpec);
      return connectionFactory;
   }

   public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
      ActiveMQConnectionRequestInfo amqInfo;
      if (ActiveMQConnectionRequestInfo.class.isInstance(connectionRequestInfo)) {
         amqInfo = (ActiveMQConnectionRequestInfo)ActiveMQConnectionRequestInfo.class.cast(connectionRequestInfo);
      } else {
         amqInfo = this.getInfo();
      }

      try {
         return new TomEEManagedConnection(subject, this.makeConnection(amqInfo), amqInfo);
      } catch (JMSException e) {
         throw new ResourceException("Could not create connection.", e);
      }
   }

   public boolean equals(Object object) {
      return object != null && this.getClass().isInstance(object) && ((ActiveMQManagedConnectionFactory)object).getInfo().equals(this.getInfo());
   }
}
