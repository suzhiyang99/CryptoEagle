package org.apache.spark.scheduler;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:Q!\u0001\u0002\t\u0002-\tabU2iK\u0012,H.\u001b8h\u001b>$WM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u0001\u0001C\u0001\u0007\u000e\u001b\u0005\u0011a!\u0002\b\u0003\u0011\u0003y!AD*dQ\u0016$W\u000f\\5oO6{G-Z\n\u0003\u001bA\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u00111\"\u00128v[\u0016\u0014\u0018\r^5p]\")q#\u0004C\u00011\u00051A(\u001b8jiz\"\u0012aC\u0003\u0005\u001d5\u0001!\u0004\u0005\u0002\u001c95\tQ\"\u0003\u0002\u001e)\t)a+\u00197vK\"9q$\u0004b\u0001\n\u0003\u0001\u0013\u0001\u0002$B\u0013J+\u0012A\u0007\u0005\u0007E5\u0001\u000b\u0011\u0002\u000e\u0002\u000b\u0019\u000b\u0015J\u0015\u0011\t\u000f\u0011j!\u0019!C\u0001A\u0005!a)\u0013$P\u0011\u00191S\u0002)A\u00055\u0005)a)\u0013$PA!9\u0001&\u0004b\u0001\n\u0003\u0001\u0013\u0001\u0002(P\u001d\u0016CaAK\u0007!\u0002\u0013Q\u0012!\u0002(P\u001d\u0016\u0003\u0003"
)
public final class SchedulingMode {
   public static Enumeration.Value NONE() {
      return SchedulingMode$.MODULE$.NONE();
   }

   public static Enumeration.Value FIFO() {
      return SchedulingMode$.MODULE$.FIFO();
   }

   public static Enumeration.Value FAIR() {
      return SchedulingMode$.MODULE$.FAIR();
   }

   public static Enumeration.Value withName(String var0) {
      return SchedulingMode$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return SchedulingMode$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return SchedulingMode$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return SchedulingMode$.MODULE$.values();
   }

   public static String toString() {
      return SchedulingMode$.MODULE$.toString();
   }
}
