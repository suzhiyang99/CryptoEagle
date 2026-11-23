package org.apache.spark.deploy.master;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.IntParam$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001a4Q!\u0001\u0002\u0001\u00051\u0011q\"T1ti\u0016\u0014\u0018I]4v[\u0016tGo\u001d\u0006\u0003\u0007\u0011\ta!\\1ti\u0016\u0014(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bM\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0018\u001b\u0005)\"B\u0001\f\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\r\u0016\u0005\u001daunZ4j]\u001eD\u0001B\u0007\u0001\u0003\u0002\u0003\u0006I\u0001H\u0001\u0005CJ<7o\u0001\u0001\u0011\u00079ir$\u0003\u0002\u001f\u001f\t)\u0011I\u001d:bsB\u0011\u0001e\t\b\u0003\u001d\u0005J!AI\b\u0002\rA\u0013X\rZ3g\u0013\t!SE\u0001\u0004TiJLgn\u001a\u0006\u0003E=A\u0001b\n\u0001\u0003\u0002\u0003\u0006I\u0001K\u0001\u0005G>tg\r\u0005\u0002*U5\ta!\u0003\u0002,\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\u0006[\u0001!\tAL\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007=\n$\u0007\u0005\u00021\u00015\t!\u0001C\u0003\u001bY\u0001\u0007A\u0004C\u0003(Y\u0001\u0007\u0001\u0006C\u00045\u0001\u0001\u0007I\u0011A\u001b\u0002\t!|7\u000f^\u000b\u0002?!9q\u0007\u0001a\u0001\n\u0003A\u0014\u0001\u00035pgR|F%Z9\u0015\u0005eb\u0004C\u0001\b;\u0013\tYtB\u0001\u0003V]&$\bbB\u001f7\u0003\u0003\u0005\raH\u0001\u0004q\u0012\n\u0004BB \u0001A\u0003&q$A\u0003i_N$\b\u0005C\u0004B\u0001\u0001\u0007I\u0011\u0001\"\u0002\tA|'\u000f^\u000b\u0002\u0007B\u0011a\u0002R\u0005\u0003\u000b>\u00111!\u00138u\u0011\u001d9\u0005\u00011A\u0005\u0002!\u000b\u0001\u0002]8si~#S-\u001d\u000b\u0003s%Cq!\u0010$\u0002\u0002\u0003\u00071\t\u0003\u0004L\u0001\u0001\u0006KaQ\u0001\u0006a>\u0014H\u000f\t\u0005\b\u001b\u0002\u0001\r\u0011\"\u0001C\u0003%9XMY+j!>\u0014H\u000fC\u0004P\u0001\u0001\u0007I\u0011\u0001)\u0002\u001b],'-V5Q_J$x\fJ3r)\tI\u0014\u000bC\u0004>\u001d\u0006\u0005\t\u0019A\"\t\rM\u0003\u0001\u0015)\u0003D\u0003)9XMY+j!>\u0014H\u000f\t\u0005\b+\u0002\u0001\r\u0011\"\u00016\u00039\u0001(o\u001c9feRLWm\u001d$jY\u0016Dqa\u0016\u0001A\u0002\u0013\u0005\u0001,\u0001\nqe>\u0004XM\u001d;jKN4\u0015\u000e\\3`I\u0015\fHCA\u001dZ\u0011\u001did+!AA\u0002}Aaa\u0017\u0001!B\u0013y\u0012a\u00049s_B,'\u000f^5fg\u001aKG.\u001a\u0011\t\u000bu\u0003A\u0011\u00020\u0002\u000bA\f'o]3\u0015\u0005ez\u0006\"\u0002\u000e]\u0001\u0004\u0001\u0007cA1j?9\u0011!m\u001a\b\u0003G\u001al\u0011\u0001\u001a\u0006\u0003Kn\ta\u0001\u0010:p_Rt\u0014\"\u0001\t\n\u0005!|\u0011a\u00029bG.\fw-Z\u0005\u0003U.\u0014A\u0001T5ti*\u0011\u0001n\u0004\u0015\u000396\u0004\"A\\9\u000e\u0003=T!\u0001]\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002s_\n9A/Y5me\u0016\u001c\u0007\"\u0002;\u0001\t\u0013)\u0018!\u00059sS:$Xk]1hK\u0006sG-\u0012=jiR\u0011\u0011H\u001e\u0005\u0006oN\u0004\raQ\u0001\tKbLGoQ8eK\u0002"
)
public class MasterArguments implements Logging {
   private String host;
   private int port;
   private int webUiPort;
   private String propertiesFile;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

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
            if ("--webui-port".equals(var27) && var28 instanceof $colon$colon) {
               $colon$colon var29 = ($colon$colon)var28;
               String var30 = (String)var29.head();
               List tail = var29.tl$1();
               Option var32 = IntParam$.MODULE$.unapply(var30);
               if (!var32.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var32.get());
                  this.webUiPort_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var34 = (String)var4.head();
            List var35 = var4.tl$1();
            if ("--properties-file".equals(var34) && var35 instanceof $colon$colon) {
               $colon$colon var36 = ($colon$colon)var35;
               String value = (String)var36.head();
               List tail = var36.tl$1();
               this.propertiesFile_$eq(value);
               args = tail;
               continue;
            }
         }

