package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.storage.RDDBlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function2;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u000154Q!\u0001\u0002\u0001\t)\u0011a\u0003T8dC2\u0014F\tR\"iK\u000e\\\u0007o\\5oi\u0012\u000bG/\u0019\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\u0013'\r\u0001Ab\b\t\u0004\u001b9\u0001R\"\u0001\u0002\n\u0005=\u0011!!\u0005*E\t\u000eCWmY6q_&tG\u000fR1uCB\u0011\u0011C\u0005\u0007\u0001\t\u0015\u0019\u0002A1\u0001\u0016\u0005\u0005!6\u0001A\t\u0003-q\u0001\"a\u0006\u000e\u000e\u0003aQ\u0011!G\u0001\u0006g\u000e\fG.Y\u0005\u00037a\u0011qAT8uQ&tw\r\u0005\u0002\u0018;%\u0011a\u0004\u0007\u0002\u0004\u0003:L\bC\u0001\u0011$\u001b\u0005\t#B\u0001\u0012\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0013\"\u0005\u001daunZ4j]\u001eD\u0001b\u0001\u0001\u0003\u0006\u0004%IAJ\u000b\u0002OA\u0019Q\u0002\u000b\t\n\u0005%\u0012!a\u0001*E\t\"A1\u0006\u0001B\u0001B\u0003%q%\u0001\u0003sI\u0012\u0004\u0003F\u0001\u0016.!\t9b&\u0003\u000201\tIAO]1og&,g\u000e\u001e\u0005\tc\u0001\u0011\u0019\u0011)A\u0006e\u0005QQM^5eK:\u001cW\rJ\u0019\u0011\u0007M2\u0004#D\u00015\u0015\t)\u0004$A\u0004sK\u001adWm\u0019;\n\u0005]\"$\u0001C\"mCN\u001cH+Y4\t\u000be\u0002A\u0011\u0001\u001e\u0002\rqJg.\u001b;?)\tYd\b\u0006\u0002={A\u0019Q\u0002\u0001\t\t\u000bEB\u00049\u0001\u001a\t\u000b\rA\u0004\u0019A\u0014\t\u000b\u0001\u0003A\u0011K!\u0002\u0019\u0011|7\t[3dWB|\u0017N\u001c;\u0015\u0003\t\u00032!D\"\u0011\u0013\t!%AA\u0007DQ\u0016\u001c7\u000e]8j]R\u0014F\tR\u0004\u0007\r\nA\t\u0001B$\u0002-1{7-\u00197S\t\u0012\u001b\u0005.Z2la>Lg\u000e\u001e#bi\u0006\u0004\"!\u0004%\u0007\r\u0005\u0011\u0001\u0012\u0001\u0003J'\rA%*\u0014\t\u0003/-K!\u0001\u0014\r\u0003\r\u0005s\u0017PU3g!\t9b*\u0003\u0002P1\ta1+\u001a:jC2L'0\u00192mK\")\u0011\b\u0013C\u0001#R\tq\tC\u0004T\u0011\n\u0007I\u0011\u0001+\u0002+\u0011+e)Q+M)~\u001bFk\u0014*B\u000f\u0016{F*\u0012,F\u0019V\tQ\u000b\u0005\u0002W36\tqK\u0003\u0002Y\t\u000591\u000f^8sC\u001e,\u0017B\u0001.X\u00051\u0019Fo\u001c:bO\u0016dUM^3m\u0011\u0019a\u0006\n)A\u0005+\u00061B)\u0012$B+2#vl\u0015+P%\u0006;Ui\u0018'F-\u0016c\u0005\u0005C\u0003_\u0011\u0012\u0005q,A\u000bue\u0006t7OZ8s[N#xN]1hK2+g/\u001a7\u0015\u0005U\u0003\u0007\"B1^\u0001\u0004)\u0016!\u00027fm\u0016d\u0007bB2I\u0003\u0003%I\u0001Z\u0001\fe\u0016\fGMU3t_24X\rF\u0001f!\t17.D\u0001h\u0015\tA\u0017.\u0001\u0003mC:<'\"\u00016\u0002\t)\fg/Y\u0005\u0003Y\u001e\u0014aa\u00142kK\u000e$\b"
)
public class LocalRDDCheckpointData<T> extends RDDCheckpointData<T> implements Logging {
   private final transient RDD<T> org$apache$spark$rdd$LocalRDDCheckpointData$$rdd;
   private final ClassTag<T> evidence$1;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static StorageLevel transformStorageLevel(StorageLevel var0) {
      return LocalRDDCheckpointData$.MODULE$.transformStorageLevel(var0);
   }

   public static StorageLevel DEFAULT_STORAGE_LEVEL() {
      return LocalRDDCheckpointData$.MODULE$.DEFAULT_STORAGE_LEVEL();
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

   public RDD<T> org$apache$spark$rdd$LocalRDDCheckpointData$$rdd() {
      return this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd;
   }

   public CheckpointRDD<T> doCheckpoint() {
      StorageLevel level = this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd().getStorageLevel();
      Predef$.MODULE$.assume(level.useDisk(), new Serializable(this, level) {
         public static final long serialVersionUID = 0L;
         private final StorageLevel level$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Storage level ", " is not appropriate for local checkpointing"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.level$1}));
         }

         public {
            this.level$1 = level$1;
         }
      });
      Function2 action = new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskContext tc, Iterator<T> iterator) {
            return Utils$.MODULE$.getIteratorSize(iterator);
         }
      };
      int[] missingPartitionIndices = (int[])Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd().partitions()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Partition x$1) {
            return x$1.index();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LocalRDDCheckpointData $outer;

         public final boolean apply(int i) {
            return this.apply$mcZI$sp(i);
         }

         public boolean apply$mcZI$sp(int i) {
            return !SparkEnv$.MODULE$.get().blockManager().master().contains(new RDDBlockId(this.$outer.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd().id(), i));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      if (Predef$.MODULE$.intArrayOps(missingPartitionIndices).nonEmpty()) {
         this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd().sparkContext().runJob(this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd(), action, Predef$.MODULE$.wrapIntArray(missingPartitionIndices), ClassTag$.MODULE$.Long());
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return new LocalCheckpointRDD<T>(this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd(), this.evidence$1);
   }

   public LocalRDDCheckpointData(RDD<T> rdd, ClassTag<T> evidence$1) {
      super(rdd, evidence$1);
      this.org$apache$spark$rdd$LocalRDDCheckpointData$$rdd = rdd;
      this.evidence$1 = evidence$1;
      Logging$class.$init$(this);
   }
}
