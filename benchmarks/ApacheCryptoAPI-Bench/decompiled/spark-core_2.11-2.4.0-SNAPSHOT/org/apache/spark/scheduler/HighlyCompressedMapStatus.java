package org.apache.spark.scheduler;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.BlockManagerId$;
import org.apache.spark.util.Utils$;
import org.roaringbitmap.RoaringBitmap;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0001\u0011Q!!\u0007%jO\"d\u0017pQ8naJ,7o]3e\u001b\u0006\u00048\u000b^1ukNT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011#\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!C'baN#\u0018\r^;t!\t12$D\u0001\u0018\u0015\tA\u0012$\u0001\u0002j_*\t!$\u0001\u0003kCZ\f\u0017B\u0001\u000f\u0018\u00059)\u0005\u0010^3s]\u0006d\u0017N_1cY\u0016D\u0001B\b\u0001\u0003\u0002\u0003\u0006K\u0001I\u0001\u0004Y>\u001c7\u0001\u0001\t\u0003C\u0011j\u0011A\t\u0006\u0003G\u0011\tqa\u001d;pe\u0006<W-\u0003\u0002&E\tq!\t\\8dW6\u000bg.Y4fe&#\u0007\u0002C\u0014\u0001\u0005\u0003\u0005\u000b\u0015\u0002\u0015\u0002#9,XNT8o\u000b6\u0004H/\u001f\"m_\u000e\\7\u000f\u0005\u0002\rS%\u0011!&\u0004\u0002\u0004\u0013:$\b\u0002\u0003\u0017\u0001\u0005\u0003\u0005\u000b\u0015B\u0017\u0002\u0017\u0015l\u0007\u000f^=CY>\u001c7n\u001d\t\u0003]Ej\u0011a\f\u0006\u0003a!\tQB]8be&twMY5u[\u0006\u0004\u0018B\u0001\u001a0\u00055\u0011v.\u0019:j]\u001e\u0014\u0015\u000e^7ba\"AA\u0007\u0001B\u0001B\u0003&Q'A\u0004bm\u001e\u001c\u0016N_3\u0011\u000511\u0014BA\u001c\u000e\u0005\u0011auN\\4\t\u0011e\u0002!\u00111A\u0005\ni\na\u0002[;hK\ncwnY6TSj,7/F\u0001<!\u0011at\b\u000b\"\u000f\u00051i\u0014B\u0001 \u000e\u0003\u0019\u0001&/\u001a3fM&\u0011\u0001)\u0011\u0002\u0004\u001b\u0006\u0004(B\u0001 \u000e!\ta1)\u0003\u0002E\u001b\t!!)\u001f;f\u0011!1\u0005A!a\u0001\n\u00139\u0015A\u00055vO\u0016\u0014En\\2l'&TXm]0%KF$\"\u0001S&\u0011\u00051I\u0015B\u0001&\u000e\u0005\u0011)f.\u001b;\t\u000f1+\u0015\u0011!a\u0001w\u0005\u0019\u0001\u0010J\u0019\t\u00119\u0003!\u0011!Q!\nm\nq\u0002[;hK\ncwnY6TSj,7\u000f\t\u0005\u0006!\u0002!I!U\u0001\u0007y%t\u0017\u000e\u001e \u0015\rI\u001bF+\u0016,X!\t\u0011\u0002\u0001C\u0003\u001f\u001f\u0002\u0007\u0001\u0005C\u0003(\u001f\u0002\u0007\u0001\u0006C\u0003-\u001f\u0002\u0007Q\u0006C\u00035\u001f\u0002\u0007Q\u0007C\u0003:\u001f\u0002\u00071\bC\u0003Q\u0001\u0011E\u0011\fF\u0001S\u0011\u0015Y\u0006\u0001\"\u0011]\u0003!awnY1uS>tW#\u0001\u0011\t\u000by\u0003A\u0011I0\u0002\u001f\u001d,GoU5{K\u001a{'O\u00117pG.$\"!\u000e1\t\u000b\u0005l\u0006\u0019\u0001\u0015\u0002\u0011I,G-^2f\u0013\u0012DQa\u0019\u0001\u0005B\u0011\fQb\u001e:ji\u0016,\u0005\u0010^3s]\u0006dGC\u0001%f\u0011\u00151'\r1\u0001h\u0003\ryW\u000f\u001e\t\u0003-!L!![\f\u0003\u0019=\u0013'.Z2u\u001fV$\b/\u001e;\t\u000b-\u0004A\u0011\t7\u0002\u0019I,\u0017\rZ#yi\u0016\u0014h.\u00197\u0015\u0005!k\u0007\"\u00028k\u0001\u0004y\u0017AA5o!\t1\u0002/\u0003\u0002r/\tYqJ\u00196fGRLe\u000e];u\u000f\u0019\u0019(\u0001#\u0001\u0005i\u0006I\u0002*[4iYf\u001cu.\u001c9sKN\u001cX\rZ'baN#\u0018\r^;t!\t\u0011RO\u0002\u0004\u0002\u0005!\u0005AA^\n\u0004k.9\bC\u0001\u0007y\u0013\tIXB\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0003Qk\u0012\u00051\u0010F\u0001u\u0011\u0015iX\u000f\"\u0001\u007f\u0003\u0015\t\u0007\u000f\u001d7z)\u0011\u0011v0!\u0001\t\u000bya\b\u0019\u0001\u0011\t\u000f\u0005\rA\u00101\u0001\u0002\u0006\u0005\tRO\\2p[B\u0014Xm]:fINK'0Z:\u0011\t1\t9!N\u0005\u0004\u0003\u0013i!!B!se\u0006L\b\"CA\u0007k\u0006\u0005I\u0011BA\b\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005E\u0001\u0003BA\n\u00033i!!!\u0006\u000b\u0007\u0005]\u0011$\u0001\u0003mC:<\u0017\u0002BA\u000e\u0003+\u0011aa\u00142kK\u000e$\b"
)
public class HighlyCompressedMapStatus implements MapStatus, Externalizable {
   public BlockManagerId org$apache$spark$scheduler$HighlyCompressedMapStatus$$loc;
   public RoaringBitmap org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks;
   public long org$apache$spark$scheduler$HighlyCompressedMapStatus$$avgSize;
   private Map<Object, Object> org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes;

