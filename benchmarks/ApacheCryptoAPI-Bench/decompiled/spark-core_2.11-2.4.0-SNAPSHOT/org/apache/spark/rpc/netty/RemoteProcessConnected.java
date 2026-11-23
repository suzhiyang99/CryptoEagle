package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
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
   bytes = "\u0006\u0001\u0005\u0005b!B\u0001\u0003\u0001\na!A\u0006*f[>$X\r\u0015:pG\u0016\u001c8oQ8o]\u0016\u001cG/\u001a3\u000b\u0005\r!\u0011!\u00028fiRL(BA\u0003\u0007\u0003\r\u0011\bo\u0019\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON)\u0001!D\n\u00185A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0003\u0019%s'm\u001c=NKN\u001c\u0018mZ3\u0011\u00059A\u0012BA\r\u0010\u0005\u001d\u0001&o\u001c3vGR\u0004\"AD\u000e\n\u0005qy!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u0010\u0001\u0005+\u0007I\u0011\u0001\u0011\u0002\u001bI,Wn\u001c;f\u0003\u0012$'/Z:t\u0007\u0001)\u0012!\t\t\u0003E\rj\u0011\u0001B\u0005\u0003I\u0011\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011!1\u0003A!E!\u0002\u0013\t\u0013A\u0004:f[>$X-\u00113ee\u0016\u001c8\u000f\t\u0005\u0006Q\u0001!\t!K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005)Z\u0003C\u0001\u000b\u0001\u0011\u0015qr\u00051\u0001\"\u0011\u001di\u0003!!A\u0005\u00029\nAaY8qsR\u0011!f\f\u0005\b=1\u0002\n\u00111\u0001\"\u0011\u001d\t\u0004!%A\u0005\u0002I\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00014U\t\tCgK\u00016!\t14(D\u00018\u0015\tA\u0014(A\u0005v]\u000eDWmY6fI*\u0011!hD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001\u001f8\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b}\u0001\t\t\u0011\"\u0011@\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t\u0001\t\u0005\u0002B\r6\t!I\u0003\u0002D\t\u0006!A.\u00198h\u0015\u0005)\u0015\u0001\u00026bm\u0006L!a\u0012\"\u0003\rM#(/\u001b8h\u0011\u001dI\u0005!!A\u0005\u0002)\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012a\u0013\t\u0003\u001d1K!!T\b\u0003\u0007%sG\u000fC\u0004P\u0001\u0005\u0005I\u0011\u0001)\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011\u0011\u000b\u0016\t\u0003\u001dIK!aU\b\u0003\u0007\u0005s\u0017\u0010C\u0004V\u001d\u0006\u0005\t\u0019A&\u0002\u0007a$\u0013\u0007C\u0004X\u0001\u0005\u0005I\u0011\t-\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012!\u0017\t\u00045v\u000bV\"A.\u000b\u0005q{\u0011AC2pY2,7\r^5p]&\u0011al\u0017\u0002\t\u0013R,'/\u0019;pe\"9\u0001\rAA\u0001\n\u0003\t\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005\t,\u0007C\u0001\bd\u0013\t!wBA\u0004C_>dW-\u00198\t\u000fU{\u0016\u0011!a\u0001#\"9q\rAA\u0001\n\u0003B\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003-CqA\u001b\u0001\u0002\u0002\u0013\u00053.\u0001\u0005u_N#(/\u001b8h)\u0005\u0001\u0005bB7\u0001\u0003\u0003%\tE\\\u0001\u0007KF,\u0018\r\\:\u0015\u0005\t|\u0007bB+m\u0003\u0003\u0005\r!U\u0004\tc\n\t\t\u0011#\u0001\u0003e\u00061\"+Z7pi\u0016\u0004&o\\2fgN\u001cuN\u001c8fGR,G\r\u0005\u0002\u0015g\u001aA\u0011AAA\u0001\u0012\u0003\u0011AoE\u0002tkj\u0001BA^=\"U5\tqO\u0003\u0002y\u001f\u00059!/\u001e8uS6,\u0017B\u0001>x\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\u0006QM$\t\u0001 \u000b\u0002e\"9!n]A\u0001\n\u000bZ\u0007\u0002C@t\u0003\u0003%\t)!\u0001\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007)\n\u0019\u0001C\u0003\u001f}\u0002\u0007\u0011\u0005C\u0005\u0002\bM\f\t\u0011\"!\u0002\n\u00059QO\\1qa2LH\u0003BA\u0006\u0003#\u0001BADA\u0007C%\u0019\u0011qB\b\u0003\r=\u0003H/[8o\u0011%\t\u0019\"!\u0002\u0002\u0002\u0003\u0007!&A\u0002yIAB\u0011\"a\u0006t\u0003\u0003%I!!\u0007\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u00037\u00012!QA\u000f\u0013\r\tyB\u0011\u0002\u0007\u001f\nTWm\u0019;"
)
public class RemoteProcessConnected implements InboxMessage, Product, Serializable {
   private final RpcAddress remoteAddress;

   public static Option<RpcAddress> unapply(RemoteProcessConnected var0) {
      return RemoteProcessConnected$.MODULE$.unapply(var0);
   }

   public static RemoteProcessConnected apply(RpcAddress var0) {
      return RemoteProcessConnected$.MODULE$.apply(var0);
   }

   public static <A> Function1<RpcAddress, A> andThen(Function1<RemoteProcessConnected, A> var0) {
      return RemoteProcessConnected$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, RemoteProcessConnected> compose(Function1<A, RpcAddress> var0) {
      return RemoteProcessConnected$.MODULE$.compose(var0);
   }

   public RpcAddress remoteAddress() {
      return this.remoteAddress;
   }

   public RemoteProcessConnected copy(RpcAddress remoteAddress) {
      return new RemoteProcessConnected(remoteAddress);
   }

   public RpcAddress copy$default$1() {
      return this.remoteAddress();
   }

   public String productPrefix() {
      return "RemoteProcessConnected";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.remoteAddress();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof RemoteProcessConnected;
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
            if (x$1 instanceof RemoteProcessConnected) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     RemoteProcessConnected var4 = (RemoteProcessConnected)x$1;
                     RpcAddress var10000 = this.remoteAddress();
                     RpcAddress var5 = var4.remoteAddress();
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

   public RemoteProcessConnected(RpcAddress remoteAddress) {
      this.remoteAddress = remoteAddress;
      Product$class.$init$(this);
   }
}
