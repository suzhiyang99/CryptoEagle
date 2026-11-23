package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]a!B\u0001\u0003\u0001\u0011Q!AD+oS>t\u0007+\u0019:uSRLwN\u001c\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006%'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0003\n\u0005U!!!\u0003)beRLG/[8o\u0011!9\u0002A!A!\u0002\u0013I\u0012aA5eq\u000e\u0001\u0001CA\u0007\u001b\u0013\tYbBA\u0002J]RD\u0001b\u0001\u0001\u0003\u0006\u0004%I!H\u000b\u0002=A\u0019q\u0004\t\u0012\u000e\u0003\tI!!\t\u0002\u0003\u0007I#E\t\u0005\u0002$I1\u0001A!B\u0013\u0001\u0005\u00041#!\u0001+\u0012\u0005\u001dR\u0003CA\u0007)\u0013\tIcBA\u0004O_RD\u0017N\\4\u0011\u00055Y\u0013B\u0001\u0017\u000f\u0005\r\te.\u001f\u0005\t]\u0001\u0011\t\u0011)A\u0005=\u0005!!\u000f\u001a3!Q\ti\u0003\u0007\u0005\u0002\u000ec%\u0011!G\u0004\u0002\niJ\fgn]5f]RD\u0001\u0002\u000e\u0001\u0003\u0006\u0004%\t!N\u0001\u000fa\u0006\u0014XM\u001c;SI\u0012Le\u000eZ3y+\u0005I\u0002\u0002C\u001c\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001fA\f'/\u001a8u%\u0012$\u0017J\u001c3fq\u0002B\u0001\"\u000f\u0001\u0003\u0006\u0004%I!N\u0001\u0018a\u0006\u0014XM\u001c;SI\u0012\u0004\u0016M\u001d;ji&|g.\u00138eKbD\u0001b\u000f\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0019a\u0006\u0014XM\u001c;SI\u0012\u0004\u0016M\u001d;ji&|g.\u00138eKb\u0004\u0003F\u0001\u001e1\u0011!q\u0004AaA!\u0002\u0017y\u0014AC3wS\u0012,gnY3%cA\u0019\u0001i\u0011\u0012\u000e\u0003\u0005S!A\u0011\b\u0002\u000fI,g\r\\3di&\u0011A)\u0011\u0002\t\u00072\f7o\u001d+bO\")a\t\u0001C\u0001\u000f\u00061A(\u001b8jiz\"R\u0001S&M\u001b:#\"!\u0013&\u0011\u0007}\u0001!\u0005C\u0003?\u000b\u0002\u000fq\bC\u0003\u0018\u000b\u0002\u0007\u0011\u0004C\u0003\u0004\u000b\u0002\u0007a\u0004C\u00035\u000b\u0002\u0007\u0011\u0004C\u0003:\u000b\u0002\u0007\u0011\u0004C\u0004Q\u0001\u0001\u0007I\u0011A)\u0002\u001fA\f'/\u001a8u!\u0006\u0014H/\u001b;j_:,\u0012A\u0005\u0005\b'\u0002\u0001\r\u0011\"\u0001U\u0003M\u0001\u0018M]3oiB\u000b'\u000f^5uS>tw\fJ3r)\t)\u0006\f\u0005\u0002\u000e-&\u0011qK\u0004\u0002\u0005+:LG\u000fC\u0004Z%\u0006\u0005\t\u0019\u0001\n\u0002\u0007a$\u0013\u0007\u0003\u0004\\\u0001\u0001\u0006KAE\u0001\u0011a\u0006\u0014XM\u001c;QCJ$\u0018\u000e^5p]\u0002BQ!\u0018\u0001\u0005\u0002y\u000b!\u0003\u001d:fM\u0016\u0014(/\u001a3M_\u000e\fG/[8ogR\tq\fE\u0002aQ.t!!\u00194\u000f\u0005\t,W\"A2\u000b\u0005\u0011D\u0012A\u0002\u001fs_>$h(C\u0001\u0010\u0013\t9g\"A\u0004qC\u000e\\\u0017mZ3\n\u0005%T'aA*fc*\u0011qM\u0004\t\u0003Y>t!!D7\n\u00059t\u0011A\u0002)sK\u0012,g-\u0003\u0002qc\n11\u000b\u001e:j]\u001eT!A\u001c\b\t\u000fM\u0004!\u0019!C!k\u0005)\u0011N\u001c3fq\"1Q\u000f\u0001Q\u0001\ne\ta!\u001b8eKb\u0004\u0003\"B<\u0001\t\u0013A\u0018aC<sSR,wJ\u00196fGR$\"!V=\t\u000bi4\b\u0019A>\u0002\u0007=|7\u000fE\u0002}\u0003\u0007i\u0011! \u0006\u0003}~\f!![8\u000b\u0005\u0005\u0005\u0011\u0001\u00026bm\u0006L1!!\u0002~\u0005Iy%M[3di>+H\u000f];u'R\u0014X-Y7)\u000bY\fI!!\u0006\u0011\u000b5\tY!a\u0004\n\u0007\u00055aB\u0001\u0004uQJ|wo\u001d\t\u0004y\u0006E\u0011bAA\n{\nY\u0011jT#yG\u0016\u0004H/[8oG\t\ty\u0001"
)
public class UnionPartition<T> implements Partition {
   private final transient RDD<T> org$apache$spark$rdd$UnionPartition$$rdd;
   private final int parentRddIndex;
   private final transient int org$apache$spark$rdd$UnionPartition$$parentRddPartitionIndex;
   private Partition parentPartition;
   private final int index;

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

   public RDD<T> org$apache$spark$rdd$UnionPartition$$rdd() {
      return this.org$apache$spark$rdd$UnionPartition$$rdd;
   }

   public int parentRddIndex() {
      return this.parentRddIndex;
   }

   public int org$apache$spark$rdd$UnionPartition$$parentRddPartitionIndex() {
      return this.org$apache$spark$rdd$UnionPartition$$parentRddPartitionIndex;
   }

   public Partition parentPartition() {
      return this.parentPartition;
   }

   public void parentPartition_$eq(Partition x$1) {
      this.parentPartition = x$1;
   }

   public Seq<String> preferredLocations() {
      return this.org$apache$spark$rdd$UnionPartition$$rdd().preferredLocations(this.parentPartition());
   }

   public int index() {
      return this.index;
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final UnionPartition $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.parentPartition_$eq(this.$outer.org$apache$spark$rdd$UnionPartition$$rdd().partitions()[this.$outer.org$apache$spark$rdd$UnionPartition$$parentRddPartitionIndex()]);
            this.oos$1.defaultWriteObject();
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

   public UnionPartition(int idx, RDD<T> rdd, int parentRddIndex, int parentRddPartitionIndex, ClassTag<T> evidence$1) {
      this.org$apache$spark$rdd$UnionPartition$$rdd = rdd;
      this.parentRddIndex = parentRddIndex;
      this.org$apache$spark$rdd$UnionPartition$$parentRddPartitionIndex = parentRddPartitionIndex;
      Partition$class.$init$(this);
      this.parentPartition = rdd.partitions()[parentRddPartitionIndex];
      this.index = idx;
   }
}
