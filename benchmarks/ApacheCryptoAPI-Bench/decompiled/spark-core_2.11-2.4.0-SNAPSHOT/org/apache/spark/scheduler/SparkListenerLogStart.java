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
   bytes = "\u0006\u0001\u00055b\u0001B\u0001\u0003\u0001.\u0011Qc\u00159be.d\u0015n\u001d;f]\u0016\u0014Hj\\4Ti\u0006\u0014HO\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011#\u0002\u0001\r%YI\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u0014)5\t!!\u0003\u0002\u0016\u0005\t\u00112\u000b]1sW2K7\u000f^3oKJ,e/\u001a8u!\tiq#\u0003\u0002\u0019\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u001b\u0013\tYbB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001e\u0001\tU\r\u0011\"\u0001\u001f\u00031\u0019\b/\u0019:l-\u0016\u00148/[8o+\u0005y\u0002C\u0001\u0011$\u001d\ti\u0011%\u0003\u0002#\u001d\u00051\u0001K]3eK\u001aL!\u0001J\u0013\u0003\rM#(/\u001b8h\u0015\t\u0011c\u0002\u0003\u0005(\u0001\tE\t\u0015!\u0003 \u00035\u0019\b/\u0019:l-\u0016\u00148/[8oA!)\u0011\u0006\u0001C\u0001U\u00051A(\u001b8jiz\"\"a\u000b\u0017\u0011\u0005M\u0001\u0001\"B\u000f)\u0001\u0004y\u0002b\u0002\u0018\u0001\u0003\u0003%\taL\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002,a!9Q$\fI\u0001\u0002\u0004y\u0002b\u0002\u001a\u0001#\u0003%\taM\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005!$FA\u00106W\u00051\u0004CA\u001c=\u001b\u0005A$BA\u001d;\u0003%)hn\u00195fG.,GM\u0003\u0002<\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005uB$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\bAA\u0001\n\u0003\u0002\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001B!\t\u0011u)D\u0001D\u0015\t!U)\u0001\u0003mC:<'\"\u0001$\u0002\t)\fg/Y\u0005\u0003I\rCq!\u0013\u0001\u0002\u0002\u0013\u0005!*\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001L!\tiA*\u0003\u0002N\u001d\t\u0019\u0011J\u001c;\t\u000f=\u0003\u0011\u0011!C\u0001!\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA)U!\ti!+\u0003\u0002T\u001d\t\u0019\u0011I\\=\t\u000fUs\u0015\u0011!a\u0001\u0017\u0006\u0019\u0001\u0010J\u0019\t\u000f]\u0003\u0011\u0011!C!1\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001Z!\rQV,U\u0007\u00027*\u0011ALD\u0001\u000bG>dG.Z2uS>t\u0017B\u00010\\\u0005!IE/\u001a:bi>\u0014\bb\u00021\u0001\u0003\u0003%\t!Y\u0001\tG\u0006tW)];bYR\u0011!-\u001a\t\u0003\u001b\rL!\u0001\u001a\b\u0003\u000f\t{w\u000e\\3b]\"9QkXA\u0001\u0002\u0004\t\u0006bB4\u0001\u0003\u0003%\t\u0005[\u0001\tQ\u0006\u001c\bnQ8eKR\t1\nC\u0004k\u0001\u0005\u0005I\u0011I6\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012!\u0011\u0005\b[\u0002\t\t\u0011\"\u0011o\u0003\u0019)\u0017/^1mgR\u0011!m\u001c\u0005\b+2\f\t\u00111\u0001RQ\t\u0001\u0011\u000f\u0005\u0002si6\t1O\u0003\u0002<\t%\u0011Qo\u001d\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\bo\n\t\t\u0011#\u0001y\u0003U\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe2{wm\u0015;beR\u0004\"aE=\u0007\u000f\u0005\u0011\u0011\u0011!E\u0001uN\u0019\u0011p_\r\u0011\tq|xdK\u0007\u0002{*\u0011aPD\u0001\beVtG/[7f\u0013\r\t\t! \u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004BB\u0015z\t\u0003\t)\u0001F\u0001y\u0011\u001dQ\u00170!A\u0005F-D\u0011\"a\u0003z\u0003\u0003%\t)!\u0004\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007-\ny\u0001\u0003\u0004\u001e\u0003\u0013\u0001\ra\b\u0005\n\u0003'I\u0018\u0011!CA\u0003+\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\u0018\u0005u\u0001\u0003B\u0007\u0002\u001a}I1!a\u0007\u000f\u0005\u0019y\u0005\u000f^5p]\"I\u0011qDA\t\u0003\u0003\u0005\raK\u0001\u0004q\u0012\u0002\u0004\"CA\u0012s\u0006\u0005I\u0011BA\u0013\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u001d\u0002c\u0001\"\u0002*%\u0019\u00111F\"\u0003\r=\u0013'.Z2u\u0001"
)
public class SparkListenerLogStart implements SparkListenerEvent, Product, Serializable {
   private final String sparkVersion;

   public static Option<String> unapply(SparkListenerLogStart var0) {
      return SparkListenerLogStart$.MODULE$.unapply(var0);
   }

   public static SparkListenerLogStart apply(String var0) {
      return SparkListenerLogStart$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<SparkListenerLogStart, A> var0) {
      return SparkListenerLogStart$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerLogStart> compose(Function1<A, String> var0) {
      return SparkListenerLogStart$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public String sparkVersion() {
      return this.sparkVersion;
   }

   public SparkListenerLogStart copy(String sparkVersion) {
      return new SparkListenerLogStart(sparkVersion);
   }

   public String copy$default$1() {
      return this.sparkVersion();
   }

   public String productPrefix() {
      return "SparkListenerLogStart";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.sparkVersion();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerLogStart;
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
            if (x$1 instanceof SparkListenerLogStart) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SparkListenerLogStart var4 = (SparkListenerLogStart)x$1;
                     String var10000 = this.sparkVersion();
                     String var5 = var4.sparkVersion();
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

   public SparkListenerLogStart(String sparkVersion) {
      this.sparkVersion = sparkVersion;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
