package org.apache.spark.storage;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.spark.network.util.AbstractFileRegion;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001e3A!\u0001\u0002\u0005\u0017\tI\"+Z1eC\ndWm\u00115b]:,GNR5mKJ+w-[8o\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0019A\u0011QBE\u0007\u0002\u001d)\u0011q\u0002E\u0001\u0005kRLGN\u0003\u0002\u0012\t\u00059a.\u001a;x_J\\\u0017BA\n\u000f\u0005I\t%m\u001d;sC\u000e$h)\u001b7f%\u0016<\u0017n\u001c8\t\u0011U\u0001!\u0011!Q\u0001\nY\taa]8ve\u000e,\u0007CA\f\u001f\u001b\u0005A\"BA\r\u001b\u0003!\u0019\u0007.\u00198oK2\u001c(BA\u000e\u001d\u0003\rq\u0017n\u001c\u0006\u0002;\u0005!!.\u0019<b\u0013\ty\u0002DA\nSK\u0006$\u0017M\u00197f\u0005f$Xm\u00115b]:,G\u000e\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003#\u0003%\u0011Gn\\2l'&TX\r\u0005\u0002$M5\tAEC\u0001&\u0003\u0015\u00198-\u00197b\u0013\t9CE\u0001\u0003M_:<\u0007\"B\u0015\u0001\t\u0003Q\u0013A\u0002\u001fj]&$h\bF\u0002,[9\u0002\"\u0001\f\u0001\u000e\u0003\tAQ!\u0006\u0015A\u0002YAQ!\t\u0015A\u0002\tBq\u0001\r\u0001A\u0002\u0013%\u0011'\u0001\u0007`iJ\fgn\u001d4feJ,G-F\u0001#\u0011\u001d\u0019\u0004\u00011A\u0005\nQ\n\u0001c\u0018;sC:\u001ch-\u001a:sK\u0012|F%Z9\u0015\u0005UB\u0004CA\u00127\u0013\t9DE\u0001\u0003V]&$\bbB\u001d3\u0003\u0003\u0005\rAI\u0001\u0004q\u0012\n\u0004BB\u001e\u0001A\u0003&!%A\u0007`iJ\fgn\u001d4feJ,G\r\t\u0005\b{\u0001\u0011\r\u0011\"\u0003?\u0003\u0019\u0011WO\u001a4feV\tq\b\u0005\u0002A\u00036\t!$\u0003\u0002C5\tQ!)\u001f;f\u0005V4g-\u001a:\t\r\u0011\u0003\u0001\u0015!\u0003@\u0003\u001d\u0011WO\u001a4fe\u0002BQA\u0012\u0001\u0005B\u001d\u000bQaY8v]R$\u0012A\t\u0005\u0006\u0013\u0002!\teR\u0001\ta>\u001c\u0018\u000e^5p]\")1\n\u0001C!\u000f\u0006YAO]1og\u001a,'O]3e\u0011\u0015i\u0005\u0001\"\u0011O\u0003)!(/\u00198tM\u0016\u0014Hk\u001c\u000b\u0004E=#\u0006\"\u0002)M\u0001\u0004\t\u0016A\u0002;be\u001e,G\u000f\u0005\u0002\u0018%&\u00111\u000b\u0007\u0002\u0014/JLG/\u00192mK\nKH/Z\"iC:tW\r\u001c\u0005\u0006+2\u0003\rAI\u0001\u0004a>\u001c\b\"B,\u0001\t\u0003B\u0016A\u00033fC2dwnY1uKR\tQ\u0007"
)
public class ReadableChannelFileRegion extends AbstractFileRegion {
   private final ReadableByteChannel source;
   private final long blockSize;
   private long _transferred;
   private final ByteBuffer buffer;

   private long _transferred() {
      return this._transferred;
   }

   private void _transferred_$eq(long x$1) {
      this._transferred = x$1;
   }

   private ByteBuffer buffer() {
      return this.buffer;
   }

   public long count() {
      return this.blockSize;
   }

   public long position() {
      return 0L;
   }

   public long transferred() {
      return this._transferred();
   }

   public long transferTo(WritableByteChannel target, long pos) {
      Predef$.MODULE$.assert(pos == this.transfered(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Invalid position.";
         }
      });
      long written = 0L;
      long lastWrite = -1L;

      while(lastWrite != 0L) {
         if (this.buffer().hasRemaining()) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.buffer().clear();
            this.source.read(this.buffer());
            this.buffer().flip();
         }

         if (this.buffer().hasRemaining()) {
            lastWrite = (long)target.write(this.buffer());
            written += lastWrite;
         } else {
            lastWrite = 0L;
         }
      }

      this._transferred_$eq(this._transferred() + written);
      return written;
   }

   public void deallocate() {
      this.source.close();
   }

   public ReadableChannelFileRegion(ReadableByteChannel source, long blockSize) {
      this.source = source;
      this.blockSize = blockSize;
      this._transferred = 0L;
      this.buffer = ByteBuffer.allocateDirect(65536);
      this.buffer().flip();
   }
}
