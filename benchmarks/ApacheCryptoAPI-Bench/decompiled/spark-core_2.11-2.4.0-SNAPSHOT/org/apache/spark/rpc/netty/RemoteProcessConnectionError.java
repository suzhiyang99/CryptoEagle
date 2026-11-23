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
   bytes = "\u0006\u0001\u0005]c!B\u0001\u0003\u0001\na!\u0001\b*f[>$X\r\u0015:pG\u0016\u001c8oQ8o]\u0016\u001cG/[8o\u000bJ\u0014xN\u001d\u0006\u0003\u0007\u0011\tQA\\3uifT!!\u0002\u0004\u0002\u0007I\u00048M\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\u0015\u0001QbE\f\u001b!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011A#F\u0007\u0002\u0005%\u0011aC\u0001\u0002\r\u0013:\u0014w\u000e_'fgN\fw-\u001a\t\u0003\u001daI!!G\b\u0003\u000fA\u0013x\u000eZ;diB\u0011abG\u0005\u00039=\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\b\u0001\u0003\u0016\u0004%\t\u0001I\u0001\u0006G\u0006,8/Z\u0002\u0001+\u0005\t\u0003C\u0001\u0012+\u001d\t\u0019\u0003F\u0004\u0002%O5\tQE\u0003\u0002'?\u00051AH]8pizJ\u0011\u0001E\u0005\u0003S=\tq\u0001]1dW\u0006<W-\u0003\u0002,Y\tIA\u000b\u001b:po\u0006\u0014G.\u001a\u0006\u0003S=A\u0001B\f\u0001\u0003\u0012\u0003\u0006I!I\u0001\u0007G\u0006,8/\u001a\u0011\t\u0011A\u0002!Q3A\u0005\u0002E\nQB]3n_R,\u0017\t\u001a3sKN\u001cX#\u0001\u001a\u0011\u0005M\"T\"\u0001\u0003\n\u0005U\"!A\u0003*qG\u0006#GM]3tg\"Aq\u0007\u0001B\tB\u0003%!'\u0001\bsK6|G/Z!eIJ,7o\u001d\u0011\t\u000be\u0002A\u0011\u0001\u001e\u0002\rqJg.\u001b;?)\rYD(\u0010\t\u0003)\u0001AQA\b\u001dA\u0002\u0005BQ\u0001\r\u001dA\u0002IBqa\u0010\u0001\u0002\u0002\u0013\u0005\u0001)\u0001\u0003d_BLHcA\u001eB\u0005\"9aD\u0010I\u0001\u0002\u0004\t\u0003b\u0002\u0019?!\u0003\u0005\rA\r\u0005\b\t\u0002\t\n\u0011\"\u0001F\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012A\u0012\u0016\u0003C\u001d[\u0013\u0001\u0013\t\u0003\u0013:k\u0011A\u0013\u0006\u0003\u00172\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u00055{\u0011AC1o]>$\u0018\r^5p]&\u0011qJ\u0013\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB)\u0001#\u0003%\tAU\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005\u0019&F\u0001\u001aH\u0011\u001d)\u0006!!A\u0005BY\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A,\u0011\u0005akV\"A-\u000b\u0005i[\u0016\u0001\u00027b]\u001eT\u0011\u0001X\u0001\u0005U\u00064\u0018-\u0003\u0002_3\n11\u000b\u001e:j]\u001eDq\u0001\u0019\u0001\u0002\u0002\u0013\u0005\u0011-\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001c!\tq1-\u0003\u0002e\u001f\t\u0019\u0011J\u001c;\t\u000f\u0019\u0004\u0011\u0011!C\u0001O\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u00015l!\tq\u0011.\u0003\u0002k\u001f\t\u0019\u0011I\\=\t\u000f1,\u0017\u0011!a\u0001E\u0006\u0019\u0001\u0010J\u0019\t\u000f9\u0004\u0011\u0011!C!_\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001q!\r\tH\u000f[\u0007\u0002e*\u00111oD\u0001\u000bG>dG.Z2uS>t\u0017BA;s\u0005!IE/\u001a:bi>\u0014\bbB<\u0001\u0003\u0003%\t\u0001_\u0001\tG\u0006tW)];bYR\u0011\u0011\u0010 \t\u0003\u001diL!a_\b\u0003\u000f\t{w\u000e\\3b]\"9AN^A\u0001\u0002\u0004A\u0007b\u0002@\u0001\u0003\u0003%\te`\u0001\tQ\u0006\u001c\bnQ8eKR\t!\rC\u0005\u0002\u0004\u0001\t\t\u0011\"\u0011\u0002\u0006\u0005AAo\\*ue&tw\rF\u0001X\u0011%\tI\u0001AA\u0001\n\u0003\nY!\u0001\u0004fcV\fGn\u001d\u000b\u0004s\u00065\u0001\u0002\u00037\u0002\b\u0005\u0005\t\u0019\u00015\b\u0015\u0005E!!!A\t\u0002\t\t\u0019\"\u0001\u000fSK6|G/\u001a)s_\u000e,7o]\"p]:,7\r^5p]\u0016\u0013(o\u001c:\u0011\u0007Q\t)BB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002\u0002\u0018M)\u0011QCA\r5A9\u00111DA\u0011CIZTBAA\u000f\u0015\r\tybD\u0001\beVtG/[7f\u0013\u0011\t\u0019#!\b\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t'\u0007C\u0004:\u0003+!\t!a\n\u0015\u0005\u0005M\u0001BCA\u0002\u0003+\t\t\u0011\"\u0012\u0002\u0006!Q\u0011QFA\u000b\u0003\u0003%\t)a\f\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000bm\n\t$a\r\t\ry\tY\u00031\u0001\"\u0011\u0019\u0001\u00141\u0006a\u0001e!Q\u0011qGA\u000b\u0003\u0003%\t)!\u000f\u0002\u000fUt\u0017\r\u001d9msR!\u00111HA$!\u0015q\u0011QHA!\u0013\r\tyd\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u000b9\t\u0019%\t\u001a\n\u0007\u0005\u0015sB\u0001\u0004UkBdWM\r\u0005\n\u0003\u0013\n)$!AA\u0002m\n1\u0001\u001f\u00131\u0011)\ti%!\u0006\u0002\u0002\u0013%\u0011qJ\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002RA\u0019\u0001,a\u0015\n\u0007\u0005U\u0013L\u0001\u0004PE*,7\r\u001e"
)
public class RemoteProcessConnectionError implements InboxMessage, Product, Serializable {
   private final Throwable cause;
   private final RpcAddress remoteAddress;

