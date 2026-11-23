package org.apache.openejb.resource.activemq.jms2;

import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class DelegateMessage implements Message {
   private final Message message;
   private long deliveryTime;

   public DelegateMessage(Message message) {
      this.message = message;
   }

   public long getJMSDeliveryTime() throws JMSException {
      return this.deliveryTime;
   }

   public void setJMSDeliveryTime(long deliveryTime) throws JMSException {
      this.deliveryTime = deliveryTime;
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      return (T)this.message.getBody(c);
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return this.message.isBodyAssignableTo(c);
   }

   public String getJMSMessageID() throws JMSException {
      return this.message.getJMSMessageID();
   }

   public void setJMSMessageID(String id) throws JMSException {
      this.message.setJMSMessageID(id);
   }

   public long getJMSTimestamp() throws JMSException {
      return this.message.getJMSTimestamp();
   }

   public void setJMSTimestamp(long timestamp) throws JMSException {
      this.message.setJMSTimestamp(timestamp);
   }

   public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
      return this.message.getJMSCorrelationIDAsBytes();
   }

   public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
      this.message.setJMSCorrelationIDAsBytes(correlationID);
   }

   public void setJMSCorrelationID(String correlationID) throws JMSException {
      this.message.setJMSCorrelationID(correlationID);
   }

   public String getJMSCorrelationID() throws JMSException {
      return this.message.getJMSCorrelationID();
   }

   public Destination getJMSReplyTo() throws JMSException {
      return this.message.getJMSReplyTo();
   }

   public void setJMSReplyTo(Destination replyTo) throws JMSException {
      this.message.setJMSReplyTo(replyTo);
   }

   public Destination getJMSDestination() throws JMSException {
      return this.message.getJMSDestination();
   }

   public void setJMSDestination(Destination destination) throws JMSException {
      this.message.setJMSDestination(destination);
   }

   public int getJMSDeliveryMode() throws JMSException {
      return this.message.getJMSDeliveryMode();
   }

   public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
      this.message.setJMSDeliveryMode(deliveryMode);
   }

   public boolean getJMSRedelivered() throws JMSException {
      return this.message.getJMSRedelivered();
   }

   public void setJMSRedelivered(boolean redelivered) throws JMSException {
      this.message.setJMSRedelivered(redelivered);
   }

   public String getJMSType() throws JMSException {
      return this.message.getJMSType();
   }

   public void setJMSType(String type) throws JMSException {
      this.message.setJMSType(type);
   }

   public long getJMSExpiration() throws JMSException {
      return this.message.getJMSExpiration();
   }

   public void setJMSExpiration(long expiration) throws JMSException {
      this.message.setJMSExpiration(expiration);
   }

   public int getJMSPriority() throws JMSException {
      return this.message.getJMSPriority();
   }

   public void setJMSPriority(int priority) throws JMSException {
      this.message.setJMSPriority(priority);
   }

   public void clearProperties() throws JMSException {
      this.message.clearProperties();
   }

   public boolean propertyExists(String name) throws JMSException {
      return this.message.propertyExists(name);
   }

   public boolean getBooleanProperty(String name) throws JMSException {
      return this.message.getBooleanProperty(name);
   }

   public byte getByteProperty(String name) throws JMSException {
      return this.message.getByteProperty(name);
   }

   public short getShortProperty(String name) throws JMSException {
      return this.message.getShortProperty(name);
   }

   public int getIntProperty(String name) throws JMSException {
      return this.message.getIntProperty(name);
   }

   public long getLongProperty(String name) throws JMSException {
      return this.message.getLongProperty(name);
   }

   public float getFloatProperty(String name) throws JMSException {
      return this.message.getFloatProperty(name);
   }

   public double getDoubleProperty(String name) throws JMSException {
      return this.message.getDoubleProperty(name);
   }

   public String getStringProperty(String name) throws JMSException {
      return this.message.getStringProperty(name);
   }

   public Object getObjectProperty(String name) throws JMSException {
      return this.message.getObjectProperty(name);
   }

   public Enumeration getPropertyNames() throws JMSException {
      return this.message.getPropertyNames();
   }

   public void setBooleanProperty(String name, boolean value) throws JMSException {
      this.message.setBooleanProperty(name, value);
   }

   public void setByteProperty(String name, byte value) throws JMSException {
      this.message.setByteProperty(name, value);
   }

   public void setShortProperty(String name, short value) throws JMSException {
      this.message.setShortProperty(name, value);
   }

   public void setIntProperty(String name, int value) throws JMSException {
      this.message.setIntProperty(name, value);
   }

   public void setLongProperty(String name, long value) throws JMSException {
      this.message.setLongProperty(name, value);
   }

   public void setFloatProperty(String name, float value) throws JMSException {
      this.message.setFloatProperty(name, value);
   }

   public void setDoubleProperty(String name, double value) throws JMSException {
      this.message.setDoubleProperty(name, value);
   }

   public void setStringProperty(String name, String value) throws JMSException {
      this.message.setStringProperty(name, value);
   }

   public void setObjectProperty(String name, Object value) throws JMSException {
      this.message.setObjectProperty(name, value);
   }

   public void acknowledge() throws JMSException {
      this.message.acknowledge();
   }

   public void clearBody() throws JMSException {
      this.message.clearBody();
   }

   public Message unwrap() {
      return this.message;
   }
}
