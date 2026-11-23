package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.StreamMessage;

public class WrappingStreamMessage extends DelegateMessage implements StreamMessage {
   private final StreamMessage message;

   public WrappingStreamMessage(StreamMessage message) {
      super(message);
      this.message = message;
   }

   public <T> T getBody(Class<T> c) throws JMSException {
      throw new MessageFormatException("Can't getBody on a stream message");
   }

   public boolean isBodyAssignableTo(Class c) throws JMSException {
      return false;
   }

   public boolean readBoolean() throws JMSException {
      return this.message.readBoolean();
   }

   public byte readByte() throws JMSException {
      return this.message.readByte();
   }

   public short readShort() throws JMSException {
      return this.message.readShort();
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

   public String readString() throws JMSException {
      return this.message.readString();
   }

   public int readBytes(byte[] value) throws JMSException {
      return this.message.readBytes(value);
   }

   public Object readObject() throws JMSException {
      return this.message.readObject();
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

   public void writeString(String value) throws JMSException {
      this.message.writeString(value);
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
