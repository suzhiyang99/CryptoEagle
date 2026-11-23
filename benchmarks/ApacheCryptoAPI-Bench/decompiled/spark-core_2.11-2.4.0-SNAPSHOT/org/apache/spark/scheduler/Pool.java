package org.apache.spark.scheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%f!B\u0001\u0003\u0001\u0011Q!\u0001\u0002)p_2T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011#\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!aC*dQ\u0016$W\u000f\\1cY\u0016\u0004\"AF\r\u000e\u0003]Q!\u0001\u0007\u0003\u0002\u0011%tG/\u001a:oC2L!AG\f\u0003\u000f1{wmZ5oO\"AA\u0004\u0001BC\u0002\u0013\u0005a$\u0001\u0005q_>dg*Y7f\u0007\u0001)\u0012a\b\t\u0003A\rr!\u0001D\u0011\n\u0005\tj\u0011A\u0002)sK\u0012,g-\u0003\u0002%K\t11\u000b\u001e:j]\u001eT!AI\u0007\t\u0011\u001d\u0002!\u0011!Q\u0001\n}\t\u0011\u0002]8pY:\u000bW.\u001a\u0011\t\u0011%\u0002!Q1A\u0005\u0002)\nab]2iK\u0012,H.\u001b8h\u001b>$W-F\u0001,!\ta#H\u0004\u0002.q9\u0011af\u000e\b\u0003_Yr!\u0001M\u001b\u000f\u0005E\"T\"\u0001\u001a\u000b\u0005Mj\u0012A\u0002\u001fs_>$h(C\u0001\n\u0013\t9\u0001\"\u0003\u0002\u0006\r%\u00111\u0001B\u0005\u0003s\t\tabU2iK\u0012,H.\u001b8h\u001b>$W-\u0003\u0002<y\tq1k\u00195fIVd\u0017N\\4N_\u0012,'BA\u001d\u0003\u0011!q\u0004A!A!\u0002\u0013Y\u0013aD:dQ\u0016$W\u000f\\5oO6{G-\u001a\u0011\t\u0011\u0001\u0003!\u0011!Q\u0001\n\u0005\u000bA\"\u001b8ji6Kgn\u00155be\u0016\u0004\"\u0001\u0004\"\n\u0005\rk!aA%oi\"AQ\t\u0001B\u0001B\u0003%\u0011)\u0001\u0006j]&$x+Z5hQRDQa\u0012\u0001\u0005\u0002!\u000ba\u0001P5oSRtD#B%K\u00172k\u0005C\u0001\n\u0001\u0011\u0015ab\t1\u0001 \u0011\u0015Ic\t1\u0001,\u0011\u0015\u0001e\t1\u0001B\u0011\u0015)e\t1\u0001B\u0011\u001dy\u0005A1A\u0005\u0002A\u000b\u0001c]2iK\u0012,H.\u00192mKF+X-^3\u0016\u0003E\u00032AU-\u0012\u001b\u0005\u0019&B\u0001+V\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0003-^\u000bA!\u001e;jY*\t\u0001,\u0001\u0003kCZ\f\u0017B\u0001.T\u0005U\u0019uN\\2veJ,g\u000e\u001e'j].,G-U;fk\u0016Da\u0001\u0018\u0001!\u0002\u0013\t\u0016!E:dQ\u0016$W\u000f\\1cY\u0016\fV/Z;fA!9a\f\u0001b\u0001\n\u0003y\u0016\u0001H:dQ\u0016$W\u000f\\1cY\u0016t\u0015-\\3U_N\u001b\u0007.\u001a3vY\u0006\u0014G.Z\u000b\u0002AB!!+Y\u0010\u0012\u0013\t\u00117KA\tD_:\u001cWO\u001d:f]RD\u0015m\u001d5NCBDa\u0001\u001a\u0001!\u0002\u0013\u0001\u0017!H:dQ\u0016$W\u000f\\1cY\u0016t\u0015-\\3U_N\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\u0011\t\u000f\u0019\u0004!\u0019!C\u0001O\u00061q/Z5hQR,\u0012!\u0011\u0005\u0007S\u0002\u0001\u000b\u0011B!\u0002\u000f],\u0017n\u001a5uA!91\u000e\u0001b\u0001\n\u00039\u0017\u0001C7j]NC\u0017M]3\t\r5\u0004\u0001\u0015!\u0003B\u0003%i\u0017N\\*iCJ,\u0007\u0005C\u0004p\u0001\u0001\u0007I\u0011A4\u0002\u0019I,hN\\5oOR\u000b7o[:\t\u000fE\u0004\u0001\u0019!C\u0001e\u0006\u0001\"/\u001e8oS:<G+Y:lg~#S-\u001d\u000b\u0003gZ\u0004\"\u0001\u0004;\n\u0005Ul!\u0001B+oSRDqa\u001e9\u0002\u0002\u0003\u0007\u0011)A\u0002yIEBa!\u001f\u0001!B\u0013\t\u0015!\u0004:v]:Lgn\u001a+bg.\u001c\b\u0005C\u0004|\u0001\t\u0007I\u0011A4\u0002\u0011A\u0014\u0018n\u001c:jifDa! \u0001!\u0002\u0013\t\u0015!\u00039sS>\u0014\u0018\u000e^=!\u0011\u001dy\b\u00011A\u0005\u0002\u001d\fqa\u001d;bO\u0016LE\rC\u0005\u0002\u0004\u0001\u0001\r\u0011\"\u0001\u0002\u0006\u0005Y1\u000f^1hK&#w\fJ3r)\r\u0019\u0018q\u0001\u0005\to\u0006\u0005\u0011\u0011!a\u0001\u0003\"9\u00111\u0002\u0001!B\u0013\t\u0015\u0001C:uC\u001e,\u0017\n\u001a\u0011\t\u0011\u0005=\u0001A1A\u0005\u0002y\tAA\\1nK\"9\u00111\u0003\u0001!\u0002\u0013y\u0012!\u00028b[\u0016\u0004\u0003\"CA\f\u0001\u0001\u0007I\u0011AA\r\u0003\u0019\u0001\u0018M]3oiV\t\u0011\nC\u0005\u0002\u001e\u0001\u0001\r\u0011\"\u0001\u0002 \u0005Q\u0001/\u0019:f]R|F%Z9\u0015\u0007M\f\t\u0003\u0003\u0005x\u00037\t\t\u00111\u0001J\u0011\u001d\t)\u0003\u0001Q!\n%\u000bq\u0001]1sK:$\b\u0005C\u0005\u0002*\u0001\u0011\r\u0011\"\u0003\u0002,\u0005QB/Y:l'\u0016$8k\u00195fIVd\u0017N\\4BY\u001e|'/\u001b;i[V\u0011\u0011Q\u0006\t\u0004%\u0005=\u0012bAA\u0019\u0005\t\u00192k\u00195fIVd\u0017N\\4BY\u001e|'/\u001b;i[\"A\u0011Q\u0007\u0001!\u0002\u0013\ti#A\u000euCN\\7+\u001a;TG\",G-\u001e7j]\u001e\fEnZ8sSRDW\u000e\t\u0005\b\u0003s\u0001A\u0011IA\u001e\u00039\tG\rZ*dQ\u0016$W\u000f\\1cY\u0016$2a]A\u001f\u0011\u001d\ty$a\u000eA\u0002E\t1b]2iK\u0012,H.\u00192mK\"9\u00111\t\u0001\u0005B\u0005\u0015\u0013!\u0005:f[>4XmU2iK\u0012,H.\u00192mKR\u00191/a\u0012\t\u000f\u0005}\u0012\u0011\ta\u0001#!9\u00111\n\u0001\u0005B\u00055\u0013\u0001F4fiN\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\"z\u001d\u0006lW\rF\u0002\u0012\u0003\u001fBq!!\u0015\u0002J\u0001\u0007q$A\btG\",G-\u001e7bE2,g*Y7f\u0011\u001d\t)\u0006\u0001C!\u0003/\nA\"\u001a=fGV$xN\u001d'pgR$ra]A-\u0003;\n\t\u0007C\u0004\u0002\\\u0005M\u0003\u0019A\u0010\u0002\u0015\u0015DXmY;u_JLE\rC\u0004\u0002`\u0005M\u0003\u0019A\u0010\u0002\t!|7\u000f\u001e\u0005\t\u0003G\n\u0019\u00061\u0001\u0002f\u00051!/Z1t_:\u00042AEA4\u0013\r\tIG\u0001\u0002\u0013\u000bb,7-\u001e;pe2{7o\u001d*fCN|g\u000eC\u0004\u0002n\u0001!\t%a\u001c\u0002-\rDWmY6Ta\u0016\u001cW\u000f\\1uC\ndW\rV1tWN$B!!\u001d\u0002xA\u0019A\"a\u001d\n\u0007\u0005UTBA\u0004C_>dW-\u00198\t\u000f\u0005e\u00141\u000ea\u0001\u0003\u0006!R.\u001b8US6,Gk\\*qK\u000e,H.\u0019;j_:Dq!! \u0001\t\u0003\ny(A\u000bhKR\u001cvN\u001d;fIR\u000b7o[*fiF+X-^3\u0016\u0005\u0005\u0005\u0005CBAB\u0003\u001b\u000b\t*\u0004\u0002\u0002\u0006*!\u0011qQAE\u0003\u001diW\u000f^1cY\u0016T1!a#\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u001f\u000b)IA\u0006BeJ\f\u0017PQ;gM\u0016\u0014\bc\u0001\n\u0002\u0014&\u0019\u0011Q\u0013\u0002\u0003\u001dQ\u000b7o[*fi6\u000bg.Y4fe\"9\u0011\u0011\u0014\u0001\u0005\u0002\u0005m\u0015\u0001F5oGJ,\u0017m]3Sk:t\u0017N\\4UCN\\7\u000fF\u0002t\u0003;Cq!a(\u0002\u0018\u0002\u0007\u0011)A\u0004uCN\\g*^7\t\u000f\u0005\r\u0006\u0001\"\u0001\u0002&\u0006!B-Z2sK\u0006\u001cXMU;o]&tw\rV1tWN$2a]AT\u0011\u001d\ty*!)A\u0002\u0005\u0003"
)
public class Pool implements Schedulable, Logging {
   private final String poolName;
   private final Enumeration.Value schedulingMode;
   private final ConcurrentLinkedQueue<Schedulable> schedulableQueue;
   private final ConcurrentHashMap<String, Schedulable> schedulableNameToSchedulable;
   private final int weight;
   private final int minShare;
   private int runningTasks;
   private final int priority;
   private int stageId;
   private final String name;
   private Pool parent;
   private final SchedulingAlgorithm org$apache$spark$scheduler$Pool$$taskSetSchedulingAlgorithm;
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

