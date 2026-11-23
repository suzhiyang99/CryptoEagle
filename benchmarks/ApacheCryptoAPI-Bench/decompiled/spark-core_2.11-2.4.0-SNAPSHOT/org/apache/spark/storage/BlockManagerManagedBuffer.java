package org.apache.spark.storage;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.spark.network.buffer.ManagedBuffer;
import scala.Option;
import scala.Predef$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-4Q!\u0001\u0002\u0001\u0005)\u0011\u0011D\u00117pG.l\u0015M\\1hKJl\u0015M\\1hK\u0012\u0014UO\u001a4fe*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0004ck\u001a4WM\u001d\u0006\u0003!\u0011\tqA\\3uo>\u00148.\u0003\u0002\u0013\u001b\tiQ*\u00198bO\u0016$')\u001e4gKJD\u0001\u0002\u0006\u0001\u0003\u0002\u0003\u0006IAF\u0001\u0011E2|7m[%oM>l\u0015M\\1hKJ\u001c\u0001\u0001\u0005\u0002\u001815\t!!\u0003\u0002\u001a\u0005\t\u0001\"\t\\8dW&sgm\\'b]\u0006<WM\u001d\u0005\t7\u0001\u0011\t\u0011)A\u00059\u00059!\r\\8dW&#\u0007CA\f\u001e\u0013\tq\"AA\u0004CY>\u001c7.\u00133\t\u0011\u0001\u0002!\u0011!Q\u0001\n\u0005\nA\u0001Z1uCB\u0011qCI\u0005\u0003G\t\u0011\u0011B\u00117pG.$\u0015\r^1\t\u0011\u0015\u0002!\u0011!Q\u0001\n\u0019\nq\u0001Z5ta>\u001cX\r\u0005\u0002(U5\t\u0001FC\u0001*\u0003\u0015\u00198-\u00197b\u0013\tY\u0003FA\u0004C_>dW-\u00198\t\u000b5\u0002A\u0011\u0001\u0018\u0002\rqJg.\u001b;?)\u0015y\u0003'\r\u001a4!\t9\u0002\u0001C\u0003\u0015Y\u0001\u0007a\u0003C\u0003\u001cY\u0001\u0007A\u0004C\u0003!Y\u0001\u0007\u0011\u0005C\u0003&Y\u0001\u0007a\u0005C\u00046\u0001\t\u0007I\u0011\u0002\u001c\u0002\u0011I,gmQ8v]R,\u0012a\u000e\t\u0003q\u0005k\u0011!\u000f\u0006\u0003um\na!\u0019;p[&\u001c'B\u0001\u001f>\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0003}}\nA!\u001e;jY*\t\u0001)\u0001\u0003kCZ\f\u0017B\u0001\":\u00055\tEo\\7jG&sG/Z4fe\"1A\t\u0001Q\u0001\n]\n\u0011B]3g\u0007>,h\u000e\u001e\u0011\t\u000b\u0019\u0003A\u0011I$\u0002\tML'0\u001a\u000b\u0002\u0011B\u0011q%S\u0005\u0003\u0015\"\u0012A\u0001T8oO\")A\n\u0001C!\u001b\u0006ia.[8CsR,')\u001e4gKJ$\u0012A\u0014\t\u0003\u001fJk\u0011\u0001\u0015\u0006\u0003#~\n1A\\5p\u0013\t\u0019\u0006K\u0001\u0006CsR,')\u001e4gKJDQ!\u0016\u0001\u0005BY\u000b\u0011c\u0019:fCR,\u0017J\u001c9viN#(/Z1n)\u00059\u0006C\u0001-\\\u001b\u0005I&B\u0001.@\u0003\tIw.\u0003\u0002]3\nY\u0011J\u001c9viN#(/Z1n\u0011\u0015q\u0006\u0001\"\u0011`\u00039\u0019wN\u001c<feR$vNT3uif$\u0012\u0001\u0019\t\u0003C\u0012l\u0011A\u0019\u0006\u0003G~\nA\u0001\\1oO&\u0011QM\u0019\u0002\u0007\u001f\nTWm\u0019;\t\u000b\u001d\u0004A\u0011\t5\u0002\rI,G/Y5o)\u0005Y\u0001\"\u00026\u0001\t\u0003B\u0017a\u0002:fY\u0016\f7/\u001a"
)
public class BlockManagerManagedBuffer extends ManagedBuffer {
   private final BlockInfoManager blockInfoManager;
   private final BlockId blockId;
   private final BlockData data;
   private final boolean dispose;
   private final AtomicInteger refCount;

   private AtomicInteger refCount() {
      return this.refCount;
   }

   public long size() {
      return this.data.size();
   }

   public ByteBuffer nioByteBuffer() {
      return this.data.toByteBuffer();
   }

   public InputStream createInputStream() {
      return this.data.toInputStream();
   }

   public Object convertToNetty() {
      return this.data.toNetty();
   }

   public ManagedBuffer retain() {
      this.refCount().incrementAndGet();
      Option locked = this.blockInfoManager.lockForReading(this.blockId, false);
      Predef$.MODULE$.assert(locked.isDefined());
      return this;
   }

   public ManagedBuffer release() {
      this.blockInfoManager.unlock(this.blockId, this.blockInfoManager.unlock$default$2());
      if (this.refCount().decrementAndGet() == 0 && this.dispose) {
         this.data.dispose();
      }

      return this;
   }

   public BlockManagerManagedBuffer(BlockInfoManager blockInfoManager, BlockId blockId, BlockData data, boolean dispose) {
      this.blockInfoManager = blockInfoManager;
      this.blockId = blockId;
      this.data = data;
      this.dispose = dispose;
      this.refCount = new AtomicInteger(1);
   }
}
