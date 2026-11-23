package org.apache.spark.rdd;

import org.apache.hadoop.fs.Path;
import org.apache.spark.ContextCleaner;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0001\u0011Q!!\u0007*fY&\f'\r\\3S\t\u0012\u001b\u0005.Z2la>Lg\u000e\u001e#bi\u0006T!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h+\tY!cE\u0002\u0001\u0019}\u00012!\u0004\b\u0011\u001b\u0005\u0011\u0011BA\b\u0003\u0005E\u0011F\tR\"iK\u000e\\\u0007o\\5oi\u0012\u000bG/\u0019\t\u0003#Ia\u0001\u0001B\u0003\u0014\u0001\t\u0007QCA\u0001U\u0007\u0001\t\"A\u0006\u000f\u0011\u0005]QR\"\u0001\r\u000b\u0003e\tQa]2bY\u0006L!a\u0007\r\u0003\u000f9{G\u000f[5oOB\u0011q#H\u0005\u0003=a\u00111!\u00118z!\t\u00013%D\u0001\"\u0015\t\u0011C!\u0001\u0005j]R,'O\\1m\u0013\t!\u0013EA\u0004M_\u001e<\u0017N\\4\t\u0011\r\u0001!Q1A\u0005\n\u0019*\u0012a\n\t\u0004\u001b!\u0002\u0012BA\u0015\u0003\u0005\r\u0011F\t\u0012\u0005\tW\u0001\u0011\t\u0011)A\u0005O\u0005!!\u000f\u001a3!Q\tQS\u0006\u0005\u0002\u0018]%\u0011q\u0006\u0007\u0002\niJ\fgn]5f]RD\u0001\"\r\u0001\u0003\u0004\u0003\u0006YAM\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004cA\u001a7!5\tAG\u0003\u000261\u00059!/\u001a4mK\u000e$\u0018BA\u001c5\u0005!\u0019E.Y:t)\u0006<\u0007\"B\u001d\u0001\t\u0003Q\u0014A\u0002\u001fj]&$h\b\u0006\u0002<}Q\u0011A(\u0010\t\u0004\u001b\u0001\u0001\u0002\"B\u00199\u0001\b\u0011\u0004\"B\u00029\u0001\u00049\u0003b\u0002!\u0001\u0005\u0004%I!Q\u0001\u0006GB$\u0015N]\u000b\u0002\u0005B\u00111I\u0012\b\u0003/\u0011K!!\u0012\r\u0002\rA\u0013X\rZ3g\u0013\t9\u0005J\u0001\u0004TiJLgn\u001a\u0006\u0003\u000bbAaA\u0013\u0001!\u0002\u0013\u0011\u0015AB2q\t&\u0014\b\u0005C\u0003M\u0001\u0011\u0005Q*\u0001\thKR\u001c\u0005.Z2la>Lg\u000e\u001e#jeV\ta\nE\u0002\u0018\u001f\nK!\u0001\u0015\r\u0003\r=\u0003H/[8o\u0011\u0015\u0011\u0006\u0001\"\u0015T\u00031!wn\u00115fG.\u0004x.\u001b8u)\u0005!\u0006cA\u0007V!%\u0011aK\u0001\u0002\u000e\u0007\",7m\u001b9pS:$(\u000b\u0012#\b\ra\u0013\u0001\u0012\u0001\u0003Z\u0003e\u0011V\r\\5bE2,'\u000b\u0012#DQ\u0016\u001c7\u000e]8j]R$\u0015\r^1\u0011\u00055QfAB\u0001\u0003\u0011\u0003!1l\u0005\u0003[9~y\u0006CA\f^\u0013\tq\u0006D\u0001\u0004B]f\u0014VM\u001a\t\u0003/\u0001L!!\u0019\r\u0003\u0019M+'/[1mSj\f'\r\\3\t\u000beRF\u0011A2\u0015\u0003eCQ!\u001a.\u0005\u0002\u0019\fab\u00195fG.\u0004x.\u001b8u!\u0006$\b\u000eF\u0002haZ\u00042aF(i!\tIg.D\u0001k\u0015\tYG.\u0001\u0002gg*\u0011QNB\u0001\u0007Q\u0006$wn\u001c9\n\u0005=T'\u0001\u0002)bi\"DQ!\u001d3A\u0002I\f!a]2\u0011\u0005M$X\"\u0001\u0003\n\u0005U$!\u0001D*qCJ\\7i\u001c8uKb$\b\"B<e\u0001\u0004A\u0018!\u0002:eI&#\u0007CA\fz\u0013\tQ\bDA\u0002J]RDQ\u0001 .\u0005\u0002u\fqb\u00197fC:\u001c\u0005.Z2la>Lg\u000e\u001e\u000b\u0006}\u0006\r\u0011Q\u0001\t\u0003/}L1!!\u0001\u0019\u0005\u0011)f.\u001b;\t\u000bE\\\b\u0019\u0001:\t\u000b]\\\b\u0019\u0001=\t\u0013\u0005%!,!A\u0005\n\u0005-\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0004\u0011\t\u0005=\u0011\u0011D\u0007\u0003\u0003#QA!a\u0005\u0002\u0016\u0005!A.\u00198h\u0015\t\t9\"\u0001\u0003kCZ\f\u0017\u0002BA\u000e\u0003#\u0011aa\u00142kK\u000e$\b"
)
public class ReliableRDDCheckpointData<T> extends RDDCheckpointData<T> implements Logging {
   private final transient RDD<T> org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd;
   private final ClassTag<T> evidence$1;
   private final String org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static void cleanCheckpoint(SparkContext var0, int var1) {
      ReliableRDDCheckpointData$.MODULE$.cleanCheckpoint(var0, var1);
   }

