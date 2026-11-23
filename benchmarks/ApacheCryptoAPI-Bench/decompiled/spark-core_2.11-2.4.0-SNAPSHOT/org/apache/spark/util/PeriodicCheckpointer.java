package org.apache.spark.util;

import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterable;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Queue;
import scala.collection.mutable.Queue$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ecAB\u0001\u0003\u0003\u0003!!B\u0001\u000bQKJLw\u000eZ5d\u0007\",7m\u001b9pS:$XM\u001d\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\fcM\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019b#D\u0001\u0015\u0015\t)B!\u0001\u0005j]R,'O\\1m\u0013\t9BCA\u0004M_\u001e<\u0017N\\4\t\u0011e\u0001!Q1A\u0005\u0002m\t!c\u00195fG.\u0004x.\u001b8u\u0013:$XM\u001d<bY\u000e\u0001Q#\u0001\u000f\u0011\u00055i\u0012B\u0001\u0010\u000f\u0005\rIe\u000e\u001e\u0005\tA\u0001\u0011\t\u0011)A\u00059\u0005\u00192\r[3dWB|\u0017N\u001c;J]R,'O^1mA!A!\u0005\u0001BC\u0002\u0013\u00051%\u0001\u0002tGV\tA\u0005\u0005\u0002&M5\tA!\u0003\u0002(\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"A\u0011\u0006\u0001B\u0001B\u0003%A%A\u0002tG\u0002BQa\u000b\u0001\u0005\u00021\na\u0001P5oSRtDcA\u0017;wA\u0019a\u0006A\u0018\u000e\u0003\t\u0001\"\u0001M\u0019\r\u0001\u0011)!\u0007\u0001b\u0001g\t\tA+\u0005\u00025oA\u0011Q\"N\u0005\u0003m9\u0011qAT8uQ&tw\r\u0005\u0002\u000eq%\u0011\u0011H\u0004\u0002\u0004\u0003:L\b\"B\r+\u0001\u0004a\u0002\"\u0002\u0012+\u0001\u0004!\u0003bB\u001f\u0001\u0005\u0004%IAP\u0001\u0010G\",7m\u001b9pS:$\u0018+^3vKV\tq\bE\u0002A\u000b>j\u0011!\u0011\u0006\u0003\u0005\u000e\u000bq!\\;uC\ndWM\u0003\u0002E\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0019\u000b%!B)vKV,\u0007B\u0002%\u0001A\u0003%q(\u0001\tdQ\u0016\u001c7\u000e]8j]R\fV/Z;fA!9!\n\u0001b\u0001\n\u0013q\u0014A\u00049feNL7\u000f^3e#V,W/\u001a\u0005\u0007\u0019\u0002\u0001\u000b\u0011B \u0002\u001fA,'o]5ti\u0016$\u0017+^3vK\u0002BqA\u0014\u0001A\u0002\u0013%1$A\u0006va\u0012\fG/Z\"pk:$\bb\u0002)\u0001\u0001\u0004%I!U\u0001\u0010kB$\u0017\r^3D_VtGo\u0018\u0013fcR\u0011!+\u0016\t\u0003\u001bMK!\u0001\u0016\b\u0003\tUs\u0017\u000e\u001e\u0005\b->\u000b\t\u00111\u0001\u001d\u0003\rAH%\r\u0005\u00071\u0002\u0001\u000b\u0015\u0002\u000f\u0002\u0019U\u0004H-\u0019;f\u0007>,h\u000e\u001e\u0011\t\u000bi\u0003A\u0011A.\u0002\rU\u0004H-\u0019;f)\t\u0011F\fC\u0003^3\u0002\u0007q&A\u0004oK^$\u0015\r^1\t\u000b}\u0003a\u0011\u00031\u0002\u0015\rDWmY6q_&tG\u000f\u0006\u0002SC\")!M\u0018a\u0001_\u0005!A-\u0019;b\u0011\u0015!\u0007A\"\u0005f\u00039I7o\u00115fG.\u0004x.\u001b8uK\u0012$\"AZ5\u0011\u000559\u0017B\u00015\u000f\u0005\u001d\u0011un\u001c7fC:DQAY2A\u0002=BQa\u001b\u0001\u0007\u00121\fq\u0001]3sg&\u001cH\u000f\u0006\u0002S[\")!M\u001ba\u0001_!)q\u000e\u0001D\ta\u0006IQO\u001c9feNL7\u000f\u001e\u000b\u0003%FDQA\u00198A\u0002=BQa\u001d\u0001\u0007\u0012Q\f!cZ3u\u0007\",7m\u001b9pS:$h)\u001b7fgR\u0019Q/!\u0005\u0011\tYt\u00181\u0001\b\u0003ort!\u0001_>\u000e\u0003eT!A\u001f\u000e\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0011BA?\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L1a`A\u0001\u0005!IE/\u001a:bE2,'BA?\u000f!\u0011\t)!a\u0003\u000f\u00075\t9!C\u0002\u0002\n9\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u0007\u0003\u001f\u0011aa\u0015;sS:<'bAA\u0005\u001d!)!M\u001da\u0001_!9\u0011Q\u0003\u0001\u0005\u0002\u0005]\u0011\u0001E;oa\u0016\u00148/[:u\t\u0006$\u0018mU3u)\u0005\u0011\u0006bBA\u000e\u0001\u0011\u0005\u0011qC\u0001\u0015I\u0016dW\r^3BY2\u001c\u0005.Z2la>Lg\u000e^:\t\u000f\u0005}\u0001\u0001\"\u0001\u0002\u0018\u0005YB-\u001a7fi\u0016\fE\u000e\\\"iK\u000e\\\u0007o\\5oiN\u0014U\u000f\u001e'bgRDq!a\t\u0001\t\u0003\t)#A\u000bhKR\fE\u000e\\\"iK\u000e\\\u0007o\\5oi\u001aKG.Z:\u0016\u0005\u0005\u001d\u0002#B\u0007\u0002*\u0005\r\u0011bAA\u0016\u001d\t)\u0011I\u001d:bs\"9\u0011q\u0006\u0001\u0005\n\u0005]\u0011\u0001\u0006:f[>4Xm\u00115fG.\u0004x.\u001b8u\r&dWm\u0002\u0005\u00024\tA\t\u0001BA\u001b\u0003Q\u0001VM]5pI&\u001c7\t[3dWB|\u0017N\u001c;feB\u0019a&a\u000e\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0002:M!\u0011q\u0007\u0007\u0013\u0011\u001dY\u0013q\u0007C\u0001\u0003{!\"!!\u000e\t\u0011\u0005=\u0012q\u0007C\u0001\u0003\u0003\"RAUA\"\u0003\u000fB\u0001\"!\u0012\u0002@\u0001\u0007\u00111A\u0001\u000fG\",7m\u001b9pS:$h)\u001b7f\u0011!\tI%a\u0010A\u0002\u0005-\u0013\u0001B2p]\u001a\u0004B!!\u0014\u0002V5\u0011\u0011q\n\u0006\u0005\u0003\u0013\n\tFC\u0002\u0002T\u0019\ta\u0001[1e_>\u0004\u0018\u0002BA,\u0003\u001f\u0012QbQ8oM&<WO]1uS>t\u0007"
)
public abstract class PeriodicCheckpointer<T> implements Logging {
   private final int checkpointInterval;
   private final SparkContext sc;
   private final Queue<T> checkpointQueue;
   private final Queue<T> persistedQueue;
   private int updateCount;
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

