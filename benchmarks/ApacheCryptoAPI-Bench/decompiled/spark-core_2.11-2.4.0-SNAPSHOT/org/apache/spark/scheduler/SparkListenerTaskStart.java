package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Uc\u0001B\u0001\u0003\u0001.\u0011ac\u00159be.d\u0015n\u001d;f]\u0016\u0014H+Y:l'R\f'\u000f\u001e\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0019I1\u0012\u0004\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'Qi\u0011AA\u0005\u0003+\t\u0011!c\u00159be.d\u0015n\u001d;f]\u0016\u0014XI^3oiB\u0011QbF\u0005\u000319\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e5%\u00111D\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t;\u0001\u0011)\u001a!C\u0001=\u000591\u000f^1hK&#W#A\u0010\u0011\u00055\u0001\u0013BA\u0011\u000f\u0005\rIe\u000e\u001e\u0005\tG\u0001\u0011\t\u0012)A\u0005?\u0005A1\u000f^1hK&#\u0007\u0005\u0003\u0005&\u0001\tU\r\u0011\"\u0001\u001f\u00039\u0019H/Y4f\u0003R$X-\u001c9u\u0013\u0012D\u0001b\n\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0010gR\fw-Z!ui\u0016l\u0007\u000f^%eA!A\u0011\u0006\u0001BK\u0002\u0013\u0005!&\u0001\u0005uCN\\\u0017J\u001c4p+\u0005Y\u0003CA\n-\u0013\ti#A\u0001\u0005UCN\\\u0017J\u001c4p\u0011!y\u0003A!E!\u0002\u0013Y\u0013!\u0003;bg.LeNZ8!\u0011\u0015\t\u0004\u0001\"\u00013\u0003\u0019a\u0014N\\5u}Q!1\u0007N\u001b7!\t\u0019\u0002\u0001C\u0003\u001ea\u0001\u0007q\u0004C\u0003&a\u0001\u0007q\u0004C\u0003*a\u0001\u00071\u0006C\u00049\u0001\u0005\u0005I\u0011A\u001d\u0002\t\r|\u0007/\u001f\u000b\u0005giZD\bC\u0004\u001eoA\u0005\t\u0019A\u0010\t\u000f\u0015:\u0004\u0013!a\u0001?!9\u0011f\u000eI\u0001\u0002\u0004Y\u0003b\u0002 \u0001#\u0003%\taP\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0001%FA\u0010BW\u0005\u0011\u0005CA\"I\u001b\u0005!%BA#G\u0003%)hn\u00195fG.,GM\u0003\u0002H\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005%#%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"91\nAI\u0001\n\u0003y\u0014AD2paf$C-\u001a4bk2$HE\r\u0005\b\u001b\u0002\t\n\u0011\"\u0001O\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012a\u0014\u0016\u0003W\u0005Cq!\u0015\u0001\u0002\u0002\u0013\u0005#+A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002'B\u0011A+W\u0007\u0002+*\u0011akV\u0001\u0005Y\u0006twMC\u0001Y\u0003\u0011Q\u0017M^1\n\u0005i+&AB*ue&tw\rC\u0004]\u0001\u0005\u0005I\u0011\u0001\u0010\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000fy\u0003\u0011\u0011!C\u0001?\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u00011d!\ti\u0011-\u0003\u0002c\u001d\t\u0019\u0011I\\=\t\u000f\u0011l\u0016\u0011!a\u0001?\u0005\u0019\u0001\u0010J\u0019\t\u000f\u0019\u0004\u0011\u0011!C!O\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001i!\rIG\u000eY\u0007\u0002U*\u00111ND\u0001\u000bG>dG.Z2uS>t\u0017BA7k\u0005!IE/\u001a:bi>\u0014\bbB8\u0001\u0003\u0003%\t\u0001]\u0001\tG\u0006tW)];bYR\u0011\u0011\u000f\u001e\t\u0003\u001bIL!a\u001d\b\u0003\u000f\t{w\u000e\\3b]\"9AM\\A\u0001\u0002\u0004\u0001\u0007b\u0002<\u0001\u0003\u0003%\te^\u0001\tQ\u0006\u001c\bnQ8eKR\tq\u0004C\u0004z\u0001\u0005\u0005I\u0011\t>\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012a\u0015\u0005\by\u0002\t\t\u0011\"\u0011~\u0003\u0019)\u0017/^1mgR\u0011\u0011O \u0005\bIn\f\t\u00111\u0001aQ\r\u0001\u0011\u0011\u0001\t\u0005\u0003\u0007\t9!\u0004\u0002\u0002\u0006)\u0011q\tB\u0005\u0005\u0003\u0013\t)A\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018nB\u0005\u0002\u000e\t\t\t\u0011#\u0001\u0002\u0010\u000512\u000b]1sW2K7\u000f^3oKJ$\u0016m]6Ti\u0006\u0014H\u000fE\u0002\u0014\u0003#1\u0001\"\u0001\u0002\u0002\u0002#\u0005\u00111C\n\u0006\u0003#\t)\"\u0007\t\t\u0003/\tibH\u0010,g5\u0011\u0011\u0011\u0004\u0006\u0004\u00037q\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003?\tIBA\tBEN$(/Y2u\rVt7\r^5p]NBq!MA\t\t\u0003\t\u0019\u0003\u0006\u0002\u0002\u0010!A\u00110!\u0005\u0002\u0002\u0013\u0015#\u0010\u0003\u0006\u0002*\u0005E\u0011\u0011!CA\u0003W\tQ!\u00199qYf$raMA\u0017\u0003_\t\t\u0004\u0003\u0004\u001e\u0003O\u0001\ra\b\u0005\u0007K\u0005\u001d\u0002\u0019A\u0010\t\r%\n9\u00031\u0001,\u0011)\t)$!\u0005\u0002\u0002\u0013\u0005\u0015qG\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tI$!\u0012\u0011\u000b5\tY$a\u0010\n\u0007\u0005ubB\u0001\u0004PaRLwN\u001c\t\u0007\u001b\u0005\u0005sdH\u0016\n\u0007\u0005\rcB\u0001\u0004UkBdWm\r\u0005\n\u0003\u000f\n\u0019$!AA\u0002M\n1\u0001\u001f\u00131\u0011)\tY%!\u0005\u0002\u0002\u0013%\u0011QJ\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002PA\u0019A+!\u0015\n\u0007\u0005MSK\u0001\u0004PE*,7\r\u001e"
)
public class SparkListenerTaskStart implements SparkListenerEvent, Product, Serializable {
   private final int stageId;
   private final int stageAttemptId;
   private final TaskInfo taskInfo;

