package org.apache.spark.deploy.rest;

import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkApplication;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001Q3Q!\u0001\u0002\u0001\r1\u0011qCU3tiN+(-\\5tg&|gn\u00117jK:$\u0018\t\u001d9\u000b\u0005\r!\u0011\u0001\u0002:fgRT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\u0011I!A\u0006\u0003\u0003!M\u0003\u0018M]6BaBd\u0017nY1uS>t\u0007\"\u0002\r\u0001\t\u0003Q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003m\u0001\"\u0001\b\u0001\u000e\u0003\tAQA\b\u0001\u0005\u0002}\t1A];o)\u0019\u00013\u0005\f\u00184sA\u0011A$I\u0005\u0003E\t\u0011!dU;c[&$(+Z:u!J|Go\\2pYJ+7\u000f]8og\u0016DQ\u0001J\u000fA\u0002\u0015\n1\"\u00199q%\u0016\u001cx.\u001e:dKB\u0011a%\u000b\b\u0003\u001d\u001dJ!\u0001K\b\u0002\rA\u0013X\rZ3g\u0013\tQ3F\u0001\u0004TiJLgn\u001a\u0006\u0003Q=AQ!L\u000fA\u0002\u0015\n\u0011\"\\1j]\u000ec\u0017m]:\t\u000b=j\u0002\u0019\u0001\u0019\u0002\u000f\u0005\u0004\b/\u0011:hgB\u0019a\"M\u0013\n\u0005Iz!!B!se\u0006L\b\"\u0002\u001b\u001e\u0001\u0004)\u0014\u0001B2p]\u001a\u0004\"AN\u001c\u000e\u0003\u0019I!\u0001\u000f\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007b\u0002\u001e\u001e!\u0003\u0005\raO\u0001\u0004K:4\b\u0003\u0002\u0014=K\u0015J!!P\u0016\u0003\u00075\u000b\u0007\u000fC\u0003@\u0001\u0011\u0005\u0003)A\u0003ti\u0006\u0014H\u000fF\u0002B\t\u001a\u0003\"A\u0004\"\n\u0005\r{!\u0001B+oSRDQ!\u0012 A\u0002A\nA!\u0019:hg\")AG\u0010a\u0001k!9\u0001\nAI\u0001\n\u0003I\u0015!\u0004:v]\u0012\"WMZ1vYR$S'F\u0001KU\tY4jK\u0001M!\ti%+D\u0001O\u0015\ty\u0005+A\u0005v]\u000eDWmY6fI*\u0011\u0011kD\u0001\u000bC:tw\u000e^1uS>t\u0017BA*O\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a"
)
public class RestSubmissionClientApp implements SparkApplication {
   public SubmitRestProtocolResponse run(String appResource, String mainClass, String[] appArgs, SparkConf conf, Map<String, String> env) {
      String master = (String)conf.getOption("spark.master").getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new IllegalArgumentException("'spark.master' must be set.");
         }
      });
      Map sparkProperties = Predef$.MODULE$.refArrayOps(conf.getAll()).toMap(Predef$.MODULE$.$conforms());
      RestSubmissionClient client = new RestSubmissionClient(master);
      CreateSubmissionRequest submitRequest = client.constructSubmitRequest(appResource, mainClass, appArgs, sparkProperties, env);
      return client.createSubmission(submitRequest);
   }

   public Map<String, String> run$default$5() {
      return (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
   }

   public void start(String[] args, SparkConf conf) {
      if (args.length < 2) {
         throw package$.MODULE$.error("Usage: RestSubmissionClient [app resource] [main class] [app args*]");
      } else {
         String appResource = args[0];
         String mainClass = args[1];
         String[] appArgs = (String[])Predef$.MODULE$.refArrayOps((Object[])args).slice(2, args.length);
         Map env = RestSubmissionClient$.MODULE$.filterSystemEnvironment(package$.MODULE$.env());
         this.run(appResource, mainClass, appArgs, conf, env);
      }
   }
}
