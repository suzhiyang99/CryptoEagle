package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Uc\u0001B\u0001\u0003\u0001.\u0011\u0001C\u0011:pC\u0012\u001c\u0017m\u001d;CY>\u001c7.\u00133\u000b\u0005\r!\u0011aB:u_J\fw-\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\r!Y\u0001\"!\u0004\b\u000e\u0003\tI!a\u0004\u0002\u0003\u000f\tcwnY6JIB\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t9\u0001K]8ek\u000e$\bCA\t\u0018\u0013\tA\"C\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001b\u0001\tU\r\u0011\"\u0001\u001c\u0003-\u0011'o\\1eG\u0006\u001cH/\u00133\u0016\u0003q\u0001\"!E\u000f\n\u0005y\u0011\"\u0001\u0002'p]\u001eD\u0001\u0002\t\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\rEJ|\u0017\rZ2bgRLE\r\t\u0005\tE\u0001\u0011)\u001a!C\u0001G\u0005)a-[3mIV\tA\u0005\u0005\u0002&Q9\u0011\u0011CJ\u0005\u0003OI\ta\u0001\u0015:fI\u00164\u0017BA\u0015+\u0005\u0019\u0019FO]5oO*\u0011qE\u0005\u0005\tY\u0001\u0011\t\u0012)A\u0005I\u00051a-[3mI\u0002BQA\f\u0001\u0005\u0002=\na\u0001P5oSRtDc\u0001\u00192eA\u0011Q\u0002\u0001\u0005\u000655\u0002\r\u0001\b\u0005\bE5\u0002\n\u00111\u0001%\u0011\u0015!\u0004\u0001\"\u0011$\u0003\u0011q\u0017-\\3\t\u000fY\u0002\u0011\u0011!C\u0001o\u0005!1m\u001c9z)\r\u0001\u0004(\u000f\u0005\b5U\u0002\n\u00111\u0001\u001d\u0011\u001d\u0011S\u0007%AA\u0002\u0011Bqa\u000f\u0001\u0012\u0002\u0013\u0005A(\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003uR#\u0001\b ,\u0003}\u0002\"\u0001Q#\u000e\u0003\u0005S!AQ\"\u0002\u0013Ut7\r[3dW\u0016$'B\u0001#\u0013\u0003)\tgN\\8uCRLwN\\\u0005\u0003\r\u0006\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001dA\u0005!%A\u0005\u0002%\u000babY8qs\u0012\"WMZ1vYR$#'F\u0001KU\t!c\bC\u0004M\u0001\u0005\u0005I\u0011I'\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005q\u0005CA(U\u001b\u0005\u0001&BA)S\u0003\u0011a\u0017M\\4\u000b\u0003M\u000bAA[1wC&\u0011\u0011\u0006\u0015\u0005\b-\u0002\t\t\u0011\"\u0001X\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005A\u0006CA\tZ\u0013\tQ&CA\u0002J]RDq\u0001\u0018\u0001\u0002\u0002\u0013\u0005Q,\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005y\u000b\u0007CA\t`\u0013\t\u0001'CA\u0002B]fDqAY.\u0002\u0002\u0003\u0007\u0001,A\u0002yIEBq\u0001\u001a\u0001\u0002\u0002\u0013\u0005S-A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u00051\u0007cA4k=6\t\u0001N\u0003\u0002j%\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005-D'\u0001C%uKJ\fGo\u001c:\t\u000f5\u0004\u0011\u0011!C\u0001]\u0006A1-\u00198FcV\fG\u000e\u0006\u0002peB\u0011\u0011\u0003]\u0005\u0003cJ\u0011qAQ8pY\u0016\fg\u000eC\u0004cY\u0006\u0005\t\u0019\u00010\t\u000fQ\u0004\u0011\u0011!C!k\u0006A\u0001.Y:i\u0007>$W\rF\u0001Y\u0011\u001d9\b!!A\u0005Ba\fa!Z9vC2\u001cHCA8z\u0011\u001d\u0011g/!AA\u0002yC#\u0001A>\u0011\u0005qtX\"A?\u000b\u0005\u0011#\u0011BA@~\u00051!UM^3m_B,'/\u00119j\u000f%\t\u0019AAA\u0001\u0012\u0003\t)!\u0001\tCe>\fGmY1ti\ncwnY6JIB\u0019Q\"a\u0002\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\u0003\u0013\u0019R!a\u0002\u0002\fY\u0001r!!\u0004\u0002\u0014q!\u0003'\u0004\u0002\u0002\u0010)\u0019\u0011\u0011\u0003\n\u0002\u000fI,h\u000e^5nK&!\u0011QCA\b\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gN\r\u0005\b]\u0005\u001dA\u0011AA\r)\t\t)\u0001\u0003\u0006\u0002\u001e\u0005\u001d\u0011\u0011!C#\u0003?\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002\u001d\"Q\u00111EA\u0004\u0003\u0003%\t)!\n\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000bA\n9#!\u000b\t\ri\t\t\u00031\u0001\u001d\u0011!\u0011\u0013\u0011\u0005I\u0001\u0002\u0004!\u0003BCA\u0017\u0003\u000f\t\t\u0011\"!\u00020\u00059QO\\1qa2LH\u0003BA\u0019\u0003{\u0001R!EA\u001a\u0003oI1!!\u000e\u0013\u0005\u0019y\u0005\u000f^5p]B)\u0011#!\u000f\u001dI%\u0019\u00111\b\n\u0003\rQ+\b\u000f\\33\u0011%\ty$a\u000b\u0002\u0002\u0003\u0007\u0001'A\u0002yIAB\u0011\"a\u0011\u0002\bE\u0005I\u0011A%\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIIB\u0011\"a\u0012\u0002\bE\u0005I\u0011A%\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00133\u0011)\tY%a\u0002\u0002\u0002\u0013%\u0011QJ\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002PA\u0019q*!\u0015\n\u0007\u0005M\u0003K\u0001\u0004PE*,7\r\u001e"
)
public class BroadcastBlockId extends BlockId implements Product, Serializable {
   private final long broadcastId;
   private final String field;

