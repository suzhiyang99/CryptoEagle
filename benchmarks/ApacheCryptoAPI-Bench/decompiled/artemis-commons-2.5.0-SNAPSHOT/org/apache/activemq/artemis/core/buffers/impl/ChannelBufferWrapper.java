package org.apache.activemq.artemis.core.buffers.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.utils.ByteUtil;
import org.apache.activemq.artemis.utils.UTF8Util;

public class ChannelBufferWrapper implements ActiveMQBuffer {
   protected final ByteBuf buffer;
   private final boolean releasable;
   private final boolean isPooled;

   public static ByteBuf unwrap(ByteBuf buffer) {
      ByteBuf parent;
      while((parent = buffer.unwrap()) != null && parent != buffer) {
         buffer = parent;
      }

      return buffer;
   }

   public ChannelBufferWrapper(ByteBuf buffer) {
      this(buffer, false);
   }

   public ChannelBufferWrapper(ByteBuf buffer, boolean releasable) {
      this(buffer, releasable, false);
   }

   public ChannelBufferWrapper(ByteBuf buffer, boolean releasable, boolean pooled) {
      if (!releasable) {
         this.buffer = Unpooled.unreleasableBuffer(buffer);
      } else {
         this.buffer = buffer;
      }

      this.releasable = releasable;
      this.isPooled = pooled;
   }

   public boolean readBoolean() {
      return this.readByte() != 0;
   }

   public SimpleString readNullableSimpleString() {
      return SimpleString.readNullableSimpleString(this.buffer);
   }

   public String readNullableString() {
      int b = this.buffer.readByte();
      return b == 0 ? null : this.readStringInternal();
   }

   public SimpleString readSimpleString() {
      return SimpleString.readSimpleString(this.buffer);
   }

   public String readString() {
      return this.readStringInternal();
   }

   private String readStringInternal() {
      int len = this.buffer.readInt();
      if (len >= 9) {
         return len < 4095 ? this.readUTF() : SimpleString.readSimpleString(this.buffer).toString();
      } else {
         char[] chars = new char[len];

         for(int i = 0; i < len; ++i) {
            chars[i] = (char)this.buffer.readShort();
         }

         return new String(chars);
      }
   }

   public void writeNullableString(String val) {
      UTF8Util.writeNullableString(this.buffer, val);
   }

   public void writeUTF(String utf) {
      UTF8Util.saveUTF(this.buffer, utf);
   }

   public String readUTF() {
      return UTF8Util.readUTF(this);
   }

   public void writeBoolean(boolean val) {
      this.buffer.writeByte((byte)(val ? -1 : 0));
   }

   public void writeNullableSimpleString(SimpleString val) {
      SimpleString.writeNullableSimpleString(this.buffer, val);
   }

   public void writeSimpleString(SimpleString val) {
      SimpleString.writeSimpleString(this.buffer, val);
   }

   public void writeString(String val) {
      UTF8Util.writeString(this.buffer, val);
   }

   public int capacity() {
      return this.buffer.capacity();
   }

   public ByteBuf byteBuf() {
      return this.buffer;
   }

   public void clear() {
      this.buffer.clear();
   }

   public ActiveMQBuffer copy() {
      return new ChannelBufferWrapper(this.buffer.copy(), this.releasable);
   }

   public ActiveMQBuffer copy(int index, int length) {
      return new ChannelBufferWrapper(this.buffer.copy(index, length), this.releasable);
   }

   public void discardReadBytes() {
      this.buffer.discardReadBytes();
   }

   public ActiveMQBuffer duplicate() {
      return new ChannelBufferWrapper(this.buffer.duplicate(), this.releasable);
   }

   public byte getByte(int index) {
      return this.buffer.getByte(index);
   }

