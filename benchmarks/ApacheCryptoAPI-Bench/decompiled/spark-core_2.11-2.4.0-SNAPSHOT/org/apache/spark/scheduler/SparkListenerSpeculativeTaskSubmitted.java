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
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a\u0001B\u0001\u0003\u0001.\u0011Qe\u00159be.d\u0015n\u001d;f]\u0016\u00148\u000b]3dk2\fG/\u001b<f)\u0006\u001c8nU;c[&$H/\u001a3\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0006\u00011\u0011b#\u0007\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!AE*qCJ\\G*[:uK:,'/\u0012<f]R\u0004\"!D\f\n\u0005aq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001biI!a\u0007\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011u\u0001!Q3A\u0005\u0002y\tqa\u001d;bO\u0016LE-F\u0001 !\ti\u0001%\u0003\u0002\"\u001d\t\u0019\u0011J\u001c;\t\u0011\r\u0002!\u0011#Q\u0001\n}\t\u0001b\u001d;bO\u0016LE\r\t\u0005\u0006K\u0001!\tAJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u001dB\u0003CA\n\u0001\u0011\u0015iB\u00051\u0001 \u0011\u001dQ\u0003!!A\u0005\u0002-\nAaY8qsR\u0011q\u0005\f\u0005\b;%\u0002\n\u00111\u0001 \u0011\u001dq\u0003!%A\u0005\u0002=\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00011U\ty\u0012gK\u00013!\t\u0019\u0004(D\u00015\u0015\t)d'A\u0005v]\u000eDWmY6fI*\u0011qGD\u0001\u000bC:tw\u000e^1uS>t\u0017BA\u001d5\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bw\u0001\t\t\u0011\"\u0011=\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tQ\b\u0005\u0002?\u00076\tqH\u0003\u0002A\u0003\u0006!A.\u00198h\u0015\u0005\u0011\u0015\u0001\u00026bm\u0006L!\u0001R \u0003\rM#(/\u001b8h\u0011\u001d1\u0005!!A\u0005\u0002y\tA\u0002\u001d:pIV\u001cG/\u0011:jifDq\u0001\u0013\u0001\u0002\u0002\u0013\u0005\u0011*\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005)k\u0005CA\u0007L\u0013\taeBA\u0002B]fDqAT$\u0002\u0002\u0003\u0007q$A\u0002yIEBq\u0001\u0015\u0001\u0002\u0002\u0013\u0005\u0013+A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005\u0011\u0006cA*W\u00156\tAK\u0003\u0002V\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005]#&\u0001C%uKJ\fGo\u001c:\t\u000fe\u0003\u0011\u0011!C\u00015\u0006A1-\u00198FcV\fG\u000e\u0006\u0002\\=B\u0011Q\u0002X\u0005\u0003;:\u0011qAQ8pY\u0016\fg\u000eC\u0004O1\u0006\u0005\t\u0019\u0001&\t\u000f\u0001\u0004\u0011\u0011!C!C\u0006A\u0001.Y:i\u0007>$W\rF\u0001 \u0011\u001d\u0019\u0007!!A\u0005B\u0011\f\u0001\u0002^8TiJLgn\u001a\u000b\u0002{!9a\rAA\u0001\n\u0003:\u0017AB3rk\u0006d7\u000f\u0006\u0002\\Q\"9a*ZA\u0001\u0002\u0004Q\u0005F\u0001\u0001k!\tYW.D\u0001m\u0015\t9D!\u0003\u0002oY\naA)\u001a<fY>\u0004XM]!qS\u001e9\u0001OAA\u0001\u0012\u0003\t\u0018!J*qCJ\\G*[:uK:,'o\u00159fGVd\u0017\r^5wKR\u000b7o[*vE6LG\u000f^3e!\t\u0019\"OB\u0004\u0002\u0005\u0005\u0005\t\u0012A:\u0014\u0007I$\u0018\u0004\u0005\u0003vq~9S\"\u0001<\u000b\u0005]t\u0011a\u0002:v]RLW.Z\u0005\u0003sZ\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u0015)#\u000f\"\u0001|)\u0005\t\bbB2s\u0003\u0003%)\u0005\u001a\u0005\b}J\f\t\u0011\"!\u0000\u0003\u0015\t\u0007\u000f\u001d7z)\r9\u0013\u0011\u0001\u0005\u0006;u\u0004\ra\b\u0005\n\u0003\u000b\u0011\u0018\u0011!CA\u0003\u000f\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\n\u0005=\u0001\u0003B\u0007\u0002\f}I1!!\u0004\u000f\u0005\u0019y\u0005\u000f^5p]\"I\u0011\u0011CA\u0002\u0003\u0003\u0005\raJ\u0001\u0004q\u0012\u0002\u0004\"CA\u000be\u0006\u0005I\u0011BA\f\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005e\u0001c\u0001 \u0002\u001c%\u0019\u0011QD \u0003\r=\u0013'.Z2u\u0001"
)
public class SparkListenerSpeculativeTaskSubmitted implements SparkListenerEvent, Product, Serializable {
   private final int stageId;

   public static Option<Object> unapply(SparkListenerSpeculativeTaskSubmitted var0) {
      return SparkListenerSpeculativeTaskSubmitted$.MODULE$.unapply(var0);
   }

   public static SparkListenerSpeculativeTaskSubmitted apply(int var0) {
      return SparkListenerSpeculativeTaskSubmitted$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<SparkListenerSpeculativeTaskSubmitted, A> var0) {
      return SparkListenerSpeculativeTaskSubmitted$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerSpeculativeTaskSubmitted> compose(Function1<A, Object> var0) {
      return SparkListenerSpeculativeTaskSubmitted$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public int stageId() {
      return this.stageId;
   }

   public SparkListenerSpeculativeTaskSubmitted copy(int stageId) {
      return new SparkListenerSpeculativeTaskSubmitted(stageId);
   }

   public int copy$default$1() {
      return this.stageId();
   }

   public String productPrefix() {
      return "SparkListenerSpeculativeTaskSubmitted";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.stageId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerSpeculativeTaskSubmitted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.stageId());
      return Statics.finalizeHash(var1, 1);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof SparkListenerSpeculativeTaskSubmitted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               SparkListenerSpeculativeTaskSubmitted var4 = (SparkListenerSpeculativeTaskSubmitted)x$1;
               if (this.stageId() == var4.stageId() && var4.canEqual(this)) {
                  break label48;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public SparkListenerSpeculativeTaskSubmitted(int stageId) {
      this.stageId = stageId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
