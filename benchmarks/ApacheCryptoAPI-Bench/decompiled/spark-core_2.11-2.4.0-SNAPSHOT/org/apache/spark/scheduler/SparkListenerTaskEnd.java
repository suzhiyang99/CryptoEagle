package org.apache.spark.scheduler;

import javax.annotation.Nullable;
import org.apache.spark.TaskEndReason;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.executor.TaskMetrics;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple6;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005=g\u0001B\u0001\u0003\u0001.\u0011Ac\u00159be.d\u0015n\u001d;f]\u0016\u0014H+Y:l\u000b:$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\u000fM$\u0018mZ3JIV\tq\u0004\u0005\u0002\u000eA%\u0011\u0011E\u0004\u0002\u0004\u0013:$\b\u0002C\u0012\u0001\u0005#\u0005\u000b\u0011B\u0010\u0002\u0011M$\u0018mZ3JI\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAH\u0001\u000fgR\fw-Z!ui\u0016l\u0007\u000f^%e\u0011!9\u0003A!E!\u0002\u0013y\u0012aD:uC\u001e,\u0017\t\u001e;f[B$\u0018\n\u001a\u0011\t\u0011%\u0002!Q3A\u0005\u0002)\n\u0001\u0002^1tWRK\b/Z\u000b\u0002WA\u0011Af\f\b\u0003\u001b5J!A\f\b\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0014G\u0001\u0004TiJLgn\u001a\u0006\u0003]9A\u0001b\r\u0001\u0003\u0012\u0003\u0006IaK\u0001\ni\u0006\u001c8\u000eV=qK\u0002B\u0001\"\u000e\u0001\u0003\u0016\u0004%\tAN\u0001\u0007e\u0016\f7o\u001c8\u0016\u0003]\u0002\"\u0001O\u001d\u000e\u0003\u0011I!A\u000f\u0003\u0003\u001bQ\u000b7o[#oIJ+\u0017m]8o\u0011!a\u0004A!E!\u0002\u00139\u0014a\u0002:fCN|g\u000e\t\u0005\t}\u0001\u0011)\u001a!C\u0001\u007f\u0005AA/Y:l\u0013:4w.F\u0001A!\t\u0019\u0012)\u0003\u0002C\u0005\tAA+Y:l\u0013:4w\u000e\u0003\u0005E\u0001\tE\t\u0015!\u0003A\u0003%!\u0018m]6J]\u001a|\u0007\u0005\u0003\u0005G\u0001\tU\r\u0011\"\u0001H\u0003-!\u0018m]6NKR\u0014\u0018nY:\u0016\u0003!\u0003\"!\u0013'\u000e\u0003)S!a\u0013\u0003\u0002\u0011\u0015DXmY;u_JL!!\u0014&\u0003\u0017Q\u000b7o['fiJL7m\u001d\u0005\t\u001f\u0002\u0011\t\u0012)A\u0005\u0011\u0006aA/Y:l\u001b\u0016$(/[2tA!)\u0011\u000b\u0001C\u0001%\u00061A(\u001b8jiz\"ra\u0015+V-^C\u0016\f\u0005\u0002\u0014\u0001!)Q\u0004\u0015a\u0001?!)Q\u0005\u0015a\u0001?!)\u0011\u0006\u0015a\u0001W!)Q\u0007\u0015a\u0001o!)a\b\u0015a\u0001\u0001\")a\t\u0015a\u0001\u0011\"\u0012\u0011l\u0017\t\u00039\u0006l\u0011!\u0018\u0006\u0003=~\u000b!\"\u00198o_R\fG/[8o\u0015\u0005\u0001\u0017!\u00026bm\u0006D\u0018B\u00012^\u0005!qU\u000f\u001c7bE2,\u0007b\u00023\u0001\u0003\u0003%\t!Z\u0001\u0005G>\u0004\u0018\u0010F\u0004TM\u001eD\u0017N[6\t\u000fu\u0019\u0007\u0013!a\u0001?!9Qe\u0019I\u0001\u0002\u0004y\u0002bB\u0015d!\u0003\u0005\ra\u000b\u0005\bk\r\u0004\n\u00111\u00018\u0011\u001dq4\r%AA\u0002\u0001CqAR2\u0011\u0002\u0003\u0007\u0001\nC\u0004n\u0001E\u0005I\u0011\u00018\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tqN\u000b\u0002 a.\n\u0011\u000f\u0005\u0002sm6\t1O\u0003\u0002uk\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003=:I!a^:\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004z\u0001E\u0005I\u0011\u00018\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%e!91\u0010AI\u0001\n\u0003a\u0018AD2paf$C-\u001a4bk2$HeM\u000b\u0002{*\u00121\u0006\u001d\u0005\t\u007f\u0002\t\n\u0011\"\u0001\u0002\u0002\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\"TCAA\u0002U\t9\u0004\u000fC\u0005\u0002\b\u0001\t\n\u0011\"\u0001\u0002\n\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012*TCAA\u0006U\t\u0001\u0005\u000fC\u0005\u0002\u0010\u0001\t\n\u0011\"\u0001\u0002\u0012\u0005q1m\u001c9zI\u0011,g-Y;mi\u00122TCAA\nU\tA\u0005\u000fC\u0005\u0002\u0018\u0001\t\t\u0011\"\u0011\u0002\u001a\u0005i\u0001O]8ek\u000e$\bK]3gSb,\"!a\u0007\u0011\t\u0005u\u0011qE\u0007\u0003\u0003?QA!!\t\u0002$\u0005!A.\u00198h\u0015\t\t)#\u0001\u0003kCZ\f\u0017b\u0001\u0019\u0002 !A\u00111\u0006\u0001\u0002\u0002\u0013\u0005a$\u0001\u0007qe>$Wo\u0019;Be&$\u0018\u0010C\u0005\u00020\u0001\t\t\u0011\"\u0001\u00022\u0005q\u0001O]8ek\u000e$X\t\\3nK:$H\u0003BA\u001a\u0003s\u00012!DA\u001b\u0013\r\t9D\u0004\u0002\u0004\u0003:L\b\"CA\u001e\u0003[\t\t\u00111\u0001 \u0003\rAH%\r\u0005\n\u0003\u007f\u0001\u0011\u0011!C!\u0003\u0003\nq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003\u0007\u0002b!!\u0012\u0002L\u0005MRBAA$\u0015\r\tIED\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA'\u0003\u000f\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003#\u0002\u0011\u0011!C\u0001\u0003'\n\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003+\nY\u0006E\u0002\u000e\u0003/J1!!\u0017\u000f\u0005\u001d\u0011un\u001c7fC:D!\"a\u000f\u0002P\u0005\u0005\t\u0019AA\u001a\u0011%\ty\u0006AA\u0001\n\u0003\n\t'\u0001\u0005iCND7i\u001c3f)\u0005y\u0002\"CA3\u0001\u0005\u0005I\u0011IA4\u0003!!xn\u0015;sS:<GCAA\u000e\u0011%\tY\u0007AA\u0001\n\u0003\ni'\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003+\ny\u0007\u0003\u0006\u0002<\u0005%\u0014\u0011!a\u0001\u0003gA3\u0001AA:!\u0011\t)(!\u001f\u000e\u0005\u0005]$B\u00010\u0005\u0013\u0011\tY(a\u001e\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\b\u0013\u0005}$!!A\t\u0002\u0005\u0005\u0015\u0001F*qCJ\\G*[:uK:,'\u000fV1tW\u0016sG\rE\u0002\u0014\u0003\u00073\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011QQ\n\u0006\u0003\u0007\u000b9)\u0007\t\f\u0003\u0013\u000byiH\u0010,o\u0001C5+\u0004\u0002\u0002\f*\u0019\u0011Q\u0012\b\u0002\u000fI,h\u000e^5nK&!\u0011\u0011SAF\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gN\u000e\u0005\b#\u0006\rE\u0011AAK)\t\t\t\t\u0003\u0006\u0002f\u0005\r\u0015\u0011!C#\u0003OB!\"a'\u0002\u0004\u0006\u0005I\u0011QAO\u0003\u0015\t\u0007\u000f\u001d7z)5\u0019\u0016qTAQ\u0003G\u000b)+a*\u0002*\"1Q$!'A\u0002}Aa!JAM\u0001\u0004y\u0002BB\u0015\u0002\u001a\u0002\u00071\u0006\u0003\u00046\u00033\u0003\ra\u000e\u0005\u0007}\u0005e\u0005\u0019\u0001!\t\r\u0019\u000bI\n1\u0001IQ\r\tIk\u0017\u0005\u000b\u0003_\u000b\u0019)!A\u0005\u0002\u0006E\u0016aB;oCB\u0004H.\u001f\u000b\u0005\u0003g\u000by\fE\u0003\u000e\u0003k\u000bI,C\u0002\u00028:\u0011aa\u00149uS>t\u0007#C\u0007\u0002<~y2f\u000e!I\u0013\r\tiL\u0004\u0002\u0007)V\u0004H.\u001a\u001c\t\u0013\u0005\u0005\u0017QVA\u0001\u0002\u0004\u0019\u0016a\u0001=%a!Q\u0011QYAB\u0003\u0003%I!a2\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0013\u0004B!!\b\u0002L&!\u0011QZA\u0010\u0005\u0019y%M[3di\u0002"
)
public class SparkListenerTaskEnd implements SparkListenerEvent, Product, Serializable {
   private final int stageId;
   private final int stageAttemptId;
   private final String taskType;
   private final TaskEndReason reason;
   private final TaskInfo taskInfo;
   private final TaskMetrics taskMetrics;

