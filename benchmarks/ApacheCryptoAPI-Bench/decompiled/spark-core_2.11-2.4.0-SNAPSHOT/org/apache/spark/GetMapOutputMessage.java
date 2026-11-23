package org.apache.spark;

import org.apache.spark.rpc.RpcCallContext;
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
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mb!B\u0001\u0003\u0001\nA!aE$fi6\u000b\u0007oT;uaV$X*Z:tC\u001e,'BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0014\t\u0001IqB\u0005\t\u0003\u00155i\u0011a\u0003\u0006\u0002\u0019\u0005)1oY1mC&\u0011ab\u0003\u0002\u0007\u0003:L(+\u001a4\u0011\u0005)\u0001\u0012BA\t\f\u0005\u001d\u0001&o\u001c3vGR\u0004\"AC\n\n\u0005QY!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\f\u0001\u0005+\u0007I\u0011\u0001\r\u0002\u0013MDWO\u001a4mK&#7\u0001A\u000b\u00023A\u0011!BG\u0005\u00037-\u00111!\u00138u\u0011!i\u0002A!E!\u0002\u0013I\u0012AC:ik\u001a4G.Z%eA!Aq\u0004\u0001BK\u0002\u0013\u0005\u0001%A\u0004d_:$X\r\u001f;\u0016\u0003\u0005\u0002\"AI\u0013\u000e\u0003\rR!\u0001\n\u0002\u0002\u0007I\u00048-\u0003\u0002'G\tq!\u000b]2DC2d7i\u001c8uKb$\b\u0002\u0003\u0015\u0001\u0005#\u0005\u000b\u0011B\u0011\u0002\u0011\r|g\u000e^3yi\u0002BQA\u000b\u0001\u0005\u0002-\na\u0001P5oSRtDc\u0001\u0017/_A\u0011Q\u0006A\u0007\u0002\u0005!)a#\u000ba\u00013!)q$\u000ba\u0001C!9\u0011\u0007AA\u0001\n\u0003\u0011\u0014\u0001B2paf$2\u0001L\u001a5\u0011\u001d1\u0002\u0007%AA\u0002eAqa\b\u0019\u0011\u0002\u0003\u0007\u0011\u0005C\u00047\u0001E\u0005I\u0011A\u001c\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0001H\u000b\u0002\u001as-\n!\b\u0005\u0002<\u00016\tAH\u0003\u0002>}\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u007f-\t!\"\u00198o_R\fG/[8o\u0013\t\tEHA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dqa\u0011\u0001\u0012\u0002\u0013\u0005A)\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003\u0015S#!I\u001d\t\u000f\u001d\u0003\u0011\u0011!C!\u0011\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u0013\t\u0003\u0015>k\u0011a\u0013\u0006\u0003\u00196\u000bA\u0001\\1oO*\ta*\u0001\u0003kCZ\f\u0017B\u0001)L\u0005\u0019\u0019FO]5oO\"9!\u000bAA\u0001\n\u0003A\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bb\u0002+\u0001\u0003\u0003%\t!V\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t1\u0016\f\u0005\u0002\u000b/&\u0011\u0001l\u0003\u0002\u0004\u0003:L\bb\u0002.T\u0003\u0003\u0005\r!G\u0001\u0004q\u0012\n\u0004b\u0002/\u0001\u0003\u0003%\t%X\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\ta\fE\u0002`EZk\u0011\u0001\u0019\u0006\u0003C.\t!bY8mY\u0016\u001cG/[8o\u0013\t\u0019\u0007M\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d)\u0007!!A\u0005\u0002\u0019\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003O*\u0004\"A\u00035\n\u0005%\\!a\u0002\"p_2,\u0017M\u001c\u0005\b5\u0012\f\t\u00111\u0001W\u0011\u001da\u0007!!A\u0005B5\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00023!9q\u000eAA\u0001\n\u0003\u0002\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003%CqA\u001d\u0001\u0002\u0002\u0013\u00053/\u0001\u0004fcV\fGn\u001d\u000b\u0003ORDqAW9\u0002\u0002\u0003\u0007ak\u0002\u0005w\u0005\u0005\u0005\t\u0012\u0001\u0002x\u0003M9U\r^'ba>+H\u000f];u\u001b\u0016\u001c8/Y4f!\ti\u0003P\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002z'\rA(P\u0005\t\u0006wzL\u0012\u0005L\u0007\u0002y*\u0011QpC\u0001\beVtG/[7f\u0013\tyHPA\tBEN$(/Y2u\rVt7\r^5p]JBaA\u000b=\u0005\u0002\u0005\rA#A<\t\u000f=D\u0018\u0011!C#a\"I\u0011\u0011\u0002=\u0002\u0002\u0013\u0005\u00151B\u0001\u0006CB\u0004H.\u001f\u000b\u0006Y\u00055\u0011q\u0002\u0005\u0007-\u0005\u001d\u0001\u0019A\r\t\r}\t9\u00011\u0001\"\u0011%\t\u0019\u0002_A\u0001\n\u0003\u000b)\"A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005]\u00111\u0005\t\u0006\u0015\u0005e\u0011QD\u0005\u0004\u00037Y!AB(qi&|g\u000eE\u0003\u000b\u0003?I\u0012%C\u0002\u0002\"-\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\u0013\u0003#\t\t\u00111\u0001-\u0003\rAH\u0005\r\u0005\n\u0003SA\u0018\u0011!C\u0005\u0003W\t1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u0006\t\u0004\u0015\u0006=\u0012bAA\u0019\u0017\n1qJ\u00196fGR\u0004"
)
public class GetMapOutputMessage implements Product, Serializable {
   private final int shuffleId;
   private final RpcCallContext context;

   public static Option<Tuple2<Object, RpcCallContext>> unapply(GetMapOutputMessage var0) {
      return GetMapOutputMessage$.MODULE$.unapply(var0);
   }

   public static GetMapOutputMessage apply(int var0, RpcCallContext var1) {
      return GetMapOutputMessage$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, RpcCallContext>, GetMapOutputMessage> tupled() {
      return GetMapOutputMessage$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<RpcCallContext, GetMapOutputMessage>> curried() {
      return GetMapOutputMessage$.MODULE$.curried();
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public RpcCallContext context() {
      return this.context;
   }

   public GetMapOutputMessage copy(int shuffleId, RpcCallContext context) {
      return new GetMapOutputMessage(shuffleId, context);
   }

   public int copy$default$1() {
      return this.shuffleId();
   }

   public RpcCallContext copy$default$2() {
      return this.context();
   }

   public String productPrefix() {
      return "GetMapOutputMessage";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.shuffleId());
            break;
         case 1:
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
      return x$1 instanceof GetMapOutputMessage;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.shuffleId());
      var1 = Statics.mix(var1, Statics.anyHash(this.context()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof GetMapOutputMessage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  GetMapOutputMessage var4 = (GetMapOutputMessage)x$1;
                  if (this.shuffleId() == var4.shuffleId()) {
                     label35: {
                        RpcCallContext var10000 = this.context();
                        RpcCallContext var5 = var4.context();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
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

   public GetMapOutputMessage(int shuffleId, RpcCallContext context) {
      this.shuffleId = shuffleId;
      this.context = context;
      Product$class.$init$(this);
   }
}
