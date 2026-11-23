package org.apache.spark.deploy;

import java.io.File;
import java.io.FileWriter;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.MatchError;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.concurrent.duration.package;
import scala.concurrent.duration.package$;
import scala.runtime.BoxedUnit;
import scala.sys.process.ProcessBuilder;
import scala.sys.process.ProcessLogger$;

public final class SparkDocker$ {
   public static final SparkDocker$ MODULE$;

   static {
      new SparkDocker$();
   }

   public TestMasterInfo startMaster(String mountDir) {
      String x$17 = "spark-test-master";
      String x$19 = Docker$.MODULE$.makeRunCmd$default$2();
      ProcessBuilder cmd = Docker$.MODULE$.makeRunCmd(x$17, x$19, mountDir);
      Tuple3 var7 = this.startNode(cmd);
      if (var7 != null) {
         String ip = (String)var7._1();
         DockerId id = (DockerId)var7._2();
         File outFile = (File)var7._3();
         Tuple3 var11 = new Tuple3(ip, id, outFile);
         String ip = (String)var11._1();
         DockerId id = (DockerId)var11._2();
         File outFile = (File)var11._3();
         return new TestMasterInfo(ip, id, outFile);
      } else {
         throw new MatchError(var7);
      }
   }

   public TestWorkerInfo startWorker(String mountDir, String masters) {
      ProcessBuilder cmd = Docker$.MODULE$.makeRunCmd("spark-test-worker", masters, mountDir);
      Tuple3 var5 = this.startNode(cmd);
      if (var5 != null) {
         String ip = (String)var5._1();
         DockerId id = (DockerId)var5._2();
         File outFile = (File)var5._3();
         Tuple3 var9 = new Tuple3(ip, id, outFile);
         String ip = (String)var9._1();
         DockerId id = (DockerId)var9._2();
         File outFile = (File)var9._3();
         return new TestWorkerInfo(ip, id, outFile);
      } else {
         throw new MatchError(var5);
      }
   }

   private Tuple3<String, DockerId, File> startNode(ProcessBuilder dockerCmd) {
      Promise ipPromise = Promise$.MODULE$.apply();
      File outFile = File.createTempFile("fault-tolerance-test", "", Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2()));
      FileWriter outStream = new FileWriter(outFile);
      dockerCmd.run(ProcessLogger$.MODULE$.apply((Function1)(new Serializable(ipPromise, outStream) {
         public static final long serialVersionUID = 0L;
         private final Promise ipPromise$1;
         private final FileWriter outStream$1;

         public final void apply(String line) {
            SparkDocker$.MODULE$.org$apache$spark$deploy$SparkDocker$$findIpAndLog$1(line, this.ipPromise$1, this.outStream$1);
         }

         public {
            this.ipPromise$1 = ipPromise$1;
            this.outStream$1 = outStream$1;
         }
      })));
      String ip = (String)ThreadUtils$.MODULE$.awaitResult(ipPromise.future(), (new package.DurationInt(package$.MODULE$.DurationInt(30))).seconds());
      DockerId dockerId = Docker$.MODULE$.getLastProcessId();
      return new Tuple3<String, DockerId, File>(ip, dockerId, outFile);
   }

   public final void org$apache$spark$deploy$SparkDocker$$findIpAndLog$1(String line, Promise ipPromise$1, FileWriter outStream$1) {
      if (line.startsWith("CONTAINER_IP=")) {
         String ip = line.split("=")[1];
         ipPromise$1.success(ip);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      outStream$1.write((new StringBuilder()).append((Object)line).append((Object)"\n").toString());
      outStream$1.flush();
   }

   private SparkDocker$() {
      MODULE$ = this;
   }
}
