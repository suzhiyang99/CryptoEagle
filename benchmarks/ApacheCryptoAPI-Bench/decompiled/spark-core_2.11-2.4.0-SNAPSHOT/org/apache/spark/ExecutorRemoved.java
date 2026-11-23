package org.apache.spark;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]a\u0001B\u0001\u0003\t&\u0011q\"\u0012=fGV$xN\u001d*f[>4X\r\u001a\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\u000b!M\u0001\"a\u0003\b\u000e\u00031Q\u0011!D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001f1\u0011a!\u00118z%\u00164\u0007CA\u0006\u0012\u0013\t\u0011BBA\u0004Qe>$Wo\u0019;\u0011\u0005-!\u0012BA\u000b\r\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!9\u0002A!f\u0001\n\u0003A\u0012AC3yK\u000e,Ho\u001c:JIV\t\u0011\u0004\u0005\u0002\u001b;9\u00111bG\u0005\u000391\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0010 \u0005\u0019\u0019FO]5oO*\u0011A\u0004\u0004\u0005\tC\u0001\u0011\t\u0012)A\u00053\u0005YQ\r_3dkR|'/\u00133!\u0011\u0015\u0019\u0003\u0001\"\u0001%\u0003\u0019a\u0014N\\5u}Q\u0011Qe\n\t\u0003M\u0001i\u0011A\u0001\u0005\u0006/\t\u0002\r!\u0007\u0005\bS\u0001\t\t\u0011\"\u0001+\u0003\u0011\u0019w\u000e]=\u0015\u0005\u0015Z\u0003bB\f)!\u0003\u0005\r!\u0007\u0005\b[\u0001\t\n\u0011\"\u0001/\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\f\u0016\u00033AZ\u0013!\r\t\u0003e]j\u0011a\r\u0006\u0003iU\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005Yb\u0011AC1o]>$\u0018\r^5p]&\u0011\u0001h\r\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002\u001e\u0001\u0003\u0003%\teO\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003q\u0002\"!\u0010\"\u000e\u0003yR!a\u0010!\u0002\t1\fgn\u001a\u0006\u0002\u0003\u0006!!.\u0019<b\u0013\tqb\bC\u0004E\u0001\u0005\u0005I\u0011A#\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u0019\u0003\"aC$\n\u0005!c!aA%oi\"9!\nAA\u0001\n\u0003Y\u0015A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003\u0019>\u0003\"aC'\n\u00059c!aA!os\"9\u0001+SA\u0001\u0002\u00041\u0015a\u0001=%c!9!\u000bAA\u0001\n\u0003\u001a\u0016a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003Q\u00032!\u0016-M\u001b\u00051&BA,\r\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u00033Z\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b7\u0002\t\t\u0011\"\u0001]\u0003!\u0019\u0017M\\#rk\u0006dGCA/a!\tYa,\u0003\u0002`\u0019\t9!i\\8mK\u0006t\u0007b\u0002)[\u0003\u0003\u0005\r\u0001\u0014\u0005\bE\u0002\t\t\u0011\"\u0011d\u0003!A\u0017m\u001d5D_\u0012,G#\u0001$\t\u000f\u0015\u0004\u0011\u0011!C!M\u0006AAo\\*ue&tw\rF\u0001=\u0011\u001dA\u0007!!A\u0005B%\fa!Z9vC2\u001cHCA/k\u0011\u001d\u0001v-!AA\u00021;q\u0001\u001c\u0002\u0002\u0002#%Q.A\bFq\u0016\u001cW\u000f^8s%\u0016lwN^3e!\t1cNB\u0004\u0002\u0005\u0005\u0005\t\u0012B8\u0014\u00079\u00048\u0003\u0005\u0003rif)S\"\u0001:\u000b\u0005Md\u0011a\u0002:v]RLW.Z\u0005\u0003kJ\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u0015\u0019c\u000e\"\u0001x)\u0005i\u0007bB3o\u0003\u0003%)E\u001a\u0005\bu:\f\t\u0011\"!|\u0003\u0015\t\u0007\u000f\u001d7z)\t)C\u0010C\u0003\u0018s\u0002\u0007\u0011\u0004C\u0004\u007f]\u0006\u0005I\u0011Q@\u0002\u000fUt\u0017\r\u001d9msR!\u0011\u0011AA\u0004!\u0011Y\u00111A\r\n\u0007\u0005\u0015AB\u0001\u0004PaRLwN\u001c\u0005\t\u0003\u0013i\u0018\u0011!a\u0001K\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u00055a.!A\u0005\n\u0005=\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0005\u0011\u0007u\n\u0019\"C\u0002\u0002\u0016y\u0012aa\u00142kK\u000e$\b"
)
public class ExecutorRemoved implements Product, Serializable {
   private final String executorId;

   public static Option<String> unapply(ExecutorRemoved var0) {
      return ExecutorRemoved$.MODULE$.unapply(var0);
   }

   public static ExecutorRemoved apply(String var0) {
      return ExecutorRemoved$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<ExecutorRemoved, A> var0) {
      return ExecutorRemoved$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, ExecutorRemoved> compose(Function1<A, String> var0) {
      return ExecutorRemoved$.MODULE$.compose(var0);
   }

   public String executorId() {
      return this.executorId;
   }

   public ExecutorRemoved copy(String executorId) {
      return new ExecutorRemoved(executorId);
   }

   public String copy$default$1() {
      return this.executorId();
   }

   public String productPrefix() {
      return "ExecutorRemoved";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.executorId();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ExecutorRemoved;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof ExecutorRemoved) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     ExecutorRemoved var4 = (ExecutorRemoved)x$1;
                     String var10000 = this.executorId();
                     String var5 = var4.executorId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
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

   public ExecutorRemoved(String executorId) {
      this.executorId = executorId;
      Product$class.$init$(this);
   }
}
