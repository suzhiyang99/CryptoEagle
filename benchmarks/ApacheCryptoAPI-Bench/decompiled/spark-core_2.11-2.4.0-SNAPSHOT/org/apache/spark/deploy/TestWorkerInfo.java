package org.apache.spark.deploy;

import java.io.File;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.json4s.DefaultFormats;
import org.json4s.DefaultFormats.;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m3A!\u0001\u0002\u0005\u0017\tqA+Z:u/>\u00148.\u001a:J]\u001a|'BA\u0002\u0005\u0003\u0019!W\r\u001d7ps*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'Yi\u0011\u0001\u0006\u0006\u0003+\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003/Q\u0011q\u0001T8hO&tw\r\u0003\u0005\u001a\u0001\t\u0015\r\u0011\"\u0001\u001b\u0003\tI\u0007/F\u0001\u001c!\tarD\u0004\u0002\u000e;%\u0011aDD\u0001\u0007!J,G-\u001a4\n\u0005\u0001\n#AB*ue&twM\u0003\u0002\u001f\u001d!A1\u0005\u0001B\u0001B\u0003%1$A\u0002ja\u0002B\u0001\"\n\u0001\u0003\u0006\u0004%\tAJ\u0001\tI>\u001c7.\u001a:JIV\tq\u0005\u0005\u0002)S5\t!!\u0003\u0002+\u0005\tAAi\\2lKJLE\r\u0003\u0005-\u0001\t\u0005\t\u0015!\u0003(\u0003%!wnY6fe&#\u0007\u0005\u0003\u0005/\u0001\t\u0015\r\u0011\"\u00010\u0003\u001dawn\u001a$jY\u0016,\u0012\u0001\r\t\u0003cYj\u0011A\r\u0006\u0003gQ\n!![8\u000b\u0003U\nAA[1wC&\u0011qG\r\u0002\u0005\r&dW\r\u0003\u0005:\u0001\t\u0005\t\u0015!\u00031\u0003!awn\u001a$jY\u0016\u0004\u0003\"B\u001e\u0001\t\u0003a\u0014A\u0002\u001fj]&$h\b\u0006\u0003>}}\u0002\u0005C\u0001\u0015\u0001\u0011\u0015I\"\b1\u0001\u001c\u0011\u0015)#\b1\u0001(\u0011\u0015q#\b1\u00011\u0011\u001d\u0011\u0005A1A\u0005\u0004\r\u000bqAZ8s[\u0006$8/F\u0001E\u001d\t)UJ\u0004\u0002G\u0017:\u0011qIS\u0007\u0002\u0011*\u0011\u0011JC\u0001\u0007yI|w\u000e\u001e \n\u0003%I!\u0001\u0014\u0005\u0002\r)\u001cxN\u001c\u001bt\u0013\tqu*\u0001\bEK\u001a\fW\u000f\u001c;G_Jl\u0017\r^:\u000b\u00051C\u0001BB)\u0001A\u0003%A)\u0001\u0005g_Jl\u0017\r^:!\u0011\u0015\u0019\u0006\u0001\"\u0001U\u0003\u0011Y\u0017\u000e\u001c7\u0015\u0003U\u0003\"!\u0004,\n\u0005]s!\u0001B+oSRDQ!\u0017\u0001\u0005Bi\u000b\u0001\u0002^8TiJLgn\u001a\u000b\u00027\u0001"
)
public class TestWorkerInfo implements Logging {
   private final String ip;
   private final DockerId dockerId;
   private final File logFile;
   private final DefaultFormats formats;
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

   public String ip() {
      return this.ip;
   }

   public DockerId dockerId() {
      return this.dockerId;
   }

   public File logFile() {
      return this.logFile;
   }

   public DefaultFormats formats() {
      return this.formats;
   }

   public void kill() {
      Docker$.MODULE$.kill(this.dockerId());
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("[ip=%s, id=%s, logFile=%s]"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.ip(), this.dockerId(), this.logFile().getAbsolutePath()}));
   }

   public TestWorkerInfo(String ip, DockerId dockerId, File logFile) {
      this.ip = ip;
      this.dockerId = dockerId;
      this.logFile = logFile;
      Logging$class.$init$(this);
      this.formats = .MODULE$;
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TestWorkerInfo $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Created worker: ").append((Object)this.$outer).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }
}