   public static Option<Tuple3<Object, Object, TaskInfo>> unapply(SparkListenerTaskStart var0) {
      return SparkListenerTaskStart$.MODULE$.unapply(var0);
   }

   public static SparkListenerTaskStart apply(int var0, int var1, TaskInfo var2) {
      return SparkListenerTaskStart$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, TaskInfo>, SparkListenerTaskStart> tupled() {
      return SparkListenerTaskStart$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<TaskInfo, SparkListenerTaskStart>>> curried() {
      return SparkListenerTaskStart$.MODULE$.curried();
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

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public SparkListenerTaskStart copy(int stageId, int stageAttemptId, TaskInfo taskInfo) {
      return new SparkListenerTaskStart(stageId, stageAttemptId, taskInfo);
   }

   public int copy$default$1() {
      return this.stageId();
   }

   public int copy$default$2() {
      return this.stageAttemptId();
   }

   public TaskInfo copy$default$3() {
      return this.taskInfo();
   }

   public String productPrefix() {
      return "SparkListenerTaskStart";
   }

   public int productArity() {
      return 3;
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
            var10000 = this.taskInfo();
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
      return x$1 instanceof SparkListenerTaskStart;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.stageId());
      var1 = Statics.mix(var1, this.stageAttemptId());
      var1 = Statics.mix(var1, Statics.anyHash(this.taskInfo()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof SparkListenerTaskStart) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  SparkListenerTaskStart var4 = (SparkListenerTaskStart)x$1;
                  if (this.stageId() == var4.stageId() && this.stageAttemptId() == var4.stageAttemptId()) {
                     label36: {
                        TaskInfo var10000 = this.taskInfo();
                        TaskInfo var5 = var4.taskInfo();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label36;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label36;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label39;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerTaskStart(int stageId, int stageAttemptId, TaskInfo taskInfo) {
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.taskInfo = taskInfo;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
