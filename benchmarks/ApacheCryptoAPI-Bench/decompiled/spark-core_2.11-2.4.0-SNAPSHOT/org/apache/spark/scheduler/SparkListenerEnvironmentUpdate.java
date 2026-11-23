package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Map;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ec\u0001B\u0001\u0003\u0001.\u0011ad\u00159be.d\u0015n\u001d;f]\u0016\u0014XI\u001c<je>tW.\u001a8u+B$\u0017\r^3\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0006\u00011\u0011b#\u0007\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!AE*qCJ\\G*[:uK:,'/\u0012<f]R\u0004\"!D\f\n\u0005aq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001biI!a\u0007\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011u\u0001!Q3A\u0005\u0002y\t!#\u001a8wSJ|g.\\3oi\u0012+G/Y5mgV\tq\u0004\u0005\u0003!G\u0015bS\"A\u0011\u000b\u0005\tr\u0011AC2pY2,7\r^5p]&\u0011A%\t\u0002\u0004\u001b\u0006\u0004\bC\u0001\u0014*\u001d\tiq%\u0003\u0002)\u001d\u00051\u0001K]3eK\u001aL!AK\u0016\u0003\rM#(/\u001b8h\u0015\tAc\u0002E\u0002.kar!AL\u001a\u000f\u0005=\u0012T\"\u0001\u0019\u000b\u0005ER\u0011A\u0002\u001fs_>$h(C\u0001\u0010\u0013\t!d\"A\u0004qC\u000e\\\u0017mZ3\n\u0005Y:$aA*fc*\u0011AG\u0004\t\u0005\u001be*S%\u0003\u0002;\u001d\t1A+\u001e9mKJB\u0001\u0002\u0010\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0014K:4\u0018N]8o[\u0016tG\u000fR3uC&d7\u000f\t\u0005\u0006}\u0001!\taP\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u0001\u000b\u0005CA\n\u0001\u0011\u0015iR\b1\u0001 \u0011\u001d\u0019\u0005!!A\u0005\u0002\u0011\u000bAaY8qsR\u0011\u0001)\u0012\u0005\b;\t\u0003\n\u00111\u0001 \u0011\u001d9\u0005!%A\u0005\u0002!\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001JU\ty\"jK\u0001L!\ta\u0015+D\u0001N\u0015\tqu*A\u0005v]\u000eDWmY6fI*\u0011\u0001KD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001*N\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b)\u0002\t\t\u0011\"\u0011V\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\ta\u000b\u0005\u0002X96\t\u0001L\u0003\u0002Z5\u0006!A.\u00198h\u0015\u0005Y\u0016\u0001\u00026bm\u0006L!A\u000b-\t\u000fy\u0003\u0011\u0011!C\u0001?\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t\u0001\r\u0005\u0002\u000eC&\u0011!M\u0004\u0002\u0004\u0013:$\bb\u00023\u0001\u0003\u0003%\t!Z\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t1\u0017\u000e\u0005\u0002\u000eO&\u0011\u0001N\u0004\u0002\u0004\u0003:L\bb\u00026d\u0003\u0003\u0005\r\u0001Y\u0001\u0004q\u0012\n\u0004b\u00027\u0001\u0003\u0003%\t%\\\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\ta\u000eE\u0002!_\u001aL!\u0001]\u0011\u0003\u0011%#XM]1u_JDqA\u001d\u0001\u0002\u0002\u0013\u00051/\u0001\u0005dC:,\u0015/^1m)\t!x\u000f\u0005\u0002\u000ek&\u0011aO\u0004\u0002\b\u0005>|G.Z1o\u0011\u001dQ\u0017/!AA\u0002\u0019Dq!\u001f\u0001\u0002\u0002\u0013\u0005#0\u0001\u0005iCND7i\u001c3f)\u0005\u0001\u0007b\u0002?\u0001\u0003\u0003%\t%`\u0001\ti>\u001cFO]5oOR\ta\u000b\u0003\u0005\u0000\u0001\u0005\u0005I\u0011IA\u0001\u0003\u0019)\u0017/^1mgR\u0019A/a\u0001\t\u000f)t\u0018\u0011!a\u0001M\"\u001a\u0001!a\u0002\u0011\t\u0005%\u0011QB\u0007\u0003\u0003\u0017Q!\u0001\u0015\u0003\n\t\u0005=\u00111\u0002\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\n\u0003'\u0011\u0011\u0011!E\u0001\u0003+\tad\u00159be.d\u0015n\u001d;f]\u0016\u0014XI\u001c<je>tW.\u001a8u+B$\u0017\r^3\u0011\u0007M\t9B\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA\r'\u0015\t9\"a\u0007\u001a!\u0019\ti\"a\t \u00016\u0011\u0011q\u0004\u0006\u0004\u0003Cq\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003K\tyBA\tBEN$(/Y2u\rVt7\r^5p]FBqAPA\f\t\u0003\tI\u0003\u0006\u0002\u0002\u0016!AA0a\u0006\u0002\u0002\u0013\u0015S\u0010\u0003\u0006\u00020\u0005]\u0011\u0011!CA\u0003c\tQ!\u00199qYf$2\u0001QA\u001a\u0011\u0019i\u0012Q\u0006a\u0001?!Q\u0011qGA\f\u0003\u0003%\t)!\u000f\u0002\u000fUt\u0017\r\u001d9msR!\u00111HA!!\u0011i\u0011QH\u0010\n\u0007\u0005}bB\u0001\u0004PaRLwN\u001c\u0005\n\u0003\u0007\n)$!AA\u0002\u0001\u000b1\u0001\u001f\u00131\u0011)\t9%a\u0006\u0002\u0002\u0013%\u0011\u0011J\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002LA\u0019q+!\u0014\n\u0007\u0005=\u0003L\u0001\u0004PE*,7\r\u001e"
)
public class SparkListenerEnvironmentUpdate implements SparkListenerEvent, Product, Serializable {
   private final Map<String, Seq<Tuple2<String, String>>> environmentDetails;

