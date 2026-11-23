package org.apache.spark.deploy.master;

import org.apache.spark.SparkConf;
import org.apache.spark.serializer.Serializer;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2Q!\u0001\u0002\u0001\u00051\u0011ADW8p\u0017\u0016,\u0007/\u001a:SK\u000e|g/\u001a:z\u001b>$WMR1di>\u0014\u0018P\u0003\u0002\u0004\t\u00051Q.Y:uKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0003\u0013\t\u0001\"AA\u000fTi\u0006tG-\u00197p]\u0016\u0014VmY8wKJLXj\u001c3f\r\u0006\u001cGo\u001c:z\u0011!\u0011\u0002A!A!\u0002\u0013!\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u0016-5\ta!\u0003\u0002\u0018\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\t3\u0001\u0011\t\u0011)A\u00055\u0005Q1/\u001a:jC2L'0\u001a:\u0011\u0005miR\"\u0001\u000f\u000b\u0005e1\u0011B\u0001\u0010\u001d\u0005)\u0019VM]5bY&TXM\u001d\u0005\u0006A\u0001!\t!I\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\t\u001aC\u0005\u0005\u0002\u000f\u0001!)!c\ba\u0001)!)\u0011d\ba\u00015!)a\u0005\u0001C\u0001O\u000592M]3bi\u0016\u0004VM]:jgR,gnY3F]\u001eLg.\u001a\u000b\u0002QA\u0011a\"K\u0005\u0003U\t\u0011\u0011\u0003U3sg&\u001cH/\u001a8dK\u0016sw-\u001b8f\u0011\u0015a\u0003\u0001\"\u0001.\u0003e\u0019'/Z1uK2+\u0017\rZ3s\u000b2,7\r^5p]\u0006;WM\u001c;\u0015\u00059\n\u0004C\u0001\b0\u0013\t\u0001$AA\nMK\u0006$WM]#mK\u000e$\u0018n\u001c8BO\u0016tG\u000fC\u0003\u0004W\u0001\u0007!\u0007\u0005\u0002\u000fg%\u0011AG\u0001\u0002\u0010\u0019\u0016\fG-\u001a:FY\u0016\u001cG/\u00192mK\u0002"
)
public class ZooKeeperRecoveryModeFactory extends StandaloneRecoveryModeFactory {
   private final SparkConf conf;
   private final Serializer serializer;

   public PersistenceEngine createPersistenceEngine() {
      return new ZooKeeperPersistenceEngine(this.conf, this.serializer);
   }

   public LeaderElectionAgent createLeaderElectionAgent(LeaderElectable master) {
      return new ZooKeeperLeaderElectionAgent(master, this.conf);
   }

   public ZooKeeperRecoveryModeFactory(SparkConf conf, Serializer serializer) {
      super(conf, serializer);
      this.conf = conf;
      this.serializer = serializer;
   }
}
