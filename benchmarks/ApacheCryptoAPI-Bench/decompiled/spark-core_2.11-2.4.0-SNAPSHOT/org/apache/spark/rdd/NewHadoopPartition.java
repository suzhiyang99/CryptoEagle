package org.apache.spark.rdd;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.SerializableWritable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M3Q!\u0001\u0002\u0001\t)\u0011!CT3x\u0011\u0006$wn\u001c9QCJ$\u0018\u000e^5p]*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Mi\u0011\u0001B\u0005\u0003)\u0011\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u0011Y\u0001!\u0011!Q\u0001\na\tQA\u001d3e\u0013\u0012\u001c\u0001\u0001\u0005\u0002\r3%\u0011!$\u0004\u0002\u0004\u0013:$\b\u0002\u0003\u000f\u0001\u0005\u000b\u0007I\u0011A\u000f\u0002\u000b%tG-\u001a=\u0016\u0003aA\u0001b\b\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0007S:$W\r\u001f\u0011\t\u0011\u0005\u0002!\u0011!Q\u0001\n\t\n\u0001B]1x'Bd\u0017\u000e\u001e\n\u0004G\u0015jc\u0001\u0002\u0013\u0001\u0001\t\u0012A\u0002\u0010:fM&tW-\\3oiz\u0002\"AJ\u0016\u000e\u0003\u001dR!\u0001K\u0015\u0002\u00135\f\u0007O]3ek\u000e,'B\u0001\u0016\u0007\u0003\u0019A\u0017\rZ8pa&\u0011Af\n\u0002\u000b\u0013:\u0004X\u000f^*qY&$\bC\u0001\u00182\u001b\u0005y#B\u0001\u0019*\u0003\tIw.\u0003\u00023_\tAqK]5uC\ndW\rC\u00035\u0001\u0011\u0005Q'\u0001\u0004=S:LGO\u0010\u000b\u0005maJ$\b\u0005\u00028\u00015\t!\u0001C\u0003\u0017g\u0001\u0007\u0001\u0004C\u0003\u001dg\u0001\u0007\u0001\u0004C\u0003\"g\u0001\u00071HE\u0002=K52A\u0001\n\u0001\u0001w!9a\b\u0001b\u0001\n\u0003y\u0014aF:fe&\fG.\u001b>bE2,\u0007*\u00193p_B\u001c\u0006\u000f\\5u+\u0005\u0001\u0005c\u0001\nBE%\u0011!\t\u0002\u0002\u0015'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.Z,sSR\f'\r\\3\t\r\u0011\u0003\u0001\u0015!\u0003A\u0003a\u0019XM]5bY&T\u0018M\u00197f\u0011\u0006$wn\u001c9Ta2LG\u000f\t\u0005\u0006\r\u0002!\teR\u0001\tQ\u0006\u001c\bnQ8eKR\t\u0001\u0004C\u0003J\u0001\u0011\u0005#*\u0001\u0004fcV\fGn\u001d\u000b\u0003\u0017:\u0003\"\u0001\u0004'\n\u00055k!a\u0002\"p_2,\u0017M\u001c\u0005\u0006\u001f\"\u0003\r\u0001U\u0001\u0006_RDWM\u001d\t\u0003\u0019EK!AU\u0007\u0003\u0007\u0005s\u0017\u0010"
)
public class NewHadoopPartition implements Partition {
   private final int rddId;
   private final int index;
   private final SerializableWritable<InputSplit> serializableHadoopSplit;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int index() {
      return this.index;
   }

   public SerializableWritable<InputSplit> serializableHadoopSplit() {
      return this.serializableHadoopSplit;
   }

   public int hashCode() {
      return 31 * (31 + this.rddId) + this.index();
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public NewHadoopPartition(int rddId, int index, InputSplit rawSplit) {
      this.rddId = rddId;
      this.index = index;
      Partition$class.$init$(this);
      this.serializableHadoopSplit = new SerializableWritable<InputSplit>((Writable)rawSplit);
   }
}
