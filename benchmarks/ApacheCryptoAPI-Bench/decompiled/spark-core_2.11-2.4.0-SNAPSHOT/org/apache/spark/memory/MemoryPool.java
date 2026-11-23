package org.apache.spark.memory;

import javax.annotation.concurrent.GuardedBy;
import scala.Predef$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d3a!\u0001\u0002\u0002\u0002\tQ!AC'f[>\u0014\u0018\u0010U8pY*\u00111\u0001B\u0001\u0007[\u0016lwN]=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011!\u0011\u0002A!A!\u0002\u0013!\u0012\u0001\u00027pG.\u001c\u0001\u0001\u0005\u0002\u001655\taC\u0003\u0002\u00181\u0005!A.\u00198h\u0015\u0005I\u0012\u0001\u00026bm\u0006L!a\u0007\f\u0003\r=\u0013'.Z2u\u0011\u0015i\u0002\u0001\"\u0001\u001f\u0003\u0019a\u0014N\\5u}Q\u0011q$\t\t\u0003A\u0001i\u0011A\u0001\u0005\u0006%q\u0001\r\u0001\u0006\u0005\u0007G\u0001\u0001\u000b\u0015\u0002\u0013\u0002\u0013}\u0003xn\u001c7TSj,\u0007C\u0001\u0007&\u0013\t1SB\u0001\u0003M_:<\u0007\u0006\u0002\u0012)eM\u0002\"!\u000b\u0019\u000e\u0003)R!a\u000b\u0017\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002.]\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\u000b\u0003=\nQA[1wCbL!!\r\u0016\u0003\u0013\u001d+\u0018M\u001d3fI\nK\u0018!\u0002<bYV,\u0017%\u0001\n\t\u000bU\u0002AQ\u0001\u001c\u0002\u0011A|w\u000e\\*ju\u0016,\u0012\u0001\n\u0005\u0006q\u0001!)AN\u0001\u000b[\u0016lwN]=Ge\u0016,\u0007\"\u0002\u001e\u0001\t\u000bY\u0014!E5oGJ,W.\u001a8u!>|GnU5{KR\u0011Ah\u0010\t\u0003\u0019uJ!AP\u0007\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u0001f\u0002\r\u0001J\u0001\u0006I\u0016dG/\u0019\u0005\u0006\u0005\u0002!)aQ\u0001\u0012I\u0016\u001c'/Z7f]R\u0004vn\u001c7TSj,GC\u0001\u001fE\u0011\u0015\u0001\u0015\t1\u0001%\u0011\u00151\u0005A\"\u00017\u0003)iW-\\8ssV\u001bX\r\u001a"
)
public abstract class MemoryPool {
   private final Object lock;
   @GuardedBy("lock")
   private long _poolSize;

   public final long poolSize() {
      Long var2;
      synchronized(this.lock) {
         var2 = BoxesRunTime.boxToLong(this._poolSize);
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public final long memoryFree() {
      Long var2;
      synchronized(this.lock) {
         var2 = BoxesRunTime.boxToLong(this._poolSize - this.memoryUsed());
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public final void incrementPoolSize(long delta) {
      synchronized(this.lock) {
         Predef$.MODULE$.require(delta >= 0L);
         this._poolSize += delta;
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public final void decrementPoolSize(long delta) {
      synchronized(this.lock) {
         Predef$.MODULE$.require(delta >= 0L);
         Predef$.MODULE$.require(delta <= this._poolSize);
         Predef$.MODULE$.require(this._poolSize - delta >= this.memoryUsed());
         this._poolSize -= delta;
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public abstract long memoryUsed();

   public MemoryPool(Object lock) {
      this.lock = lock;
      this._poolSize = 0L;
   }
}
