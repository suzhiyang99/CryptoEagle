package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockManagerId;

public final class MapStatus$ {
   public static final MapStatus$ MODULE$;
   private final double LOG_BASE;

   static {
      new MapStatus$();
   }

   public MapStatus apply(BlockManagerId loc, long[] uncompressedSizes) {
      return (MapStatus)(uncompressedSizes.length > 2000 ? HighlyCompressedMapStatus$.MODULE$.apply(loc, uncompressedSizes) : new CompressedMapStatus(loc, uncompressedSizes));
   }

   public byte compressSize(long size) {
      return size == 0L ? 0 : (size <= 1L ? 1 : (byte)scala.math.package$.MODULE$.min(255, (int)scala.math.package$.MODULE$.ceil(scala.math.package$.MODULE$.log((double)size) / scala.math.package$.MODULE$.log(this.LOG_BASE))));
   }

   public long decompressSize(byte compressedSize) {
      return compressedSize == 0 ? 0L : (long)scala.math.package$.MODULE$.pow(this.LOG_BASE, (double)(compressedSize & 255));
   }

   private MapStatus$() {
      MODULE$ = this;
      this.LOG_BASE = 1.1;
   }
}
