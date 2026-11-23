package org.apache.openejb.resource.activemq.jms2;

import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.IllegalStateRuntimeException;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.XAConnection;

public class JMSContextImpl implements JMSContext {
   private final int sessionMode;
   private final String username;
   private final String password;
   private final ConnectionFactory factory;
   private Session session;
   private boolean autoStart = true;
   private MessageProducer innerProducer;
   private boolean xa;
   private boolean closed;
   private Connection connection;
   private volatile Message lastMessagesWaitingAck;

   public JMSContextImpl(ConnectionFactory factory, int sessionMode, String user, String pwd, boolean xa) {
      this.factory = factory;
      this.sessionMode = sessionMode;
      this.username = user;
      this.password = pwd;
      this.xa = xa;
   }

   Message setLastMessage(Message lastMessageReceived) {
      if (this.sessionMode == 2) {
         this.lastMessagesWaitingAck = lastMessageReceived;
      }

      return lastMessageReceived;
   }

   protected Connection connection() {
      if (this.connection == null) {
         try {
            this.connection = this.username != null ? this.factory.createConnection(this.username, this.password) : this.factory.createConnection();
            this.xa = XAConnection.class.isInstance(this.connection);
         } catch (JMSException e) {
            throw JMS2.toRuntimeException(e);
         }
      }

      return this.connection;
   }

   protected Session session() {
      if (this.session == null) {
         synchronized(this) {
            if (this.closed) {
               throw new IllegalStateRuntimeException("Context is closed");
            }

            if (this.session == null) {
               try {
                  if (this.xa) {
                     this.session = ((XAConnection)XAConnection.class.cast(this.connection())).createXASession();
                  } else {
                     this.session = this.connection().createSession(this.sessionMode);
                  }
               } catch (JMSException e) {
                  throw JMS2.toRuntimeException(e);
               }
            }
         }
      }

      return this.session;
   }

   private synchronized void checkAutoStart() throws JMSException {
      if (this.closed) {
         throw new IllegalStateRuntimeException("Context is closed");
      } else {
         if (this.autoStart) {
            this.connection.start();
         }

      }
   }

   private synchronized MessageProducer getInnerProducer() throws JMSException {
      if (this.innerProducer == null) {
         this.innerProducer = this.session().createProducer((Destination)null);
      }

      return this.innerProducer;
   }

   public void acknowledge() {
      this.session();

      try {
         if (this.lastMessagesWaitingAck != null) {
            this.lastMessagesWaitingAck.acknowledge();
         }

      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void close() {
      try {
         synchronized(this) {
            if (this.session != null) {
               this.session.close();
            }

            if (this.connection != null) {
               this.connection.close();
            }

            this.closed = true;
         }
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void commit() {
      try {
         this.session().commit();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public QueueBrowser createBrowser(Queue queue) {
      try {
         QueueBrowser browser = this.session().createBrowser(queue);
         this.checkAutoStart();
         return browser;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public QueueBrowser createBrowser(Queue queue, String messageSelector) {
      try {
         QueueBrowser browser = this.session().createBrowser(queue, messageSelector);
         this.checkAutoStart();
         return browser;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public BytesMessage createBytesMessage() {
      try {
         return (BytesMessage)JMS2.wrap(this.session().createBytesMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createConsumer(Destination destination) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createConsumer(destination));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createConsumer(Destination destination, String messageSelector) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createConsumer(destination, messageSelector));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createConsumer(destination, messageSelector, noLocal));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSContext createContext(int sessionMode) {
      if (this.xa) {
         throw new JMSRuntimeException("Illegal call to createContext");
      } else {
         return this.factory.createContext(sessionMode);
      }
   }

   public JMSConsumer createDurableConsumer(Topic topic, String name) {
      try {
         MessageConsumer delegate = this.session().createDurableSubscriber(topic, name);
         this.checkAutoStart();
         return new JMSConsumerImpl(this, delegate);
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
      try {
         MessageConsumer delegate = this.session().createDurableSubscriber(topic, name, messageSelector, noLocal);
         this.checkAutoStart();
         return new JMSConsumerImpl(this, delegate);
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public MapMessage createMapMessage() {
      try {
         return (MapMessage)JMS2.wrap(this.session().createMapMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Message createMessage() {
      try {
         return JMS2.wrap(this.session().createMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public ObjectMessage createObjectMessage() {
      try {
         return (ObjectMessage)JMS2.wrap(this.session().createObjectMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public ObjectMessage createObjectMessage(Serializable object) {
      try {
         return (ObjectMessage)JMS2.wrap(this.session().createObjectMessage(object));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSProducer createProducer() {
      try {
         return new JMSProducerImpl(this, this.getInnerProducer());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Queue createQueue(String queueName) {
      try {
         return this.session().createQueue(queueName);
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createSharedConsumer(topic, sharedSubscriptionName));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createSharedConsumer(topic, sharedSubscriptionName, messageSelector));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createSharedDurableConsumer(topic, name));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
      try {
         JMSConsumerImpl consumer = new JMSConsumerImpl(this, this.session().createSharedDurableConsumer(topic, name, messageSelector));
         this.checkAutoStart();
         return consumer;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public StreamMessage createStreamMessage() {
      try {
         return (StreamMessage)JMS2.wrap(this.session().createStreamMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public TemporaryQueue createTemporaryQueue() {
      try {
         return this.session().createTemporaryQueue();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public TemporaryTopic createTemporaryTopic() {
      try {
         return this.session().createTemporaryTopic();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public TextMessage createTextMessage() {
      try {
         return (TextMessage)JMS2.wrap(this.session().createTextMessage());
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public TextMessage createTextMessage(String text) {
      try {
         return (TextMessage)JMS2.wrap(this.session().createTextMessage(text));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Topic createTopic(String topicName) {
      try {
         return this.session().createTopic(topicName);
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public boolean getAutoStart() {
      return this.autoStart;
   }

   public String getClientID() {
      try {
         return this.connection().getClientID();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public ExceptionListener getExceptionListener() {
      try {
         return this.connection().getExceptionListener();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public ConnectionMetaData getMetaData() {
      try {
         return this.connection().getMetaData();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public int getSessionMode() {
      return this.sessionMode;
   }

   public boolean getTransacted() {
      try {
         return this.session().getTransacted();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void recover() {
      try {
         this.session().recover();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void rollback() {
      this.session();

      try {
         this.session().rollback();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void setAutoStart(boolean autoStart) {
      this.autoStart = autoStart;
   }

   public void setClientID(String clientID) {
      if (this.xa) {
         throw new JMSRuntimeException("Illegal call to setClientID");
      } else {
         try {
            this.connection().setClientID(clientID);
         } catch (JMSException e) {
            throw JMS2.toRuntimeException(e);
         }
      }
   }

   public void setExceptionListener(ExceptionListener listener) {
      if (this.xa) {
         throw new JMSRuntimeException("Illegal call to setExceptionListener");
      } else {
         try {
            this.connection().setExceptionListener(listener);
         } catch (JMSException e) {
            throw JMS2.toRuntimeException(e);
         }
      }
   }

   public void start() {
      try {
         this.connection().start();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void stop() {
      if (this.xa) {
         throw new JMSRuntimeException("Illegal call to stop");
      } else {
         try {
            this.connection().stop();
         } catch (JMSException e) {
            throw JMS2.toRuntimeException(e);
         }
      }
   }

   public void unsubscribe(String name) {
      try {
         this.session().unsubscribe(name);
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }
}
