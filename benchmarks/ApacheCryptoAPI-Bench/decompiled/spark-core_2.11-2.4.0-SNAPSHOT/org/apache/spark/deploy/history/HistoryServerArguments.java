package org.apache.spark.deploy.history;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u00114Q!\u0001\u0002\u0001\u00051\u0011a\u0003S5ti>\u0014\u0018pU3sm\u0016\u0014\u0018I]4v[\u0016tGo\u001d\u0006\u0003\u0007\u0011\tq\u0001[5ti>\u0014\u0018P\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0019+\t9Aj\\4hS:<\u0007\u0002\u0003\u000e\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000f\u0002\t\r|gNZ\u0002\u0001!\tib$D\u0001\u0007\u0013\tybAA\u0005Ta\u0006\u00148nQ8oM\"A\u0011\u0005\u0001B\u0001B\u0003%!%\u0001\u0003be\u001e\u001c\bc\u0001\b$K%\u0011Ae\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003M%r!AD\u0014\n\u0005!z\u0011A\u0002)sK\u0012,g-\u0003\u0002+W\t11\u000b\u001e:j]\u001eT!\u0001K\b\t\u000b5\u0002A\u0011\u0001\u0018\u0002\rqJg.\u001b;?)\ry\u0013G\r\t\u0003a\u0001i\u0011A\u0001\u0005\u000651\u0002\r\u0001\b\u0005\u0006C1\u0002\rA\t\u0005\bi\u0001\u0001\r\u0011\"\u00036\u00039\u0001(o\u001c9feRLWm\u001d$jY\u0016,\u0012!\n\u0005\bo\u0001\u0001\r\u0011\"\u00039\u0003I\u0001(o\u001c9feRLWm\u001d$jY\u0016|F%Z9\u0015\u0005eb\u0004C\u0001\b;\u0013\tYtB\u0001\u0003V]&$\bbB\u001f7\u0003\u0003\u0005\r!J\u0001\u0004q\u0012\n\u0004BB \u0001A\u0003&Q%A\bqe>\u0004XM\u001d;jKN4\u0015\u000e\\3!\u0011\u0015\t\u0005\u0001\"\u0003C\u0003\u0015\u0001\u0018M]:f)\tI4\tC\u0003\"\u0001\u0002\u0007A\tE\u0002F\u001b\u0016r!AR&\u000f\u0005\u001dSU\"\u0001%\u000b\u0005%[\u0012A\u0002\u001fs_>$h(C\u0001\u0011\u0013\tau\"A\u0004qC\u000e\\\u0017mZ3\n\u00059{%\u0001\u0002'jgRT!\u0001T\b)\u0005\u0001\u000b\u0006C\u0001*V\u001b\u0005\u0019&B\u0001+\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0003-N\u0013q\u0001^1jYJ,7\rC\u0003Y\u0001\u0011%\u0011,A\btKRdun\u001a#je\u0016\u001cGo\u001c:z)\tI$\fC\u0003\\/\u0002\u0007Q%A\u0003wC2,X\rC\u0003^\u0001\u0011%a,A\tqe&tG/V:bO\u0016\fe\u000eZ#ySR$\"!O0\t\u000b\u0001d\u0006\u0019A1\u0002\u0011\u0015D\u0018\u000e^\"pI\u0016\u0004\"A\u00042\n\u0005\r|!aA%oi\u0002"
)
public class HistoryServerArguments implements Logging {
   private final SparkConf conf;
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

   private String propertiesFile() {
      return this.propertiesFile;
   }

   private void propertiesFile_$eq(String x$1) {
      this.propertiesFile = x$1;
   }

   private void parse(List<String> args) {
      while(true) {
         if (args.length() == 1) {
            this.setLogDirectory((String)args.head());
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            boolean var3 = false;
            $colon$colon var4 = null;
            if (args instanceof $colon$colon) {
               var3 = true;
               var4 = ($colon$colon)args;
               String var6 = (String)var4.head();
               List var7 = var4.tl$1();
               boolean var8;
               if ("--dir".equals(var6)) {
                  var8 = true;
               } else if ("-d".equals(var6)) {
                  var8 = true;
               } else {
                  var8 = false;
               }

               if (var8 && var7 instanceof $colon$colon) {
                  $colon$colon var9 = ($colon$colon)var7;
                  String value = (String)var9.head();
                  List tail = var9.tl$1();
                  this.setLogDirectory(value);
                  args = tail;
                  continue;
               }
            }

            label56: {
               if (var3) {
                  String var13 = (String)var4.head();
                  boolean var14;
                  if ("--help".equals(var13)) {
                     var14 = true;
                  } else if ("-h".equals(var13)) {
                     var14 = true;
                  } else {
                     var14 = false;
                  }

                  if (var14) {
                     this.printUsageAndExit(0);
                     BoxedUnit var21 = BoxedUnit.UNIT;
                     break label56;
                  }
               }

               if (var3) {
                  String var15 = (String)var4.head();
                  List var16 = var4.tl$1();
                  if ("--properties-file".equals(var15) && var16 instanceof $colon$colon) {
                     $colon$colon var17 = ($colon$colon)var16;
                     String value = (String)var17.head();
                     List tail = var17.tl$1();
                     this.propertiesFile_$eq(value);
                     args = tail;
                     continue;
                  }
               }

               if (Nil$.MODULE$.equals(args)) {
                  BoxedUnit var12 = BoxedUnit.UNIT;
               } else {
                  this.printUsageAndExit(1);
                  BoxedUnit var20 = BoxedUnit.UNIT;
               }
            }

            BoxedUnit var22 = BoxedUnit.UNIT;
         }

         return;
      }
   }

   private void setLogDirectory(String value) {
      this.logWarning(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Setting log directory through the command line is deprecated as of Spark 1.1.0. Please set this through spark.history.fs.logDirectory instead.";
         }
      });
      this.conf.set("spark.history.fs.logDirectory", value);
   }

   private void printUsageAndExit(int exitCode) {
      System.err.println((new StringOps(Predef$.MODULE$.augmentString("\n      |Usage: HistoryServer [options]\n      |\n      |Options:\n      |  DIR                         Deprecated; set spark.history.fs.logDirectory directly\n      |  --dir DIR (-d DIR)          Deprecated; set spark.history.fs.logDirectory directly\n      |  --properties-file FILE      Path to a custom Spark properties file.\n      |                              Default is conf/spark-defaults.conf.\n      |\n      |Configuration options can be set by setting the corresponding JVM system property.\n      |History Server options are always available; additional options depend on the provider.\n      |\n      |History Server options:\n      |\n      |  spark.history.ui.port              Port where server will listen for connections\n      |                                     (default 18080)\n      |  spark.history.acls.enable          Whether to enable view acls for all applications\n      |                                     (default false)\n      |  spark.history.provider             Name of history provider class (defaults to\n      |                                     file system-based provider)\n      |  spark.history.retainedApplications Max number of application UIs to keep loaded in memory\n      |                                     (default 50)\n      |FsHistoryProvider options:\n      |\n      |  spark.history.fs.logDirectory      Directory where app logs are stored\n      |                                     (default: file:/tmp/spark-events)\n      |  spark.history.fs.updateInterval    How often to reload log data from storage\n      |                                     (in seconds, default: 10)\n      |"))).stripMargin());
      System.exit(exitCode);
   }

   public HistoryServerArguments(SparkConf conf, String[] args) {
      this.conf = conf;
      Logging$class.$init$(this);
      this.propertiesFile = null;
      this.parse(Predef$.MODULE$.refArrayOps((Object[])args).toList());
      Utils$.MODULE$.loadDefaultSparkProperties(conf, this.propertiesFile());
   }
}
