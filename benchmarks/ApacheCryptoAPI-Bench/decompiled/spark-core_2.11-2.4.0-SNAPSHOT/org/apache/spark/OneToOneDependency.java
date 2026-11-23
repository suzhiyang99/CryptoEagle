package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.Predef$;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u00113A!\u0001\u0002\u0001\u0013\t\u0011rJ\\3U_>sW\rR3qK:$WM\\2z\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\u000b\u0003\u0015E\u0019\"\u0001A\u0006\u0011\u00071iq\"D\u0001\u0003\u0013\tq!A\u0001\tOCJ\u0014xn\u001e#fa\u0016tG-\u001a8dsB\u0011\u0001#\u0005\u0007\u0001\t\u0015\u0011\u0002A1\u0001\u0014\u0005\u0005!\u0016C\u0001\u000b\u001b!\t)\u0002$D\u0001\u0017\u0015\u00059\u0012!B:dC2\f\u0017BA\r\u0017\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!F\u000e\n\u0005q1\"aA!os\"Aa\u0004\u0001B\u0001B\u0003%q$A\u0002sI\u0012\u00042\u0001\t\u0012\u0010\u001b\u0005\t#B\u0001\u0010\u0003\u0013\t\u0019\u0013EA\u0002S\t\u0012CQ!\n\u0001\u0005\u0002\u0019\na\u0001P5oSRtDCA\u0014)!\ra\u0001a\u0004\u0005\u0006=\u0011\u0002\ra\b\u0005\u0006U\u0001!\teK\u0001\u000bO\u0016$\b+\u0019:f]R\u001cHC\u0001\u0017<!\riS\u0007\u000f\b\u0003]Mr!a\f\u001a\u000e\u0003AR!!\r\u0005\u0002\rq\u0012xn\u001c;?\u0013\u00059\u0012B\u0001\u001b\u0017\u0003\u001d\u0001\u0018mY6bO\u0016L!AN\u001c\u0003\t1K7\u000f\u001e\u0006\u0003iY\u0001\"!F\u001d\n\u0005i2\"aA%oi\")A(\u000ba\u0001q\u0005Y\u0001/\u0019:uSRLwN\\%eQ\t\u0001a\b\u0005\u0002@\u00056\t\u0001I\u0003\u0002B\u0005\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\r\u0003%\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class OneToOneDependency<T> extends NarrowDependency<T> {
   public List<Object> getParents(int partitionId) {
      return List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{partitionId}));
   }

   public OneToOneDependency(RDD<T> rdd) {
      super(rdd);
   }
}
