package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}b!B\u0001\u0003\u0001\na!!D(oK^\u000b\u00170T3tg\u0006<WM\u0003\u0002\u0004\t\u0005)a.\u001a;us*\u0011QAB\u0001\u0004eB\u001c'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u000b\u0001i1c\u0006\u000e\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0003\u0013\t1\"A\u0001\u0007J]\n|\u00070T3tg\u0006<W\r\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\b!J|G-^2u!\tq1$\u0003\u0002\u001d\u001f\ta1+\u001a:jC2L'0\u00192mK\"Aa\u0004\u0001BK\u0002\u0013\u0005\u0001%A\u0007tK:$WM]!eIJ,7o]\u0002\u0001+\u0005\t\u0003C\u0001\u0012$\u001b\u0005!\u0011B\u0001\u0013\u0005\u0005)\u0011\u0006oY!eIJ,7o\u001d\u0005\tM\u0001\u0011\t\u0012)A\u0005C\u0005q1/\u001a8eKJ\fE\r\u001a:fgN\u0004\u0003\u0002\u0003\u0015\u0001\u0005+\u0007I\u0011A\u0015\u0002\u000f\r|g\u000e^3oiV\t!\u0006\u0005\u0002\u000fW%\u0011Af\u0004\u0002\u0004\u0003:L\b\u0002\u0003\u0018\u0001\u0005#\u0005\u000b\u0011\u0002\u0016\u0002\u0011\r|g\u000e^3oi\u0002BQ\u0001\r\u0001\u0005\u0002E\na\u0001P5oSRtDc\u0001\u001a4iA\u0011A\u0003\u0001\u0005\u0006==\u0002\r!\t\u0005\u0006Q=\u0002\rA\u000b\u0005\bm\u0001\t\t\u0011\"\u00018\u0003\u0011\u0019w\u000e]=\u0015\u0007IB\u0014\bC\u0004\u001fkA\u0005\t\u0019A\u0011\t\u000f!*\u0004\u0013!a\u0001U!91\bAI\u0001\n\u0003a\u0014AD2paf$C-\u001a4bk2$H%M\u000b\u0002{)\u0012\u0011EP\u0016\u0002\u007fA\u0011\u0001)R\u0007\u0002\u0003*\u0011!iQ\u0001\nk:\u001c\u0007.Z2lK\u0012T!\u0001R\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002G\u0003\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f!\u0003\u0011\u0013!C\u0001\u0013\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#\u0001&+\u0005)r\u0004b\u0002'\u0001\u0003\u0003%\t%T\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u00039\u0003\"a\u0014+\u000e\u0003AS!!\u0015*\u0002\t1\fgn\u001a\u0006\u0002'\u0006!!.\u0019<b\u0013\t)\u0006K\u0001\u0004TiJLgn\u001a\u0005\b/\u0002\t\t\u0011\"\u0001Y\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005I\u0006C\u0001\b[\u0013\tYvBA\u0002J]RDq!\u0018\u0001\u0002\u0002\u0013\u0005a,\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005)z\u0006b\u00021]\u0003\u0003\u0005\r!W\u0001\u0004q\u0012\n\u0004b\u00022\u0001\u0003\u0003%\teY\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\tA\rE\u0002fQ*j\u0011A\u001a\u0006\u0003O>\t!bY8mY\u0016\u001cG/[8o\u0013\tIgM\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dY\u0007!!A\u0005\u00021\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003[B\u0004\"A\u00048\n\u0005=|!a\u0002\"p_2,\u0017M\u001c\u0005\bA*\f\t\u00111\u0001+\u0011\u001d\u0011\b!!A\u0005BM\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00023\"9Q\u000fAA\u0001\n\u00032\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u00039Cq\u0001\u001f\u0001\u0002\u0002\u0013\u0005\u00130\u0001\u0004fcV\fGn\u001d\u000b\u0003[jDq\u0001Y<\u0002\u0002\u0003\u0007!f\u0002\u0005}\u0005\u0005\u0005\t\u0012\u0001\u0002~\u00035ye.Z,bs6+7o]1hKB\u0011AC \u0004\t\u0003\t\t\t\u0011#\u0001\u0003\u007fN!a0!\u0001\u001b!\u001d\t\u0019!!\u0003\"UIj!!!\u0002\u000b\u0007\u0005\u001dq\"A\u0004sk:$\u0018.\\3\n\t\u0005-\u0011Q\u0001\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u0014\u0004B\u0002\u0019\u007f\t\u0003\ty\u0001F\u0001~\u0011\u001d)h0!A\u0005FYD\u0011\"!\u0006\u007f\u0003\u0003%\t)a\u0006\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000bI\nI\"a\u0007\t\ry\t\u0019\u00021\u0001\"\u0011\u0019A\u00131\u0003a\u0001U!I\u0011q\u0004@\u0002\u0002\u0013\u0005\u0015\u0011E\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t\u0019#a\f\u0011\u000b9\t)#!\u000b\n\u0007\u0005\u001drB\u0001\u0004PaRLwN\u001c\t\u0006\u001d\u0005-\u0012EK\u0005\u0004\u0003[y!A\u0002+va2,'\u0007C\u0005\u00022\u0005u\u0011\u0011!a\u0001e\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005Ub0!A\u0005\n\u0005]\u0012a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u000f\u0011\u0007=\u000bY$C\u0002\u0002>A\u0013aa\u00142kK\u000e$\b"
)
public class OneWayMessage implements InboxMessage, Product, Serializable {
   private final RpcAddress senderAddress;
   private final Object content;

   public static Option<Tuple2<RpcAddress, Object>> unapply(OneWayMessage var0) {
      return OneWayMessage$.MODULE$.unapply(var0);
   }

   public static OneWayMessage apply(RpcAddress var0, Object var1) {
      return OneWayMessage$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<RpcAddress, Object>, OneWayMessage> tupled() {
      return OneWayMessage$.MODULE$.tupled();
   }

   public static Function1<RpcAddress, Function1<Object, OneWayMessage>> curried() {
      return OneWayMessage$.MODULE$.curried();
   }

   public RpcAddress senderAddress() {
      return this.senderAddress;
   }

   public Object content() {
      return this.content;
   }

   public OneWayMessage copy(RpcAddress senderAddress, Object content) {
      return new OneWayMessage(senderAddress, content);
   }

   public RpcAddress copy$default$1() {
      return this.senderAddress();
   }

   public Object copy$default$2() {
      return this.content();
   }

   public String productPrefix() {
      return "OneWayMessage";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.senderAddress();
            break;
         case 1:
            var10000 = this.content();
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
      return x$1 instanceof OneWayMessage;
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
         label54: {
            boolean var3;
            if (x$1 instanceof OneWayMessage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  label36: {
                     OneWayMessage var4 = (OneWayMessage)x$1;
                     RpcAddress var10000 = this.senderAddress();
                     RpcAddress var5 = var4.senderAddress();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label36;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label36;
                     }

                     if (BoxesRunTime.equals(this.content(), var4.content()) && var4.canEqual(this)) {
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

   public OneWayMessage(RpcAddress senderAddress, Object content) {
      this.senderAddress = senderAddress;
      this.content = content;
      Product$class.$init$(this);
   }
}
