package org.apache.spark.scheduler;

import java.util.Properties;
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

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec\u0001B\u0001\u0003\u0001.\u00111d\u00159be.d\u0015n\u001d;f]\u0016\u00148\u000b^1hKN+(-\\5ui\u0016$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\u0013M$\u0018mZ3J]\u001a|W#A\u0010\u0011\u0005M\u0001\u0013BA\u0011\u0003\u0005%\u0019F/Y4f\u0013:4w\u000e\u0003\u0005$\u0001\tE\t\u0015!\u0003 \u0003)\u0019H/Y4f\u0013:4w\u000e\t\u0005\tK\u0001\u0011)\u001a!C\u0001M\u0005Q\u0001O]8qKJ$\u0018.Z:\u0016\u0003\u001d\u0002\"\u0001K\u0017\u000e\u0003%R!AK\u0016\u0002\tU$\u0018\u000e\u001c\u0006\u0002Y\u0005!!.\u0019<b\u0013\tq\u0013F\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001\u0002\r\u0001\u0003\u0012\u0003\u0006IaJ\u0001\faJ|\u0007/\u001a:uS\u0016\u001c\b\u0005C\u00033\u0001\u0011\u00051'\u0001\u0004=S:LGO\u0010\u000b\u0004iU2\u0004CA\n\u0001\u0011\u0015i\u0012\u00071\u0001 \u0011\u001d)\u0013\u0007%AA\u0002\u001dBq\u0001\u000f\u0001\u0002\u0002\u0013\u0005\u0011(\u0001\u0003d_BLHc\u0001\u001b;w!9Qd\u000eI\u0001\u0002\u0004y\u0002bB\u00138!\u0003\u0005\ra\n\u0005\b{\u0001\t\n\u0011\"\u0001?\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\u0010\u0016\u0003?\u0001[\u0013!\u0011\t\u0003\u0005\u001ek\u0011a\u0011\u0006\u0003\t\u0016\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0019s\u0011AC1o]>$\u0018\r^5p]&\u0011\u0001j\u0011\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002&\u0001#\u0003%\taS\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005a%FA\u0014A\u0011\u001dq\u0005!!A\u0005B=\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001)\u0011\u0005E#V\"\u0001*\u000b\u0005M[\u0013\u0001\u00027b]\u001eL!!\u0016*\u0003\rM#(/\u001b8h\u0011\u001d9\u0006!!A\u0005\u0002a\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012!\u0017\t\u0003\u001biK!a\u0017\b\u0003\u0007%sG\u000fC\u0004^\u0001\u0005\u0005I\u0011\u00010\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011qL\u0019\t\u0003\u001b\u0001L!!\u0019\b\u0003\u0007\u0005s\u0017\u0010C\u0004d9\u0006\u0005\t\u0019A-\u0002\u0007a$\u0013\u0007C\u0004f\u0001\u0005\u0005I\u0011\t4\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012a\u001a\t\u0004Q.|V\"A5\u000b\u0005)t\u0011AC2pY2,7\r^5p]&\u0011A.\u001b\u0002\t\u0013R,'/\u0019;pe\"9a\u000eAA\u0001\n\u0003y\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005A\u001c\bCA\u0007r\u0013\t\u0011hBA\u0004C_>dW-\u00198\t\u000f\rl\u0017\u0011!a\u0001?\"9Q\u000fAA\u0001\n\u00032\u0018\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003eCq\u0001\u001f\u0001\u0002\u0002\u0013\u0005\u00130\u0001\u0005u_N#(/\u001b8h)\u0005\u0001\u0006bB>\u0001\u0003\u0003%\t\u0005`\u0001\u0007KF,\u0018\r\\:\u0015\u0005Al\bbB2{\u0003\u0003\u0005\ra\u0018\u0015\u0003\u0001}\u0004B!!\u0001\u0002\u00065\u0011\u00111\u0001\u0006\u0003\r\u0012IA!a\u0002\u0002\u0004\taA)\u001a<fY>\u0004XM]!qS\u001eI\u00111\u0002\u0002\u0002\u0002#\u0005\u0011QB\u0001\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmU;c[&$H/\u001a3\u0011\u0007M\tyA\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA\t'\u0015\ty!a\u0005\u001a!\u001d\t)\"a\u0007 OQj!!a\u0006\u000b\u0007\u0005ea\"A\u0004sk:$\u0018.\\3\n\t\u0005u\u0011q\u0003\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u0014\u0004b\u0002\u001a\u0002\u0010\u0011\u0005\u0011\u0011\u0005\u000b\u0003\u0003\u001bA\u0001\u0002_A\b\u0003\u0003%)%\u001f\u0005\u000b\u0003O\ty!!A\u0005\u0002\u0006%\u0012!B1qa2LH#\u0002\u001b\u0002,\u00055\u0002BB\u000f\u0002&\u0001\u0007q\u0004\u0003\u0005&\u0003K\u0001\n\u00111\u0001(\u0011)\t\t$a\u0004\u0002\u0002\u0013\u0005\u00151G\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t)$!\u0011\u0011\u000b5\t9$a\u000f\n\u0007\u0005ebB\u0001\u0004PaRLwN\u001c\t\u0006\u001b\u0005urdJ\u0005\u0004\u0003\u007fq!A\u0002+va2,'\u0007C\u0005\u0002D\u0005=\u0012\u0011!a\u0001i\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005\u001d\u0013qBI\u0001\n\u0003Y\u0015aD1qa2LH\u0005Z3gCVdG\u000f\n\u001a\t\u0013\u0005-\u0013qBI\u0001\n\u0003Y\u0015a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$#\u0007\u0003\u0006\u0002P\u0005=\u0011\u0011!C\u0005\u0003#\n1B]3bIJ+7o\u001c7wKR\u0011\u00111\u000b\t\u0004#\u0006U\u0013bAA,%\n1qJ\u00196fGR\u0004"
)
public class SparkListenerStageSubmitted implements SparkListenerEvent, Product, Serializable {
   private final StageInfo stageInfo;
   private final Properties properties;

