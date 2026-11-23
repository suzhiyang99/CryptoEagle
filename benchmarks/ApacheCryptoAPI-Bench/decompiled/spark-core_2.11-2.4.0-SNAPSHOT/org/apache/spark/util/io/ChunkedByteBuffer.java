package org.apache.spark.util.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.config.package$;
import org.apache.spark.network.util.ByteArrayWritableChannel;
import org.apache.spark.storage.StorageUtils$;
import scala.Array$;
import scala.Function1;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005b!B\u0001\u0003\u0001\u0019a!!E\"ik:\\W\r\u001a\"zi\u0016\u0014UO\u001a4fe*\u00111\u0001B\u0001\u0003S>T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011Q\u0001!\u00111A\u0005\u0002Y\taa\u00195v].\u001c8\u0001A\u000b\u0002/A\u0019a\u0002\u0007\u000e\n\u0005ey!!B!se\u0006L\bCA\u000e!\u001b\u0005a\"BA\u000f\u001f\u0003\rq\u0017n\u001c\u0006\u0002?\u0005!!.\u0019<b\u0013\t\tCD\u0001\u0006CsR,')\u001e4gKJD\u0001b\t\u0001\u0003\u0002\u0004%\t\u0001J\u0001\u000bG\",hn[:`I\u0015\fHCA\u0013)!\tqa%\u0003\u0002(\u001f\t!QK\\5u\u0011\u001dI#%!AA\u0002]\t1\u0001\u001f\u00132\u0011!Y\u0003A!A!B\u00139\u0012aB2ik:\\7\u000f\t\u0005\u0006[\u0001!\tAL\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005=\n\u0004C\u0001\u0019\u0001\u001b\u0005\u0011\u0001\"\u0002\u000b-\u0001\u00049\u0002bB\u001a\u0001\u0005\u0004%I\u0001N\u0001\u0015EV4g-\u001a:Xe&$Xm\u00115v].\u001c\u0016N_3\u0016\u0003U\u0002\"A\u0004\u001c\n\u0005]z!aA%oi\"1\u0011\b\u0001Q\u0001\nU\nQCY;gM\u0016\u0014xK]5uK\u000eCWO\\6TSj,\u0007\u0005\u0003\u0004<\u0001\u0001\u0006K\u0001P\u0001\tI&\u001c\bo\\:fIB\u0011a\"P\u0005\u0003}=\u0011qAQ8pY\u0016\fg\u000eC\u0004A\u0001\t\u0007I\u0011A!\u0002\tML'0Z\u000b\u0002\u0005B\u0011abQ\u0005\u0003\t>\u0011A\u0001T8oO\"1a\t\u0001Q\u0001\n\t\u000bQa]5{K\u0002BQ!\f\u0001\u0005\u0002!#\"aL%\t\u000b);\u0005\u0019\u0001\u000e\u0002\u0015\tLH/\u001a\"vM\u001a,'\u000fC\u0003M\u0001\u0011\u0005Q*\u0001\u0006xe&$XMR;mYf$\"!\n(\t\u000b=[\u0005\u0019\u0001)\u0002\u000f\rD\u0017M\u001c8fYB\u0011\u0011\u000bV\u0007\u0002%*\u00111\u000bH\u0001\tG\"\fgN\\3mg&\u0011QK\u0015\u0002\u0014/JLG/\u00192mK\nKH/Z\"iC:tW\r\u001c\u0005\u0006/\u0002!\t\u0001W\u0001\bi>tU\r\u001e;z+\u0005I\u0006C\u0001.a\u001b\u0005Y&B\u0001/^\u0003\u0019\u0011WO\u001a4fe*\u0011alX\u0001\u0006]\u0016$H/\u001f\u0006\u0002\u0007%\u0011\u0011m\u0017\u0002\b\u0005f$XMQ;g\u0011\u0015\u0019\u0007\u0001\"\u0001e\u0003\u001d!x.\u0011:sCf,\u0012!\u001a\t\u0004\u001da1\u0007C\u0001\bh\u0013\tAwB\u0001\u0003CsR,\u0007\"\u00026\u0001\t\u0003Y\u0017\u0001\u0004;p\u0005f$XMQ;gM\u0016\u0014X#\u0001\u000e\t\u000b5\u0004A\u0011\u00018\u0002\u001bQ|\u0017J\u001c9viN#(/Z1n)\tyG\u000f\u0005\u0002qe6\t\u0011O\u0003\u0002\u0004=%\u00111/\u001d\u0002\f\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u0004vYB\u0005\t\u0019\u0001\u001f\u0002\u000f\u0011L7\u000f]8tK\")q\u000f\u0001C\u0001q\u0006Iq-\u001a;DQVt7n\u001d\u000b\u0002/!)!\u0010\u0001C\u0001w\u0006!1m\u001c9z)\tyC\u0010C\u0003~s\u0002\u0007a0A\u0005bY2|7-\u0019;peB!ab`\u001b\u001b\u0013\r\t\ta\u0004\u0002\n\rVt7\r^5p]FBa!\u001e\u0001\u0005\u0002\u0005\u0015A#A\u0013\t\u0013\u0005%\u0001!%A\u0005\u0002\u0005-\u0011a\u0006;p\u0013:\u0004X\u000f^*ue\u0016\fW\u000e\n3fM\u0006,H\u000e\u001e\u00132+\t\tiAK\u0002=\u0003\u001fY#!!\u0005\u0011\t\u0005M\u0011QD\u0007\u0003\u0003+QA!a\u0006\u0002\u001a\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u00037y\u0011AC1o]>$\u0018\r^5p]&!\u0011qDA\u000b\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a"
)
public class ChunkedByteBuffer {
   private ByteBuffer[] chunks;
   private final int org$apache$spark$util$io$ChunkedByteBuffer$$bufferWriteChunkSize;
   private boolean disposed;
   private final long size;