         label76: {
            if (var3) {
               String var39 = (String)var4.head();
               if ("--help".equals(var39)) {
                  this.printUsageAndExit(0);
                  BoxedUnit var41 = BoxedUnit.UNIT;
                  break label76;
               }
            }

            if (Nil$.MODULE$.equals(args)) {
               BoxedUnit var12 = BoxedUnit.UNIT;
            } else {
               this.printUsageAndExit(1);
               BoxedUnit var40 = BoxedUnit.UNIT;
            }
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
         return;
      }
   }

   private void printUsageAndExit(int exitCode) {
      System.err.println("Usage: Master [options]\n\nOptions:\n  -i HOST, --ip HOST     Hostname to listen on (deprecated, please use --host or -h) \n  -h HOST, --host HOST   Hostname to listen on\n  -p PORT, --port PORT   Port to listen on (default: 7077)\n  --webui-port PORT      Port for web UI (default: 8080)\n  --properties-file FILE Path to a custom Spark properties file.\n                         Default is conf/spark-defaults.conf.");
      System.exit(exitCode);
   }

   public MasterArguments(String[] args, SparkConf conf) {
      Logging$class.$init$(this);
      this.host = Utils$.MODULE$.localHostName();
      this.port = 7077;
      this.webUiPort = 8080;
      this.propertiesFile = null;
      if (System.getenv("SPARK_MASTER_IP") != null) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "SPARK_MASTER_IP is deprecated, please use SPARK_MASTER_HOST";
            }
         });
         this.host_$eq(System.getenv("SPARK_MASTER_IP"));
      }

      if (System.getenv("SPARK_MASTER_HOST") != null) {
         this.host_$eq(System.getenv("SPARK_MASTER_HOST"));
      }

      if (System.getenv("SPARK_MASTER_PORT") != null) {
         this.port_$eq((new StringOps(Predef$.MODULE$.augmentString(System.getenv("SPARK_MASTER_PORT")))).toInt());
      }

      if (System.getenv("SPARK_MASTER_WEBUI_PORT") != null) {
         this.webUiPort_$eq((new StringOps(Predef$.MODULE$.augmentString(System.getenv("SPARK_MASTER_WEBUI_PORT")))).toInt());
      }

      this.parse(Predef$.MODULE$.refArrayOps((Object[])args).toList());
      this.propertiesFile_$eq(Utils$.MODULE$.loadDefaultSparkProperties(conf, this.propertiesFile()));
      if (conf.contains("spark.master.ui.port")) {
         this.webUiPort_$eq((new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.master.ui.port")))).toInt());
      }

   }
}
