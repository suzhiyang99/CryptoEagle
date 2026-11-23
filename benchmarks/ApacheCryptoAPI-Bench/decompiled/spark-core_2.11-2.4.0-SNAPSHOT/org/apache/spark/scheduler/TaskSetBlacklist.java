package org.apache.spark.scheduler;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Clock;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterable;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]d!B\u0001\u0003\u0001\tQ!\u0001\u0005+bg.\u001cV\r\u001e\"mC\u000e\\G.[:u\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0006\u0004%IAG\u0001\fY&\u001cH/\u001a8fe\n+8o\u0001\u0001\u0016\u0003m\u0001\"\u0001H\u000f\u000e\u0003\tI!A\b\u0002\u0003\u001f1Kg/\u001a'jgR,g.\u001a:CkND\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006IaG\u0001\rY&\u001cH/\u001a8fe\n+8\u000f\t\u0005\tE\u0001\u0011)\u0019!C\u0001G\u0005!1m\u001c8g+\u0005!\u0003CA\u0013'\u001b\u0005!\u0011BA\u0014\u0005\u0005%\u0019\u0006/\u0019:l\u0007>tg\r\u0003\u0005*\u0001\t\u0005\t\u0015!\u0003%\u0003\u0015\u0019wN\u001c4!\u0011!Y\u0003A!b\u0001\n\u0003a\u0013aB:uC\u001e,\u0017\nZ\u000b\u0002[A\u0011ABL\u0005\u0003_5\u00111!\u00138u\u0011!\t\u0004A!A!\u0002\u0013i\u0013\u0001C:uC\u001e,\u0017\n\u001a\u0011\t\u0011M\u0002!Q1A\u0005\u00021\nab\u001d;bO\u0016\fE\u000f^3naRLE\r\u0003\u00056\u0001\t\u0005\t\u0015!\u0003.\u0003=\u0019H/Y4f\u0003R$X-\u001c9u\u0013\u0012\u0004\u0003\u0002C\u001c\u0001\u0005\u000b\u0007I\u0011\u0001\u001d\u0002\u000b\rdwnY6\u0016\u0003e\u0002\"AO\u001f\u000e\u0003mR!\u0001\u0010\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003}m\u0012Qa\u00117pG.D\u0001\u0002\u0011\u0001\u0003\u0002\u0003\u0006I!O\u0001\u0007G2|7m\u001b\u0011\t\u000b\t\u0003A\u0011A\"\u0002\rqJg.\u001b;?)\u0019!UIR$I\u0013B\u0011A\u0004\u0001\u0005\u00061\u0005\u0003\ra\u0007\u0005\u0006E\u0005\u0003\r\u0001\n\u0005\u0006W\u0005\u0003\r!\f\u0005\u0006g\u0005\u0003\r!\f\u0005\u0006o\u0005\u0003\r!\u000f\u0005\b\u0017\u0002\u0011\r\u0011\"\u0003-\u0003yi\u0015\tW0U\u0003N[u,\u0011+U\u000b6\u0003FkU0Q\u000bJ{V\tW#D+R{%\u000b\u0003\u0004N\u0001\u0001\u0006I!L\u0001 \u001b\u0006Cv\fV!T\u0017~\u000bE\u000bV#N!R\u001bv\fU#S?\u0016CViQ+U\u001fJ\u0003\u0003bB(\u0001\u0005\u0004%I\u0001L\u0001\u001b\u001b\u0006Cv\fV!T\u0017~\u000bE\u000bV#N!R\u001bv\fU#S?:{E)\u0012\u0005\u0007#\u0002\u0001\u000b\u0011B\u0017\u000275\u000b\u0005l\u0018+B'.{\u0016\t\u0016+F\u001bB#6k\u0018)F%~su\nR#!\u0011\u001d\u0019\u0006A1A\u0005\n1\n1$T!Y?\u001a\u000b\u0015\nT+S\u000bN{\u0006+\u0012*`\u000bb+5iX*U\u0003\u001e+\u0005BB+\u0001A\u0003%Q&\u0001\u000fN\u0003b{f)Q%M+J+5k\u0018)F%~+\u0005,R\"`'R\u000bu)\u0012\u0011\t\u000f]\u0003!\u0019!C\u0005Y\u0005qR*\u0011-`\r\u0006KE*\u0012#`\u000bb+5i\u0018)F%~su\nR#`'R\u000bu)\u0012\u0005\u00073\u0002\u0001\u000b\u0011B\u0017\u0002?5\u000b\u0005l\u0018$B\u00132+EiX#Y\u000b\u000e{\u0006+\u0012*`\u001d>#UiX*U\u0003\u001e+\u0005\u0005C\u0004\\\u0001\t\u0007I\u0011\u0001/\u0002\u001d\u0015DXm\u0019+p\r\u0006LG.\u001e:fgV\tQ\f\u0005\u0003_G\u0016dW\"A0\u000b\u0005\u0001\f\u0017aB7vi\u0006\u0014G.\u001a\u0006\u0003E6\t!bY8mY\u0016\u001cG/[8o\u0013\t!wLA\u0004ICNDW*\u00199\u0011\u0005\u0019LgB\u0001\u0007h\u0013\tAW\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003U.\u0014aa\u0015;sS:<'B\u00015\u000e!\taR.\u0003\u0002o\u0005\tIR\t_3dkR|'OR1jYV\u0014Xm]%o)\u0006\u001c8nU3u\u0011\u0019\u0001\b\u0001)A\u0005;\u0006yQ\r_3d)>4\u0015-\u001b7ve\u0016\u001c\b\u0005C\u0004s\u0001\t\u0007I\u0011B:\u0002/9|G-\u001a+p\u000bb,7m],ji\"4\u0015-\u001b7ve\u0016\u001cX#\u0001;\u0011\ty\u001bW-\u001e\t\u0004=Z,\u0017BA<`\u0005\u001dA\u0015m\u001d5TKRDa!\u001f\u0001!\u0002\u0013!\u0018\u0001\u00078pI\u0016$v.\u0012=fGN<\u0016\u000e\u001e5GC&dWO]3tA!91\u0010\u0001b\u0001\n\u0013a\u0018\u0001\b8pI\u0016$vN\u00117bG.d\u0017n\u001d;fIR\u000b7o[%oI\u0016DXm]\u000b\u0002{B!alY3\u007f!\rqf/\f\u0005\b\u0003\u0003\u0001\u0001\u0015!\u0003~\u0003uqw\u000eZ3U_\nc\u0017mY6mSN$X\r\u001a+bg.Le\u000eZ3yKN\u0004\u0003\"CA\u0003\u0001\t\u0007I\u0011BA\u0004\u0003A\u0011G.Y2lY&\u001cH/\u001a3Fq\u0016\u001c7/F\u0001v\u0011\u001d\tY\u0001\u0001Q\u0001\nU\f\u0011C\u00197bG.d\u0017n\u001d;fI\u0016CXmY:!\u0011%\ty\u0001\u0001b\u0001\n\u0013\t9!\u0001\tcY\u0006\u001c7\u000e\\5ti\u0016$gj\u001c3fg\"9\u00111\u0003\u0001!\u0002\u0013)\u0018!\u00052mC\u000e\\G.[:uK\u0012tu\u000eZ3tA!I\u0011q\u0003\u0001A\u0002\u0013%\u0011\u0011D\u0001\u0014Y\u0006$Xm\u001d;GC&dWO]3SK\u0006\u001cxN\\\u000b\u0002K\"I\u0011Q\u0004\u0001A\u0002\u0013%\u0011qD\u0001\u0018Y\u0006$Xm\u001d;GC&dWO]3SK\u0006\u001cxN\\0%KF$B!!\t\u0002(A\u0019A\"a\t\n\u0007\u0005\u0015RB\u0001\u0003V]&$\b\"CA\u0015\u00037\t\t\u00111\u0001f\u0003\rAH%\r\u0005\b\u0003[\u0001\u0001\u0015)\u0003f\u0003Qa\u0017\r^3ti\u001a\u000b\u0017\u000e\\;sKJ+\u0017m]8oA!9\u0011\u0011\u0007\u0001\u0005\u0002\u0005e\u0011AF4fi2\u000bG/Z:u\r\u0006LG.\u001e:f%\u0016\f7o\u001c8\t\u000f\u0005U\u0002\u0001\"\u0001\u00028\u0005a\u0012n]#yK\u000e,Ho\u001c:CY\u0006\u001c7\u000e\\5ti\u0016$gi\u001c:UCN\\GCBA\u001d\u0003\u007f\t\u0019\u0005E\u0002\r\u0003wI1!!\u0010\u000e\u0005\u001d\u0011un\u001c7fC:Dq!!\u0011\u00024\u0001\u0007Q-\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012Dq!!\u0012\u00024\u0001\u0007Q&A\u0003j]\u0012,\u0007\u0010C\u0004\u0002J\u0001!\t!a\u0013\u00021%\u001chj\u001c3f\u00052\f7m\u001b7jgR,GMR8s)\u0006\u001c8\u000e\u0006\u0004\u0002:\u00055\u0013\u0011\u000b\u0005\b\u0003\u001f\n9\u00051\u0001f\u0003\u0011qw\u000eZ3\t\u000f\u0005\u0015\u0013q\ta\u0001[!9\u0011Q\u000b\u0001\u0005\u0002\u0005]\u0013aH5t\u000bb,7-\u001e;pe\nc\u0017mY6mSN$X\r\u001a$peR\u000b7o[*fiR!\u0011\u0011HA-\u0011\u001d\t\t%a\u0015A\u0002\u0015Dq!!\u0018\u0001\t\u0003\ty&A\u000ejg:{G-\u001a\"mC\u000e\\G.[:uK\u00124uN\u001d+bg.\u001cV\r\u001e\u000b\u0005\u0003s\t\t\u0007C\u0004\u0002P\u0005m\u0003\u0019A3\t\u0011\u0005\u0015\u0004\u0001\"\u0001\u0003\u0003O\nA$\u001e9eCR,'\t\\1dW2L7\u000f\u001e$pe\u001a\u000b\u0017\u000e\\3e)\u0006\u001c8\u000e\u0006\u0006\u0002\"\u0005%\u0014QNA9\u0003gBq!a\u001b\u0002d\u0001\u0007Q-\u0001\u0003i_N$\bbBA8\u0003G\u0002\r!Z\u0001\u0005Kb,7\rC\u0004\u0002F\u0005\r\u0004\u0019A\u0017\t\u000f\u0005U\u00141\ra\u0001K\u0006ia-Y5mkJ,'+Z1t_:\u0004"
)
public class TaskSetBlacklist implements Logging {
   private final LiveListenerBus listenerBus;
   private final SparkConf conf;
   private final int stageId;
   private final int stageAttemptId;
   private final Clock clock;
   private final int org$apache$spark$scheduler$TaskSetBlacklist$$MAX_TASK_ATTEMPTS_PER_EXECUTOR;
   private final int MAX_TASK_ATTEMPTS_PER_NODE;
   private final int MAX_FAILURES_PER_EXEC_STAGE;
   private final int MAX_FAILED_EXEC_PER_NODE_STAGE;
   private final HashMap<String, ExecutorFailuresInTaskSet> execToFailures;
   private final HashMap<String, HashSet<String>> nodeToExecsWithFailures;
   private final HashMap<String, HashSet<Object>> nodeToBlacklistedTaskIndexes;
   private final HashSet<String> org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs;
   private final HashSet<String> blacklistedNodes;
   private String latestFailureReason;
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

