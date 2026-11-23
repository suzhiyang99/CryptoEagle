package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005uc!B\u0001\u0003\u0001\na!A\u0003*qG6+7o]1hK*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7#\u0002\u0001\u000e']Q\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\ta\u0011J\u001c2pq6+7o]1hKB\u0011a\u0002G\u0005\u00033=\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000f7%\u0011Ad\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t=\u0001\u0011)\u001a!C\u0001A\u0005i1/\u001a8eKJ\fE\r\u001a:fgN\u001c\u0001!F\u0001\"!\t\u00113%D\u0001\u0005\u0013\t!CA\u0001\u0006Sa\u000e\fE\r\u001a:fgND\u0001B\n\u0001\u0003\u0012\u0003\u0006I!I\u0001\u000fg\u0016tG-\u001a:BI\u0012\u0014Xm]:!\u0011!A\u0003A!f\u0001\n\u0003I\u0013aB2p]R,g\u000e^\u000b\u0002UA\u0011abK\u0005\u0003Y=\u00111!\u00118z\u0011!q\u0003A!E!\u0002\u0013Q\u0013\u0001C2p]R,g\u000e\u001e\u0011\t\u0011A\u0002!Q3A\u0005\u0002E\nqaY8oi\u0016DH/F\u00013!\t!2'\u0003\u00025\u0005\t\u0019b*\u001a;usJ\u00038mQ1mY\u000e{g\u000e^3yi\"Aa\u0007\u0001B\tB\u0003%!'\u0001\u0005d_:$X\r\u001f;!\u0011\u0015A\u0004\u0001\"\u0001:\u0003\u0019a\u0014N\\5u}Q!!h\u000f\u001f>!\t!\u0002\u0001C\u0003\u001fo\u0001\u0007\u0011\u0005C\u0003)o\u0001\u0007!\u0006C\u00031o\u0001\u0007!\u0007C\u0004@\u0001\u0005\u0005I\u0011\u0001!\u0002\t\r|\u0007/\u001f\u000b\u0005u\u0005\u00135\tC\u0004\u001f}A\u0005\t\u0019A\u0011\t\u000f!r\u0004\u0013!a\u0001U!9\u0001G\u0010I\u0001\u0002\u0004\u0011\u0004bB#\u0001#\u0003%\tAR\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u00059%FA\u0011IW\u0005I\u0005C\u0001&P\u001b\u0005Y%B\u0001'N\u0003%)hn\u00195fG.,GM\u0003\u0002O\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005A[%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9!\u000bAI\u0001\n\u0003\u0019\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u0002)*\u0012!\u0006\u0013\u0005\b-\u0002\t\n\u0011\"\u0001X\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u0017\u0016\u0003e!CqA\u0017\u0001\u0002\u0002\u0013\u00053,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00029B\u0011QLY\u0007\u0002=*\u0011q\fY\u0001\u0005Y\u0006twMC\u0001b\u0003\u0011Q\u0017M^1\n\u0005\rt&AB*ue&tw\rC\u0004f\u0001\u0005\u0005I\u0011\u00014\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u001d\u0004\"A\u00045\n\u0005%|!aA%oi\"91\u000eAA\u0001\n\u0003a\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003U5DqA\u001c6\u0002\u0002\u0003\u0007q-A\u0002yIEBq\u0001\u001d\u0001\u0002\u0002\u0013\u0005\u0013/A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005\u0011\bcA:wU5\tAO\u0003\u0002v\u001f\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005]$(\u0001C%uKJ\fGo\u001c:\t\u000fe\u0004\u0011\u0011!C\u0001u\u0006A1-\u00198FcV\fG\u000e\u0006\u0002|}B\u0011a\u0002`\u0005\u0003{>\u0011qAQ8pY\u0016\fg\u000eC\u0004oq\u0006\u0005\t\u0019\u0001\u0016\t\u0013\u0005\u0005\u0001!!A\u0005B\u0005\r\u0011\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003\u001dD\u0011\"a\u0002\u0001\u0003\u0003%\t%!\u0003\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u0018\u0005\n\u0003\u001b\u0001\u0011\u0011!C!\u0003\u001f\ta!Z9vC2\u001cHcA>\u0002\u0012!Aa.a\u0003\u0002\u0002\u0003\u0007!f\u0002\u0006\u0002\u0016\t\t\t\u0011#\u0001\u0003\u0003/\t!B\u00159d\u001b\u0016\u001c8/Y4f!\r!\u0012\u0011\u0004\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u00037\u0019R!!\u0007\u0002\u001ei\u0001\u0002\"a\b\u0002&\u0005R#GO\u0007\u0003\u0003CQ1!a\t\u0010\u0003\u001d\u0011XO\u001c;j[\u0016LA!a\n\u0002\"\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\u000fa\nI\u0002\"\u0001\u0002,Q\u0011\u0011q\u0003\u0005\u000b\u0003\u000f\tI\"!A\u0005F\u0005%\u0001BCA\u0019\u00033\t\t\u0011\"!\u00024\u0005)\u0011\r\u001d9msR9!(!\u000e\u00028\u0005e\u0002B\u0002\u0010\u00020\u0001\u0007\u0011\u0005\u0003\u0004)\u0003_\u0001\rA\u000b\u0005\u0007a\u0005=\u0002\u0019\u0001\u001a\t\u0015\u0005u\u0012\u0011DA\u0001\n\u0003\u000by$A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005\u0005\u0013Q\n\t\u0006\u001d\u0005\r\u0013qI\u0005\u0004\u0003\u000bz!AB(qi&|g\u000e\u0005\u0004\u000f\u0003\u0013\n#FM\u0005\u0004\u0003\u0017z!A\u0002+va2,7\u0007C\u0005\u0002P\u0005m\u0012\u0011!a\u0001u\u0005\u0019\u0001\u0010\n\u0019\t\u0015\u0005M\u0013\u0011DA\u0001\n\u0013\t)&A\u0006sK\u0006$'+Z:pYZ,GCAA,!\ri\u0016\u0011L\u0005\u0004\u00037r&AB(cU\u0016\u001cG\u000f"
)
public class RpcMessage implements InboxMessage, Product, Serializable {
   private final RpcAddress senderAddress;
   private final Object content;
   private final NettyRpcCallContext context;

