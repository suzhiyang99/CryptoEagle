package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec\u0001B\u0001\u0003\u0001.\u00111B\u00117pG.\u001cF/\u0019;vg*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0005\u00011\u0011R\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001bMI!\u0001\u0006\b\u0003\u000fA\u0013x\u000eZ;diB\u0011QBF\u0005\u0003/9\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\u0007\u0001\u0003\u0016\u0004%\tAG\u0001\rgR|'/Y4f\u0019\u00164X\r\\\u000b\u00027A\u0011A$H\u0007\u0002\u0005%\u0011aD\u0001\u0002\r'R|'/Y4f\u0019\u00164X\r\u001c\u0005\tA\u0001\u0011\t\u0012)A\u00057\u0005i1\u000f^8sC\u001e,G*\u001a<fY\u0002B\u0001B\t\u0001\u0003\u0016\u0004%\taI\u0001\b[\u0016l7+\u001b>f+\u0005!\u0003CA\u0007&\u0013\t1cB\u0001\u0003M_:<\u0007\u0002\u0003\u0015\u0001\u0005#\u0005\u000b\u0011\u0002\u0013\u0002\u00115,WnU5{K\u0002B\u0001B\u000b\u0001\u0003\u0016\u0004%\taI\u0001\tI&\u001c8nU5{K\"AA\u0006\u0001B\tB\u0003%A%A\u0005eSN\\7+\u001b>fA!)a\u0006\u0001C\u0001_\u00051A(\u001b8jiz\"B\u0001M\u00193gA\u0011A\u0004\u0001\u0005\u000635\u0002\ra\u0007\u0005\u0006E5\u0002\r\u0001\n\u0005\u0006U5\u0002\r\u0001\n\u0005\u0006k\u0001!\tAN\u0001\tSN\u001c\u0015m\u00195fIV\tq\u0007\u0005\u0002\u000eq%\u0011\u0011H\u0004\u0002\b\u0005>|G.Z1o\u0011\u001dY\u0004!!A\u0005\u0002q\nAaY8qsR!\u0001'\u0010 @\u0011\u001dI\"\b%AA\u0002mAqA\t\u001e\u0011\u0002\u0003\u0007A\u0005C\u0004+uA\u0005\t\u0019\u0001\u0013\t\u000f\u0005\u0003\u0011\u0013!C\u0001\u0005\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\"+\u0005m!5&A#\u0011\u0005\u0019[U\"A$\u000b\u0005!K\u0015!C;oG\",7m[3e\u0015\tQe\"\u0001\u0006b]:|G/\u0019;j_:L!\u0001T$\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004O\u0001E\u0005I\u0011A(\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\t\u0001K\u000b\u0002%\t\"9!\u000bAI\u0001\n\u0003y\u0015AD2paf$C-\u001a4bk2$He\r\u0005\b)\u0002\t\t\u0011\"\u0011V\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\ta\u000b\u0005\u0002X96\t\u0001L\u0003\u0002Z5\u0006!A.\u00198h\u0015\u0005Y\u0016\u0001\u00026bm\u0006L!!\u0018-\u0003\rM#(/\u001b8h\u0011\u001dy\u0006!!A\u0005\u0002\u0001\fA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012!\u0019\t\u0003\u001b\tL!a\u0019\b\u0003\u0007%sG\u000fC\u0004f\u0001\u0005\u0005I\u0011\u00014\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011qM\u001b\t\u0003\u001b!L!!\u001b\b\u0003\u0007\u0005s\u0017\u0010C\u0004lI\u0006\u0005\t\u0019A1\u0002\u0007a$\u0013\u0007C\u0004n\u0001\u0005\u0005I\u0011\t8\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012a\u001c\t\u0004aN<W\"A9\u000b\u0005It\u0011AC2pY2,7\r^5p]&\u0011A/\u001d\u0002\t\u0013R,'/\u0019;pe\"9a\u000fAA\u0001\n\u00039\u0018\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005]B\bbB6v\u0003\u0003\u0005\ra\u001a\u0005\bu\u0002\t\t\u0011\"\u0011|\u0003!A\u0017m\u001d5D_\u0012,G#A1\t\u000fu\u0004\u0011\u0011!C!}\u0006AAo\\*ue&tw\rF\u0001W\u0011%\t\t\u0001AA\u0001\n\u0003\n\u0019!\u0001\u0004fcV\fGn\u001d\u000b\u0004o\u0005\u0015\u0001bB6\u0000\u0003\u0003\u0005\ra\u001a\u0015\u0004\u0001\u0005%\u0001\u0003BA\u0006\u0003\u001fi!!!\u0004\u000b\u0005)#\u0011\u0002BA\t\u0003\u001b\u0011A\u0002R3wK2|\u0007/\u001a:Ba&<q!!\u0006\u0003\u0011\u0003\t9\"A\u0006CY>\u001c7n\u0015;biV\u001c\bc\u0001\u000f\u0002\u001a\u00191\u0011A\u0001E\u0001\u00037\u0019B!!\u0007\r+!9a&!\u0007\u0005\u0002\u0005}ACAA\f\u0011!\t\u0019#!\u0007\u0005\u0002\u0005\u0015\u0012!B3naRLX#\u0001\u0019\t\u0015\u0005%\u0012\u0011DA\u0001\n\u0003\u000bY#A\u0003baBd\u0017\u0010F\u00041\u0003[\ty#!\r\t\re\t9\u00031\u0001\u001c\u0011\u0019\u0011\u0013q\u0005a\u0001I!1!&a\nA\u0002\u0011B!\"!\u000e\u0002\u001a\u0005\u0005I\u0011QA\u001c\u0003\u001d)h.\u00199qYf$B!!\u000f\u0002FA)Q\"a\u000f\u0002@%\u0019\u0011Q\b\b\u0003\r=\u0003H/[8o!\u0019i\u0011\u0011I\u000e%I%\u0019\u00111\t\b\u0003\rQ+\b\u000f\\34\u0011%\t9%a\r\u0002\u0002\u0003\u0007\u0001'A\u0002yIAB!\"a\u0013\u0002\u001a\u0005\u0005I\u0011BA'\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005=\u0003cA,\u0002R%\u0019\u00111\u000b-\u0003\r=\u0013'.Z2uQ\u0011\tI\"!\u0003)\t\u0005M\u0011\u0011\u0002"
)
public class BlockStatus implements Product, Serializable {
   private final StorageLevel storageLevel;
   private final long memSize;
   private final long diskSize;

