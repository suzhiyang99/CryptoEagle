package org.apache.spark.storage;

import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001i2A!\u0001\u0002\u0001\u0017\t)B)\u001a4bk2$Hk\u001c9pY><\u00170T1qa\u0016\u0014(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001A\u0002\u0005\t\u0003\u001b9i\u0011AA\u0005\u0003\u001f\t\u0011a\u0002V8q_2|w-_'baB,'\u000f\u0005\u0002\u0012)5\t!C\u0003\u0002\u0014\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0016%\t9Aj\\4hS:<\u0007\u0002C\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\t\r|gN\u001a\t\u00033ii\u0011\u0001B\u0005\u00037\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000bu\u0001A\u0011\u0001\u0010\u0002\rqJg.\u001b;?)\ty\u0002\u0005\u0005\u0002\u000e\u0001!)q\u0003\ba\u00011!)!\u0005\u0001C!G\u0005\u0011r-\u001a;U_B|Gn\\4z\r>\u0014\bj\\:u)\t!\u0013\u0007E\u0002&Q)j\u0011A\n\u0006\u0002O\u0005)1oY1mC&\u0011\u0011F\n\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005-rcBA\u0013-\u0013\tic%\u0001\u0004Qe\u0016$WMZ\u0005\u0003_A\u0012aa\u0015;sS:<'BA\u0017'\u0011\u0015\u0011\u0014\u00051\u0001+\u0003!Awn\u001d;oC6,\u0007F\u0001\u00015!\t)\u0004(D\u00017\u0015\t9D!\u0001\u0006b]:|G/\u0019;j_:L!!\u000f\u001c\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class DefaultTopologyMapper extends TopologyMapper implements Logging {
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

   public Option<String> getTopologyForHost(String hostname) {
      this.logDebug(new Serializable(this, hostname) {
         public static final long serialVersionUID = 0L;
         private final String hostname$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got a request for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hostname$1}));
         }

         public {
            this.hostname$1 = hostname$1;
         }
      });
      return None$.MODULE$;
   }

   public DefaultTopologyMapper(SparkConf conf) {
      super(conf);
      Logging$class.$init$(this);
   }
}