   public void getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst, dstIndex, length);
   }

   public void getBytes(int index, byte[] dst) {
      this.buffer.getBytes(index, dst);
   }

   public void getBytes(int index, ByteBuffer dst) {
      this.buffer.getBytes(index, dst);
   }

   public void getBytes(int index, ActiveMQBuffer dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst.byteBuf(), dstIndex, length);
   }

   public void getBytes(int index, ActiveMQBuffer dst, int length) {
      this.buffer.getBytes(index, dst.byteBuf(), length);
   }

   public void getBytes(int index, ActiveMQBuffer dst) {
      this.buffer.getBytes(index, dst.byteBuf());
   }

   public char getChar(int index) {
      return (char)this.buffer.getShort(index);
   }

   public double getDouble(int index) {
      return Double.longBitsToDouble(this.buffer.getLong(index));
   }

   public float getFloat(int index) {
      return Float.intBitsToFloat(this.buffer.getInt(index));
   }

   public int getInt(int index) {
      return this.buffer.getInt(index);
   }

   public long getLong(int index) {
      return this.buffer.getLong(index);
   }

   public short getShort(int index) {
      return this.buffer.getShort(index);
   }

   public short getUnsignedByte(int index) {
      return this.buffer.getUnsignedByte(index);
   }

   public long getUnsignedInt(int index) {
      return this.buffer.getUnsignedInt(index);
   }

   public int getUnsignedShort(int index) {
      return this.buffer.getUnsignedShort(index);
   }

   public void markReaderIndex() {
      this.buffer.markReaderIndex();
   }

   public void markWriterIndex() {
      this.buffer.markWriterIndex();
   }

   public boolean readable() {
      return this.buffer.isReadable();
   }

   public int readableBytes() {
      return this.buffer.readableBytes();
   }

   public byte readByte() {
      return this.buffer.readByte();
   }

   public void readBytes(byte[] dst, int dstIndex, int length) {
      this.buffer.readBytes(dst, dstIndex, length);
   }

   public void readBytes(byte[] dst) {
      this.buffer.readBytes(dst);
   }

   public void readBytes(ByteBuffer dst) {
      this.buffer.readBytes(dst);
   }

   public void readBytes(ActiveMQBuffer dst, int dstIndex, int length) {
      this.buffer.readBytes(dst.byteBuf(), dstIndex, length);
   }

   public void readBytes(ActiveMQBuffer dst, int length) {
      this.buffer.readBytes(dst.byteBuf(), length);
   }

   public void readBytes(ActiveMQBuffer dst) {
      this.buffer.readBytes(dst.byteBuf());
   }

   public ActiveMQBuffer readBytes(int length) {
      return new ChannelBufferWrapper(this.buffer.readBytes(length), this.releasable);
   }

   public char readChar() {
      return (char)this.buffer.readShort();
   }

   public double readDouble() {
      return Double.longBitsToDouble(this.buffer.readLong());
   }

   public int readerIndex() {
      return this.buffer.readerIndex();
   }

   public void readerIndex(int readerIndex) {
      this.buffer.readerIndex(readerIndex);
   }

   public float readFloat() {
      return Float.intBitsToFloat(this.buffer.readInt());
   }

   public int readInt() {
      return this.buffer.readInt();
   }

   public long readLong() {
      return this.buffer.readLong();
   }

   public short readShort() {
      return this.buffer.readShort();
   }

   public ActiveMQBuffer readSlice(int length) {
      if (this.isPooled) {
         ByteBuf fromBuffer = this.buffer.readSlice(length);
         ByteBuf newNettyBuffer = Unpooled.buffer(fromBuffer.capacity());
         int read = fromBuffer.readerIndex();
         int writ = fromBuffer.writerIndex();
         fromBuffer.readerIndex(0);
         fromBuffer.readBytes(newNettyBuffer, 0, writ);
         newNettyBuffer.setIndex(read, writ);
         ActiveMQBuffer returnBuffer = new ChannelBufferWrapper(newNettyBuffer, this.releasable, false);
         returnBuffer.setIndex(read, writ);
         return returnBuffer;
      } else {
         return new ChannelBufferWrapper(this.buffer.readSlice(length), this.releasable, this.isPooled);
      }
   }

   public int readUnsignedByte() {
      return this.buffer.readUnsignedByte();
   }

   public long readUnsignedInt() {
      return this.buffer.readUnsignedInt();
   }

   public int readUnsignedShort() {
      return this.buffer.readUnsignedShort();
   }

   public void resetReaderIndex() {
      this.buffer.resetReaderIndex();
   }

   public void resetWriterIndex() {
      this.buffer.resetWriterIndex();
   }

   public void setByte(int index, byte value) {
      this.buffer.setByte(index, value);
   }

   public void setBytes(int index, byte[] src, int srcIndex, int length) {
      this.buffer.setBytes(index, src, srcIndex, length);
   }

   public void setBytes(int index, byte[] src) {
      this.buffer.setBytes(index, src);
   }

   public void setBytes(int index, ByteBuffer src) {
      this.buffer.setBytes(index, src);
   }

   public void setBytes(int index, ActiveMQBuffer src, int srcIndex, int length) {
      this.buffer.setBytes(index, src.byteBuf(), srcIndex, length);
   }

   public void setBytes(int index, ActiveMQBuffer src, int length) {
      this.buffer.setBytes(index, src.byteBuf(), length);
   }

   public void setBytes(int index, ActiveMQBuffer src) {
      this.buffer.setBytes(index, src.byteBuf());
   }

   public void setChar(int index, char value) {
      this.buffer.setShort(index, (short)value);
   }

   public void setDouble(int index, double value) {
      this.buffer.setLong(index, Double.doubleToLongBits(value));
   }

   public void setFloat(int index, float value) {
      this.buffer.setInt(index, Float.floatToIntBits(value));
   }

   public void setIndex(int readerIndex, int writerIndex) {
      this.buffer.setIndex(readerIndex, writerIndex);
   }

   public void setInt(int index, int value) {
      this.buffer.setInt(index, value);
   }

   public void setLong(int index, long value) {
      this.buffer.setLong(index, value);
   }

   public void setShort(int index, short value) {
      this.buffer.setShort(index, value);
   }

   public int skipBytes(int length) {
      this.buffer.skipBytes(length);
      return length;
   }

   public ActiveMQBuffer slice() {
      return new ChannelBufferWrapper(this.buffer.slice(), this.releasable);
   }

   public ActiveMQBuffer slice(int index, int length) {
      return new ChannelBufferWrapper(this.buffer.slice(index, length), this.releasable);
   }

   public ByteBuffer toByteBuffer() {
      return this.buffer.nioBuffer();
   }

   public ByteBuffer toByteBuffer(int index, int length) {
      return this.buffer.nioBuffer(index, length);
   }

   public void release() {
      if (this.isPooled) {
         this.buffer.release();
      }

   }

   public boolean writable() {
      return this.buffer.isWritable();
   }

   public int writableBytes() {
      return this.buffer.writableBytes();
   }

   public void writeByte(byte value) {
      this.buffer.writeByte(value);
   }

   public void writeBytes(byte[] src, int srcIndex, int length) {
      this.buffer.writeBytes(src, srcIndex, length);
   }

   public void writeBytes(byte[] src) {
      this.buffer.writeBytes(src);
   }

   public void writeBytes(ByteBuffer src) {
      this.buffer.writeBytes(src);
   }

   public void writeBytes(ByteBuf src, int srcIndex, int length) {
      this.buffer.writeBytes(src, srcIndex, length);
   }

   public void writeBytes(ActiveMQBuffer src, int srcIndex, int length) {
      this.buffer.writeBytes(src.byteBuf(), srcIndex, length);
   }

   public void writeBytes(ActiveMQBuffer src, int length) {
      this.buffer.writeBytes(src.byteBuf(), length);
   }

   public void writeChar(char chr) {
      this.buffer.writeShort((short)chr);
   }

   public void writeDouble(double value) {
      this.buffer.writeLong(Double.doubleToLongBits(value));
   }

   public void writeFloat(float value) {
      this.buffer.writeInt(Float.floatToIntBits(value));
   }

   public void writeInt(int value) {
      this.buffer.writeInt(value);
   }

   public void writeLong(long value) {
      this.buffer.writeLong(value);
   }

   public int writerIndex() {
      return this.buffer.writerIndex();
   }

   public void writerIndex(int writerIndex) {
      this.buffer.writerIndex(writerIndex);
   }

   public void writeShort(short value) {
      this.buffer.writeShort(value);
   }

   public void readFully(byte[] b) throws IOException {
      this.readBytes(b);
   }

   public void readFully(byte[] b, int off, int len) throws IOException {
      this.readBytes(b, off, len);
   }

   public String readLine() throws IOException {
      return ByteUtil.readLine(this);
   }
}
