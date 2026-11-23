package org.apache.spark.scheduler;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055c!B\u0001\u0003\u0001\u0012Q!AD#yK\u000e,Ho\u001c:Fq&$X\r\u001a\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0010+A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0013\u000bb,7-\u001e;pe2{7o\u001d*fCN|g\u000e\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012CA\u0004Qe>$Wo\u0019;\u0011\u0005A1\u0012BA\f\u0012\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0002A!f\u0001\n\u0003Y\u0012\u0001C3ySR\u001cu\u000eZ3\u0004\u0001U\tA\u0004\u0005\u0002\u0011;%\u0011a$\u0005\u0002\u0004\u0013:$\b\u0002\u0003\u0011\u0001\u0005#\u0005\u000b\u0011\u0002\u000f\u0002\u0013\u0015D\u0018\u000e^\"pI\u0016\u0004\u0003\u0002\u0003\u0012\u0001\u0005+\u0007I\u0011A\u0012\u0002\u001f\u0015D\u0018\u000e^\"bkN,GMQ=BaB,\u0012\u0001\n\t\u0003!\u0015J!AJ\t\u0003\u000f\t{w\u000e\\3b]\"A\u0001\u0006\u0001B\tB\u0003%A%\u0001\tfq&$8)Y;tK\u0012\u0014\u00150\u00119qA!A!\u0006\u0001BK\u0002\u0013\u00051&\u0001\u0004sK\u0006\u001cxN\\\u000b\u0002YA\u0011Q\u0006\r\b\u0003!9J!aL\t\u0002\rA\u0013X\rZ3g\u0013\t\t$G\u0001\u0004TiJLgn\u001a\u0006\u0003_EA\u0011\u0002\u000e\u0001\u0003\u0012\u0003\u0006I\u0001L\u001b\u0002\u000fI,\u0017m]8oA%\u0011a'D\u0001\b[\u0016\u001c8/Y4f\u0011\u0015A\u0004\u0001\"\u0001:\u0003\u0019a\u0014N\\5u}Q!!h\u000f\u001f>!\ta\u0001\u0001C\u0003\u001ao\u0001\u0007A\u0004C\u0003#o\u0001\u0007A\u0005C\u0003+o\u0001\u0007A\u0006C\u0004@\u0001\u0005\u0005I\u0011\u0001!\u0002\t\r|\u0007/\u001f\u000b\u0005u\u0005\u00135\tC\u0004\u001a}A\u0005\t\u0019\u0001\u000f\t\u000f\tr\u0004\u0013!a\u0001I!9!F\u0010I\u0001\u0002\u0004a\u0003bB#\u0001#\u0003%\tAR\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u00059%F\u0001\u000fIW\u0005I\u0005C\u0001&P\u001b\u0005Y%B\u0001'N\u0003%)hn\u00195fG.,GM\u0003\u0002O#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005A[%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9!\u000bAI\u0001\n\u0003\u0019\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u0002)*\u0012A\u0005\u0013\u0005\b-\u0002\t\n\u0011\"\u0001X\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u0017\u0016\u0003Y!CqA\u0017\u0001\u0002\u0002\u0013\u00053,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00029B\u0011QLY\u0007\u0002=*\u0011q\fY\u0001\u0005Y\u0006twMC\u0001b\u0003\u0011Q\u0017M^1\n\u0005Er\u0006b\u00023\u0001\u0003\u0003%\taG\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\bM\u0002\t\t\u0011\"\u0001h\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001[6\u0011\u0005AI\u0017B\u00016\u0012\u0005\r\te.\u001f\u0005\bY\u0016\f\t\u00111\u0001\u001d\u0003\rAH%\r\u0005\b]\u0002\t\t\u0011\"\u0011p\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00019\u0011\u0007E$\b.D\u0001s\u0015\t\u0019\u0018#\u0001\u0006d_2dWm\u0019;j_:L!!\u001e:\u0003\u0011%#XM]1u_JDqa\u001e\u0001\u0002\u0002\u0013\u0005\u00010\u0001\u0005dC:,\u0015/^1m)\t!\u0013\u0010C\u0004mm\u0006\u0005\t\u0019\u00015\t\u000fm\u0004\u0011\u0011!C!y\u0006A\u0001.Y:i\u0007>$W\rF\u0001\u001d\u0011\u001dq\b!!A\u0005B}\fa!Z9vC2\u001cHc\u0001\u0013\u0002\u0002!9A.`A\u0001\u0002\u0004Aw\u0001CA\u0003\u0005!\u0005A!a\u0002\u0002\u001d\u0015CXmY;u_J,\u00050\u001b;fIB\u0019A\"!\u0003\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0002\fM)\u0011\u0011BA\u0007+A\u0019\u0001#a\u0004\n\u0007\u0005E\u0011C\u0001\u0004B]f\u0014VM\u001a\u0005\bq\u0005%A\u0011AA\u000b)\t\t9\u0001\u0003\u0005\u0002\u001a\u0005%A\u0011AA\u000e\u0003\u0015\t\u0007\u000f\u001d7z)\u0015Q\u0014QDA\u0010\u0011\u0019I\u0012q\u0003a\u00019!1!%a\u0006A\u0002\u0011B!\"!\u0007\u0002\n\u0005\u0005I\u0011QA\u0012)\u001dQ\u0014QEA\u0014\u0003SAa!GA\u0011\u0001\u0004a\u0002B\u0002\u0012\u0002\"\u0001\u0007A\u0005\u0003\u0004+\u0003C\u0001\r\u0001\f\u0005\u000b\u0003[\tI!!A\u0005\u0002\u0006=\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0003c\ti\u0004E\u0003\u0011\u0003g\t9$C\u0002\u00026E\u0011aa\u00149uS>t\u0007C\u0002\t\u0002:q!C&C\u0002\u0002<E\u0011a\u0001V;qY\u0016\u001c\u0004\"CA \u0003W\t\t\u00111\u0001;\u0003\rAH\u0005\r\u0005\u000b\u0003\u0007\nI!!A\u0005\n\u0005\u0015\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0012\u0011\u0007u\u000bI%C\u0002\u0002Ly\u0013aa\u00142kK\u000e$\b"
)
public class ExecutorExited extends ExecutorLossReason implements Product {
   private final int exitCode;
   private final boolean exitCausedByApp;

