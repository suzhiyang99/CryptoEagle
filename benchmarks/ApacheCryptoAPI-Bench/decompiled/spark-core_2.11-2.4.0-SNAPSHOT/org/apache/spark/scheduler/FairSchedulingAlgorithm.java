package org.apache.spark.scheduler;

import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2Q!\u0001\u0002\u0001\t)\u0011qCR1jeN\u001b\u0007.\u001a3vY&tw-\u00117h_JLG\u000f[7\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003'M\u001b\u0007.\u001a3vY&tw-\u00117h_JLG\u000f[7\t\u000bY\u0001A\u0011\u0001\r\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0007\t\u0003%\u0001AQa\u0007\u0001\u0005Bq\t!bY8na\u0006\u0014\u0018\r^8s)\ri\u0002%\n\t\u0003\u0019yI!aH\u0007\u0003\u000f\t{w\u000e\\3b]\")\u0011E\u0007a\u0001E\u0005\u00111/\r\t\u0003%\rJ!\u0001\n\u0002\u0003\u0017M\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\u0005\u0006Mi\u0001\rAI\u0001\u0003gJ\u0002"
)
public class FairSchedulingAlgorithm implements SchedulingAlgorithm {
   public boolean comparator(Schedulable s1, Schedulable s2) {
      int minShare1 = s1.minShare();
      int minShare2 = s2.minShare();
      int runningTasks1 = s1.runningTasks();
      int runningTasks2 = s2.runningTasks();
      boolean s1Needy = runningTasks1 < minShare1;
      boolean s2Needy = runningTasks2 < minShare2;
      double minShareRatio1 = (double)runningTasks1 / scala.math.package$.MODULE$.max((double)minShare1, (double)1.0F);
      double minShareRatio2 = (double)runningTasks2 / scala.math.package$.MODULE$.max((double)minShare2, (double)1.0F);
      double taskToWeightRatio1 = (double)runningTasks1 / (double)s1.weight();
      double taskToWeightRatio2 = (double)runningTasks2 / (double)s2.weight();
      int compare = 0;
      if (s1Needy && !s2Needy) {
         return true;
      } else if (!s1Needy && s2Needy) {
         return false;
      } else {
         if (s1Needy && s2Needy) {
            compare = Predef$.MODULE$.double2Double(minShareRatio1).compareTo(Predef$.MODULE$.double2Double(minShareRatio2));
         } else {
            compare = Predef$.MODULE$.double2Double(taskToWeightRatio1).compareTo(Predef$.MODULE$.double2Double(taskToWeightRatio2));
         }

         return compare < 0 ? true : (compare > 0 ? false : (new StringOps(Predef$.MODULE$.augmentString(s1.name()))).$less(s2.name()));
      }
   }
}
