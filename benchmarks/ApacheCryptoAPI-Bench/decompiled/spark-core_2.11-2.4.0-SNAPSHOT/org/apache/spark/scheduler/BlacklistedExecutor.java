package org.apache.spark.scheduler;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}b\u0001B\u0001\u0003\r.\u00111C\u00117bG.d\u0017n\u001d;fI\u0016CXmY;u_JT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\n\u0016!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u0011QbE\u0005\u0003)9\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e-%\u0011qC\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00015\u0005!an\u001c3f+\u0005Y\u0002C\u0001\u000f \u001d\tiQ$\u0003\u0002\u001f\u001d\u00051\u0001K]3eK\u001aL!\u0001I\u0011\u0003\rM#(/\u001b8h\u0015\tqb\u0002\u0003\u0005$\u0001\tE\t\u0015!\u0003\u001c\u0003\u0015qw\u000eZ3!\u0011!)\u0003A!f\u0001\n\u00031\u0013AC3ya&\u0014\u0018\u0010V5nKV\tq\u0005\u0005\u0002\u000eQ%\u0011\u0011F\u0004\u0002\u0005\u0019>tw\r\u0003\u0005,\u0001\tE\t\u0015!\u0003(\u0003-)\u0007\u0010]5ssRKW.\u001a\u0011\t\u000b5\u0002A\u0011\u0001\u0018\u0002\rqJg.\u001b;?)\ry\u0013G\r\t\u0003a\u0001i\u0011A\u0001\u0005\u000631\u0002\ra\u0007\u0005\u0006K1\u0002\ra\n\u0005\bi\u0001\t\t\u0011\"\u00016\u0003\u0011\u0019w\u000e]=\u0015\u0007=2t\u0007C\u0004\u001agA\u0005\t\u0019A\u000e\t\u000f\u0015\u001a\u0004\u0013!a\u0001O!9\u0011\bAI\u0001\n\u0003Q\u0014AD2paf$C-\u001a4bk2$H%M\u000b\u0002w)\u00121\u0004P\u0016\u0002{A\u0011ahQ\u0007\u0002\u007f)\u0011\u0001)Q\u0001\nk:\u001c\u0007.Z2lK\u0012T!A\u0011\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002E\u007f\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f\u0019\u0003\u0011\u0013!C\u0001\u000f\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#\u0001%+\u0005\u001db\u0004b\u0002&\u0001\u0003\u0003%\teS\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u00031\u0003\"!\u0014*\u000e\u00039S!a\u0014)\u0002\t1\fgn\u001a\u0006\u0002#\u0006!!.\u0019<b\u0013\t\u0001c\nC\u0004U\u0001\u0005\u0005I\u0011A+\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003Y\u0003\"!D,\n\u0005as!aA%oi\"9!\fAA\u0001\n\u0003Y\u0016A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u00039~\u0003\"!D/\n\u0005ys!aA!os\"9\u0001-WA\u0001\u0002\u00041\u0016a\u0001=%c!9!\rAA\u0001\n\u0003\u001a\u0017a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003\u0011\u00042!\u001a5]\u001b\u00051'BA4\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003S\u001a\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\bW\u0002\t\t\u0011\"\u0001m\u0003!\u0019\u0017M\\#rk\u0006dGCA7q!\tia.\u0003\u0002p\u001d\t9!i\\8mK\u0006t\u0007b\u00021k\u0003\u0003\u0005\r\u0001\u0018\u0005\be\u0002\t\t\u0011\"\u0011t\u0003!A\u0017m\u001d5D_\u0012,G#\u0001,\t\u000fU\u0004\u0011\u0011!C!m\u0006AAo\\*ue&tw\rF\u0001M\u0011\u001dA\b!!A\u0005Be\fa!Z9vC2\u001cHCA7{\u0011\u001d\u0001w/!AA\u0002q;q\u0001 \u0002\u0002\u0002#%Q0A\nCY\u0006\u001c7\u000e\\5ti\u0016$W\t_3dkR|'\u000f\u0005\u00021}\u001a9\u0011AAA\u0001\u0012\u0013y8\u0003\u0002@\u0002\u0002U\u0001r!a\u0001\u0002\nm9s&\u0004\u0002\u0002\u0006)\u0019\u0011q\u0001\b\u0002\u000fI,h\u000e^5nK&!\u00111BA\u0003\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gN\r\u0005\u0007[y$\t!a\u0004\u0015\u0003uDq!\u001e@\u0002\u0002\u0013\u0015c\u000fC\u0005\u0002\u0016y\f\t\u0011\"!\u0002\u0018\u0005)\u0011\r\u001d9msR)q&!\u0007\u0002\u001c!1\u0011$a\u0005A\u0002mAa!JA\n\u0001\u00049\u0003\"CA\u0010}\u0006\u0005I\u0011QA\u0011\u0003\u001d)h.\u00199qYf$B!a\t\u00020A)Q\"!\n\u0002*%\u0019\u0011q\u0005\b\u0003\r=\u0003H/[8o!\u0015i\u00111F\u000e(\u0013\r\tiC\u0004\u0002\u0007)V\u0004H.\u001a\u001a\t\u0013\u0005E\u0012QDA\u0001\u0002\u0004y\u0013a\u0001=%a!I\u0011Q\u0007@\u0002\u0002\u0013%\u0011qG\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002:A\u0019Q*a\u000f\n\u0007\u0005ubJ\u0001\u0004PE*,7\r\u001e"
)
public final class BlacklistedExecutor implements Product, Serializable {
   private final String node;
   private final long expiryTime;

   public static Option<Tuple2<String, Object>> unapply(BlacklistedExecutor var0) {
      return BlacklistedExecutor$.MODULE$.unapply(var0);
   }

   public static BlacklistedExecutor apply(String var0, long var1) {
      return BlacklistedExecutor$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, Object>, BlacklistedExecutor> tupled() {
      return BlacklistedExecutor$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, BlacklistedExecutor>> curried() {
      return BlacklistedExecutor$.MODULE$.curried();
   }

   public String node() {
      return this.node;
   }

   public long expiryTime() {
      return this.expiryTime;
   }

   public BlacklistedExecutor copy(String node, long expiryTime) {
      return new BlacklistedExecutor(node, expiryTime);
   }

   public String copy$default$1() {
      return this.node();
   }

   public long copy$default$2() {
      return this.expiryTime();
   }

   public String productPrefix() {
      return "BlacklistedExecutor";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.node();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.expiryTime());
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
      return x$1 instanceof BlacklistedExecutor;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.node()));
      var1 = Statics.mix(var1, Statics.longHash(this.expiryTime()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof BlacklistedExecutor) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     BlacklistedExecutor var4 = (BlacklistedExecutor)x$1;
                     String var10000 = this.node();
                     String var5 = var4.node();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (this.expiryTime() == var4.expiryTime()) {
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

   public BlacklistedExecutor(String node, long expiryTime) {
      this.node = node;
      this.expiryTime = expiryTime;
      Product$class.$init$(this);
   }
}
