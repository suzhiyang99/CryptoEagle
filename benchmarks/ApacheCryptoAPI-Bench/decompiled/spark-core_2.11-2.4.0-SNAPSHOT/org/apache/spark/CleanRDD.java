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
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a\u0001B\u0001\u0003\t&\u0011\u0001b\u00117fC:\u0014F\t\u0012\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sO\u000e\u00011#\u0002\u0001\u000b!Q9\u0002CA\u0006\u000f\u001b\u0005a!\"A\u0007\u0002\u000bM\u001c\u0017\r\\1\n\u0005=a!AB!osJ+g\r\u0005\u0002\u0012%5\t!!\u0003\u0002\u0014\u0005\tY1\t\\3b]V\u0004H+Y:l!\tYQ#\u0003\u0002\u0017\u0019\t9\u0001K]8ek\u000e$\bCA\u0006\u0019\u0013\tIBB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001c\u0001\tU\r\u0011\"\u0001\u001d\u0003\u0015\u0011H\rZ%e+\u0005i\u0002CA\u0006\u001f\u0013\tyBBA\u0002J]RD\u0001\"\t\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0007e\u0012$\u0017\n\u001a\u0011\t\u000b\r\u0002A\u0011\u0001\u0013\u0002\rqJg.\u001b;?)\t)c\u0005\u0005\u0002\u0012\u0001!)1D\ta\u0001;!9\u0001\u0006AA\u0001\n\u0003I\u0013\u0001B2paf$\"!\n\u0016\t\u000fm9\u0003\u0013!a\u0001;!9A\u0006AI\u0001\n\u0003i\u0013AD2paf$C-\u001a4bk2$H%M\u000b\u0002])\u0012QdL\u0016\u0002aA\u0011\u0011GN\u0007\u0002e)\u00111\u0007N\u0001\nk:\u001c\u0007.Z2lK\u0012T!!\u000e\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u00028e\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000fe\u0002\u0011\u0011!C!u\u0005i\u0001O]8ek\u000e$\bK]3gSb,\u0012a\u000f\t\u0003y\u0005k\u0011!\u0010\u0006\u0003}}\nA\u0001\\1oO*\t\u0001)\u0001\u0003kCZ\f\u0017B\u0001\">\u0005\u0019\u0019FO]5oO\"9A\tAA\u0001\n\u0003a\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bb\u0002$\u0001\u0003\u0003%\taR\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tA5\n\u0005\u0002\f\u0013&\u0011!\n\u0004\u0002\u0004\u0003:L\bb\u0002'F\u0003\u0003\u0005\r!H\u0001\u0004q\u0012\n\u0004b\u0002(\u0001\u0003\u0003%\teT\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0001\u000bE\u0002R)\"k\u0011A\u0015\u0006\u0003'2\t!bY8mY\u0016\u001cG/[8o\u0013\t)&K\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d9\u0006!!A\u0005\u0002a\u000b\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u00033r\u0003\"a\u0003.\n\u0005mc!a\u0002\"p_2,\u0017M\u001c\u0005\b\u0019Z\u000b\t\u00111\u0001I\u0011\u001dq\u0006!!A\u0005B}\u000b\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002;!9\u0011\rAA\u0001\n\u0003\u0012\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003mBq\u0001\u001a\u0001\u0002\u0002\u0013\u0005S-\u0001\u0004fcV\fGn\u001d\u000b\u00033\u001aDq\u0001T2\u0002\u0002\u0003\u0007\u0001jB\u0004i\u0005\u0005\u0005\t\u0012B5\u0002\u0011\rcW-\u00198S\t\u0012\u0003\"!\u00056\u0007\u000f\u0005\u0011\u0011\u0011!E\u0005WN\u0019!\u000e\\\f\u0011\t5\u0004X$J\u0007\u0002]*\u0011q\u000eD\u0001\beVtG/[7f\u0013\t\thNA\tBEN$(/Y2u\rVt7\r^5p]FBQa\t6\u0005\u0002M$\u0012!\u001b\u0005\bC*\f\t\u0011\"\u0012c\u0011\u001d1(.!A\u0005\u0002^\fQ!\u00199qYf$\"!\n=\t\u000bm)\b\u0019A\u000f\t\u000fiT\u0017\u0011!CAw\u00069QO\\1qa2LHC\u0001?\u0000!\rYQ0H\u0005\u0003}2\u0011aa\u00149uS>t\u0007\u0002CA\u0001s\u0006\u0005\t\u0019A\u0013\u0002\u0007a$\u0003\u0007C\u0005\u0002\u0006)\f\t\u0011\"\u0003\u0002\b\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tI\u0001E\u0002=\u0003\u0017I1!!\u0004>\u0005\u0019y%M[3di\u0002"
)
public class CleanRDD implements CleanupTask, Product, Serializable {
   private final int rddId;

   public static Option<Object> unapply(CleanRDD var0) {
      return CleanRDD$.MODULE$.unapply(var0);
   }

   public static CleanRDD apply(int var0) {
      return CleanRDD$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<CleanRDD, A> var0) {
      return CleanRDD$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, CleanRDD> compose(Function1<A, Object> var0) {
      return CleanRDD$.MODULE$.compose(var0);
   }

   public int rddId() {
      return this.rddId;
   }

   public CleanRDD copy(int rddId) {
      return new CleanRDD(rddId);
   }

   public int copy$default$1() {
      return this.rddId();
   }

   public String productPrefix() {
      return "CleanRDD";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.rddId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof CleanRDD;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.rddId());
      return Statics.finalizeHash(var1, 1);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof CleanRDD) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               CleanRDD var4 = (CleanRDD)x$1;
               if (this.rddId() == var4.rddId() && var4.canEqual(this)) {
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

   public CleanRDD(int rddId) {
      this.rddId = rddId;
      Product$class.$init$(this);
   }
}
