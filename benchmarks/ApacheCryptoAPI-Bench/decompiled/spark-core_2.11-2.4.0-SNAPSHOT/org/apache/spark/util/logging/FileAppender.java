package org.apache.spark.util.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mb!B\u0001\u0003\u0001\u0019a!\u0001\u0004$jY\u0016\f\u0005\u000f]3oI\u0016\u0014(BA\u0002\u0005\u0003\u001dawnZ4j]\u001eT!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!r#D\u0001\u0016\u0015\t1b!\u0001\u0005j]R,'O\\1m\u0013\tARCA\u0004M_\u001e<\u0017N\\4\t\u0011i\u0001!\u0011!Q\u0001\nq\t1\"\u001b8qkR\u001cFO]3b[\u000e\u0001\u0001CA\u000f#\u001b\u0005q\"BA\u0010!\u0003\tIwNC\u0001\"\u0003\u0011Q\u0017M^1\n\u0005\rr\"aC%oaV$8\u000b\u001e:fC6D\u0001\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0005M&dW\r\u0005\u0002\u001eO%\u0011\u0001F\b\u0002\u0005\r&dW\r\u0003\u0005+\u0001\t\u0005\t\u0015!\u0003,\u0003)\u0011WO\u001a4feNK'0\u001a\t\u0003\u001d1J!!L\b\u0003\u0007%sG\u000fC\u00030\u0001\u0011\u0005\u0001'\u0001\u0004=S:LGO\u0010\u000b\u0005cM\"T\u0007\u0005\u00023\u00015\t!\u0001C\u0003\u001b]\u0001\u0007A\u0004C\u0003&]\u0001\u0007a\u0005C\u0004+]A\u0005\t\u0019A\u0016\t\u000f]\u0002\u0001\u0019!C\u0005q\u0005aq.\u001e;qkR\u001cFO]3b[V\t\u0011\b\u0005\u0002\u001eu%\u00111H\b\u0002\u0011\r&dWmT;uaV$8\u000b\u001e:fC6Dq!\u0010\u0001A\u0002\u0013%a(\u0001\tpkR\u0004X\u000f^*ue\u0016\fWn\u0018\u0013fcR\u0011qH\u0011\t\u0003\u001d\u0001K!!Q\b\u0003\tUs\u0017\u000e\u001e\u0005\b\u0007r\n\t\u00111\u0001:\u0003\rAH%\r\u0005\u0007\u000b\u0002\u0001\u000b\u0015B\u001d\u0002\u001b=,H\u000f];u'R\u0014X-Y7!Q\t!u\t\u0005\u0002\u000f\u0011&\u0011\u0011j\u0004\u0002\tm>d\u0017\r^5mK\"91\n\u0001a\u0001\n\u0013a\u0015!D7be.,GMR8s'R|\u0007/F\u0001N!\tqa*\u0003\u0002P\u001f\t9!i\\8mK\u0006t\u0007bB)\u0001\u0001\u0004%IAU\u0001\u0012[\u0006\u00148.\u001a3G_J\u001cFo\u001c9`I\u0015\fHCA T\u0011\u001d\u0019\u0005+!AA\u00025Ca!\u0016\u0001!B\u0013i\u0015AD7be.,GMR8s'R|\u0007\u000f\t\u0015\u0003)\u001eCq\u0001\u0017\u0001C\u0002\u0013%\u0011,A\u0007xe&$\u0018N\\4UQJ,\u0017\rZ\u000b\u00025B\u00111LX\u0007\u00029*\u0011Q\fI\u0001\u0005Y\u0006tw-\u0003\u0002`9\n1A\u000b\u001b:fC\u0012Da!\u0019\u0001!\u0002\u0013Q\u0016AD<sSRLgn\u001a+ie\u0016\fG\r\t\u0005\u0006G\u0002!\t\u0001Z\u0001\u0011C^\f\u0017\u000e\u001e+fe6Lg.\u0019;j_:$\u0012a\u0010\u0005\u0006M\u0002!\t\u0001Z\u0001\u0005gR|\u0007\u000fC\u0003i\u0001\u0011EA-\u0001\nbaB,g\u000eZ*ue\u0016\fW\u000eV8GS2,\u0007\"\u00026\u0001\t#Y\u0017\u0001D1qa\u0016tG\rV8GS2,GcA mi\")Q.\u001ba\u0001]\u0006)!-\u001f;fgB\u0019ab\\9\n\u0005A|!!B!se\u0006L\bC\u0001\bs\u0013\t\u0019xB\u0001\u0003CsR,\u0007\"B;j\u0001\u0004Y\u0013a\u00017f]\")q\u000f\u0001C\tI\u0006Aq\u000e]3o\r&dW\rC\u0003z\u0001\u0011EA-A\u0005dY>\u001cXMR5mK\u001e11P\u0001E\u0001\rq\fABR5mK\u0006\u0003\b/\u001a8eKJ\u0004\"AM?\u0007\r\u0005\u0011\u0001\u0012\u0001\u0004\u007f'\riXb\u0005\u0005\u0007_u$\t!!\u0001\u0015\u0003qDq!!\u0002~\t\u0003\t9!A\u0003baBd\u0017\u0010F\u00042\u0003\u0013\tY!!\u0004\t\ri\t\u0019\u00011\u0001\u001d\u0011\u0019)\u00131\u0001a\u0001M!A\u0011qBA\u0002\u0001\u0004\t\t\"\u0001\u0003d_:4\u0007\u0003BA\n\u0003+i\u0011AB\u0005\u0004\u0003/1!!C*qCJ\\7i\u001c8g\u0011%\tY\"`I\u0001\n\u0003\ti\"A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HeM\u000b\u0003\u0003?Q3aKA\u0011W\t\t\u0019\u0003\u0005\u0003\u0002&\u0005=RBAA\u0014\u0015\u0011\tI#a\u000b\u0002\u0013Ut7\r[3dW\u0016$'bAA\u0017\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005E\u0012q\u0005\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007"
)
public class FileAppender implements Logging {
   public final InputStream org$apache$spark$util$logging$FileAppender$$inputStream;
   public final File org$apache$spark$util$logging$FileAppender$$file;
   public final int org$apache$spark$util$logging$FileAppender$$bufferSize;
   private volatile FileOutputStream outputStream;
   private volatile boolean org$apache$spark$util$logging$FileAppender$$markedForStop;
   private final Thread writingThread;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static int $lessinit$greater$default$3() {
      return FileAppender$.MODULE$.$lessinit$greater$default$3();
   }

