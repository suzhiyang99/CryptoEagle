package org.apache.spark.storage;

import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001)3A!\u0001\u0002\u0001\u0017\t9b)\u001b7f\u0005\u0006\u001cX\r\u001a+pa>dwnZ=NCB\u0004XM\u001d\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\u0007\u0011!\tia\"D\u0001\u0003\u0013\ty!A\u0001\bU_B|Gn\\4z\u001b\u0006\u0004\b/\u001a:\u0011\u0005E!R\"\u0001\n\u000b\u0005M!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005U\u0011\"a\u0002'pO\u001eLgn\u001a\u0005\t/\u0001\u0011\t\u0011)A\u00051\u0005!1m\u001c8g!\tI\"$D\u0001\u0005\u0013\tYBAA\u0005Ta\u0006\u00148nQ8oM\")Q\u0004\u0001C\u0001=\u00051A(\u001b8jiz\"\"a\b\u0011\u0011\u00055\u0001\u0001\"B\f\u001d\u0001\u0004A\u0002b\u0002\u0012\u0001\u0005\u0004%\taI\u0001\ri>\u0004x\u000e\\8hs\u001aKG.Z\u000b\u0002IA\u0019Q\u0005\u000b\u0016\u000e\u0003\u0019R\u0011aJ\u0001\u0006g\u000e\fG.Y\u0005\u0003S\u0019\u0012aa\u00149uS>t\u0007CA\u0016/\u001d\t)C&\u0003\u0002.M\u00051\u0001K]3eK\u001aL!a\f\u0019\u0003\rM#(/\u001b8h\u0015\tic\u0005\u0003\u00043\u0001\u0001\u0006I\u0001J\u0001\u000ei>\u0004x\u000e\\8hs\u001aKG.\u001a\u0011\t\u000fQ\u0002!\u0019!C\u0001k\u0005YAo\u001c9pY><\u00170T1q+\u00051\u0004\u0003B\u001c;U)j\u0011\u0001\u000f\u0006\u0003s\u0019\n!bY8mY\u0016\u001cG/[8o\u0013\tY\u0004HA\u0002NCBDa!\u0010\u0001!\u0002\u00131\u0014\u0001\u0004;pa>dwnZ=NCB\u0004\u0003\"B \u0001\t\u0003\u0002\u0015AE4fiR{\u0007o\u001c7pOf4uN\u001d%pgR$\"\u0001J!\t\u000b\ts\u0004\u0019\u0001\u0016\u0002\u0011!|7\u000f\u001e8b[\u0016D#\u0001\u0001#\u0011\u0005\u0015CU\"\u0001$\u000b\u0005\u001d#\u0011AC1o]>$\u0018\r^5p]&\u0011\u0011J\u0012\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public class FileBasedTopologyMapper extends TopologyMapper implements Logging {
   private final Option<String> topologyFile;
   private final Map<String, String> topologyMap;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public Option<String> topologyFile() {
      return this.topologyFile;
   }

   public Map<String, String> topologyMap() {
      return this.topologyMap;
   }

   public Option<String> getTopologyForHost(String hostname) {
      Option topology = this.topologyMap().get(hostname);
      if (topology.isDefined()) {
         this.logDebug(new Serializable(this, hostname, topology) {
            public static final long serialVersionUID = 0L;
            private final String hostname$2;
            private final Option topology$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " -> ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hostname$2, this.topology$1.get()}));
            }

            public {
               this.hostname$2 = hostname$2;
               this.topology$1 = topology$1;
            }
         });
      } else {
         this.logWarning(new Serializable(this, hostname) {
            public static final long serialVersionUID = 0L;
            private final String hostname$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " does not have any topology information"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hostname$2}));
            }

            public {
               this.hostname$2 = hostname$2;
            }
         });
      }

      return topology;
   }

   public FileBasedTopologyMapper(SparkConf conf) {
      super(conf);
      Logging$class.$init$(this);
      this.topologyFile = conf.getOption("spark.storage.replication.topologyFile");
      Predef$.MODULE$.require(this.topologyFile().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Please specify topology file via spark.storage.replication.topologyFile for FileBasedTopologyMapper.";
         }
      });
      this.topologyMap = Utils$.MODULE$.getPropertiesFromFile((String)this.topologyFile().get());
   }
}
