package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-c!B\u0001\u0003\u0001\u0012Q!!C*mCZ,Gj\\:u\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017=)\u0002C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005I)\u00050Z2vi>\u0014Hj\\:t%\u0016\f7o\u001c8\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\u000fA\u0013x\u000eZ;diB\u0011\u0001CF\u0005\u0003/E\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\u0007\u0001\u0003\u0016\u0004%\taG\u0001\t?6,7o]1hK\u000e\u0001Q#\u0001\u000f\u0011\u0005u\u0001cB\u0001\t\u001f\u0013\ty\u0012#\u0001\u0004Qe\u0016$WMZ\u0005\u0003C\t\u0012aa\u0015;sS:<'BA\u0010\u0012\u0011%!\u0003A!E!\u0002\u0013aR%A\u0005`[\u0016\u001c8/Y4fA%\u0011a%D\u0001\b[\u0016\u001c8/Y4f\u0011!A\u0003A!f\u0001\n\u0003I\u0013AC<pe.,'\u000fT8tiV\t!\u0006\u0005\u0002\u0011W%\u0011A&\u0005\u0002\b\u0005>|G.Z1o\u0011!q\u0003A!E!\u0002\u0013Q\u0013aC<pe.,'\u000fT8ti\u0002BQ\u0001\r\u0001\u0005\u0002E\na\u0001P5oSRtDc\u0001\u001a4iA\u0011A\u0002\u0001\u0005\b3=\u0002\n\u00111\u0001\u001d\u0011\u001dAs\u0006%AA\u0002)BqA\u000e\u0001\u0002\u0002\u0013\u0005q'\u0001\u0003d_BLHc\u0001\u001a9s!9\u0011$\u000eI\u0001\u0002\u0004a\u0002b\u0002\u00156!\u0003\u0005\rA\u000b\u0005\bw\u0001\t\n\u0011\"\u0001=\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u0010\u0016\u00039yZ\u0013a\u0010\t\u0003\u0001\u0016k\u0011!\u0011\u0006\u0003\u0005\u000e\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0011\u000b\u0012AC1o]>$\u0018\r^5p]&\u0011a)\u0011\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002%\u0001#\u0003%\t!S\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005Q%F\u0001\u0016?\u0011\u001da\u0005!!A\u0005B5\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001(\u0011\u0005=#V\"\u0001)\u000b\u0005E\u0013\u0016\u0001\u00027b]\u001eT\u0011aU\u0001\u0005U\u00064\u0018-\u0003\u0002\"!\"9a\u000bAA\u0001\n\u00039\u0016\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001-\u0011\u0005AI\u0016B\u0001.\u0012\u0005\rIe\u000e\u001e\u0005\b9\u0002\t\t\u0011\"\u0001^\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"AX1\u0011\u0005Ay\u0016B\u00011\u0012\u0005\r\te.\u001f\u0005\bEn\u000b\t\u00111\u0001Y\u0003\rAH%\r\u0005\bI\u0002\t\t\u0011\"\u0011f\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00014\u0011\u0007\u001dTg,D\u0001i\u0015\tI\u0017#\u0001\u0006d_2dWm\u0019;j_:L!a\u001b5\u0003\u0011%#XM]1u_JDq!\u001c\u0001\u0002\u0002\u0013\u0005a.\u0001\u0005dC:,\u0015/^1m)\tQs\u000eC\u0004cY\u0006\u0005\t\u0019\u00010\t\u000fE\u0004\u0011\u0011!C!e\u0006A\u0001.Y:i\u0007>$W\rF\u0001Y\u0011\u001d!\b!!A\u0005BU\fa!Z9vC2\u001cHC\u0001\u0016w\u0011\u001d\u00117/!AA\u0002y;\u0001\u0002\u001f\u0002\u0002\u0002#\u0005A!_\u0001\n'2\fg/\u001a'pgR\u0004\"\u0001\u0004>\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\tm\u001c2A\u001f?\u0016!\u0019i\u0018\u0011\u0001\u000f+e5\taP\u0003\u0002\u0000#\u00059!/\u001e8uS6,\u0017bAA\u0002}\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\rARH\u0011AA\u0004)\u0005I\b\"CA\u0006u\u0006\u0005IQIA\u0007\u0003!!xn\u0015;sS:<G#\u0001(\t\u0013\u0005E!0!A\u0005\u0002\u0006M\u0011!B1qa2LH#\u0002\u001a\u0002\u0016\u0005]\u0001\u0002C\r\u0002\u0010A\u0005\t\u0019\u0001\u000f\t\u0011!\ny\u0001%AA\u0002)B\u0011\"a\u0007{\u0003\u0003%\t)!\b\u0002\u000fUt\u0017\r\u001d9msR!\u0011qDA\u0016!\u0015\u0001\u0012\u0011EA\u0013\u0013\r\t\u0019#\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u000bA\t9\u0003\b\u0016\n\u0007\u0005%\u0012C\u0001\u0004UkBdWM\r\u0005\n\u0003[\tI\"!AA\u0002I\n1\u0001\u001f\u00131\u0011!\t\tD_I\u0001\n\u0003a\u0014aD1qa2LH\u0005Z3gCVdG\u000fJ\u0019\t\u0011\u0005U\"0%A\u0005\u0002%\u000bq\"\u00199qYf$C-\u001a4bk2$HE\r\u0005\t\u0003sQ\u0018\u0013!C\u0001y\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIEB\u0001\"!\u0010{#\u0003%\t!S\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001a\t\u0013\u0005\u0005#0!A\u0005\n\u0005\r\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0012\u0011\u0007=\u000b9%C\u0002\u0002JA\u0013aa\u00142kK\u000e$\b"
)
public class SlaveLost extends ExecutorLossReason implements Product {
   private final boolean workerLost;

