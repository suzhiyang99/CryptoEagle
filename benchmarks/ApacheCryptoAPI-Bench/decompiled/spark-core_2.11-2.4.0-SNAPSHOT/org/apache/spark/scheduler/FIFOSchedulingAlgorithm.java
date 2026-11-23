package org.apache.spark.scheduler;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2Q!\u0001\u0002\u0001\t)\u0011qCR%G\u001fN\u001b\u0007.\u001a3vY&tw-\u00117h_JLG\u000f[7\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003'M\u001b\u0007.\u001a3vY&tw-\u00117h_JLG\u000f[7\t\u000bY\u0001A\u0011\u0001\r\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0007\t\u0003%\u0001AQa\u0007\u0001\u0005Bq\t!bY8na\u0006\u0014\u0018\r^8s)\ri\u0002%\n\t\u0003\u0019yI!aH\u0007\u0003\u000f\t{w\u000e\\3b]\")\u0011E\u0007a\u0001E\u0005\u00111/\r\t\u0003%\rJ!\u0001\n\u0002\u0003\u0017M\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\u0005\u0006Mi\u0001\rAI\u0001\u0003gJ\u0002"
)
public class FIFOSchedulingAlgorithm implements SchedulingAlgorithm {
   public boolean comparator(Schedulable s1, Schedulable s2) {
      int priority1 = s1.priority();
      int priority2 = s2.priority();
      int res = scala.math.package$.MODULE$.signum(priority1 - priority2);
      if (res == 0) {
         int stageId1 = s1.stageId();
         int stageId2 = s2.stageId();
         res = scala.math.package$.MODULE$.signum(stageId1 - stageId2);
      }

      return res < 0;
   }
}
