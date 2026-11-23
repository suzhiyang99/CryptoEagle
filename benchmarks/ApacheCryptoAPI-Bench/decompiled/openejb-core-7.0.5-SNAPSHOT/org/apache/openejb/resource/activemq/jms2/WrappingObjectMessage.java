package org.apache.openejb.resource.activemq.jms2;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public class WrappingObjectMessage extends DelegateMessage implements ObjectMessage {
   private final ObjectMessage message;

   public WrappingObjectMessage(ObjectMessage message) {
      super(message);
      this.message = message;
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return c.isInstance(this.message.getObject());
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      return (T)c.cast(this.message.getObject());
   }

   public void setObject(Serializable object) throws JMSException {
      this.message.setObject(object);
   }

   public Serializable getObject() throws JMSException {
      return this.message.getObject();
   }
}
