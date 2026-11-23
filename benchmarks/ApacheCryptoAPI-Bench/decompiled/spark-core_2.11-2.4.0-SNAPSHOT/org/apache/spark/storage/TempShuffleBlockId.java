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
   bytes = "\u0006\u0001\u0005-b!B\u0001\u0003\u0001\u0012Q!A\u0005+f[B\u001c\u0006.\u001e4gY\u0016\u0014En\\2l\u0013\u0012T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017=)\u0002C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005\u001d\u0011En\\2l\u0013\u0012\u0004\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0011-%\u0011q#\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00017\u0005\u0011\u0011\u000eZ\u0002\u0001+\u0005a\u0002CA\u000f#\u001b\u0005q\"BA\u0010!\u0003\u0011)H/\u001b7\u000b\u0003\u0005\nAA[1wC&\u00111E\b\u0002\u0005+VKE\t\u0003\u0005&\u0001\tE\t\u0015!\u0003\u001d\u0003\rIG\r\t\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005%R\u0003C\u0001\u0007\u0001\u0011\u0015Ib\u00051\u0001\u001d\u0011\u0015a\u0003\u0001\"\u0011.\u0003\u0011q\u0017-\\3\u0016\u00039\u0002\"a\f\u001a\u000f\u0005A\u0001\u0014BA\u0019\u0012\u0003\u0019\u0001&/\u001a3fM&\u00111\u0007\u000e\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005E\n\u0002b\u0002\u001c\u0001\u0003\u0003%\taN\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002*q!9\u0011$\u000eI\u0001\u0002\u0004a\u0002b\u0002\u001e\u0001#\u0003%\taO\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005a$F\u0001\u000f>W\u0005q\u0004CA E\u001b\u0005\u0001%BA!C\u0003%)hn\u00195fG.,GM\u0003\u0002D#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u0015\u0003%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\tAA\u0001\n\u0003B\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001J!\tQU*D\u0001L\u0015\ta\u0005%\u0001\u0003mC:<\u0017BA\u001aL\u0011\u001dy\u0005!!A\u0005\u0002A\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012!\u0015\t\u0003!IK!aU\t\u0003\u0007%sG\u000fC\u0004V\u0001\u0005\u0005I\u0011\u0001,\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011qK\u0017\t\u0003!aK!!W\t\u0003\u0007\u0005s\u0017\u0010C\u0004\\)\u0006\u0005\t\u0019A)\u0002\u0007a$\u0013\u0007C\u0004^\u0001\u0005\u0005I\u0011\t0\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012a\u0018\t\u0004A\u000e<V\"A1\u000b\u0005\t\f\u0012AC2pY2,7\r^5p]&\u0011A-\u0019\u0002\t\u0013R,'/\u0019;pe\"9a\rAA\u0001\n\u00039\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005!\\\u0007C\u0001\tj\u0013\tQ\u0017CA\u0004C_>dW-\u00198\t\u000fm+\u0017\u0011!a\u0001/\"9Q\u000eAA\u0001\n\u0003r\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003ECq\u0001\u001d\u0001\u0002\u0002\u0013\u0005\u0013/\u0001\u0004fcV\fGn\u001d\u000b\u0003QJDqaW8\u0002\u0002\u0003\u0007qk\u0002\u0005u\u0005\u0005\u0005\t\u0012\u0001\u0003v\u0003I!V-\u001c9TQV4g\r\\3CY>\u001c7.\u00133\u0011\u000511h\u0001C\u0001\u0003\u0003\u0003E\t\u0001B<\u0014\u0007YDX\u0003\u0005\u0003zyrIS\"\u0001>\u000b\u0005m\f\u0012a\u0002:v]RLW.Z\u0005\u0003{j\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u00159c\u000f\"\u0001\u0000)\u0005)\b\"CA\u0002m\u0006\u0005IQIA\u0003\u0003!!xn\u0015;sS:<G#A%\t\u0013\u0005%a/!A\u0005\u0002\u0006-\u0011!B1qa2LHcA\u0015\u0002\u000e!1\u0011$a\u0002A\u0002qA\u0011\"!\u0005w\u0003\u0003%\t)a\u0005\u0002\u000fUt\u0017\r\u001d9msR!\u0011QCA\u000e!\u0011\u0001\u0012q\u0003\u000f\n\u0007\u0005e\u0011C\u0001\u0004PaRLwN\u001c\u0005\n\u0003;\ty!!AA\u0002%\n1\u0001\u001f\u00131\u0011%\t\tC^A\u0001\n\u0013\t\u0019#A\u0006sK\u0006$'+Z:pYZ,GCAA\u0013!\rQ\u0015qE\u0005\u0004\u0003SY%AB(cU\u0016\u001cG\u000f"
)
public class TempShuffleBlockId extends BlockId implements Product, Serializable {
   private final UUID id;

   public static Option<UUID> unapply(TempShuffleBlockId var0) {
      return TempShuffleBlockId$.MODULE$.unapply(var0);
   }

   public static TempShuffleBlockId apply(UUID var0) {
      return TempShuffleBlockId$.MODULE$.apply(var0);
   }

   public static <A> Function1<UUID, A> andThen(Function1<TempShuffleBlockId, A> var0) {
      return TempShuffleBlockId$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, TempShuffleBlockId> compose(Function1<A, UUID> var0) {
      return TempShuffleBlockId$.MODULE$.compose(var0);
   }

   public UUID id() {
      return this.id;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"temp_shuffle_").append((Object)this.id()).toString();
   }

   public TempShuffleBlockId copy(UUID id) {
      return new TempShuffleBlockId(id);
   }

   public UUID copy$default$1() {
      return this.id();
   }

   public String productPrefix() {
      return "TempShuffleBlockId";
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
      return x$1 instanceof TempShuffleBlockId;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof TempShuffleBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     TempShuffleBlockId var4 = (TempShuffleBlockId)x$1;
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

   public TempShuffleBlockId(UUID id) {
      this.id = id;
      Product$class.$init$(this);
   }
}
