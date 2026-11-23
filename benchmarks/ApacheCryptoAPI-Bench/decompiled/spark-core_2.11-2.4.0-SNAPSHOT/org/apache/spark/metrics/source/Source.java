package org.apache.spark.metrics.source;

import com.codahale.metrics.MetricRegistry;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u0007'>,(oY3\u000b\u0005\r!\u0011AB:pkJ\u001cWM\u0003\u0002\u0006\r\u00059Q.\u001a;sS\u000e\u001c(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\rC\u0003\u0015\u0001\u0019\u0005a#\u0001\u0006t_V\u00148-\u001a(b[\u0016\u001c\u0001!F\u0001\u0018!\tA2D\u0004\u0002\u000f3%\u0011!dD\u0001\u0007!J,G-\u001a4\n\u0005qi\"AB*ue&twM\u0003\u0002\u001b\u001f!)q\u0004\u0001D\u0001A\u0005qQ.\u001a;sS\u000e\u0014VmZ5tiJLX#A\u0011\u0011\u0005\tBS\"A\u0012\u000b\u0005\u0015!#BA\u0013'\u0003!\u0019w\u000eZ1iC2,'\"A\u0014\u0002\u0007\r|W.\u0003\u0002*G\tqQ*\u001a;sS\u000e\u0014VmZ5tiJL\b"
)
public interface Source {
   String sourceName();

   MetricRegistry metricRegistry();
}
