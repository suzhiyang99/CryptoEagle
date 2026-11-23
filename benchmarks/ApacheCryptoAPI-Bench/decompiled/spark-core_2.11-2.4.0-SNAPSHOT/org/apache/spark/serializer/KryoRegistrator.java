package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r2q!\u0001\u0002\u0011\u0002G\u00051BA\bLef|'+Z4jgR\u0014\u0018\r^8s\u0015\t\u0019A!\u0001\u0006tKJL\u0017\r\\5{KJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\u0005\u0006'\u00011\t\u0001F\u0001\u0010e\u0016<\u0017n\u001d;fe\u000ec\u0017m]:fgR\u0011Q\u0003\u0007\t\u0003\u001bYI!a\u0006\b\u0003\tUs\u0017\u000e\u001e\u0005\u00063I\u0001\rAG\u0001\u0005WJLx\u000e\u0005\u0002\u001cC5\tAD\u0003\u0002\u001a;)\u0011adH\u0001\u0011KN|G/\u001a:jGN|g\r^<be\u0016T\u0011\u0001I\u0001\u0004G>l\u0017B\u0001\u0012\u001d\u0005\u0011Y%/_8"
)
public interface KryoRegistrator {
   void registerClasses(Kryo var1);
}
