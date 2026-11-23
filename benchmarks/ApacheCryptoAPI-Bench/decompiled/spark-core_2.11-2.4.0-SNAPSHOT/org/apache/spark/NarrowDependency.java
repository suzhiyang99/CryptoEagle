package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u001d3Q!\u0001\u0002\u0002\u0002%\u0011\u0001CT1se><H)\u001a9f]\u0012,gnY=\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001!\u0006\u0002\u000b#M\u0011\u0001a\u0003\t\u0004\u00195yQ\"\u0001\u0002\n\u00059\u0011!A\u0003#fa\u0016tG-\u001a8dsB\u0011\u0001#\u0005\u0007\u0001\t\u0015\u0011\u0002A1\u0001\u0014\u0005\u0005!\u0016C\u0001\u000b\u001b!\t)\u0002$D\u0001\u0017\u0015\u00059\u0012!B:dC2\f\u0017BA\r\u0017\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!F\u000e\n\u0005q1\"aA!os\"Aa\u0004\u0001B\u0001B\u0003%q$\u0001\u0003`e\u0012$\u0007c\u0001\u0011$\u001f5\t\u0011E\u0003\u0002#\u0005\u0005\u0019!\u000f\u001a3\n\u0005\u0011\n#a\u0001*E\t\")a\u0005\u0001C\u0001O\u00051A(\u001b8jiz\"\"\u0001K\u0015\u0011\u00071\u0001q\u0002C\u0003\u001fK\u0001\u0007q\u0004C\u0003,\u0001\u0019\u0005A&\u0001\u0006hKR\u0004\u0016M]3oiN$\"!\f\u001f\u0011\u000792\u0014H\u0004\u00020i9\u0011\u0001gM\u0007\u0002c)\u0011!\u0007C\u0001\u0007yI|w\u000e\u001e \n\u0003]I!!\u000e\f\u0002\u000fA\f7m[1hK&\u0011q\u0007\u000f\u0002\u0004'\u0016\f(BA\u001b\u0017!\t)\"(\u0003\u0002<-\t\u0019\u0011J\u001c;\t\u000buR\u0003\u0019A\u001d\u0002\u0017A\f'\u000f^5uS>t\u0017\n\u001a\u0005\u0006E\u0001!\teP\u000b\u0002?!\u0012\u0001!\u0011\t\u0003\u0005\u0016k\u0011a\u0011\u0006\u0003\t\n\t!\"\u00198o_R\fG/[8o\u0013\t15I\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public abstract class NarrowDependency<T> extends Dependency<T> {
   private final RDD<T> _rdd;

   public abstract Seq<Object> getParents(int var1);

   public RDD<T> rdd() {
      return this._rdd;
   }

   public NarrowDependency(RDD<T> _rdd) {
      this._rdd = _rdd;
   }
}
