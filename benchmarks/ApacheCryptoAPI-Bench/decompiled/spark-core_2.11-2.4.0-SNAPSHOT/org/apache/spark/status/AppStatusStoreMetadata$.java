package org.apache.spark.status;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class AppStatusStoreMetadata$ extends AbstractFunction1<Object, AppStatusStoreMetadata> implements Serializable {
   public static final AppStatusStoreMetadata$ MODULE$;

   static {
      new AppStatusStoreMetadata$();
   }

   public final String toString() {
      return "AppStatusStoreMetadata";
   }

   public AppStatusStoreMetadata apply(long version) {
      return new AppStatusStoreMetadata(version);
   }

   public Option<Object> unapply(AppStatusStoreMetadata x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(x$0.version())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AppStatusStoreMetadata$() {
      MODULE$ = this;
   }
}
