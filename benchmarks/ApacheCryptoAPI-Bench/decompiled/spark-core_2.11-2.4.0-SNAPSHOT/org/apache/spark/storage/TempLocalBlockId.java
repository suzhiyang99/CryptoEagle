package org.apache.spark.storage;

import java.util.UUID;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-b!B\u0001\u0003\u0001\u0012Q!\u0001\u0005+f[BdunY1m\u00052|7m[%e\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0010+A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\b\u00052|7m[%e!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001\u0005\f\n\u0005]\t\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\r\u0001\u0005+\u0007I\u0011A\u000e\u0002\u0005%$7\u0001A\u000b\u00029A\u0011QDI\u0007\u0002=)\u0011q\u0004I\u0001\u0005kRLGNC\u0001\"\u0003\u0011Q\u0017M^1\n\u0005\rr\"\u0001B+V\u0013\u0012C\u0001\"\n\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\u0004S\u0012\u0004\u0003\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\b\u0006\u0002*UA\u0011A\u0002\u0001\u0005\u00063\u0019\u0002\r\u0001\b\u0005\u0006Y\u0001!\t%L\u0001\u0005]\u0006lW-F\u0001/!\ty#G\u0004\u0002\u0011a%\u0011\u0011'E\u0001\u0007!J,G-\u001a4\n\u0005M\"$AB*ue&twM\u0003\u00022#!9a\u0007AA\u0001\n\u00039\u0014\u0001B2paf$\"!\u000b\u001d\t\u000fe)\u0004\u0013!a\u00019!9!\bAI\u0001\n\u0003Y\u0014AD2paf$C-\u001a4bk2$H%M\u000b\u0002y)\u0012A$P\u0016\u0002}A\u0011q\bR\u0007\u0002\u0001*\u0011\u0011IQ\u0001\nk:\u001c\u0007.Z2lK\u0012T!aQ\t\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002F\u0001\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f\u001d\u0003\u0011\u0011!C!\u0011\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u0013\t\u0003\u00156k\u0011a\u0013\u0006\u0003\u0019\u0002\nA\u0001\\1oO&\u00111g\u0013\u0005\b\u001f\u0002\t\t\u0011\"\u0001Q\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005\t\u0006C\u0001\tS\u0013\t\u0019\u0016CA\u0002J]RDq!\u0016\u0001\u0002\u0002\u0013\u0005a+\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005]S\u0006C\u0001\tY\u0013\tI\u0016CA\u0002B]fDqa\u0017+\u0002\u0002\u0003\u0007\u0011+A\u0002yIEBq!\u0018\u0001\u0002\u0002\u0013\u0005c,A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005y\u0006c\u00011d/6\t\u0011M\u0003\u0002c#\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0011\f'\u0001C%uKJ\fGo\u001c:\t\u000f\u0019\u0004\u0011\u0011!C\u0001O\u0006A1-\u00198FcV\fG\u000e\u0006\u0002iWB\u0011\u0001#[\u0005\u0003UF\u0011qAQ8pY\u0016\fg\u000eC\u0004\\K\u0006\u0005\t\u0019A,\t\u000f5\u0004\u0011\u0011!C!]\u0006A\u0001.Y:i\u0007>$W\rF\u0001R\u0011\u001d\u0001\b!!A\u0005BE\fa!Z9vC2\u001cHC\u00015s\u0011\u001dYv.!AA\u0002];\u0001\u0002\u001e\u0002\u0002\u0002#\u0005A!^\u0001\u0011)\u0016l\u0007\u000fT8dC2\u0014En\\2l\u0013\u0012\u0004\"\u0001\u0004<\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\t]\u001c2A\u001e=\u0016!\u0011IH\u0010H\u0015\u000e\u0003iT!a_\t\u0002\u000fI,h\u000e^5nK&\u0011QP\u001f\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004\"B\u0014w\t\u0003yH#A;\t\u0013\u0005\ra/!A\u0005F\u0005\u0015\u0011\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003%C\u0011\"!\u0003w\u0003\u0003%\t)a\u0003\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007%\ni\u0001\u0003\u0004\u001a\u0003\u000f\u0001\r\u0001\b\u0005\n\u0003#1\u0018\u0011!CA\u0003'\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\u0016\u0005m\u0001\u0003\u0002\t\u0002\u0018qI1!!\u0007\u0012\u0005\u0019y\u0005\u000f^5p]\"I\u0011QDA\b\u0003\u0003\u0005\r!K\u0001\u0004q\u0012\u0002\u0004\"CA\u0011m\u0006\u0005I\u0011BA\u0012\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u0015\u0002c\u0001&\u0002(%\u0019\u0011\u0011F&\u0003\r=\u0013'.Z2u\u0001"
)
public class TempLocalBlockId extends BlockId implements Product, Serializable {
   private final UUID id;

   public static Option<UUID> unapply(TempLocalBlockId var0) {
      return TempLocalBlockId$.MODULE$.unapply(var0);
   }

   public static TempLocalBlockId apply(UUID var0) {
      return TempLocalBlockId$.MODULE$.apply(var0);
   }

   public static <A> Function1<UUID, A> andThen(Function1<TempLocalBlockId, A> var0) {
      return TempLocalBlockId$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, TempLocalBlockId> compose(Function1<A, UUID> var0) {
      return TempLocalBlockId$.MODULE$.compose(var0);
   }

   public UUID id() {
      return this.id;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"temp_local_").append((Object)this.id()).toString();
   }

   public TempLocalBlockId copy(UUID id) {
      return new TempLocalBlockId(id);
   }

   public UUID copy$default$1() {
      return this.id();
   }

   public String productPrefix() {
      return "TempLocalBlockId";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.id();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof TempLocalBlockId;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof TempLocalBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     TempLocalBlockId var4 = (TempLocalBlockId)x$1;
                     UUID var10000 = this.id();
                     UUID var5 = var4.id();
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

   public TempLocalBlockId(UUID id) {
      this.id = id;
      Product$class.$init$(this);
   }
}
