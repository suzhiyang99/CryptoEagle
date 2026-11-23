package org.apache.spark.deploy.worker;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import org.apache.spark.SparkConf;
import org.apache.spark.util.IntParam$;
import org.apache.spark.util.MemoryParam$;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Predef$;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]b!B\u0001\u0003\u0001\ta!aD,pe.,'/\u0011:hk6,g\u000e^:\u000b\u0005\r!\u0011AB<pe.,'O\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\t\u0005\u0014xm]\u0002\u0001!\rqq#G\u0005\u00031=\u0011Q!\u0011:sCf\u0004\"AG\u000f\u000f\u00059Y\u0012B\u0001\u000f\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011ad\b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005qy\u0001\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\t\r|gN\u001a\t\u0003G\u0011j\u0011AB\u0005\u0003K\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000b\u001d\u0002A\u0011\u0001\u0015\u0002\rqJg.\u001b;?)\rI3\u0006\f\t\u0003U\u0001i\u0011A\u0001\u0005\u0006)\u0019\u0002\rA\u0006\u0005\u0006C\u0019\u0002\rA\t\u0005\b]\u0001\u0001\r\u0011\"\u00010\u0003\u0011Awn\u001d;\u0016\u0003eAq!\r\u0001A\u0002\u0013\u0005!'\u0001\u0005i_N$x\fJ3r)\t\u0019d\u0007\u0005\u0002\u000fi%\u0011Qg\u0004\u0002\u0005+:LG\u000fC\u00048a\u0005\u0005\t\u0019A\r\u0002\u0007a$\u0013\u0007\u0003\u0004:\u0001\u0001\u0006K!G\u0001\u0006Q>\u001cH\u000f\t\u0005\bw\u0001\u0001\r\u0011\"\u0001=\u0003\u0011\u0001xN\u001d;\u0016\u0003u\u0002\"A\u0004 \n\u0005}z!aA%oi\"9\u0011\t\u0001a\u0001\n\u0003\u0011\u0015\u0001\u00039peR|F%Z9\u0015\u0005M\u001a\u0005bB\u001cA\u0003\u0003\u0005\r!\u0010\u0005\u0007\u000b\u0002\u0001\u000b\u0015B\u001f\u0002\u000bA|'\u000f\u001e\u0011\t\u000f\u001d\u0003\u0001\u0019!C\u0001y\u0005Iq/\u001a2VSB{'\u000f\u001e\u0005\b\u0013\u0002\u0001\r\u0011\"\u0001K\u000359XMY+j!>\u0014Ho\u0018\u0013fcR\u00111g\u0013\u0005\bo!\u000b\t\u00111\u0001>\u0011\u0019i\u0005\u0001)Q\u0005{\u0005Qq/\u001a2VSB{'\u000f\u001e\u0011\t\u000f=\u0003\u0001\u0019!C\u0001y\u0005)1m\u001c:fg\"9\u0011\u000b\u0001a\u0001\n\u0003\u0011\u0016!C2pe\u0016\u001cx\fJ3r)\t\u00194\u000bC\u00048!\u0006\u0005\t\u0019A\u001f\t\rU\u0003\u0001\u0015)\u0003>\u0003\u0019\u0019wN]3tA!9q\u000b\u0001a\u0001\n\u0003a\u0014AB7f[>\u0014\u0018\u0010C\u0004Z\u0001\u0001\u0007I\u0011\u0001.\u0002\u00155,Wn\u001c:z?\u0012*\u0017\u000f\u0006\u000247\"9q\u0007WA\u0001\u0002\u0004i\u0004BB/\u0001A\u0003&Q(A\u0004nK6|'/\u001f\u0011\t\u000f}\u0003\u0001\u0019!C\u0001A\u00069Q.Y:uKJ\u001cX#\u0001\f\t\u000f\t\u0004\u0001\u0019!C\u0001G\u0006YQ.Y:uKJ\u001cx\fJ3r)\t\u0019D\rC\u00048C\u0006\u0005\t\u0019\u0001\f\t\r\u0019\u0004\u0001\u0015)\u0003\u0017\u0003!i\u0017m\u001d;feN\u0004\u0003b\u00025\u0001\u0001\u0004%\taL\u0001\bo>\u00148\u000eR5s\u0011\u001dQ\u0007\u00011A\u0005\u0002-\f1b^8sW\u0012K'o\u0018\u0013fcR\u00111\u0007\u001c\u0005\bo%\f\t\u00111\u0001\u001a\u0011\u0019q\u0007\u0001)Q\u00053\u0005Aqo\u001c:l\t&\u0014\b\u0005C\u0004q\u0001\u0001\u0007I\u0011A\u0018\u0002\u001dA\u0014x\u000e]3si&,7OR5mK\"9!\u000f\u0001a\u0001\n\u0003\u0019\u0018A\u00059s_B,'\u000f^5fg\u001aKG.Z0%KF$\"a\r;\t\u000f]\n\u0018\u0011!a\u00013!1a\u000f\u0001Q!\ne\tq\u0002\u001d:pa\u0016\u0014H/[3t\r&dW\r\t\u0005\u0006q\u0002!I!_\u0001\u0006a\u0006\u00148/\u001a\u000b\u0003giDQ\u0001F<A\u0002m\u0004B\u0001`A\u000539\u0019Q0!\u0002\u000f\u0007y\f\u0019!D\u0001\u0000\u0015\r\t\t!F\u0001\u0007yI|w\u000e\u001e \n\u0003AI1!a\u0002\u0010\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\u0003\u0002\u000e\t!A*[:u\u0015\r\t9a\u0004\u0015\u0004o\u0006E\u0001\u0003BA\n\u00033i!!!\u0006\u000b\u0007\u0005]q\"\u0001\u0006b]:|G/\u0019;j_:LA!a\u0007\u0002\u0016\t9A/Y5me\u0016\u001c\u0007bBA\u0010\u0001\u0011\u0005\u0011\u0011E\u0001\u0012aJLg\u000e^+tC\u001e,\u0017I\u001c3Fq&$HcA\u001a\u0002$!9\u0011QEA\u000f\u0001\u0004i\u0014\u0001C3ySR\u001cu\u000eZ3\t\u000f\u0005%\u0002\u0001\"\u0001\u0002,\u0005\t\u0012N\u001c4fe\u0012+g-Y;mi\u000e{'/Z:\u0015\u0003uBq!a\f\u0001\t\u0003\tY#\u0001\nj]\u001a,'\u000fR3gCVdG/T3n_JL\bbBA\u001a\u0001\u0011\u0005\u0011QG\u0001\u0012G\",7m[,pe.,'/T3n_JLH#A\u001a"
)
public class WorkerArguments {
   private String host;
   private int port;
   private int webUiPort;
   private int cores;
   private int memory;
   private String[] masters;
   private String workDir;
   private String propertiesFile;

