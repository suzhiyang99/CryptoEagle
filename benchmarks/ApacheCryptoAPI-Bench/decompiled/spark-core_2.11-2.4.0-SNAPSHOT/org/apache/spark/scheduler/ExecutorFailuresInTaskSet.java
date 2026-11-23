package org.apache.spark.scheduler;

import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.Tuple2$mcIJ$sp;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001=3Q!\u0001\u0002\u0001\u0005)\u0011\u0011$\u0012=fGV$xN\u001d$bS2,(/Z:J]R\u000b7o[*fi*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007\u0002\u0003\n\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\t9|G-Z\u0002\u0001+\u0005)\u0002C\u0001\f\u001a\u001d\taq#\u0003\u0002\u0019\u001b\u00051\u0001K]3eK\u001aL!AG\u000e\u0003\rM#(/\u001b8h\u0015\tAR\u0002\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u0016\u0003\u0015qw\u000eZ3!\u0011\u0015y\u0002\u0001\"\u0001!\u0003\u0019a\u0014N\\5u}Q\u0011\u0011e\t\t\u0003E\u0001i\u0011A\u0001\u0005\u0006%y\u0001\r!\u0006\u0005\bK\u0001\u0011\r\u0011\"\u0001'\u0003\u0001\"\u0018m]6U_\u001a\u000b\u0017\u000e\\;sK\u000e{WO\u001c;B]\u00124\u0015-\u001b7ve\u0016$\u0016.\\3\u0016\u0003\u001d\u0002B\u0001K\u00170e5\t\u0011F\u0003\u0002+W\u00059Q.\u001e;bE2,'B\u0001\u0017\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003]%\u0012q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0002\ra%\u0011\u0011'\u0004\u0002\u0004\u0013:$\b\u0003\u0002\u00074_UJ!\u0001N\u0007\u0003\rQ+\b\u000f\\33!\taa'\u0003\u00028\u001b\t!Aj\u001c8h\u0011\u0019I\u0004\u0001)A\u0005O\u0005\tC/Y:l)>4\u0015-\u001b7ve\u0016\u001cu.\u001e8u\u0003:$g)Y5mkJ,G+[7fA!)1\b\u0001C\u0001y\u0005\tR\u000f\u001d3bi\u0016<\u0016\u000e\u001e5GC&dWO]3\u0015\u0007u\u0002%\t\u0005\u0002\r}%\u0011q(\u0004\u0002\u0005+:LG\u000fC\u0003Bu\u0001\u0007q&A\u0005uCN\\\u0017J\u001c3fq\")1I\u000fa\u0001k\u0005Ya-Y5mkJ,G+[7f\u0011\u0015)\u0005\u0001\"\u0001G\u0003iqW/\\+oSF,X\rV1tWN<\u0016\u000e\u001e5GC&dWO]3t+\u0005y\u0003\"\u0002%\u0001\t\u0003I\u0015AE4fi:+X\u000eV1tW\u001a\u000b\u0017\u000e\\;sKN$\"a\f&\t\u000b-;\u0005\u0019A\u0018\u0002\u000b%tG-\u001a=\t\u000b5\u0003A\u0011\t(\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012!\u0006"
)
public class ExecutorFailuresInTaskSet {
   private final String node;
   private final HashMap<Object, Tuple2<Object, Object>> taskToFailureCountAndFailureTime;

   public String node() {
      return this.node;
   }

   public HashMap<Object, Tuple2<Object, Object>> taskToFailureCountAndFailureTime() {
      return this.taskToFailureCountAndFailureTime;
   }

   public void updateWithFailure(int taskIndex, long failureTime) {
      Tuple2 var5 = (Tuple2)this.taskToFailureCountAndFailureTime().getOrElse(BoxesRunTime.boxToInteger(taskIndex), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, Object> apply() {
            return new Tuple2$mcIJ$sp(0, -1L);
         }
      });
      if (var5 != null) {
         int prevFailureCount = var5._1$mcI$sp();
         long prevFailureTime = var5._2$mcJ$sp();
         Tuple2$mcIJ$sp var9 = new Tuple2$mcIJ$sp(prevFailureCount, prevFailureTime);
         int prevFailureCount = ((Tuple2)var9)._1$mcI$sp();
         long prevFailureTime = ((Tuple2)var9)._2$mcJ$sp();
         long newFailureTime = scala.math.package$.MODULE$.max(prevFailureTime, failureTime);
         this.taskToFailureCountAndFailureTime().update(BoxesRunTime.boxToInteger(taskIndex), new Tuple2$mcIJ$sp(prevFailureCount + 1, newFailureTime));
      } else {
         throw new MatchError(var5);
      }
   }

   public int numUniqueTasksWithFailures() {
      return this.taskToFailureCountAndFailureTime().size();
   }

   public int getNumTaskFailures(int index) {
      return ((Tuple2)this.taskToFailureCountAndFailureTime().getOrElse(BoxesRunTime.boxToInteger(index), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, Object> apply() {
            return new Tuple2$mcII$sp(0, 0);
         }
      }))._1$mcI$sp();
   }

   public String toString() {
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"numUniqueTasksWithFailures = ", "; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.numUniqueTasksWithFailures())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"tasksToFailureCount = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.taskToFailureCountAndFailureTime()}))).toString();
   }

   public ExecutorFailuresInTaskSet(String node) {
      this.node = node;
      this.taskToFailureCountAndFailureTime = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
   }
}
