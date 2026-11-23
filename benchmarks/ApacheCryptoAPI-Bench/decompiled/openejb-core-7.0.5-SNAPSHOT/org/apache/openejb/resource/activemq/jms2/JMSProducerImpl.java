package org.apache.openejb.resource.activemq.jms2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jms.BytesMessage;
import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageFormatRuntimeException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import org.apache.xbean.propertyeditor.PropertyEditorException;
import org.apache.xbean.propertyeditor.PropertyEditors;

class JMSProducerImpl implements JMSProducer {
   private final JMSContextImpl context;
   private final MessageProducer producer;
   private final Map<String, Object> properties = new HashMap();
   private volatile CompletionListener completionListener;
   private Destination jmsHeaderReplyTo;
   private String jmsHeaderCorrelationID;
   private byte[] jmsHeaderCorrelationIDAsBytes;
   private String jmsHeaderType;

   JMSProducerImpl(JMSContextImpl jmsContext, MessageProducer innerProducer) {
      this.context = jmsContext;
      this.producer = innerProducer;
   }

   private <T> T getProperty(String key, Class<T> type) {
      Object val = this.properties.get(key);
      if (val != null && !type.isInstance(val)) {
         try {
            return (T)type.cast(PropertyEditors.getValue(type, val.toString()));
         } catch (PropertyEditorException pee) {
            throw new MessageFormatRuntimeException(pee.getMessage());
         }
      } else {
         return (T)type.cast(val);
      }
   }

   public JMSProducer send(Destination destination, Message message) {
      if (message == null) {
         throw new MessageFormatRuntimeException("null message");
      } else {
         try {
            if (this.jmsHeaderCorrelationID != null) {
               message.setJMSCorrelationID(this.jmsHeaderCorrelationID);
            }

            if (this.jmsHeaderCorrelationIDAsBytes != null && this.jmsHeaderCorrelationIDAsBytes.length > 0) {
               message.setJMSCorrelationIDAsBytes(this.jmsHeaderCorrelationIDAsBytes);
            }

            if (this.jmsHeaderReplyTo != null) {
               message.setJMSReplyTo(this.jmsHeaderReplyTo);
            }

            if (this.jmsHeaderType != null) {
               message.setJMSType(this.jmsHeaderType);
            }

            this.setProperties(message);
            if (this.completionListener != null) {
               this.producer.send(destination, message, this.completionListener);
            } else {
               this.producer.send(destination, message);
            }

            return this;
         } catch (JMSException e) {
            throw JMS2.toRuntimeException(e);
         }
      }
   }

   private void setProperties(Message message) throws JMSException {
      for(Map.Entry<String, Object> entry : this.properties.entrySet()) {
         message.setObjectProperty((String)entry.getKey(), entry.getValue());
      }

   }

   public JMSProducer send(Destination destination, String body) {
      this.send(destination, JMS2.wrap(this.context.createTextMessage(body)));
      return this;
   }

   public JMSProducer send(Destination destination, Map<String, Object> body) {
      MapMessage message = (MapMessage)JMS2.wrap(this.context.createMapMessage());
      if (body != null) {
         try {
            for(Map.Entry<String, Object> entry : body.entrySet()) {
               String name = (String)entry.getKey();
               Object v = entry.getValue();
               if (v instanceof String) {
                  message.setString(name, (String)v);
               } else if (v instanceof Long) {
                  message.setLong(name, (Long)v);
               } else if (v instanceof Double) {
                  message.setDouble(name, (Double)v);
               } else if (v instanceof Integer) {
                  message.setInt(name, (Integer)v);
               } else if (v instanceof Character) {
                  message.setChar(name, (Character)v);
               } else if (v instanceof Short) {
                  message.setShort(name, (Short)v);
               } else if (v instanceof Boolean) {
                  message.setBoolean(name, (Boolean)v);
               } else if (v instanceof Float) {
                  message.setFloat(name, (Float)v);
               } else if (v instanceof Byte) {
                  message.setByte(name, (Byte)v);
               } else if (v instanceof byte[]) {
                  byte[] array = (byte[])v;
                  message.setBytes(name, array, 0, array.length);
               } else {
                  message.setObject(name, v);
               }
            }
         } catch (JMSException e) {
            throw new MessageFormatRuntimeException(e.getMessage());
         }
      }

      this.send(destination, (Message)message);
      return this;
   }

   public JMSProducer send(Destination destination, byte[] body) {
      BytesMessage message = (BytesMessage)JMS2.wrap(this.context.createBytesMessage());
      if (body != null) {
         try {
            message.writeBytes(body);
         } catch (JMSException e) {
            throw new MessageFormatRuntimeException(e.getMessage());
         }
      }

      this.send(destination, (Message)message);
      return this;
   }

   public JMSProducer send(Destination destination, Serializable body) {
      ObjectMessage message = (ObjectMessage)JMS2.wrap(this.context.createObjectMessage(body));
      this.send(destination, (Message)message);
      return this;
   }