   public int checkpointInterval() {
      return this.checkpointInterval;
   }

   public SparkContext sc() {
      return this.sc;
   }

   private Queue<T> checkpointQueue() {
      return this.checkpointQueue;
   }

   private Queue<T> persistedQueue() {
      return this.persistedQueue;
   }

   private int updateCount() {
      return this.updateCount;
   }

   private void updateCount_$eq(int x$1) {
      this.updateCount = x$1;
   }

   public void update(T newData) {
      this.persist(newData);
      this.persistedQueue().enqueue(Predef$.MODULE$.genericWrapArray(new Object[]{newData}));

      while(this.persistedQueue().size() > 3) {
         Object dataToUnpersist = this.persistedQueue().dequeue();
         this.unpersist(dataToUnpersist);
      }

      this.updateCount_$eq(this.updateCount() + 1);
      if (this.checkpointInterval() != -1 && this.updateCount() % this.checkpointInterval() == 0 && this.sc().getCheckpointDir().nonEmpty()) {
         this.checkpoint(newData);
         this.checkpointQueue().enqueue(Predef$.MODULE$.genericWrapArray(new Object[]{newData}));
         boolean canDelete = true;

         while(this.checkpointQueue().size() > 1 && canDelete) {
            if (this.isCheckpointed(this.checkpointQueue().head())) {
               this.removeCheckpointFile();
            } else {
               canDelete = false;
            }
         }
      }

   }

   public abstract void checkpoint(T var1);

   public abstract boolean isCheckpointed(T var1);

   public abstract void persist(T var1);

   public abstract void unpersist(T var1);

   public abstract Iterable<String> getCheckpointFiles(T var1);

   public void unpersistDataSet() {
      while(this.persistedQueue().nonEmpty()) {
         Object dataToUnpersist = this.persistedQueue().dequeue();
         this.unpersist(dataToUnpersist);
      }

   }

   public void deleteAllCheckpoints() {
      while(this.checkpointQueue().nonEmpty()) {
         this.removeCheckpointFile();
      }

   }

   public void deleteAllCheckpointsButLast() {
      while(this.checkpointQueue().size() > 1) {
         this.removeCheckpointFile();
      }

   }

   public String[] getAllCheckpointFiles() {
      return (String[])((TraversableOnce)this.checkpointQueue().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PeriodicCheckpointer $outer;

         public final Iterable<String> apply(T data) {
            return this.$outer.getCheckpointFiles(data);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Queue$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(String.class));
   }

   private void removeCheckpointFile() {
      Object old = this.checkpointQueue().dequeue();
      this.getCheckpointFiles(old).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PeriodicCheckpointer $outer;

         public final void apply(String x$1) {
            PeriodicCheckpointer$.MODULE$.removeCheckpointFile(x$1, this.$outer.sc().hadoopConfiguration());
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

   public PeriodicCheckpointer(int checkpointInterval, SparkContext sc) {
      this.checkpointInterval = checkpointInterval;
      this.sc = sc;
      Logging$class.$init$(this);
      this.checkpointQueue = (Queue)Queue$.MODULE$.apply(Nil$.MODULE$);
      this.persistedQueue = (Queue)Queue$.MODULE$.apply(Nil$.MODULE$);
      this.updateCount = 0;
   }
}
