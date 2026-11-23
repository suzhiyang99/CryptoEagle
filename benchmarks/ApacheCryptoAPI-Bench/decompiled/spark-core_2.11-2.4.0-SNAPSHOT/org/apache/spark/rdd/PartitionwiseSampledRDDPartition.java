package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2Q!\u0001\u0002\u0001\t)\u0011\u0001\u0005U1si&$\u0018n\u001c8xSN,7+Y7qY\u0016$'\u000b\u0012#QCJ$\u0018\u000e^5p]*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011#\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0003\n\u0005Q!!!\u0003)beRLG/[8o!\taa#\u0003\u0002\u0018\u001b\ta1+\u001a:jC2L'0\u00192mK\"A\u0011\u0004\u0001BC\u0002\u0013\u00051$\u0001\u0003qe\u001648\u0001A\u000b\u0002#!AQ\u0004\u0001B\u0001B\u0003%\u0011#A\u0003qe\u00164\b\u0005\u0003\u0005 \u0001\t\u0015\r\u0011\"\u0001!\u0003\u0011\u0019X-\u001a3\u0016\u0003\u0005\u0002\"\u0001\u0004\u0012\n\u0005\rj!\u0001\u0002'p]\u001eD\u0001\"\n\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0006g\u0016,G\r\t\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%ZC\u0006\u0005\u0002+\u00015\t!\u0001C\u0003\u001aM\u0001\u0007\u0011\u0003C\u0003 M\u0001\u0007\u0011\u0005C\u0004/\u0001\t\u0007I\u0011I\u0018\u0002\u000b%tG-\u001a=\u0016\u0003A\u0002\"\u0001D\u0019\n\u0005Ij!aA%oi\"1A\u0007\u0001Q\u0001\nA\na!\u001b8eKb\u0004\u0003"
)
public class PartitionwiseSampledRDDPartition implements Partition {
   private final Partition prev;
   private final long seed;
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

   public Partition prev() {
      return this.prev;
   }

   public long seed() {
      return this.seed;
   }

   public int index() {
      return this.index;
   }

   public PartitionwiseSampledRDDPartition(Partition prev, long seed) {
      this.prev = prev;
      this.seed = seed;
      Partition$class.$init$(this);
      this.index = prev.index();
   }
}
