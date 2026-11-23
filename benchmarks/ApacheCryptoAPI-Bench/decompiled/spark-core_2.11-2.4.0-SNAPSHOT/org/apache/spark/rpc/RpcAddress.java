package org.apache.spark.rpc;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015c!B\u0001\u0003\u0001\u0012Q!A\u0003*qG\u0006#GM]3tg*\u00111\u0001B\u0001\u0004eB\u001c'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011\u0003\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\u000b\n\u0005Yi!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\t!|7\u000f^\u0002\u0001+\u0005Y\u0002C\u0001\u000f \u001d\taQ$\u0003\u0002\u001f\u001b\u00051\u0001K]3eK\u001aL!\u0001I\u0011\u0003\rM#(/\u001b8h\u0015\tqR\u0002\u0003\u0005$\u0001\tE\t\u0015!\u0003\u001c\u0003\u0015Awn\u001d;!\u0011!)\u0003A!f\u0001\n\u00031\u0013\u0001\u00029peR,\u0012a\n\t\u0003\u0019!J!!K\u0007\u0003\u0007%sG\u000f\u0003\u0005,\u0001\tE\t\u0015!\u0003(\u0003\u0015\u0001xN\u001d;!\u0011\u0015i\u0003\u0001\"\u0001/\u0003\u0019a\u0014N\\5u}Q\u0019q&\r\u001a\u0011\u0005A\u0002Q\"\u0001\u0002\t\u000baa\u0003\u0019A\u000e\t\u000b\u0015b\u0003\u0019A\u0014\t\u000bQ\u0002A\u0011\u0001\u000e\u0002\u0011!|7\u000f\u001e)peRDQA\u000e\u0001\u0005\u0002i\t!\u0002^8Ta\u0006\u00148.\u0016*M\u0011\u0015A\u0004\u0001\"\u0011:\u0003!!xn\u0015;sS:<G#A\u000e\t\u000fm\u0002\u0011\u0011!C\u0001y\u0005!1m\u001c9z)\rySH\u0010\u0005\b1i\u0002\n\u00111\u0001\u001c\u0011\u001d)#\b%AA\u0002\u001dBq\u0001\u0011\u0001\u0012\u0002\u0013\u0005\u0011)\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003\tS#aG\",\u0003\u0011\u0003\"!\u0012&\u000e\u0003\u0019S!a\u0012%\u0002\u0013Ut7\r[3dW\u0016$'BA%\u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u0017\u001a\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001di\u0005!%A\u0005\u00029\u000babY8qs\u0012\"WMZ1vYR$#'F\u0001PU\t93\tC\u0004R\u0001\u0005\u0005I\u0011\t*\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005\u0019\u0006C\u0001+Z\u001b\u0005)&B\u0001,X\u0003\u0011a\u0017M\\4\u000b\u0003a\u000bAA[1wC&\u0011\u0001%\u0016\u0005\b7\u0002\t\t\u0011\"\u0001'\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011\u001di\u0006!!A\u0005\u0002y\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002`EB\u0011A\u0002Y\u0005\u0003C6\u00111!\u00118z\u0011\u001d\u0019G,!AA\u0002\u001d\n1\u0001\u001f\u00132\u0011\u001d)\u0007!!A\u0005B\u0019\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002OB\u0019\u0001n[0\u000e\u0003%T!A[\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002mS\nA\u0011\n^3sCR|'\u000fC\u0004o\u0001\u0005\u0005I\u0011A8\u0002\u0011\r\fg.R9vC2$\"\u0001]:\u0011\u00051\t\u0018B\u0001:\u000e\u0005\u001d\u0011un\u001c7fC:DqaY7\u0002\u0002\u0003\u0007q\fC\u0004v\u0001\u0005\u0005I\u0011\t<\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\n\u0005\bq\u0002\t\t\u0011\"\u0011z\u0003\u0019)\u0017/^1mgR\u0011\u0001O\u001f\u0005\bG^\f\t\u00111\u0001`\u000f\u0019a(\u0001#\u0001\u0005{\u0006Q!\u000b]2BI\u0012\u0014Xm]:\u0011\u0005ArhAB\u0001\u0003\u0011\u0003!qpE\u0002\u007f\u0017QAa!\f@\u0005\u0002\u0005\rA#A?\t\u000f\u0005\u001da\u0010\"\u0001\u0002\n\u0005iaM]8n+JK5\u000b\u001e:j]\u001e$2aLA\u0006\u0011\u001d\ti!!\u0002A\u0002m\t1!\u001e:j\u0011\u001d\t\tB C\u0001\u0003'\tAB\u001a:p[N\u0003\u0018M]6V%2#2aLA\u000b\u0011\u001d\t9\"a\u0004A\u0002m\t\u0001b\u001d9be.,&\u000f\u001c\u0005\n\u00037q\u0018\u0011!CA\u0003;\tQ!\u00199qYf$RaLA\u0010\u0003CAa\u0001GA\r\u0001\u0004Y\u0002BB\u0013\u0002\u001a\u0001\u0007q\u0005C\u0005\u0002&y\f\t\u0011\"!\u0002(\u00059QO\\1qa2LH\u0003BA\u0015\u0003k\u0001R\u0001DA\u0016\u0003_I1!!\f\u000e\u0005\u0019y\u0005\u000f^5p]B)A\"!\r\u001cO%\u0019\u00111G\u0007\u0003\rQ+\b\u000f\\33\u0011%\t9$a\t\u0002\u0002\u0003\u0007q&A\u0002yIAB\u0011\"a\u000f\u007f\u0003\u0003%I!!\u0010\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u007f\u00012\u0001VA!\u0013\r\t\u0019%\u0016\u0002\u0007\u001f\nTWm\u0019;"
)
public class RpcAddress implements Product, Serializable {
   private final String host;
   private final int port;

   public static Option<Tuple2<String, Object>> unapply(RpcAddress var0) {
      return RpcAddress$.MODULE$.unapply(var0);
   }

   public static RpcAddress apply(String var0, int var1) {
      return RpcAddress$.MODULE$.apply(var0, var1);
   }

   public static RpcAddress fromSparkURL(String var0) {
      return RpcAddress$.MODULE$.fromSparkURL(var0);
   }

   public static RpcAddress fromURIString(String var0) {
      return RpcAddress$.MODULE$.fromURIString(var0);
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public String hostPort() {
      return (new StringBuilder()).append((Object)this.host()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(this.port())).toString();
   }

   public String toSparkURL() {
      return (new StringBuilder()).append((Object)"spark://").append((Object)this.hostPort()).toString();
   }

   public String toString() {
      return this.hostPort();
   }

   public RpcAddress copy(String host, int port) {
      return new RpcAddress(host, port);
   }

   public String copy$default$1() {
      return this.host();
   }

   public int copy$default$2() {
      return this.port();
   }

   public String productPrefix() {
      return "RpcAddress";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.host();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.port());
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
      return x$1 instanceof RpcAddress;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.host()));
      var1 = Statics.mix(var1, this.port());
      return Statics.finalizeHash(var1, 2);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof RpcAddress) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  label36: {
                     RpcAddress var4 = (RpcAddress)x$1;
                     String var10000 = this.host();
                     String var5 = var4.host();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label36;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label36;
                     }

                     if (this.port() == var4.port() && var4.canEqual(this)) {
                        var7 = true;
                        break label37;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label54;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public RpcAddress(String host, int port) {
      this.host = host;
      this.port = port;
      Product$class.$init$(this);
   }
}
