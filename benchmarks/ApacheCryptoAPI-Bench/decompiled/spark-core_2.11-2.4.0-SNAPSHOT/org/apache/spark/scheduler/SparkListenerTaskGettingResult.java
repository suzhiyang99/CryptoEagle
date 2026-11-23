package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001db\u0001B\u0001\u0003\u0001.\u0011ad\u00159be.d\u0015n\u001d;f]\u0016\u0014H+Y:l\u000f\u0016$H/\u001b8h%\u0016\u001cX\u000f\u001c;\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0006\u00011\u0011b#\u0007\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!AE*qCJ\\G*[:uK:,'/\u0012<f]R\u0004\"!D\f\n\u0005aq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001biI!a\u0007\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011u\u0001!Q3A\u0005\u0002y\t\u0001\u0002^1tW&sgm\\\u000b\u0002?A\u00111\u0003I\u0005\u0003C\t\u0011\u0001\u0002V1tW&sgm\u001c\u0005\tG\u0001\u0011\t\u0012)A\u0005?\u0005IA/Y:l\u0013:4w\u000e\t\u0005\u0006K\u0001!\tAJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u001dB\u0003CA\n\u0001\u0011\u0015iB\u00051\u0001 \u0011\u001dQ\u0003!!A\u0005\u0002-\nAaY8qsR\u0011q\u0005\f\u0005\b;%\u0002\n\u00111\u0001 \u0011\u001dq\u0003!%A\u0005\u0002=\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00011U\ty\u0012gK\u00013!\t\u0019\u0004(D\u00015\u0015\t)d'A\u0005v]\u000eDWmY6fI*\u0011qGD\u0001\u000bC:tw\u000e^1uS>t\u0017BA\u001d5\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bw\u0001\t\t\u0011\"\u0011=\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tQ\b\u0005\u0002?\u00076\tqH\u0003\u0002A\u0003\u0006!A.\u00198h\u0015\u0005\u0011\u0015\u0001\u00026bm\u0006L!\u0001R \u0003\rM#(/\u001b8h\u0011\u001d1\u0005!!A\u0005\u0002\u001d\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012\u0001\u0013\t\u0003\u001b%K!A\u0013\b\u0003\u0007%sG\u000fC\u0004M\u0001\u0005\u0005I\u0011A'\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011a*\u0015\t\u0003\u001b=K!\u0001\u0015\b\u0003\u0007\u0005s\u0017\u0010C\u0004S\u0017\u0006\u0005\t\u0019\u0001%\u0002\u0007a$\u0013\u0007C\u0004U\u0001\u0005\u0005I\u0011I+\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012A\u0016\t\u0004/jsU\"\u0001-\u000b\u0005es\u0011AC2pY2,7\r^5p]&\u00111\f\u0017\u0002\t\u0013R,'/\u0019;pe\"9Q\fAA\u0001\n\u0003q\u0016\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005}\u0013\u0007CA\u0007a\u0013\t\tgBA\u0004C_>dW-\u00198\t\u000fIc\u0016\u0011!a\u0001\u001d\"9A\rAA\u0001\n\u0003*\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003!Cqa\u001a\u0001\u0002\u0002\u0013\u0005\u0003.\u0001\u0005u_N#(/\u001b8h)\u0005i\u0004b\u00026\u0001\u0003\u0003%\te[\u0001\u0007KF,\u0018\r\\:\u0015\u0005}c\u0007b\u0002*j\u0003\u0003\u0005\rA\u0014\u0015\u0003\u00019\u0004\"a\\9\u000e\u0003AT!a\u000e\u0003\n\u0005I\u0004(\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwa\u0002;\u0003\u0003\u0003E\t!^\u0001\u001f'B\f'o\u001b'jgR,g.\u001a:UCN\\w)\u001a;uS:<'+Z:vYR\u0004\"a\u0005<\u0007\u000f\u0005\u0011\u0011\u0011!E\u0001oN\u0019a\u000f_\r\u0011\tedxdJ\u0007\u0002u*\u00111PD\u0001\beVtG/[7f\u0013\ti(PA\tBEN$(/Y2u\rVt7\r^5p]FBQ!\n<\u0005\u0002}$\u0012!\u001e\u0005\bOZ\f\t\u0011\"\u0012i\u0011%\t)A^A\u0001\n\u0003\u000b9!A\u0003baBd\u0017\u0010F\u0002(\u0003\u0013Aa!HA\u0002\u0001\u0004y\u0002\"CA\u0007m\u0006\u0005I\u0011QA\b\u0003\u001d)h.\u00199qYf$B!!\u0005\u0002\u0018A!Q\"a\u0005 \u0013\r\t)B\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0013\u0005e\u00111BA\u0001\u0002\u00049\u0013a\u0001=%a!I\u0011Q\u0004<\u0002\u0002\u0013%\u0011qD\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002\"A\u0019a(a\t\n\u0007\u0005\u0015rH\u0001\u0004PE*,7\r\u001e"
)
public class SparkListenerTaskGettingResult implements SparkListenerEvent, Product, Serializable {
   private final TaskInfo taskInfo;

   public static Option<TaskInfo> unapply(SparkListenerTaskGettingResult var0) {
      return SparkListenerTaskGettingResult$.MODULE$.unapply(var0);
   }

   public static SparkListenerTaskGettingResult apply(TaskInfo var0) {
      return SparkListenerTaskGettingResult$.MODULE$.apply(var0);
   }

   public static <A> Function1<TaskInfo, A> andThen(Function1<SparkListenerTaskGettingResult, A> var0) {
      return SparkListenerTaskGettingResult$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerTaskGettingResult> compose(Function1<A, TaskInfo> var0) {
      return SparkListenerTaskGettingResult$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public SparkListenerTaskGettingResult copy(TaskInfo taskInfo) {
      return new SparkListenerTaskGettingResult(taskInfo);
   }

   public TaskInfo copy$default$1() {
      return this.taskInfo();
   }

   public String productPrefix() {
      return "SparkListenerTaskGettingResult";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.taskInfo();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerTaskGettingResult;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof SparkListenerTaskGettingResult) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SparkListenerTaskGettingResult var4 = (SparkListenerTaskGettingResult)x$1;
                     TaskInfo var10000 = this.taskInfo();
                     TaskInfo var5 = var4.taskInfo();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerTaskGettingResult(TaskInfo taskInfo) {
      this.taskInfo = taskInfo;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
