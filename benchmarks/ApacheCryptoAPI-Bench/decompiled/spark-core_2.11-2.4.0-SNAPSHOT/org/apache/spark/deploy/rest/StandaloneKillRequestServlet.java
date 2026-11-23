package org.apache.spark.deploy.rest;

import org.apache.spark.SparkConf;
import org.apache.spark.package$;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.rpc.RpcEndpointRef;
import scala.Predef$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a2Q!\u0001\u0002\u0001\u00051\u0011Ad\u0015;b]\u0012\fGn\u001c8f\u0017&dGNU3rk\u0016\u001cHoU3sm2,GO\u0003\u0002\u0004\t\u0005!!/Z:u\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u0011!cS5mYJ+\u0017/^3tiN+'O\u001e7fi\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\bnCN$XM]#oIB|\u0017N\u001c;\u0004\u0001A\u0011Q\u0003G\u0007\u0002-)\u0011qCB\u0001\u0004eB\u001c\u0017BA\r\u0017\u00059\u0011\u0006oY#oIB|\u0017N\u001c;SK\u001aD\u0001b\u0007\u0001\u0003\u0002\u0003\u0006I\u0001H\u0001\u0005G>tg\r\u0005\u0002\u001e=5\ta!\u0003\u0002 \r\tI1\u000b]1sW\u000e{gN\u001a\u0005\u0006C\u0001!\tAI\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\r\"S\u0005\u0005\u0002\u000f\u0001!)!\u0003\ta\u0001)!)1\u0004\ta\u00019!)q\u0005\u0001C\tQ\u0005Q\u0001.\u00198eY\u0016\\\u0015\u000e\u001c7\u0015\u0005%b\u0003C\u0001\b+\u0013\tY#A\u0001\fLS2d7+\u001e2nSN\u001c\u0018n\u001c8SKN\u0004xN\\:f\u0011\u0015ic\u00051\u0001/\u00031\u0019XOY7jgNLwN\\%e!\tySG\u0004\u00021g5\t\u0011GC\u00013\u0003\u0015\u00198-\u00197b\u0013\t!\u0014'\u0001\u0004Qe\u0016$WMZ\u0005\u0003m]\u0012aa\u0015;sS:<'B\u0001\u001b2\u0001"
)
public class StandaloneKillRequestServlet extends KillRequestServlet {
   private final RpcEndpointRef masterEndpoint;

   public KillSubmissionResponse handleKill(String submissionId) {
      DeployMessages.KillDriverResponse response = (DeployMessages.KillDriverResponse)this.masterEndpoint.askSync(new DeployMessages.RequestKillDriver(submissionId), ClassTag$.MODULE$.apply(DeployMessages.KillDriverResponse.class));
      KillSubmissionResponse k = new KillSubmissionResponse();
      k.serverSparkVersion_$eq(package$.MODULE$.SPARK_VERSION());
      k.message_$eq(response.message());
      k.submissionId_$eq(submissionId);
      k.success_$eq(Predef$.MODULE$.boolean2Boolean(response.success()));
      return k;
   }

   public StandaloneKillRequestServlet(RpcEndpointRef masterEndpoint, SparkConf conf) {
      this.masterEndpoint = masterEndpoint;
   }
}
