package org.apache.spark.deploy.rest;

import org.apache.spark.SparkConf;
import org.apache.spark.package$;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.rpc.RpcEndpointRef;
import scala.Enumeration;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a2Q!\u0001\u0002\u0001\u00051\u0011ad\u0015;b]\u0012\fGn\u001c8f'R\fG/^:SKF,Xm\u001d;TKJ4H.\u001a;\u000b\u0005\r!\u0011\u0001\u0002:fgRT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0003\u0013\t\u0001\"A\u0001\u000bTi\u0006$Xo\u001d*fcV,7\u000f^*feZdW\r\u001e\u0005\t%\u0001\u0011\t\u0011)A\u0005)\u0005qQ.Y:uKJ,e\u000e\u001a9pS:$8\u0001\u0001\t\u0003+ai\u0011A\u0006\u0006\u0003/\u0019\t1A\u001d9d\u0013\tIbC\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0011m\u0001!\u0011!Q\u0001\nq\tAaY8oMB\u0011QDH\u0007\u0002\r%\u0011qD\u0002\u0002\n'B\f'o[\"p]\u001aDQ!\t\u0001\u0005\u0002\t\na\u0001P5oSRtDcA\u0012%KA\u0011a\u0002\u0001\u0005\u0006%\u0001\u0002\r\u0001\u0006\u0005\u00067\u0001\u0002\r\u0001\b\u0005\u0006O\u0001!\t\u0002K\u0001\rQ\u0006tG\r\\3Ti\u0006$Xo\u001d\u000b\u0003S1\u0002\"A\u0004\u0016\n\u0005-\u0012!\u0001G*vE6L7o]5p]N#\u0018\r^;t%\u0016\u001c\bo\u001c8tK\")QF\na\u0001]\u0005a1/\u001e2nSN\u001c\u0018n\u001c8JIB\u0011q&\u000e\b\u0003aMj\u0011!\r\u0006\u0002e\u0005)1oY1mC&\u0011A'M\u0001\u0007!J,G-\u001a4\n\u0005Y:$AB*ue&twM\u0003\u00025c\u0001"
)
public class StandaloneStatusRequestServlet extends StatusRequestServlet {
   private final RpcEndpointRef masterEndpoint;

   public SubmissionStatusResponse handleStatus(String submissionId) {
      DeployMessages.DriverStatusResponse response = (DeployMessages.DriverStatusResponse)this.masterEndpoint.askSync(new DeployMessages.RequestDriverStatus(submissionId), ClassTag$.MODULE$.apply(DeployMessages.DriverStatusResponse.class));
      Option message = response.exception().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StandaloneStatusRequestServlet $outer;

         public final String apply(Exception x$1) {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception from the cluster:\\n"})))).s(Nil$.MODULE$)).append((Object)this.$outer.formatException(x$1)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      SubmissionStatusResponse d = new SubmissionStatusResponse();
      d.serverSparkVersion_$eq(package$.MODULE$.SPARK_VERSION());
      d.submissionId_$eq(submissionId);
      d.success_$eq(Predef$.MODULE$.boolean2Boolean(response.found()));
      d.driverState_$eq((String)response.state().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Enumeration.Value x$2) {
            return x$2.toString();
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      d.workerId_$eq((String)response.workerId().orNull(Predef$.MODULE$.$conforms()));
      d.workerHostPort_$eq((String)response.workerHostPort().orNull(Predef$.MODULE$.$conforms()));
      d.message_$eq((String)message.orNull(Predef$.MODULE$.$conforms()));
      return d;
   }

   public StandaloneStatusRequestServlet(RpcEndpointRef masterEndpoint, SparkConf conf) {
      this.masterEndpoint = masterEndpoint;
   }
}