   public static HighlyCompressedMapStatus apply(BlockManagerId var0, long[] var1) {
      return HighlyCompressedMapStatus$.MODULE$.apply(var0, var1);
   }

   public Map<Object, Object> org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes() {
      return this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes;
   }

   public void org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes_$eq(Map<Object, Object> x$1) {
      this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes = x$1;
   }

   public BlockManagerId location() {
      return this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$loc;
   }

   public long getSizeForBlock(int reduceId) {
      Predef$.MODULE$.assert(this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes() != null);
      long var10000;
      if (this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks.contains(reduceId)) {
         var10000 = 0L;
      } else {
         Option var2 = this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes().get(BoxesRunTime.boxToInteger(reduceId));
         long var5;
         if (var2 instanceof Some) {
            Some var3 = (Some)var2;
            byte size = BoxesRunTime.unboxToByte(var3.x());
            var5 = MapStatus$.MODULE$.decompressSize(size);
         } else {
            if (!None$.MODULE$.equals(var2)) {
               throw new MatchError(var2);
            }

            var5 = this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$avgSize;
         }

         var10000 = var5;
      }

      return var10000;
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HighlyCompressedMapStatus $outer;
         public final ObjectOutput out$2;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$loc.writeExternal(this.out$2);
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks.writeExternal(this.out$2);
            this.out$2.writeLong(this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$avgSize);
            this.out$2.writeInt(this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes().size());
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Tuple2<Object, Object> kv) {
                  this.$outer.out$2.writeInt(kv._1$mcI$sp());
                  this.$outer.out$2.writeByte(BoxesRunTime.unboxToByte(kv._2()));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$2 = out$2;
            }
         }
      });
   }

   public void readExternal(ObjectInput in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HighlyCompressedMapStatus $outer;
         public final ObjectInput in$2;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$loc = BlockManagerId$.MODULE$.apply(this.in$2);
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks = new RoaringBitmap();
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks.readExternal(this.in$2);
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$avgSize = this.in$2.readLong();
            int count = this.in$2.readInt();
            ArrayBuffer hugeBlockSizesArray = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
            RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), count).foreach(new Serializable(this, hugeBlockSizesArray) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final ArrayBuffer hugeBlockSizesArray$1;

               public final ArrayBuffer<Tuple2<Object, Object>> apply(int x$1) {
                  int block = this.$outer.in$2.readInt();
                  byte size = this.$outer.in$2.readByte();
                  return this.hugeBlockSizesArray$1.$plus$eq(new Tuple2(BoxesRunTime.boxToInteger(block), BoxesRunTime.boxToByte(size)));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.hugeBlockSizesArray$1 = hugeBlockSizesArray$1;
                  }
               }
            });
            this.$outer.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes_$eq(hugeBlockSizesArray.toMap(Predef$.MODULE$.$conforms()));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$2 = in$2;
            }
         }
      });
   }

   public HighlyCompressedMapStatus(BlockManagerId loc, int numNonEmptyBlocks, RoaringBitmap emptyBlocks, long avgSize, Map<Object, Object> hugeBlockSizes) {
      this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$loc = loc;
      this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$emptyBlocks = emptyBlocks;
      this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$avgSize = avgSize;
      this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes = hugeBlockSizes;
      super();
      Predef$.MODULE$.require(loc == null || avgSize > 0L || this.org$apache$spark$scheduler$HighlyCompressedMapStatus$$hugeBlockSizes().size() > 0 || numNonEmptyBlocks == 0, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Average size can only be zero for map stages that produced no output";
         }
      });
   }

   public HighlyCompressedMapStatus() {
      this((BlockManagerId)null, -1, (RoaringBitmap)null, -1L, (Map)null);
   }
}
