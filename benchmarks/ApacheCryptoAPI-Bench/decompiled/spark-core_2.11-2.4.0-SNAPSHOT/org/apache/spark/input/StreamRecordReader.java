package org.apache.spark.input;

import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001q2Q!\u0001\u0002\u0001\t)\u0011!c\u0015;sK\u0006l'+Z2pe\u0012\u0014V-\u00193fe*\u00111\u0001B\u0001\u0006S:\u0004X\u000f\u001e\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0004\u00195yQ\"\u0001\u0002\n\u00059\u0011!aF*ue\u0016\fWNQ1tK\u0012\u0014VmY8sIJ+\u0017\rZ3s!\ta\u0001#\u0003\u0002\u0012\u0005\t\u0011\u0002k\u001c:uC\ndW\rR1uCN#(/Z1n\u0011!\u0019\u0002A!A!\u0002\u0013)\u0012!B:qY&$8\u0001\u0001\t\u0003-yi\u0011a\u0006\u0006\u0003\u0007aQ!!\u0007\u000e\u0002\u00071L'M\u0003\u0002\u001c9\u0005IQ.\u00199sK\u0012,8-\u001a\u0006\u0003;\u0019\ta\u0001[1e_>\u0004\u0018BA\u0010\u0018\u0005A\u0019u.\u001c2j]\u00164\u0015\u000e\\3Ta2LG\u000f\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003#\u0003\u001d\u0019wN\u001c;fqR\u0004\"a\t\u0013\u000e\u0003iI!!\n\u000e\u0003%Q\u000b7o[!ui\u0016l\u0007\u000f^\"p]R,\u0007\u0010\u001e\u0005\tO\u0001\u0011\t\u0011)A\u0005Q\u0005)\u0011N\u001c3fqB\u0011\u0011FL\u0007\u0002U)\u00111\u0006L\u0001\u0005Y\u0006twMC\u0001.\u0003\u0011Q\u0017M^1\n\u0005=R#aB%oi\u0016<WM\u001d\u0005\u0006c\u0001!\tAM\u0001\u0007y%t\u0017\u000e\u001e \u0015\tM\"TG\u000e\t\u0003\u0019\u0001AQa\u0005\u0019A\u0002UAQ!\t\u0019A\u0002\tBQa\n\u0019A\u0002!BQ\u0001\u000f\u0001\u0005\u0002e\n1\u0002]1sg\u0016\u001cFO]3b[R\u0011qB\u000f\u0005\u0006w]\u0002\raD\u0001\tS:\u001cFO]3b[\u0002"
)
public class StreamRecordReader extends StreamBasedRecordReader<PortableDataStream> {
   public PortableDataStream parseStream(PortableDataStream inStream) {
      return inStream;
   }

   public StreamRecordReader(CombineFileSplit split, TaskAttemptContext context, Integer index) {
      super(split, context, index);
   }
}
