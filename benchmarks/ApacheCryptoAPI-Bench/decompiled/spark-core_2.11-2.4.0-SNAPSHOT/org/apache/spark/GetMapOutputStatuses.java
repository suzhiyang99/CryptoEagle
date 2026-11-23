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
   bytes = "\u0006\u0001\u0005=a!B\u0001\u0003\u0001\nA!\u0001F$fi6\u000b\u0007oT;uaV$8\u000b^1ukN,7O\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h'\u0015\u0001\u0011bD\n\u0017!\tQQ\"D\u0001\f\u0015\u0005a\u0011!B:dC2\f\u0017B\u0001\b\f\u0005\u0019\te.\u001f*fMB\u0011\u0001#E\u0007\u0002\u0005%\u0011!C\u0001\u0002\u0018\u001b\u0006\u0004x*\u001e;qkR$&/Y2lKJlUm]:bO\u0016\u0004\"A\u0003\u000b\n\u0005UY!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0015]I!\u0001G\u0006\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002q\t\u0011b\u001d5vM\u001adW-\u00133\u0004\u0001U\tQ\u0004\u0005\u0002\u000b=%\u0011qd\u0003\u0002\u0004\u0013:$\b\u0002C\u0011\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0015MDWO\u001a4mK&#\u0007\u0005C\u0003$\u0001\u0011\u0005A%\u0001\u0004=S:LGO\u0010\u000b\u0003K\u0019\u0002\"\u0001\u0005\u0001\t\u000bi\u0011\u0003\u0019A\u000f\t\u000f!\u0002\u0011\u0011!C\u0001S\u0005!1m\u001c9z)\t)#\u0006C\u0004\u001bOA\u0005\t\u0019A\u000f\t\u000f1\u0002\u0011\u0013!C\u0001[\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001\u0018+\u0005uy3&\u0001\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014!C;oG\",7m[3e\u0015\t)4\"\u0001\u0006b]:|G/\u0019;j_:L!a\u000e\u001a\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004:\u0001\u0005\u0005I\u0011\t\u001e\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005Y\u0004C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003\u0011a\u0017M\\4\u000b\u0003\u0001\u000bAA[1wC&\u0011!)\u0010\u0002\u0007'R\u0014\u0018N\\4\t\u000f\u0011\u0003\u0011\u0011!C\u00019\u0005a\u0001O]8ek\u000e$\u0018I]5us\"9a\tAA\u0001\n\u00039\u0015A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003\u0011.\u0003\"AC%\n\u0005)[!aA!os\"9A*RA\u0001\u0002\u0004i\u0012a\u0001=%c!9a\nAA\u0001\n\u0003z\u0015a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003A\u00032!\u0015+I\u001b\u0005\u0011&BA*\f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003+J\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b/\u0002\t\t\u0011\"\u0001Y\u0003!\u0019\u0017M\\#rk\u0006dGCA-]!\tQ!,\u0003\u0002\\\u0017\t9!i\\8mK\u0006t\u0007b\u0002'W\u0003\u0003\u0005\r\u0001\u0013\u0005\b=\u0002\t\t\u0011\"\u0011`\u0003!A\u0017m\u001d5D_\u0012,G#A\u000f\t\u000f\u0005\u0004\u0011\u0011!C!E\u0006AAo\\*ue&tw\rF\u0001<\u0011\u001d!\u0007!!A\u0005B\u0015\fa!Z9vC2\u001cHCA-g\u0011\u001da5-!AA\u0002!;\u0001\u0002\u001b\u0002\u0002\u0002#\u0005!![\u0001\u0015\u000f\u0016$X*\u00199PkR\u0004X\u000f^*uCR,8/Z:\u0011\u0005AQg\u0001C\u0001\u0003\u0003\u0003E\tAA6\u0014\u0007)dg\u0003\u0005\u0003nav)S\"\u00018\u000b\u0005=\\\u0011a\u0002:v]RLW.Z\u0005\u0003c:\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u0015\u0019#\u000e\"\u0001t)\u0005I\u0007bB1k\u0003\u0003%)E\u0019\u0005\bm*\f\t\u0011\"!x\u0003\u0015\t\u0007\u000f\u001d7z)\t)\u0003\u0010C\u0003\u001bk\u0002\u0007Q\u0004C\u0004{U\u0006\u0005I\u0011Q>\u0002\u000fUt\u0017\r\u001d9msR\u0011Ap \t\u0004\u0015ul\u0012B\u0001@\f\u0005\u0019y\u0005\u000f^5p]\"A\u0011\u0011A=\u0002\u0002\u0003\u0007Q%A\u0002yIAB\u0011\"!\u0002k\u0003\u0003%I!a\u0002\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0013\u00012\u0001PA\u0006\u0013\r\ti!\u0010\u0002\u0007\u001f\nTWm\u0019;"
)
public class GetMapOutputStatuses implements MapOutputTrackerMessage, Product, Serializable {
   private final int shuffleId;

   public static Option<Object> unapply(GetMapOutputStatuses var0) {
      return GetMapOutputStatuses$.MODULE$.unapply(var0);
   }

   public static GetMapOutputStatuses apply(int var0) {
      return GetMapOutputStatuses$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<GetMapOutputStatuses, A> var0) {
      return GetMapOutputStatuses$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, GetMapOutputStatuses> compose(Function1<A, Object> var0) {
      return GetMapOutputStatuses$.MODULE$.compose(var0);
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public GetMapOutputStatuses copy(int shuffleId) {
      return new GetMapOutputStatuses(shuffleId);
   }

   public int copy$default$1() {
      return this.shuffleId();
   }

   public String productPrefix() {
      return "GetMapOutputStatuses";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.shuffleId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof GetMapOutputStatuses;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.shuffleId());
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
            if (x$1 instanceof GetMapOutputStatuses) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               GetMapOutputStatuses var4 = (GetMapOutputStatuses)x$1;
               if (this.shuffleId() == var4.shuffleId() && var4.canEqual(this)) {
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

   public GetMapOutputStatuses(int shuffleId) {
      this.shuffleId = shuffleId;
      Product$class.$init$(this);
   }
}
