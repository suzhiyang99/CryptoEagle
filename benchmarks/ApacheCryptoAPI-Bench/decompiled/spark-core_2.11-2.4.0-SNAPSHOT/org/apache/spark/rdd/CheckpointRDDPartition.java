package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00112Q!\u0001\u0002\u0001\t)\u0011ac\u00115fG.\u0004x.\u001b8u%\u0012#\u0005+\u0019:uSRLwN\u001c\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\u0011I!\u0001\u0006\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\u0002\u0003\f\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u000b%tG-\u001a=\u0004\u0001U\t\u0011\u0004\u0005\u0002\r5%\u00111$\u0004\u0002\u0004\u0013:$\b\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\r%tG-\u001a=!\u0011\u0015y\u0002\u0001\"\u0001!\u0003\u0019a\u0014N\\5u}Q\u0011\u0011e\t\t\u0003E\u0001i\u0011A\u0001\u0005\u0006-y\u0001\r!\u0007"
)
public class CheckpointRDDPartition implements Partition {
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

   public int index() {
      return this.index;
   }

   public CheckpointRDDPartition(int index) {
      this.index = index;
      Partition$class.$init$(this);
   }
}
