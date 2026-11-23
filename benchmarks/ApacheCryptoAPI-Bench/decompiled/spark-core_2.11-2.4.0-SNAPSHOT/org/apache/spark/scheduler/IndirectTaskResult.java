package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockId;
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
   bytes = "\u0006\u0001\u00055d!B\u0001\u0003\u0001\u0012Q!AE%oI&\u0014Xm\u0019;UCN\\'+Z:vYRT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-A2C\u0002\u0001\r%\tRS\u0006\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0004'Q1R\"\u0001\u0002\n\u0005U\u0011!A\u0003+bg.\u0014Vm];miB\u0011q\u0003\u0007\u0007\u0001\t\u0015I\u0002A1\u0001\u001c\u0005\u0005!6\u0001A\t\u00039}\u0001\"!D\u000f\n\u0005yq!a\u0002(pi\"Lgn\u001a\t\u0003\u001b\u0001J!!\t\b\u0003\u0007\u0005s\u0017\u0010\u0005\u0002$Q5\tAE\u0003\u0002&M\u0005\u0011\u0011n\u001c\u0006\u0002O\u0005!!.\u0019<b\u0013\tICE\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0005\u0002\u000eW%\u0011AF\u0004\u0002\b!J|G-^2u!\tia&\u0003\u0002*\u001d!A\u0001\u0007\u0001BK\u0002\u0013\u0005\u0011'A\u0004cY>\u001c7.\u00133\u0016\u0003I\u0002\"a\r\u001c\u000e\u0003QR!!\u000e\u0003\u0002\u000fM$xN]1hK&\u0011q\u0007\u000e\u0002\b\u00052|7m[%e\u0011!I\u0004A!E!\u0002\u0013\u0011\u0014\u0001\u00032m_\u000e\\\u0017\n\u001a\u0011\t\u0011m\u0002!Q3A\u0005\u0002q\nAa]5{KV\tQ\b\u0005\u0002\u000e}%\u0011qH\u0004\u0002\u0004\u0013:$\b\u0002C!\u0001\u0005#\u0005\u000b\u0011B\u001f\u0002\u000bML'0\u001a\u0011\t\u000b\r\u0003A\u0011\u0001#\u0002\rqJg.\u001b;?)\r)ei\u0012\t\u0004'\u00011\u0002\"\u0002\u0019C\u0001\u0004\u0011\u0004\"B\u001eC\u0001\u0004i\u0004bB%\u0001\u0003\u0003%\tAS\u0001\u0005G>\u0004\u00180\u0006\u0002L\u001dR\u0019Aj\u0014)\u0011\u0007M\u0001Q\n\u0005\u0002\u0018\u001d\u0012)\u0011\u0004\u0013b\u00017!9\u0001\u0007\u0013I\u0001\u0002\u0004\u0011\u0004bB\u001eI!\u0003\u0005\r!\u0010\u0005\b%\u0002\t\n\u0011\"\u0001T\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\"\u0001V0\u0016\u0003US#A\r,,\u0003]\u0003\"\u0001W/\u000e\u0003eS!AW.\u0002\u0013Ut7\r[3dW\u0016$'B\u0001/\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u0003=f\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u0015I\u0012K1\u0001\u001c\u0011\u001d\t\u0007!%A\u0005\u0002\t\fabY8qs\u0012\"WMZ1vYR$#'\u0006\u0002dKV\tAM\u000b\u0002>-\u0012)\u0011\u0004\u0019b\u00017!9q\rAA\u0001\n\u0003B\u0017!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001j!\tQW.D\u0001l\u0015\tag%\u0001\u0003mC:<\u0017B\u00018l\u0005\u0019\u0019FO]5oO\"9\u0001\u000fAA\u0001\n\u0003a\u0014\u0001\u00049s_\u0012,8\r^!sSRL\bb\u0002:\u0001\u0003\u0003%\ta]\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tyB\u000fC\u0004vc\u0006\u0005\t\u0019A\u001f\u0002\u0007a$\u0013\u0007C\u0004x\u0001\u0005\u0005I\u0011\t=\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012!\u001f\t\u0004uv|R\"A>\u000b\u0005qt\u0011AC2pY2,7\r^5p]&\u0011ap\u001f\u0002\t\u0013R,'/\u0019;pe\"I\u0011\u0011\u0001\u0001\u0002\u0002\u0013\u0005\u00111A\u0001\tG\u0006tW)];bYR!\u0011QAA\u0006!\ri\u0011qA\u0005\u0004\u0003\u0013q!a\u0002\"p_2,\u0017M\u001c\u0005\bk~\f\t\u00111\u0001 \u0011%\ty\u0001AA\u0001\n\u0003\n\t\"\u0001\u0005iCND7i\u001c3f)\u0005i\u0004\"CA\u000b\u0001\u0005\u0005I\u0011IA\f\u0003!!xn\u0015;sS:<G#A5\t\u0013\u0005m\u0001!!A\u0005B\u0005u\u0011AB3rk\u0006d7\u000f\u0006\u0003\u0002\u0006\u0005}\u0001\u0002C;\u0002\u001a\u0005\u0005\t\u0019A\u0010\b\u0015\u0005\r\"!!A\t\u0002\u0011\t)#\u0001\nJ]\u0012L'/Z2u)\u0006\u001c8NU3tk2$\bcA\n\u0002(\u0019I\u0011AAA\u0001\u0012\u0003!\u0011\u0011F\n\u0005\u0003OaQ\u0006C\u0004D\u0003O!\t!!\f\u0015\u0005\u0005\u0015\u0002BCA\u000b\u0003O\t\t\u0011\"\u0012\u0002\u0018!Q\u00111GA\u0014\u0003\u0003%\t)!\u000e\u0002\u000b\u0005\u0004\b\u000f\\=\u0016\t\u0005]\u0012Q\b\u000b\u0007\u0003s\ty$!\u0011\u0011\tM\u0001\u00111\b\t\u0004/\u0005uBAB\r\u00022\t\u00071\u0004\u0003\u00041\u0003c\u0001\rA\r\u0005\u0007w\u0005E\u0002\u0019A\u001f\t\u0015\u0005\u0015\u0013qEA\u0001\n\u0003\u000b9%A\u0004v]\u0006\u0004\b\u000f\\=\u0016\t\u0005%\u0013q\f\u000b\u0005\u0003\u0017\n9\u0006E\u0003\u000e\u0003\u001b\n\t&C\u0002\u0002P9\u0011aa\u00149uS>t\u0007#B\u0007\u0002TIj\u0014bAA+\u001d\t1A+\u001e9mKJB!\"!\u0017\u0002D\u0005\u0005\t\u0019AA.\u0003\rAH\u0005\r\t\u0005'\u0001\ti\u0006E\u0002\u0018\u0003?\"a!GA\"\u0005\u0004Y\u0002BCA2\u0003O\t\t\u0011\"\u0003\u0002f\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t9\u0007E\u0002k\u0003SJ1!a\u001bl\u0005\u0019y%M[3di\u0002"
)
public class IndirectTaskResult<T> implements TaskResult<T>, Product, Serializable {
   private final BlockId blockId;
   private final int size;

