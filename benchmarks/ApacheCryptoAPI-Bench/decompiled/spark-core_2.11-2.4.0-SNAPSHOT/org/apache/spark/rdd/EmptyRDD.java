package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Array$;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M3Q!\u0001\u0002\u0001\t)\u0011\u0001\"R7qif\u0014F\t\u0012\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\u0013'\t\u0001A\u0002E\u0002\u000e\u001dAi\u0011AA\u0005\u0003\u001f\t\u00111A\u0015#E!\t\t\"\u0003\u0004\u0001\u0005\u000bM\u0001!\u0019A\u000b\u0003\u0003Q\u001b\u0001!\u0005\u0002\u00179A\u0011qCG\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t9aj\u001c;iS:<\u0007CA\f\u001e\u0013\tq\u0002DA\u0002B]fD\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0003g\u000e\u0004\"AI\u0012\u000e\u0003\u0011I!\u0001\n\u0003\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\t\u0011\u0019\u0002!1!Q\u0001\f\u001d\n!\"\u001a<jI\u0016t7-\u001a\u00132!\rA3\u0006E\u0007\u0002S)\u0011!\u0006G\u0001\be\u00164G.Z2u\u0013\ta\u0013F\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u0015q\u0003\u0001\"\u00010\u0003\u0019a\u0014N\\5u}Q\u0011\u0001g\r\u000b\u0003cI\u00022!\u0004\u0001\u0011\u0011\u00151S\u0006q\u0001(\u0011\u0015\u0001S\u00061\u0001\"\u0011\u0015)\u0004\u0001\"\u00117\u000359W\r\u001e)beRLG/[8ogV\tq\u0007E\u0002\u0018qiJ!!\u000f\r\u0003\u000b\u0005\u0013(/Y=\u0011\u0005\tZ\u0014B\u0001\u001f\u0005\u0005%\u0001\u0016M\u001d;ji&|g\u000eC\u0003?\u0001\u0011\u0005s(A\u0004d_6\u0004X\u000f^3\u0015\u0007\u0001ce\nE\u0002B\u0013Bq!AQ$\u000f\u0005\r3U\"\u0001#\u000b\u0005\u0015#\u0012A\u0002\u001fs_>$h(C\u0001\u001a\u0013\tA\u0005$A\u0004qC\u000e\\\u0017mZ3\n\u0005)[%\u0001C%uKJ\fGo\u001c:\u000b\u0005!C\u0002\"B'>\u0001\u0004Q\u0014!B:qY&$\b\"B(>\u0001\u0004\u0001\u0016aB2p]R,\u0007\u0010\u001e\t\u0003EEK!A\u0015\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e"
)
public class EmptyRDD<T> extends RDD<T> {
   public Partition[] getPartitions() {
      return (Partition[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(Partition.class));
   }

   public Iterator<T> compute(Partition split, TaskContext context) {
      throw new UnsupportedOperationException("empty RDD");
   }

   public EmptyRDD(SparkContext sc, ClassTag<T> evidence$1) {
      super(sc, Nil$.MODULE$, evidence$1);
   }
}
