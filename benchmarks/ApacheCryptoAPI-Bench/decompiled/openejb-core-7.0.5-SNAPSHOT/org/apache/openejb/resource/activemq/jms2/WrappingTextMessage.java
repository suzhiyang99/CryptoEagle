package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class WrappingTextMessage extends DelegateMessage implements TextMessage {
   private final TextMessage message;

   public WrappingTextMessage(TextMessage message) {
      super(message);
      this.message = message;
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return CharSequence.class.isAssignableFrom(c);
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      return (T)c.cast(this.message.getText());
   }

   public String getText() throws JMSException {
      return this.message.getText();
   }

   public void setText(String string) throws JMSException {
      this.message.setText(string);
   }
}
