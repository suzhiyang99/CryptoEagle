package org.apache.spark.scheduler;

import org.apache.spark.util.ListenerBus;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000112\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005AA\u0003\u0002\u0011'B\f'o\u001b'jgR,g.\u001a:CkNT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0005%U92$D\u0001\u0014\u0015\t!B!\u0001\u0003vi&d\u0017B\u0001\f\u0014\u0005-a\u0015n\u001d;f]\u0016\u0014()^:\u0011\u0005aIR\"\u0001\u0002\n\u0005i\u0011!AF*qCJ\\G*[:uK:,'/\u00138uKJ4\u0017mY3\u0011\u0005aa\u0012BA\u000f\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\t\u000b}\u0001A\u0011A\u0011\u0002\r\u0011Jg.\u001b;%\u0007\u0001!\u0012A\t\t\u0003\u0019\rJ!\u0001J\u0007\u0003\tUs\u0017\u000e\u001e\u0005\u0006M\u0001!\tfJ\u0001\fI>\u0004vn\u001d;Fm\u0016tG\u000fF\u0002#Q)BQ!K\u0013A\u0002]\t\u0001\u0002\\5ti\u0016tWM\u001d\u0005\u0006W\u0015\u0002\raG\u0001\u0006KZ,g\u000e\u001e"
)
public interface SparkListenerBus extends ListenerBus<SparkListenerInterface, SparkListenerEvent> {
   void doPostEvent(SparkListenerInterface var1, SparkListenerEvent var2);
}
