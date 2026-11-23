package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ec\u0001B\u0001\u0003\u0001.\u0011!e\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'/\u00168cY\u0006\u001c7\u000e\\5ti\u0016$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\tQLW.Z\u000b\u0002?A\u0011Q\u0002I\u0005\u0003C9\u0011A\u0001T8oO\"A1\u0005\u0001B\tB\u0003%q$A\u0003uS6,\u0007\u0005\u0003\u0005&\u0001\tU\r\u0011\"\u0001'\u0003))\u00070Z2vi>\u0014\u0018\nZ\u000b\u0002OA\u0011\u0001f\u000b\b\u0003\u001b%J!A\u000b\b\u0002\rA\u0013X\rZ3g\u0013\taSF\u0001\u0004TiJLgn\u001a\u0006\u0003U9A\u0001b\f\u0001\u0003\u0012\u0003\u0006IaJ\u0001\fKb,7-\u001e;pe&#\u0007\u0005C\u00032\u0001\u0011\u0005!'\u0001\u0004=S:LGO\u0010\u000b\u0004gQ*\u0004CA\n\u0001\u0011\u0015i\u0002\u00071\u0001 \u0011\u0015)\u0003\u00071\u0001(\u0011\u001d9\u0004!!A\u0005\u0002a\nAaY8qsR\u00191'\u000f\u001e\t\u000fu1\u0004\u0013!a\u0001?!9QE\u000eI\u0001\u0002\u00049\u0003b\u0002\u001f\u0001#\u0003%\t!P\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005q$FA\u0010@W\u0005\u0001\u0005CA!G\u001b\u0005\u0011%BA\"E\u0003%)hn\u00195fG.,GM\u0003\u0002F\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u001d\u0013%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9\u0011\nAI\u0001\n\u0003Q\u0015AD2paf$C-\u001a4bk2$HEM\u000b\u0002\u0017*\u0012qe\u0010\u0005\b\u001b\u0002\t\t\u0011\"\u0011O\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tq\n\u0005\u0002Q+6\t\u0011K\u0003\u0002S'\u0006!A.\u00198h\u0015\u0005!\u0016\u0001\u00026bm\u0006L!\u0001L)\t\u000f]\u0003\u0011\u0011!C\u00011\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t\u0011\f\u0005\u0002\u000e5&\u00111L\u0004\u0002\u0004\u0013:$\bbB/\u0001\u0003\u0003%\tAX\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\ty&\r\u0005\u0002\u000eA&\u0011\u0011M\u0004\u0002\u0004\u0003:L\bbB2]\u0003\u0003\u0005\r!W\u0001\u0004q\u0012\n\u0004bB3\u0001\u0003\u0003%\tEZ\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\tq\rE\u0002iW~k\u0011!\u001b\u0006\u0003U:\t!bY8mY\u0016\u001cG/[8o\u0013\ta\u0017N\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dq\u0007!!A\u0005\u0002=\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003aN\u0004\"!D9\n\u0005It!a\u0002\"p_2,\u0017M\u001c\u0005\bG6\f\t\u00111\u0001`\u0011\u001d)\b!!A\u0005BY\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00023\"9\u0001\u0010AA\u0001\n\u0003J\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003=Cqa\u001f\u0001\u0002\u0002\u0013\u0005C0\u0001\u0004fcV\fGn\u001d\u000b\u0003avDqa\u0019>\u0002\u0002\u0003\u0007q\f\u000b\u0002\u0001\u007fB!\u0011\u0011AA\u0003\u001b\t\t\u0019A\u0003\u0002F\t%!\u0011qAA\u0002\u00051!UM^3m_B,'/\u00119j\u000f%\tYAAA\u0001\u0012\u0003\ti!\u0001\u0012Ta\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:V]\nd\u0017mY6mSN$X\r\u001a\t\u0004'\u0005=a\u0001C\u0001\u0003\u0003\u0003E\t!!\u0005\u0014\u000b\u0005=\u00111C\r\u0011\u000f\u0005U\u00111D\u0010(g5\u0011\u0011q\u0003\u0006\u0004\u00033q\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003;\t9BA\tBEN$(/Y2u\rVt7\r^5p]JBq!MA\b\t\u0003\t\t\u0003\u0006\u0002\u0002\u000e!A\u00010a\u0004\u0002\u0002\u0013\u0015\u0013\u0010\u0003\u0006\u0002(\u0005=\u0011\u0011!CA\u0003S\tQ!\u00199qYf$RaMA\u0016\u0003[Aa!HA\u0013\u0001\u0004y\u0002BB\u0013\u0002&\u0001\u0007q\u0005\u0003\u0006\u00022\u0005=\u0011\u0011!CA\u0003g\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u00026\u0005\u0005\u0003#B\u0007\u00028\u0005m\u0012bAA\u001d\u001d\t1q\n\u001d;j_:\u0004R!DA\u001f?\u001dJ1!a\u0010\u000f\u0005\u0019!V\u000f\u001d7fe!I\u00111IA\u0018\u0003\u0003\u0005\raM\u0001\u0004q\u0012\u0002\u0004BCA$\u0003\u001f\t\t\u0011\"\u0003\u0002J\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\u0005E\u0002Q\u0003\u001bJ1!a\u0014R\u0005\u0019y%M[3di\u0002"
)
public class SparkListenerExecutorUnblacklisted implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String executorId;

   public static Option<Tuple2<Object, String>> unapply(SparkListenerExecutorUnblacklisted var0) {
      return SparkListenerExecutorUnblacklisted$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorUnblacklisted apply(long var0, String var2) {
      return SparkListenerExecutorUnblacklisted$.MODULE$.apply(var0, var2);
   }

   public static Function1<Tuple2<Object, String>, SparkListenerExecutorUnblacklisted> tupled() {
      return SparkListenerExecutorUnblacklisted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, SparkListenerExecutorUnblacklisted>> curried() {
      return SparkListenerExecutorUnblacklisted$.MODULE$.curried();
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

   public SparkListenerExecutorUnblacklisted copy(long time, String executorId) {
      return new SparkListenerExecutorUnblacklisted(time, executorId);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.executorId();
   }

   public String productPrefix() {
      return "SparkListenerExecutorUnblacklisted";
   }

   public int productArity() {
      return 2;
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
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerExecutorUnblacklisted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof SparkListenerExecutorUnblacklisted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  SparkListenerExecutorUnblacklisted var4 = (SparkListenerExecutorUnblacklisted)x$1;
                  if (this.time() == var4.time()) {
                     label35: {
                        String var10000 = this.executorId();
                        String var5 = var4.executorId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label54;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerExecutorUnblacklisted(long time, String executorId) {
      this.time = time;
      this.executorId = executorId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
