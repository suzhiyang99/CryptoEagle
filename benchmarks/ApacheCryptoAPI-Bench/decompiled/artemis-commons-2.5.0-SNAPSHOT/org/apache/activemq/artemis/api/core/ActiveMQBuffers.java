package org.apache.activemq.artemis.api.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import org.apache.activemq.artemis.core.buffers.impl.ChannelBufferWrapper;

public final class ActiveMQBuffers {
   private static final PooledByteBufAllocator ALLOCATOR;

   public static ActiveMQBuffer dynamicBuffer(int size) {
      return new ChannelBufferWrapper(Unpooled.buffer(size));
   }

   public static ActiveMQBuffer pooledBuffer(int size) {
      return new ChannelBufferWrapper(ALLOCATOR.heapBuffer(size), true, true);
   }

   public static ActiveMQBuffer dynamicBuffer(byte[] bytes) {
      ActiveMQBuffer buff = dynamicBuffer(bytes.length);
      buff.writeBytes(bytes);
      return buff;
   }

   public static ActiveMQBuffer wrappedBuffer(ByteBuffer underlying) {
      ActiveMQBuffer buff = new ChannelBufferWrapper(Unpooled.wrappedBuffer(underlying));
      buff.clear();
      return buff;
   }

   public static ActiveMQBuffer wrappedBuffer(ByteBuf underlying) {
      ActiveMQBuffer buff = new ChannelBufferWrapper(underlying.duplicate());
      return buff;
   }

   public static ActiveMQBuffer wrappedBuffer(byte[] underlying) {
      return new ChannelBufferWrapper(Unpooled.wrappedBuffer(underlying));
   }

   public static ActiveMQBuffer fixedBuffer(int size) {
      return new ChannelBufferWrapper(Unpooled.buffer(size, size));
   }

   private ActiveMQBuffers() {
   }

   static {
      ALLOCATOR = PooledByteBufAllocator.DEFAULT;
   }
}
