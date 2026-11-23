package org.apache.spark.ui.storage;

import scala.Function1;
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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ed!B\u0001\u0003\u0001\u0012a!!\u0005\"m_\u000e\\G+\u00192mKJ{w\u000fR1uC*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0003\u0001\u001bM1\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u000f)%\u0011Qc\u0004\u0002\b!J|G-^2u!\tqq#\u0003\u0002\u0019\u001f\ta1+\u001a:jC2L'0\u00192mK\"A!\u0004\u0001BK\u0002\u0013\u0005A$A\u0005cY>\u001c7NT1nK\u000e\u0001Q#A\u000f\u0011\u0005y\tcB\u0001\b \u0013\t\u0001s\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003E\r\u0012aa\u0015;sS:<'B\u0001\u0011\u0010\u0011!)\u0003A!E!\u0002\u0013i\u0012A\u00032m_\u000e\\g*Y7fA!Aq\u0005\u0001BK\u0002\u0013\u0005A$\u0001\u0007ti>\u0014\u0018mZ3MKZ,G\u000e\u0003\u0005*\u0001\tE\t\u0015!\u0003\u001e\u00035\u0019Ho\u001c:bO\u0016dUM^3mA!A1\u0006\u0001BK\u0002\u0013\u0005A&\u0001\u0006nK6|'/_+tK\u0012,\u0012!\f\t\u0003\u001d9J!aL\b\u0003\t1{gn\u001a\u0005\tc\u0001\u0011\t\u0012)A\u0005[\u0005YQ.Z7pef,6/\u001a3!\u0011!\u0019\u0004A!f\u0001\n\u0003a\u0013\u0001\u00033jg.,6/\u001a3\t\u0011U\u0002!\u0011#Q\u0001\n5\n\u0011\u0002Z5tWV\u001bX\r\u001a\u0011\t\u0011]\u0002!Q3A\u0005\u0002q\t\u0011\"\u001a=fGV$xN]:\t\u0011e\u0002!\u0011#Q\u0001\nu\t!\"\u001a=fGV$xN]:!\u0011\u0015Y\u0004\u0001\"\u0001=\u0003\u0019a\u0014N\\5u}Q1Qh\u0010!B\u0005\u000e\u0003\"A\u0010\u0001\u000e\u0003\tAQA\u0007\u001eA\u0002uAQa\n\u001eA\u0002uAQa\u000b\u001eA\u00025BQa\r\u001eA\u00025BQa\u000e\u001eA\u0002uAq!\u0012\u0001\u0002\u0002\u0013\u0005a)\u0001\u0003d_BLHCB\u001fH\u0011&S5\nC\u0004\u001b\tB\u0005\t\u0019A\u000f\t\u000f\u001d\"\u0005\u0013!a\u0001;!91\u0006\u0012I\u0001\u0002\u0004i\u0003bB\u001aE!\u0003\u0005\r!\f\u0005\bo\u0011\u0003\n\u00111\u0001\u001e\u0011\u001di\u0005!%A\u0005\u00029\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001PU\ti\u0002kK\u0001R!\t\u0011v+D\u0001T\u0015\t!V+A\u0005v]\u000eDWmY6fI*\u0011akD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001-T\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b5\u0002\t\n\u0011\"\u0001O\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIIBq\u0001\u0018\u0001\u0012\u0002\u0013\u0005Q,\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0003yS#!\f)\t\u000f\u0001\u0004\u0011\u0013!C\u0001;\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\"\u0004b\u00022\u0001#\u0003%\tAT\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00136\u0011\u001d!\u0007!!A\u0005B\u0015\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u00014\u0011\u0005\u001ddW\"\u00015\u000b\u0005%T\u0017\u0001\u00027b]\u001eT\u0011a[\u0001\u0005U\u00064\u0018-\u0003\u0002#Q\"9a\u000eAA\u0001\n\u0003y\u0017\u0001\u00049s_\u0012,8\r^!sSRLX#\u00019\u0011\u00059\t\u0018B\u0001:\u0010\u0005\rIe\u000e\u001e\u0005\bi\u0002\t\t\u0011\"\u0001v\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"A^=\u0011\u000599\u0018B\u0001=\u0010\u0005\r\te.\u001f\u0005\buN\f\t\u00111\u0001q\u0003\rAH%\r\u0005\by\u0002\t\t\u0011\"\u0011~\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u0001@\u0011\t}\f)A^\u0007\u0003\u0003\u0003Q1!a\u0001\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u000f\t\tA\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\tY\u0001AA\u0001\n\u0003\ti!\u0001\u0005dC:,\u0015/^1m)\u0011\ty!!\u0006\u0011\u00079\t\t\"C\u0002\u0002\u0014=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005{\u0003\u0013\t\t\u00111\u0001w\u0011%\tI\u0002AA\u0001\n\u0003\nY\"\u0001\u0005iCND7i\u001c3f)\u0005\u0001\b\"CA\u0010\u0001\u0005\u0005I\u0011IA\u0011\u0003!!xn\u0015;sS:<G#\u00014\t\u0013\u0005\u0015\u0002!!A\u0005B\u0005\u001d\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002\u0010\u0005%\u0002\u0002\u0003>\u0002$\u0005\u0005\t\u0019\u0001<\b\u0015\u00055\"!!A\t\u0002\u0011\ty#A\tCY>\u001c7\u000eV1cY\u0016\u0014vn\u001e#bi\u0006\u00042APA\u0019\r%\t!!!A\t\u0002\u0011\t\u0019dE\u0003\u00022\u0005Ub\u0003\u0005\u0006\u00028\u0005uR$H\u0017.;uj!!!\u000f\u000b\u0007\u0005mr\"A\u0004sk:$\u0018.\\3\n\t\u0005}\u0012\u0011\b\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:,\u0004bB\u001e\u00022\u0011\u0005\u00111\t\u000b\u0003\u0003_A!\"a\b\u00022\u0005\u0005IQIA\u0011\u0011)\tI%!\r\u0002\u0002\u0013\u0005\u00151J\u0001\u0006CB\u0004H.\u001f\u000b\f{\u00055\u0013qJA)\u0003'\n)\u0006\u0003\u0004\u001b\u0003\u000f\u0002\r!\b\u0005\u0007O\u0005\u001d\u0003\u0019A\u000f\t\r-\n9\u00051\u0001.\u0011\u0019\u0019\u0014q\ta\u0001[!1q'a\u0012A\u0002uA!\"!\u0017\u00022\u0005\u0005I\u0011QA.\u0003\u001d)h.\u00199qYf$B!!\u0018\u0002jA)a\"a\u0018\u0002d%\u0019\u0011\u0011M\b\u0003\r=\u0003H/[8o!!q\u0011QM\u000f\u001e[5j\u0012bAA4\u001f\t1A+\u001e9mKVB\u0011\"a\u001b\u0002X\u0005\u0005\t\u0019A\u001f\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002p\u0005E\u0012\u0011!C\u0005\u0003c\n1B]3bIJ+7o\u001c7wKR\u0011\u00111\u000f\t\u0004O\u0006U\u0014bAA<Q\n1qJ\u00196fGR\u0004"
)
public class BlockTableRowData implements Product, Serializable {
   private final String blockName;
   private final String storageLevel;
   private final long memoryUsed;
   private final long diskUsed;
   private final String executors;

