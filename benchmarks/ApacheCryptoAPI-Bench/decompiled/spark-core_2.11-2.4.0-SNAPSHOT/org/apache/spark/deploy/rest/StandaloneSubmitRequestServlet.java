package org.apache.spark.deploy.rest;

import java.io.File;
import javax.servlet.http.HttpServletResponse;
import org.apache.spark.SparkConf;
import org.apache.spark.package$;
import org.apache.spark.deploy.ClientArguments$;
import org.apache.spark.deploy.Command;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.DriverDescription;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayOps;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001a3Q!\u0001\u0002\u0001\u00051\u0011ad\u0015;b]\u0012\fGn\u001c8f'V\u0014W.\u001b;SKF,Xm\u001d;TKJ4H.\u001a;\u000b\u0005\r!\u0011\u0001\u0002:fgRT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0003\u0013\t\u0001\"A\u0001\u000bTk\nl\u0017\u000e\u001e*fcV,7\u000f^*feZdW\r\u001e\u0005\t%\u0001\u0011\t\u0011)A\u0005)\u0005qQ.Y:uKJ,e\u000e\u001a9pS:$8\u0001\u0001\t\u0003+ai\u0011A\u0006\u0006\u0003/\u0019\t1A\u001d9d\u0013\tIbC\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0011m\u0001!\u0011!Q\u0001\nq\t\u0011\"\\1ti\u0016\u0014XK\u001d7\u0011\u0005u\u0019cB\u0001\u0010\"\u001b\u0005y\"\"\u0001\u0011\u0002\u000bM\u001c\u0017\r\\1\n\u0005\tz\u0012A\u0002)sK\u0012,g-\u0003\u0002%K\t11\u000b\u001e:j]\u001eT!AI\u0010\t\u0011\u001d\u0002!\u0011!Q\u0001\n!\nAaY8oMB\u0011\u0011FK\u0007\u0002\r%\u00111F\u0002\u0002\n'B\f'o[\"p]\u001aDQ!\f\u0001\u0005\u00029\na\u0001P5oSRtD\u0003B\u00181cI\u0002\"A\u0004\u0001\t\u000bIa\u0003\u0019\u0001\u000b\t\u000bma\u0003\u0019\u0001\u000f\t\u000b\u001db\u0003\u0019\u0001\u0015\t\u000bQ\u0002A\u0011B\u001b\u0002-\t,\u0018\u000e\u001c3Ee&4XM\u001d#fg\u000e\u0014\u0018\u000e\u001d;j_:$\"A\u000e\u001e\u0011\u0005]BT\"\u0001\u0003\n\u0005e\"!!\u0005#sSZ,'\u000fR3tGJL\u0007\u000f^5p]\")1h\ra\u0001y\u00059!/Z9vKN$\bC\u0001\b>\u0013\tq$AA\fDe\u0016\fG/Z*vE6L7o]5p]J+\u0017/^3ti\")\u0001\t\u0001C)\u0003\u0006a\u0001.\u00198eY\u0016\u001cVOY7jiR!!)R$M!\tq1)\u0003\u0002E\u0005\tQ2+\u001e2nSR\u0014Vm\u001d;Qe>$xnY8m%\u0016\u001c\bo\u001c8tK\")ai\u0010a\u00019\u0005\u0011\"/Z9vKN$X*Z:tC\u001e,'j]8o\u0011\u0015Au\b1\u0001J\u00039\u0011X-];fgRlUm]:bO\u0016\u0004\"A\u0004&\n\u0005-\u0013!!G*vE6LGOU3tiB\u0013x\u000e^8d_2lUm]:bO\u0016DQ!T A\u00029\u000bqB]3ta>t7/Z*feZdW\r\u001e\t\u0003\u001fZk\u0011\u0001\u0015\u0006\u0003#J\u000bA\u0001\u001b;ua*\u00111\u000bV\u0001\bg\u0016\u0014h\u000f\\3u\u0015\u0005)\u0016!\u00026bm\u0006D\u0018BA,Q\u0005MAE\u000f\u001e9TKJ4H.\u001a;SKN\u0004xN\\:f\u0001"
)
public class StandaloneSubmitRequestServlet extends SubmitRequestServlet {
   private final RpcEndpointRef masterEndpoint;
   private final String masterUrl;

