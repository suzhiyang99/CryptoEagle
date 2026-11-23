package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.util.CallSite;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class MapStageSubmitted$ extends AbstractFunction5<Object, ShuffleDependency<?, ?, ?>, CallSite, JobListener, Properties, MapStageSubmitted> implements Serializable {
   public static final MapStageSubmitted$ MODULE$;

   static {
      new MapStageSubmitted$();
   }

   public final String toString() {
      return "MapStageSubmitted";
   }

   public MapStageSubmitted apply(int jobId, ShuffleDependency<?, ?, ?> dependency, CallSite callSite, JobListener listener, Properties properties) {
      return new MapStageSubmitted(jobId, dependency, callSite, listener, properties);
   }

   public Option<Tuple5<Object, ShuffleDependency<Object, Object, Object>, CallSite, JobListener, Properties>> unapply(MapStageSubmitted x$0) {
      return (Option<Tuple5<Object, ShuffleDependency<Object, Object, Object>, CallSite, JobListener, Properties>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(BoxesRunTime.boxToInteger(x$0.jobId()), x$0.dependency(), x$0.callSite(), x$0.listener(), x$0.properties())));
   }

   public Properties apply$default$5() {
      return null;
   }

   public Properties $lessinit$greater$default$5() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private MapStageSubmitted$() {
      MODULE$ = this;
   }
}
