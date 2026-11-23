package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ed\u0001B\u0001\u0003\u0001&\u00111#\u0012=fGV$xN\u001d'pgR4\u0015-\u001b7ve\u0016T!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001'\u0015\u0001!\u0002\u0005\u000b\u0018!\tYa\"D\u0001\r\u0015\u0005i\u0011!B:dC2\f\u0017BA\b\r\u0005\u0019\te.\u001f*fMB\u0011\u0011CE\u0007\u0002\u0005%\u00111C\u0001\u0002\u0011)\u0006\u001c8NR1jY\u0016$'+Z1t_:\u0004\"aC\u000b\n\u0005Ya!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0017aI!!\u0007\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011m\u0001!Q3A\u0005\u0002q\ta!\u001a=fG&#W#A\u000f\u0011\u0005y\tcBA\u0006 \u0013\t\u0001C\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003E\r\u0012aa\u0015;sS:<'B\u0001\u0011\r\u0011!)\u0003A!E!\u0002\u0013i\u0012aB3yK\u000eLE\r\t\u0005\tO\u0001\u0011)\u001a!C\u0001Q\u0005yQ\r_5u\u0007\u0006,8/\u001a3Cs\u0006\u0003\b/F\u0001*!\tY!&\u0003\u0002,\u0019\t9!i\\8mK\u0006t\u0007\u0002C\u0017\u0001\u0005#\u0005\u000b\u0011B\u0015\u0002!\u0015D\u0018\u000e^\"bkN,GMQ=BaB\u0004\u0003\u0002C\u0018\u0001\u0005+\u0007I\u0011\u0001\u0019\u0002\rI,\u0017m]8o+\u0005\t\u0004cA\u00063;%\u00111\u0007\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0011U\u0002!\u0011#Q\u0001\nE\nqA]3bg>t\u0007\u0005C\u00038\u0001\u0011\u0005\u0001(\u0001\u0004=S:LGO\u0010\u000b\u0005siZD\b\u0005\u0002\u0012\u0001!)1D\u000ea\u0001;!9qE\u000eI\u0001\u0002\u0004I\u0003\"B\u00187\u0001\u0004\t\u0004\"\u0002 \u0001\t\u0003b\u0012!\u0004;p\u000bJ\u0014xN]*ue&tw\rC\u0003A\u0001\u0011\u0005\u0003&\u0001\rd_VtG\u000fV8xCJ$7\u000fV1tW\u001a\u000b\u0017\u000e\\;sKNDqA\u0011\u0001\u0002\u0002\u0013\u00051)\u0001\u0003d_BLH\u0003B\u001dE\u000b\u001aCqaG!\u0011\u0002\u0003\u0007Q\u0004C\u0004(\u0003B\u0005\t\u0019A\u0015\t\u000f=\n\u0005\u0013!a\u0001c!9\u0001\nAI\u0001\n\u0003I\u0015AD2paf$C-\u001a4bk2$H%M\u000b\u0002\u0015*\u0012QdS\u0016\u0002\u0019B\u0011QJU\u0007\u0002\u001d*\u0011q\nU\u0001\nk:\u001c\u0007.Z2lK\u0012T!!\u0015\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002T\u001d\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000fU\u0003\u0011\u0013!C\u0001-\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#A,+\u0005%Z\u0005bB-\u0001#\u0003%\tAW\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\u0005Y&FA\u0019L\u0011\u001di\u0006!!A\u0005By\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A0\u0011\u0005\u0001,W\"A1\u000b\u0005\t\u001c\u0017\u0001\u00027b]\u001eT\u0011\u0001Z\u0001\u0005U\u00064\u0018-\u0003\u0002#C\"9q\rAA\u0001\n\u0003A\u0017\u0001\u00049s_\u0012,8\r^!sSRLX#A5\u0011\u0005-Q\u0017BA6\r\u0005\rIe\u000e\u001e\u0005\b[\u0002\t\t\u0011\"\u0001o\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\u001c:\u0011\u0005-\u0001\u0018BA9\r\u0005\r\te.\u001f\u0005\bg2\f\t\u00111\u0001j\u0003\rAH%\r\u0005\bk\u0002\t\t\u0011\"\u0011w\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A<\u0011\u0007a\\x.D\u0001z\u0015\tQH\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001`=\u0003\u0011%#XM]1u_JDqA \u0001\u0002\u0002\u0013\u0005q0\u0001\u0005dC:,\u0015/^1m)\rI\u0013\u0011\u0001\u0005\bgv\f\t\u00111\u0001p\u0011%\t)\u0001AA\u0001\n\u0003\n9!\u0001\u0005iCND7i\u001c3f)\u0005I\u0007\"CA\u0006\u0001\u0005\u0005I\u0011IA\u0007\u0003!!xn\u0015;sS:<G#A0\t\u0013\u0005E\u0001!!A\u0005B\u0005M\u0011AB3rk\u0006d7\u000fF\u0002*\u0003+A\u0001b]A\b\u0003\u0003\u0005\ra\u001c\u0015\u0004\u0001\u0005e\u0001\u0003BA\u000e\u0003?i!!!\b\u000b\u0005E\u0013\u0011\u0002BA\u0011\u0003;\u0011A\u0002R3wK2|\u0007/\u001a:Ba&<\u0011\"!\n\u0003\u0003\u0003E\t!a\n\u0002'\u0015CXmY;u_Jdun\u001d;GC&dWO]3\u0011\u0007E\tIC\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA\u0016'\u0015\tI#!\f\u0018!!\ty#!\u000e\u001eSEJTBAA\u0019\u0015\r\t\u0019\u0004D\u0001\beVtG/[7f\u0013\u0011\t9$!\r\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t7\u0007C\u00048\u0003S!\t!a\u000f\u0015\u0005\u0005\u001d\u0002BCA\u0006\u0003S\t\t\u0011\"\u0012\u0002\u000e!Q\u0011\u0011IA\u0015\u0003\u0003%\t)a\u0011\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000fe\n)%a\u0012\u0002J!11$a\u0010A\u0002uA\u0001bJA !\u0003\u0005\r!\u000b\u0005\u0007_\u0005}\u0002\u0019A\u0019\t\u0015\u00055\u0013\u0011FA\u0001\n\u0003\u000by%A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005E\u0013\u0011\f\t\u0005\u0017I\n\u0019\u0006\u0005\u0004\f\u0003+j\u0012&M\u0005\u0004\u0003/b!A\u0002+va2,7\u0007C\u0005\u0002\\\u0005-\u0013\u0011!a\u0001s\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005}\u0013\u0011FI\u0001\n\u00031\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$#\u0007C\u0005\u0002d\u0005%\u0012\u0013!C\u0001-\u0006y\u0011\r\u001d9ms\u0012\"WMZ1vYR$#\u0007\u0003\u0006\u0002h\u0005%\u0012\u0011!C\u0005\u0003S\n1B]3bIJ+7o\u001c7wKR\u0011\u00111\u000e\t\u0004A\u00065\u0014bAA8C\n1qJ\u00196fGR\u0004"
)
public class ExecutorLostFailure implements TaskFailedReason, Product, Serializable {
   private final String execId;
   private final boolean exitCausedByApp;
   private final Option<String> reason;

