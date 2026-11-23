package org.apache.spark.scheduler;

import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.storage.BlockManagerId;
import org.roaringbitmap.RoaringBitmap;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class HighlyCompressedMapStatus$ implements Serializable {
   public static final HighlyCompressedMapStatus$ MODULE$;

   static {
      new HighlyCompressedMapStatus$();
   }

   public HighlyCompressedMapStatus apply(BlockManagerId loc, long[] uncompressedSizes) {
      int i = 0;
      int numNonEmptyBlocks = 0;
      int numSmallBlocks = 0;
      long totalSmallBlockSize = 0L;
      RoaringBitmap emptyBlocks = new RoaringBitmap();
      int totalNumBlocks = uncompressedSizes.length;
      long threshold = BoxesRunTime.unboxToLong(Option$.MODULE$.apply(SparkEnv$.MODULE$.get()).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(SparkEnv x$2) {
            return BoxesRunTime.unboxToLong(x$2.conf().get(org.apache.spark.internal.config.package$.MODULE$.SHUFFLE_ACCURATE_BLOCK_THRESHOLD()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BoxesRunTime.unboxToLong(org.apache.spark.internal.config.package$.MODULE$.SHUFFLE_ACCURATE_BLOCK_THRESHOLD().defaultValue().get());
         }
      }));

      ArrayBuffer hugeBlockSizesArray;
      for(hugeBlockSizesArray = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$); i < totalNumBlocks; ++i) {
         long size = uncompressedSizes[i];
         if (size > 0L) {
            ++numNonEmptyBlocks;
            if (size < threshold) {
               totalSmallBlockSize += size;
               ++numSmallBlocks;
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               hugeBlockSizesArray.$plus$eq(new Tuple2(BoxesRunTime.boxToInteger(i), BoxesRunTime.boxToByte(MapStatus$.MODULE$.compressSize(uncompressedSizes[i]))));
            }
         } else {
            emptyBlocks.add(i);
            BoxedUnit var17 = BoxedUnit.UNIT;
         }
      }

      long avgSize = numSmallBlocks > 0 ? totalSmallBlockSize / (long)numSmallBlocks : 0L;
      emptyBlocks.trim();
      emptyBlocks.runOptimize();
      return new HighlyCompressedMapStatus(loc, numNonEmptyBlocks, emptyBlocks, avgSize, hugeBlockSizesArray.toMap(Predef$.MODULE$.$conforms()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HighlyCompressedMapStatus$() {
      MODULE$ = this;
   }
}
