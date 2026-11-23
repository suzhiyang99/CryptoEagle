package org.apache.spark.storage.memory;

import org.apache.spark.memory.MemoryMode;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ef\u0001B\u0001\u0003\t6\u0011q\u0003R3tKJL\u0017\r\\5{K\u0012lU-\\8ss\u0016sGO]=\u000b\u0005\r!\u0011AB7f[>\u0014\u0018P\u0003\u0002\u0006\r\u000591\u000f^8sC\u001e,'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001U\u0011abG\n\u0006\u0001=)Be\n\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0007Y9\u0012$D\u0001\u0003\u0013\tA\"AA\u0006NK6|'/_#oiJL\bC\u0001\u000e\u001c\u0019\u0001!Q\u0001\b\u0001C\u0002u\u0011\u0011\u0001V\t\u0003=\u0005\u0002\"\u0001E\u0010\n\u0005\u0001\n\"a\u0002(pi\"Lgn\u001a\t\u0003!\tJ!aI\t\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u0011K%\u0011a%\u0005\u0002\b!J|G-^2u!\t\u0001\u0002&\u0003\u0002*#\ta1+\u001a:jC2L'0\u00192mK\"A1\u0006\u0001BK\u0002\u0013\u0005A&A\u0003wC2,X-F\u0001.!\r\u0001b&G\u0005\u0003_E\u0011Q!\u0011:sCfD\u0001\"\r\u0001\u0003\u0012\u0003\u0006I!L\u0001\u0007m\u0006dW/\u001a\u0011\t\u0011M\u0002!Q3A\u0005\u0002Q\nAa]5{KV\tQ\u0007\u0005\u0002\u0011m%\u0011q'\u0005\u0002\u0005\u0019>tw\r\u0003\u0005:\u0001\tE\t\u0015!\u00036\u0003\u0015\u0019\u0018N_3!\u0011!Y\u0004A!f\u0001\n\u0003a\u0014\u0001C2mCN\u001cH+Y4\u0016\u0003u\u00022AP!\u001a\u001b\u0005y$B\u0001!\u0012\u0003\u001d\u0011XM\u001a7fGRL!AQ \u0003\u0011\rc\u0017m]:UC\u001eD\u0001\u0002\u0012\u0001\u0003\u0012\u0003\u0006I!P\u0001\nG2\f7o\u001d+bO\u0002BQA\u0012\u0001\u0005\u0002\u001d\u000ba\u0001P5oSRtD\u0003\u0002%J\u0015.\u00032A\u0006\u0001\u001a\u0011\u0015YS\t1\u0001.\u0011\u0015\u0019T\t1\u00016\u0011\u0015YT\t1\u0001>\u0011\u001di\u0005A1A\u0005\u00029\u000b!\"\\3n_JLXj\u001c3f+\u0005y\u0005C\u0001)S\u001b\u0005\t&BA\u0002\u0007\u0013\t\u0019\u0016K\u0001\u0006NK6|'/_'pI\u0016Da!\u0016\u0001!\u0002\u0013y\u0015aC7f[>\u0014\u00180T8eK\u0002Bqa\u0016\u0001\u0002\u0002\u0013\u0005\u0001,\u0001\u0003d_BLXCA-])\u0011QVl\u00181\u0011\u0007Y\u00011\f\u0005\u0002\u001b9\u0012)AD\u0016b\u0001;!91F\u0016I\u0001\u0002\u0004q\u0006c\u0001\t/7\"91G\u0016I\u0001\u0002\u0004)\u0004bB\u001eW!\u0003\u0005\r!\u0019\t\u0004}\u0005[\u0006bB2\u0001#\u0003%\t\u0001Z\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\t)\u0007/F\u0001gU\tismK\u0001i!\tIg.D\u0001k\u0015\tYG.A\u0005v]\u000eDWmY6fI*\u0011Q.E\u0001\u000bC:tw\u000e^1uS>t\u0017BA8k\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0003\u00069\t\u0014\r!\b\u0005\be\u0002\t\n\u0011\"\u0001t\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\"\u0001\u001e<\u0016\u0003UT#!N4\u0005\u000bq\t(\u0019A\u000f\t\u000fa\u0004\u0011\u0013!C\u0001s\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aTC\u0001>}+\u0005Y(FA\u001fh\t\u0015arO1\u0001\u001e\u0011\u001dq\b!!A\u0005B}\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAA\u0001!\u0011\t\u0019!!\u0004\u000e\u0005\u0005\u0015!\u0002BA\u0004\u0003\u0013\tA\u0001\\1oO*\u0011\u00111B\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002\u0010\u0005\u0015!AB*ue&tw\rC\u0005\u0002\u0014\u0001\t\t\u0011\"\u0001\u0002\u0016\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011\u0011q\u0003\t\u0004!\u0005e\u0011bAA\u000e#\t\u0019\u0011J\u001c;\t\u0013\u0005}\u0001!!A\u0005\u0002\u0005\u0005\u0012A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004C\u0005\r\u0002BCA\u0013\u0003;\t\t\u00111\u0001\u0002\u0018\u0005\u0019\u0001\u0010J\u0019\t\u0013\u0005%\u0002!!A\u0005B\u0005-\u0012a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0005\u00055\u0002#BA\u0018\u0003k\tSBAA\u0019\u0015\r\t\u0019$E\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\u001c\u0003c\u0011\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003w\u0001\u0011\u0011!C\u0001\u0003{\t\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003\u007f\t)\u0005E\u0002\u0011\u0003\u0003J1!a\u0011\u0012\u0005\u001d\u0011un\u001c7fC:D\u0011\"!\n\u0002:\u0005\u0005\t\u0019A\u0011\t\u0013\u0005%\u0003!!A\u0005B\u0005-\u0013\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0005\u0005]\u0001\"CA(\u0001\u0005\u0005I\u0011IA)\u0003!!xn\u0015;sS:<GCAA\u0001\u0011%\t)\u0006AA\u0001\n\u0003\n9&\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u007f\tI\u0006C\u0005\u0002&\u0005M\u0013\u0011!a\u0001C\u001dI\u0011Q\f\u0002\u0002\u0002#%\u0011qL\u0001\u0018\t\u0016\u001cXM]5bY&TX\rZ'f[>\u0014\u00180\u00128uef\u00042AFA1\r!\t!!!A\t\n\u0005\r4\u0003BA1\u001f\u001dBqARA1\t\u0003\t9\u0007\u0006\u0002\u0002`!Q\u0011qJA1\u0003\u0003%)%!\u0015\t\u0015\u00055\u0014\u0011MA\u0001\n\u0003\u000by'A\u0003baBd\u00170\u0006\u0003\u0002r\u0005]D\u0003CA:\u0003s\ni(a \u0011\tY\u0001\u0011Q\u000f\t\u00045\u0005]DA\u0002\u000f\u0002l\t\u0007Q\u0004C\u0004,\u0003W\u0002\r!a\u001f\u0011\tAq\u0013Q\u000f\u0005\u0007g\u0005-\u0004\u0019A\u001b\t\u000fm\nY\u00071\u0001\u0002\u0002B!a(QA;\u0011)\t))!\u0019\u0002\u0002\u0013\u0005\u0015qQ\u0001\bk:\f\u0007\u000f\u001d7z+\u0011\tI)a'\u0015\t\u0005-\u0015q\u0014\t\u0006!\u00055\u0015\u0011S\u0005\u0004\u0003\u001f\u000b\"AB(qi&|g\u000e\u0005\u0005\u0011\u0003'\u000b9*NAO\u0013\r\t)*\u0005\u0002\u0007)V\u0004H.Z\u001a\u0011\tAq\u0013\u0011\u0014\t\u00045\u0005mEA\u0002\u000f\u0002\u0004\n\u0007Q\u0004\u0005\u0003?\u0003\u0006e\u0005BCAQ\u0003\u0007\u000b\t\u00111\u0001\u0002$\u0006\u0019\u0001\u0010\n\u0019\u0011\tY\u0001\u0011\u0011\u0014\u0005\u000b\u0003O\u000b\t'!A\u0005\n\u0005%\u0016a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a+\u0011\t\u0005\r\u0011QV\u0005\u0005\u0003_\u000b)A\u0001\u0004PE*,7\r\u001e"
)
public class DeserializedMemoryEntry<T> implements MemoryEntry<T>, Product, Serializable {
   private final Object value;
   private final long size;
   private final ClassTag<T> classTag;
   private final MemoryMode memoryMode;

