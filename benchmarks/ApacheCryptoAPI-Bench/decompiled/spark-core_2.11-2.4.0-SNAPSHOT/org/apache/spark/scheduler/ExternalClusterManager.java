package org.apache.spark.scheduler;

import org.apache.spark.SparkContext;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00053\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0017\u000bb$XM\u001d8bY\u000ecWo\u001d;fe6\u000bg.Y4fe*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007\"\u0002\n\u0001\r\u0003!\u0012!C2b]\u000e\u0013X-\u0019;f\u0007\u0001!\"!\u0006\r\u0011\u000511\u0012BA\f\u000e\u0005\u001d\u0011un\u001c7fC:DQ!G\tA\u0002i\t\u0011\"\\1ti\u0016\u0014XK\u0015'\u0011\u0005mqbB\u0001\u0007\u001d\u0013\tiR\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003?\u0001\u0012aa\u0015;sS:<'BA\u000f\u000e\u0011\u0015\u0011\u0003A\"\u0001$\u0003M\u0019'/Z1uKR\u000b7o[*dQ\u0016$W\u000f\\3s)\r!\u0003F\f\t\u0003K\u0019j\u0011AA\u0005\u0003O\t\u0011Q\u0002V1tWN\u001b\u0007.\u001a3vY\u0016\u0014\b\"B\u0015\"\u0001\u0004Q\u0013AA:d!\tYC&D\u0001\u0005\u0013\tiCA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000fC\u0003\u001aC\u0001\u0007!\u0004C\u00031\u0001\u0019\u0005\u0011'\u0001\fde\u0016\fG/Z*dQ\u0016$W\u000f\\3s\u0005\u0006\u001c7.\u001a8e)\u0011\u0011TGN\u001c\u0011\u0005\u0015\u001a\u0014B\u0001\u001b\u0003\u0005A\u00196\r[3ek2,'OQ1dW\u0016tG\rC\u0003*_\u0001\u0007!\u0006C\u0003\u001a_\u0001\u0007!\u0004C\u0003\u0004_\u0001\u0007A\u0005C\u0003:\u0001\u0019\u0005!(\u0001\u0006j]&$\u0018.\u00197ju\u0016$2a\u000f @!\taA(\u0003\u0002>\u001b\t!QK\\5u\u0011\u0015\u0019\u0001\b1\u0001%\u0011\u0015\u0001\u0005\b1\u00013\u0003\u001d\u0011\u0017mY6f]\u0012\u0004"
)
public interface ExternalClusterManager {
   boolean canCreate(String var1);

   TaskScheduler createTaskScheduler(SparkContext var1, String var2);

   SchedulerBackend createSchedulerBackend(SparkContext var1, String var2, TaskScheduler var3);

   void initialize(TaskScheduler var1, SchedulerBackend var2);
}
