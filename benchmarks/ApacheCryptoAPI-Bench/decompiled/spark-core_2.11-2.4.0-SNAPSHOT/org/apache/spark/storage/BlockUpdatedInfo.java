package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ef\u0001B\u0001\u0003\u0001.\u0011\u0001C\u00117pG.,\u0006\u000fZ1uK\u0012LeNZ8\u000b\u0005\r!\u0011aB:u_J\fw-\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\r%U\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\u0007\u0014\u0013\t!bBA\u0004Qe>$Wo\u0019;\u0011\u000551\u0012BA\f\u000f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0002A!f\u0001\n\u0003Q\u0012A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\nZ\u000b\u00027A\u0011A$H\u0007\u0002\u0005%\u0011aD\u0001\u0002\u000f\u00052|7m['b]\u0006<WM]%e\u0011!\u0001\u0003A!E!\u0002\u0013Y\u0012a\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\u0011\t\u0011\t\u0002!Q3A\u0005\u0002\r\nqA\u00197pG.LE-F\u0001%!\taR%\u0003\u0002'\u0005\t9!\t\\8dW&#\u0007\u0002\u0003\u0015\u0001\u0005#\u0005\u000b\u0011\u0002\u0013\u0002\u0011\tdwnY6JI\u0002B\u0001B\u000b\u0001\u0003\u0016\u0004%\taK\u0001\rgR|'/Y4f\u0019\u00164X\r\\\u000b\u0002YA\u0011A$L\u0005\u0003]\t\u0011Ab\u0015;pe\u0006<W\rT3wK2D\u0001\u0002\r\u0001\u0003\u0012\u0003\u0006I\u0001L\u0001\u000egR|'/Y4f\u0019\u00164X\r\u001c\u0011\t\u0011I\u0002!Q3A\u0005\u0002M\nq!\\3n'&TX-F\u00015!\tiQ'\u0003\u00027\u001d\t!Aj\u001c8h\u0011!A\u0004A!E!\u0002\u0013!\u0014\u0001C7f[NK'0\u001a\u0011\t\u0011i\u0002!Q3A\u0005\u0002M\n\u0001\u0002Z5tWNK'0\u001a\u0005\ty\u0001\u0011\t\u0012)A\u0005i\u0005IA-[:l'&TX\r\t\u0005\u0006}\u0001!\taP\u0001\u0007y%t\u0017\u000e\u001e \u0015\r\u0001\u000b%i\u0011#F!\ta\u0002\u0001C\u0003\u001a{\u0001\u00071\u0004C\u0003#{\u0001\u0007A\u0005C\u0003+{\u0001\u0007A\u0006C\u00033{\u0001\u0007A\u0007C\u0003;{\u0001\u0007A\u0007C\u0004H\u0001\u0005\u0005I\u0011\u0001%\u0002\t\r|\u0007/\u001f\u000b\u0007\u0001&S5\nT'\t\u000fe1\u0005\u0013!a\u00017!9!E\u0012I\u0001\u0002\u0004!\u0003b\u0002\u0016G!\u0003\u0005\r\u0001\f\u0005\be\u0019\u0003\n\u00111\u00015\u0011\u001dQd\t%AA\u0002QBqa\u0014\u0001\u0012\u0002\u0013\u0005\u0001+\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003ES#a\u0007*,\u0003M\u0003\"\u0001V-\u000e\u0003US!AV,\u0002\u0013Ut7\r[3dW\u0016$'B\u0001-\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u00035V\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001da\u0006!%A\u0005\u0002u\u000babY8qs\u0012\"WMZ1vYR$#'F\u0001_U\t!#\u000bC\u0004a\u0001E\u0005I\u0011A1\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%gU\t!M\u000b\u0002-%\"9A\rAI\u0001\n\u0003)\u0017AD2paf$C-\u001a4bk2$H\u0005N\u000b\u0002M*\u0012AG\u0015\u0005\bQ\u0002\t\n\u0011\"\u0001f\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIUBqA\u001b\u0001\u0002\u0002\u0013\u00053.A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002YB\u0011QN]\u0007\u0002]*\u0011q\u000e]\u0001\u0005Y\u0006twMC\u0001r\u0003\u0011Q\u0017M^1\n\u0005Mt'AB*ue&tw\rC\u0004v\u0001\u0005\u0005I\u0011\u0001<\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003]\u0004\"!\u0004=\n\u0005et!aA%oi\"91\u0010AA\u0001\n\u0003a\u0018A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004{\u0006\u0005\u0001CA\u0007\u007f\u0013\tyhBA\u0002B]fD\u0001\"a\u0001{\u0003\u0003\u0005\ra^\u0001\u0004q\u0012\n\u0004\"CA\u0004\u0001\u0005\u0005I\u0011IA\u0005\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA\u0006!\u0015\ti!a\u0005~\u001b\t\tyAC\u0002\u0002\u00129\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t)\"a\u0004\u0003\u0011%#XM]1u_JD\u0011\"!\u0007\u0001\u0003\u0003%\t!a\u0007\u0002\u0011\r\fg.R9vC2$B!!\b\u0002$A\u0019Q\"a\b\n\u0007\u0005\u0005bBA\u0004C_>dW-\u00198\t\u0013\u0005\r\u0011qCA\u0001\u0002\u0004i\b\"CA\u0014\u0001\u0005\u0005I\u0011IA\u0015\u0003!A\u0017m\u001d5D_\u0012,G#A<\t\u0013\u00055\u0002!!A\u0005B\u0005=\u0012\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u00031D\u0011\"a\r\u0001\u0003\u0003%\t%!\u000e\u0002\r\u0015\fX/\u00197t)\u0011\ti\"a\u000e\t\u0013\u0005\r\u0011\u0011GA\u0001\u0002\u0004i\bf\u0001\u0001\u0002<A!\u0011QHA!\u001b\t\tyD\u0003\u0002Y\t%!\u00111IA \u00051!UM^3m_B,'/\u00119j\u000f!\t9E\u0001E\u0001\t\u0005%\u0013\u0001\u0005\"m_\u000e\\W\u000b\u001d3bi\u0016$\u0017J\u001c4p!\ra\u00121\n\u0004\b\u0003\tA\t\u0001BA''\u0011\tY\u0005D\u000b\t\u000fy\nY\u0005\"\u0001\u0002RQ\u0011\u0011\u0011\n\u0005\n\u0003+\nY\u0005\"\u0001\u0005\u0003/\nQ!\u00199qYf$2\u0001QA-\u0011!\tY&a\u0015A\u0002\u0005u\u0013aD;qI\u0006$XM\u00117pG.LeNZ8\u0011\t\u0005}\u00131\u0010\b\u0005\u0003C\n9H\u0004\u0003\u0002d\u0005Ud\u0002BA3\u0003grA!a\u001a\u0002r9!\u0011\u0011NA8\u001b\t\tYGC\u0002\u0002n)\ta\u0001\u0010:p_Rt\u0014\"A\u0005\n\u0005\u001dA\u0011BA\u0003\u0007\u0013\t\u0019A!C\u0002\u0002z\t\tAC\u00117pG.l\u0015M\\1hKJlUm]:bO\u0016\u001c\u0018\u0002BA?\u0003\u007f\u0012q\"\u00169eCR,'\t\\8dW&sgm\u001c\u0006\u0004\u0003s\u0012\u0001BCA+\u0003\u0017\n\t\u0011\"!\u0002\u0004RY\u0001)!\"\u0002\b\u0006%\u00151RAG\u0011\u0019I\u0012\u0011\u0011a\u00017!1!%!!A\u0002\u0011BaAKAA\u0001\u0004a\u0003B\u0002\u001a\u0002\u0002\u0002\u0007A\u0007\u0003\u0004;\u0003\u0003\u0003\r\u0001\u000e\u0005\u000b\u0003#\u000bY%!A\u0005\u0002\u0006M\u0015aB;oCB\u0004H.\u001f\u000b\u0005\u0003+\u000b\t\u000bE\u0003\u000e\u0003/\u000bY*C\u0002\u0002\u001a:\u0011aa\u00149uS>t\u0007\u0003C\u0007\u0002\u001en!C\u0006\u000e\u001b\n\u0007\u0005}eB\u0001\u0004UkBdW-\u000e\u0005\n\u0003G\u000by)!AA\u0002\u0001\u000b1\u0001\u001f\u00131\u0011)\t9+a\u0013\u0002\u0002\u0013%\u0011\u0011V\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002,B\u0019Q.!,\n\u0007\u0005=fN\u0001\u0004PE*,7\r\u001e"
)
public class BlockUpdatedInfo implements Product, Serializable {
   private final BlockManagerId blockManagerId;
   private final BlockId blockId;
   private final StorageLevel storageLevel;
   private final long memSize;
   private final long diskSize;

