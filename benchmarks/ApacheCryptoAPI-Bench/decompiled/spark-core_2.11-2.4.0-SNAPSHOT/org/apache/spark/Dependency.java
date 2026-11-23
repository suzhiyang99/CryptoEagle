package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001M2Q!\u0001\u0002\u0002\u0002%\u0011!\u0002R3qK:$WM\\2z\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\u000b\u0003\u0015m\u00192\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011ABE\u0005\u0003'5\u0011AbU3sS\u0006d\u0017N_1cY\u0016DQ!\u0006\u0001\u0005\u0002Y\ta\u0001P5oSRtD#A\f\u0011\u0007a\u0001\u0011$D\u0001\u0003!\tQ2\u0004\u0004\u0001\u0005\u000bq\u0001!\u0019A\u000f\u0003\u0003Q\u000b\"AH\u0011\u0011\u00051y\u0012B\u0001\u0011\u000e\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001\u0004\u0012\n\u0005\rj!aA!os\")Q\u0005\u0001D\u0001M\u0005\u0019!\u000f\u001a3\u0016\u0003\u001d\u00022\u0001\u000b\u0016\u001a\u001b\u0005I#BA\u0013\u0003\u0013\tY\u0013FA\u0002S\t\u0012C#\u0001A\u0017\u0011\u00059\nT\"A\u0018\u000b\u0005A\u0012\u0011AC1o]>$\u0018\r^5p]&\u0011!g\f\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public abstract class Dependency<T> implements Serializable {
   public abstract RDD<T> rdd();
}