   public static Option<Tuple6<Object, Object, String, TaskEndReason, TaskInfo, TaskMetrics>> unapply(SparkListenerTaskEnd var0) {
      return SparkListenerTaskEnd$.MODULE$.unapply(var0);
   }

   public static SparkListenerTaskEnd apply(int var0, int var1, String var2, TaskEndReason var3, TaskInfo var4, @Nullable TaskMetrics var5) {
      return SparkListenerTaskEnd$.MODULE$.apply(var0, var1, var2, var3, var4, var5);
   }

   public static Function1<Tuple6<Object, Object, String, TaskEndReason, TaskInfo, TaskMetrics>, SparkListenerTaskEnd> tupled() {
      return SparkListenerTaskEnd$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<String, Function1<TaskEndReason, Function1<TaskInfo, Function1<TaskMetrics, SparkListenerTaskEnd>>>>>> curried() {
      return SparkListenerTaskEnd$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public String taskType() {
      return this.taskType;
   }

   public TaskEndReason reason() {
      return this.reason;
   }

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public TaskMetrics taskMetrics() {
      return this.taskMetrics;
   }

   public SparkListenerTaskEnd copy(int stageId, int stageAttemptId, String taskType, TaskEndReason reason, TaskInfo taskInfo, TaskMetrics taskMetrics) {
      return new SparkListenerTaskEnd(stageId, stageAttemptId, taskType, reason, taskInfo, taskMetrics);
   }

   public int copy$default$1() {
      return this.stageId();
   }

   public int copy$default$2() {
      return this.stageAttemptId();
   }

   public String copy$default$3() {
      return this.taskType();
   }

   public TaskEndReason copy$default$4() {
      return this.reason();
   }

   public TaskInfo copy$default$5() {
      return this.taskInfo();
   }

   public TaskMetrics copy$default$6() {
      return this.taskMetrics();
   }

   public String productPrefix() {
      return "SparkListenerTaskEnd";
   }

   public int productArity() {
      return 6;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.stageId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.stageAttemptId());
            break;
         case 2:
            var10000 = this.taskType();
            break;
         case 3:
            var10000 = this.reason();
            break;
         case 4:
            var10000 = this.taskInfo();
            break;
         case 5:
            var10000 = this.taskMetrics();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerTaskEnd;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.stageId());
      var1 = Statics.mix(var1, this.stageAttemptId());
      var1 = Statics.mix(var1, Statics.anyHash(this.taskType()));
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      var1 = Statics.mix(var1, Statics.anyHash(this.taskInfo()));
      var1 = Statics.mix(var1, Statics.anyHash(this.taskMetrics()));
      return Statics.finalizeHash(var1, 6);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var13;
      if (this != x$1) {
         label84: {
            boolean var3;
            if (x$1 instanceof SparkListenerTaskEnd) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label66: {
                  SparkListenerTaskEnd var4 = (SparkListenerTaskEnd)x$1;
                  if (this.stageId() == var4.stageId() && this.stageAttemptId() == var4.stageAttemptId()) {
                     label75: {
                        String var10000 = this.taskType();
                        String var5 = var4.taskType();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label75;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label75;
                        }

                        TaskEndReason var9 = this.reason();
                        TaskEndReason var6 = var4.reason();
                        if (var9 == null) {
                           if (var6 != null) {
                              break label75;
                           }
                        } else if (!var9.equals(var6)) {
                           break label75;
                        }

                        TaskInfo var10 = this.taskInfo();
                        TaskInfo var7 = var4.taskInfo();
                        if (var10 == null) {
                           if (var7 != null) {
                              break label75;
                           }
                        } else if (!var10.equals(var7)) {
                           break label75;
                        }

                        TaskMetrics var11 = this.taskMetrics();
                        TaskMetrics var8 = var4.taskMetrics();
                        if (var11 == null) {
                           if (var8 != null) {
                              break label75;
                           }
                        } else if (!var11.equals(var8)) {
                           break label75;
                        }

                        if (var4.canEqual(this)) {
                           var13 = true;
                           break label66;
                        }
                     }
                  }

                  var13 = false;
               }

               if (var13) {
                  break label84;
               }
            }

            var13 = false;
            return var13;
         }
      }

      var13 = true;
      return var13;
   }

   public SparkListenerTaskEnd(int stageId, int stageAttemptId, String taskType, TaskEndReason reason, TaskInfo taskInfo, @Nullable TaskMetrics taskMetrics) {
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.taskType = taskType;
      this.reason = reason;
      this.taskInfo = taskInfo;
      this.taskMetrics = taskMetrics;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
