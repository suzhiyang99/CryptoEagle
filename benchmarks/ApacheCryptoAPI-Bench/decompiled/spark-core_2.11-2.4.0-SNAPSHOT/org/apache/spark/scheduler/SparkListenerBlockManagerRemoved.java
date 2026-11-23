package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.storage.BlockManagerId;
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

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ec\u0001B\u0001\u0003\u0001.\u0011\u0001e\u00159be.d\u0015n\u001d;f]\u0016\u0014(\t\\8dW6\u000bg.Y4feJ+Wn\u001c<fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0015\u0001AB\u0005\f\u001a!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111\u0003F\u0007\u0002\u0005%\u0011QC\u0001\u0002\u0013'B\f'o\u001b'jgR,g.\u001a:Fm\u0016tG\u000f\u0005\u0002\u000e/%\u0011\u0001D\u0004\u0002\b!J|G-^2u!\ti!$\u0003\u0002\u001c\u001d\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$\u0001\u0003uS6,W#A\u0010\u0011\u00055\u0001\u0013BA\u0011\u000f\u0005\u0011auN\\4\t\u0011\r\u0002!\u0011#Q\u0001\n}\tQ\u0001^5nK\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAJ\u0001\u000fE2|7m['b]\u0006<WM]%e+\u00059\u0003C\u0001\u0015,\u001b\u0005I#B\u0001\u0016\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016L!\u0001L\u0015\u0003\u001d\tcwnY6NC:\fw-\u001a:JI\"Aa\u0006\u0001B\tB\u0003%q%A\bcY>\u001c7.T1oC\u001e,'/\u00133!\u0011\u0015\u0001\u0004\u0001\"\u00012\u0003\u0019a\u0014N\\5u}Q\u0019!g\r\u001b\u0011\u0005M\u0001\u0001\"B\u000f0\u0001\u0004y\u0002\"B\u00130\u0001\u00049\u0003b\u0002\u001c\u0001\u0003\u0003%\taN\u0001\u0005G>\u0004\u0018\u0010F\u00023qeBq!H\u001b\u0011\u0002\u0003\u0007q\u0004C\u0004&kA\u0005\t\u0019A\u0014\t\u000fm\u0002\u0011\u0013!C\u0001y\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\u001f+\u0005}q4&A \u0011\u0005\u0001+U\"A!\u000b\u0005\t\u001b\u0015!C;oG\",7m[3e\u0015\t!e\"\u0001\u0006b]:|G/\u0019;j_:L!AR!\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004I\u0001E\u0005I\u0011A%\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\t!J\u000b\u0002(}!9A\nAA\u0001\n\u0003j\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001O!\tyE+D\u0001Q\u0015\t\t&+\u0001\u0003mC:<'\"A*\u0002\t)\fg/Y\u0005\u0003+B\u0013aa\u0015;sS:<\u0007bB,\u0001\u0003\u0003%\t\u0001W\u0001\raJ|G-^2u\u0003JLG/_\u000b\u00023B\u0011QBW\u0005\u00037:\u00111!\u00138u\u0011\u001di\u0006!!A\u0005\u0002y\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002`EB\u0011Q\u0002Y\u0005\u0003C:\u00111!\u00118z\u0011\u001d\u0019G,!AA\u0002e\u000b1\u0001\u001f\u00132\u0011\u001d)\u0007!!A\u0005B\u0019\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002OB\u0019\u0001n[0\u000e\u0003%T!A\u001b\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002mS\nA\u0011\n^3sCR|'\u000fC\u0004o\u0001\u0005\u0005I\u0011A8\u0002\u0011\r\fg.R9vC2$\"\u0001]:\u0011\u00055\t\u0018B\u0001:\u000f\u0005\u001d\u0011un\u001c7fC:DqaY7\u0002\u0002\u0003\u0007q\fC\u0004v\u0001\u0005\u0005I\u0011\t<\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012!\u0017\u0005\bq\u0002\t\t\u0011\"\u0011z\u0003!!xn\u0015;sS:<G#\u0001(\t\u000fm\u0004\u0011\u0011!C!y\u00061Q-];bYN$\"\u0001]?\t\u000f\rT\u0018\u0011!a\u0001?\"\u0012\u0001a \t\u0005\u0003\u0003\t)!\u0004\u0002\u0002\u0004)\u0011A\tB\u0005\u0005\u0003\u000f\t\u0019A\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018nB\u0005\u0002\f\t\t\t\u0011#\u0001\u0002\u000e\u0005\u00013\u000b]1sW2K7\u000f^3oKJ\u0014En\\2l\u001b\u0006t\u0017mZ3s%\u0016lwN^3e!\r\u0019\u0012q\u0002\u0004\t\u0003\t\t\t\u0011#\u0001\u0002\u0012M)\u0011qBA\n3A9\u0011QCA\u000e?\u001d\u0012TBAA\f\u0015\r\tIBD\u0001\beVtG/[7f\u0013\u0011\ti\"a\u0006\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t'\u0007C\u00041\u0003\u001f!\t!!\t\u0015\u0005\u00055\u0001\u0002\u0003=\u0002\u0010\u0005\u0005IQI=\t\u0015\u0005\u001d\u0012qBA\u0001\n\u0003\u000bI#A\u0003baBd\u0017\u0010F\u00033\u0003W\ti\u0003\u0003\u0004\u001e\u0003K\u0001\ra\b\u0005\u0007K\u0005\u0015\u0002\u0019A\u0014\t\u0015\u0005E\u0012qBA\u0001\n\u0003\u000b\u0019$A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005U\u0012\u0011\t\t\u0006\u001b\u0005]\u00121H\u0005\u0004\u0003sq!AB(qi&|g\u000eE\u0003\u000e\u0003{yr%C\u0002\u0002@9\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\"\u0003_\t\t\u00111\u00013\u0003\rAH\u0005\r\u0005\u000b\u0003\u000f\ny!!A\u0005\n\u0005%\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0013\u0011\u0007=\u000bi%C\u0002\u0002PA\u0013aa\u00142kK\u000e$\b"
)
public class SparkListenerBlockManagerRemoved implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final BlockManagerId blockManagerId;

   public static Option<Tuple2<Object, BlockManagerId>> unapply(SparkListenerBlockManagerRemoved var0) {
      return SparkListenerBlockManagerRemoved$.MODULE$.unapply(var0);
   }

   public static SparkListenerBlockManagerRemoved apply(long var0, BlockManagerId var2) {
      return SparkListenerBlockManagerRemoved$.MODULE$.apply(var0, var2);
   }

   public static Function1<Tuple2<Object, BlockManagerId>, SparkListenerBlockManagerRemoved> tupled() {
      return SparkListenerBlockManagerRemoved$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<BlockManagerId, SparkListenerBlockManagerRemoved>> curried() {
      return SparkListenerBlockManagerRemoved$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public SparkListenerBlockManagerRemoved copy(long time, BlockManagerId blockManagerId) {
      return new SparkListenerBlockManagerRemoved(time, blockManagerId);
   }

   public long copy$default$1() {
      return this.time();
   }

   public BlockManagerId copy$default$2() {
      return this.blockManagerId();
   }

   public String productPrefix() {
      return "SparkListenerBlockManagerRemoved";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 1:
            var10000 = this.blockManagerId();
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
      return x$1 instanceof SparkListenerBlockManagerRemoved;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.blockManagerId()));
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
            if (x$1 instanceof SparkListenerBlockManagerRemoved) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  SparkListenerBlockManagerRemoved var4 = (SparkListenerBlockManagerRemoved)x$1;
                  if (this.time() == var4.time()) {
                     label35: {
                        BlockManagerId var10000 = this.blockManagerId();
                        BlockManagerId var5 = var4.blockManagerId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
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

   public SparkListenerBlockManagerRemoved(long time, BlockManagerId blockManagerId) {
      this.time = time;
      this.blockManagerId = blockManagerId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
