package org.apache.spark.status;

import org.apache.spark.SparkConf;
import org.apache.spark.util.kvstore.InMemoryStore;
import scala.None$;
import scala.Option;
import scala.Some;

public final class AppStatusStore$ {
   public static final AppStatusStore$ MODULE$;
   private final long CURRENT_VERSION;

   static {
      new AppStatusStore$();
   }

   public long CURRENT_VERSION() {
      return this.CURRENT_VERSION;
   }

   public AppStatusStore createLiveStore(SparkConf conf) {
      ElementTrackingStore store = new ElementTrackingStore(new InMemoryStore(), conf);
      AppStatusListener listener = new AppStatusListener(store, conf, true, AppStatusListener$.MODULE$.$lessinit$greater$default$4());
      return new AppStatusStore(store, new Some(listener));
   }

   public Option<AppStatusListener> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   private AppStatusStore$() {
      MODULE$ = this;
      this.CURRENT_VERSION = 1L;
   }
}
