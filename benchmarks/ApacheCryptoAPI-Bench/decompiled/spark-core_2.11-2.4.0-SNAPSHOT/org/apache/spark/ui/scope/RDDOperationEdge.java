package org.apache.spark.ui.scope;

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
   bytes = "\u0006\u0001\u0005%b!B\u0001\u0003\u0001\u001aa!\u0001\u0005*E\t>\u0003XM]1uS>tW\tZ4f\u0015\t\u0019A!A\u0003tG>\u0004XM\u0003\u0002\u0006\r\u0005\u0011Q/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON!\u0001!D\n\u0017!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011a\u0002F\u0005\u0003+=\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000f/%\u0011\u0001d\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t5\u0001\u0011)\u001a!C\u00019\u00051aM]8n\u0013\u0012\u001c\u0001!F\u0001\u001e!\tqa$\u0003\u0002 \u001f\t\u0019\u0011J\u001c;\t\u0011\u0005\u0002!\u0011#Q\u0001\nu\tqA\u001a:p[&#\u0007\u0005\u0003\u0005$\u0001\tU\r\u0011\"\u0001\u001d\u0003\u0011!x.\u00133\t\u0011\u0015\u0002!\u0011#Q\u0001\nu\tQ\u0001^8JI\u0002BQa\n\u0001\u0005\u0002!\na\u0001P5oSRtDcA\u0015,YA\u0011!\u0006A\u0007\u0002\u0005!)!D\na\u0001;!)1E\na\u0001;!9a\u0006AA\u0001\n\u0003y\u0013\u0001B2paf$2!\u000b\u00192\u0011\u001dQR\u0006%AA\u0002uAqaI\u0017\u0011\u0002\u0003\u0007Q\u0004C\u00044\u0001E\u0005I\u0011\u0001\u001b\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tQG\u000b\u0002\u001em-\nq\u0007\u0005\u00029{5\t\u0011H\u0003\u0002;w\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003y=\t!\"\u00198o_R\fG/[8o\u0013\tq\u0014HA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u0011\u0001\u0012\u0002\u0013\u0005A'\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000f\t\u0003\u0011\u0011!C!\u0007\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012\u0001\u0012\t\u0003\u000b*k\u0011A\u0012\u0006\u0003\u000f\"\u000bA\u0001\\1oO*\t\u0011*\u0001\u0003kCZ\f\u0017BA&G\u0005\u0019\u0019FO]5oO\"9Q\nAA\u0001\n\u0003a\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bbB(\u0001\u0003\u0003%\t\u0001U\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\tF\u000b\u0005\u0002\u000f%&\u00111k\u0004\u0002\u0004\u0003:L\bbB+O\u0003\u0003\u0005\r!H\u0001\u0004q\u0012\n\u0004bB,\u0001\u0003\u0003%\t\u0005W\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0011\fE\u0002[;Fk\u0011a\u0017\u0006\u00039>\t!bY8mY\u0016\u001cG/[8o\u0013\tq6L\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d\u0001\u0007!!A\u0005\u0002\u0005\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003E\u0016\u0004\"AD2\n\u0005\u0011|!a\u0002\"p_2,\u0017M\u001c\u0005\b+~\u000b\t\u00111\u0001R\u0011\u001d9\u0007!!A\u0005B!\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002;!9!\u000eAA\u0001\n\u0003Z\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0011Cq!\u001c\u0001\u0002\u0002\u0013\u0005c.\u0001\u0004fcV\fGn\u001d\u000b\u0003E>Dq!\u00167\u0002\u0002\u0003\u0007\u0011k\u0002\u0005r\u0005\u0005\u0005\t\u0012\u0001\u0004s\u0003A\u0011F\tR(qKJ\fG/[8o\u000b\u0012<W\r\u0005\u0002+g\u001aA\u0011AAA\u0001\u0012\u00031AoE\u0002tkZ\u0001RA^=\u001e;%j\u0011a\u001e\u0006\u0003q>\tqA];oi&lW-\u0003\u0002{o\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\u000b\u001d\u001aH\u0011\u0001?\u0015\u0003IDqA[:\u0002\u0002\u0013\u00153\u000e\u0003\u0005\u0000g\u0006\u0005I\u0011QA\u0001\u0003\u0015\t\u0007\u000f\u001d7z)\u0015I\u00131AA\u0003\u0011\u0015Qb\u00101\u0001\u001e\u0011\u0015\u0019c\u00101\u0001\u001e\u0011%\tIa]A\u0001\n\u0003\u000bY!A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u00055\u0011\u0011\u0004\t\u0006\u001d\u0005=\u00111C\u0005\u0004\u0003#y!AB(qi&|g\u000eE\u0003\u000f\u0003+iR$C\u0002\u0002\u0018=\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\u000e\u0003\u000f\t\t\u00111\u0001*\u0003\rAH\u0005\r\u0005\n\u0003?\u0019\u0018\u0011!C\u0005\u0003C\t1B]3bIJ+7o\u001c7wKR\u0011\u00111\u0005\t\u0004\u000b\u0006\u0015\u0012bAA\u0014\r\n1qJ\u00196fGR\u0004"
)
public class RDDOperationEdge implements Product, Serializable {
   private final int fromId;
   private final int toId;

   public static Option<Tuple2<Object, Object>> unapply(RDDOperationEdge var0) {
      return RDDOperationEdge$.MODULE$.unapply(var0);
   }

   public static RDDOperationEdge apply(int var0, int var1) {
      return RDDOperationEdge$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, Object>, RDDOperationEdge> tupled() {
      return RDDOperationEdge$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, RDDOperationEdge>> curried() {
      return RDDOperationEdge$.MODULE$.curried();
   }

   public int fromId() {
      return this.fromId;
   }

   public int toId() {
      return this.toId;
   }

   public RDDOperationEdge copy(int fromId, int toId) {
      return new RDDOperationEdge(fromId, toId);
   }

   public int copy$default$1() {
      return this.fromId();
   }

   public int copy$default$2() {
      return this.toId();
   }

   public String productPrefix() {
      return "RDDOperationEdge";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Integer var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.fromId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.toId());
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
      return x$1 instanceof RDDOperationEdge;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.fromId());
      var1 = Statics.mix(var1, this.toId());
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label50: {
            boolean var3;
            if (x$1 instanceof RDDOperationEdge) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               RDDOperationEdge var4 = (RDDOperationEdge)x$1;
               if (this.fromId() == var4.fromId() && this.toId() == var4.toId() && var4.canEqual(this)) {
                  break label50;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public RDDOperationEdge(int fromId, int toId) {
      this.fromId = fromId;
      this.toId = toId;
      Product$class.$init$(this);
   }
}