   public static <T> Option<Tuple3<Object, Object, ClassTag<T>>> unapply(DeserializedMemoryEntry<T> var0) {
      return DeserializedMemoryEntry$.MODULE$.unapply(var0);
   }

   public static <T> DeserializedMemoryEntry<T> apply(Object var0, long var1, ClassTag<T> var3) {
      return DeserializedMemoryEntry$.MODULE$.<T>apply(var0, var1, var3);
   }

   public Object value() {
      return this.value;
   }

   public long size() {
      return this.size;
   }

   public ClassTag<T> classTag() {
      return this.classTag;
   }

   public MemoryMode memoryMode() {
      return this.memoryMode;
   }

   public <T> DeserializedMemoryEntry<T> copy(Object value, long size, ClassTag<T> classTag) {
      return new DeserializedMemoryEntry<T>(value, size, classTag);
   }

   public <T> Object copy$default$1() {
      return this.value();
   }

   public <T> long copy$default$2() {
      return this.size();
   }

   public <T> ClassTag<T> copy$default$3() {
      return this.classTag();
   }

   public String productPrefix() {
      return "DeserializedMemoryEntry";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.value();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.size());
            break;
         case 2:
            var10000 = this.classTag();
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
      return x$1 instanceof DeserializedMemoryEntry;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.value()));
      var1 = Statics.mix(var1, Statics.longHash(this.size()));
      var1 = Statics.mix(var1, Statics.anyHash(this.classTag()));
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
            if (x$1 instanceof DeserializedMemoryEntry) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  DeserializedMemoryEntry var4 = (DeserializedMemoryEntry)x$1;
                  if (BoxesRunTime.equals(this.value(), var4.value()) && this.size() == var4.size()) {
                     label36: {
                        ClassTag var10000 = this.classTag();
                        ClassTag var5 = var4.classTag();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label36;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label36;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label39;
                        }
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

   public DeserializedMemoryEntry(Object value, long size, ClassTag<T> classTag) {
      this.value = value;
      this.size = size;
      this.classTag = classTag;
      Product$class.$init$(this);
      this.memoryMode = MemoryMode.ON_HEAP;
   }
}
