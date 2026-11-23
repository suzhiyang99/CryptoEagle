package org.apache.spark.scheduler;

import java.util.Properties;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple4;
import scala.collection.Seq;
import scala.runtime.AbstractFunction4;
import scala.runtime.BoxesRunTime;

public final class SparkListenerJobStart$ extends AbstractFunction4<Object, Object, Seq<StageInfo>, Properties, SparkListenerJobStart> implements Serializable {
   public static final SparkListenerJobStart$ MODULE$;

   static {
      new SparkListenerJobStart$();
   }

   public final String toString() {
      return "SparkListenerJobStart";
   }

   public SparkListenerJobStart apply(int jobId, long time, Seq<StageInfo> stageInfos, Properties properties) {
      return new SparkListenerJobStart(jobId, time, stageInfos, properties);
   }

   public Option<Tuple4<Object, Object, Seq<StageInfo>, Properties>> unapply(SparkListenerJobStart x$0) {
      return (Option<Tuple4<Object, Object, Seq<StageInfo>, Properties>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(BoxesRunTime.boxToInteger(x$0.jobId()), BoxesRunTime.boxToLong(x$0.time()), x$0.stageInfos(), x$0.properties())));
   }

   public Properties apply$default$4() {
      return null;
   }

   public Properties $lessinit$greater$default$4() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerJobStart$() {
      MODULE$ = this;
   }
}