   public static Option<Tuple3<StorageLevel, Object, Object>> unapply(BlockStatus var0) {
      return BlockStatus$.MODULE$.unapply(var0);
   }

   public static BlockStatus apply(StorageLevel var0, long var1, long var3) {
      return BlockStatus$.MODULE$.apply(var0, var1, var3);
   }

   public static BlockStatus empty() {
      return BlockStatus$.MODULE$.empty();
   }

   public StorageLevel storageLevel() {
      return this.storageLevel;
   }

   public long memSize() {
      return this.memSize;
   }

   public long diskSize() {
      return this.diskSize;
   }

   public boolean isCached() {
      return this.memSize() + this.diskSize() > 0L;
   }

   public BlockStatus copy(StorageLevel storageLevel, long memSize, long diskSize) {
      return new BlockStatus(storageLevel, memSize, diskSize);
   }

   public StorageLevel copy$default$1() {
      return this.storageLevel();
   }

   public long copy$default$2() {
      return this.memSize();
   }

   public long copy$default$3() {
      return this.diskSize();
   }

   public String productPrefix() {
      return "BlockStatus";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.storageLevel();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.memSize());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToLong(this.diskSize());
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
      return x$1 instanceof BlockStatus;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.storageLevel()));
      var1 = Statics.mix(var1, Statics.longHash(this.memSize()));
      var1 = Statics.mix(var1, Statics.longHash(this.diskSize()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof BlockStatus) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  label38: {
                     BlockStatus var4 = (BlockStatus)x$1;
                     StorageLevel var10000 = this.storageLevel();
                     StorageLevel var5 = var4.storageLevel();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label38;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label38;
                     }

                     if (this.memSize() == var4.memSize() && this.diskSize() == var4.diskSize() && var4.canEqual(this)) {
                        var7 = true;
                        break label39;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public BlockStatus(StorageLevel storageLevel, long memSize, long diskSize) {
      this.storageLevel = storageLevel;
      this.memSize = memSize;
      this.diskSize = diskSize;
      Product$class.$init$(this);
   }
}
