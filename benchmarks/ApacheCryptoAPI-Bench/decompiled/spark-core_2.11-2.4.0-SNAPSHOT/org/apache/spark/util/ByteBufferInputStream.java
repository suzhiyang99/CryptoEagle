package org.apache.spark.util;

import java.io.InputStream;
import java.nio.ByteBuffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I3Q!\u0001\u0002\u0001\t)\u0011QCQ=uK\n+hMZ3s\u0013:\u0004X\u000f^*ue\u0016\fWN\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0002j_*\t\u0001#\u0001\u0003kCZ\f\u0017B\u0001\n\u000e\u0005-Ie\u000e];u'R\u0014X-Y7\t\u0011Q\u0001!\u00111A\u0005\nY\taAY;gM\u0016\u00148\u0001A\u000b\u0002/A\u0011\u0001dG\u0007\u00023)\u0011!dD\u0001\u0004]&|\u0017B\u0001\u000f\u001a\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\t=\u0001\u0011\t\u0019!C\u0005?\u0005Q!-\u001e4gKJ|F%Z9\u0015\u0005\u00012\u0003CA\u0011%\u001b\u0005\u0011#\"A\u0012\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u0015\u0012#\u0001B+oSRDqaJ\u000f\u0002\u0002\u0003\u0007q#A\u0002yIEB\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006KaF\u0001\bEV4g-\u001a:!\u0011\u0015Y\u0003\u0001\"\u0001-\u0003\u0019a\u0014N\\5u}Q\u0011Qf\f\t\u0003]\u0001i\u0011A\u0001\u0005\u0006))\u0002\ra\u0006\u0005\u0006c\u0001!\tEM\u0001\u0005e\u0016\fG\rF\u00014!\t\tC'\u0003\u00026E\t\u0019\u0011J\u001c;\t\u000bE\u0002A\u0011I\u001c\u0015\u0005MB\u0004\"B\u001d7\u0001\u0004Q\u0014\u0001\u00023fgR\u00042!I\u001e>\u0013\ta$EA\u0003BeJ\f\u0017\u0010\u0005\u0002\"}%\u0011qH\t\u0002\u0005\u0005f$X\rC\u00032\u0001\u0011\u0005\u0013\t\u0006\u00034\u0005\u000e+\u0005\"B\u001dA\u0001\u0004Q\u0004\"\u0002#A\u0001\u0004\u0019\u0014AB8gMN,G\u000fC\u0003G\u0001\u0002\u00071'\u0001\u0004mK:<G\u000f\u001b\u0005\u0006\u0011\u0002!\t%S\u0001\u0005g.L\u0007\u000f\u0006\u0002K\u001bB\u0011\u0011eS\u0005\u0003\u0019\n\u0012A\u0001T8oO\")aj\u0012a\u0001\u0015\u0006)!-\u001f;fg\")\u0001\u000b\u0001C\u0005#\u000691\r\\3b]V\u0003H#\u0001\u0011"
)
public class ByteBufferInputStream extends InputStream {
   private ByteBuffer buffer;

   private ByteBuffer buffer() {
      return this.buffer;
   }

   private void buffer_$eq(ByteBuffer x$1) {
      this.buffer = x$1;
   }

   public int read() {
      int var10000;
      if (this.buffer() != null && this.buffer().remaining() != 0) {
         var10000 = this.buffer().get() & 255;
      } else {
         this.cleanUp();
         var10000 = -1;
      }

      return var10000;
   }

   public int read(byte[] dest) {
      return this.read(dest, 0, dest.length);
   }

   public int read(byte[] dest, int offset, int length) {
      int var10000;
      if (this.buffer() != null && this.buffer().remaining() != 0) {
         int amountToGet = scala.math.package$.MODULE$.min(this.buffer().remaining(), length);
         this.buffer().get(dest, offset, amountToGet);
         var10000 = amountToGet;
      } else {
         this.cleanUp();
         var10000 = -1;
      }

      return var10000;
   }

   public long skip(long bytes) {
      long var10000;
      if (this.buffer() == null) {
         var10000 = 0L;
      } else {
         int amountToSkip = (int)scala.math.package$.MODULE$.min(bytes, (long)this.buffer().remaining());
         this.buffer().position(this.buffer().position() + amountToSkip);
         if (this.buffer().remaining() == 0) {
            this.cleanUp();
         }

         var10000 = (long)amountToSkip;
      }

      return var10000;
   }

   private void cleanUp() {
      if (this.buffer() != null) {
         this.buffer_$eq((ByteBuffer)null);
      }

   }

   public ByteBufferInputStream(ByteBuffer buffer) {
      this.buffer = buffer;
      super();
   }
}
