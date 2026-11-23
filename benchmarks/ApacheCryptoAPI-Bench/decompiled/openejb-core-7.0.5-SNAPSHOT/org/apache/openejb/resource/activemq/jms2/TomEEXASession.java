package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQXAConnection;
import org.apache.activemq.ActiveMQXASession;
import org.apache.activemq.command.SessionId;

public class TomEEXASession extends ActiveMQXASession {
   public TomEEXASession(ActiveMQXAConnection connection, SessionId sessionId, int theAcknowlegeMode, boolean dispatchAsync) throws JMSException {
      super(connection, sessionId, theAcknowlegeMode, dispatchAsync);
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
