package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.storage.RDDBlockId;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154Q!\u0001\u0002\u0001\t)\u0011!\u0003T8dC2\u001c\u0005.Z2la>Lg\u000e\u001e*E\t*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-\u00112C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u000e\u0007\",7m\u001b9pS:$(\u000b\u0012#\u0011\u0005E\u0011B\u0002\u0001\u0003\u0006'\u0001\u0011\r!\u0006\u0002\u0002)\u000e\u0001\u0011C\u0001\f\u001d!\t9\"$D\u0001\u0019\u0015\u0005I\u0012!B:dC2\f\u0017BA\u000e\u0019\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"aF\u000f\n\u0005yA\"aA!os\"A\u0001\u0005\u0001B\u0001B\u0003%\u0011%\u0001\u0002tGB\u0011!eI\u0007\u0002\t%\u0011A\u0005\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\tM\u0001\u0011\t\u0011)A\u0005O\u0005)!\u000f\u001a3JIB\u0011q\u0003K\u0005\u0003Sa\u00111!\u00138u\u0011!Y\u0003A!A!\u0002\u00139\u0013!\u00048v[B\u000b'\u000f^5uS>t7\u000f\u0003\u0005.\u0001\t\r\t\u0015a\u0003/\u0003))g/\u001b3f]\u000e,G%\r\t\u0004_I\u0002R\"\u0001\u0019\u000b\u0005EB\u0012a\u0002:fM2,7\r^\u0005\u0003gA\u0012\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006k\u0001!\tAN\u0001\u0007y%t\u0017\u000e\u001e \u0015\t]R4\b\u0010\u000b\u0003qe\u00022!\u0004\u0001\u0011\u0011\u0015iC\u0007q\u0001/\u0011\u0015\u0001C\u00071\u0001\"\u0011\u00151C\u00071\u0001(\u0011\u0015YC\u00071\u0001(\u0011\u0015)\u0004\u0001\"\u0001?)\ty$\t\u0006\u00029\u0001\"9\u0011)PA\u0001\u0002\bq\u0013AC3wS\u0012,gnY3%e!)1!\u0010a\u0001\u0007B\u0019Q\u0002\u0012\t\n\u0005\u0015\u0013!a\u0001*E\t\")q\t\u0001C)\u0011\u0006iq-\u001a;QCJ$\u0018\u000e^5p]N,\u0012!\u0013\t\u0004/)c\u0015BA&\u0019\u0005\u0015\t%O]1z!\t\u0011S*\u0003\u0002O\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\u0006!\u0002!\t%U\u0001\bG>l\u0007/\u001e;f)\r\u0011f\f\u0019\t\u0004'n\u0003bB\u0001+Z\u001d\t)\u0006,D\u0001W\u0015\t9F#\u0001\u0004=e>|GOP\u0005\u00023%\u0011!\fG\u0001\ba\u0006\u001c7.Y4f\u0013\taVL\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\tQ\u0006\u0004C\u0003`\u001f\u0002\u0007A*A\u0005qCJ$\u0018\u000e^5p]\")\u0011m\u0014a\u0001E\u000691m\u001c8uKb$\bC\u0001\u0012d\u0013\t!GAA\u0006UCN\\7i\u001c8uKb$\b"
)
public class LocalCheckpointRDD<T> extends CheckpointRDD<T> {
   private final int rddId;
   private final int numPartitions;

   public Partition[] getPartitions() {
      return (Partition[])Predef$.MODULE$.intArrayOps((int[])RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.numPartitions).toArray(ClassTag$.MODULE$.Int())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final CheckpointRDDPartition apply(int i) {
            return new CheckpointRDDPartition(i);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Partition.class)));
   }

   public Iterator<T> compute(Partition partition, TaskContext context) {
      throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Checkpoint block ", " not found! Either the executor "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{new RDDBlockId(this.rddId, partition.index())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"that originally checkpointed this partition is no longer alive, or the original RDD is "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unpersisted. If this problem persists, you may consider using `rdd.checkpoint()` "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"instead, which is slower than local checkpointing but more fault-tolerant."})))).s(Nil$.MODULE$)).toString());
   }

   public LocalCheckpointRDD(SparkContext sc, int rddId, int numPartitions, ClassTag<T> evidence$1) {
      super(sc, evidence$1);
      this.rddId = rddId;
      this.numPartitions = numPartitions;
   }

   public LocalCheckpointRDD(RDD<T> rdd, ClassTag<T> evidence$2) {
      this(rdd.context(), rdd.id(), rdd.partitions().length, evidence$2);
   }
}
