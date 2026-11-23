package org.apache.spark.api.r;

import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015b!B\u0001\u0003\u0003\u0013i!\u0001\u0003\"bg\u0016\u0014&\u000b\u0012#\u000b\u0005\r!\u0011!\u0001:\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00079iscE\u0002\u0001\u001f\r\u00022\u0001E\n\u0016\u001b\u0005\t\"B\u0001\n\u0007\u0003\r\u0011H\rZ\u0005\u0003)E\u00111A\u0015#E!\t1r\u0003\u0004\u0001\u0005\u000ba\u0001!\u0019A\r\u0003\u0003U\u000b\"A\u0007\u0011\u0011\u0005mqR\"\u0001\u000f\u000b\u0003u\tQa]2bY\u0006L!a\b\u000f\u0003\u000f9{G\u000f[5oOB\u00111$I\u0005\u0003Eq\u00111!\u00118z!\t!s%D\u0001&\u0015\t1c!\u0001\u0005j]R,'O\\1m\u0013\tASEA\u0004M_\u001e<\u0017N\\4\t\u0011)\u0002!\u0011!Q\u0001\n-\na\u0001]1sK:$\bc\u0001\t\u0014YA\u0011a#\f\u0003\u0006]\u0001\u0011\r!\u0007\u0002\u0002)\"A\u0001\u0007\u0001B\u0001B\u0003%\u0011'A\u0007ok6\u0004\u0016M\u001d;ji&|gn\u001d\t\u00037IJ!a\r\u000f\u0003\u0007%sG\u000f\u0003\u00056\u0001\t\u0005\t\u0015!\u00037\u0003\u00111WO\\2\u0011\u0007m9\u0014(\u0003\u000299\t)\u0011I\u001d:bsB\u00111DO\u0005\u0003wq\u0011AAQ=uK\"AQ\b\u0001B\u0001B\u0003%a(\u0001\u0007eKN,'/[1mSj,'\u000f\u0005\u0002@\u0005:\u00111\u0004Q\u0005\u0003\u0003r\ta\u0001\u0015:fI\u00164\u0017BA\"E\u0005\u0019\u0019FO]5oO*\u0011\u0011\t\b\u0005\t\r\u0002\u0011\t\u0011)A\u0005}\u0005Q1/\u001a:jC2L'0\u001a:\t\u0011!\u0003!\u0011!Q\u0001\nY\nA\u0002]1dW\u0006<WMT1nKND\u0001B\u0013\u0001\u0003\u0002\u0003\u0006IaS\u0001\u000eEJ|\u0017\rZ2bgR4\u0016M]:\u0011\u0007m9D\nE\u0002N!Jk\u0011A\u0014\u0006\u0003\u001f\u001a\t\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\n\u0005Es%!\u0003\"s_\u0006$7-Y:u!\t\u0019\u0006,D\u0001U\u0015\t)f+\u0001\u0003mC:<'\"A,\u0002\t)\fg/Y\u0005\u00033R\u0013aa\u00142kK\u000e$\b\u0002C.\u0001\u0005\u0007\u0005\u000b1\u0002/\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002^A2j\u0011A\u0018\u0006\u0003?r\tqA]3gY\u0016\u001cG/\u0003\u0002b=\nA1\t\\1tgR\u000bw\r\u0003\u0005d\u0001\t\r\t\u0015a\u0003e\u0003))g/\u001b3f]\u000e,GE\r\t\u0004;\u0002,\u0002\"\u00024\u0001\t\u00039\u0017A\u0002\u001fj]&$h\b\u0006\u0005i[:|\u0007/\u001d:t)\rI7\u000e\u001c\t\u0005U\u0002aS#D\u0001\u0003\u0011\u0015YV\rq\u0001]\u0011\u0015\u0019W\rq\u0001e\u0011\u0015QS\r1\u0001,\u0011\u0015\u0001T\r1\u00012\u0011\u0015)T\r1\u00017\u0011\u0015iT\r1\u0001?\u0011\u00151U\r1\u0001?\u0011\u0015AU\r1\u00017\u0011\u0015QU\r1\u0001L\u0011\u0015)\b\u0001\"\u0011w\u000359W\r\u001e)beRLG/[8ogV\tq\u000fE\u0002\u001coa\u0004\"!\u001f>\u000e\u0003\u0019I!a\u001f\u0004\u0003\u0013A\u000b'\u000f^5uS>t\u0007\"B?\u0001\t\u0003r\u0018aB2p[B,H/\u001a\u000b\u0006\u007f\u0006]\u00111\u0004\t\u0006\u0003\u0003\t\t\"\u0006\b\u0005\u0003\u0007\tiA\u0004\u0003\u0002\u0006\u0005-QBAA\u0004\u0015\r\tI\u0001D\u0001\u0007yI|w\u000e\u001e \n\u0003uI1!a\u0004\u001d\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\u0005\u0002\u0016\tA\u0011\n^3sCR|'OC\u0002\u0002\u0010qAa!!\u0007}\u0001\u0004A\u0018!\u00039beRLG/[8o\u0011\u001d\ti\u0002 a\u0001\u0003?\tqaY8oi\u0016DH\u000fE\u0002z\u0003CI1!a\t\u0007\u0005-!\u0016m]6D_:$X\r\u001f;"
)
public abstract class BaseRRDD<T, U> extends RDD<U> {
   private final RDD<T> parent;
   private final int numPartitions;
   private final byte[] func;
   private final String deserializer;
   private final String serializer;
   private final byte[] packageNames;
   private final Broadcast<Object>[] broadcastVars;
   private final ClassTag<T> evidence$1;

   public Partition[] getPartitions() {
      return this.parent.partitions();
   }

   public Iterator<U> compute(Partition partition, TaskContext context) {
      RRunner runner = new RRunner(this.func, this.deserializer, this.serializer, this.packageNames, this.broadcastVars, this.numPartitions, RRunner$.MODULE$.$lessinit$greater$default$7(), RRunner$.MODULE$.$lessinit$greater$default$8(), RRunner$.MODULE$.$lessinit$greater$default$9());
      Iterator parentIterator = this.firstParent(this.evidence$1).iterator(partition, context);
      return runner.compute(parentIterator, partition.index());
   }

   public BaseRRDD(RDD<T> parent, int numPartitions, byte[] func, String deserializer, String serializer, byte[] packageNames, Broadcast<Object>[] broadcastVars, ClassTag<T> evidence$1, ClassTag<U> evidence$2) {
      super(parent, evidence$2);
      this.parent = parent;
      this.numPartitions = numPartitions;
      this.func = func;
      this.deserializer = deserializer;
      this.serializer = serializer;
      this.packageNames = packageNames;
      this.broadcastVars = broadcastVars;
      this.evidence$1 = evidence$1;
   }
}
