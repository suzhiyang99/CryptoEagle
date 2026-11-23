package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a!B\u0001\u0003\u0001\u0011Q!!\u0007.jaB,G\rU1si&$\u0018n\u001c8t!\u0006\u0014H/\u001b;j_:T!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0003\n\u0005Q!!!\u0003)beRLG/[8o\u0011!1\u0002A!A!\u0002\u0013A\u0012aA5eq\u000e\u0001\u0001C\u0001\u0007\u001a\u0013\tQRBA\u0002J]RD\u0001\u0002\b\u0001\u0003\u0006\u0004%I!H\u0001\u0005e\u0012$7/F\u0001\u001f!\ryrE\u000b\b\u0003A\u0015r!!\t\u0013\u000e\u0003\tR!aI\f\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011B\u0001\u0014\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001K\u0015\u0003\u0007M+\u0017O\u0003\u0002'\u001bA\u00121&\r\t\u0004Y5zS\"\u0001\u0002\n\u00059\u0012!a\u0001*E\tB\u0011\u0001'\r\u0007\u0001\t%\u00114'!A\u0001\u0002\u000b\u0005!HA\u0002`IEB\u0001\u0002\u000e\u0001\u0003\u0002\u0003\u0006I!N\u0001\u0006e\u0012$7\u000f\t\t\u0004?\u001d2\u0004GA\u001c:!\raS\u0006\u000f\t\u0003ae\"\u0011BM\u001a\u0002\u0002\u0003\u0005)\u0011\u0001\u001e\u0012\u0005mr\u0004C\u0001\u0007=\u0013\tiTBA\u0004O_RD\u0017N\\4\u0011\u00051y\u0014B\u0001!\u000e\u0005\r\te.\u001f\u0015\u0003g\t\u0003\"\u0001D\"\n\u0005\u0011k!!\u0003;sC:\u001c\u0018.\u001a8u\u0011!1\u0005A!b\u0001\n\u00039\u0015A\u00059sK\u001a,'O]3e\u0019>\u001c\u0017\r^5p]N,\u0012\u0001\u0013\t\u0004?\u001dJ\u0005C\u0001&N\u001d\ta1*\u0003\u0002M\u001b\u00051\u0001K]3eK\u001aL!AT(\u0003\rM#(/\u001b8h\u0015\taU\u0002\u0003\u0005R\u0001\t\u0005\t\u0015!\u0003I\u0003M\u0001(/\u001a4feJ,G\rT8dCRLwN\\:!Q\t\u0001&\tC\u0003U\u0001\u0011\u0005Q+\u0001\u0004=S:LGO\u0010\u000b\u0005-^Cf\f\u0005\u0002-\u0001!)ac\u0015a\u00011!)Ad\u0015a\u00013B\u0019qd\n.1\u0005mk\u0006c\u0001\u0017.9B\u0011\u0001'\u0018\u0003\nea\u000b\t\u0011!A\u0003\u0002iBQAR*A\u0002!Cq\u0001\u0019\u0001C\u0002\u0013\u0005\u0013-A\u0003j]\u0012,\u00070F\u0001\u0019\u0011\u0019\u0019\u0007\u0001)A\u00051\u00051\u0011N\u001c3fq\u0002Bq!\u001a\u0001A\u0002\u0013\u0005a-A\bqCJ$\u0018\u000e^5p]Z\u000bG.^3t+\u00059\u0007c\u00015l#5\t\u0011N\u0003\u0002k\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005!J\u0007bB7\u0001\u0001\u0004%\tA\\\u0001\u0014a\u0006\u0014H/\u001b;j_:4\u0016\r\\;fg~#S-\u001d\u000b\u0003_J\u0004\"\u0001\u00049\n\u0005El!\u0001B+oSRDqa\u001d7\u0002\u0002\u0003\u0007q-A\u0002yIEBa!\u001e\u0001!B\u00139\u0017\u0001\u00059beRLG/[8o-\u0006dW/Z:!\u0011\u00159\b\u0001\"\u0001y\u0003)\u0001\u0018M\u001d;ji&|gn]\u000b\u0002sB\u0019qdJ\t\t\u000bm\u0004A\u0011\u0002?\u0002\u0017]\u0014\u0018\u000e^3PE*,7\r\u001e\u000b\u0003_vDQA >A\u0002}\f1a\\8t!\u0011\t\t!a\u0003\u000e\u0005\u0005\r!\u0002BA\u0003\u0003\u000f\t!![8\u000b\u0005\u0005%\u0011\u0001\u00026bm\u0006LA!!\u0004\u0002\u0004\t\u0011rJ\u00196fGR|U\u000f\u001e9viN#(/Z1nQ\u0015Q\u0018\u0011CA\u000f!\u0015a\u00111CA\f\u0013\r\t)\"\u0004\u0002\u0007i\"\u0014xn^:\u0011\t\u0005\u0005\u0011\u0011D\u0005\u0005\u00037\t\u0019AA\u0006J\u001f\u0016C8-\u001a9uS>t7EAA\f\u0001"
)
public class ZippedPartitionsPartition implements Partition {
   public final int org$apache$spark$rdd$ZippedPartitionsPartition$$idx;
   private final transient Seq<RDD<?>> org$apache$spark$rdd$ZippedPartitionsPartition$$rdds;
   private final transient Seq<String> preferredLocations;
   private final int index;
   private Seq<Partition> partitionValues;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int hashCode() {
      return Partition$class.hashCode(this);
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public Seq<RDD<?>> org$apache$spark$rdd$ZippedPartitionsPartition$$rdds() {
      return this.org$apache$spark$rdd$ZippedPartitionsPartition$$rdds;
   }

   public Seq<String> preferredLocations() {
      return this.preferredLocations;
   }

   public int index() {
      return this.index;
   }

   public Seq<Partition> partitionValues() {
      return this.partitionValues;
   }

   public void partitionValues_$eq(Seq<Partition> x$1) {
      this.partitionValues = x$1;
   }

   public Seq<Partition> partitions() {
      return this.partitionValues();
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ZippedPartitionsPartition $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.partitionValues_$eq((Seq)this.$outer.org$apache$spark$rdd$ZippedPartitionsPartition$$rdds().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Partition apply(RDD<?> rdd) {
                  return rdd.partitions()[this.$outer.org$apache$spark$rdd$ZippedPartitionsPartition$$anonfun$$$outer().org$apache$spark$rdd$ZippedPartitionsPartition$$idx];
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom()));
            this.oos$1.defaultWriteObject();
         }

         // $FF: synthetic method
         public ZippedPartitionsPartition org$apache$spark$rdd$ZippedPartitionsPartition$$anonfun$$$outer() {
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

   public ZippedPartitionsPartition(int idx, Seq<RDD<?>> rdds, Seq<String> preferredLocations) {
      this.org$apache$spark$rdd$ZippedPartitionsPartition$$idx = idx;
      this.org$apache$spark$rdd$ZippedPartitionsPartition$$rdds = rdds;
      this.preferredLocations = preferredLocations;
      Partition$class.$init$(this);
      this.index = idx;
      this.partitionValues = (Seq)rdds.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ZippedPartitionsPartition $outer;

         public final Partition apply(RDD<?> rdd) {
            return rdd.partitions()[this.$outer.org$apache$spark$rdd$ZippedPartitionsPartition$$idx];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }
}