   public ByteBuffer[] chunks() {
      return this.chunks;
   }

   public void chunks_$eq(ByteBuffer[] x$1) {
      this.chunks = x$1;
   }

   public int org$apache$spark$util$io$ChunkedByteBuffer$$bufferWriteChunkSize() {
      return this.org$apache$spark$util$io$ChunkedByteBuffer$$bufferWriteChunkSize;
   }

   public long size() {
      return this.size;
   }

   public void writeFully(WritableByteChannel channel) {
      Predef$.MODULE$.refArrayOps((Object[])this.getChunks()).foreach(new Serializable(this, channel) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ChunkedByteBuffer $outer;
         private final WritableByteChannel channel$1;

         public final void apply(ByteBuffer bytes) {
            while(bytes.remaining() > 0) {
               int ioSize = Math.min(bytes.remaining(), this.$outer.org$apache$spark$util$io$ChunkedByteBuffer$$bufferWriteChunkSize());
               bytes.limit(bytes.position() + ioSize);
               this.channel$1.write(bytes);
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.channel$1 = channel$1;
            }
         }
      });
   }

   public ByteBuf toNetty() {
      return Unpooled.wrappedBuffer(this.chunks().length, this.getChunks());
   }

   public byte[] toArray() {
      if (this.size() >= 2147483647L) {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"cannot call toArray because buffer size (", " bytes) exceeds maximum array size"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.size())})));
      } else {
         ByteArrayWritableChannel byteChannel = new ByteArrayWritableChannel((int)this.size());
         this.writeFully(byteChannel);
         byteChannel.close();
         return byteChannel.getData();
      }
   }

   public ByteBuffer toByteBuffer() {
      return this.chunks().length == 1 ? ((ByteBuffer)Predef$.MODULE$.refArrayOps((Object[])this.chunks()).head()).duplicate() : ByteBuffer.wrap(this.toArray());
   }

   public InputStream toInputStream(boolean dispose) {
      return new ChunkedByteBufferInputStream(this, dispose);
   }

   public boolean toInputStream$default$1() {
      return false;
   }

   public ByteBuffer[] getChunks() {
      return (ByteBuffer[])Predef$.MODULE$.refArrayOps((Object[])this.chunks()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ByteBuffer apply(ByteBuffer x$4) {
            return x$4.duplicate();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ByteBuffer.class)));
   }

   public ChunkedByteBuffer copy(Function1<Object, ByteBuffer> allocator) {
      ByteBuffer[] copiedChunks = (ByteBuffer[])Predef$.MODULE$.refArrayOps((Object[])this.getChunks()).map(new Serializable(this, allocator) {
         public static final long serialVersionUID = 0L;
         private final Function1 allocator$1;

         public final ByteBuffer apply(ByteBuffer chunk) {
            ByteBuffer newChunk = (ByteBuffer)this.allocator$1.apply(BoxesRunTime.boxToInteger(chunk.limit()));
            newChunk.put(chunk);
            newChunk.flip();
            return newChunk;
         }

         public {
            this.allocator$1 = allocator$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ByteBuffer.class)));
      return new ChunkedByteBuffer(copiedChunks);
   }

   public void dispose() {
      if (!this.disposed) {
         Predef$.MODULE$.refArrayOps((Object[])this.chunks()).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(ByteBuffer buffer) {
               StorageUtils$.MODULE$.dispose(buffer);
            }
         });
         this.disposed = true;
      }

   }

   public ChunkedByteBuffer(ByteBuffer[] chunks) {
      this.chunks = chunks;
      super();
      Predef$.MODULE$.require(this.chunks() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "chunks must not be null";
         }
      });
      Predef$.MODULE$.require(Predef$.MODULE$.refArrayOps((Object[])this.chunks()).forall(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ByteBuffer x$1) {
            return x$1.position() == 0;
         }
      }), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "chunks' positions must be 0";
         }
      });
      this.org$apache$spark$util$io$ChunkedByteBuffer$$bufferWriteChunkSize = (int)BoxesRunTime.unboxToLong(Option$.MODULE$.apply(SparkEnv$.MODULE$.get()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(SparkEnv x$2) {
            return BoxesRunTime.unboxToLong(x$2.conf().get(package$.MODULE$.BUFFER_WRITE_CHUNK_SIZE()));
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BoxesRunTime.unboxToLong(package$.MODULE$.BUFFER_WRITE_CHUNK_SIZE().defaultValue().get());
         }
      }));
      this.disposed = false;
      this.size = BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps((Object[])this.chunks()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(ByteBuffer x$3) {
            return (long)x$3.limit();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
   }

   public ChunkedByteBuffer(ByteBuffer byteBuffer) {
      this((ByteBuffer[])((Object[])(new ByteBuffer[]{byteBuffer})));
   }
}
