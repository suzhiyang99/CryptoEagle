package org.apache.spark.storage;

public final class BlockInfo$ {
   public static final BlockInfo$ MODULE$;
   private final long NO_WRITER;
   private final long NON_TASK_WRITER;

   static {
      new BlockInfo$();
   }

   public long NO_WRITER() {
      return this.NO_WRITER;
   }

   public long NON_TASK_WRITER() {
      return this.NON_TASK_WRITER;
   }

   private BlockInfo$() {
      MODULE$ = this;
      this.NO_WRITER = -1L;
      this.NON_TASK_WRITER = -1024L;
   }
}
