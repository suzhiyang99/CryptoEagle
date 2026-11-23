package org.apache.spark.rpc;

import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mc!B\u0001\u0003\u0001\u0012Q!A\u0005*qG\u0016sG\r]8j]R\fE\r\u001a:fgNT!a\u0001\u0003\u0002\u0007I\u00048M\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0011\u00011\"\u0005\u000b\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\ta!#\u0003\u0002\u0014\u001b\t9\u0001K]8ek\u000e$\bC\u0001\u0007\u0016\u0013\t1RB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0019\u0001\tU\r\u0011\"\u0001\u001b\u0003)\u0011\boY!eIJ,7o]\u0002\u0001+\u0005Y\u0002C\u0001\u000f\u001e\u001b\u0005\u0011\u0011B\u0001\u0010\u0003\u0005)\u0011\u0006oY!eIJ,7o\u001d\u0005\tA\u0001\u0011\t\u0012)A\u00057\u0005Y!\u000f]2BI\u0012\u0014Xm]:!\u0011!\u0011\u0003A!f\u0001\n\u0003\u0019\u0013\u0001\u00028b[\u0016,\u0012\u0001\n\t\u0003K!r!\u0001\u0004\u0014\n\u0005\u001dj\u0011A\u0002)sK\u0012,g-\u0003\u0002*U\t11\u000b\u001e:j]\u001eT!aJ\u0007\t\u00111\u0002!\u0011#Q\u0001\n\u0011\nQA\\1nK\u0002BQA\f\u0001\u0005\u0002=\na\u0001P5oSRtDc\u0001\u00192eA\u0011A\u0004\u0001\u0005\u000615\u0002\ra\u0007\u0005\u0006E5\u0002\r\u0001\n\u0005\u0006]\u0001!\t\u0001\u000e\u000b\u0005aU:D\bC\u00037g\u0001\u0007A%\u0001\u0003i_N$\b\"\u0002\u001d4\u0001\u0004I\u0014\u0001\u00029peR\u0004\"\u0001\u0004\u001e\n\u0005mj!aA%oi\")!e\ra\u0001I!9a\b\u0001b\u0001\n\u0003\u001a\u0013\u0001\u0003;p'R\u0014\u0018N\\4\t\r\u0001\u0003\u0001\u0015!\u0003%\u0003%!xn\u0015;sS:<\u0007\u0005C\u0004C\u0001\u0005\u0005I\u0011A\"\u0002\t\r|\u0007/\u001f\u000b\u0004a\u0011+\u0005b\u0002\rB!\u0003\u0005\ra\u0007\u0005\bE\u0005\u0003\n\u00111\u0001%\u0011\u001d9\u0005!%A\u0005\u0002!\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001JU\tY\"jK\u0001L!\ta\u0015+D\u0001N\u0015\tqu*A\u0005v]\u000eDWmY6fI*\u0011\u0001+D\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001*N\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b)\u0002\t\n\u0011\"\u0001V\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\u0012A\u0016\u0016\u0003I)Cq\u0001\u0017\u0001\u0002\u0002\u0013\u0005\u0013,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00025B\u00111\fY\u0007\u00029*\u0011QLX\u0001\u0005Y\u0006twMC\u0001`\u0003\u0011Q\u0017M^1\n\u0005%b\u0006b\u00022\u0001\u0003\u0003%\taY\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002s!9Q\rAA\u0001\n\u00031\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003O*\u0004\"\u0001\u00045\n\u0005%l!aA!os\"91\u000eZA\u0001\u0002\u0004I\u0014a\u0001=%c!9Q\u000eAA\u0001\n\u0003r\u0017a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003=\u00042\u0001]:h\u001b\u0005\t(B\u0001:\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003iF\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\bm\u0002\t\t\u0011\"\u0001x\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001=|!\ta\u00110\u0003\u0002{\u001b\t9!i\\8mK\u0006t\u0007bB6v\u0003\u0003\u0005\ra\u001a\u0005\b{\u0002\t\t\u0011\"\u0011\u007f\u0003!A\u0017m\u001d5D_\u0012,G#A\u001d\t\u0013\u0005\u0005\u0001!!A\u0005B\u0005\r\u0011AB3rk\u0006d7\u000fF\u0002y\u0003\u000bAqa[@\u0002\u0002\u0003\u0007qm\u0002\u0005\u0002\n\tA\t\u0001BA\u0006\u0003I\u0011\u0006oY#oIB|\u0017N\u001c;BI\u0012\u0014Xm]:\u0011\u0007q\tiAB\u0004\u0002\u0005!\u0005A!a\u0004\u0014\t\u000551\u0002\u0006\u0005\b]\u00055A\u0011AA\n)\t\tY\u0001\u0003\u0005\u0002\u0018\u00055A\u0011AA\r\u0003\u0015\t\u0007\u000f\u001d7z)\u001d\u0001\u00141DA\u000f\u0003?AaANA\u000b\u0001\u0004!\u0003B\u0002\u001d\u0002\u0016\u0001\u0007\u0011\b\u0003\u0004#\u0003+\u0001\r\u0001\n\u0005\t\u0003/\ti\u0001\"\u0001\u0002$Q\u0019\u0001'!\n\t\u000f\u0005\u001d\u0012\u0011\u0005a\u0001I\u0005A1\u000f]1sWV\u0013H\u000e\u0003\u0006\u0002\u0018\u00055\u0011\u0011!CA\u0003W!R\u0001MA\u0017\u0003_Aa\u0001GA\u0015\u0001\u0004Y\u0002B\u0002\u0012\u0002*\u0001\u0007A\u0005\u0003\u0006\u00024\u00055\u0011\u0011!CA\u0003k\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u00028\u0005\r\u0003#\u0002\u0007\u0002:\u0005u\u0012bAA\u001e\u001b\t1q\n\u001d;j_:\u0004R\u0001DA 7\u0011J1!!\u0011\u000e\u0005\u0019!V\u000f\u001d7fe!I\u0011QIA\u0019\u0003\u0003\u0005\r\u0001M\u0001\u0004q\u0012\u0002\u0004BCA%\u0003\u001b\t\t\u0011\"\u0003\u0002L\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\ti\u0005E\u0002\\\u0003\u001fJ1!!\u0015]\u0005\u0019y%M[3di\u0002"
)
public class RpcEndpointAddress implements Product, Serializable {
   private final RpcAddress rpcAddress;
   private final String name;
   private final String toString;

