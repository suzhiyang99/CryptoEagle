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
   bytes = "\u0006\u0001\u0005Ec\u0001B\u0001\u0003\u0001.\u0011ad\u00159be.d\u0015n\u001d;f]\u0016\u0014hj\u001c3f+:\u0014G.Y2lY&\u001cH/\u001a3\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0006\u00011\u0011b#\u0007\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!AE*qCJ\\G*[:uK:,'/\u0012<f]R\u0004\"!D\f\n\u0005aq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001biI!a\u0007\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011u\u0001!Q3A\u0005\u0002y\tA\u0001^5nKV\tq\u0004\u0005\u0002\u000eA%\u0011\u0011E\u0004\u0002\u0005\u0019>tw\r\u0003\u0005$\u0001\tE\t\u0015!\u0003 \u0003\u0015!\u0018.\\3!\u0011!)\u0003A!f\u0001\n\u00031\u0013A\u00025pgRLE-F\u0001(!\tA3F\u0004\u0002\u000eS%\u0011!FD\u0001\u0007!J,G-\u001a4\n\u00051j#AB*ue&twM\u0003\u0002+\u001d!Aq\u0006\u0001B\tB\u0003%q%A\u0004i_N$\u0018\n\u001a\u0011\t\u000bE\u0002A\u0011\u0001\u001a\u0002\rqJg.\u001b;?)\r\u0019D'\u000e\t\u0003'\u0001AQ!\b\u0019A\u0002}AQ!\n\u0019A\u0002\u001dBqa\u000e\u0001\u0002\u0002\u0013\u0005\u0001(\u0001\u0003d_BLHcA\u001a:u!9QD\u000eI\u0001\u0002\u0004y\u0002bB\u00137!\u0003\u0005\ra\n\u0005\by\u0001\t\n\u0011\"\u0001>\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012A\u0010\u0016\u0003?}Z\u0013\u0001\u0011\t\u0003\u0003\u001ak\u0011A\u0011\u0006\u0003\u0007\u0012\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0015s\u0011AC1o]>$\u0018\r^5p]&\u0011qI\u0011\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB%\u0001#\u0003%\tAS\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005Y%FA\u0014@\u0011\u001di\u0005!!A\u0005B9\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A(\u0011\u0005A+V\"A)\u000b\u0005I\u001b\u0016\u0001\u00027b]\u001eT\u0011\u0001V\u0001\u0005U\u00064\u0018-\u0003\u0002-#\"9q\u000bAA\u0001\n\u0003A\u0016\u0001\u00049s_\u0012,8\r^!sSRLX#A-\u0011\u00055Q\u0016BA.\u000f\u0005\rIe\u000e\u001e\u0005\b;\u0002\t\t\u0011\"\u0001_\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\u00182\u0011\u00055\u0001\u0017BA1\u000f\u0005\r\te.\u001f\u0005\bGr\u000b\t\u00111\u0001Z\u0003\rAH%\r\u0005\bK\u0002\t\t\u0011\"\u0011g\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A4\u0011\u0007!\\w,D\u0001j\u0015\tQg\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001\\5\u0003\u0011%#XM]1u_JDqA\u001c\u0001\u0002\u0002\u0013\u0005q.\u0001\u0005dC:,\u0015/^1m)\t\u00018\u000f\u0005\u0002\u000ec&\u0011!O\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\u0019W.!AA\u0002}Cq!\u001e\u0001\u0002\u0002\u0013\u0005c/\u0001\u0005iCND7i\u001c3f)\u0005I\u0006b\u0002=\u0001\u0003\u0003%\t%_\u0001\ti>\u001cFO]5oOR\tq\nC\u0004|\u0001\u0005\u0005I\u0011\t?\u0002\r\u0015\fX/\u00197t)\t\u0001X\u0010C\u0004du\u0006\u0005\t\u0019A0)\u0005\u0001y\b\u0003BA\u0001\u0003\u000bi!!a\u0001\u000b\u0005\u0015#\u0011\u0002BA\u0004\u0003\u0007\u0011A\u0002R3wK2|\u0007/\u001a:Ba&<\u0011\"a\u0003\u0003\u0003\u0003E\t!!\u0004\u0002=M\u0003\u0018M]6MSN$XM\\3s\u001d>$W-\u00168cY\u0006\u001c7\u000e\\5ti\u0016$\u0007cA\n\u0002\u0010\u0019A\u0011AAA\u0001\u0012\u0003\t\tbE\u0003\u0002\u0010\u0005M\u0011\u0004E\u0004\u0002\u0016\u0005mqdJ\u001a\u000e\u0005\u0005]!bAA\r\u001d\u00059!/\u001e8uS6,\u0017\u0002BA\u000f\u0003/\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c83\u0011\u001d\t\u0014q\u0002C\u0001\u0003C!\"!!\u0004\t\u0011a\fy!!A\u0005FeD!\"a\n\u0002\u0010\u0005\u0005I\u0011QA\u0015\u0003\u0015\t\u0007\u000f\u001d7z)\u0015\u0019\u00141FA\u0017\u0011\u0019i\u0012Q\u0005a\u0001?!1Q%!\nA\u0002\u001dB!\"!\r\u0002\u0010\u0005\u0005I\u0011QA\u001a\u0003\u001d)h.\u00199qYf$B!!\u000e\u0002BA)Q\"a\u000e\u0002<%\u0019\u0011\u0011\b\b\u0003\r=\u0003H/[8o!\u0015i\u0011QH\u0010(\u0013\r\tyD\u0004\u0002\u0007)V\u0004H.\u001a\u001a\t\u0013\u0005\r\u0013qFA\u0001\u0002\u0004\u0019\u0014a\u0001=%a!Q\u0011qIA\b\u0003\u0003%I!!\u0013\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0017\u00022\u0001UA'\u0013\r\ty%\u0015\u0002\u0007\u001f\nTWm\u0019;"
)
public class SparkListenerNodeUnblacklisted implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String hostId;

   public static Option<Tuple2<Object, String>> unapply(SparkListenerNodeUnblacklisted var0) {
      return SparkListenerNodeUnblacklisted$.MODULE$.unapply(var0);
   }

   public static SparkListenerNodeUnblacklisted apply(long var0, String var2) {
      return SparkListenerNodeUnblacklisted$.MODULE$.apply(var0, var2);
   }

   public static Function1<Tuple2<Object, String>, SparkListenerNodeUnblacklisted> tupled() {
      return SparkListenerNodeUnblacklisted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, SparkListenerNodeUnblacklisted>> curried() {
      return SparkListenerNodeUnblacklisted$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public String hostId() {
      return this.hostId;
   }

   public SparkListenerNodeUnblacklisted copy(long time, String hostId) {
      return new SparkListenerNodeUnblacklisted(time, hostId);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.hostId();
   }

   public String productPrefix() {
      return "SparkListenerNodeUnblacklisted";
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
            var10000 = this.hostId();
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
      return x$1 instanceof SparkListenerNodeUnblacklisted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.hostId()));
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
            if (x$1 instanceof SparkListenerNodeUnblacklisted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  SparkListenerNodeUnblacklisted var4 = (SparkListenerNodeUnblacklisted)x$1;
                  if (this.time() == var4.time()) {
                     label35: {
                        String var10000 = this.hostId();
                        String var5 = var4.hostId();
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

   public SparkListenerNodeUnblacklisted(long time, String hostId) {
      this.time = time;
      this.hostId = hostId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
