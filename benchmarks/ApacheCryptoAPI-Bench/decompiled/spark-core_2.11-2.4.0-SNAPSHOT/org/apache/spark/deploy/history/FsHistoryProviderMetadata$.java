package org.apache.spark.deploy.history;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class FsHistoryProviderMetadata$ extends AbstractFunction3<Object, Object, String, FsHistoryProviderMetadata> implements Serializable {
   public static final FsHistoryProviderMetadata$ MODULE$;

   static {
      new FsHistoryProviderMetadata$();
   }

   public final String toString() {
      return "FsHistoryProviderMetadata";
   }

   public FsHistoryProviderMetadata apply(long version, long uiVersion, String logDir) {
      return new FsHistoryProviderMetadata(version, uiVersion, logDir);
   }

   public Option<Tuple3<Object, Object, String>> unapply(FsHistoryProviderMetadata x$0) {
      return (Option<Tuple3<Object, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.version()), BoxesRunTime.boxToLong(x$0.uiVersion()), x$0.logDir())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private FsHistoryProviderMetadata$() {
      MODULE$ = this;
   }
}