   public String poolName() {
      return this.poolName;
   }

   public Enumeration.Value schedulingMode() {
      return this.schedulingMode;
   }

   public ConcurrentLinkedQueue<Schedulable> schedulableQueue() {
      return this.schedulableQueue;
   }

   public ConcurrentHashMap<String, Schedulable> schedulableNameToSchedulable() {
      return this.schedulableNameToSchedulable;
   }

   public int weight() {
      return this.weight;
   }

   public int minShare() {
      return this.minShare;
   }

   public int runningTasks() {
      return this.runningTasks;
   }

   public void runningTasks_$eq(int x$1) {
      this.runningTasks = x$1;
   }

   public int priority() {
      return this.priority;
   }

   public int stageId() {
      return this.stageId;
   }

   public void stageId_$eq(int x$1) {
      this.stageId = x$1;
   }

   public String name() {
      return this.name;
   }

   public Pool parent() {
      return this.parent;
   }

   public void parent_$eq(Pool x$1) {
      this.parent = x$1;
   }

   public SchedulingAlgorithm org$apache$spark$scheduler$Pool$$taskSetSchedulingAlgorithm() {
      return this.org$apache$spark$scheduler$Pool$$taskSetSchedulingAlgorithm;
   }

   public void addSchedulable(Schedulable schedulable) {
      Predef$.MODULE$.require(schedulable != null);
      this.schedulableQueue().add(schedulable);
      this.schedulableNameToSchedulable().put(schedulable.name(), schedulable);
      schedulable.parent_$eq(this);
   }

