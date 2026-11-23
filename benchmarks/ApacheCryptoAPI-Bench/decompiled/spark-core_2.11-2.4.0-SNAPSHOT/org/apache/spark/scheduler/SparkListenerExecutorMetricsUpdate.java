package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005]d\u0001B\u0001\u0003\u0001.\u0011!e\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'/T3ue&\u001c7/\u00169eCR,'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\r\u0015DXmY%e+\u0005y\u0002C\u0001\u0011$\u001d\ti\u0011%\u0003\u0002#\u001d\u00051\u0001K]3eK\u001aL!\u0001J\u0013\u0003\rM#(/\u001b8h\u0015\t\u0011c\u0002\u0003\u0005(\u0001\tE\t\u0015!\u0003 \u0003\u001d)\u00070Z2JI\u0002B\u0001\"\u000b\u0001\u0003\u0016\u0004%\tAK\u0001\rC\u000e\u001cW/\\+qI\u0006$Xm]\u000b\u0002WA\u0019A\u0006N\u001c\u000f\u00055\u0012dB\u0001\u00182\u001b\u0005y#B\u0001\u0019\u000b\u0003\u0019a$o\\8u}%\tq\"\u0003\u00024\u001d\u00059\u0001/Y2lC\u001e,\u0017BA\u001b7\u0005\r\u0019V-\u001d\u0006\u0003g9\u0001b!\u0004\u001d;{u\u0002\u0015BA\u001d\u000f\u0005\u0019!V\u000f\u001d7fiA\u0011QbO\u0005\u0003y9\u0011A\u0001T8oOB\u0011QBP\u0005\u0003\u007f9\u00111!\u00138u!\raC'\u0011\t\u0003'\tK!a\u0011\u0002\u0003\u001f\u0005\u001b7-^7vY\u0006\u0014G.Z%oM>D\u0001\"\u0012\u0001\u0003\u0012\u0003\u0006IaK\u0001\u000eC\u000e\u001cW/\\+qI\u0006$Xm\u001d\u0011\t\u000b\u001d\u0003A\u0011\u0001%\u0002\rqJg.\u001b;?)\rI%j\u0013\t\u0003'\u0001AQ!\b$A\u0002}AQ!\u000b$A\u0002-Bq!\u0014\u0001\u0002\u0002\u0013\u0005a*\u0001\u0003d_BLHcA%P!\"9Q\u0004\u0014I\u0001\u0002\u0004y\u0002bB\u0015M!\u0003\u0005\ra\u000b\u0005\b%\u0002\t\n\u0011\"\u0001T\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012\u0001\u0016\u0016\u0003?U[\u0013A\u0016\t\u0003/rk\u0011\u0001\u0017\u0006\u00033j\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005ms\u0011AC1o]>$\u0018\r^5p]&\u0011Q\f\u0017\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB0\u0001#\u0003%\t\u0001Y\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005\t'FA\u0016V\u0011\u001d\u0019\u0007!!A\u0005B\u0011\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A3\u0011\u0005\u0019\\W\"A4\u000b\u0005!L\u0017\u0001\u00027b]\u001eT\u0011A[\u0001\u0005U\u00064\u0018-\u0003\u0002%O\"9Q\u000eAA\u0001\n\u0003q\u0017\u0001\u00049s_\u0012,8\r^!sSRLX#A\u001f\t\u000fA\u0004\u0011\u0011!C\u0001c\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u0001:v!\ti1/\u0003\u0002u\u001d\t\u0019\u0011I\\=\t\u000fY|\u0017\u0011!a\u0001{\u0005\u0019\u0001\u0010J\u0019\t\u000fa\u0004\u0011\u0011!C!s\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001{!\rYhP]\u0007\u0002y*\u0011QPD\u0001\u000bG>dG.Z2uS>t\u0017BA@}\u0005!IE/\u001a:bi>\u0014\b\"CA\u0002\u0001\u0005\u0005I\u0011AA\u0003\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BA\u0004\u0003\u001b\u00012!DA\u0005\u0013\r\tYA\u0004\u0002\b\u0005>|G.Z1o\u0011!1\u0018\u0011AA\u0001\u0002\u0004\u0011\b\"CA\t\u0001\u0005\u0005I\u0011IA\n\u0003!A\u0017m\u001d5D_\u0012,G#A\u001f\t\u0013\u0005]\u0001!!A\u0005B\u0005e\u0011\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0015D\u0011\"!\b\u0001\u0003\u0003%\t%a\b\u0002\r\u0015\fX/\u00197t)\u0011\t9!!\t\t\u0011Y\fY\"!AA\u0002ID3\u0001AA\u0013!\u0011\t9#a\u000b\u000e\u0005\u0005%\"BA.\u0005\u0013\u0011\ti#!\u000b\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\b\u0013\u0005E\"!!A\t\u0002\u0005M\u0012AI*qCJ\\G*[:uK:,'/\u0012=fGV$xN]'fiJL7m]+qI\u0006$X\rE\u0002\u0014\u0003k1\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011qG\n\u0006\u0003k\tI$\u0007\t\b\u0003w\t\teH\u0016J\u001b\t\tiDC\u0002\u0002@9\tqA];oi&lW-\u0003\u0003\u0002D\u0005u\"!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!9q)!\u000e\u0005\u0002\u0005\u001dCCAA\u001a\u0011)\t9\"!\u000e\u0002\u0002\u0013\u0015\u0013\u0011\u0004\u0005\u000b\u0003\u001b\n)$!A\u0005\u0002\u0006=\u0013!B1qa2LH#B%\u0002R\u0005M\u0003BB\u000f\u0002L\u0001\u0007q\u0004\u0003\u0004*\u0003\u0017\u0002\ra\u000b\u0005\u000b\u0003/\n)$!A\u0005\u0002\u0006e\u0013aB;oCB\u0004H.\u001f\u000b\u0005\u00037\n9\u0007E\u0003\u000e\u0003;\n\t'C\u0002\u0002`9\u0011aa\u00149uS>t\u0007#B\u0007\u0002d}Y\u0013bAA3\u001d\t1A+\u001e9mKJB\u0011\"!\u001b\u0002V\u0005\u0005\t\u0019A%\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002n\u0005U\u0012\u0011!C\u0005\u0003_\n1B]3bIJ+7o\u001c7wKR\u0011\u0011\u0011\u000f\t\u0004M\u0006M\u0014bAA;O\n1qJ\u00196fGR\u0004"
)
public class SparkListenerExecutorMetricsUpdate implements SparkListenerEvent, Product, Serializable {
   private final String execId;
   private final Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> accumUpdates;

