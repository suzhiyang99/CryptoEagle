package org.apache.openejb.resource.activemq.jms2;

import java.util.Enumeration;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;
import org.apache.activemq.command.ActiveMQMapMessage;

public class WrappingMapMessage extends DelegateMessage implements MapMessage {
   private final MapMessage message;

   public WrappingMapMessage(MapMessage message) {
      super(message);
      this.message = message;
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return c == Map.class || Object.class == c;
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      if (this.isBodyAssignableTo(c)) {
         return (T)c.cast(ActiveMQMapMessage.class.isInstance(this.message) ? ((ActiveMQMapMessage)ActiveMQMapMessage.class.cast(this.message)).getContentMap() : this.message);
      } else {
         throw new MessageFormatException("Can't get the body with type " + c);
      }
   }

   public boolean getBoolean(String name) throws JMSException {
      return this.message.getBoolean(name);
   }

   public byte getByte(String name) throws JMSException {
      return this.message.getByte(name);
   }

   public short getShort(String name) throws JMSException {
      return this.message.getShort(name);
   }

   public char getChar(String name) throws JMSException {
      return this.message.getChar(name);
   }

   public int getInt(String name) throws JMSException {
      return this.message.getInt(name);
   }

   public long getLong(String name) throws JMSException {
      return this.message.getLong(name);
   }

   public float getFloat(String name) throws JMSException {
      return this.message.getFloat(name);
   }

   public double getDouble(String name) throws JMSException {
      return this.message.getDouble(name);
   }

   public String getString(String name) throws JMSException {
      return this.message.getString(name);
   }

   public byte[] getBytes(String name) throws JMSException {
      return this.message.getBytes(name);
   }

   public Object getObject(String name) throws JMSException {
      return this.message.getObject(name);
   }

   public Enumeration getMapNames() throws JMSException {
      return this.message.getMapNames();
   }

   public void setBoolean(String name, boolean value) throws JMSException {
      this.message.setBoolean(name, value);
   }

   public void setByte(String name, byte value) throws JMSException {
      this.message.setByte(name, value);
   }

   public void setShort(String name, short value) throws JMSException {
      this.message.setShort(name, value);
   }

   public void setChar(String name, char value) throws JMSException {
      this.message.setChar(name, value);
   }

   public void setInt(String name, int value) throws JMSException {
      this.message.setInt(name, value);
   }

   public void setLong(String name, long value) throws JMSException {
      this.message.setLong(name, value);
   }

   public void setFloat(String name, float value) throws JMSException {
      this.message.setFloat(name, value);
   }

   public void setDouble(String name, double value) throws JMSException {
      this.message.setDouble(name, value);
   }

   public void setString(String name, String value) throws JMSException {
      this.message.setString(name, value);
   }

   public void setBytes(String name, byte[] value) throws JMSException {
      this.message.setBytes(name, value);
   }

   public void setBytes(String name, byte[] value, int offset, int length) throws JMSException {
      this.message.setBytes(name, value, offset, length);
   }

   public void setObject(String name, Object value) throws JMSException {
      this.message.setObject(name, value);
   }

   public boolean itemExists(String name) throws JMSException {
      return this.message.itemExists(name);
   }
}
