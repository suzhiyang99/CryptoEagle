package org.apache.spark.security;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001M2Q!\u0001\u0002\u0001\t)\u0011qd\u00155fY2\u0014\u0015m]3e\u000fJ|W\u000f]:NCB\u0004\u0018N\\4Qe>4\u0018\u000eZ3s\u0015\t\u0019A!\u0001\u0005tK\u000e,(/\u001b;z\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0003\u0002\u0001\f#U\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0014\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u0005m9%o\\;q\u001b\u0006\u0004\b/\u001b8h'\u0016\u0014h/[2f!J|g/\u001b3feB\u0011a#G\u0007\u0002/)\u0011\u0001\u0004B\u0001\tS:$XM\u001d8bY&\u0011!d\u0006\u0002\b\u0019><w-\u001b8h\u0011\u0015a\u0002\u0001\"\u0001\u001f\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u0010\u0011\u0005I\u0001\u0001\"B\u0011\u0001\t\u0003\u0012\u0013!C4fi\u001e\u0013x.\u001e9t)\t\u0019S\u0006E\u0002%O)r!\u0001D\u0013\n\u0005\u0019j\u0011A\u0002)sK\u0012,g-\u0003\u0002)S\t\u00191+\u001a;\u000b\u0005\u0019j\u0001C\u0001\u0013,\u0013\ta\u0013F\u0001\u0004TiJLgn\u001a\u0005\u0006]\u0001\u0002\rAK\u0001\tkN,'O\\1nK\")\u0001\u0007\u0001C\u0005c\u0005iq-\u001a;V]&DxI]8vaN$\"a\t\u001a\t\u000b9z\u0003\u0019\u0001\u0016"
)
public class ShellBasedGroupsMappingProvider implements GroupMappingServiceProvider, Logging {
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

   public Set<String> getGroups(String username) {
      Set userGroups = this.getUnixGroups(username);
      this.logDebug(new Serializable(this, username, userGroups) {
         public static final long serialVersionUID = 0L;
         private final String username$1;
         private final Set userGroups$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"User: ").append((Object)this.username$1).append((Object)" Groups: ").append((Object)this.userGroups$1.mkString(",")).toString();
         }

         public {
            this.username$1 = username$1;
            this.userGroups$1 = userGroups$1;
         }
      });
      return userGroups;
   }

   private Set<String> getUnixGroups(String username) {
      Seq cmdSeq = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"bash", "-c", (new StringBuilder()).append((Object)"id -Gn ").append((Object)username).toString()})));
      return Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(Utils$.MODULE$.executeAndGetOutput(cmdSeq, Utils$.MODULE$.executeAndGetOutput$default$2(), Utils$.MODULE$.executeAndGetOutput$default$3(), Utils$.MODULE$.executeAndGetOutput$default$4())))).stripLineEnd().split(" ")).toSet();
   }

   public ShellBasedGroupsMappingProvider() {
      Logging$class.$init$(this);
   }
}
