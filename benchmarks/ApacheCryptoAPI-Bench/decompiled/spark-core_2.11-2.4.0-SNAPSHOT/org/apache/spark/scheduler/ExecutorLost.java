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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015c!B\u0001\u0003\u0001\nQ!\u0001D#yK\u000e,Ho\u001c:M_N$(BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0015\u00011\"E\u000b\u0019!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!cE\u0007\u0002\u0005%\u0011AC\u0001\u0002\u0012\t\u0006;5k\u00195fIVdWM]#wK:$\bC\u0001\u0007\u0017\u0013\t9RBA\u0004Qe>$Wo\u0019;\u0011\u00051I\u0012B\u0001\u000e\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!a\u0002A!f\u0001\n\u0003q\u0012AB3yK\u000eLEm\u0001\u0001\u0016\u0003}\u0001\"\u0001I\u0012\u000f\u00051\t\u0013B\u0001\u0012\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011A%\n\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tj\u0001\u0002C\u0014\u0001\u0005#\u0005\u000b\u0011B\u0010\u0002\u000f\u0015DXmY%eA!A\u0011\u0006\u0001BK\u0002\u0013\u0005!&\u0001\u0004sK\u0006\u001cxN\\\u000b\u0002WA\u0011!\u0003L\u0005\u0003[\t\u0011!#\u0012=fGV$xN\u001d'pgN\u0014V-Y:p]\"Aq\u0006\u0001B\tB\u0003%1&A\u0004sK\u0006\u001cxN\u001c\u0011\t\u000bE\u0002A\u0011\u0001\u001a\u0002\rqJg.\u001b;?)\r\u0019D'\u000e\t\u0003%\u0001AQ\u0001\b\u0019A\u0002}AQ!\u000b\u0019A\u0002-Bqa\u000e\u0001\u0002\u0002\u0013\u0005\u0001(\u0001\u0003d_BLHcA\u001a:u!9AD\u000eI\u0001\u0002\u0004y\u0002bB\u00157!\u0003\u0005\ra\u000b\u0005\by\u0001\t\n\u0011\"\u0001>\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012A\u0010\u0016\u0003?}Z\u0013\u0001\u0011\t\u0003\u0003\u001ak\u0011A\u0011\u0006\u0003\u0007\u0012\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0015k\u0011AC1o]>$\u0018\r^5p]&\u0011qI\u0011\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB%\u0001#\u0003%\tAS\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005Y%FA\u0016@\u0011\u001di\u0005!!A\u0005B9\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A(\u0011\u0005A+V\"A)\u000b\u0005I\u001b\u0016\u0001\u00027b]\u001eT\u0011\u0001V\u0001\u0005U\u00064\u0018-\u0003\u0002%#\"9q\u000bAA\u0001\n\u0003A\u0016\u0001\u00049s_\u0012,8\r^!sSRLX#A-\u0011\u00051Q\u0016BA.\u000e\u0005\rIe\u000e\u001e\u0005\b;\u0002\t\t\u0011\"\u0001_\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\u00182\u0011\u00051\u0001\u0017BA1\u000e\u0005\r\te.\u001f\u0005\bGr\u000b\t\u00111\u0001Z\u0003\rAH%\r\u0005\bK\u0002\t\t\u0011\"\u0011g\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A4\u0011\u0007!\\w,D\u0001j\u0015\tQW\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001\\5\u0003\u0011%#XM]1u_JDqA\u001c\u0001\u0002\u0002\u0013\u0005q.\u0001\u0005dC:,\u0015/^1m)\t\u00018\u000f\u0005\u0002\rc&\u0011!/\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\u0019W.!AA\u0002}Cq!\u001e\u0001\u0002\u0002\u0013\u0005c/\u0001\u0005iCND7i\u001c3f)\u0005I\u0006b\u0002=\u0001\u0003\u0003%\t%_\u0001\ti>\u001cFO]5oOR\tq\nC\u0004|\u0001\u0005\u0005I\u0011\t?\u0002\r\u0015\fX/\u00197t)\t\u0001X\u0010C\u0004du\u0006\u0005\t\u0019A0\b\u0013}\u0014\u0011\u0011!E\u0001\u0005\u0005\u0005\u0011\u0001D#yK\u000e,Ho\u001c:M_N$\bc\u0001\n\u0002\u0004\u0019I\u0011AAA\u0001\u0012\u0003\u0011\u0011QA\n\u0006\u0003\u0007\t9\u0001\u0007\t\b\u0003\u0013\tyaH\u00164\u001b\t\tYAC\u0002\u0002\u000e5\tqA];oi&lW-\u0003\u0003\u0002\u0012\u0005-!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!9\u0011'a\u0001\u0005\u0002\u0005UACAA\u0001\u0011!A\u00181AA\u0001\n\u000bJ\bBCA\u000e\u0003\u0007\t\t\u0011\"!\u0002\u001e\u0005)\u0011\r\u001d9msR)1'a\b\u0002\"!1A$!\u0007A\u0002}Aa!KA\r\u0001\u0004Y\u0003BCA\u0013\u0003\u0007\t\t\u0011\"!\u0002(\u00059QO\\1qa2LH\u0003BA\u0015\u0003k\u0001R\u0001DA\u0016\u0003_I1!!\f\u000e\u0005\u0019y\u0005\u000f^5p]B)A\"!\r W%\u0019\u00111G\u0007\u0003\rQ+\b\u000f\\33\u0011%\t9$a\t\u0002\u0002\u0003\u00071'A\u0002yIAB!\"a\u000f\u0002\u0004\u0005\u0005I\u0011BA\u001f\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005}\u0002c\u0001)\u0002B%\u0019\u00111I)\u0003\r=\u0013'.Z2u\u0001"
)
public class ExecutorLost implements DAGSchedulerEvent, Product, Serializable {
   private final String execId;
   private final ExecutorLossReason reason;

   public static Option<Tuple2<String, ExecutorLossReason>> unapply(ExecutorLost var0) {
      return ExecutorLost$.MODULE$.unapply(var0);
   }

   public static ExecutorLost apply(String var0, ExecutorLossReason var1) {
      return ExecutorLost$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, ExecutorLossReason>, ExecutorLost> tupled() {
      return ExecutorLost$.MODULE$.tupled();
   }

   public static Function1<String, Function1<ExecutorLossReason, ExecutorLost>> curried() {
      return ExecutorLost$.MODULE$.curried();
   }

   public String execId() {
      return this.execId;
   }

   public ExecutorLossReason reason() {
      return this.reason;
   }

   public ExecutorLost copy(String execId, ExecutorLossReason reason) {
      return new ExecutorLost(execId, reason);
   }

   public String copy$default$1() {
      return this.execId();
   }

   public ExecutorLossReason copy$default$2() {
      return this.reason();
   }

   public String productPrefix() {
      return "ExecutorLost";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.execId();
            break;
         case 1:
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
      return x$1 instanceof ExecutorLost;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof ExecutorLost) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     ExecutorLost var4 = (ExecutorLost)x$1;
                     String var10000 = this.execId();
                     String var5 = var4.execId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     ExecutorLossReason var7 = this.reason();
                     ExecutorLossReason var6 = var4.reason();
                     if (var7 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var7.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public ExecutorLost(String execId, ExecutorLossReason reason) {
      this.execId = execId;
      this.reason = reason;
      Product$class.$init$(this);
   }
}