   public static Option<Tuple3<Object, Object, String>> unapply(ExecutorExited var0) {
      return ExecutorExited$.MODULE$.unapply(var0);
   }

   public static ExecutorExited apply(int var0, boolean var1, String var2) {
      return ExecutorExited$.MODULE$.apply(var0, var1, var2);
   }

   public static ExecutorExited apply(int var0, boolean var1) {
      return ExecutorExited$.MODULE$.apply(var0, var1);
   }

   public int exitCode() {
      return this.exitCode;
   }

   public boolean exitCausedByApp() {
      return this.exitCausedByApp;
   }

   public String reason() {
      return super.message();
   }

   public ExecutorExited copy(int exitCode, boolean exitCausedByApp, String reason) {
      return new ExecutorExited(exitCode, exitCausedByApp, reason);
   }

   public int copy$default$1() {
      return this.exitCode();
   }

   public boolean copy$default$2() {
      return this.exitCausedByApp();
   }

   public String copy$default$3() {
      return this.reason();
   }

   public String productPrefix() {
      return "ExecutorExited";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.exitCode());
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
      return x$1 instanceof ExecutorExited;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.exitCode());
      var1 = Statics.mix(var1, this.exitCausedByApp() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      return Statics.finalizeHash(var1, 3);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof ExecutorExited) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  ExecutorExited var4 = (ExecutorExited)x$1;
                  if (this.exitCode() == var4.exitCode() && this.exitCausedByApp() == var4.exitCausedByApp()) {
                     label36: {
                        String var10000 = this.reason();
                        String var5 = var4.reason();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label36;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label36;
                        }

                        if (var4.canEqual(this)) {
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

   public ExecutorExited(int exitCode, boolean exitCausedByApp, String reason) {
      super(reason);
      this.exitCode = exitCode;
      this.exitCausedByApp = exitCausedByApp;
      Product$class.$init$(this);
   }
}
