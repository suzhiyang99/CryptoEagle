package org.apache.spark.rpc.netty;

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
   bytes = "\u0006\u0001\u0005-b!B\u0001\u0003\u0001\na!A\u0003*qG\u001a\u000b\u0017\u000e\\;sK*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0003\u0002\u0001\u000e'Y\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\b\u0015\u0013\t)rBA\u0004Qe>$Wo\u0019;\u0011\u000599\u0012B\u0001\r\u0010\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!Q\u0002A!f\u0001\n\u0003a\u0012!A3\u0004\u0001U\tQ\u0004\u0005\u0002\u001fM9\u0011q\u0004\n\b\u0003A\rj\u0011!\t\u0006\u0003Em\ta\u0001\u0010:p_Rt\u0014\"\u0001\t\n\u0005\u0015z\u0011a\u00029bG.\fw-Z\u0005\u0003O!\u0012\u0011\u0002\u00165s_^\f'\r\\3\u000b\u0005\u0015z\u0001\u0002\u0003\u0016\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0005\u0015\u0004\u0003\"\u0002\u0017\u0001\t\u0003i\u0013A\u0002\u001fj]&$h\b\u0006\u0002/aA\u0011q\u0006A\u0007\u0002\u0005!)!d\u000ba\u0001;!9!\u0007AA\u0001\n\u0003\u0019\u0014\u0001B2paf$\"A\f\u001b\t\u000fi\t\u0004\u0013!a\u0001;!9a\u0007AI\u0001\n\u00039\u0014AD2paf$C-\u001a4bk2$H%M\u000b\u0002q)\u0012Q$O\u0016\u0002uA\u00111\bQ\u0007\u0002y)\u0011QHP\u0001\nk:\u001c\u0007.Z2lK\u0012T!aP\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002By\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f\r\u0003\u0011\u0011!C!\t\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u0012\t\u0003\r.k\u0011a\u0012\u0006\u0003\u0011&\u000bA\u0001\\1oO*\t!*\u0001\u0003kCZ\f\u0017B\u0001'H\u0005\u0019\u0019FO]5oO\"9a\nAA\u0001\n\u0003y\u0015\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001)\u0011\u00059\t\u0016B\u0001*\u0010\u0005\rIe\u000e\u001e\u0005\b)\u0002\t\t\u0011\"\u0001V\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"AV-\u0011\u000599\u0016B\u0001-\u0010\u0005\r\te.\u001f\u0005\b5N\u000b\t\u00111\u0001Q\u0003\rAH%\r\u0005\b9\u0002\t\t\u0011\"\u0011^\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00010\u0011\u0007}\u0013g+D\u0001a\u0015\t\tw\"\u0001\u0006d_2dWm\u0019;j_:L!a\u00191\u0003\u0011%#XM]1u_JDq!\u001a\u0001\u0002\u0002\u0013\u0005a-\u0001\u0005dC:,\u0015/^1m)\t9'\u000e\u0005\u0002\u000fQ&\u0011\u0011n\u0004\u0002\b\u0005>|G.Z1o\u0011\u001dQF-!AA\u0002YCq\u0001\u001c\u0001\u0002\u0002\u0013\u0005S.\u0001\u0005iCND7i\u001c3f)\u0005\u0001\u0006bB8\u0001\u0003\u0003%\t\u0005]\u0001\ti>\u001cFO]5oOR\tQ\tC\u0004s\u0001\u0005\u0005I\u0011I:\u0002\r\u0015\fX/\u00197t)\t9G\u000fC\u0004[c\u0006\u0005\t\u0019\u0001,\b\u0011Y\u0014\u0011\u0011!E\u0001\u0005]\f!B\u00159d\r\u0006LG.\u001e:f!\ty\u0003P\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002z'\rA(P\u0006\t\u0005wzlb&D\u0001}\u0015\tix\"A\u0004sk:$\u0018.\\3\n\u0005}d(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!1A\u0006\u001fC\u0001\u0003\u0007!\u0012a\u001e\u0005\b_b\f\t\u0011\"\u0012q\u0011%\tI\u0001_A\u0001\n\u0003\u000bY!A\u0003baBd\u0017\u0010F\u0002/\u0003\u001bAaAGA\u0004\u0001\u0004i\u0002\"CA\tq\u0006\u0005I\u0011QA\n\u0003\u001d)h.\u00199qYf$B!!\u0006\u0002\u001cA!a\"a\u0006\u001e\u0013\r\tIb\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0013\u0005u\u0011qBA\u0001\u0002\u0004q\u0013a\u0001=%a!I\u0011\u0011\u0005=\u0002\u0002\u0013%\u00111E\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002&A\u0019a)a\n\n\u0007\u0005%rI\u0001\u0004PE*,7\r\u001e"
)
public class RpcFailure implements Product, Serializable {
   private final Throwable e;

   public static Option<Throwable> unapply(RpcFailure var0) {
      return RpcFailure$.MODULE$.unapply(var0);
   }

   public static RpcFailure apply(Throwable var0) {
      return RpcFailure$.MODULE$.apply(var0);
   }

   public static <A> Function1<Throwable, A> andThen(Function1<RpcFailure, A> var0) {
      return RpcFailure$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, RpcFailure> compose(Function1<A, Throwable> var0) {
      return RpcFailure$.MODULE$.compose(var0);
   }

   public Throwable e() {
      return this.e;
   }

   public RpcFailure copy(Throwable e) {
      return new RpcFailure(e);
   }

   public Throwable copy$default$1() {
      return this.e();
   }

   public String productPrefix() {
      return "RpcFailure";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.e();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof RpcFailure;
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
            if (x$1 instanceof RpcFailure) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     RpcFailure var4 = (RpcFailure)x$1;
                     Throwable var10000 = this.e();
                     Throwable var5 = var4.e();
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

   public RpcFailure(Throwable e) {
      this.e = e;
      Product$class.$init$(this);
   }
}