   public static <T> Option<Tuple2<BlockId, Object>> unapply(IndirectTaskResult<T> var0) {
      return IndirectTaskResult$.MODULE$.unapply(var0);
   }

   public static <T> IndirectTaskResult<T> apply(BlockId var0, int var1) {
      return IndirectTaskResult$.MODULE$.<T>apply(var0, var1);
   }

   public BlockId blockId() {
      return this.blockId;
   }

   public int size() {
      return this.size;
   }

   public <T> IndirectTaskResult<T> copy(BlockId blockId, int size) {
      return new IndirectTaskResult<T>(blockId, size);
   }

   public <T> BlockId copy$default$1() {
      return this.blockId();
   }

   public <T> int copy$default$2() {
      return this.size();
   }

   public String productPrefix() {
      return "IndirectTaskResult";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.blockId();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.size());
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
      return x$1 instanceof IndirectTaskResult;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.blockId()));
      var1 = Statics.mix(var1, this.size());
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
            if (x$1 instanceof IndirectTaskResult) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  label36: {
                     IndirectTaskResult var4 = (IndirectTaskResult)x$1;
                     BlockId var10000 = this.blockId();
                     BlockId var5 = var4.blockId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label36;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label36;
                     }

                     if (this.size() == var4.size() && var4.canEqual(this)) {
                        var7 = true;
                        break label37;
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

   public IndirectTaskResult(BlockId blockId, int size) {
      this.blockId = blockId;
      this.size = size;
      Product$class.$init$(this);
   }
}
