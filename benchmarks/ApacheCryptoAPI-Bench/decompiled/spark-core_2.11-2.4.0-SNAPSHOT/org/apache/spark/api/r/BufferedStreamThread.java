package org.apache.spark.api.r;

import java.io.InputStream;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.io.Codec$;
import scala.io.Source$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001a3Q!\u0001\u0002\u0001\u00051\u0011ACQ;gM\u0016\u0014X\rZ*ue\u0016\fW\u000e\u00165sK\u0006$'BA\u0002\u0005\u0003\u0005\u0011(BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\u000b\u0011\u00059\u0019R\"A\b\u000b\u0005A\t\u0012\u0001\u00027b]\u001eT\u0011AE\u0001\u0005U\u00064\u0018-\u0003\u0002\u0015\u001f\t1A\u000b\u001b:fC\u0012\u0004\"AF\r\u000e\u0003]Q!\u0001\u0007\u0004\u0002\u0011%tG/\u001a:oC2L!AG\f\u0003\u000f1{wmZ5oO\"AA\u0004\u0001B\u0001B\u0003%a$\u0001\u0002j]\u000e\u0001\u0001CA\u0010#\u001b\u0005\u0001#BA\u0011\u0012\u0003\tIw.\u0003\u0002$A\tY\u0011J\u001c9viN#(/Z1n\u0011!)\u0003A!A!\u0002\u00131\u0013\u0001\u00028b[\u0016\u0004\"aJ\u0017\u000f\u0005!ZS\"A\u0015\u000b\u0003)\nQa]2bY\u0006L!\u0001L\u0015\u0002\rA\u0013X\rZ3g\u0013\tqsF\u0001\u0004TiJLgn\u001a\u0006\u0003Y%B\u0001\"\r\u0001\u0003\u0002\u0003\u0006IAM\u0001\u000eKJ\u0014()\u001e4gKJ\u001c\u0016N_3\u0011\u0005!\u001a\u0014B\u0001\u001b*\u0005\rIe\u000e\u001e\u0005\u0006m\u0001!\taN\u0001\u0007y%t\u0017\u000e\u001e \u0015\taR4\b\u0010\t\u0003s\u0001i\u0011A\u0001\u0005\u00069U\u0002\rA\b\u0005\u0006KU\u0002\rA\n\u0005\u0006cU\u0002\rA\r\u0005\b}\u0001\u0011\r\u0011\"\u0001@\u0003\u0015a\u0017N\\3t+\u0005\u0001\u0005c\u0001\u0015BM%\u0011!)\u000b\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\u0007\t\u0002\u0001\u000b\u0011\u0002!\u0002\r1Lg.Z:!\u0011\u001d1\u0005\u00011A\u0005\u0002\u001d\u000bq\u0001\\5oK&#\u00070F\u00013\u0011\u001dI\u0005\u00011A\u0005\u0002)\u000b1\u0002\\5oK&#\u0007p\u0018\u0013fcR\u00111J\u0014\t\u0003Q1K!!T\u0015\u0003\tUs\u0017\u000e\u001e\u0005\b\u001f\"\u000b\t\u00111\u00013\u0003\rAH%\r\u0005\u0007#\u0002\u0001\u000b\u0015\u0002\u001a\u0002\u00111Lg.Z%eq\u0002BQa\u0015\u0001\u0005BQ\u000b1A];o)\u0005Y\u0005\"\u0002,\u0001\t\u00039\u0016\u0001C4fi2Kg.Z:\u0015\u0003\u0019\u0002"
)
public class BufferedStreamThread extends Thread implements Logging {
   private final InputStream in;
   public final int org$apache$spark$api$r$BufferedStreamThread$$errBufferSize;
   private final String[] lines;
   private int lineIdx;
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

   public String[] lines() {
      return this.lines;
   }

   public int lineIdx() {
      return this.lineIdx;
   }

   public void lineIdx_$eq(int x$1) {
      this.lineIdx = x$1;
   }

   public void run() {
      Source$.MODULE$.fromInputStream(this.in, Codec$.MODULE$.fallbackSystemCodec()).getLines().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BufferedStreamThread $outer;

         public final void apply(String line) {
            synchronized(this.$outer) {
               this.$outer.lines()[this.$outer.lineIdx()] = line;
               this.$outer.lineIdx_$eq((this.$outer.lineIdx() + 1) % this.$outer.org$apache$spark$api$r$BufferedStreamThread$$errBufferSize);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            this.$outer.logInfo(new Serializable(this, line) {
               public static final long serialVersionUID = 0L;
               private final String line$1;

               public final String apply() {
                  return this.line$1;
               }

               public {
                  this.line$1 = line$1;
               }
            });
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

   public synchronized String getLines() {
      return ((TraversableOnce)((TraversableLike)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$api$r$BufferedStreamThread$$errBufferSize).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BufferedStreamThread $outer;

         public final boolean apply(int x) {
            return this.apply$mcZI$sp(x);
         }

         public boolean apply$mcZI$sp(int x) {
            return this.$outer.lines()[(x + this.$outer.lineIdx()) % this.$outer.org$apache$spark$api$r$BufferedStreamThread$$errBufferSize] != null;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BufferedStreamThread $outer;

         public final String apply(int x) {
            return this.$outer.lines()[(x + this.$outer.lineIdx()) % this.$outer.org$apache$spark$api$r$BufferedStreamThread$$errBufferSize];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).mkString("\n");
   }

   public BufferedStreamThread(InputStream in, String name, int errBufferSize) {
      super(name);
      this.in = in;
      this.org$apache$spark$api$r$BufferedStreamThread$$errBufferSize = errBufferSize;
      Logging$class.$init$(this);
      this.lines = new String[errBufferSize];
      this.lineIdx = 0;
   }
}
