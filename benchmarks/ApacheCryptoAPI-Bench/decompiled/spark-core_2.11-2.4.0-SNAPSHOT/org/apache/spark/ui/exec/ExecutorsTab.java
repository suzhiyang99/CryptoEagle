package org.apache.spark.ui.exec;

import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUITab;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00192Q!\u0001\u0002\u0001\t1\u0011A\"\u0012=fGV$xN]:UC\nT!a\u0001\u0003\u0002\t\u0015DXm\u0019\u0006\u0003\u000b\u0019\t!!^5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059yQ\"\u0001\u0003\n\u0005A!!AC*qCJ\\W+\u0013+bE\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\u0004qCJ,g\u000e^\u0002\u0001!\tqQ#\u0003\u0002\u0017\t\t91\u000b]1sWVK\u0005\"\u0002\r\u0001\t\u0003I\u0012A\u0002\u001fj]&$h\b\u0006\u0002\u001b9A\u00111\u0004A\u0007\u0002\u0005!)!c\u0006a\u0001)!)a\u0004\u0001C\u0005?\u0005!\u0011N\\5u)\u0005\u0001\u0003CA\u0011%\u001b\u0005\u0011#\"A\u0012\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u0015\u0012#\u0001B+oSR\u0004"
)
public class ExecutorsTab extends SparkUITab {
   private final SparkUI parent;

   private void init() {
      boolean threadDumpEnabled = this.parent.sc().isDefined() && this.parent.conf().getBoolean("spark.ui.threadDumpsEnabled", true);
      this.attachPage(new ExecutorsPage(this, threadDumpEnabled));
      if (threadDumpEnabled) {
         this.attachPage(new ExecutorThreadDumpPage(this, this.parent.sc()));
      }

   }

   public ExecutorsTab(SparkUI parent) {
      super(parent, "executors");
      this.parent = parent;
      this.init();
   }
}