   public static Option<Map<String, Seq<Tuple2<String, String>>>> unapply(SparkListenerEnvironmentUpdate var0) {
      return SparkListenerEnvironmentUpdate$.MODULE$.unapply(var0);
   }

   public static SparkListenerEnvironmentUpdate apply(Map<String, Seq<Tuple2<String, String>>> var0) {
      return SparkListenerEnvironmentUpdate$.MODULE$.apply(var0);
   }

   public static <A> Function1<Map<String, Seq<Tuple2<String, String>>>, A> andThen(Function1<SparkListenerEnvironmentUpdate, A> var0) {
      return SparkListenerEnvironmentUpdate$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerEnvironmentUpdate> compose(Function1<A, Map<String, Seq<Tuple2<String, String>>>> var0) {
      return SparkListenerEnvironmentUpdate$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public Map<String, Seq<Tuple2<String, String>>> environmentDetails() {
      return this.environmentDetails;
   }

   public SparkListenerEnvironmentUpdate copy(Map<String, Seq<Tuple2<String, String>>> environmentDetails) {
      return new SparkListenerEnvironmentUpdate(environmentDetails);
   }

   public Map<String, Seq<Tuple2<String, String>>> copy$default$1() {
      return this.environmentDetails();
   }

   public String productPrefix() {
      return "SparkListenerEnvironmentUpdate";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.environmentDetails();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerEnvironmentUpdate;
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
            if (x$1 instanceof SparkListenerEnvironmentUpdate) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SparkListenerEnvironmentUpdate var4 = (SparkListenerEnvironmentUpdate)x$1;
                     Map var10000 = this.environmentDetails();
                     Map var5 = var4.environmentDetails();
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

   public SparkListenerEnvironmentUpdate(Map<String, Seq<Tuple2<String, String>>> environmentDetails) {
      this.environmentDetails = environmentDetails;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
