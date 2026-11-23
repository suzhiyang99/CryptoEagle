package org.apache.spark.api.python;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eb!B\u0001\u0003\u0001\u001aa!AF\"iC&tW\r\u001a)zi\"|gNR;oGRLwN\\:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cB\u0001A\u0007\u0014-A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001d]I!\u0001G\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002q\tQAZ;oGN\u001c\u0001!F\u0001\u001e!\rqb%\u000b\b\u0003?\u0011r!\u0001I\u0012\u000e\u0003\u0005R!AI\u000e\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012BA\u0013\u0010\u0003\u001d\u0001\u0018mY6bO\u0016L!a\n\u0015\u0003\u0007M+\u0017O\u0003\u0002&\u001fA\u0011!fK\u0007\u0002\u0005%\u0011AF\u0001\u0002\u000f!f$\bn\u001c8Gk:\u001cG/[8o\u0011!q\u0003A!E!\u0002\u0013i\u0012A\u00024v]\u000e\u001c\b\u0005C\u00031\u0001\u0011\u0005\u0011'\u0001\u0004=S:LGO\u0010\u000b\u0003eM\u0002\"A\u000b\u0001\t\u000biy\u0003\u0019A\u000f\t\u000fU\u0002\u0011\u0011!C\u0001m\u0005!1m\u001c9z)\t\u0011t\u0007C\u0004\u001biA\u0005\t\u0019A\u000f\t\u000fe\u0002\u0011\u0013!C\u0001u\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\u001e+\u0005ua4&A\u001f\u0011\u0005y\u001aU\"A \u000b\u0005\u0001\u000b\u0015!C;oG\",7m[3e\u0015\t\u0011u\"\u0001\u0006b]:|G/\u0019;j_:L!\u0001R \u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004G\u0001\u0005\u0005I\u0011I$\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005A\u0005CA%O\u001b\u0005Q%BA&M\u0003\u0011a\u0017M\\4\u000b\u00035\u000bAA[1wC&\u0011qJ\u0013\u0002\u0007'R\u0014\u0018N\\4\t\u000fE\u0003\u0011\u0011!C\u0001%\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t1\u000b\u0005\u0002\u000f)&\u0011Qk\u0004\u0002\u0004\u0013:$\bbB,\u0001\u0003\u0003%\t\u0001W\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tIF\f\u0005\u0002\u000f5&\u00111l\u0004\u0002\u0004\u0003:L\bbB/W\u0003\u0003\u0005\raU\u0001\u0004q\u0012\n\u0004bB0\u0001\u0003\u0003%\t\u0005Y\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0011\rE\u0002cKfk\u0011a\u0019\u0006\u0003I>\t!bY8mY\u0016\u001cG/[8o\u0013\t17M\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dA\u0007!!A\u0005\u0002%\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003U6\u0004\"AD6\n\u00051|!a\u0002\"p_2,\u0017M\u001c\u0005\b;\u001e\f\t\u00111\u0001Z\u0011\u001dy\u0007!!A\u0005BA\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002'\"9!\u000fAA\u0001\n\u0003\u001a\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003!Cq!\u001e\u0001\u0002\u0002\u0013\u0005c/\u0001\u0004fcV\fGn\u001d\u000b\u0003U^Dq!\u0018;\u0002\u0002\u0003\u0007\u0011l\u0002\u0005z\u0005\u0005\u0005\t\u0012\u0001\u0004{\u0003Y\u0019\u0005.Y5oK\u0012\u0004\u0016\u0010\u001e5p]\u001a+hn\u0019;j_:\u001c\bC\u0001\u0016|\r!\t!!!A\t\u0002\u0019a8cA>~-A)a0a\u0001\u001ee5\tqPC\u0002\u0002\u0002=\tqA];oi&lW-C\u0002\u0002\u0006}\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u0019\u00014\u0010\"\u0001\u0002\nQ\t!\u0010C\u0004sw\u0006\u0005IQI:\t\u0013\u0005=10!A\u0005\u0002\u0006E\u0011!B1qa2LHc\u0001\u001a\u0002\u0014!1!$!\u0004A\u0002uA\u0011\"a\u0006|\u0003\u0003%\t)!\u0007\u0002\u000fUt\u0017\r\u001d9msR!\u00111DA\u0011!\u0011q\u0011QD\u000f\n\u0007\u0005}qB\u0001\u0004PaRLwN\u001c\u0005\n\u0003G\t)\"!AA\u0002I\n1\u0001\u001f\u00131\u0011%\t9c_A\u0001\n\u0013\tI#A\u0006sK\u0006$'+Z:pYZ,GCAA\u0016!\rI\u0015QF\u0005\u0004\u0003_Q%AB(cU\u0016\u001cG\u000f"
)
public class ChainedPythonFunctions implements Product, Serializable {
   private final Seq<PythonFunction> funcs;

   public static Option<Seq<PythonFunction>> unapply(ChainedPythonFunctions var0) {
      return ChainedPythonFunctions$.MODULE$.unapply(var0);
   }

   public static ChainedPythonFunctions apply(Seq<PythonFunction> var0) {
      return ChainedPythonFunctions$.MODULE$.apply(var0);
   }

   public static <A> Function1<Seq<PythonFunction>, A> andThen(Function1<ChainedPythonFunctions, A> var0) {
      return ChainedPythonFunctions$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, ChainedPythonFunctions> compose(Function1<A, Seq<PythonFunction>> var0) {
      return ChainedPythonFunctions$.MODULE$.compose(var0);
   }

   public Seq<PythonFunction> funcs() {
      return this.funcs;
   }

   public ChainedPythonFunctions copy(Seq<PythonFunction> funcs) {
      return new ChainedPythonFunctions(funcs);
   }

   public Seq<PythonFunction> copy$default$1() {
      return this.funcs();
   }

   public String productPrefix() {
      return "ChainedPythonFunctions";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.funcs();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ChainedPythonFunctions;
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
            if (x$1 instanceof ChainedPythonFunctions) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     ChainedPythonFunctions var4 = (ChainedPythonFunctions)x$1;
                     Seq var10000 = this.funcs();
                     Seq var5 = var4.funcs();
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

   public ChainedPythonFunctions(Seq<PythonFunction> funcs) {
      this.funcs = funcs;
      Product$class.$init$(this);
   }
}
