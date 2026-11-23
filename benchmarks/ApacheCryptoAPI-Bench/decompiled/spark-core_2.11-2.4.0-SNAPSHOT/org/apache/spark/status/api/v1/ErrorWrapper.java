package org.apache.spark.status.api.v1;

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
   bytes = "\u0006\u0001\u0005\rb!B\u0001\u0003\u0001\nq!\u0001D#se>\u0014xK]1qa\u0016\u0014(BA\u0002\u0005\u0003\t1\u0018G\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011AB:uCR,8O\u0003\u0002\n\u0015\u0005)1\u000f]1sW*\u00111\u0002D\u0001\u0007CB\f7\r[3\u000b\u00035\t1a\u001c:h'\u0011\u0001q\"\u0006\r\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\t\u0001b#\u0003\u0002\u0018#\t9\u0001K]8ek\u000e$\bC\u0001\t\u001a\u0013\tQ\u0012C\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001d\u0001\tU\r\u0011\"\u0001\u001f\u0003\u0005\u00198\u0001A\u000b\u0002?A\u0011\u0001e\t\b\u0003!\u0005J!AI\t\u0002\rA\u0013X\rZ3g\u0013\t!SE\u0001\u0004TiJLgn\u001a\u0006\u0003EEA\u0001b\n\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0003g\u0002BQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtDCA\u0016.!\ta\u0003!D\u0001\u0003\u0011\u0015a\u0002\u00061\u0001 \u0011\u001dy\u0003!!A\u0005\u0002A\nAaY8qsR\u00111&\r\u0005\b99\u0002\n\u00111\u0001 \u0011\u001d\u0019\u0004!%A\u0005\u0002Q\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00016U\tybgK\u00018!\tAT(D\u0001:\u0015\tQ4(A\u0005v]\u000eDWmY6fI*\u0011A(E\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001 :\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b\u0001\u0002\t\t\u0011\"\u0011B\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t!\t\u0005\u0002D\u00116\tAI\u0003\u0002F\r\u0006!A.\u00198h\u0015\u00059\u0015\u0001\u00026bm\u0006L!\u0001\n#\t\u000f)\u0003\u0011\u0011!C\u0001\u0017\u0006a\u0001O]8ek\u000e$\u0018I]5usV\tA\n\u0005\u0002\u0011\u001b&\u0011a*\u0005\u0002\u0004\u0013:$\bb\u0002)\u0001\u0003\u0003%\t!U\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\u0011V\u000b\u0005\u0002\u0011'&\u0011A+\u0005\u0002\u0004\u0003:L\bb\u0002,P\u0003\u0003\u0005\r\u0001T\u0001\u0004q\u0012\n\u0004b\u0002-\u0001\u0003\u0003%\t%W\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t!\fE\u0002\\=Jk\u0011\u0001\u0018\u0006\u0003;F\t!bY8mY\u0016\u001cG/[8o\u0013\tyFL\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d\t\u0007!!A\u0005\u0002\t\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003G\u001a\u0004\"\u0001\u00053\n\u0005\u0015\f\"a\u0002\"p_2,\u0017M\u001c\u0005\b-\u0002\f\t\u00111\u0001S\u0011\u001dA\u0007!!A\u0005B%\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002\u0019\"91\u000eAA\u0001\n\u0003b\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\tCqA\u001c\u0001\u0002\u0002\u0013\u0005s.\u0001\u0004fcV\fGn\u001d\u000b\u0003GBDqAV7\u0002\u0002\u0003\u0007!k\u0002\u0005s\u0005\u0005\u0005\t\u0012\u0001\u0002t\u00031)%O]8s/J\f\u0007\u000f]3s!\taCO\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002v'\r!h\u000f\u0007\t\u0005oj|2&D\u0001y\u0015\tI\u0018#A\u0004sk:$\u0018.\\3\n\u0005mD(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!)\u0011\u0006\u001eC\u0001{R\t1\u000fC\u0004li\u0006\u0005IQ\t7\t\u0013\u0005\u0005A/!A\u0005\u0002\u0006\r\u0011!B1qa2LHcA\u0016\u0002\u0006!)Ad a\u0001?!I\u0011\u0011\u0002;\u0002\u0002\u0013\u0005\u00151B\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\ti!a\u0005\u0011\tA\tyaH\u0005\u0004\u0003#\t\"AB(qi&|g\u000eC\u0005\u0002\u0016\u0005\u001d\u0011\u0011!a\u0001W\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005eA/!A\u0005\n\u0005m\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\b\u0011\u0007\r\u000by\"C\u0002\u0002\"\u0011\u0013aa\u00142kK\u000e$\b"
)
public class ErrorWrapper implements Product, Serializable {
   private final String s;

   public static Option<String> unapply(ErrorWrapper var0) {
      return ErrorWrapper$.MODULE$.unapply(var0);
   }

   public static ErrorWrapper apply(String var0) {
      return ErrorWrapper$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<ErrorWrapper, A> var0) {
      return ErrorWrapper$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, ErrorWrapper> compose(Function1<A, String> var0) {
      return ErrorWrapper$.MODULE$.compose(var0);
   }

   public String s() {
      return this.s;
   }

   public ErrorWrapper copy(String s) {
      return new ErrorWrapper(s);
   }

   public String copy$default$1() {
      return this.s();
   }

   public String productPrefix() {
      return "ErrorWrapper";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.s();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ErrorWrapper;
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
            if (x$1 instanceof ErrorWrapper) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     ErrorWrapper var4 = (ErrorWrapper)x$1;
                     String var10000 = this.s();
                     String var5 = var4.s();
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

   public ErrorWrapper(String s) {
      this.s = s;
      Product$class.$init$(this);
   }
}
