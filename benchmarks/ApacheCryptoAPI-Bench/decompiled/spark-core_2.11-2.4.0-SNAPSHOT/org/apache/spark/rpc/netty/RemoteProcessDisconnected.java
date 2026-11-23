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
   bytes = "\u0006\u0001\u0005\u0005b!B\u0001\u0003\u0001\na!!\u0007*f[>$X\r\u0015:pG\u0016\u001c8\u000fR5tG>tg.Z2uK\u0012T!a\u0001\u0003\u0002\u000b9,G\u000f^=\u000b\u0005\u00151\u0011a\u0001:qG*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0003\u0001\u001bM9\"\u0004\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011AA\u0005\u0003-\t\u0011A\"\u00138c_blUm]:bO\u0016\u0004\"A\u0004\r\n\u0005ey!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001dmI!\u0001H\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011y\u0001!Q3A\u0005\u0002\u0001\nQB]3n_R,\u0017\t\u001a3sKN\u001c8\u0001A\u000b\u0002CA\u0011!eI\u0007\u0002\t%\u0011A\u0005\u0002\u0002\u000b%B\u001c\u0017\t\u001a3sKN\u001c\b\u0002\u0003\u0014\u0001\u0005#\u0005\u000b\u0011B\u0011\u0002\u001dI,Wn\u001c;f\u0003\u0012$'/Z:tA!)\u0001\u0006\u0001C\u0001S\u00051A(\u001b8jiz\"\"AK\u0016\u0011\u0005Q\u0001\u0001\"\u0002\u0010(\u0001\u0004\t\u0003bB\u0017\u0001\u0003\u0003%\tAL\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002+_!9a\u0004\fI\u0001\u0002\u0004\t\u0003bB\u0019\u0001#\u0003%\tAM\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0019$FA\u00115W\u0005)\u0004C\u0001\u001c<\u001b\u00059$B\u0001\u001d:\u0003%)hn\u00195fG.,GM\u0003\u0002;\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005q:$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9a\bAA\u0001\n\u0003z\u0014!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001A!\t\te)D\u0001C\u0015\t\u0019E)\u0001\u0003mC:<'\"A#\u0002\t)\fg/Y\u0005\u0003\u000f\n\u0013aa\u0015;sS:<\u0007bB%\u0001\u0003\u0003%\tAS\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002\u0017B\u0011a\u0002T\u0005\u0003\u001b>\u00111!\u00138u\u0011\u001dy\u0005!!A\u0005\u0002A\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002R)B\u0011aBU\u0005\u0003'>\u00111!\u00118z\u0011\u001d)f*!AA\u0002-\u000b1\u0001\u001f\u00132\u0011\u001d9\u0006!!A\u0005Ba\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u00023B\u0019!,X)\u000e\u0003mS!\u0001X\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002_7\nA\u0011\n^3sCR|'\u000fC\u0004a\u0001\u0005\u0005I\u0011A1\u0002\u0011\r\fg.R9vC2$\"AY3\u0011\u00059\u0019\u0017B\u00013\u0010\u0005\u001d\u0011un\u001c7fC:Dq!V0\u0002\u0002\u0003\u0007\u0011\u000bC\u0004h\u0001\u0005\u0005I\u0011\t5\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\u0013\u0005\bU\u0002\t\t\u0011\"\u0011l\u0003!!xn\u0015;sS:<G#\u0001!\t\u000f5\u0004\u0011\u0011!C!]\u00061Q-];bYN$\"AY8\t\u000fUc\u0017\u0011!a\u0001#\u001eA\u0011OAA\u0001\u0012\u0003\u0011!/A\rSK6|G/\u001a)s_\u000e,7o\u001d#jg\u000e|gN\\3di\u0016$\u0007C\u0001\u000bt\r!\t!!!A\t\u0002\t!8cA:v5A!a/_\u0011+\u001b\u00059(B\u0001=\u0010\u0003\u001d\u0011XO\u001c;j[\u0016L!A_<\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007C\u0003)g\u0012\u0005A\u0010F\u0001s\u0011\u001dQ7/!A\u0005F-D\u0001b`:\u0002\u0002\u0013\u0005\u0015\u0011A\u0001\u0006CB\u0004H.\u001f\u000b\u0004U\u0005\r\u0001\"\u0002\u0010\u007f\u0001\u0004\t\u0003\"CA\u0004g\u0006\u0005I\u0011QA\u0005\u0003\u001d)h.\u00199qYf$B!a\u0003\u0002\u0012A!a\"!\u0004\"\u0013\r\tya\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0013\u0005M\u0011QAA\u0001\u0002\u0004Q\u0013a\u0001=%a!I\u0011qC:\u0002\u0002\u0013%\u0011\u0011D\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002\u001cA\u0019\u0011)!\b\n\u0007\u0005}!I\u0001\u0004PE*,7\r\u001e"
)
public class RemoteProcessDisconnected implements InboxMessage, Product, Serializable {
   private final RpcAddress remoteAddress;

   public static Option<RpcAddress> unapply(RemoteProcessDisconnected var0) {
      return RemoteProcessDisconnected$.MODULE$.unapply(var0);
   }

   public static RemoteProcessDisconnected apply(RpcAddress var0) {
      return RemoteProcessDisconnected$.MODULE$.apply(var0);
   }

   public static <A> Function1<RpcAddress, A> andThen(Function1<RemoteProcessDisconnected, A> var0) {
      return RemoteProcessDisconnected$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, RemoteProcessDisconnected> compose(Function1<A, RpcAddress> var0) {
      return RemoteProcessDisconnected$.MODULE$.compose(var0);
   }

   public RpcAddress remoteAddress() {
      return this.remoteAddress;
   }

   public RemoteProcessDisconnected copy(RpcAddress remoteAddress) {
      return new RemoteProcessDisconnected(remoteAddress);
   }

   public RpcAddress copy$default$1() {
      return this.remoteAddress();
   }

   public String productPrefix() {
      return "RemoteProcessDisconnected";
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
      return x$1 instanceof RemoteProcessDisconnected;
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
            if (x$1 instanceof RemoteProcessDisconnected) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     RemoteProcessDisconnected var4 = (RemoteProcessDisconnected)x$1;
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

   public RemoteProcessDisconnected(RpcAddress remoteAddress) {
      this.remoteAddress = remoteAddress;
      Product$class.$init$(this);
   }
}