   private DriverDescription buildDriverDescription(CreateSubmissionRequest request) {
      String appResource = (String)Option$.MODULE$.apply(request.appResource()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new SubmitRestMissingFieldException("Application jar is missing.");
         }
      });
      String mainClass = (String)Option$.MODULE$.apply(request.mainClass()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new SubmitRestMissingFieldException("Main class is missing.");
         }
      });
      Map sparkProperties = request.sparkProperties();
      Option driverMemory = sparkProperties.get("spark.driver.memory");
      Option driverCores = sparkProperties.get("spark.driver.cores");
      Option driverExtraJavaOptions = sparkProperties.get("spark.driver.extraJavaOptions");
      Option driverExtraClassPath = sparkProperties.get("spark.driver.extraClassPath");
      Option driverExtraLibraryPath = sparkProperties.get("spark.driver.extraLibraryPath");
      Option superviseDriver = sparkProperties.get("spark.driver.supervise");
      String[] appArgs = request.appArgs();
      Map environmentVariables = (Map)request.environmentVariables().filterNot(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x) {
            return ((String)x._1()).matches("SPARK_LOCAL_(IP|HOSTNAME)");
         }
      });
      SparkConf conf = (new SparkConf(false)).setAll(sparkProperties).set("spark.master", this.masterUrl);
      Seq extraClassPath = (Seq)Option$.MODULE$.option2Iterable(driverExtraClassPath).toSeq().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(String x$3) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])x$3.split(File.pathSeparator));
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq extraLibraryPath = (Seq)Option$.MODULE$.option2Iterable(driverExtraLibraryPath).toSeq().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(String x$4) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])x$4.split(File.pathSeparator));
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq extraJavaOpts = (Seq)driverExtraJavaOptions.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<String> apply(String s) {
            return Utils$.MODULE$.splitCommandString(s);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      Seq sparkJavaOpts = Utils$.MODULE$.sparkJavaOpts(conf, Utils$.MODULE$.sparkJavaOpts$default$2());
      Seq javaOpts = (Seq)sparkJavaOpts.$plus$plus(extraJavaOpts, Seq$.MODULE$.canBuildFrom());
      Command command = new Command("org.apache.spark.deploy.worker.DriverWrapper", (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"{{WORKER_URL}}", "{{USER_JAR}}", mainClass})))).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])appArgs), Seq$.MODULE$.canBuildFrom()), environmentVariables, extraClassPath, extraLibraryPath, javaOpts);
      int actualDriverMemory = BoxesRunTime.unboxToInt(driverMemory.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String str) {
            return Utils$.MODULE$.memoryStringToMb(str);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return ClientArguments$.MODULE$.DEFAULT_MEMORY();
         }
      }));
      int actualDriverCores = BoxesRunTime.unboxToInt(driverCores.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$5) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$5))).toInt();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return ClientArguments$.MODULE$.DEFAULT_CORES();
         }
      }));
      boolean actualSuperviseDriver = BoxesRunTime.unboxToBoolean(superviseDriver.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$6) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$6))).toBoolean();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return ClientArguments$.MODULE$.DEFAULT_SUPERVISE();
         }
      }));
      return new DriverDescription(appResource, actualDriverMemory, actualDriverCores, actualSuperviseDriver, command);
   }

   public SubmitRestProtocolResponse handleSubmit(String requestMessageJson, SubmitRestProtocolMessage requestMessage, HttpServletResponse responseServlet) {
      Object var6;
      if (requestMessage instanceof CreateSubmissionRequest) {
         CreateSubmissionRequest var5 = (CreateSubmissionRequest)requestMessage;
         DriverDescription driverDescription = this.buildDriverDescription(var5);
         DeployMessages.SubmitDriverResponse response = (DeployMessages.SubmitDriverResponse)this.masterEndpoint.askSync(new DeployMessages.RequestSubmitDriver(driverDescription), ClassTag$.MODULE$.apply(DeployMessages.SubmitDriverResponse.class));
         CreateSubmissionResponse submitResponse = new CreateSubmissionResponse();
         submitResponse.serverSparkVersion_$eq(package$.MODULE$.SPARK_VERSION());
         submitResponse.message_$eq(response.message());
         submitResponse.success_$eq(Predef$.MODULE$.boolean2Boolean(response.success()));
         submitResponse.submissionId_$eq((String)response.driverId().orNull(Predef$.MODULE$.$conforms()));
         String[] unknownFields = this.findUnknownFields(requestMessageJson, requestMessage);
         if (Predef$.MODULE$.refArrayOps((Object[])unknownFields).nonEmpty()) {
            submitResponse.unknownFields_$eq(unknownFields);
         }

         var6 = submitResponse;
      } else {
         responseServlet.setStatus(400);
         var6 = this.handleError((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received message of unexpected type ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{requestMessage.messageType()})));
      }

      return (SubmitRestProtocolResponse)var6;
   }

   public StandaloneSubmitRequestServlet(RpcEndpointRef masterEndpoint, String masterUrl, SparkConf conf) {
      this.masterEndpoint = masterEndpoint;
      this.masterUrl = masterUrl;
   }
}
