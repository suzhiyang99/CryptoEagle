package org.apache.spark.api.python;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001M3Q!\u0001\u0002\u0001\r1\u0011q\u0002U=uQ>t'I]8bI\u000e\f7\u000f\u001e\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON!\u0001!D\n\u001c!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011A#G\u0007\u0002+)\u0011acF\u0001\u0003S>T\u0011\u0001G\u0001\u0005U\u00064\u0018-\u0003\u0002\u001b+\ta1+\u001a:jC2L'0\u00192mKB\u0011AdH\u0007\u0002;)\u0011aDB\u0001\tS:$XM\u001d8bY&\u0011\u0001%\b\u0002\b\u0019><w-\u001b8h\u0011!\u0011\u0003A!a\u0001\n\u0003!\u0013\u0001\u00029bi\"\u001c\u0001!F\u0001&!\t1\u0013F\u0004\u0002\u000fO%\u0011\u0001fD\u0001\u0007!J,G-\u001a4\n\u0005)Z#AB*ue&twM\u0003\u0002)\u001f!AQ\u0006\u0001BA\u0002\u0013\u0005a&\u0001\u0005qCRDw\fJ3r)\ty#\u0007\u0005\u0002\u000fa%\u0011\u0011g\u0004\u0002\u0005+:LG\u000fC\u00044Y\u0005\u0005\t\u0019A\u0013\u0002\u0007a$\u0013\u0007\u0003\u00056\u0001\t\u0005\t\u0015)\u0003&\u0003\u0015\u0001\u0018\r\u001e5!Q\t!t\u0007\u0005\u0002\u000fq%\u0011\u0011h\u0004\u0002\niJ\fgn]5f]RDQa\u000f\u0001\u0005\u0002q\na\u0001P5oSRtDCA\u001f@!\tq\u0004!D\u0001\u0003\u0011\u0015\u0011#\b1\u0001&\u0011\u0015\t\u0005\u0001\"\u0003C\u0003-9(/\u001b;f\u001f\nTWm\u0019;\u0015\u0005=\u001a\u0005\"\u0002#A\u0001\u0004)\u0015aA8viB\u0011ACR\u0005\u0003\u000fV\u0011!c\u00142kK\u000e$x*\u001e;qkR\u001cFO]3b[\")\u0011\n\u0001C\u0005\u0015\u0006Q!/Z1e\u001f\nTWm\u0019;\u0015\u0005=Z\u0005\"\u0002'I\u0001\u0004i\u0015AA5o!\t!b*\u0003\u0002P+\t\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7\t\u000bE\u0003A\u0011\t*\u0002\u0011\u0019Lg.\u00197ju\u0016$\u0012a\f"
)
public class PythonBroadcast implements Serializable, Logging {
   private transient String path;
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

   public String path() {
      return this.path;
   }

   public void path_$eq(String x$1) {
      this.path = x$1;
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new scala.Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PythonBroadcast $outer;
         private final ObjectOutputStream out$3;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            FileInputStream in = new FileInputStream(new File(this.$outer.path()));

            long var10000;
            try {
               var10000 = Utils$.MODULE$.copyStream(in, this.out$3, Utils$.MODULE$.copyStream$default$3(), Utils$.MODULE$.copyStream$default$4());
            } finally {
               in.close();
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$3 = out$3;
            }
         }
      });
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new scala.Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PythonBroadcast $outer;
         public final ObjectInputStream in$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            File dir = new File(Utils$.MODULE$.getLocalDir(SparkEnv$.MODULE$.get().conf()));
            File file = File.createTempFile("broadcast", "", dir);
            this.$outer.path_$eq(file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file);
            return BoxesRunTime.unboxToLong(Utils$.MODULE$.tryWithSafeFinally(new scala.Serializable(this, out) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final FileOutputStream out$4;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return Utils$.MODULE$.copyStream(this.$outer.in$1, this.out$4, Utils$.MODULE$.copyStream$default$3(), Utils$.MODULE$.copyStream$default$4());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.out$4 = out$4;
                  }
               }
            }, new scala.Serializable(this, out) {
               public static final long serialVersionUID = 0L;
               private final FileOutputStream out$4;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.out$4.close();
               }

               public {
                  this.out$4 = out$4;
               }
            }));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public void finalize() {
      if (!this.path().isEmpty()) {
         File file = new File(this.path());
         if (file.exists() && !file.delete()) {
            this.logWarning(new scala.Serializable(this, file) {
               public static final long serialVersionUID = 0L;
               private final File file$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1.getPath()}));
               }

               public {
                  this.file$1 = file$1;
               }
            });
         }
      }

      super.finalize();
   }

   public PythonBroadcast(String path) {
      this.path = path;
      super();
      Logging$class.$init$(this);
   }
}