   public static String $lessinit$greater$default$2() {
      return BroadcastBlockId$.MODULE$.$lessinit$greater$default$2();
   }

   public static String apply$default$2() {
      return BroadcastBlockId$.MODULE$.apply$default$2();
   }

   public static Option<Tuple2<Object, String>> unapply(BroadcastBlockId var0) {
      return BroadcastBlockId$.MODULE$.unapply(var0);
   }

   public static BroadcastBlockId apply(long var0, String var2) {
      return BroadcastBlockId$.MODULE$.apply(var0, var2);
   }

   public static Function1<Tuple2<Object, String>, BroadcastBlockId> tupled() {
      return BroadcastBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, BroadcastBlockId>> curried() {
      return BroadcastBlockId$.MODULE$.curried();
   }

   public long broadcastId() {
      return this.broadcastId;
   }

   public String field() {
      return this.field;
   }

   public String name() {
      String var2;
      StringBuilder var10000;
      label23: {
         var10000 = (new StringBuilder()).append((Object)"broadcast_").append((Object)BoxesRunTime.boxToLong(this.broadcastId()));
         var2 = this.field();
         String var1 = "";
         if (var2 == null) {
            if (var1 == null) {
               break label23;
            }
         } else if (var2.equals(var1)) {
            break label23;
         }

         var2 = (new StringBuilder()).append((Object)"_").append((Object)this.field()).toString();
         return var10000.append((Object)var2).toString();
      }

      var2 = "";
      return var10000.append((Object)var2).toString();
   }

   public BroadcastBlockId copy(long broadcastId, String field) {
      return new BroadcastBlockId(broadcastId, field);
   }

   public long copy$default$1() {
      return this.broadcastId();
   }

   public String copy$default$2() {
      return this.field();
   }

   public String productPrefix() {
      return "BroadcastBlockId";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.broadcastId());
            break;
         case 1:
            var10000 = this.field();
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
      return x$1 instanceof BroadcastBlockId;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.broadcastId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.field()));
      return Statics.finalizeHash(var1, 2);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof BroadcastBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  BroadcastBlockId var4 = (BroadcastBlockId)x$1;
                  if (this.broadcastId() == var4.broadcastId()) {
                     label35: {
                        String var10000 = this.field();
                        String var5 = var4.field();
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

   public BroadcastBlockId(long broadcastId, String field) {
      this.broadcastId = broadcastId;
      this.field = field;
      Product$class.$init$(this);
   }
}
