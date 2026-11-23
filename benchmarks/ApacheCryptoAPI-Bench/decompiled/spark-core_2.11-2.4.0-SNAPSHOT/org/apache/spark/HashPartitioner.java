package org.apache.spark;

import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001E2A!\u0001\u0002\u0001\u0013\ty\u0001*Y:i!\u0006\u0014H/\u001b;j_:,'O\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0006\u0011\u0005-aQ\"\u0001\u0002\n\u00055\u0011!a\u0003)beRLG/[8oKJD\u0001b\u0004\u0001\u0003\u0002\u0003\u0006I\u0001E\u0001\u000ba\u0006\u0014H/\u001b;j_:\u001c\bCA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"aA%oi\")q\u0003\u0001C\u00011\u00051A(\u001b8jiz\"\"!\u0007\u000e\u0011\u0005-\u0001\u0001\"B\b\u0017\u0001\u0004\u0001\u0002\"\u0002\u000f\u0001\t\u0003i\u0012!\u00048v[B\u000b'\u000f^5uS>t7/F\u0001\u0011\u0011\u0015y\u0002\u0001\"\u0001!\u000319W\r\u001e)beRLG/[8o)\t\u0001\u0012\u0005C\u0003#=\u0001\u00071%A\u0002lKf\u0004\"!\u0005\u0013\n\u0005\u0015\u0012\"aA!os\")q\u0005\u0001C!Q\u00051Q-];bYN$\"!\u000b\u0017\u0011\u0005EQ\u0013BA\u0016\u0013\u0005\u001d\u0011un\u001c7fC:DQ!\f\u0014A\u0002\r\nQa\u001c;iKJDQa\f\u0001\u0005BA\n\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002!\u0001"
)
public class HashPartitioner extends Partitioner {
   public final int org$apache$spark$HashPartitioner$$partitions;

   public int numPartitions() {
      return this.org$apache$spark$HashPartitioner$$partitions;
   }

   public int getPartition(Object key) {
      int var3;
      if (key == null) {
         var3 = 0;
      } else {
         var3 = Utils$.MODULE$.nonNegativeMod(key.hashCode(), this.numPartitions());
      }

      return var3;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof HashPartitioner) {
         HashPartitioner var3 = (HashPartitioner)other;
         var4 = var3.numPartitions() == this.numPartitions();
      } else {
         var4 = false;
      }

      return var4;
   }

   public int hashCode() {
      return this.numPartitions();
   }

   public HashPartitioner(int partitions) {
      this.org$apache$spark$HashPartitioner$$partitions = partitions;
      Predef$.MODULE$.require(partitions >= 0, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HashPartitioner $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Number of partitions (", ") cannot be negative."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$HashPartitioner$$partitions)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }
}
