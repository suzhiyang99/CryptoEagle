package org.apache.openejb.resource.activemq.jms2;

import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.AsyncCallback;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerId;

public class TomEEProducer extends ActiveMQMessageProducer {
   private final ActiveMQDestination destination;
   private long deliveryDelay;

   public TomEEProducer(ActiveMQSession session, ProducerId producerId, ActiveMQDestination destination, int sendTimeout) throws JMSException {
      super(session, producerId, destination, sendTimeout);
      this.destination = destination;
   }

   public void send(Message message, CompletionListener completionListener) throws JMSException {
      super.send(this.destination, message, new ProducerAsyncCallback(message, completionListener));
   }

   public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException {
      super.send(destination, message, new ProducerAsyncCallback(message, completionListener));
   }

   public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
      super.send(this.destination, message, deliveryMode, priority, timeToLive, new ProducerAsyncCallback(message, completionListener));
   }

   public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
      super.send(destination, message, deliveryMode, priority, timeToLive, new ProducerAsyncCallback(message, completionListener));
   }

   public long getDeliveryDelay() throws JMSException {
      return this.deliveryDelay;
   }

   public void setDeliveryDelay(long deliveryDelay) throws JMSException {
      this.deliveryDelay = deliveryDelay;
   }

   private static final class ProducerAsyncCallback implements AsyncCallback {
      private final Message message;
      private final CompletionListener completionListener;

      private ProducerAsyncCallback(Message message, CompletionListener completionListener) {
         this.message = message;
         this.completionListener = completionListener;
      }

      public void onSuccess() {
         this.completionListener.onCompletion(this.message);
      }

      public void onException(JMSException exception) {
         this.completionListener.onException(this.message, exception);
      }
   }
}
