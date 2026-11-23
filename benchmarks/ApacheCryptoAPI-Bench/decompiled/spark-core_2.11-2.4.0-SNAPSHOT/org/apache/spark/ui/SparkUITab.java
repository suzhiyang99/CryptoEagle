package org.apache.spark.ui;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000152a!\u0001\u0002\u0002\u0002\u0011Q!AC*qCJ\\W+\u0013+bE*\u00111\u0001B\u0001\u0003k&T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\u0007\u000e\u0003\tI!A\u0004\u0002\u0003\u0011]+'-V%UC\nD\u0001\u0002\u0005\u0001\u0003\u0002\u0003\u0006IAE\u0001\u0007a\u0006\u0014XM\u001c;\u0004\u0001A\u0011AbE\u0005\u0003)\t\u0011qa\u00159be.,\u0016\nC\u0005\u0017\u0001\t\u0005\t\u0015!\u0003\u0018C\u00051\u0001O]3gSb\u0004\"\u0001\u0007\u0010\u000f\u0005eaR\"\u0001\u000e\u000b\u0003m\tQa]2bY\u0006L!!\b\u000e\u0002\rA\u0013X\rZ3g\u0013\ty\u0002E\u0001\u0004TiJLgn\u001a\u0006\u0003;iI!AF\u0007\t\u000b\r\u0002A\u0011\u0001\u0013\u0002\rqJg.\u001b;?)\r)ce\n\t\u0003\u0019\u0001AQ\u0001\u0005\u0012A\u0002IAQA\u0006\u0012A\u0002]AQ!\u000b\u0001\u0005\u0002)\nq!\u00199q\u001d\u0006lW-F\u0001\u0018\u0011\u0015a\u0003\u0001\"\u0001+\u0003=\t\u0007\u000f]*qCJ\\g+\u001a:tS>t\u0007"
)
public abstract class SparkUITab extends WebUITab {
   private final SparkUI parent;

   public String appName() {
      return this.parent.appName();
   }

   public String appSparkVersion() {
      return this.parent.appSparkVersion();
   }

   public SparkUITab(SparkUI parent, String prefix) {
      super(parent, prefix);
      this.parent = parent;
   }
}
