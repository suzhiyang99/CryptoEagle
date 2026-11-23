package org.apache.spark.storage;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0001\u0012Q!a\u0003+fgR\u0014En\\2l\u0013\u0012T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017=)\u0002C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005\u001d\u0011En\\2l\u0013\u0012\u0004\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0011-%\u0011q#\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00017\u0005\u0011\u0011\u000eZ\u0002\u0001+\u0005a\u0002CA\u000f!\u001d\t\u0001b$\u0003\u0002 #\u00051\u0001K]3eK\u001aL!!\t\u0012\u0003\rM#(/\u001b8h\u0015\ty\u0012\u0003\u0003\u0005%\u0001\tE\t\u0015!\u0003\u001d\u0003\rIG\r\t\u0005\u0006M\u0001!\taJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005!J\u0003C\u0001\u0007\u0001\u0011\u0015IR\u00051\u0001\u001d\u0011\u0015Y\u0003\u0001\"\u0011\u001c\u0003\u0011q\u0017-\\3\t\u000f5\u0002\u0011\u0011!C\u0001]\u0005!1m\u001c9z)\tAs\u0006C\u0004\u001aYA\u0005\t\u0019\u0001\u000f\t\u000fE\u0002\u0011\u0013!C\u0001e\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\u001a+\u0005q!4&A\u001b\u0011\u0005YZT\"A\u001c\u000b\u0005aJ\u0014!C;oG\",7m[3e\u0015\tQ\u0014#\u0001\u0006b]:|G/\u0019;j_:L!\u0001P\u001c\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004?\u0001\u0005\u0005I\u0011I \u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005\u0001\u0005CA!G\u001b\u0005\u0011%BA\"E\u0003\u0011a\u0017M\\4\u000b\u0003\u0015\u000bAA[1wC&\u0011\u0011E\u0011\u0005\b\u0011\u0002\t\t\u0011\"\u0001J\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005Q\u0005C\u0001\tL\u0013\ta\u0015CA\u0002J]RDqA\u0014\u0001\u0002\u0002\u0013\u0005q*\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005A\u001b\u0006C\u0001\tR\u0013\t\u0011\u0016CA\u0002B]fDq\u0001V'\u0002\u0002\u0003\u0007!*A\u0002yIEBqA\u0016\u0001\u0002\u0002\u0013\u0005s+A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005A\u0006cA-]!6\t!L\u0003\u0002\\#\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005uS&\u0001C%uKJ\fGo\u001c:\t\u000f}\u0003\u0011\u0011!C\u0001A\u0006A1-\u00198FcV\fG\u000e\u0006\u0002bIB\u0011\u0001CY\u0005\u0003GF\u0011qAQ8pY\u0016\fg\u000eC\u0004U=\u0006\u0005\t\u0019\u0001)\t\u000f\u0019\u0004\u0011\u0011!C!O\u0006A\u0001.Y:i\u0007>$W\rF\u0001K\u0011\u001dI\u0007!!A\u0005B)\fa!Z9vC2\u001cHCA1l\u0011\u001d!\u0006.!AA\u0002A;\u0001\"\u001c\u0002\u0002\u0002#\u0005AA\\\u0001\f)\u0016\u001cHO\u00117pG.LE\r\u0005\u0002\r_\u001aA\u0011AAA\u0001\u0012\u0003!\u0001oE\u0002pcV\u0001BA];\u001dQ5\t1O\u0003\u0002u#\u00059!/\u001e8uS6,\u0017B\u0001<t\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\u0006M=$\t\u0001\u001f\u000b\u0002]\"9!p\\A\u0001\n\u000bZ\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0001Cq!`8\u0002\u0002\u0013\u0005e0A\u0003baBd\u0017\u0010\u0006\u0002)\u007f\")\u0011\u0004 a\u00019!I\u00111A8\u0002\u0002\u0013\u0005\u0015QA\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t9!!\u0004\u0011\tA\tI\u0001H\u0005\u0004\u0003\u0017\t\"AB(qi&|g\u000eC\u0005\u0002\u0010\u0005\u0005\u0011\u0011!a\u0001Q\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005Mq.!A\u0005\n\u0005U\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0006\u0011\u0007\u0005\u000bI\"C\u0002\u0002\u001c\t\u0013aa\u00142kK\u000e$\b"
)
public class TestBlockId extends BlockId implements Product, Serializable {
   private final String id;

   public static Option<String> unapply(TestBlockId var0) {
      return TestBlockId$.MODULE$.unapply(var0);
   }

   public static TestBlockId apply(String var0) {
      return TestBlockId$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<TestBlockId, A> var0) {
      return TestBlockId$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, TestBlockId> compose(Function1<A, String> var0) {
      return TestBlockId$.MODULE$.compose(var0);
   }

   public String id() {
      return this.id;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"test_").append((Object)this.id()).toString();
   }

   public TestBlockId copy(String id) {
      return new TestBlockId(id);
   }

   public String copy$default$1() {
      return this.id();
   }

   public String productPrefix() {
      return "TestBlockId";
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
      return x$1 instanceof TestBlockId;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof TestBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     TestBlockId var4 = (TestBlockId)x$1;
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

   public TestBlockId(String id) {
      this.id = id;
      Product$class.$init$(this);
   }
}
