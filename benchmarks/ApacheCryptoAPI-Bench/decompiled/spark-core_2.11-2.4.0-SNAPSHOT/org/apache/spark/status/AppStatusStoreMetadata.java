package org.apache.spark.status;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ua!B\u0001\u0003\u0001\u0012Q!AF!qaN#\u0018\r^;t'R|'/Z'fi\u0006$\u0017\r^1\u000b\u0005\r!\u0011AB:uCR,8O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0011\u00011\"\u0005\u000b\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\ta!#\u0003\u0002\u0014\u001b\t9\u0001K]8ek\u000e$\bC\u0001\u0007\u0016\u0013\t1RB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0019\u0001\tU\r\u0011\"\u0001\u001b\u0003\u001d1XM]:j_:\u001c\u0001!F\u0001\u001c!\taA$\u0003\u0002\u001e\u001b\t!Aj\u001c8h\u0011!y\u0002A!E!\u0002\u0013Y\u0012\u0001\u0003<feNLwN\u001c\u0011\t\u000b\u0005\u0002A\u0011\u0001\u0012\u0002\rqJg.\u001b;?)\t\u0019S\u0005\u0005\u0002%\u00015\t!\u0001C\u0003\u0019A\u0001\u00071\u0004C\u0004(\u0001\u0005\u0005I\u0011\u0001\u0015\u0002\t\r|\u0007/\u001f\u000b\u0003G%Bq\u0001\u0007\u0014\u0011\u0002\u0003\u00071\u0004C\u0004,\u0001E\u0005I\u0011\u0001\u0017\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tQF\u000b\u0002\u001c]-\nq\u0006\u0005\u00021k5\t\u0011G\u0003\u00023g\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003i5\t!\"\u00198o_R\fG/[8o\u0013\t1\u0014GA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u000f\u0001\u0002\u0002\u0013\u0005\u0013(A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002uA\u00111\bQ\u0007\u0002y)\u0011QHP\u0001\u0005Y\u0006twMC\u0001@\u0003\u0011Q\u0017M^1\n\u0005\u0005c$AB*ue&tw\rC\u0004D\u0001\u0005\u0005I\u0011\u0001#\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u0015\u0003\"\u0001\u0004$\n\u0005\u001dk!aA%oi\"9\u0011\nAA\u0001\n\u0003Q\u0015A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003\u0017:\u0003\"\u0001\u0004'\n\u00055k!aA!os\"9q\nSA\u0001\u0002\u0004)\u0015a\u0001=%c!9\u0011\u000bAA\u0001\n\u0003\u0012\u0016a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003M\u00032\u0001V,L\u001b\u0005)&B\u0001,\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u00031V\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b5\u0002\t\t\u0011\"\u0001\\\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001/`!\taQ,\u0003\u0002_\u001b\t9!i\\8mK\u0006t\u0007bB(Z\u0003\u0003\u0005\ra\u0013\u0005\bC\u0002\t\t\u0011\"\u0011c\u0003!A\u0017m\u001d5D_\u0012,G#A#\t\u000f\u0011\u0004\u0011\u0011!C!K\u0006AAo\\*ue&tw\rF\u0001;\u0011\u001d9\u0007!!A\u0005B!\fa!Z9vC2\u001cHC\u0001/j\u0011\u001dye-!AA\u0002-;\u0001b\u001b\u0002\u0002\u0002#\u0005A\u0001\\\u0001\u0017\u0003B\u00048\u000b^1ukN\u001cFo\u001c:f\u001b\u0016$\u0018\rZ1uCB\u0011A%\u001c\u0004\t\u0003\t\t\t\u0011#\u0001\u0005]N\u0019Qn\u001c\u000b\u0011\tA\u001c8dI\u0007\u0002c*\u0011!/D\u0001\beVtG/[7f\u0013\t!\u0018OA\tBEN$(/Y2u\rVt7\r^5p]FBQ!I7\u0005\u0002Y$\u0012\u0001\u001c\u0005\bI6\f\t\u0011\"\u0012f\u0011\u001dIX.!A\u0005\u0002j\fQ!\u00199qYf$\"aI>\t\u000baA\b\u0019A\u000e\t\u000ful\u0017\u0011!CA}\u00069QO\\1qa2LHcA@\u0002\u0006A!A\"!\u0001\u001c\u0013\r\t\u0019!\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0011\u0005\u001dA0!AA\u0002\r\n1\u0001\u001f\u00131\u0011%\tY!\\A\u0001\n\u0013\ti!A\u0006sK\u0006$'+Z:pYZ,GCAA\b!\rY\u0014\u0011C\u0005\u0004\u0003'a$AB(cU\u0016\u001cG\u000f"
)
public class AppStatusStoreMetadata implements Product, Serializable {
   private final long version;

   public static Option<Object> unapply(AppStatusStoreMetadata var0) {
      return AppStatusStoreMetadata$.MODULE$.unapply(var0);
   }

   public static AppStatusStoreMetadata apply(long var0) {
      return AppStatusStoreMetadata$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<AppStatusStoreMetadata, A> var0) {
      return AppStatusStoreMetadata$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, AppStatusStoreMetadata> compose(Function1<A, Object> var0) {
      return AppStatusStoreMetadata$.MODULE$.compose(var0);
   }

   public long version() {
      return this.version;
   }

   public AppStatusStoreMetadata copy(long version) {
      return new AppStatusStoreMetadata(version);
   }

   public long copy$default$1() {
      return this.version();
   }

   public String productPrefix() {
      return "AppStatusStoreMetadata";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToLong(this.version());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof AppStatusStoreMetadata;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.version()));
      return Statics.finalizeHash(var1, 1);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof AppStatusStoreMetadata) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               AppStatusStoreMetadata var4 = (AppStatusStoreMetadata)x$1;
               if (this.version() == var4.version() && var4.canEqual(this)) {
                  break label48;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public AppStatusStoreMetadata(long version) {
      this.version = version;
      Product$class.$init$(this);
   }
}
