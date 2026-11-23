package org.apache.spark.deploy;

import org.apache.log4j.Logger;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.master.Master$;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcAddress$;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001I2Q!\u0001\u0002\u0001\t)\u0011\u0011b\u00117jK:$\u0018\t\u001d9\u000b\u0005\r!\u0011A\u00023fa2|\u0017P\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!\u0001E*qCJ\\\u0017\t\u001d9mS\u000e\fG/[8o\u0011\u00151\u0002\u0001\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#A\r\u0011\u0005I\u0001\u0001\"B\u000e\u0001\t\u0003b\u0012!B:uCJ$HcA\u000f!YA\u0011ABH\u0005\u0003?5\u0011A!\u00168ji\")\u0011E\u0007a\u0001E\u0005!\u0011M]4t!\ra1%J\u0005\u0003I5\u0011Q!\u0011:sCf\u0004\"AJ\u0015\u000f\u000519\u0013B\u0001\u0015\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011!f\u000b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005!j\u0001\"B\u0017\u001b\u0001\u0004q\u0013\u0001B2p]\u001a\u0004\"a\f\u0019\u000e\u0003\u0011I!!\r\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007"
)
public class ClientApp implements SparkApplication {
   public void start(String[] args, SparkConf conf) {
      ClientArguments driverArgs = new ClientArguments(args);
      if (conf.contains("spark.rpc.askTimeout")) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         conf.set("spark.rpc.askTimeout", "10s");
      }

      Logger.getRootLogger().setLevel(driverArgs.logLevel());
      RpcEnv rpcEnv = RpcEnv$.MODULE$.create("driverClient", Utils$.MODULE$.localHostName(), 0, conf, new SecurityManager(conf, SecurityManager$.MODULE$.$lessinit$greater$default$2()), RpcEnv$.MODULE$.create$default$6());
      RpcEndpointRef[] masterEndpoints = (RpcEndpointRef[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])driverArgs.masters()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RpcAddress apply(String sparkUrl) {
            return RpcAddress$.MODULE$.fromSparkURL(sparkUrl);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(RpcAddress.class)))).map(new Serializable(this, rpcEnv) {
         public static final long serialVersionUID = 0L;
         private final RpcEnv rpcEnv$1;

         public final RpcEndpointRef apply(RpcAddress x$1) {
            return this.rpcEnv$1.setupEndpointRef(x$1, Master$.MODULE$.ENDPOINT_NAME());
         }

         public {
            this.rpcEnv$1 = rpcEnv$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(RpcEndpointRef.class)));
      rpcEnv.setupEndpoint("client", new ClientEndpoint(rpcEnv, driverArgs, Predef$.MODULE$.wrapRefArray(masterEndpoints), conf));
      rpcEnv.awaitTermination();
   }
}