   public static Option<Tuple5<BlockManagerId, BlockId, StorageLevel, Object, Object>> unapply(BlockUpdatedInfo var0) {
      return BlockUpdatedInfo$.MODULE$.unapply(var0);
   }

   public static BlockUpdatedInfo apply(BlockManagerId var0, BlockId var1, StorageLevel var2, long var3, long var5) {
      return BlockUpdatedInfo$.MODULE$.apply(var0, var1, var2, var3, var5);
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public BlockId blockId() {
      return this.blockId;
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

   public BlockUpdatedInfo copy(BlockManagerId blockManagerId, BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      return new BlockUpdatedInfo(blockManagerId, blockId, storageLevel, memSize, diskSize);
   }

   public BlockManagerId copy$default$1() {
      return this.blockManagerId();
   }

   public BlockId copy$default$2() {
      return this.blockId();
   }

   public StorageLevel copy$default$3() {
      return this.storageLevel();
   }

   public long copy$default$4() {
      return this.memSize();
   }

   public long copy$default$5() {
      return this.diskSize();
   }

   public String productPrefix() {
      return "BlockUpdatedInfo";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.blockManagerId();
            break;
         case 1:
            var10000 = this.blockId();
            break;
         case 2:
            var10000 = this.storageLevel();
            break;
         case 3:
            var10000 = BoxesRunTime.boxToLong(this.memSize());
            break;
         case 4:
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
      return x$1 instanceof BlockUpdatedInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.blockManagerId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.blockId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.storageLevel()));
      var1 = Statics.mix(var1, Statics.longHash(this.memSize()));
      var1 = Statics.mix(var1, Statics.longHash(this.diskSize()));
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label75: {
            boolean var3;
            if (x$1 instanceof BlockUpdatedInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  label66: {
                     BlockUpdatedInfo var4 = (BlockUpdatedInfo)x$1;
                     BlockManagerId var10000 = this.blockManagerId();
                     BlockManagerId var5 = var4.blockManagerId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label66;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label66;
                     }

                     BlockId var8 = this.blockId();
                     BlockId var6 = var4.blockId();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label66;
                        }
                     } else if (!var8.equals(var6)) {
                        break label66;
                     }

                     StorageLevel var9 = this.storageLevel();
                     StorageLevel var7 = var4.storageLevel();
                     if (var9 == null) {
                        if (var7 != null) {
                           break label66;
                        }
                     } else if (!var9.equals(var7)) {
                        break label66;
                     }

                     if (this.memSize() == var4.memSize() && this.diskSize() == var4.diskSize() && var4.canEqual(this)) {
                        var11 = true;
                        break label57;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label75;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public BlockUpdatedInfo(BlockManagerId blockManagerId, BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      this.blockManagerId = blockManagerId;
      this.blockId = blockId;
      this.storageLevel = storageLevel;
      this.memSize = memSize;
      this.diskSize = diskSize;
      Product$class.$init$(this);
   }
}