   public static Properties $lessinit$greater$default$2() {
      return SparkListenerStageSubmitted$.MODULE$.$lessinit$greater$default$2();
   }

   public static Properties apply$default$2() {
      return SparkListenerStageSubmitted$.MODULE$.apply$default$2();
   }

   public static Option<Tuple2<StageInfo, Properties>> unapply(SparkListenerStageSubmitted var0) {
      return SparkListenerStageSubmitted$.MODULE$.unapply(var0);
   }

   public static SparkListenerStageSubmitted apply(StageInfo var0, Properties var1) {
      return SparkListenerStageSubmitted$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<StageInfo, Properties>, SparkListenerStageSubmitted> tupled() {
      return SparkListenerStageSubmitted$.MODULE$.tupled();
   }

   public static Function1<StageInfo, Function1<Properties, SparkListenerStageSubmitted>> curried() {
      return SparkListenerStageSubmitted$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public StageInfo stageInfo() {
      return this.stageInfo;
   }

   public Properties properties() {
      return this.properties;
   }

   public SparkListenerStageSubmitted copy(StageInfo stageInfo, Properties properties) {
      return new SparkListenerStageSubmitted(stageInfo, properties);
   }

   public StageInfo copy$default$1() {
      return this.stageInfo();
   }

   public Properties copy$default$2() {
      return this.properties();
   }

   public String productPrefix() {
      return "SparkListenerStageSubmitted";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.stageInfo();
            break;
         case 1:
            var10000 = this.properties();
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
      return x$1 instanceof SparkListenerStageSubmitted;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof SparkListenerStageSubmitted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     SparkListenerStageSubmitted var4 = (SparkListenerStageSubmitted)x$1;
                     StageInfo var10000 = this.stageInfo();
                     StageInfo var5 = var4.stageInfo();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     Properties var7 = this.properties();
                     Properties var6 = var4.properties();
                     if (var7 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var7.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public SparkListenerStageSubmitted(StageInfo stageInfo, Properties properties) {
      this.stageInfo = stageInfo;
      this.properties = properties;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
