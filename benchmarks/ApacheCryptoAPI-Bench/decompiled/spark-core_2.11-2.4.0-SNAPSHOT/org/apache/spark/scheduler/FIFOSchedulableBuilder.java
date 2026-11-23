package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00053Q!\u0001\u0002\u0001\t)\u0011aCR%G\u001fN\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\"vS2$WM\u001d\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0012+A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003%M\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\"vS2$WM\u001d\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\r\u0003\u0005\u001d\u0001\t\u0015\r\u0011\"\u0001\u001f\u0003!\u0011xn\u001c;Q_>d7\u0001A\u000b\u0002?A\u0011!\u0003I\u0005\u0003C\t\u0011A\u0001U8pY\"A1\u0005\u0001B\u0001B\u0003%q$A\u0005s_>$\bk\\8mA!)Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"\"a\n\u0015\u0011\u0005I\u0001\u0001\"\u0002\u000f%\u0001\u0004y\u0002\"\u0002\u0016\u0001\t\u0003Z\u0013A\u00032vS2$\u0007k\\8mgR\tA\u0006\u0005\u0002\r[%\u0011a&\u0004\u0002\u0005+:LG\u000fC\u00031\u0001\u0011\u0005\u0013'A\tbI\u0012$\u0016m]6TKRl\u0015M\\1hKJ$2\u0001\f\u001a8\u0011\u0015\u0019t\u00061\u00015\u0003\u001di\u0017M\\1hKJ\u0004\"AE\u001b\n\u0005Y\u0012!aC*dQ\u0016$W\u000f\\1cY\u0016DQ\u0001O\u0018A\u0002e\n!\u0002\u001d:pa\u0016\u0014H/[3t!\tQt(D\u0001<\u0015\taT(\u0001\u0003vi&d'\"\u0001 \u0002\t)\fg/Y\u0005\u0003\u0001n\u0012!\u0002\u0015:pa\u0016\u0014H/[3t\u0001"
)
public class FIFOSchedulableBuilder implements SchedulableBuilder, Logging {
   private final Pool rootPool;
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

   public Pool rootPool() {
      return this.rootPool;
   }

   public void buildPools() {
   }

   public void addTaskSetManager(Schedulable manager, Properties properties) {
      this.rootPool().addSchedulable(manager);
   }

   public FIFOSchedulableBuilder(Pool rootPool) {
      this.rootPool = rootPool;
      Logging$class.$init$(this);
   }
}