   public static FileAppender apply(InputStream var0, File var1, SparkConf var2) {
      return FileAppender$.MODULE$.apply(var0, var1, var2);
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

   private FileOutputStream outputStream() {
      return this.outputStream;
   }

   private void outputStream_$eq(FileOutputStream x$1) {
      this.outputStream = x$1;
   }

   public boolean org$apache$spark$util$logging$FileAppender$$markedForStop() {
      return this.org$apache$spark$util$logging$FileAppender$$markedForStop;
   }

   private void org$apache$spark$util$logging$FileAppender$$markedForStop_$eq(boolean x$1) {
      this.org$apache$spark$util$logging$FileAppender$$markedForStop = x$1;
   }

   private Thread writingThread() {
      return this.writingThread;
   }

   public void awaitTermination() {
      this.writingThread().join();
   }

   public void stop() {
      this.org$apache$spark$util$logging$FileAppender$$markedForStop_$eq(true);
   }

   public void appendStreamToFile() {
      try {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Started appending thread";
            }
         });
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FileAppender $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.openFile();
               byte[] buf = new byte[this.$outer.org$apache$spark$util$logging$FileAppender$$bufferSize];
               int n = 0;

               while(!this.$outer.org$apache$spark$util$logging$FileAppender$$markedForStop() && n != -1) {
                  try {
                     n = this.$outer.org$apache$spark$util$logging$FileAppender$$inputStream.read(buf);
                  } catch (Throwable var7) {
                     label69: {
                        if (!(var7 instanceof IOException) || !this.$outer.org$apache$spark$util$logging$FileAppender$$markedForStop()) {
                           throw var7;
                        }

                        BoxedUnit var5 = BoxedUnit.UNIT;
                        break label69;
                     }
                  }

                  if (n > 0) {
                     this.$outer.appendToFile(buf, n);
                  }
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FileAppender $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.closeFile();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
      } catch (Exception var2) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FileAppender $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error writing stream to file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$FileAppender$$file}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, var2);
      }

   }

   public void appendToFile(byte[] bytes, int len) {
      if (this.outputStream() == null) {
         this.openFile();
      }

      this.outputStream().write(bytes, 0, len);
   }

   public void openFile() {
      this.outputStream_$eq(new FileOutputStream(this.org$apache$spark$util$logging$FileAppender$$file, true));
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileAppender $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Opened file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$FileAppender$$file}));
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

   public void closeFile() {
      this.outputStream().flush();
      this.outputStream().close();
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileAppender $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Closed file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$FileAppender$$file}));
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

   public FileAppender(InputStream inputStream, File file, int bufferSize) {
      this.org$apache$spark$util$logging$FileAppender$$inputStream = inputStream;
      this.org$apache$spark$util$logging$FileAppender$$file = file;
      this.org$apache$spark$util$logging$FileAppender$$bufferSize = bufferSize;
      Logging$class.$init$(this);
      this.outputStream = null;
      this.org$apache$spark$util$logging$FileAppender$$markedForStop = false;
      this.writingThread = new Thread(this) {
         // $FF: synthetic field
         private final FileAppender $outer;

         public void run() {
            Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$util$logging$FileAppender$$anon$$$outer().appendStreamToFile();
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

         // $FF: synthetic method
         public FileAppender org$apache$spark$util$logging$FileAppender$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.setDaemon(true);
            }
         }
      };
      this.writingThread().start();
   }
}