   public static Option<Tuple2<String, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>>> unapply(SparkListenerExecutorMetricsUpdate var0) {
      return SparkListenerExecutorMetricsUpdate$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorMetricsUpdate apply(String var0, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> var1) {
      return SparkListenerExecutorMetricsUpdate$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>>, SparkListenerExecutorMetricsUpdate> tupled() {
      return SparkListenerExecutorMetricsUpdate$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>, SparkListenerExecutorMetricsUpdate>> curried() {
      return SparkListenerExecutorMetricsUpdate$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public String execId() {
      return this.execId;
   }

   public Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> accumUpdates() {
      return this.accumUpdates;
   }

   public SparkListenerExecutorMetricsUpdate copy(String execId, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> accumUpdates) {
      return new SparkListenerExecutorMetricsUpdate(execId, accumUpdates);
   }

   public String copy$default$1() {
      return this.execId();
   }

   public Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> copy$default$2() {
      return this.accumUpdates();
   }

   public String productPrefix() {
      return "SparkListenerExecutorMetricsUpdate";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.execId();
            break;
         case 1:
            var10000 = this.accumUpdates();
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
      return x$1 instanceof SparkListenerExecutorMetricsUpdate;
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
            if (x$1 instanceof SparkListenerExecutorMetricsUpdate) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     SparkListenerExecutorMetricsUpdate var4 = (SparkListenerExecutorMetricsUpdate)x$1;
                     String var10000 = this.execId();
                     String var5 = var4.execId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     Seq var7 = this.accumUpdates();
                     Seq var6 = var4.accumUpdates();
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

   public SparkListenerExecutorMetricsUpdate(String execId, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> accumUpdates) {
      this.execId = execId;
      this.accumUpdates = accumUpdates;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
