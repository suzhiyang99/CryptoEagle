package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}3a!\u0001\u0002\u0002\u0002\u0011Q!!D\"iK\u000e\\\u0007o\\5oiJ#EI\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,\"a\u0003\n\u0014\u0005\u0001a\u0001cA\u0007\u000f!5\t!!\u0003\u0002\u0010\u0005\t\u0019!\u000b\u0012#\u0011\u0005E\u0011B\u0002\u0001\u0003\u0006'\u0001\u0011\r!\u0006\u0002\u0002)\u000e\u0001\u0011C\u0001\f\u001d!\t9\"$D\u0001\u0019\u0015\u0005I\u0012!B:dC2\f\u0017BA\u000e\u0019\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"aF\u000f\n\u0005yA\"aA!os\"A\u0001\u0005\u0001B\u0001B\u0003%\u0011%\u0001\u0002tGB\u0011!eI\u0007\u0002\t%\u0011A\u0005\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\tM\u0001\u0011\u0019\u0011)A\u0006O\u0005QQM^5eK:\u001cW\rJ\u0019\u0011\u0007!Z\u0003#D\u0001*\u0015\tQ\u0003$A\u0004sK\u001adWm\u0019;\n\u00051J#\u0001C\"mCN\u001cH+Y4\t\u000b9\u0002A\u0011A\u0018\u0002\rqJg.\u001b;?)\t\u00014\u0007\u0006\u00022eA\u0019Q\u0002\u0001\t\t\u000b\u0019j\u00039A\u0014\t\u000b\u0001j\u0003\u0019A\u0011\t\u000bU\u0002A\u0011\t\u001c\u0002\u0019\u0011|7\t[3dWB|\u0017N\u001c;\u0015\u0003]\u0002\"a\u0006\u001d\n\u0005eB\"\u0001B+oSRDQa\u000f\u0001\u0005BY\n!b\u00195fG.\u0004x.\u001b8u\u0011\u0015i\u0004\u0001\"\u0011?\u0003=awnY1m\u0007\",7m\u001b9pS:$H#A \u000e\u0003\u0001AQ!\u0011\u0001\u0005R\t\u000bQbZ3u!\u0006\u0014H/\u001b;j_:\u001cX#A\"\u0011\u0007]!e)\u0003\u0002F1\t)\u0011I\u001d:bsB\u0011!eR\u0005\u0003\u0011\u0012\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u000b)\u0003A\u0011I&\u0002\u000f\r|W\u000e];uKR\u0019A\n\u0017.\u0011\u00075+\u0006C\u0004\u0002O':\u0011qJU\u0007\u0002!*\u0011\u0011\u000bF\u0001\u0007yI|w\u000e\u001e \n\u0003eI!\u0001\u0016\r\u0002\u000fA\f7m[1hK&\u0011ak\u0016\u0002\t\u0013R,'/\u0019;pe*\u0011A\u000b\u0007\u0005\u00063&\u0003\rAR\u0001\u0002a\")1,\u0013a\u00019\u0006\u0011Ao\u0019\t\u0003EuK!A\u0018\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e"
)
public abstract class CheckpointRDD<T> extends RDD<T> {
   public void doCheckpoint() {
   }

   public void checkpoint() {
   }

   public CheckpointRDD<T> localCheckpoint() {
      return this;
   }

   public Partition[] getPartitions() {
      throw Predef$.MODULE$.$qmark$qmark$qmark();
   }

   public Iterator<T> compute(Partition p, TaskContext tc) {
      throw Predef$.MODULE$.$qmark$qmark$qmark();
   }

   public CheckpointRDD(SparkContext sc, ClassTag<T> evidence$1) {
      super(sc, Nil$.MODULE$, evidence$1);
   }
}