   public static Option<Tuple5<String, String, Object, Object, String>> unapply(BlockTableRowData var0) {
      return BlockTableRowData$.MODULE$.unapply(var0);
   }

   public static BlockTableRowData apply(String var0, String var1, long var2, long var4, String var6) {
      return BlockTableRowData$.MODULE$.apply(var0, var1, var2, var4, var6);
   }

   public static Function1<Tuple5<String, String, Object, Object, String>, BlockTableRowData> tupled() {
      return BlockTableRowData$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, Function1<Object, Function1<Object, Function1<String, BlockTableRowData>>>>> curried() {
      return BlockTableRowData$.MODULE$.curried();
   }

   public String blockName() {
      return this.blockName;
   }

   public String storageLevel() {
      return this.storageLevel;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public String executors() {
      return this.executors;
   }

   public BlockTableRowData copy(String blockName, String storageLevel, long memoryUsed, long diskUsed, String executors) {
      return new BlockTableRowData(blockName, storageLevel, memoryUsed, diskUsed, executors);
   }

   public String copy$default$1() {
      return this.blockName();
   }

   public String copy$default$2() {
      return this.storageLevel();
   }

   public long copy$default$3() {
      return this.memoryUsed();
   }

   public long copy$default$4() {
      return this.diskUsed();
   }

   public String copy$default$5() {
      return this.executors();
   }

   public String productPrefix() {
      return "BlockTableRowData";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.blockName();
            break;
         case 1:
            var10000 = this.storageLevel();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToLong(this.memoryUsed());
            break;
         case 3:
            var10000 = BoxesRunTime.boxToLong(this.diskUsed());
            break;
         case 4:
            var10000 = this.executors();
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
      return x$1 instanceof BlockTableRowData;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.blockName()));
      var1 = Statics.mix(var1, Statics.anyHash(this.storageLevel()));
      var1 = Statics.mix(var1, Statics.longHash(this.memoryUsed()));
      var1 = Statics.mix(var1, Statics.longHash(this.diskUsed()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executors()));
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
            if (x$1 instanceof BlockTableRowData) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  label66: {
                     BlockTableRowData var4 = (BlockTableRowData)x$1;
                     String var10000 = this.blockName();
                     String var5 = var4.blockName();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label66;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label66;
                     }

                     var10000 = this.storageLevel();
                     String var6 = var4.storageLevel();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label66;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label66;
                     }

                     if (this.memoryUsed() == var4.memoryUsed() && this.diskUsed() == var4.diskUsed()) {
                        label44: {
                           var10000 = this.executors();
                           String var7 = var4.executors();
                           if (var10000 == null) {
                              if (var7 != null) {
                                 break label44;
                              }
                           } else if (!var10000.equals(var7)) {
                              break label44;
                           }

                           if (var4.canEqual(this)) {
                              var11 = true;
                              break label57;
                           }
                        }
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

   public BlockTableRowData(String blockName, String storageLevel, long memoryUsed, long diskUsed, String executors) {
      this.blockName = blockName;
      this.storageLevel = storageLevel;
      this.memoryUsed = memoryUsed;
      this.diskUsed = diskUsed;
      this.executors = executors;
      Product$class.$init$(this);
   }
}