   public String host() {
      return this.host;
   }

   public void host_$eq(String x$1) {
      this.host = x$1;
   }

   public int port() {
      return this.port;
   }

   public void port_$eq(int x$1) {
      this.port = x$1;
   }

   public int webUiPort() {
      return this.webUiPort;
   }

   public void webUiPort_$eq(int x$1) {
      this.webUiPort = x$1;
   }

   public int cores() {
      return this.cores;
   }

   public void cores_$eq(int x$1) {
      this.cores = x$1;
   }

   public int memory() {
      return this.memory;
   }

   public void memory_$eq(int x$1) {
      this.memory = x$1;
   }

   public String[] masters() {
      return this.masters;
   }

   public void masters_$eq(String[] x$1) {
      this.masters = x$1;
   }

   public String workDir() {
      return this.workDir;
   }

   public void workDir_$eq(String x$1) {
      this.workDir = x$1;
   }

   public String propertiesFile() {
      return this.propertiesFile;
   }

   public void propertiesFile_$eq(String x$1) {
      this.propertiesFile = x$1;
   }

   private void parse(List<String> args) {
      while(true) {
         boolean var3 = false;
         $colon$colon var4 = null;
         if (args instanceof $colon$colon) {
            var3 = true;
            var4 = ($colon$colon)args;
            String var6 = (String)var4.head();
            List var7 = var4.tl$1();
            boolean var8;
            if ("--ip".equals(var6)) {
               var8 = true;
            } else if ("-i".equals(var6)) {
               var8 = true;
            } else {
               var8 = false;
            }

            if (var8 && var7 instanceof $colon$colon) {
               $colon$colon var9 = ($colon$colon)var7;
               String value = (String)var9.head();
               List tail = var9.tl$1();
               Utils$.MODULE$.checkHost(value);
               this.host_$eq(value);
               args = tail;
               continue;
            }
         }

         if (var3) {
            String var13 = (String)var4.head();
            List var14 = var4.tl$1();
            boolean var15;
            if ("--host".equals(var13)) {
               var15 = true;
            } else if ("-h".equals(var13)) {
               var15 = true;
            } else {
               var15 = false;
            }

            if (var15 && var14 instanceof $colon$colon) {
               $colon$colon var16 = ($colon$colon)var14;
               String value = (String)var16.head();
               List tail = var16.tl$1();
               Utils$.MODULE$.checkHost(value);
               this.host_$eq(value);
               args = tail;
               continue;
            }
         }

         if (var3) {
            String var19 = (String)var4.head();
            List var20 = var4.tl$1();
            boolean var21;
            if ("--port".equals(var19)) {
               var21 = true;
            } else if ("-p".equals(var19)) {
               var21 = true;
            } else {
               var21 = false;
            }

            if (var21 && var20 instanceof $colon$colon) {
               $colon$colon var22 = ($colon$colon)var20;
               String var23 = (String)var22.head();
               List tail = var22.tl$1();
               Option var25 = IntParam$.MODULE$.unapply(var23);
               if (!var25.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var25.get());
                  this.port_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var27 = (String)var4.head();
            List var28 = var4.tl$1();
            boolean var29;
            if ("--cores".equals(var27)) {
               var29 = true;
            } else if ("-c".equals(var27)) {
               var29 = true;
            } else {
               var29 = false;
            }

            if (var29 && var28 instanceof $colon$colon) {
               $colon$colon var30 = ($colon$colon)var28;
               String var31 = (String)var30.head();
               List tail = var30.tl$1();
               Option var33 = IntParam$.MODULE$.unapply(var31);
               if (!var33.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var33.get());
                  this.cores_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var35 = (String)var4.head();
            List var36 = var4.tl$1();
            boolean var37;
            if ("--memory".equals(var35)) {
               var37 = true;
            } else if ("-m".equals(var35)) {
               var37 = true;
            } else {
               var37 = false;
            }

            if (var37 && var36 instanceof $colon$colon) {
               $colon$colon var38 = ($colon$colon)var36;
               String var39 = (String)var38.head();
               List tail = var38.tl$1();
               Option var41 = MemoryParam$.MODULE$.unapply(var39);
               if (!var41.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var41.get());
                  this.memory_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var43 = (String)var4.head();
            List var44 = var4.tl$1();
            boolean var45;
            if ("--work-dir".equals(var43)) {
               var45 = true;
            } else if ("-d".equals(var43)) {
               var45 = true;
            } else {
               var45 = false;
            }

            if (var45 && var44 instanceof $colon$colon) {
               $colon$colon var46 = ($colon$colon)var44;
               String value = (String)var46.head();
               List tail = var46.tl$1();
               this.workDir_$eq(value);
               args = tail;
               continue;
            }
         }

         if (var3) {
            String var49 = (String)var4.head();
            List var50 = var4.tl$1();
            if ("--webui-port".equals(var49) && var50 instanceof $colon$colon) {
               $colon$colon var51 = ($colon$colon)var50;
               String var52 = (String)var51.head();
               List tail = var51.tl$1();
               Option var54 = IntParam$.MODULE$.unapply(var52);
               if (!var54.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var54.get());
                  this.webUiPort_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var56 = (String)var4.head();
            List var57 = var4.tl$1();
            if ("--properties-file".equals(var56) && var57 instanceof $colon$colon) {
               $colon$colon var58 = ($colon$colon)var57;
               String value = (String)var58.head();
               List tail = var58.tl$1();
               this.propertiesFile_$eq(value);
               args = tail;
               continue;
            }
         }

         label152: {
            if (var3) {
               String var61 = (String)var4.head();
               if ("--help".equals(var61)) {
                  this.printUsageAndExit(0);
                  BoxedUnit var64 = BoxedUnit.UNIT;
                  break label152;
               }
            }

            if (var3) {
               String value = (String)var4.head();
               List tail = var4.tl$1();
               if (this.masters() != null) {
                  this.printUsageAndExit(1);
               }

               this.masters_$eq(Utils$.MODULE$.parseStandaloneMasterUrls(value));
               args = tail;
               continue;
            }

            if (Nil$.MODULE$.equals(args)) {
               if (this.masters() == null) {
                  this.printUsageAndExit(1);
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var65 = BoxedUnit.UNIT;
               }
            } else {
               this.printUsageAndExit(1);
               BoxedUnit var12 = BoxedUnit.UNIT;
            }
         }

         BoxedUnit var66 = BoxedUnit.UNIT;
         return;
      }
   }

   public void printUsageAndExit(int exitCode) {
      System.err.println("Usage: Worker [options] <master>\n\nMaster must be a URL of the form spark://hostname:port\n\nOptions:\n  -c CORES, --cores CORES  Number of cores to use\n  -m MEM, --memory MEM     Amount of memory to use (e.g. 1000M, 2G)\n  -d DIR, --work-dir DIR   Directory to run apps in (default: SPARK_HOME/work)\n  -i HOST, --ip IP         Hostname to listen on (deprecated, please use --host or -h)\n  -h HOST, --host HOST     Hostname to listen on\n  -p PORT, --port PORT     Port to listen on (default: random)\n  --webui-port PORT        Port for web UI (default: 8081)\n  --properties-file FILE   Path to a custom Spark properties file.\n                           Default is conf/spark-defaults.conf.");
      System.exit(exitCode);
   }

   public int inferDefaultCores() {
      return Runtime.getRuntime().availableProcessors();
   }

   public int inferDefaultMemory() {
      boolean ibmVendor = System.getProperty("java.vendor").contains("IBM");
      int totalMb = 0;

      try {
         OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
         if (ibmVendor) {
            Class beanClass = Class.forName("com.ibm.lang.management.OperatingSystemMXBean");
            Method method = beanClass.getDeclaredMethod("getTotalPhysicalMemory");
            totalMb = (int)(BoxesRunTime.unboxToLong(method.invoke(bean)) / 1024L / 1024L);
         } else {
            Class beanClass = Class.forName("com.sun.management.OperatingSystemMXBean");
            Method method = beanClass.getDeclaredMethod("getTotalPhysicalMemorySize");
            totalMb = (int)(BoxesRunTime.unboxToLong(method.invoke(bean)) / 1024L / 1024L);
         }
      } catch (Exception var9) {
         totalMb = 2048;
         System.out.println((new StringBuilder()).append((Object)"Failed to get total physical memory. Using ").append((Object)BoxesRunTime.boxToInteger(totalMb)).append((Object)" MB").toString());
      }

      return package$.MODULE$.max(totalMb - 1024, Utils$.MODULE$.DEFAULT_DRIVER_MEM_MB());
   }

   public void checkWorkerMemory() {
      if (this.memory() <= 0) {
         String message = "Memory is below 1MB, or missing a M/G at the end of the memory specification?";
         throw new IllegalStateException(message);
      }
   }

   public WorkerArguments(String[] args, SparkConf conf) {
      this.host = Utils$.MODULE$.localHostName();
      this.port = 0;
      this.webUiPort = 8081;
      this.cores = this.inferDefaultCores();
      this.memory = this.inferDefaultMemory();
      this.masters = null;
      this.workDir = null;
      this.propertiesFile = null;
      if (System.getenv("SPARK_WORKER_PORT") != null) {
         this.port_$eq((new StringOps(Predef$.MODULE$.augmentString(System.getenv("SPARK_WORKER_PORT")))).toInt());
      }

      if (System.getenv("SPARK_WORKER_CORES") != null) {
         this.cores_$eq((new StringOps(Predef$.MODULE$.augmentString(System.getenv("SPARK_WORKER_CORES")))).toInt());
      }

      if (conf.getenv("SPARK_WORKER_MEMORY") != null) {
         this.memory_$eq(Utils$.MODULE$.memoryStringToMb(conf.getenv("SPARK_WORKER_MEMORY")));
      }

      if (System.getenv("SPARK_WORKER_WEBUI_PORT") != null) {
         this.webUiPort_$eq((new StringOps(Predef$.MODULE$.augmentString(System.getenv("SPARK_WORKER_WEBUI_PORT")))).toInt());
      }

      if (System.getenv("SPARK_WORKER_DIR") != null) {
         this.workDir_$eq(System.getenv("SPARK_WORKER_DIR"));
      }

      this.parse(Predef$.MODULE$.refArrayOps((Object[])args).toList());
      this.propertiesFile_$eq(Utils$.MODULE$.loadDefaultSparkProperties(conf, this.propertiesFile()));
      if (conf.contains("spark.worker.ui.port")) {
         this.webUiPort_$eq((new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.worker.ui.port")))).toInt());
      }

      this.checkWorkerMemory();
   }
}