   public void removeSchedulable(Schedulable schedulable) {
      this.schedulableQueue().remove(schedulable);
      this.schedulableNameToSchedulable().remove(schedulable.name());
   }

   public Schedulable getSchedulableByName(String schedulableName) {
      Object var2 = new Object();

      Schedulable var10000;
      try {
         if (this.schedulableNameToSchedulable().containsKey(schedulableName)) {
            return (Schedulable)this.schedulableNameToSchedulable().get(schedulableName);
         }

         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.schedulableQueue()).asScala()).foreach(new Serializable(this, schedulableName, var2) {
            public static final long serialVersionUID = 0L;
            private final String schedulableName$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(Schedulable schedulable) {
               Schedulable sched = schedulable.getSchedulableByName(this.schedulableName$1);
               if (sched != null) {
                  throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, sched);
               }
            }

            public {
               this.schedulableName$1 = schedulableName$1;
               this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
            }
         });
         var10000 = null;
      } catch (NonLocalReturnControl var4) {
         if (var4.key() != var2) {
            throw var4;
         }

         var10000 = (Schedulable)var4.value();
      }

      return var10000;
   }

   public void executorLost(String executorId, String host, ExecutorLossReason reason) {
      ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.schedulableQueue()).asScala()).foreach(new Serializable(this, executorId, host, reason) {
         public static final long serialVersionUID = 0L;
         private final String executorId$1;
         private final String host$1;
         private final ExecutorLossReason reason$1;

         public final void apply(Schedulable x$1) {
            x$1.executorLost(this.executorId$1, this.host$1, this.reason$1);
         }

         public {
            this.executorId$1 = executorId$1;
            this.host$1 = host$1;
            this.reason$1 = reason$1;
         }
      });
   }

   public boolean checkSpeculatableTasks(int minTimeToSpeculation) {
      BooleanRef shouldRevive = BooleanRef.create(false);
      ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.schedulableQueue()).asScala()).foreach(new Serializable(this, minTimeToSpeculation, shouldRevive) {
         public static final long serialVersionUID = 0L;
         private final int minTimeToSpeculation$1;
         private final BooleanRef shouldRevive$1;

         public final void apply(Schedulable schedulable) {
            this.shouldRevive$1.elem |= schedulable.checkSpeculatableTasks(this.minTimeToSpeculation$1);
         }

         public {
            this.minTimeToSpeculation$1 = minTimeToSpeculation$1;
            this.shouldRevive$1 = shouldRevive$1;
         }
      });
      return shouldRevive.elem;
   }

   public ArrayBuffer<TaskSetManager> getSortedTaskSetQueue() {
      ArrayBuffer sortedTaskSetQueue = new ArrayBuffer();
      Seq sortedSchedulableQueue = (Seq)((TraversableOnce)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.schedulableQueue()).asScala()).toSeq().sortWith(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Pool $outer;

         public final boolean apply(Schedulable s1, Schedulable s2) {
            return this.$outer.org$apache$spark$scheduler$Pool$$taskSetSchedulingAlgorithm().comparator(s1, s2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      sortedSchedulableQueue.foreach(new Serializable(this, sortedTaskSetQueue) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer sortedTaskSetQueue$1;

         public final ArrayBuffer<TaskSetManager> apply(Schedulable schedulable) {
            return this.sortedTaskSetQueue$1.$plus$plus$eq(schedulable.getSortedTaskSetQueue());
         }

         public {
            this.sortedTaskSetQueue$1 = sortedTaskSetQueue$1;
         }
      });
      return sortedTaskSetQueue;
   }

   public void increaseRunningTasks(int taskNum) {
      this.runningTasks_$eq(this.runningTasks() + taskNum);
      if (this.parent() != null) {
         this.parent().increaseRunningTasks(taskNum);
      }

   }

   public void decreaseRunningTasks(int taskNum) {
      this.runningTasks_$eq(this.runningTasks() - taskNum);
      if (this.parent() != null) {
         this.parent().decreaseRunningTasks(taskNum);
      }

   }

   public Pool(String poolName, Enumeration.Value schedulingMode, int initMinShare, int initWeight) {
      Object var7;
      label29: {
         label31: {
            this.poolName = poolName;
            this.schedulingMode = schedulingMode;
            super();
            Logging$class.$init$(this);
            this.schedulableQueue = new ConcurrentLinkedQueue();
            this.schedulableNameToSchedulable = new ConcurrentHashMap();
            this.weight = initWeight;
            this.minShare = initMinShare;
            this.runningTasks = 0;
            this.priority = 0;
            this.stageId = -1;
            this.name = poolName;
            this.parent = null;
            Enumeration.Value var10001 = SchedulingMode$.MODULE$.FAIR();
            if (var10001 == null) {
               if (schedulingMode == null) {
                  break label31;
               }
            } else if (var10001.equals(schedulingMode)) {
               break label31;
            }

            label22: {
               var10001 = SchedulingMode$.MODULE$.FIFO();
               if (var10001 == null) {
                  if (schedulingMode == null) {
                     break label22;
                  }
               } else if (var10001.equals(schedulingMode)) {
                  break label22;
               }

               String msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsupported scheduling mode: ", ". Use FAIR or FIFO instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{schedulingMode}));
               throw new IllegalArgumentException(msg);
            }

            var7 = new FIFOSchedulingAlgorithm();
            break label29;
         }

         var7 = new FairSchedulingAlgorithm();
      }

      this.org$apache$spark$scheduler$Pool$$taskSetSchedulingAlgorithm = (SchedulingAlgorithm)var7;
   }
}
