package org.apache.spark.api.python;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m1Q!\u0001\u0002\u0001\u00051\u00111\u0003R8vE2,\u0017I\u001d:bs^\u0013\u0018\u000e^1cY\u0016T!a\u0001\u0003\u0002\rALH\u000f[8o\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\n\u000e\u0003=Q!\u0001E\t\u0002\u0005%|'B\u0001\n\t\u0003\u0019A\u0017\rZ8pa&\u0011Ac\u0004\u0002\u000e\u0003J\u0014\u0018-_,sSR\f'\r\\3\t\u000bY\u0001A\u0011\u0001\r\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0007\t\u00035\u0001i\u0011A\u0001"
)
public class DoubleArrayWritable extends ArrayWritable {
   public DoubleArrayWritable() {
      super(DoubleWritable.class);
   }
}