   public static boolean $lessinit$greater$default$2() {
      return SlaveLost$.MODULE$.$lessinit$greater$default$2();
   }

   public static String $lessinit$greater$default$1() {
      return SlaveLost$.MODULE$.$lessinit$greater$default$1();
   }

   public static boolean apply$default$2() {
      return SlaveLost$.MODULE$.apply$default$2();
   }

   public static String apply$default$1() {
      return SlaveLost$.MODULE$.apply$default$1();
   }

   public static Option<Tuple2<String, Object>> unapply(SlaveLost var0) {
      return SlaveLost$.MODULE$.unapply(var0);
   }

   public static SlaveLost apply(String var0, boolean var1) {
      return SlaveLost$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, Object>, SlaveLost> tupled() {
      return SlaveLost$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, SlaveLost>> curried() {
      return SlaveLost$.MODULE$.curried();
   }

   public String _message() {
      return super.message();
   }

   public boolean workerLost() {
      return this.workerLost;
   }

   public SlaveLost copy(String _message, boolean workerLost) {
      return new SlaveLost(_message, workerLost);
   }

   public String copy$default$1() {
      return this._message();
   }

   public boolean copy$default$2() {
      return this.workerLost();
   }

   public String productPrefix() {
      return "SlaveLost";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this._message();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToBoolean(this.workerLost());
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
      return x$1 instanceof SlaveLost;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this._message()));
      var1 = Statics.mix(var1, this.workerLost() ? 1231 : 1237);
      return Statics.finalizeHash(var1, 2);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof SlaveLost) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  label36: {
                     SlaveLost var4 = (SlaveLost)x$1;
                     String var10000 = this._message();
                     String var5 = var4._message();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label36;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label36;
                     }

                     if (this.workerLost() == var4.workerLost() && var4.canEqual(this)) {
                        var7 = true;
                        break label37;
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

   public SlaveLost(String _message, boolean workerLost) {
      super(_message);
      this.workerLost = workerLost;
      Product$class.$init$(this);
   }
}
