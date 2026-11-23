package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001da!B\u0001\u0003\u0001\u0011Q!!\t)beRLG/[8oKJ\fu/\u0019:f+:LwN\u001c*E\tB\u000b'\u000f^5uS>t'BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u00112#D\u0001\u0005\u0013\t!BAA\u0005QCJ$\u0018\u000e^5p]\"Aa\u0003\u0001BC\u0002\u0013\u0005\u0001$\u0001\u0003sI\u0012\u001c8\u0001A\u000b\u00023A\u0019!DI\u0013\u000f\u0005m\u0001cB\u0001\u000f \u001b\u0005i\"B\u0001\u0010\u0018\u0003\u0019a$o\\8u}%\ta\"\u0003\u0002\"\u001b\u00059\u0001/Y2lC\u001e,\u0017BA\u0012%\u0005\r\u0019V-\u001d\u0006\u0003C5\u0001$A\n\u0017\u0011\u0007\u001dB#&D\u0001\u0003\u0013\tI#AA\u0002S\t\u0012\u0003\"a\u000b\u0017\r\u0001\u0011IQFLA\u0001\u0002\u0003\u0015\t!\u000e\u0002\u0004?\u0012\n\u0004\u0002C\u0018\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u000bI$Gm\u001d\u0011\u0011\u0007i\u0011\u0013\u0007\r\u00023iA\u0019q\u0005K\u001a\u0011\u0005-\"D!C\u0017/\u0003\u0003\u0005\tQ!\u00016#\t1\u0014\b\u0005\u0002\ro%\u0011\u0001(\u0004\u0002\b\u001d>$\b.\u001b8h!\ta!(\u0003\u0002<\u001b\t\u0019\u0011I\\=)\u00059j\u0004C\u0001\u0007?\u0013\tyTBA\u0005ue\u0006t7/[3oi\"A\u0011\t\u0001BC\u0002\u0013\u0005#)A\u0003j]\u0012,\u00070F\u0001D!\taA)\u0003\u0002F\u001b\t\u0019\u0011J\u001c;\t\u0011\u001d\u0003!\u0011!Q\u0001\n\r\u000ba!\u001b8eKb\u0004\u0003\"B%\u0001\t\u0003Q\u0015A\u0002\u001fj]&$h\bF\u0002L\u0019J\u0003\"a\n\u0001\t\u000bYA\u0005\u0019A'\u0011\u0007i\u0011c\n\r\u0002P#B\u0019q\u0005\u000b)\u0011\u0005-\nF!C\u0017M\u0003\u0003\u0005\tQ!\u00016\u0011\u0015\t\u0005\n1\u0001D\u0011\u001d!\u0006\u00011A\u0005\u0002U\u000bq\u0001]1sK:$8/F\u0001W!\raq+E\u0005\u000316\u0011Q!\u0011:sCfDqA\u0017\u0001A\u0002\u0013\u00051,A\u0006qCJ,g\u000e^:`I\u0015\fHC\u0001/`!\taQ,\u0003\u0002_\u001b\t!QK\\5u\u0011\u001d\u0001\u0017,!AA\u0002Y\u000b1\u0001\u001f\u00132\u0011\u0019\u0011\u0007\u0001)Q\u0005-\u0006A\u0001/\u0019:f]R\u001c\b\u0005C\u0003e\u0001\u0011\u0005S-\u0001\u0005iCND7i\u001c3f)\u0005\u0019\u0005\"B4\u0001\t\u0003B\u0017AB3rk\u0006d7\u000f\u0006\u0002jYB\u0011AB[\u0005\u0003W6\u0011qAQ8pY\u0016\fg\u000eC\u0003nM\u0002\u0007\u0011(A\u0003pi\",'\u000fC\u0003p\u0001\u0011%\u0001/A\u0006xe&$Xm\u00142kK\u000e$HC\u0001/r\u0011\u0015\u0011h\u000e1\u0001t\u0003\rywn\u001d\t\u0003ifl\u0011!\u001e\u0006\u0003m^\f!![8\u000b\u0003a\fAA[1wC&\u0011!0\u001e\u0002\u0013\u001f\nTWm\u0019;PkR\u0004X\u000f^*ue\u0016\fW\u000e\u000b\u0003oy\u0006\u0015\u0001c\u0001\u0007~\u007f&\u0011a0\u0004\u0002\u0007i\"\u0014xn^:\u0011\u0007Q\f\t!C\u0002\u0002\u0004U\u00141\"S(Fq\u000e,\u0007\u000f^5p]\u000e\nq\u0010"
)
public class PartitionerAwareUnionRDDPartition implements Partition {
   private final transient Seq<RDD<?>> rdds;
   private final int index;
   private Partition[] parents;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public Seq<RDD<?>> rdds() {
      return this.rdds;
   }

   public int index() {
      return this.index;
   }

   public Partition[] parents() {
      return this.parents;
   }

   public void parents_$eq(Partition[] x$1) {
      this.parents = x$1;
   }

   public int hashCode() {
      return this.index();
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDDPartition $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.parents_$eq((Partition[])((TraversableOnce)this.$outer.rdds().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Partition apply(RDD<?> x$2) {
                  return x$2.partitions()[this.$outer.org$apache$spark$rdd$PartitionerAwareUnionRDDPartition$$anonfun$$$outer().index()];
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class)));
            this.oos$1.defaultWriteObject();
         }

         // $FF: synthetic method
         public PartitionerAwareUnionRDDPartition org$apache$spark$rdd$PartitionerAwareUnionRDDPartition$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.oos$1 = oos$1;
            }
         }
      });
   }

   public PartitionerAwareUnionRDDPartition(Seq<RDD<?>> rdds, int index) {
      this.rdds = rdds;
      this.index = index;
      Partition$class.$init$(this);
      this.parents = (Partition[])((TraversableOnce)rdds.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDDPartition $outer;

         public final Partition apply(RDD<?> x$1) {
            return x$1.partitions()[this.$outer.index()];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class));
   }
}
