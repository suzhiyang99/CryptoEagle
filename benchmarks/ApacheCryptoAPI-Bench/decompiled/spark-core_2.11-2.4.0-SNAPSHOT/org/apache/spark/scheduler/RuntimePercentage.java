package org.apache.spark.scheduler;

import org.apache.spark.executor.TaskMetrics;
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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]c\u0001B\u0001\u0003\t.\u0011\u0011CU;oi&lW\rU3sG\u0016tG/Y4f\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\t\u0001a!#\u0006\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00055\u0019\u0012B\u0001\u000b\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\f\n\u0005]q!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u0017\u0015DXmY;u_J\u00046\r^\u000b\u00027A\u0011Q\u0002H\u0005\u0003;9\u0011a\u0001R8vE2,\u0007\u0002C\u0010\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\u0019\u0015DXmY;u_J\u00046\r\u001e\u0011\t\u0011\u0005\u0002!Q3A\u0005\u0002\t\n\u0001BZ3uG\"\u00046\r^\u000b\u0002GA\u0019Q\u0002J\u000e\n\u0005\u0015r!AB(qi&|g\u000e\u0003\u0005(\u0001\tE\t\u0015!\u0003$\u0003%1W\r^2i!\u000e$\b\u0005\u0003\u0005*\u0001\tU\r\u0011\"\u0001\u001b\u0003\u0015yG\u000f[3s\u0011!Y\u0003A!E!\u0002\u0013Y\u0012AB8uQ\u0016\u0014\b\u0005C\u0003.\u0001\u0011\u0005a&\u0001\u0004=S:LGO\u0010\u000b\u0005_E\u00124\u0007\u0005\u00021\u00015\t!\u0001C\u0003\u001aY\u0001\u00071\u0004C\u0003\"Y\u0001\u00071\u0005C\u0003*Y\u0001\u00071\u0004C\u00046\u0001\u0005\u0005I\u0011\u0001\u001c\u0002\t\r|\u0007/\u001f\u000b\u0005_]B\u0014\bC\u0004\u001aiA\u0005\t\u0019A\u000e\t\u000f\u0005\"\u0004\u0013!a\u0001G!9\u0011\u0006\u000eI\u0001\u0002\u0004Y\u0002bB\u001e\u0001#\u0003%\t\u0001P\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005i$FA\u000e?W\u0005y\u0004C\u0001!F\u001b\u0005\t%B\u0001\"D\u0003%)hn\u00195fG.,GM\u0003\u0002E\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u0019\u000b%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9\u0001\nAI\u0001\n\u0003I\u0015AD2paf$C-\u001a4bk2$HEM\u000b\u0002\u0015*\u00121E\u0010\u0005\b\u0019\u0002\t\n\u0011\"\u0001=\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBqA\u0014\u0001\u0002\u0002\u0013\u0005s*A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002!B\u0011\u0011KV\u0007\u0002%*\u00111\u000bV\u0001\u0005Y\u0006twMC\u0001V\u0003\u0011Q\u0017M^1\n\u0005]\u0013&AB*ue&tw\rC\u0004Z\u0001\u0005\u0005I\u0011\u0001.\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003m\u0003\"!\u0004/\n\u0005us!aA%oi\"9q\fAA\u0001\n\u0003\u0001\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003C\u0012\u0004\"!\u00042\n\u0005\rt!aA!os\"9QMXA\u0001\u0002\u0004Y\u0016a\u0001=%c!9q\rAA\u0001\n\u0003B\u0017a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003%\u00042A[7b\u001b\u0005Y'B\u00017\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003].\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\ba\u0002\t\t\u0011\"\u0001r\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001:v!\ti1/\u0003\u0002u\u001d\t9!i\\8mK\u0006t\u0007bB3p\u0003\u0003\u0005\r!\u0019\u0005\bo\u0002\t\t\u0011\"\u0011y\u0003!A\u0017m\u001d5D_\u0012,G#A.\t\u000fi\u0004\u0011\u0011!C!w\u0006AAo\\*ue&tw\rF\u0001Q\u0011\u001di\b!!A\u0005By\fa!Z9vC2\u001cHC\u0001:\u0000\u0011\u001d)G0!AA\u0002\u0005<q!a\u0001\u0003\u0011\u0013\t)!A\tSk:$\u0018.\\3QKJ\u001cWM\u001c;bO\u0016\u00042\u0001MA\u0004\r\u0019\t!\u0001#\u0003\u0002\nM!\u0011q\u0001\u0007\u0016\u0011\u001di\u0013q\u0001C\u0001\u0003\u001b!\"!!\u0002\t\u0011\u0005E\u0011q\u0001C\u0001\u0003'\tQ!\u00199qYf$RaLA\u000b\u0003?A\u0001\"a\u0006\u0002\u0010\u0001\u0007\u0011\u0011D\u0001\ni>$\u0018\r\u001c+j[\u0016\u00042!DA\u000e\u0013\r\tiB\u0004\u0002\u0005\u0019>tw\r\u0003\u0005\u0002\"\u0005=\u0001\u0019AA\u0012\u0003\u001diW\r\u001e:jGN\u0004B!!\n\u0002,5\u0011\u0011q\u0005\u0006\u0004\u0003S!\u0011\u0001C3yK\u000e,Ho\u001c:\n\t\u00055\u0012q\u0005\u0002\f)\u0006\u001c8.T3ue&\u001c7\u000f\u0003\u0006\u0002\u0012\u0005\u001d\u0011\u0011!CA\u0003c!raLA\u001a\u0003k\t9\u0004\u0003\u0004\u001a\u0003_\u0001\ra\u0007\u0005\u0007C\u0005=\u0002\u0019A\u0012\t\r%\ny\u00031\u0001\u001c\u0011)\tY$a\u0002\u0002\u0002\u0013\u0005\u0015QH\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\ty$a\u0012\u0011\t5!\u0013\u0011\t\t\u0007\u001b\u0005\r3dI\u000e\n\u0007\u0005\u0015cB\u0001\u0004UkBdWm\r\u0005\n\u0003\u0013\nI$!AA\u0002=\n1\u0001\u001f\u00131\u0011)\ti%a\u0002\u0002\u0002\u0013%\u0011qJ\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002RA\u0019\u0011+a\u0015\n\u0007\u0005U#K\u0001\u0004PE*,7\r\u001e"
)
public class RuntimePercentage implements Product, Serializable {
   private final double executorPct;
   private final Option<Object> fetchPct;
   private final double other;

