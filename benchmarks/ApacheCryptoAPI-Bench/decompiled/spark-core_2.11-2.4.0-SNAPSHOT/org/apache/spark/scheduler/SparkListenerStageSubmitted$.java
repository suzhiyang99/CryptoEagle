package org.apache.spark.scheduler;

import java.util.Properties;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class SparkListenerStageSubmitted$ extends AbstractFunction2<StageInfo, Properties, SparkListenerStageSubmitted> implements Serializable {
   public static final SparkListenerStageSubmitted$ MODULE$;

   static {
      new SparkListenerStageSubmitted$();
   }

   public final String toString() {
      return "SparkListenerStageSubmitted";
   }

   public SparkListenerStageSubmitted apply(StageInfo stageInfo, Properties properties) {
      return new SparkListenerStageSubmitted(stageInfo, properties);
   }

   public Option<Tuple2<StageInfo, Properties>> unapply(SparkListenerStageSubmitted x$0) {
      return (Option<Tuple2<StageInfo, Properties>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.stageInfo(), x$0.properties())));
   }

   public Properties apply$default$2() {
      return null;
   }

   public Properties $lessinit$greater$default$2() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerStageSubmitted$() {
      MODULE$ = this;
   }
}
