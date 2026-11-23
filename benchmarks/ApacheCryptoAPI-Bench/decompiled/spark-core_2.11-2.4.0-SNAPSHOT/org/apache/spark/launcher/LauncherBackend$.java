package org.apache.spark.launcher;

import java.util.concurrent.ThreadFactory;
import org.apache.spark.util.ThreadUtils$;

public final class LauncherBackend$ {
   public static final LauncherBackend$ MODULE$;
   private final ThreadFactory threadFactory;

   static {
      new LauncherBackend$();
   }

   public ThreadFactory threadFactory() {
      return this.threadFactory;
   }

   private LauncherBackend$() {
      MODULE$ = this;
      this.threadFactory = ThreadUtils$.MODULE$.namedThreadFactory("LauncherBackend");
   }
}
