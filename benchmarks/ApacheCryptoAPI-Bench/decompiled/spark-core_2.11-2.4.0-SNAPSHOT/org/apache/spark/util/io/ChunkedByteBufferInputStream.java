package org.apache.spark.util.io;

import java.io.InputStream;
import java.nio.ByteBuffer;
import org.spark_project.guava.primitives.UnsignedBytes;
import scala.Predef$;
import scala.collection.Iterator;
import scala.math.package$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r4Q!\u0001\u0002\u0001\r1\u0011Ad\u00115v].,GMQ=uK\n+hMZ3s\u0013:\u0004X\u000f^*ue\u0016\fWN\u0003\u0002\u0004\t\u0005\u0011\u0011n\u001c\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011aBE\u0007\u0002\u001f)\u00111\u0001\u0005\u0006\u0002#\u0005!!.\u0019<b\u0013\t\u0019rBA\u0006J]B,Ho\u0015;sK\u0006l\u0007\u0002C\u000b\u0001\u0005\u0003\u0007I\u0011A\f\u0002#\rDWO\\6fI\nKH/\u001a\"vM\u001a,'o\u0001\u0001\u0016\u0003a\u0001\"!\u0007\u000e\u000e\u0003\tI!a\u0007\u0002\u0003#\rCWO\\6fI\nKH/\u001a\"vM\u001a,'\u000f\u0003\u0005\u001e\u0001\t\u0005\r\u0011\"\u0001\u001f\u0003U\u0019\u0007.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM]0%KF$\"aH\u0013\u0011\u0005\u0001\u001aS\"A\u0011\u000b\u0003\t\nQa]2bY\u0006L!\u0001J\u0011\u0003\tUs\u0017\u000e\u001e\u0005\bMq\t\t\u00111\u0001\u0019\u0003\rAH%\r\u0005\tQ\u0001\u0011\t\u0011)Q\u00051\u0005\u00112\r[;oW\u0016$')\u001f;f\u0005V4g-\u001a:!\u0011!Q\u0003A!A!\u0002\u0013Y\u0013a\u00023jgB|7/\u001a\t\u0003A1J!!L\u0011\u0003\u000f\t{w\u000e\\3b]\")q\u0006\u0001C\u0001a\u00051A(\u001b8jiz\"2!\r\u001a4!\tI\u0002\u0001C\u0003\u0016]\u0001\u0007\u0001\u0004C\u0003+]\u0001\u00071\u0006\u0003\u00046\u0001\u0001\u0006KAN\u0001\u0007G\",hn[:\u0011\u0007]RD(D\u00019\u0015\tI\u0014%\u0001\u0006d_2dWm\u0019;j_:L!a\u000f\u001d\u0003\u0011%#XM]1u_J\u0004\"!\u0010!\u000e\u0003yR!a\u0010\t\u0002\u00079Lw.\u0003\u0002B}\tQ!)\u001f;f\u0005V4g-\u001a:\t\r\r\u0003\u0001\u0015)\u0003=\u00031\u0019WO\u001d:f]R\u001c\u0005.\u001e8l\u0011\u0015)\u0005\u0001\"\u0011G\u0003\u0011\u0011X-\u00193\u0015\u0003\u001d\u0003\"\u0001\t%\n\u0005%\u000b#aA%oi\")Q\t\u0001C!\u0017R!q\t\u0014+W\u0011\u0015i%\n1\u0001O\u0003\u0011!Wm\u001d;\u0011\u0007\u0001z\u0015+\u0003\u0002QC\t)\u0011I\u001d:bsB\u0011\u0001EU\u0005\u0003'\u0006\u0012AAQ=uK\")QK\u0013a\u0001\u000f\u00061qN\u001a4tKRDQa\u0016&A\u0002\u001d\u000ba\u0001\\3oORD\u0007\"B-\u0001\t\u0003R\u0016\u0001B:lSB$\"a\u00170\u0011\u0005\u0001b\u0016BA/\"\u0005\u0011auN\\4\t\u000b}C\u0006\u0019A.\u0002\u000b\tLH/Z:\t\u000b\u0005\u0004A\u0011\t2\u0002\u000b\rdwn]3\u0015\u0003}\u0001"
)
public class ChunkedByteBufferInputStream extends InputStream {
   private ChunkedByteBuffer chunkedByteBuffer;
   private final boolean dispose;
   private Iterator<ByteBuffer> chunks;
   private ByteBuffer currentChunk;

   public ChunkedByteBuffer chunkedByteBuffer() {
      return this.chunkedByteBuffer;
   }

   public void chunkedByteBuffer_$eq(ChunkedByteBuffer x$1) {
      this.chunkedByteBuffer = x$1;
   }

   public int read() {
      if (this.currentChunk != null && !this.currentChunk.hasRemaining() && this.chunks.hasNext()) {
         this.currentChunk = this.chunks.next();
      }

      int var10000;
      if (this.currentChunk != null && this.currentChunk.hasRemaining()) {
         var10000 = UnsignedBytes.toInt(this.currentChunk.get());
      } else {
         this.close();
         var10000 = -1;
      }

      return var10000;
   }

   public int read(byte[] dest, int offset, int length) {
      if (this.currentChunk != null && !this.currentChunk.hasRemaining() && this.chunks.hasNext()) {
         this.currentChunk = this.chunks.next();
      }

      int var10000;
      if (this.currentChunk != null && this.currentChunk.hasRemaining()) {
         int amountToGet = package$.MODULE$.min(this.currentChunk.remaining(), length);
         this.currentChunk.get(dest, offset, amountToGet);
         var10000 = amountToGet;
      } else {
         this.close();
         var10000 = -1;
      }

      return var10000;
   }

   public long skip(long bytes) {
      long var10000;
      if (this.currentChunk == null) {
         var10000 = 0L;
      } else {
         int amountToSkip = (int)package$.MODULE$.min(bytes, (long)this.currentChunk.remaining());
         this.currentChunk.position(this.currentChunk.position() + amountToSkip);
         if (this.currentChunk.remaining() == 0) {
            if (this.chunks.hasNext()) {
               this.currentChunk = this.chunks.next();
            } else {
               this.close();
            }
         }

         var10000 = (long)amountToSkip;
      }

      return var10000;
   }

   public void close() {
      if (this.chunkedByteBuffer() != null && this.dispose) {
         this.chunkedByteBuffer().dispose();
      }

      this.chunkedByteBuffer_$eq((ChunkedByteBuffer)null);
      this.chunks = null;
      this.currentChunk = null;
   }

   public ChunkedByteBufferInputStream(ChunkedByteBuffer chunkedByteBuffer, boolean dispose) {
      this.chunkedByteBuffer = chunkedByteBuffer;
      this.dispose = dispose;
      super();
      this.chunks = Predef$.MODULE$.refArrayOps((Object[])this.chunkedByteBuffer().getChunks()).iterator();
      this.currentChunk = this.chunks.hasNext() ? (ByteBuffer)this.chunks.next() : null;
   }
}
