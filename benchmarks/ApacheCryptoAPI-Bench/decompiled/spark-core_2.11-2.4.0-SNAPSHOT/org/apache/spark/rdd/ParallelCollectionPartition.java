package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.SparkEnv$;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mb!B\u0001\u0003\u0001\u0011Q!a\u0007)be\u0006dG.\u001a7D_2dWm\u0019;j_:\u0004\u0016M\u001d;ji&|gN\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,\"aC$\u0014\t\u0001a!C\u0006\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0003\n\u0005U!!!\u0003)beRLG/[8o!\tiq#\u0003\u0002\u0019\u001d\ta1+\u001a:jC2L'0\u00192mK\"A!\u0004\u0001BA\u0002\u0013\u0005A$A\u0003sI\u0012LEm\u0001\u0001\u0016\u0003u\u0001\"!\u0004\u0010\n\u0005}q!\u0001\u0002'p]\u001eD\u0001\"\t\u0001\u0003\u0002\u0004%\tAI\u0001\ne\u0012$\u0017\nZ0%KF$\"a\t\u0014\u0011\u00055!\u0013BA\u0013\u000f\u0005\u0011)f.\u001b;\t\u000f\u001d\u0002\u0013\u0011!a\u0001;\u0005\u0019\u0001\u0010J\u0019\t\u0011%\u0002!\u0011!Q!\nu\taA\u001d3e\u0013\u0012\u0004\u0003\u0002C\u0016\u0001\u0005\u0003\u0007I\u0011\u0001\u0017\u0002\u000bMd\u0017nY3\u0016\u00035\u0002\"!\u0004\u0018\n\u0005=r!aA%oi\"A\u0011\u0007\u0001BA\u0002\u0013\u0005!'A\u0005tY&\u001cWm\u0018\u0013fcR\u00111e\r\u0005\bOA\n\t\u00111\u0001.\u0011!)\u0004A!A!B\u0013i\u0013AB:mS\u000e,\u0007\u0005\u0003\u00058\u0001\t\u0005\r\u0011\"\u00019\u0003\u00191\u0018\r\\;fgV\t\u0011\bE\u0002;\u0005\u0016s!a\u000f!\u000f\u0005qzT\"A\u001f\u000b\u0005yZ\u0012A\u0002\u001fs_>$h(C\u0001\u0010\u0013\t\te\"A\u0004qC\u000e\\\u0017mZ3\n\u0005\r#%aA*fc*\u0011\u0011I\u0004\t\u0003\r\u001ec\u0001\u0001B\u0003I\u0001\t\u0007\u0011JA\u0001U#\tQU\n\u0005\u0002\u000e\u0017&\u0011AJ\u0004\u0002\b\u001d>$\b.\u001b8h!\tia*\u0003\u0002P\u001d\t\u0019\u0011I\\=\t\u0011E\u0003!\u00111A\u0005\u0002I\u000b!B^1mk\u0016\u001cx\fJ3r)\t\u00193\u000bC\u0004(!\u0006\u0005\t\u0019A\u001d\t\u0011U\u0003!\u0011!Q!\ne\nqA^1mk\u0016\u001c\b\u0005\u0003\u0005X\u0001\t\r\t\u0015a\u0003Y\u0003))g/\u001b3f]\u000e,G%\r\t\u00043r+U\"\u0001.\u000b\u0005ms\u0011a\u0002:fM2,7\r^\u0005\u0003;j\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006?\u0002!\t\u0001Y\u0001\u0007y%t\u0017\u000e\u001e \u0015\t\u0005,gm\u001a\u000b\u0003E\u0012\u00042a\u0019\u0001F\u001b\u0005\u0011\u0001\"B,_\u0001\bA\u0006\"\u0002\u000e_\u0001\u0004i\u0002\"B\u0016_\u0001\u0004i\u0003\"B\u001c_\u0001\u0004I\u0004\"B5\u0001\t\u0003Q\u0017\u0001C5uKJ\fGo\u001c:\u0016\u0003-\u00042A\u000f7F\u0013\tiGI\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u0015y\u0007\u0001\"\u0011q\u0003!A\u0017m\u001d5D_\u0012,G#A\u0017\t\u000bI\u0004A\u0011I:\u0002\r\u0015\fX/\u00197t)\t!x\u000f\u0005\u0002\u000ek&\u0011aO\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015A\u0018\u000f1\u0001N\u0003\u0015yG\u000f[3s\u0011\u0015Q\b\u0001\"\u0011-\u0003\u0015Ig\u000eZ3y\u0011\u0015a\b\u0001\"\u0003~\u0003-9(/\u001b;f\u001f\nTWm\u0019;\u0015\u0005\rr\bBB@|\u0001\u0004\t\t!A\u0002pkR\u0004B!a\u0001\u0002\u000e5\u0011\u0011Q\u0001\u0006\u0005\u0003\u000f\tI!\u0001\u0002j_*\u0011\u00111B\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002\u0010\u0005\u0015!AE(cU\u0016\u001cGoT;uaV$8\u000b\u001e:fC6DSa_A\n\u0003?\u0001R!DA\u000b\u00033I1!a\u0006\u000f\u0005\u0019!\bN]8xgB!\u00111AA\u000e\u0013\u0011\ti\"!\u0002\u0003\u0017%{U\t_2faRLwN\\\u0012\u0003\u00033Aq!a\t\u0001\t\u0013\t)#\u0001\u0006sK\u0006$wJ\u00196fGR$2aIA\u0014\u0011!\tI#!\tA\u0002\u0005-\u0012AA5o!\u0011\t\u0019!!\f\n\t\u0005=\u0012Q\u0001\u0002\u0012\u001f\nTWm\u0019;J]B,Ho\u0015;sK\u0006l\u0007FBA\u0011\u0003'\ty\u0002"
)
public class ParallelCollectionPartition<T> implements Partition {
   private long rddId;
   private int slice;
   private Seq<T> values;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public long rddId() {
      return this.rddId;
   }

