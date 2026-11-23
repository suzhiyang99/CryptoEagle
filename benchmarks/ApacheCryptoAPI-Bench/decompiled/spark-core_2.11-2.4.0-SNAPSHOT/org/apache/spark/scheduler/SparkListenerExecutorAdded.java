package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.scheduler.cluster.ExecutorInfo;
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
   bytes = "\u0006\u0001\u0005Ud\u0001B\u0001\u0003\u0001.\u0011!d\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'/\u00113eK\u0012T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M)\u0001\u0001\u0004\n\u00173A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\u000b\u000e\u0003\tI!!\u0006\u0002\u0003%M\u0003\u0018M]6MSN$XM\\3s\u000bZ,g\u000e\u001e\t\u0003\u001b]I!\u0001\u0007\b\u0003\u000fA\u0013x\u000eZ;diB\u0011QBG\u0005\u000379\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\b\u0001\u0003\u0016\u0004%\tAH\u0001\u0005i&lW-F\u0001 !\ti\u0001%\u0003\u0002\"\u001d\t!Aj\u001c8h\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012!\u0002;j[\u0016\u0004\u0003\u0002C\u0013\u0001\u0005+\u0007I\u0011\u0001\u0014\u0002\u0015\u0015DXmY;u_JLE-F\u0001(!\tA3F\u0004\u0002\u000eS%\u0011!FD\u0001\u0007!J,G-\u001a4\n\u00051j#AB*ue&twM\u0003\u0002+\u001d!Aq\u0006\u0001B\tB\u0003%q%A\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\u0003\u0002C\u0019\u0001\u0005+\u0007I\u0011\u0001\u001a\u0002\u0019\u0015DXmY;u_JLeNZ8\u0016\u0003M\u0002\"\u0001N\u001c\u000e\u0003UR!A\u000e\u0002\u0002\u000f\rdWo\u001d;fe&\u0011\u0001(\u000e\u0002\r\u000bb,7-\u001e;pe&sgm\u001c\u0005\tu\u0001\u0011\t\u0012)A\u0005g\u0005iQ\r_3dkR|'/\u00138g_\u0002BQ\u0001\u0010\u0001\u0005\u0002u\na\u0001P5oSRtD\u0003\u0002 @\u0001\u0006\u0003\"a\u0005\u0001\t\u000buY\u0004\u0019A\u0010\t\u000b\u0015Z\u0004\u0019A\u0014\t\u000bEZ\u0004\u0019A\u001a\t\u000f\r\u0003\u0011\u0011!C\u0001\t\u0006!1m\u001c9z)\u0011qTIR$\t\u000fu\u0011\u0005\u0013!a\u0001?!9QE\u0011I\u0001\u0002\u00049\u0003bB\u0019C!\u0003\u0005\ra\r\u0005\b\u0013\u0002\t\n\u0011\"\u0001K\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\u0013\u0016\u0003?1[\u0013!\u0014\t\u0003\u001dNk\u0011a\u0014\u0006\u0003!F\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005Is\u0011AC1o]>$\u0018\r^5p]&\u0011Ak\u0014\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002,\u0001#\u0003%\taV\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005A&FA\u0014M\u0011\u001dQ\u0006!%A\u0005\u0002m\u000babY8qs\u0012\"WMZ1vYR$3'F\u0001]U\t\u0019D\nC\u0004_\u0001\u0005\u0005I\u0011I0\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005\u0001\u0007CA1g\u001b\u0005\u0011'BA2e\u0003\u0011a\u0017M\\4\u000b\u0003\u0015\fAA[1wC&\u0011AF\u0019\u0005\bQ\u0002\t\t\u0011\"\u0001j\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005Q\u0007CA\u0007l\u0013\tagBA\u0002J]RDqA\u001c\u0001\u0002\u0002\u0013\u0005q.\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005A\u001c\bCA\u0007r\u0013\t\u0011hBA\u0002B]fDq\u0001^7\u0002\u0002\u0003\u0007!.A\u0002yIEBqA\u001e\u0001\u0002\u0002\u0013\u0005s/A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005A\bcA=}a6\t!P\u0003\u0002|\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005uT(\u0001C%uKJ\fGo\u001c:\t\u0011}\u0004\u0011\u0011!C\u0001\u0003\u0003\t\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003\u0007\tI\u0001E\u0002\u000e\u0003\u000bI1!a\u0002\u000f\u0005\u001d\u0011un\u001c7fC:Dq\u0001\u001e@\u0002\u0002\u0003\u0007\u0001\u000fC\u0005\u0002\u000e\u0001\t\t\u0011\"\u0011\u0002\u0010\u0005A\u0001.Y:i\u0007>$W\rF\u0001k\u0011%\t\u0019\u0002AA\u0001\n\u0003\n)\"\u0001\u0005u_N#(/\u001b8h)\u0005\u0001\u0007\"CA\r\u0001\u0005\u0005I\u0011IA\u000e\u0003\u0019)\u0017/^1mgR!\u00111AA\u000f\u0011!!\u0018qCA\u0001\u0002\u0004\u0001\bf\u0001\u0001\u0002\"A!\u00111EA\u0014\u001b\t\t)C\u0003\u0002S\t%!\u0011\u0011FA\u0013\u00051!UM^3m_B,'/\u00119j\u000f%\tiCAA\u0001\u0012\u0003\ty#\u0001\u000eTa\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:BI\u0012,G\rE\u0002\u0014\u0003c1\u0001\"\u0001\u0002\u0002\u0002#\u0005\u00111G\n\u0006\u0003c\t)$\u0007\t\t\u0003o\tidH\u00144}5\u0011\u0011\u0011\b\u0006\u0004\u0003wq\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003\u007f\tIDA\tBEN$(/Y2u\rVt7\r^5p]NBq\u0001PA\u0019\t\u0003\t\u0019\u0005\u0006\u0002\u00020!Q\u00111CA\u0019\u0003\u0003%)%!\u0006\t\u0015\u0005%\u0013\u0011GA\u0001\n\u0003\u000bY%A\u0003baBd\u0017\u0010F\u0004?\u0003\u001b\ny%!\u0015\t\ru\t9\u00051\u0001 \u0011\u0019)\u0013q\ta\u0001O!1\u0011'a\u0012A\u0002MB!\"!\u0016\u00022\u0005\u0005I\u0011QA,\u0003\u001d)h.\u00199qYf$B!!\u0017\u0002fA)Q\"a\u0017\u0002`%\u0019\u0011Q\f\b\u0003\r=\u0003H/[8o!\u0019i\u0011\u0011M\u0010(g%\u0019\u00111\r\b\u0003\rQ+\b\u000f\\34\u0011%\t9'a\u0015\u0002\u0002\u0003\u0007a(A\u0002yIAB!\"a\u001b\u00022\u0005\u0005I\u0011BA7\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005=\u0004cA1\u0002r%\u0019\u00111\u000f2\u0003\r=\u0013'.Z2u\u0001"
)
public class SparkListenerExecutorAdded implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String executorId;
   private final ExecutorInfo executorInfo;

   public static Option<Tuple3<Object, String, ExecutorInfo>> unapply(SparkListenerExecutorAdded var0) {
      return SparkListenerExecutorAdded$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorAdded apply(long var0, String var2, ExecutorInfo var3) {
      return SparkListenerExecutorAdded$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, String, ExecutorInfo>, SparkListenerExecutorAdded> tupled() {
      return SparkListenerExecutorAdded$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<ExecutorInfo, SparkListenerExecutorAdded>>> curried() {
      return SparkListenerExecutorAdded$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public String executorId() {
      return this.executorId;
   }

   public ExecutorInfo executorInfo() {
      return this.executorInfo;
   }

   public SparkListenerExecutorAdded copy(long time, String executorId, ExecutorInfo executorInfo) {
      return new SparkListenerExecutorAdded(time, executorId, executorInfo);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.executorId();
   }

   public ExecutorInfo copy$default$3() {
      return this.executorInfo();
   }

   public String productPrefix() {
      return "SparkListenerExecutorAdded";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 1:
            var10000 = this.executorId();
            break;
         case 2:
            var10000 = this.executorInfo();
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
      return x$1 instanceof SparkListenerExecutorAdded;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorInfo()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label64: {
            boolean var3;
            if (x$1 instanceof SparkListenerExecutorAdded) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  SparkListenerExecutorAdded var4 = (SparkListenerExecutorAdded)x$1;
                  if (this.time() == var4.time()) {
                     label55: {
                        String var10000 = this.executorId();
                        String var5 = var4.executorId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label55;
                        }

                        ExecutorInfo var7 = this.executorInfo();
                        ExecutorInfo var6 = var4.executorInfo();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label55;
                           }
                        } else if (!var7.equals(var6)) {
                           break label55;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label46;
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label64;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public SparkListenerExecutorAdded(long time, String executorId, ExecutorInfo executorInfo) {
      this.time = time;
      this.executorId = executorId;
      this.executorInfo = executorInfo;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