   public JMSProducer setDisableMessageID(boolean value) {
      try {
         this.producer.setDisableMessageID(value);
         return this;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public boolean getDisableMessageID() {
      try {
         return this.producer.getDisableMessageID();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSProducer setDisableMessageTimestamp(boolean value) {
      try {
         this.producer.setDisableMessageTimestamp(value);
         return this;
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public boolean getDisableMessageTimestamp() {
      try {
         return this.producer.getDisableMessageTimestamp();
      } catch (JMSException e) {
         throw JMS2.toRuntimeException(e);
      }
   }

   public JMSProducer setDeliveryMode(int deliveryMode) {
      try {
         this.producer.setDeliveryMode(deliveryMode);
         return this;
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public int getDeliveryMode() {
      try {
         return this.producer.getDeliveryMode();
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public JMSProducer setPriority(int priority) {
      try {
         this.producer.setPriority(priority);
         return this;
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public int getPriority() {
      try {
         return this.producer.getPriority();
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public JMSProducer setTimeToLive(long timeToLive) {
      try {
         this.producer.setTimeToLive(timeToLive);
         return this;
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public long getTimeToLive() {
      try {
         return this.producer.getTimeToLive();
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public JMSProducer setDeliveryDelay(long deliveryDelay) {
      try {
         this.producer.setDeliveryDelay(deliveryDelay);
         return this;
      } catch (JMSException e) {
         JMSRuntimeException e2 = new JMSRuntimeException(e.getMessage());
         e2.initCause(e);
         throw e2;
      }
   }

   public long getDeliveryDelay() {
      try {
         return this.producer.getDeliveryDelay();
      } catch (Exception var2) {
         return 0L;
      }
   }

   public JMSProducer setAsync(CompletionListener completionListener) {
      this.completionListener = completionListener;
      return this;
   }

   public CompletionListener getAsync() {
      return this.completionListener;
   }

   public JMSProducer setProperty(String name, boolean value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, byte value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, short value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, int value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, long value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, float value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, double value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer setProperty(String name, String value) {
      this.validName(name);
      this.properties.put(name, value);
      return this;
   }

   public JMSProducer clearProperties() {
      this.properties.clear();
      return this;
   }

   public boolean propertyExists(String name) {
      return this.properties.containsKey(name);
   }

   public JMSProducer setProperty(String name, Object value) {
      this.validName(name);
      if (value != null && !Boolean.class.isInstance(value) && !Byte.class.isInstance(value) && !Character.class.isInstance(value) && !Short.class.isInstance(value) && !Integer.class.isInstance(value) && !Long.class.isInstance(value) && !Float.class.isInstance(value) && !Double.class.isInstance(value) && !String.class.isInstance(value) && !byte[].class.isInstance(value)) {
         throw new MessageFormatRuntimeException("Unsupported type: " + value);
      } else {
         this.properties.put(name, value);
         return this;
      }
   }

   public boolean getBooleanProperty(String name) {
      return (Boolean)this.getProperty(name, Boolean.class);
   }

   public byte getByteProperty(String name) {
      return (Byte)this.getProperty(name, Byte.class);
   }

   public short getShortProperty(String name) {
      return (Short)this.getProperty(name, Short.class);
   }

   public int getIntProperty(String name) {
      return (Integer)this.getProperty(name, Integer.class);
   }

   public long getLongProperty(String name) {
      return (Long)this.getProperty(name, Long.class);
   }

   public float getFloatProperty(String name) {
      return (Float)this.getProperty(name, Float.class);
   }

   public double getDoubleProperty(String name) {
      return (Double)this.getProperty(name, Double.class);
   }

   public String getStringProperty(String name) {
      return (String)this.getProperty(name, String.class);
   }

   public Object getObjectProperty(String name) {
      return this.getProperty(name, Object.class);
   }

   public Set<String> getPropertyNames() {
      return new HashSet(this.properties.keySet());
   }

   public JMSProducer setJMSCorrelationIDAsBytes(byte[] correlationID) {
      if (correlationID != null && correlationID.length != 0) {
         this.jmsHeaderCorrelationIDAsBytes = Arrays.copyOf(correlationID, correlationID.length);
         return this;
      } else {
         throw new JMSRuntimeException("Please specify a non-zero length byte[]");
      }
   }

   public byte[] getJMSCorrelationIDAsBytes() {
      return Arrays.copyOf(this.jmsHeaderCorrelationIDAsBytes, this.jmsHeaderCorrelationIDAsBytes.length);
   }

   public JMSProducer setJMSCorrelationID(String correlationID) {
      this.jmsHeaderCorrelationID = correlationID;
      return this;
   }

   public String getJMSCorrelationID() {
      return this.jmsHeaderCorrelationID;
   }

   public JMSProducer setJMSType(String type) {
      this.jmsHeaderType = type;
      return this;
   }

   public String getJMSType() {
      return this.jmsHeaderType;
   }

   public JMSProducer setJMSReplyTo(Destination replyTo) {
      this.jmsHeaderReplyTo = replyTo;
      return this;
   }

   public Destination getJMSReplyTo() {
      return this.jmsHeaderReplyTo;
   }

   private void validName(String name) {
      if (name == null || name.isEmpty()) {
         throw new IllegalArgumentException("name can't be blank");
      }
   }
}