   public void rddId_$eq(long x$1) {
      this.rddId = x$1;
   }

   public int slice() {
      return this.slice;
   }

   public void slice_$eq(int x$1) {
      this.slice = x$1;
   }

   public Seq<T> values() {
      return this.values;
   }

   public void values_$eq(Seq<T> x$1) {
      this.values = x$1;
   }

   public Iterator<T> iterator() {
      return this.values().iterator();
   }

   public int hashCode() {
      return (int)(41L * (41L + this.rddId()) + (long)this.slice());
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof ParallelCollectionPartition) {
         ParallelCollectionPartition var3 = (ParallelCollectionPartition)other;
         var4 = this.rddId() == var3.rddId() && this.slice() == var3.slice();
      } else {
         var4 = false;
      }

      return var4;
   }

   public int index() {
      return this.slice();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ParallelCollectionPartition $outer;
         private final ObjectOutputStream out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Serializer sfactory = SparkEnv$.MODULE$.get().serializer();
            if (sfactory instanceof JavaSerializer) {
               this.out$1.defaultWriteObject();
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               this.out$1.writeLong(this.$outer.rddId());
               this.out$1.writeInt(this.$outer.slice());
               SerializerInstance ser = sfactory.newInstance();
               Utils$.MODULE$.serializeViaNestedStream(this.out$1, ser, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(SerializationStream x$1) {
                     x$1.writeObject(this.$outer.org$apache$spark$rdd$ParallelCollectionPartition$$anonfun$$$outer().values(), ClassTag$.MODULE$.apply(Seq.class));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var5 = BoxedUnit.UNIT;
            }

         }

         // $FF: synthetic method
         public ParallelCollectionPartition org$apache$spark$rdd$ParallelCollectionPartition$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   private void readObject(ObjectInputStream in) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ParallelCollectionPartition $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Serializer sfactory = SparkEnv$.MODULE$.get().serializer();
            if (sfactory instanceof JavaSerializer) {
               this.in$1.defaultReadObject();
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               this.$outer.rddId_$eq(this.in$1.readLong());
               this.$outer.slice_$eq(this.in$1.readInt());
               SerializerInstance ser = sfactory.newInstance();
               Utils$.MODULE$.deserializeViaNestedStream(this.in$1, ser, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(DeserializationStream ds) {
                     this.$outer.org$apache$spark$rdd$ParallelCollectionPartition$$anonfun$$$outer().values_$eq((Seq)ds.readObject(ClassTag$.MODULE$.apply(Seq.class)));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var5 = BoxedUnit.UNIT;
            }

         }

         // $FF: synthetic method
         public ParallelCollectionPartition org$apache$spark$rdd$ParallelCollectionPartition$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public ParallelCollectionPartition(long rddId, int slice, Seq<T> values, ClassTag<T> evidence$1) {
      this.rddId = rddId;
      this.slice = slice;
      this.values = values;
      super();
      Partition$class.$init$(this);
   }
}
