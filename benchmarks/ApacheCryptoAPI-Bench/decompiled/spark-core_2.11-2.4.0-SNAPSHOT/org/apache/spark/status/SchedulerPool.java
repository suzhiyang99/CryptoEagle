package org.apache.spark.status;

import scala.Predef$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}2A!\u0001\u0002\u0005\u0017\ti1k\u00195fIVdWM\u001d)p_2T!a\u0001\u0003\u0002\rM$\u0018\r^;t\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\b\u000e\u0003\tI!a\u0004\u0002\u0003\u00151Kg/Z#oi&$\u0018\u0010\u0003\u0005\u0012\u0001\t\u0005\t\u0015!\u0003\u0013\u0003\u0011q\u0017-\\3\u0011\u0005MIbB\u0001\u000b\u0018\u001b\u0005)\"\"\u0001\f\u0002\u000bM\u001c\u0017\r\\1\n\u0005a)\u0012A\u0002)sK\u0012,g-\u0003\u0002\u001b7\t11\u000b\u001e:j]\u001eT!\u0001G\u000b\t\u000bu\u0001A\u0011\u0001\u0010\u0002\rqJg.\u001b;?)\ty\u0002\u0005\u0005\u0002\u000e\u0001!)\u0011\u0003\ba\u0001%!9!\u0005\u0001a\u0001\n\u0003\u0019\u0013\u0001C:uC\u001e,\u0017\nZ:\u0016\u0003\u0011\u00022!\n\u0016-\u001b\u00051#BA\u0014)\u0003%IW.\\;uC\ndWM\u0003\u0002*+\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005-2#aA*fiB\u0011A#L\u0005\u0003]U\u00111!\u00138u\u0011\u001d\u0001\u0004\u00011A\u0005\u0002E\nAb\u001d;bO\u0016LEm]0%KF$\"AM\u001b\u0011\u0005Q\u0019\u0014B\u0001\u001b\u0016\u0005\u0011)f.\u001b;\t\u000fYz\u0013\u0011!a\u0001I\u0005\u0019\u0001\u0010J\u0019\t\ra\u0002\u0001\u0015)\u0003%\u0003%\u0019H/Y4f\u0013\u0012\u001c\b\u0005C\u0003;\u0001\u0011E3(\u0001\u0005e_V\u0003H-\u0019;f)\u0005a\u0004C\u0001\u000b>\u0013\tqTCA\u0002B]f\u0004"
)
public class SchedulerPool extends LiveEntity {
   private final String name;
   private Set<Object> stageIds;

   public Set<Object> stageIds() {
      return this.stageIds;
   }

   public void stageIds_$eq(Set<Object> x$1) {
      this.stageIds = x$1;
   }

   public Object doUpdate() {
      return new PoolData(this.name, this.stageIds());
   }

   public SchedulerPool(String name) {
      this.name = name;
      this.stageIds = (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$);
   }
}
