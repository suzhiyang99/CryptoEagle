package org.apache.spark.util.logging;

public final class SizeBasedRollingPolicy$ {
   public static final SizeBasedRollingPolicy$ MODULE$;
   private final int MINIMUM_SIZE_BYTES;

   static {
      new SizeBasedRollingPolicy$();
   }

   public int MINIMUM_SIZE_BYTES() {
      return this.MINIMUM_SIZE_BYTES;
   }

   public boolean $lessinit$greater$default$2() {
      return true;
   }

   private SizeBasedRollingPolicy$() {
      MODULE$ = this;
      this.MINIMUM_SIZE_BYTES = RollingFileAppender$.MODULE$.DEFAULT_BUFFER_SIZE() * 10;
   }
}
