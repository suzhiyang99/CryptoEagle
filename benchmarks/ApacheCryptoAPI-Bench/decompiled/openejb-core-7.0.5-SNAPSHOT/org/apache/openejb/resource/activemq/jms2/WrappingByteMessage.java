package org.apache.openejb.resource.activemq.jms2;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQBytesMessage;

public class WrappingByteMessage extends DelegateMessage implements BytesMessage {
   private final BytesMessage message;

   public WrappingByteMessage(BytesMessage message) {
      super(message);
      this.message = message;
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return byte[].class == c;
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      int len = (int)this.getBodyLength();
      if (len == 0) {
         return null;
      } else {
         byte[] dst = new byte[len];
         if (ActiveMQBytesMessage.class.isInstance(this.message)) {
            System.arraycopy(((ActiveMQBytesMessage)ActiveMQBytesMessage.class.cast(this.message)).getContent().getData(), 0, dst, 0, len);
         }

         return (T)c.cast(dst);
      }
   }

   public long getBodyLength() throws JMSException {
      return this.message.getBodyLength();
   }

   public boolean readBoolean() throws JMSException {
      return this.message.readBoolean();
   }

   public byte readByte() throws JMSException {
      return this.message.readByte();
   }

   public int readUnsignedByte() throws JMSException {
      return this.message.readUnsignedByte();
   }

   public short readShort() throws JMSException {
      return this.message.readShort();
   }

   public int readUnsignedShort() throws JMSException {
      return this.message.readUnsignedShort();
   }

   public char readChar() throws JMSException {
      return this.message.readChar();
   }

   public int readInt() throws JMSException {
      return this.message.readInt();
   }

   public long readLong() throws JMSException {
      return this.message.readLong();
   }

   public float readFloat() throws JMSException {
      return this.message.readFloat();
   }

   public double readDouble() throws JMSException {
      return this.message.readDouble();
   }

   public String readUTF() throws JMSException {
      return this.message.readUTF();
   }

   public int readBytes(byte[] value) throws JMSException {
      return this.message.readBytes(value);
   }

   public int readBytes(byte[] value, int length) throws JMSException {
      return this.message.readBytes(value, length);
   }

   public void writeBoolean(boolean value) throws JMSException {
      this.message.writeBoolean(value);
   }

   public void writeByte(byte value) throws JMSException {
      this.message.writeByte(value);
   }

   public void writeShort(short value) throws JMSException {
      this.message.writeShort(value);
   }

   public void writeChar(char value) throws JMSException {
      this.message.writeChar(value);
   }

   public void writeInt(int value) throws JMSException {
      this.message.writeInt(value);
   }

   public void writeLong(long value) throws JMSException {
      this.message.writeLong(value);
   }

   public void writeFloat(float value) throws JMSException {
      this.message.writeFloat(value);
   }

   public void writeDouble(double value) throws JMSException {
      this.message.writeDouble(value);
   }

   public void writeUTF(String value) throws JMSException {
      this.message.writeUTF(value);
   }

   public void writeBytes(byte[] value) throws JMSException {
      this.message.writeBytes(value);
   }

   public void writeBytes(byte[] value, int offset, int length) throws JMSException {
      this.message.writeBytes(value, offset, length);
   }

   public void writeObject(Object value) throws JMSException {
      this.message.writeObject(value);
   }

   public void reset() throws JMSException {
      this.message.reset();
   }
}
