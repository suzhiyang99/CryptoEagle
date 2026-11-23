package org.apache.spark.api.python;

import org.apache.spark.Partitioner;
import org.apache.spark.util.Utils$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001}2Q!\u0001\u0002\u0001\r1\u0011\u0011\u0003U=uQ>t\u0007+\u0019:uSRLwN\\3s\u0015\t\u0019A!\u0001\u0004qsRDwN\u001c\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0007\u0013\t\u0001bAA\u0006QCJ$\u0018\u000e^5p]\u0016\u0014\b\u0002\u0003\n\u0001\u0005\u000b\u0007I\u0011\t\u000b\u0002\u001b9,X\u000eU1si&$\u0018n\u001c8t\u0007\u0001)\u0012!\u0006\t\u0003-ei\u0011a\u0006\u0006\u00021\u0005)1oY1mC&\u0011!d\u0006\u0002\u0004\u0013:$\b\u0002\u0003\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u001d9,X\u000eU1si&$\u0018n\u001c8tA!Aa\u0004\u0001BC\u0002\u0013\u0005q$A\u000bqsB\u000b'\u000f^5uS>tg)\u001e8di&|g.\u00133\u0016\u0003\u0001\u0002\"AF\u0011\n\u0005\t:\"\u0001\u0002'p]\u001eD\u0001\u0002\n\u0001\u0003\u0002\u0003\u0006I\u0001I\u0001\u0017af\u0004\u0016M\u001d;ji&|gNR;oGRLwN\\%eA!)a\u0005\u0001C\u0001O\u00051A(\u001b8jiz\"2\u0001\u000b\u0016,!\tI\u0003!D\u0001\u0003\u0011\u0015\u0011R\u00051\u0001\u0016\u0011\u0015qR\u00051\u0001!\u0011\u0015i\u0003\u0001\"\u0011/\u000319W\r\u001e)beRLG/[8o)\t)r\u0006C\u00031Y\u0001\u0007\u0011'A\u0002lKf\u0004\"A\u0006\u001a\n\u0005M:\"aA!os\")Q\u0007\u0001C!m\u00051Q-];bYN$\"a\u000e\u001e\u0011\u0005YA\u0014BA\u001d\u0018\u0005\u001d\u0011un\u001c7fC:DQa\u000f\u001bA\u0002E\nQa\u001c;iKJDQ!\u0010\u0001\u0005By\n\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002+\u0001"
)
public class PythonPartitioner extends Partitioner {
   private final int numPartitions;
   private final long pyPartitionFunctionId;

   public int numPartitions() {
      return this.numPartitions;
   }

   public long pyPartitionFunctionId() {
      return this.pyPartitionFunctionId;
   }

   public int getPartition(Object key) {
      int var3;
      if (key == null) {
         var3 = 0;
      } else if (key instanceof Long) {
         long var4 = BoxesRunTime.unboxToLong(key);
         var3 = Utils$.MODULE$.nonNegativeMod((int)var4, this.numPartitions());
      } else {
         var3 = Utils$.MODULE$.nonNegativeMod(key.hashCode(), this.numPartitions());
      }

      return var3;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof PythonPartitioner) {
         PythonPartitioner var3 = (PythonPartitioner)other;
         var4 = var3.numPartitions() == this.numPartitions() && var3.pyPartitionFunctionId() == this.pyPartitionFunctionId();
      } else {
         var4 = false;
      }

      return var4;
   }

   public int hashCode() {
      return 31 * this.numPartitions() + BoxesRunTime.boxToLong(this.pyPartitionFunctionId()).hashCode();
   }

   public PythonPartitioner(int numPartitions, long pyPartitionFunctionId) {
      this.numPartitions = numPartitions;
      this.pyPartitionFunctionId = pyPartitionFunctionId;
   }
}