   public static Option<Tuple3<RpcAddress, Object, NettyRpcCallContext>> unapply(RpcMessage var0) {
      return RpcMessage$.MODULE$.unapply(var0);
   }

   public static RpcMessage apply(RpcAddress var0, Object var1, NettyRpcCallContext var2) {
      return RpcMessage$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<RpcAddress, Object, NettyRpcCallContext>, RpcMessage> tupled() {
      return RpcMessage$.MODULE$.tupled();
   }

   public static Function1<RpcAddress, Function1<Object, Function1<NettyRpcCallContext, RpcMessage>>> curried() {
      return RpcMessage$.MODULE$.curried();
   }

   public RpcAddress senderAddress() {
      return this.senderAddress;
   }

   public Object content() {
      return this.content;
   }

   public NettyRpcCallContext context() {
      return this.context;
   }

   public RpcMessage copy(RpcAddress senderAddress, Object content, NettyRpcCallContext context) {
      return new RpcMessage(senderAddress, content, context);
   }

   public RpcAddress copy$default$1() {
      return this.senderAddress();
   }

   public Object copy$default$2() {
      return this.content();
   }

   public NettyRpcCallContext copy$default$3() {
      return this.context();
   }

   public String productPrefix() {
      return "RpcMessage";
   }

   public int productArity() {
      return 3;
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
         case 2:
            var10000 = this.context();
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
      return x$1 instanceof RpcMessage;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label63: {
            boolean var3;
            if (x$1 instanceof RpcMessage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  label45: {
                     RpcMessage var4 = (RpcMessage)x$1;
                     RpcAddress var10000 = this.senderAddress();
                     RpcAddress var5 = var4.senderAddress();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label45;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label45;
                     }

                     if (BoxesRunTime.equals(this.content(), var4.content())) {
                        label39: {
                           NettyRpcCallContext var7 = this.context();
                           NettyRpcCallContext var6 = var4.context();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label39;
                              }
                           } else if (!var7.equals(var6)) {
                              break label39;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label46;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label63;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public RpcMessage(RpcAddress senderAddress, Object content, NettyRpcCallContext context) {
      this.senderAddress = senderAddress;
      this.content = content;
      this.context = context;
      Product$class.$init$(this);
   }
}
