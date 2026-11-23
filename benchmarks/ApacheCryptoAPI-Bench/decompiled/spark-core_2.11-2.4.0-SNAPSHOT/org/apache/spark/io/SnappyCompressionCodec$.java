package org.apache.spark.io;

import org.xerial.snappy.Snappy;
import scala.runtime.BoxedUnit;

public final class SnappyCompressionCodec$ {
   public static final SnappyCompressionCodec$ MODULE$;
   private String org$apache$spark$io$SnappyCompressionCodec$$version;
   private volatile boolean bitmap$0;

   static {
      new SnappyCompressionCodec$();
   }

   private String org$apache$spark$io$SnappyCompressionCodec$$version$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.org$apache$spark$io$SnappyCompressionCodec$$version = this.liftedTree1$1();
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$io$SnappyCompressionCodec$$version;
   }

   public String org$apache$spark$io$SnappyCompressionCodec$$version() {
      return this.bitmap$0 ? this.org$apache$spark$io$SnappyCompressionCodec$$version : this.org$apache$spark$io$SnappyCompressionCodec$$version$lzycompute();
   }

   private final String liftedTree1$1() {
      try {
         return Snappy.getNativeLibraryVersion();
      } catch (Error var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   private SnappyCompressionCodec$() {
      MODULE$ = this;
   }
}
