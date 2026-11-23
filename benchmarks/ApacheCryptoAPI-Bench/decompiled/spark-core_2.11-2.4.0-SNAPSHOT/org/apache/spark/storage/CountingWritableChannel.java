package org.apache.spark.storage;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000193A!\u0001\u0002\u0005\u0017\t92i\\;oi&twm\u0016:ji\u0006\u0014G.Z\"iC:tW\r\u001c\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\u0007\u0015!\ti!#D\u0001\u000f\u0015\ty\u0001#\u0001\u0003mC:<'\"A\t\u0002\t)\fg/Y\u0005\u0003'9\u0011aa\u00142kK\u000e$\bCA\u000b\u001b\u001b\u00051\"BA\f\u0019\u0003!\u0019\u0007.\u00198oK2\u001c(BA\r\u0011\u0003\rq\u0017n\\\u0005\u00037Y\u00111c\u0016:ji\u0006\u0014G.\u001a\"zi\u0016\u001c\u0005.\u00198oK2D\u0001\"\b\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\u0005g&t7\u000eC\u0003 \u0001\u0011\u0005\u0001%\u0001\u0004=S:LGO\u0010\u000b\u0003C\r\u0002\"A\t\u0001\u000e\u0003\tAQ!\b\u0010A\u0002QAq!\n\u0001A\u0002\u0013%a%A\u0003d_VtG/F\u0001(!\tA3&D\u0001*\u0015\u0005Q\u0013!B:dC2\f\u0017B\u0001\u0017*\u0005\u0011auN\\4\t\u000f9\u0002\u0001\u0019!C\u0005_\u0005I1m\\;oi~#S-\u001d\u000b\u0003aM\u0002\"\u0001K\u0019\n\u0005IJ#\u0001B+oSRDq\u0001N\u0017\u0002\u0002\u0003\u0007q%A\u0002yIEBaA\u000e\u0001!B\u00139\u0013AB2pk:$\b\u0005C\u00039\u0001\u0011\u0005a%\u0001\u0005hKR\u001cu.\u001e8u\u0011\u0015Q\u0004\u0001\"\u0011<\u0003\u00159(/\u001b;f)\tat\b\u0005\u0002){%\u0011a(\u000b\u0002\u0004\u0013:$\b\"\u0002!:\u0001\u0004\t\u0015aA:sGB\u0011!iQ\u0007\u00021%\u0011A\t\u0007\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\"\u0002$\u0001\t\u0003:\u0015AB5t\u001fB,g\u000eF\u0001I!\tA\u0013*\u0003\u0002KS\t9!i\\8mK\u0006t\u0007\"\u0002'\u0001\t\u0003j\u0015!B2m_N,G#\u0001\u0019"
)
public class CountingWritableChannel implements WritableByteChannel {
   private final WritableByteChannel sink;
   private long count;

   private long count() {
      return this.count;
   }

   private void count_$eq(long x$1) {
      this.count = x$1;
   }

   public long getCount() {
      return this.count();
   }

   public int write(ByteBuffer src) {
      int written = this.sink.write(src);
      if (written > 0) {
         this.count_$eq(this.count() + (long)written);
      }

      return written;
   }

   public boolean isOpen() {
      return this.sink.isOpen();
   }

   public void close() {
      this.sink.close();
   }

   public CountingWritableChannel(WritableByteChannel sink) {
      this.sink = sink;
      this.count = 0L;
   }
}
