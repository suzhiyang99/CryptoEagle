package org.apache.spark.api.r;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a!B\u0001\u0003\u0001\na!a\u0003&W\u001b>\u0013'.Z2u\u0013\u0012T!a\u0001\u0003\u0002\u0003IT!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\u0011\u0001Qb\u0005\f\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\tqA#\u0003\u0002\u0016\u001f\t9\u0001K]8ek\u000e$\bC\u0001\b\u0018\u0013\tArB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001b\u0001\tU\r\u0011\"\u0001\u001d\u0003\tIGm\u0001\u0001\u0016\u0003u\u0001\"AH\u0011\u000f\u00059y\u0012B\u0001\u0011\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011!e\t\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0001z\u0001\u0002C\u0013\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0007%$\u0007\u0005C\u0003(\u0001\u0011\u0005\u0001&\u0001\u0004=S:LGO\u0010\u000b\u0003S-\u0002\"A\u000b\u0001\u000e\u0003\tAQA\u0007\u0014A\u0002uAq!\f\u0001\u0002\u0002\u0013\u0005a&\u0001\u0003d_BLHCA\u00150\u0011\u001dQB\u0006%AA\u0002uAq!\r\u0001\u0012\u0002\u0013\u0005!'\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003MR#!\b\u001b,\u0003U\u0002\"AN\u001e\u000e\u0003]R!\u0001O\u001d\u0002\u0013Ut7\r[3dW\u0016$'B\u0001\u001e\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0003y]\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001dq\u0004!!A\u0005B}\nQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001!\u0011\u0005\u00053U\"\u0001\"\u000b\u0005\r#\u0015\u0001\u00027b]\u001eT\u0011!R\u0001\u0005U\u00064\u0018-\u0003\u0002#\u0005\"9\u0001\nAA\u0001\n\u0003I\u0015\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001&\u0011\u00059Y\u0015B\u0001'\u0010\u0005\rIe\u000e\u001e\u0005\b\u001d\u0002\t\t\u0011\"\u0001P\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001U*\u0011\u00059\t\u0016B\u0001*\u0010\u0005\r\te.\u001f\u0005\b)6\u000b\t\u00111\u0001K\u0003\rAH%\r\u0005\b-\u0002\t\t\u0011\"\u0011X\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u0001-\u0011\u0007ec\u0006+D\u0001[\u0015\tYv\"\u0001\u0006d_2dWm\u0019;j_:L!!\u0018.\u0003\u0011%#XM]1u_JDqa\u0018\u0001\u0002\u0002\u0013\u0005\u0001-\u0001\u0005dC:,\u0015/^1m)\t\tG\r\u0005\u0002\u000fE&\u00111m\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d!f,!AA\u0002ACqA\u001a\u0001\u0002\u0002\u0013\u0005s-\u0001\u0005iCND7i\u001c3f)\u0005Q\u0005bB5\u0001\u0003\u0003%\tE[\u0001\ti>\u001cFO]5oOR\t\u0001\tC\u0004m\u0001\u0005\u0005I\u0011I7\u0002\r\u0015\fX/\u00197t)\t\tg\u000eC\u0004UW\u0006\u0005\t\u0019\u0001)\b\u0011A\u0014\u0011\u0011!E\u0001\u0005E\f1B\u0013,N\u001f\nTWm\u0019;JIB\u0011!F\u001d\u0004\t\u0003\t\t\t\u0011#\u0001\u0003gN\u0019!\u000f\u001e\f\u0011\tUDX$K\u0007\u0002m*\u0011qoD\u0001\beVtG/[7f\u0013\tIhOA\tBEN$(/Y2u\rVt7\r^5p]FBQa\n:\u0005\u0002m$\u0012!\u001d\u0005\bSJ\f\t\u0011\"\u0012k\u0011\u001dq(/!A\u0005\u0002~\fQ!\u00199qYf$2!KA\u0001\u0011\u0015QR\u00101\u0001\u001e\u0011%\t)A]A\u0001\n\u0003\u000b9!A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005%\u0011q\u0002\t\u0005\u001d\u0005-Q$C\u0002\u0002\u000e=\u0011aa\u00149uS>t\u0007\"CA\t\u0003\u0007\t\t\u00111\u0001*\u0003\rAH\u0005\r\u0005\n\u0003+\u0011\u0018\u0011!C\u0005\u0003/\t1B]3bIJ+7o\u001c7wKR\u0011\u0011\u0011\u0004\t\u0004\u0003\u0006m\u0011bAA\u000f\u0005\n1qJ\u00196fGR\u0004"
)
public class JVMObjectId implements Product, Serializable {
   private final String id;

   public static Option<String> unapply(JVMObjectId var0) {
      return JVMObjectId$.MODULE$.unapply(var0);
   }

   public static JVMObjectId apply(String var0) {
      return JVMObjectId$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<JVMObjectId, A> var0) {
      return JVMObjectId$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, JVMObjectId> compose(Function1<A, String> var0) {
      return JVMObjectId$.MODULE$.compose(var0);
   }

   public String id() {
      return this.id;
   }

   public JVMObjectId copy(String id) {
      return new JVMObjectId(id);
   }

   public String copy$default$1() {
      return this.id();
   }

   public String productPrefix() {
      return "JVMObjectId";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.id();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof JVMObjectId;
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
            if (x$1 instanceof JVMObjectId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     JVMObjectId var4 = (JVMObjectId)x$1;
                     String var10000 = this.id();
                     String var5 = var4.id();
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

   public JVMObjectId(String id) {
      this.id = id;
      Product$class.$init$(this);
      Predef$.MODULE$.require(id != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Object ID cannot be null.";
         }
      });
   }
}