   public static Option<Tuple2<Throwable, RpcAddress>> unapply(RemoteProcessConnectionError var0) {
      return RemoteProcessConnectionError$.MODULE$.unapply(var0);
   }

   public static RemoteProcessConnectionError apply(Throwable var0, RpcAddress var1) {
      return RemoteProcessConnectionError$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Throwable, RpcAddress>, RemoteProcessConnectionError> tupled() {
      return RemoteProcessConnectionError$.MODULE$.tupled();
   }

   public static Function1<Throwable, Function1<RpcAddress, RemoteProcessConnectionError>> curried() {
      return RemoteProcessConnectionError$.MODULE$.curried();
   }

   public Throwable cause() {
      return this.cause;
   }

   public RpcAddress remoteAddress() {
      return this.remoteAddress;
   }

   public RemoteProcessConnectionError copy(Throwable cause, RpcAddress remoteAddress) {
      return new RemoteProcessConnectionError(cause, remoteAddress);
   }

   public Throwable copy$default$1() {
      return this.cause();
   }

   public RpcAddress copy$default$2() {
      return this.remoteAddress();
   }

   public String productPrefix() {
      return "RemoteProcessConnectionError";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.cause();
            break;
         case 1:
            var10000 = this.remoteAddress();
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
      return x$1 instanceof RemoteProcessConnectionError;
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
         label62: {
            boolean var3;
            if (x$1 instanceof RemoteProcessConnectionError) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     RemoteProcessConnectionError var4 = (RemoteProcessConnectionError)x$1;
                     Throwable var10000 = this.cause();
                     Throwable var5 = var4.cause();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     RpcAddress var7 = this.remoteAddress();
                     RpcAddress var6 = var4.remoteAddress();
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

   public RemoteProcessConnectionError(Throwable cause, RpcAddress remoteAddress) {
      this.cause = cause;
      this.remoteAddress = remoteAddress;
      Product$class.$init$(this);
   }
}
