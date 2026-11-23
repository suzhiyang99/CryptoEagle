package org.apache.spark.deploy.master;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00152Q!\u0001\u0002\u0001\r1\u00111#T8oCJ\u001c\u0007.\u001f'fC\u0012,'/Q4f]RT!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0003\u0013\t1\"AA\nMK\u0006$WM]#mK\u000e$\u0018n\u001c8BO\u0016tG\u000f\u0003\u0005\u0019\u0001\t\u0015\r\u0011\"\u0001\u001b\u00039i\u0017m\u001d;fe&s7\u000f^1oG\u0016\u001c\u0001!F\u0001\u001c!\t!B$\u0003\u0002\u001e\u0005\tyA*Z1eKJ,E.Z2uC\ndW\r\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003\u001c\u0003=i\u0017m\u001d;fe&s7\u000f^1oG\u0016\u0004\u0003\"B\u0011\u0001\t\u0003\u0011\u0013A\u0002\u001fj]&$h\b\u0006\u0002$IA\u0011A\u0003\u0001\u0005\u00061\u0001\u0002\ra\u0007"
)
public class MonarchyLeaderAgent implements LeaderElectionAgent {
   private final LeaderElectable masterInstance;

   public void stop() {
      LeaderElectionAgent$class.stop(this);
   }

   public LeaderElectable masterInstance() {
      return this.masterInstance;
   }

   public MonarchyLeaderAgent(LeaderElectable masterInstance) {
      this.masterInstance = masterInstance;
      LeaderElectionAgent$class.$init$(this);
      masterInstance.electedLeader();
   }
}
