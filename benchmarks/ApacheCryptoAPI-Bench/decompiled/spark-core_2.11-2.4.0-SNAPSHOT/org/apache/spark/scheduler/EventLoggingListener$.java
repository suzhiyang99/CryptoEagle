package org.apache.spark.scheduler;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.util.JsonProtocol$;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.runtime.TraitSetter;

public final class EventLoggingListener$ implements Logging {
   public static final EventLoggingListener$ MODULE$;
   private final String IN_PROGRESS;
   private final String DEFAULT_LOG_DIR;
   private final FsPermission org$apache$spark$scheduler$EventLoggingListener$$LOG_FILE_PERMISSIONS;
   private final HashMap<String, CompressionCodec> org$apache$spark$scheduler$EventLoggingListener$$codecMap;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new EventLoggingListener$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public String IN_PROGRESS() {
      return this.IN_PROGRESS;
   }

   public String DEFAULT_LOG_DIR() {
      return this.DEFAULT_LOG_DIR;
   }

   public FsPermission org$apache$spark$scheduler$EventLoggingListener$$LOG_FILE_PERMISSIONS() {
      return this.org$apache$spark$scheduler$EventLoggingListener$$LOG_FILE_PERMISSIONS;
   }

   public HashMap<String, CompressionCodec> org$apache$spark$scheduler$EventLoggingListener$$codecMap() {
      return this.org$apache$spark$scheduler$EventLoggingListener$$codecMap;
   }

   public void initEventLog(OutputStream logStream, boolean testing, ArrayBuffer<JsonAST.JValue> loggedEvents) {
      SparkListenerLogStart metadata = new SparkListenerLogStart(org.apache.spark.package$.MODULE$.SPARK_VERSION());
      JsonAST.JValue eventJson = JsonProtocol$.MODULE$.logStartToJson(metadata);
      String metadataJson = (new StringBuilder()).append((Object).MODULE$.compact(eventJson)).append((Object)"\n").toString();
      logStream.write(metadataJson.getBytes(StandardCharsets.UTF_8));
      if (testing && loggedEvents != null) {
         loggedEvents.$plus$eq(eventJson);
      }

   }

   public String getLogPath(URI logBaseDir, String appId, Option<String> appAttemptId, Option<String> compressionCodecName) {
      String base = (new StringBuilder()).append((Object)(new StringOps(Predef$.MODULE$.augmentString((new Path(logBaseDir)).toString()))).stripSuffix("/")).append((Object)"/").append((Object)this.sanitize(appId)).toString();
      String codec = (String)compressionCodecName.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$5) {
            return (new StringBuilder()).append((Object)".").append((Object)x$5).toString();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      return appAttemptId.isDefined() ? (new StringBuilder()).append((Object)base).append((Object)"_").append((Object)this.sanitize((String)appAttemptId.get())).append((Object)codec).toString() : (new StringBuilder()).append((Object)base).append((Object)codec).toString();
   }

   public Option<String> getLogPath$default$4() {
      return None$.MODULE$;
   }

   private String sanitize(String str) {
      return str.replaceAll("[ :/]", "-").replaceAll("[.${}'\"]", "_").toLowerCase(Locale.ROOT);
   }

   public InputStream openEventLog(Path log, FileSystem fs) {
      BufferedInputStream in = new BufferedInputStream(fs.open(log));

      try {
         Option codec = this.codecName(log).map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final CompressionCodec apply(String c) {
               return (CompressionCodec)EventLoggingListener$.MODULE$.org$apache$spark$scheduler$EventLoggingListener$$codecMap().getOrElseUpdate(c, new Serializable(this, c) {
                  public static final long serialVersionUID = 0L;
                  private final String c$1;

                  public final CompressionCodec apply() {
                     return CompressionCodec$.MODULE$.createCodec(new SparkConf(), this.c$1);
                  }

                  public {
                     this.c$1 = c$1;
                  }
               });
            }
         });
         return (InputStream)codec.map(new Serializable(in) {
            public static final long serialVersionUID = 0L;
            private final BufferedInputStream in$1;

            public final InputStream apply(CompressionCodec x$6) {
               return x$6.compressedInputStream(this.in$1);
            }

            public {
               this.in$1 = in$1;
            }
         }).getOrElse(new Serializable(in) {
            public static final long serialVersionUID = 0L;
            private final BufferedInputStream in$1;

            public final BufferedInputStream apply() {
               return this.in$1;
            }

            public {
               this.in$1 = in$1;
            }
         });
      } finally {
         in.close();
      }
   }

   public Option<String> codecName(Path log) {
      String logName = (new StringOps(Predef$.MODULE$.augmentString(log.getName()))).stripSuffix(this.IN_PROGRESS());
      return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])logName.split("\\.")).tail()).lastOption();
   }

   private EventLoggingListener$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.IN_PROGRESS = ".inprogress";
      this.DEFAULT_LOG_DIR = "/tmp/spark-events";
      this.org$apache$spark$scheduler$EventLoggingListener$$LOG_FILE_PERMISSIONS = new FsPermission((short)Integer.parseInt("770", 8));
      this.org$apache$spark$scheduler$EventLoggingListener$$codecMap = new HashMap<String, CompressionCodec>();
   }
}
