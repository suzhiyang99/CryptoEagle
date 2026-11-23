package org.apache.spark.ui.storage;

import org.apache.spark.status.AppStatusStore;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUITab;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00192Q!\u0001\u0002\u0001\t1\u0011!b\u0015;pe\u0006<W\rV1c\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011AA;j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0005\u0013\t\u0001BA\u0001\u0006Ta\u0006\u00148.V%UC\nD\u0001B\u0005\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\u0007a\u0006\u0014XM\u001c;\u0004\u0001A\u0011a\"F\u0005\u0003-\u0011\u0011qa\u00159be.,\u0016\n\u0003\u0005\u0019\u0001\t\u0005\t\u0015!\u0003\u001a\u0003\u0015\u0019Ho\u001c:f!\tQR$D\u0001\u001c\u0015\tab!\u0001\u0004ti\u0006$Xo]\u0005\u0003=m\u0011a\"\u00119q'R\fG/^:Ti>\u0014X\rC\u0003!\u0001\u0011\u0005\u0011%\u0001\u0004=S:LGO\u0010\u000b\u0004E\u0011*\u0003CA\u0012\u0001\u001b\u0005\u0011\u0001\"\u0002\n \u0001\u0004!\u0002\"\u0002\r \u0001\u0004I\u0002"
)
public class StorageTab extends SparkUITab {
   public StorageTab(SparkUI parent, AppStatusStore store) {
      super(parent, "storage");
      this.attachPage(new StoragePage(this, store));
      this.attachPage(new RDDPage(this, store));
   }
}