   public static Option<Path> checkpointPath(SparkContext var0, int var1) {
      return ReliableRDDCheckpointData$.MODULE$.checkpointPath(var0, var1);
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

   public RDD<T> org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd() {
      return this.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd;
   }

   public String org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir() {
      return this.org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir;
   }

   public Option<String> getCheckpointDir() {
      Object var2;
      synchronized(RDDCheckpointData$.MODULE$) {
         var2 = this.isCheckpointed() ? new Some(this.org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir().toString()) : None$.MODULE$;
      }

      return (Option)var2;
   }

   public CheckpointRDD<T> doCheckpoint() {
      ReliableCheckpointRDD newRDD = ReliableCheckpointRDD$.MODULE$.writeRDDToCheckpointDirectory(this.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd(), this.org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir(), ReliableCheckpointRDD$.MODULE$.writeRDDToCheckpointDirectory$default$3(), this.evidence$1);
      if (this.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd().conf().getBoolean("spark.cleaner.referenceTracking.cleanCheckpoints", false)) {
         this.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd().context().cleaner().foreach(new Serializable(this, newRDD) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ReliableRDDCheckpointData $outer;
            private final ReliableCheckpointRDD newRDD$1;

            public final void apply(ContextCleaner cleaner) {
               cleaner.registerRDDCheckpointDataForCleanup(this.newRDD$1, this.$outer.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd().id());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.newRDD$1 = newRDD$1;
               }
            }
         });
      }

      this.logInfo(new Serializable(this, newRDD) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ReliableRDDCheckpointData $outer;
         private final ReliableCheckpointRDD newRDD$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Done checkpointing RDD ", " to ", ", new parent is RDD ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd().id()), this.$outer.org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir(), BoxesRunTime.boxToInteger(this.newRDD$1.id())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.newRDD$1 = newRDD$1;
            }
         }
      });
      return newRDD;
   }

   public ReliableRDDCheckpointData(RDD<T> rdd, ClassTag<T> evidence$1) {
      super(rdd, evidence$1);
      this.org$apache$spark$rdd$ReliableRDDCheckpointData$$rdd = rdd;
      this.evidence$1 = evidence$1;
      Logging$class.$init$(this);
      this.org$apache$spark$rdd$ReliableRDDCheckpointData$$cpDir = (String)ReliableRDDCheckpointData$.MODULE$.checkpointPath(rdd.context(), rdd.id()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Path x$1) {
            return x$1.toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new SparkException("Checkpoint dir must be specified.");
         }
      });
   }
}
