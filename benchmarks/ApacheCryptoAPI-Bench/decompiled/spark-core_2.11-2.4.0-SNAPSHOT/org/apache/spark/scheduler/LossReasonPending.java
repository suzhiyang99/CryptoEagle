package org.apache.spark.scheduler;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005:a!\u0001\u0002\t\u0002\u0011Q\u0011!\u0005'pgN\u0014V-Y:p]B+g\u000eZ5oO*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003#1{7o\u001d*fCN|g\u000eU3oI&twm\u0005\u0002\r\u001fA\u00111\u0002E\u0005\u0003#\t\u0011!#\u0012=fGV$xN\u001d'pgN\u0014V-Y:p]\")1\u0003\u0004C\u0001+\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u000b\u0011\u001d9B\"!A\u0005\na\t1B]3bIJ+7o\u001c7wKR\t\u0011\u0004\u0005\u0002\u001b?5\t1D\u0003\u0002\u001d;\u0005!A.\u00198h\u0015\u0005q\u0012\u0001\u00026bm\u0006L!\u0001I\u000e\u0003\r=\u0013'.Z2u\u0001"
)
public final class LossReasonPending {
   public static String toString() {
      return LossReasonPending$.MODULE$.toString();
   }

   public static String message() {
      return LossReasonPending$.MODULE$.message();
   }
}
