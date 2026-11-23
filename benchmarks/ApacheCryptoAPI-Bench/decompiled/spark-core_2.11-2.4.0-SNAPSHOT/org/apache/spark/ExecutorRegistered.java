package org.apache.spark;

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
   bytes = "\u0006\u0001\u0005]a\u0001B\u0001\u0003\t&\u0011!#\u0012=fGV$xN\u001d*fO&\u001cH/\u001a:fI*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0001\u0001\u0014\t\u0001Q\u0001c\u0005\t\u0003\u00179i\u0011\u0001\u0004\u0006\u0002\u001b\u0005)1oY1mC&\u0011q\u0002\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005-\t\u0012B\u0001\n\r\u0005\u001d\u0001&o\u001c3vGR\u0004\"a\u0003\u000b\n\u0005Ua!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\f\u0001\u0005+\u0007I\u0011\u0001\r\u0002\u0015\u0015DXmY;u_JLE-F\u0001\u001a!\tQRD\u0004\u0002\f7%\u0011A\u0004D\u0001\u0007!J,G-\u001a4\n\u0005yy\"AB*ue&twM\u0003\u0002\u001d\u0019!A\u0011\u0005\u0001B\tB\u0003%\u0011$A\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\u0003\"B\u0012\u0001\t\u0003!\u0013A\u0002\u001fj]&$h\b\u0006\u0002&OA\u0011a\u0005A\u0007\u0002\u0005!)qC\ta\u00013!9\u0011\u0006AA\u0001\n\u0003Q\u0013\u0001B2paf$\"!J\u0016\t\u000f]A\u0003\u0013!a\u00013!9Q\u0006AI\u0001\n\u0003q\u0013AD2paf$C-\u001a4bk2$H%M\u000b\u0002_)\u0012\u0011\u0004M\u0016\u0002cA\u0011!gN\u0007\u0002g)\u0011A'N\u0001\nk:\u001c\u0007.Z2lK\u0012T!A\u000e\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u00029g\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000fi\u0002\u0011\u0011!C!w\u0005i\u0001O]8ek\u000e$\bK]3gSb,\u0012\u0001\u0010\t\u0003{\tk\u0011A\u0010\u0006\u0003\u007f\u0001\u000bA\u0001\\1oO*\t\u0011)\u0001\u0003kCZ\f\u0017B\u0001\u0010?\u0011\u001d!\u0005!!A\u0005\u0002\u0015\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012A\u0012\t\u0003\u0017\u001dK!\u0001\u0013\u0007\u0003\u0007%sG\u000fC\u0004K\u0001\u0005\u0005I\u0011A&\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011Aj\u0014\t\u0003\u00175K!A\u0014\u0007\u0003\u0007\u0005s\u0017\u0010C\u0004Q\u0013\u0006\u0005\t\u0019\u0001$\u0002\u0007a$\u0013\u0007C\u0004S\u0001\u0005\u0005I\u0011I*\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012\u0001\u0016\t\u0004+bcU\"\u0001,\u000b\u0005]c\u0011AC2pY2,7\r^5p]&\u0011\u0011L\u0016\u0002\t\u0013R,'/\u0019;pe\"91\fAA\u0001\n\u0003a\u0016\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005u\u0003\u0007CA\u0006_\u0013\tyFBA\u0004C_>dW-\u00198\t\u000fAS\u0016\u0011!a\u0001\u0019\"9!\rAA\u0001\n\u0003\u001a\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003\u0019Cq!\u001a\u0001\u0002\u0002\u0013\u0005c-\u0001\u0005u_N#(/\u001b8h)\u0005a\u0004b\u00025\u0001\u0003\u0003%\t%[\u0001\u0007KF,\u0018\r\\:\u0015\u0005uS\u0007b\u0002)h\u0003\u0003\u0005\r\u0001T\u0004\bY\n\t\t\u0011#\u0003n\u0003I)\u00050Z2vi>\u0014(+Z4jgR,'/\u001a3\u0011\u0005\u0019rgaB\u0001\u0003\u0003\u0003EIa\\\n\u0004]B\u001c\u0002\u0003B9u3\u0015j\u0011A\u001d\u0006\u0003g2\tqA];oi&lW-\u0003\u0002ve\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u0019\t\u000b\rrG\u0011A<\u0015\u00035Dq!\u001a8\u0002\u0002\u0013\u0015c\rC\u0004{]\u0006\u0005I\u0011Q>\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0005\u0015b\b\"B\fz\u0001\u0004I\u0002b\u0002@o\u0003\u0003%\ti`\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t\t!a\u0002\u0011\t-\t\u0019!G\u0005\u0004\u0003\u000ba!AB(qi&|g\u000e\u0003\u0005\u0002\nu\f\t\u00111\u0001&\u0003\rAH\u0005\r\u0005\n\u0003\u001bq\u0017\u0011!C\u0005\u0003\u001f\t1B]3bIJ+7o\u001c7wKR\u0011\u0011\u0011\u0003\t\u0004{\u0005M\u0011bAA\u000b}\t1qJ\u00196fGR\u0004"
)
public class ExecutorRegistered implements Product, Serializable {
   private final String executorId;

   public static Option<String> unapply(ExecutorRegistered var0) {
      return ExecutorRegistered$.MODULE$.unapply(var0);
   }

   public static ExecutorRegistered apply(String var0) {
      return ExecutorRegistered$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<ExecutorRegistered, A> var0) {
      return ExecutorRegistered$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, ExecutorRegistered> compose(Function1<A, String> var0) {
      return ExecutorRegistered$.MODULE$.compose(var0);
   }

   public String executorId() {
      return this.executorId;
   }

   public ExecutorRegistered copy(String executorId) {
      return new ExecutorRegistered(executorId);
   }

   public String copy$default$1() {
      return this.executorId();
   }

   public String productPrefix() {
      return "ExecutorRegistered";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.executorId();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ExecutorRegistered;
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
            if (x$1 instanceof ExecutorRegistered) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     ExecutorRegistered var4 = (ExecutorRegistered)x$1;
                     String var10000 = this.executorId();
                     String var5 = var4.executorId();
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

   public ExecutorRegistered(String executorId) {
      this.executorId = executorId;
      Product$class.$init$(this);
   }
}
