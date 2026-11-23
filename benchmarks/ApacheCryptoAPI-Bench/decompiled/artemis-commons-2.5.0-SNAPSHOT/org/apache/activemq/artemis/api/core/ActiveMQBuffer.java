package org.apache.activemq.artemis.api.core;

import io.netty.buffer.ByteBuf;
import java.io.DataInput;
import java.nio.ByteBuffer;

public interface ActiveMQBuffer extends DataInput {
   ByteBuf byteBuf();

   int capacity();

   int readerIndex();

   void readerIndex(int var1);

   int writerIndex();

   void writerIndex(int var1);

   void setIndex(int var1, int var2);

   int readableBytes();

   int writableBytes();

   boolean readable();

   boolean writable();

   void clear();

   void markReaderIndex();

   void resetReaderIndex();

   void markWriterIndex();

   void resetWriterIndex();

   void discardReadBytes();

   byte getByte(int var1);

   short getUnsignedByte(int var1);

   short getShort(int var1);

   int getUnsignedShort(int var1);

   int getInt(int var1);

   long getUnsignedInt(int var1);

   long getLong(int var1);

   void getBytes(int var1, ActiveMQBuffer var2);

   void getBytes(int var1, ActiveMQBuffer var2, int var3);

   void getBytes(int var1, ActiveMQBuffer var2, int var3, int var4);

   void getBytes(int var1, byte[] var2);

   void getBytes(int var1, byte[] var2, int var3, int var4);

   void getBytes(int var1, ByteBuffer var2);

   char getChar(int var1);

   float getFloat(int var1);

   double getDouble(int var1);

   void setByte(int var1, byte var2);

   void setShort(int var1, short var2);

   void setInt(int var1, int var2);

   void setLong(int var1, long var2);

   void setBytes(int var1, ActiveMQBuffer var2);

   void setBytes(int var1, ActiveMQBuffer var2, int var3);

   void setBytes(int var1, ActiveMQBuffer var2, int var3, int var4);

   void setBytes(int var1, byte[] var2);

   void setBytes(int var1, byte[] var2, int var3, int var4);

   void setBytes(int var1, ByteBuffer var2);

   void setChar(int var1, char var2);

   void setFloat(int var1, float var2);

   void setDouble(int var1, double var2);

   byte readByte();

   int readUnsignedByte();

   short readShort();

   int readUnsignedShort();

   int readInt();

   long readUnsignedInt();

   long readLong();

   char readChar();

   float readFloat();

   double readDouble();

   boolean readBoolean();

   SimpleString readNullableSimpleString();

   String readNullableString();

   SimpleString readSimpleString();

   String readString();

   String readUTF();

   ActiveMQBuffer readBytes(int var1);

   ActiveMQBuffer readSlice(int var1);

   void readBytes(ActiveMQBuffer var1);

   void readBytes(ActiveMQBuffer var1, int var2);

   void readBytes(ActiveMQBuffer var1, int var2, int var3);

   void readBytes(byte[] var1);

   void readBytes(byte[] var1, int var2, int var3);

   void readBytes(ByteBuffer var1);

   int skipBytes(int var1);

   void writeByte(byte var1);

   void writeShort(short var1);

   void writeInt(int var1);

   void writeLong(long var1);

   void writeChar(char var1);

   void writeFloat(float var1);

   void writeDouble(double var1);

   void writeBoolean(boolean var1);

   void writeNullableSimpleString(SimpleString var1);

   void writeNullableString(String var1);

   void writeSimpleString(SimpleString var1);

   void writeString(String var1);

   void writeUTF(String var1);

   void writeBytes(ActiveMQBuffer var1, int var2);

   void writeBytes(ActiveMQBuffer var1, int var2, int var3);

   void writeBytes(byte[] var1);

   void writeBytes(byte[] var1, int var2, int var3);

   void writeBytes(ByteBuffer var1);

   void writeBytes(ByteBuf var1, int var2, int var3);

   ActiveMQBuffer copy();

   ActiveMQBuffer copy(int var1, int var2);

   ActiveMQBuffer slice();

   ActiveMQBuffer slice(int var1, int var2);

   ActiveMQBuffer duplicate();

   ByteBuffer toByteBuffer();

   ByteBuffer toByteBuffer(int var1, int var2);

   void release();
}