   public static boolean apply$default$2() {
      return ExecutorLostFailure$.MODULE$.apply$default$2();
   }

   public static boolean $lessinit$greater$default$2() {
      return ExecutorLostFailure$.MODULE$.$lessinit$greater$default$2();
   }

   public static Option<Tuple3<String, Object, Option<String>>> unapply(ExecutorLostFailure var0) {
      return ExecutorLostFailure$.MODULE$.unapply(var0);
   }

   public static ExecutorLostFailure apply(String var0, boolean var1, Option<String> var2) {
      return ExecutorLostFailure$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<String, Object, Option<String>>, ExecutorLostFailure> tupled() {
      return ExecutorLostFailure$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<Option<String>, ExecutorLostFailure>>> curried() {
      return ExecutorLostFailure$.MODULE$.curried();
   }

   public String execId() {
      return this.execId;
   }

   public boolean exitCausedByApp() {
      return this.exitCausedByApp;
   }

   public Option<String> reason() {
      return this.reason;
   }

   public String toErrorString() {
      String exitBehavior = this.exitCausedByApp() ? "caused by one of the running tasks" : "unrelated to the running tasks";
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ExecutorLostFailure (executor ", " exited ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.execId(), exitBehavior}))).append(this.reason().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String r) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" Reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{r}));
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).toString();
   }

   public boolean countTowardsTaskFailures() {
      return this.exitCausedByApp();
   }

   public ExecutorLostFailure copy(String execId, boolean exitCausedByApp, Option<String> reason) {
      return new ExecutorLostFailure(execId, exitCausedByApp, reason);
   }

   public String copy$default$1() {
      return this.execId();
   }

   public boolean copy$default$2() {
      return this.exitCausedByApp();
   }

   public Option<String> copy$default$3() {
      return this.reason();
   }

   public String productPrefix() {
      return "ExecutorLostFailure";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.execId();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToBoolean(this.exitCausedByApp());
            break;
         case 2:
            var10000 = this.reason();
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
      return x$1 instanceof ExecutorLostFailure;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.execId()));
      var1 = Statics.mix(var1, this.exitCausedByApp() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label63: {
            boolean var3;
            if (x$1 instanceof ExecutorLostFailure) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  label45: {
                     ExecutorLostFailure var4 = (ExecutorLostFailure)x$1;
                     String var10000 = this.execId();
                     String var5 = var4.execId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label45;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label45;
                     }

                     if (this.exitCausedByApp() == var4.exitCausedByApp()) {
                        label39: {
                           Option var7 = this.reason();
                           Option var6 = var4.reason();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label39;
                              }
                           } else if (!var7.equals(var6)) {
                              break label39;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label46;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label63;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public ExecutorLostFailure(String execId, boolean exitCausedByApp, Option<String> reason) {
      this.execId = execId;
      this.exitCausedByApp = exitCausedByApp;
      this.reason = reason;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
