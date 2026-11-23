package org.apache.spark.scheduler;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%c!B\u0001\u0003\u0001\u0012Q!aC,pe.,'o\u00144gKJT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011\u0003\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\u000b\n\u0005Yi!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u0015\u0015DXmY;u_JLEm\u0001\u0001\u0016\u0003m\u0001\"\u0001H\u0010\u000f\u00051i\u0012B\u0001\u0010\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011\u0001%\t\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005yi\u0001\u0002C\u0012\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\u0017\u0015DXmY;u_JLE\r\t\u0005\tK\u0001\u0011)\u001a!C\u00015\u0005!\u0001n\\:u\u0011!9\u0003A!E!\u0002\u0013Y\u0012!\u00025pgR\u0004\u0003\u0002C\u0015\u0001\u0005+\u0007I\u0011\u0001\u0016\u0002\u000b\r|'/Z:\u0016\u0003-\u0002\"\u0001\u0004\u0017\n\u00055j!aA%oi\"Aq\u0006\u0001B\tB\u0003%1&\u0001\u0004d_J,7\u000f\t\u0005\u0006c\u0001!\tAM\u0001\u0007y%t\u0017\u000e\u001e \u0015\tM*dg\u000e\t\u0003i\u0001i\u0011A\u0001\u0005\u00061A\u0002\ra\u0007\u0005\u0006KA\u0002\ra\u0007\u0005\u0006SA\u0002\ra\u000b\u0005\bs\u0001\t\t\u0011\"\u0001;\u0003\u0011\u0019w\u000e]=\u0015\tMZD(\u0010\u0005\b1a\u0002\n\u00111\u0001\u001c\u0011\u001d)\u0003\b%AA\u0002mAq!\u000b\u001d\u0011\u0002\u0003\u00071\u0006C\u0004@\u0001E\u0005I\u0011\u0001!\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0011I\u000b\u0002\u001c\u0005.\n1\t\u0005\u0002E\u00136\tQI\u0003\u0002G\u000f\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u00116\t!\"\u00198o_R\fG/[8o\u0013\tQUIA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u0014\u0001\u0012\u0002\u0013\u0005\u0001)\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000f9\u0003\u0011\u0013!C\u0001\u001f\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aT#\u0001)+\u0005-\u0012\u0005b\u0002*\u0001\u0003\u0003%\teU\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003Q\u0003\"!\u0016.\u000e\u0003YS!a\u0016-\u0002\t1\fgn\u001a\u0006\u00023\u0006!!.\u0019<b\u0013\t\u0001c\u000bC\u0004]\u0001\u0005\u0005I\u0011\u0001\u0016\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000fy\u0003\u0011\u0011!C\u0001?\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u00011d!\ta\u0011-\u0003\u0002c\u001b\t\u0019\u0011I\\=\t\u000f\u0011l\u0016\u0011!a\u0001W\u0005\u0019\u0001\u0010J\u0019\t\u000f\u0019\u0004\u0011\u0011!C!O\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001i!\rIG\u000eY\u0007\u0002U*\u00111.D\u0001\u000bG>dG.Z2uS>t\u0017BA7k\u0005!IE/\u001a:bi>\u0014\bbB8\u0001\u0003\u0003%\t\u0001]\u0001\tG\u0006tW)];bYR\u0011\u0011\u000f\u001e\t\u0003\u0019IL!a]\u0007\u0003\u000f\t{w\u000e\\3b]\"9AM\\A\u0001\u0002\u0004\u0001\u0007b\u0002<\u0001\u0003\u0003%\te^\u0001\tQ\u0006\u001c\bnQ8eKR\t1\u0006C\u0004z\u0001\u0005\u0005I\u0011\t>\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u0016\u0005\by\u0002\t\t\u0011\"\u0011~\u0003\u0019)\u0017/^1mgR\u0011\u0011O \u0005\bIn\f\t\u00111\u0001a\u000f)\t\tAAA\u0001\u0012\u0003!\u00111A\u0001\f/>\u00148.\u001a:PM\u001a,'\u000fE\u00025\u0003\u000b1\u0011\"\u0001\u0002\u0002\u0002#\u0005A!a\u0002\u0014\u000b\u0005\u0015\u0011\u0011\u0002\u000b\u0011\u0011\u0005-\u0011\u0011C\u000e\u001cWMj!!!\u0004\u000b\u0007\u0005=Q\"A\u0004sk:$\u0018.\\3\n\t\u0005M\u0011Q\u0002\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u001c\u0004bB\u0019\u0002\u0006\u0011\u0005\u0011q\u0003\u000b\u0003\u0003\u0007A\u0001\"_A\u0003\u0003\u0003%)E\u001f\u0005\u000b\u0003;\t)!!A\u0005\u0002\u0006}\u0011!B1qa2LHcB\u001a\u0002\"\u0005\r\u0012Q\u0005\u0005\u00071\u0005m\u0001\u0019A\u000e\t\r\u0015\nY\u00021\u0001\u001c\u0011\u0019I\u00131\u0004a\u0001W!Q\u0011\u0011FA\u0003\u0003\u0003%\t)a\u000b\u0002\u000fUt\u0017\r\u001d9msR!\u0011QFA\u001d!\u0015a\u0011qFA\u001a\u0013\r\t\t$\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\r1\t)dG\u000e,\u0013\r\t9$\u0004\u0002\u0007)V\u0004H.Z\u001a\t\u0013\u0005m\u0012qEA\u0001\u0002\u0004\u0019\u0014a\u0001=%a!Q\u0011qHA\u0003\u0003\u0003%I!!\u0011\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0007\u00022!VA#\u0013\r\t9E\u0016\u0002\u0007\u001f\nTWm\u0019;"
)
public class WorkerOffer implements Product, Serializable {
   private final String executorId;
   private final String host;
   private final int cores;

   public static Option<Tuple3<String, String, Object>> unapply(WorkerOffer var0) {
      return WorkerOffer$.MODULE$.unapply(var0);
   }

   public static WorkerOffer apply(String var0, String var1, int var2) {
      return WorkerOffer$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<String, String, Object>, WorkerOffer> tupled() {
      return WorkerOffer$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, Function1<Object, WorkerOffer>>> curried() {
      return WorkerOffer$.MODULE$.curried();
   }

   public String executorId() {
      return this.executorId;
   }

   public String host() {
      return this.host;
   }

   public int cores() {
      return this.cores;
   }

   public WorkerOffer copy(String executorId, String host, int cores) {
      return new WorkerOffer(executorId, host, cores);
   }

   public String copy$default$1() {
      return this.executorId();
   }

   public String copy$default$2() {
      return this.host();
   }

   public int copy$default$3() {
      return this.cores();
   }

   public String productPrefix() {
      return "WorkerOffer";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.executorId();
            break;
         case 1:
            var10000 = this.host();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.cores());
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
      return x$1 instanceof WorkerOffer;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.host()));
      var1 = Statics.mix(var1, this.cores());
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
            if (x$1 instanceof WorkerOffer) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  label55: {
                     WorkerOffer var4 = (WorkerOffer)x$1;
                     String var10000 = this.executorId();
                     String var5 = var4.executorId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label55;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label55;
                     }

                     var10000 = this.host();
                     String var6 = var4.host();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label55;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label55;
                     }

                     if (this.cores() == var4.cores() && var4.canEqual(this)) {
                        var9 = true;
                        break label46;
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

   public WorkerOffer(String executorId, String host, int cores) {
      this.executorId = executorId;
      this.host = host;
      this.cores = cores;
      Product$class.$init$(this);
   }
}
