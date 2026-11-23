package org.apache.spark;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001da!B\u0001\u0003\u0001\nA!!F*qCJ\\Wk]3s\u0003B\u0004X\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sON!\u0001!C\u0007\u0014!\tQ1\"D\u0001\u0003\u0013\ta!A\u0001\bTa\u0006\u00148.\u0012=dKB$\u0018n\u001c8\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\u000fA\u0013x\u000eZ;diB\u0011a\u0002F\u0005\u0003+=\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001b\u0006\u0001\u0003\u0016\u0004%\t!G\u0001\tKbLGoQ8eK\u000e\u0001Q#\u0001\u000e\u0011\u00059Y\u0012B\u0001\u000f\u0010\u0005\rIe\u000e\u001e\u0005\t=\u0001\u0011\t\u0012)A\u00055\u0005IQ\r_5u\u0007>$W\r\t\u0005\u0006A\u0001!\t!I\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\t\u001a\u0003C\u0001\u0006\u0001\u0011\u00159r\u00041\u0001\u001b\u0011\u001d)\u0003!!A\u0005\u0002\u0019\nAaY8qsR\u0011!e\n\u0005\b/\u0011\u0002\n\u00111\u0001\u001b\u0011\u001dI\u0003!%A\u0005\u0002)\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u0001,U\tQBfK\u0001.!\tq3'D\u00010\u0015\t\u0001\u0014'A\u0005v]\u000eDWmY6fI*\u0011!gD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001\u001b0\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bm\u0001\t\t\u0011\"\u00118\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t\u0001\b\u0005\u0002:}5\t!H\u0003\u0002<y\u0005!A.\u00198h\u0015\u0005i\u0014\u0001\u00026bm\u0006L!a\u0010\u001e\u0003\rM#(/\u001b8h\u0011\u001d\t\u0005!!A\u0005\u0002e\tA\u0002\u001d:pIV\u001cG/\u0011:jifDqa\u0011\u0001\u0002\u0002\u0013\u0005A)\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005\u0015C\u0005C\u0001\bG\u0013\t9uBA\u0002B]fDq!\u0013\"\u0002\u0002\u0003\u0007!$A\u0002yIEBqa\u0013\u0001\u0002\u0002\u0013\u0005C*A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005i\u0005c\u0001(R\u000b6\tqJ\u0003\u0002Q\u001f\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005I{%\u0001C%uKJ\fGo\u001c:\t\u000fQ\u0003\u0011\u0011!C\u0001+\u0006A1-\u00198FcV\fG\u000e\u0006\u0002W3B\u0011abV\u0005\u00031>\u0011qAQ8pY\u0016\fg\u000eC\u0004J'\u0006\u0005\t\u0019A#\t\u000fm\u0003\u0011\u0011!C!9\u0006A\u0001.Y:i\u0007>$W\rF\u0001\u001b\u0011\u001dq\u0006!!A\u0005B}\u000ba!Z9vC2\u001cHC\u0001,a\u0011\u001dIU,!AA\u0002\u0015;\u0001B\u0019\u0002\u0002\u0002#\u0005!aY\u0001\u0016'B\f'o[+tKJ\f\u0005\u000f]#yG\u0016\u0004H/[8o!\tQAM\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002f'\r!gm\u0005\t\u0005O*T\"%D\u0001i\u0015\tIw\"A\u0004sk:$\u0018.\\3\n\u0005-D'!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!)\u0001\u0005\u001aC\u0001[R\t1\rC\u0004pI\u0006\u0005IQ\t9\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u000f\u0005\be\u0012\f\t\u0011\"!t\u0003\u0015\t\u0007\u000f\u001d7z)\t\u0011C\u000fC\u0003\u0018c\u0002\u0007!\u0004C\u0004wI\u0006\u0005I\u0011Q<\u0002\u000fUt\u0017\r\u001d9msR\u0011\u0001p\u001f\t\u0004\u001deT\u0012B\u0001>\u0010\u0005\u0019y\u0005\u000f^5p]\"9A0^A\u0001\u0002\u0004\u0011\u0013a\u0001=%a!9a\u0010ZA\u0001\n\u0013y\u0018a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0001\u0011\u0007e\n\u0019!C\u0002\u0002\u0006i\u0012aa\u00142kK\u000e$\b"
)
public class SparkUserAppException extends SparkException implements Product, Serializable {
   private final int exitCode;

   public static Option<Object> unapply(SparkUserAppException var0) {
      return SparkUserAppException$.MODULE$.unapply(var0);
   }

   public static SparkUserAppException apply(int var0) {
      return SparkUserAppException$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<SparkUserAppException, A> var0) {
      return SparkUserAppException$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkUserAppException> compose(Function1<A, Object> var0) {
      return SparkUserAppException$.MODULE$.compose(var0);
   }

   public int exitCode() {
      return this.exitCode;
   }

   public SparkUserAppException copy(int exitCode) {
      return new SparkUserAppException(exitCode);
   }

   public int copy$default$1() {
      return this.exitCode();
   }

   public String productPrefix() {
      return "SparkUserAppException";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.exitCode());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkUserAppException;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.exitCode());
      return Statics.finalizeHash(var1, 1);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof SparkUserAppException) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               SparkUserAppException var4 = (SparkUserAppException)x$1;
               if (this.exitCode() == var4.exitCode() && var4.canEqual(this)) {
                  break label48;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public SparkUserAppException(int exitCode) {
      super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"User application exited with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(exitCode)})));
      this.exitCode = exitCode;
      Product$class.$init$(this);
   }
}
