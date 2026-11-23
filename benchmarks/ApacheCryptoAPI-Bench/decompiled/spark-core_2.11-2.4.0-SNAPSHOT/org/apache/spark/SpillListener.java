package org.apache.spark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.scheduler.SparkListenerStageCompleted;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00194A!\u0001\u0002\u0005\u0013\ti1\u000b]5mY2K7\u000f^3oKJT!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001'\t\u0001!\u0002\u0005\u0002\f\u001d5\tAB\u0003\u0002\u000e\u0005\u0005I1o\u00195fIVdWM]\u0005\u0003\u001f1\u0011Qb\u00159be.d\u0015n\u001d;f]\u0016\u0014\b\"B\t\u0001\t\u0003\u0011\u0012A\u0002\u001fj]&$h\bF\u0001\u0014!\t!\u0002!D\u0001\u0003\u0011\u001d1\u0002A1A\u0005\n]\tAc\u001d;bO\u0016LE\rV8UCN\\W*\u001a;sS\u000e\u001cX#\u0001\r\u0011\te\u0001#EJ\u0007\u00025)\u00111\u0004H\u0001\b[V$\u0018M\u00197f\u0015\tib$\u0001\u0006d_2dWm\u0019;j_:T\u0011aH\u0001\u0006g\u000e\fG.Y\u0005\u0003Ci\u0011q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0002$I5\ta$\u0003\u0002&=\t\u0019\u0011J\u001c;\u0011\u0007e9\u0013&\u0003\u0002)5\tY\u0011I\u001d:bs\n+hMZ3s!\tQS&D\u0001,\u0015\ta#!\u0001\u0005fq\u0016\u001cW\u000f^8s\u0013\tq3FA\u0006UCN\\W*\u001a;sS\u000e\u001c\bB\u0002\u0019\u0001A\u0003%\u0001$A\u000bti\u0006<W-\u00133U_R\u000b7o['fiJL7m\u001d\u0011\t\u000fI\u0002!\u0019!C\u0005g\u0005y1\u000f]5mY\u0016$7\u000b^1hK&#7/F\u00015!\rIRGI\u0005\u0003mi\u0011q\u0001S1tQN+G\u000f\u0003\u00049\u0001\u0001\u0006I\u0001N\u0001\u0011gBLG\u000e\\3e'R\fw-Z%eg\u0002BqA\u000f\u0001C\u0002\u0013%1(\u0001\u0006ti\u0006<Wm\u001d#p]\u0016,\u0012\u0001\u0010\t\u0003{\u0011k\u0011A\u0010\u0006\u0003\u007f\u0001\u000b!bY8oGV\u0014(/\u001a8u\u0015\t\t%)\u0001\u0003vi&d'\"A\"\u0002\t)\fg/Y\u0005\u0003\u000bz\u0012abQ8v]R$un\u001e8MCR\u001c\u0007\u000e\u0003\u0004H\u0001\u0001\u0006I\u0001P\u0001\fgR\fw-Z:E_:,\u0007\u0005C\u0003J\u0001\u0011\u0005!*\u0001\tok6\u001c\u0006/\u001b7mK\u0012\u001cF/Y4fgV\t!\u0005C\u0003M\u0001\u0011\u0005S*A\u0005p]R\u000b7o[#oIR\u0011a*\u0015\t\u0003G=K!\u0001\u0015\u0010\u0003\tUs\u0017\u000e\u001e\u0005\u0006%.\u0003\raU\u0001\bi\u0006\u001c8.\u00128e!\tYA+\u0003\u0002V\u0019\t!2\u000b]1sW2K7\u000f^3oKJ$\u0016m]6F]\u0012DQa\u0016\u0001\u0005Ba\u000b\u0001c\u001c8Ti\u0006<WmQ8na2,G/\u001a3\u0015\u00059K\u0006\"\u0002.W\u0001\u0004Y\u0016!D:uC\u001e,7i\\7qY\u0016$X\r\u0005\u0002\f9&\u0011Q\f\u0004\u0002\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmQ8na2,G/\u001a3\t\u000b}\u0003A\u0011\t1\u0002\u0011=t'j\u001c2F]\u0012$\"AT1\t\u000b\tt\u0006\u0019A2\u0002\r)|'-\u00128e!\tYA-\u0003\u0002f\u0019\t\u00192\u000b]1sW2K7\u000f^3oKJTuNY#oI\u0002"
)
public class SpillListener extends SparkListener {
   private final HashMap<Object, ArrayBuffer<TaskMetrics>> stageIdToTaskMetrics = new HashMap<Object, ArrayBuffer<TaskMetrics>>();
   private final HashSet<Object> spilledStageIds = new HashSet<Object>();
   private final CountDownLatch stagesDone = new CountDownLatch(1);

   private HashMap<Object, ArrayBuffer<TaskMetrics>> stageIdToTaskMetrics() {
      return this.stageIdToTaskMetrics;
   }

   private HashSet<Object> spilledStageIds() {
      return this.spilledStageIds;
   }

   private CountDownLatch stagesDone() {
      return this.stagesDone;
   }

   public int numSpilledStages() {
      Predef$.MODULE$.assert(this.stagesDone().await(10L, TimeUnit.SECONDS));
      return this.spilledStageIds().size();
   }

   public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
      ((ArrayBuffer)this.stageIdToTaskMetrics().getOrElseUpdate(BoxesRunTime.boxToInteger(taskEnd.stageId()), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayBuffer<TaskMetrics> apply() {
            return new ArrayBuffer<TaskMetrics>();
         }
      })).$plus$eq(taskEnd.taskMetrics());
   }

   public void onStageCompleted(SparkListenerStageCompleted stageComplete) {
      int stageId = stageComplete.stageInfo().stageId();
      Seq metrics = (Seq)Option$.MODULE$.option2Iterable(this.stageIdToTaskMetrics().remove(BoxesRunTime.boxToInteger(stageId))).toSeq().flatten(Predef$.MODULE$.$conforms());
      boolean spilled = BoxesRunTime.unboxToLong(((TraversableOnce)metrics.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskMetrics x$4) {
            return x$4.memoryBytesSpilled();
         }
      }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$)) > 0L;
      if (spilled) {
         this.spilledStageIds().$plus$eq(BoxesRunTime.boxToInteger(stageId));
      }

   }

   public void onJobEnd(SparkListenerJobEnd jobEnd) {
      this.stagesDone().countDown();
   }
}
