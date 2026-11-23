package org.apache.spark.util;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154Q!\u0001\u0002\u0001\t)\u0011Qd\u00159be.,fnY1vO\"$X\t_2faRLwN\u001c%b]\u0012dWM\u001d\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017MQ\u0002C\u0001\u0007\u0012\u001b\u0005i!B\u0001\b\u0010\u0003\u0011a\u0017M\\4\u000b\u0003A\tAA[1wC&\u0011!#\u0004\u0002\u0007\u001f\nTWm\u0019;\u0011\u0005Q9bB\u0001\u0007\u0016\u0013\t1R\"\u0001\u0004UQJ,\u0017\rZ\u0005\u00031e\u0011\u0001$\u00168dCV<\u0007\u000e^#yG\u0016\u0004H/[8o\u0011\u0006tG\r\\3s\u0015\t1R\u0002\u0005\u0002\u001c=5\tAD\u0003\u0002\u001e\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002 9\t9Aj\\4hS:<\u0007\u0002C\u0011\u0001\u0005\u000b\u0007I\u0011A\u0012\u0002/\u0015D\u0018\u000e^(o+:\u001c\u0017-^4ii\u0016C8-\u001a9uS>t7\u0001A\u000b\u0002IA\u0011Q\u0005K\u0007\u0002M)\tq%A\u0003tG\u0006d\u0017-\u0003\u0002*M\t9!i\\8mK\u0006t\u0007\u0002C\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0013\u00021\u0015D\u0018\u000e^(o+:\u001c\u0017-^4ii\u0016C8-\u001a9uS>t\u0007\u0005C\u0003.\u0001\u0011\u0005a&\u0001\u0004=S:LGO\u0010\u000b\u0003_E\u0002\"\u0001\r\u0001\u000e\u0003\tAq!\t\u0017\u0011\u0002\u0003\u0007A\u0005C\u00034\u0001\u0011\u0005C'A\tv]\u000e\fWo\u001a5u\u000bb\u001cW\r\u001d;j_:$2!\u000e\u001d>!\t)c'\u0003\u00028M\t!QK\\5u\u0011\u0015I$\u00071\u0001;\u0003\u0019!\bN]3bIB\u0011AbO\u0005\u0003y5\u0011a\u0001\u00165sK\u0006$\u0007\"\u0002 3\u0001\u0004y\u0014!C3yG\u0016\u0004H/[8o!\t\u0001\u0005J\u0004\u0002B\r:\u0011!)R\u0007\u0002\u0007*\u0011AII\u0001\u0007yI|w\u000e\u001e \n\u0003\u001dJ!a\u0012\u0014\u0002\u000fA\f7m[1hK&\u0011\u0011J\u0013\u0002\n)\"\u0014xn^1cY\u0016T!a\u0012\u0014\t\u000bM\u0002A\u0011\u0001'\u0015\u0005Uj\u0005\"\u0002 L\u0001\u0004yt\u0001C(\u0003\u0003\u0003E\t\u0001\u0002)\u0002;M\u0003\u0018M]6V]\u000e\fWo\u001a5u\u000bb\u001cW\r\u001d;j_:D\u0015M\u001c3mKJ\u0004\"\u0001M)\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\tI\u001b\"!U*\u0011\u0005\u0015\"\u0016BA+'\u0005\u0019\te.\u001f*fM\")Q&\u0015C\u0001/R\t\u0001\u000bC\u0004Z#F\u0005I\u0011\u0001.\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00132+\u0005Y&F\u0001\u0013]W\u0005i\u0006C\u00010d\u001b\u0005y&B\u00011b\u0003%)hn\u00195fG.,GM\u0003\u0002cM\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u0011|&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u0002"
)
public class SparkUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler, Logging {
   private final boolean exitOnUncaughtException;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean $lessinit$greater$default$1() {
      return SparkUncaughtExceptionHandler$.MODULE$.$lessinit$greater$default$1();
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

   public boolean exitOnUncaughtException() {
      return this.exitOnUncaughtException;
   }

   public void uncaughtException(Thread thread, Throwable exception) {
      boolean var9 = false;

      try {
         var9 = true;
         String inShutdownMsg = ShutdownHookManager$.MODULE$.inShutdown() ? "[Container in shutdown] " : "";
         String errMsg = "Uncaught exception in thread ";
         this.logError(new Serializable(this, thread, inShutdownMsg, errMsg) {
            public static final long serialVersionUID = 0L;
            private final Thread thread$1;
            private final String inShutdownMsg$1;
            private final String errMsg$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)this.inShutdownMsg$1).append((Object)this.errMsg$1).append((Object)this.thread$1).toString();
            }

            public {
               this.thread$1 = thread$1;
               this.inShutdownMsg$1 = inShutdownMsg$1;
               this.errMsg$1 = errMsg$1;
            }
         }, exception);
         if (!ShutdownHookManager$.MODULE$.inShutdown()) {
            if (exception instanceof OutOfMemoryError) {
               System.exit(SparkExitCode$.MODULE$.OOM());
               var9 = false;
               return;
            } else {
               if (this.exitOnUncaughtException()) {
                  System.exit(SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION());
                  var9 = false;
               } else {
                  var9 = false;
               }

               return;
            }
         } else {
            var9 = false;
            return;
         }
      } catch (OutOfMemoryError var10) {
         var9 = false;
      } finally {
         if (var9) {
            Runtime.getRuntime().halt(SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION_TWICE());
            return;
         }
      }

   }

   public void uncaughtException(Throwable exception) {
      this.uncaughtException(Thread.currentThread(), exception);
   }

   public SparkUncaughtExceptionHandler(boolean exitOnUncaughtException) {
      this.exitOnUncaughtException = exitOnUncaughtException;
      Logging$class.$init$(this);
   }
}
