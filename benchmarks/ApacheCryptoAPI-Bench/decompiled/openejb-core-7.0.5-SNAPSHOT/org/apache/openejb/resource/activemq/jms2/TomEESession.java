package org.apache.openejb.resource.activemq.jms2;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageTransformation;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.CustomDestination;
import org.apache.activemq.command.SessionId;

public class TomEESession extends ActiveMQSession {
   public TomEESession(ActiveMQConnection connection, SessionId sessionId, int acknowledgeMode, boolean asyncDispatch, boolean sessionAsyncDispatch) throws JMSException {
      super(connection, sessionId, acknowledgeMode, asyncDispatch, sessionAsyncDispatch);
   }

   public MessageProducer createProducer(Destination destination) throws JMSException {
      this.checkClosed();
      return (MessageProducer)(CustomDestination.class.isInstance(destination) ? ((CustomDestination)CustomDestination.class.cast(destination)).createProducer(this) : new TomEEProducer(this, this.getNextProducerId(), ActiveMQMessageTransformation.transformDestination(destination), this.connection.getSendTimeout()));
   }

   public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException {
      return this.createDurableSubscriber(topic, name);
   }

   public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
      return this.createDurableSubscriber(topic, name, messageSelector, noLocal);
   }

   public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException {
      return this.createConsumer(topic);
   }

   public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) throws JMSException {
      return this.createConsumer(topic, messageSelector);
   }

   public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException {
      return this.createDurableSubscriber(topic, name);
   }

   public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException {
      return this.createDurableSubscriber(topic, name, messageSelector, false);
   }
}