   public static Option<Tuple2<RpcAddress, String>> unapply(RpcEndpointAddress var0) {
      return RpcEndpointAddress$.MODULE$.unapply(var0);
   }

   public static RpcEndpointAddress apply(RpcAddress var0, String var1) {
      return RpcEndpointAddress$.MODULE$.apply(var0, var1);
   }

   public static RpcEndpointAddress apply(String var0) {
      return RpcEndpointAddress$.MODULE$.apply(var0);
   }

   public static RpcEndpointAddress apply(String var0, int var1, String var2) {
      return RpcEndpointAddress$.MODULE$.apply(var0, var1, var2);
   }

   public RpcAddress rpcAddress() {
      return this.rpcAddress;
   }

   public String name() {
      return this.name;
   }

   public String toString() {
      return this.toString;
   }

   public RpcEndpointAddress copy(RpcAddress rpcAddress, String name) {
      return new RpcEndpointAddress(rpcAddress, name);
   }

   public RpcAddress copy$default$1() {
      return this.rpcAddress();
   }

   public String copy$default$2() {
      return this.name();
   }

   public String productPrefix() {
      return "RpcEndpointAddress";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.rpcAddress();
            break;
         case 1:
            var10000 = this.name();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof RpcEndpointAddress;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof RpcEndpointAddress) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     RpcEndpointAddress var4 = (RpcEndpointAddress)x$1;
                     RpcAddress var10000 = this.rpcAddress();
                     RpcAddress var5 = var4.rpcAddress();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     String var7 = this.name();
                     String var6 = var4.name();
                     if (var7 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var7.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public RpcEndpointAddress(RpcAddress rpcAddress, String name) {
      this.rpcAddress = rpcAddress;
      this.name = name;
      Product$class.$init$(this);
      Predef$.MODULE$.require(name != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "RpcEndpoint name must be provided.";
         }
      });
      this.toString = rpcAddress == null ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark-client://", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark://", "@", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name, rpcAddress.host(), BoxesRunTime.boxToInteger(rpcAddress.port())}));
   }

   public RpcEndpointAddress(String host, int port, String name) {
      this(new RpcAddress(host, port), name);
   }
}