   private LiveListenerBus listenerBus() {
      return this.listenerBus;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public Clock clock() {
      return this.clock;
   }

   public int org$apache$spark$scheduler$TaskSetBlacklist$$MAX_TASK_ATTEMPTS_PER_EXECUTOR() {
      return this.org$apache$spark$scheduler$TaskSetBlacklist$$MAX_TASK_ATTEMPTS_PER_EXECUTOR;
   }

   private int MAX_TASK_ATTEMPTS_PER_NODE() {
      return this.MAX_TASK_ATTEMPTS_PER_NODE;
   }

   private int MAX_FAILURES_PER_EXEC_STAGE() {
      return this.MAX_FAILURES_PER_EXEC_STAGE;
   }

   private int MAX_FAILED_EXEC_PER_NODE_STAGE() {
      return this.MAX_FAILED_EXEC_PER_NODE_STAGE;
   }

   public HashMap<String, ExecutorFailuresInTaskSet> execToFailures() {
      return this.execToFailures;
   }

   private HashMap<String, HashSet<String>> nodeToExecsWithFailures() {
      return this.nodeToExecsWithFailures;
   }

   private HashMap<String, HashSet<Object>> nodeToBlacklistedTaskIndexes() {
      return this.nodeToBlacklistedTaskIndexes;
   }

   public HashSet<String> org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs() {
      return this.org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs;
   }

   private HashSet<String> blacklistedNodes() {
      return this.blacklistedNodes;
   }

   private String latestFailureReason() {
      return this.latestFailureReason;
   }

   private void latestFailureReason_$eq(String x$1) {
      this.latestFailureReason = x$1;
   }

   public String getLatestFailureReason() {
      return this.latestFailureReason();
   }

   public boolean isExecutorBlacklistedForTask(String executorId, int index) {
      return this.execToFailures().get(executorId).exists(new Serializable(this, index) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetBlacklist $outer;
         private final int index$1;

         public final boolean apply(ExecutorFailuresInTaskSet execFailures) {
            return execFailures.getNumTaskFailures(this.index$1) >= this.$outer.org$apache$spark$scheduler$TaskSetBlacklist$$MAX_TASK_ATTEMPTS_PER_EXECUTOR();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.index$1 = index$1;
            }
         }
      });
   }

   public boolean isNodeBlacklistedForTask(String node, int index) {
      return this.nodeToBlacklistedTaskIndexes().get(node).exists(new Serializable(this, index) {
         public static final long serialVersionUID = 0L;
         private final int index$2;

         public final boolean apply(HashSet<Object> x$1) {
            return x$1.contains(BoxesRunTime.boxToInteger(this.index$2));
         }

         public {
            this.index$2 = index$2;
         }
      });
   }

   public boolean isExecutorBlacklistedForTaskSet(String executorId) {
      return this.org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs().contains(executorId);
   }

   public boolean isNodeBlacklistedForTaskSet(String node) {
      return this.blacklistedNodes().contains(node);
   }

   public void updateBlacklistForFailedTask(String host, String exec, int index, String failureReason) {
      this.latestFailureReason_$eq(failureReason);
      ExecutorFailuresInTaskSet execFailures = (ExecutorFailuresInTaskSet)this.execToFailures().getOrElseUpdate(exec, new Serializable(this, host) {
         public static final long serialVersionUID = 0L;
         private final String host$1;

         public final ExecutorFailuresInTaskSet apply() {
            return new ExecutorFailuresInTaskSet(this.host$1);
         }

         public {
            this.host$1 = host$1;
         }
      });
      execFailures.updateWithFailure(index, this.clock().getTimeMillis());
      HashSet execsWithFailuresOnNode = (HashSet)this.nodeToExecsWithFailures().getOrElseUpdate(host, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final HashSet<String> apply() {
            return new HashSet<String>();
         }
      });
      execsWithFailuresOnNode.$plus$eq(exec);
      int failuresOnHost = BoxesRunTime.unboxToInt(execsWithFailuresOnNode.toIterator().flatMap(new Serializable(this, index) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetBlacklist $outer;
         public final int index$3;

         public final Iterable<Object> apply(String exec) {
            return Option$.MODULE$.<Object>option2Iterable(this.$outer.execToFailures().get(exec).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final int apply(ExecutorFailuresInTaskSet failures) {
                  return failures.getNumTaskFailures(this.$outer.index$3);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.index$3 = index$3;
            }
         }
      }).sum(Numeric.IntIsIntegral$.MODULE$));
      if (failuresOnHost >= this.MAX_TASK_ATTEMPTS_PER_NODE()) {
         ((HashSet)this.nodeToBlacklistedTaskIndexes().getOrElseUpdate(host, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final HashSet<Object> apply() {
               return new HashSet<Object>();
            }
         })).$plus$eq(BoxesRunTime.boxToInteger(index));
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      int numFailures = execFailures.numUniqueTasksWithFailures();
      if (numFailures >= this.MAX_FAILURES_PER_EXEC_STAGE() && this.org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs().add(exec)) {
         this.logInfo(new Serializable(this, exec) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetBlacklist $outer;
            private final String exec$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Blacklisting executor ", " for stage ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$1, BoxesRunTime.boxToInteger(this.$outer.stageId())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.exec$1 = exec$1;
               }
            }
         });
         HashSet blacklistedExecutorsOnNode = (HashSet)execsWithFailuresOnNode.filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetBlacklist $outer;

            public final boolean apply(String x$2) {
               return this.$outer.org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs().contains(x$2);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         long now = this.clock().getTimeMillis();
         this.listenerBus().post(new SparkListenerExecutorBlacklistedForStage(now, exec, numFailures, this.stageId(), this.stageAttemptId()));
         int numFailExec = blacklistedExecutorsOnNode.size();
         if (numFailExec >= this.MAX_FAILED_EXEC_PER_NODE_STAGE() && this.blacklistedNodes().add(host)) {
            this.logInfo(new Serializable(this, host) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetBlacklist $outer;
               private final String host$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Blacklisting ", " for stage ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1, BoxesRunTime.boxToInteger(this.$outer.stageId())}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.host$1 = host$1;
                  }
               }
            });
            this.listenerBus().post(new SparkListenerNodeBlacklistedForStage(now, host, numFailExec, this.stageId(), this.stageAttemptId()));
         }
      }

   }

   public TaskSetBlacklist(LiveListenerBus listenerBus, SparkConf conf, int stageId, int stageAttemptId, Clock clock) {
      this.listenerBus = listenerBus;
      this.conf = conf;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.clock = clock;
      Logging$class.$init$(this);
      this.org$apache$spark$scheduler$TaskSetBlacklist$$MAX_TASK_ATTEMPTS_PER_EXECUTOR = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_EXECUTOR()));
      this.MAX_TASK_ATTEMPTS_PER_NODE = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_NODE()));
      this.MAX_FAILURES_PER_EXEC_STAGE = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_FAILURES_PER_EXEC_STAGE()));
      this.MAX_FAILED_EXEC_PER_NODE_STAGE = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_FAILED_EXEC_PER_NODE_STAGE()));
      this.execToFailures = new HashMap<String, ExecutorFailuresInTaskSet>();
      this.nodeToExecsWithFailures = new HashMap<String, HashSet<String>>();
      this.nodeToBlacklistedTaskIndexes = new HashMap<String, HashSet<Object>>();
      this.org$apache$spark$scheduler$TaskSetBlacklist$$blacklistedExecs = new HashSet<String>();
      this.blacklistedNodes = new HashSet<String>();
      this.latestFailureReason = null;
   }
}
