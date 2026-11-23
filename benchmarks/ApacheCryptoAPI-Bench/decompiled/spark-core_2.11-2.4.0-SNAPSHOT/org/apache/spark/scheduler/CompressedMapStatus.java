package org.apache.spark.scheduler;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.BlockManagerId$;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u3Q!\u0001\u0002\u0001\t)\u00111cQ8naJ,7o]3e\u001b\u0006\u00048\u000b^1ukNT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011#\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!C'baN#\u0018\r^;t!\t12$D\u0001\u0018\u0015\tA\u0012$\u0001\u0002j_*\t!$\u0001\u0003kCZ\f\u0017B\u0001\u000f\u0018\u00059)\u0005\u0010^3s]\u0006d\u0017N_1cY\u0016D\u0001B\b\u0001\u0003\u0002\u0003\u0006K\u0001I\u0001\u0004Y>\u001c7\u0001\u0001\t\u0003C\u0011j\u0011A\t\u0006\u0003G\u0011\tqa\u001d;pe\u0006<W-\u0003\u0002&E\tq!\t\\8dW6\u000bg.Y4fe&#\u0007\u0002C\u0014\u0001\u0005\u0003\u0005\u000b\u0015\u0002\u0015\u0002\u001f\r|W\u000e\u001d:fgN,GmU5{KN\u00042\u0001D\u0015,\u0013\tQSBA\u0003BeJ\f\u0017\u0010\u0005\u0002\rY%\u0011Q&\u0004\u0002\u0005\u0005f$X\rC\u00030\u0001\u0011\u0005\u0001'\u0001\u0004=S:LGO\u0010\u000b\u0004cI\u001a\u0004C\u0001\n\u0001\u0011\u0015qb\u00061\u0001!\u0011\u00159c\u00061\u0001)\u0011\u0015y\u0003\u0001\"\u00056)\u0005\t\u0004\"B\u0018\u0001\t\u00039DcA\u00199s!)aD\u000ea\u0001A!)!H\u000ea\u0001w\u0005\tRO\\2p[B\u0014Xm]:fINK'0Z:\u0011\u00071IC\b\u0005\u0002\r{%\u0011a(\u0004\u0002\u0005\u0019>tw\rC\u0003A\u0001\u0011\u0005\u0013)\u0001\u0005m_\u000e\fG/[8o+\u0005\u0001\u0003\"B\"\u0001\t\u0003\"\u0015aD4fiNK'0\u001a$pe\ncwnY6\u0015\u0005q*\u0005\"\u0002$C\u0001\u00049\u0015\u0001\u0003:fIV\u001cW-\u00133\u0011\u00051A\u0015BA%\u000e\u0005\rIe\u000e\u001e\u0005\u0006\u0017\u0002!\t\u0005T\u0001\u000eoJLG/Z#yi\u0016\u0014h.\u00197\u0015\u00055\u0003\u0006C\u0001\u0007O\u0013\tyUB\u0001\u0003V]&$\b\"B)K\u0001\u0004\u0011\u0016aA8viB\u0011acU\u0005\u0003)^\u0011Ab\u00142kK\u000e$x*\u001e;qkRDQA\u0016\u0001\u0005B]\u000bAB]3bI\u0016CH/\u001a:oC2$\"!\u0014-\t\u000be+\u0006\u0019\u0001.\u0002\u0005%t\u0007C\u0001\f\\\u0013\tavCA\u0006PE*,7\r^%oaV$\b"
)
public class CompressedMapStatus implements MapStatus, Externalizable {
   public BlockManagerId org$apache$spark$scheduler$CompressedMapStatus$$loc;
   public byte[] org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes;

   public BlockManagerId location() {
      return this.org$apache$spark$scheduler$CompressedMapStatus$$loc;
   }

   public long getSizeForBlock(int reduceId) {
      return MapStatus$.MODULE$.decompressSize(this.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes[reduceId]);
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CompressedMapStatus $outer;
         private final ObjectOutput out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$loc.writeExternal(this.out$1);
            this.out$1.writeInt(this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes.length);
            this.out$1.write(this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   public void readExternal(ObjectInput in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CompressedMapStatus $outer;
         private final ObjectInput in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$loc = BlockManagerId$.MODULE$.apply(this.in$1);
            int len = this.in$1.readInt();
            this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes = new byte[len];
            this.in$1.readFully(this.$outer.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public CompressedMapStatus(BlockManagerId loc, byte[] compressedSizes) {
      this.org$apache$spark$scheduler$CompressedMapStatus$$loc = loc;
      this.org$apache$spark$scheduler$CompressedMapStatus$$compressedSizes = compressedSizes;
      super();
   }

   public CompressedMapStatus() {
      this((BlockManagerId)null, (byte[])null);
   }

   public CompressedMapStatus(BlockManagerId loc, long[] uncompressedSizes) {
      this(loc, (byte[])Predef$.MODULE$.longArrayOps(uncompressedSizes).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final byte apply(long size) {
            return MapStatus$.MODULE$.compressSize(size);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Byte())));
   }
}
