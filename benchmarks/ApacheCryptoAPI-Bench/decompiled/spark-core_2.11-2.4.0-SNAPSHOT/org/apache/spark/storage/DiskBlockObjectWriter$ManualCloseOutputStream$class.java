package org.apache.spark.storage;

import java.io.OutputStream;

public abstract class DiskBlockObjectWriter$ManualCloseOutputStream$class {
   public static void close(DiskBlockObjectWriter.ManualCloseOutputStream $this) {
      ((OutputStream)$this).flush();
   }

   public static void manualClose(DiskBlockObjectWriter.ManualCloseOutputStream $this) {
      $this.org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseOutputStream$$super$close();
   }

   public static void $init$(DiskBlockObjectWriter.ManualCloseOutputStream $this) {
   }
}
