package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class JMSConsumerImpl implements JMSConsumer {
   private final JMSContextImpl context;
   private final MessageConsumer consumer;

   public JMSConsumerImpl(JMSContextImpl jmsContext, MessageConsumer consumer) {
      this.context = jmsContext;
      this.consumer = consumer;
   }

   public String getMessageSelector() {
      try {
         return this.consumer.getMessageSelector();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public MessageListener getMessageListener() throws JMSRuntimeException {
      try {
         return this.consumer.getMessageListener();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void setMessageListener(MessageListener listener) throws JMSRuntimeException {
      try {
         this.consumer.setMessageListener(new ContextUpdaterMessageListenerWrapper(this.context, listener));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Message receive() {
      try {
         return this.context.setLastMessage(wrap(this.consumer.receive()));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Message receive(long timeout) {
      try {
         return this.context.setLastMessage(wrap(this.consumer.receive(timeout)));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public Message receiveNoWait() {
      try {
         return this.context.setLastMessage(wrap(this.consumer.receiveNoWait()));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public void close() {
      try {
         this.consumer.close();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public <T> T receiveBody(Class<T> c) {
      try {
         Message message = wrap(this.consumer.receive());
         this.context.setLastMessage(message);
         return (T)(message == null ? null : message.getBody(c));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public <T> T receiveBody(Class<T> c, long timeout) {
      try {
         Message message = wrap(this.consumer.receive(timeout));
         this.context.setLastMessage(message);
         return (T)(message == null ? null : message.getBody(c));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public <T> T receiveBodyNoWait(Class<T> c) {
      try {
         Message message = wrap(this.consumer.receiveNoWait());
         this.context.setLastMessage(message);
         return (T)(message == null ? null : message.getBody(c));
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   private static Message wrap(Message message) {
      if (message == null) {
         return null;
      } else {
         Message wrapped = JMS2.wrap(message);

         try {
            wrapped.setJMSDeliveryTime(System.currentTimeMillis());
         } catch (JMSException var3) {
         }

         return wrapped;
      }
   }

   private static final class ContextUpdaterMessageListenerWrapper implements MessageListener {
      private final JMSContextImpl context;
      private final MessageListener wrapped;

      private ContextUpdaterMessageListenerWrapper(JMSContextImpl context, MessageListener wrapped) {
         this.context = context;
         this.wrapped = wrapped;
      }

      public void onMessage(Message message) {
         Message wrappedMessage = JMSConsumerImpl.wrap(message);
         this.context.setLastMessage(wrappedMessage);
         this.wrapped.onMessage(wrappedMessage);
      }
   }
}
