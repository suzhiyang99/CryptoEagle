package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.Predef$;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u000153A!\u0001\u0002\u0001\u0013\ty!+\u00198hK\u0012+\u0007/\u001a8eK:\u001c\u0017P\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001)\"AC\t\u0014\u0005\u0001Y\u0001c\u0001\u0007\u000e\u001f5\t!!\u0003\u0002\u000f\u0005\t\u0001b*\u0019:s_^$U\r]3oI\u0016t7-\u001f\t\u0003!Ea\u0001\u0001B\u0003\u0013\u0001\t\u00071CA\u0001U#\t!\"\u0004\u0005\u0002\u001615\taCC\u0001\u0018\u0003\u0015\u00198-\u00197b\u0013\tIbCA\u0004O_RD\u0017N\\4\u0011\u0005UY\u0012B\u0001\u000f\u0017\u0005\r\te.\u001f\u0005\t=\u0001\u0011\t\u0011)A\u0005?\u0005\u0019!\u000f\u001a3\u0011\u0007\u0001\u0012s\"D\u0001\"\u0015\tq\"!\u0003\u0002$C\t\u0019!\u000b\u0012#\t\u0011\u0015\u0002!\u0011!Q\u0001\n\u0019\nq!\u001b8Ti\u0006\u0014H\u000f\u0005\u0002\u0016O%\u0011\u0001F\u0006\u0002\u0004\u0013:$\b\u0002\u0003\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u0011=,Ho\u0015;beRD\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0007Y\u0016tw\r\u001e5\t\u000b9\u0002A\u0011A\u0018\u0002\rqJg.\u001b;?)\u0015\u0001\u0014GM\u001a5!\ra\u0001a\u0004\u0005\u0006=5\u0002\ra\b\u0005\u0006K5\u0002\rA\n\u0005\u0006U5\u0002\rA\n\u0005\u0006Y5\u0002\rA\n\u0005\u0006m\u0001!\teN\u0001\u000bO\u0016$\b+\u0019:f]R\u001cHC\u0001\u001dE!\rI\u0014I\n\b\u0003u}r!a\u000f \u000e\u0003qR!!\u0010\u0005\u0002\rq\u0012xn\u001c;?\u0013\u00059\u0012B\u0001!\u0017\u0003\u001d\u0001\u0018mY6bO\u0016L!AQ\"\u0003\t1K7\u000f\u001e\u0006\u0003\u0001ZAQ!R\u001bA\u0002\u0019\n1\u0002]1si&$\u0018n\u001c8JI\"\u0012\u0001a\u0012\t\u0003\u0011.k\u0011!\u0013\u0006\u0003\u0015\n\t!\"\u00198o_R\fG/[8o\u0013\ta\u0015J\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class RangeDependency<T> extends NarrowDependency<T> {
   private final int inStart;
   private final int outStart;
   private final int length;

   public List<Object> getParents(int partitionId) {
      return (List<Object>)(partitionId >= this.outStart && partitionId < this.outStart + this.length ? List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{partitionId - this.outStart + this.inStart})) : Nil$.MODULE$);
   }

   public RangeDependency(RDD<T> rdd, int inStart, int outStart, int length) {
      super(rdd);
      this.inStart = inStart;
      this.outStart = outStart;
      this.length = length;
   }
}