   public static Option<Tuple3<Object, Option<Object>, Object>> unapply(RuntimePercentage var0) {
      return RuntimePercentage$.MODULE$.unapply(var0);
   }

   public static RuntimePercentage apply(double var0, Option<Object> var2, double var3) {
      return RuntimePercentage$.MODULE$.apply(var0, var2, var3);
   }

   public static RuntimePercentage apply(long var0, TaskMetrics var2) {
      return RuntimePercentage$.MODULE$.apply(var0, var2);
   }

   public double executorPct() {
      return this.executorPct;
   }

   public Option<Object> fetchPct() {
      return this.fetchPct;
   }

   public double other() {
      return this.other;
   }

   public RuntimePercentage copy(double executorPct, Option<Object> fetchPct, double other) {
      return new RuntimePercentage(executorPct, fetchPct, other);
   }

   public double copy$default$1() {
      return this.executorPct();
   }

   public Option<Object> copy$default$2() {
      return this.fetchPct();
   }

   public double copy$default$3() {
      return this.other();
   }

   public String productPrefix() {
      return "RuntimePercentage";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToDouble(this.executorPct());
            break;
         case 1:
            var10000 = this.fetchPct();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToDouble(this.other());
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
      return x$1 instanceof RuntimePercentage;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.doubleHash(this.executorPct()));
      var1 = Statics.mix(var1, Statics.anyHash(this.fetchPct()));
      var1 = Statics.mix(var1, Statics.doubleHash(this.other()));
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
            if (x$1 instanceof RuntimePercentage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  RuntimePercentage var4 = (RuntimePercentage)x$1;
                  if (this.executorPct() == var4.executorPct()) {
                     label37: {
                        Option var10000 = this.fetchPct();
                        Option var5 = var4.fetchPct();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label37;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label37;
                        }

                        if (this.other() == var4.other() && var4.canEqual(this)) {
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

   public RuntimePercentage(double executorPct, Option<Object> fetchPct, double other) {
      this.executorPct = executorPct;
      this.fetchPct = fetchPct;
      this.other = other;
      Product$class.$init$(this);
   }
}
