package org.apache.spark.storage;

import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import org.spark_project.guava.collect.ConcurrentHashMultiset;
import org.spark_project.guava.collect.ImmutableMultiset;
import org.spark_project.guava.collect.Multiset;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Iterable$;
import scala.collection.mutable.MultiMap;
import scala.collection.mutable.MultiMap$class;
import scala.collection.mutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001df!B\u0001\u0003\u0001\tQ!\u0001\u0005\"m_\u000e\\\u0017J\u001c4p\u001b\u0006t\u0017mZ3s\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!#F\u0007\u0002')\u0011A\u0003B\u0001\tS:$XM\u001d8bY&\u0011ac\u0005\u0002\b\u0019><w-\u001b8h\u0011\u0015A\u0002\u0001\"\u0001\u001b\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000e\u0011\u0005q\u0001Q\"\u0001\u0002\u0006\ty\u0001Aa\b\u0002\u000e)\u0006\u001c8.\u0011;uK6\u0004H/\u00133\u0011\u00051\u0001\u0013BA\u0011\u000e\u0005\u0011auN\\4\t\r\r\u0002\u0001\u0015!\u0003%\u0003\u0015IgNZ8t!\u0011)#\u0006L\u0018\u000e\u0003\u0019R!a\n\u0015\u0002\u000f5,H/\u00192mK*\u0011\u0011&D\u0001\u000bG>dG.Z2uS>t\u0017BA\u0016'\u0005\u001dA\u0015m\u001d5NCB\u0004\"\u0001H\u0017\n\u00059\u0012!a\u0002\"m_\u000e\\\u0017\n\u001a\t\u00039AJ!!\r\u0002\u0003\u0013\tcwnY6J]\u001a|\u0007\u0006\u0002\u00124{y\u0002\"\u0001N\u001e\u000e\u0003UR!AN\u001c\u0002\u0015\r|gnY;se\u0016tGO\u0003\u00029s\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\u000b\u0003i\nQA[1wCbL!\u0001P\u001b\u0003\u0013\u001d+\u0018M\u001d3fI\nK\u0018!\u0002<bYV,\u0017%A \u0002\tQD\u0017n\u001d\u0005\u0007\u0003\u0002\u0001\u000b\u0011\u0002\"\u0002!]\u0014\u0018\u000e^3M_\u000e\\7OQ=UCN\\'cA\"F\u0017\u001a!A\t\u0011\u0001C\u00051a$/\u001a4j]\u0016lWM\u001c;?!\u0011)#F\u0012%\u0011\u0005\u001dkR\"\u0001\u0001\u0011\u0007\u0015JE&\u0003\u0002KM\t\u00191+\u001a;\u0011\t\u0015be\tL\u0005\u0003\u001b\u001a\u0012\u0001\"T;mi&l\u0015\r\u001d\u0015\u0005\u0001Njd\b\u0003\u0004Q\u0001\u0001\u0006I!U\u0001\u0010e\u0016\fG\rT8dWN\u0014\u0015\u0010V1tWB!QE\u000b$S!\r\u0019F\fL\u0007\u0002)*\u0011QKV\u0001\bG>dG.Z2u\u0015\t9\u0006,\u0001\u0004d_6lwN\u001c\u0006\u00033j\u000baaZ8pO2,'\"A.\u0002\u0007\r|W.\u0003\u0002^)\n12i\u001c8dkJ\u0014XM\u001c;ICNDW*\u001e7uSN,G\u000f\u000b\u0003Pgur\u0004\"\u00021\u0001\t\u0003\t\u0017\u0001\u0004:fO&\u001cH/\u001a:UCN\\GC\u00012f!\ta1-\u0003\u0002e\u001b\t!QK\\5u\u0011\u00151w\f1\u0001G\u00035!\u0018m]6BiR,W\u000e\u001d;JI\")\u0001\u000e\u0001C\u0005S\u0006!2-\u001e:sK:$H+Y:l\u0003R$X-\u001c9u\u0013\u0012,\u0012A\u0012\u0005\u0006W\u0002!\t\u0001\\\u0001\u000fY>\u001c7NR8s%\u0016\fG-\u001b8h)\ri\u0007O\u001d\t\u0004\u00199|\u0013BA8\u000e\u0005\u0019y\u0005\u000f^5p]\")\u0011O\u001ba\u0001Y\u00059!\r\\8dW&#\u0007bB:k!\u0003\u0005\r\u0001^\u0001\tE2|7m[5oOB\u0011A\"^\u0005\u0003m6\u0011qAQ8pY\u0016\fg\u000eC\u0003y\u0001\u0011\u0005\u00110\u0001\bm_\u000e\\gi\u001c:Xe&$\u0018N\\4\u0015\u00075T8\u0010C\u0003ro\u0002\u0007A\u0006C\u0004toB\u0005\t\u0019\u0001;\t\u000bu\u0004A\u0011\u0001@\u0002;\u0005\u001c8/\u001a:u\u00052|7m[%t\u0019>\u001c7.\u001a3G_J<&/\u001b;j]\u001e$\"aL@\t\u000bEd\b\u0019\u0001\u0017\t\u0011\u0005\r\u0001\u0001\"\u0001\u0003\u0003\u000b\t1aZ3u)\ri\u0017q\u0001\u0005\u0007c\u0006\u0005\u0001\u0019\u0001\u0017\t\u000f\u0005-\u0001\u0001\"\u0001\u0002\u000e\u0005iAm\\<oOJ\fG-\u001a'pG.$2AYA\b\u0011\u0019\t\u0018\u0011\u0002a\u0001Y!9\u00111\u0003\u0001\u0005\u0002\u0005U\u0011AB;oY>\u001c7\u000eF\u0003c\u0003/\tI\u0002\u0003\u0004r\u0003#\u0001\r\u0001\f\u0005\nM\u0006E\u0001\u0013!a\u0001\u00037\u00012\u0001\u00048G\u0011\u001d\ty\u0002\u0001C\u0001\u0003C\ta\u0003\\8dW:+wO\u00117pG.4uN],sSRLgn\u001a\u000b\u0006i\u0006\r\u0012Q\u0005\u0005\u0007c\u0006u\u0001\u0019\u0001\u0017\t\u000f\u0005\u001d\u0012Q\u0004a\u0001_\u0005aa.Z<CY>\u001c7.\u00138g_\"9\u00111\u0006\u0001\u0005\u0002\u00055\u0012A\u0006:fY\u0016\f7/Z!mY2{7m[:G_J$\u0016m]6\u0015\t\u0005=\u0012q\t\t\u0006\u0003c\t\t\u0005\f\b\u0005\u0003g\tiD\u0004\u0003\u00026\u0005mRBAA\u001c\u0015\r\tI$G\u0001\u0007yI|w\u000e\u001e \n\u00039I1!a\u0010\u000e\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\u0011\u0002F\t\u00191+Z9\u000b\u0007\u0005}R\u0002\u0003\u0004g\u0003S\u0001\rA\u0012\u0005\t\u0003\u0017\u0002A\u0011\u0001\u0002\u0002N\u0005\u0001r-\u001a;UCN\\Gj\\2l\u0007>,h\u000e\u001e\u000b\u0005\u0003\u001f\n)\u0006E\u0002\r\u0003#J1!a\u0015\u000e\u0005\rIe\u000e\u001e\u0005\u0007M\u0006%\u0003\u0019\u0001$\t\u000f\u0005e\u0003\u0001\"\u0001\u0002\\\u0005!1/\u001b>f+\t\ty\u0005\u0003\u0005\u0002`\u0001!\tAAA1\u0003U9W\r\u001e(v[\n,'o\u00144NCB,e\u000e\u001e:jKN,\u0012a\b\u0005\b\u0003K\u0002A\u0011AA4\u0003\u001d)g\u000e\u001e:jKN,\"!!\u001b\u0011\r\u0005E\u00121NA8\u0013\u0011\ti'!\u0012\u0003\u0011%#XM]1u_J\u0004R\u0001DA9Y=J1!a\u001d\u000e\u0005\u0019!V\u000f\u001d7fe!9\u0011q\u000f\u0001\u0005\u0002\u0005e\u0014a\u0003:f[>4XM\u00117pG.$2AYA>\u0011\u0019\t\u0018Q\u000fa\u0001Y!9\u0011q\u0010\u0001\u0005\u0002\u0005\u0005\u0015!B2mK\u0006\u0014H#\u00012\t\u0013\u0005\u0015\u0005!%A\u0005\u0002\u0005\u001d\u0015\u0001\u00077pG.4uN\u001d*fC\u0012Lgn\u001a\u0013eK\u001a\fW\u000f\u001c;%eU\u0011\u0011\u0011\u0012\u0016\u0004i\u0006-5FAAG!\u0011\ty)a&\u000e\u0005\u0005E%\u0002BAJ\u0003+\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005aj\u0011\u0002BAM\u0003#\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011%\ti\nAI\u0001\n\u0003\t9)\u0001\rm_\u000e\\gi\u001c:Xe&$\u0018N\\4%I\u00164\u0017-\u001e7uIIB\u0011\"!)\u0001#\u0003%\t!a)\u0002!UtGn\\2lI\u0011,g-Y;mi\u0012\u0012TCAASU\u0011\tY\"a#"
)
public class BlockInfoManager implements Logging {
   @GuardedBy("this")
   public final HashMap<BlockId, BlockInfo> org$apache$spark$storage$BlockInfoManager$$infos;
   @GuardedBy("this")
   private final HashMap<Object, Set<BlockId>> writeLocksByTask;
   @GuardedBy("this")
   private final HashMap<Object, ConcurrentHashMultiset<BlockId>> readLocksByTask;
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

   public synchronized void registerTask(long taskAttemptId) {
      Predef$.MODULE$.require(!this.readLocksByTask.contains(BoxesRunTime.boxToLong(taskAttemptId)), new Serializable(this, taskAttemptId) {
         public static final long serialVersionUID = 0L;
         private final long taskAttemptId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task attempt ", " is already registered"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskAttemptId$1)}));
         }

         public {
            this.taskAttemptId$1 = taskAttemptId$1;
         }
      });
      this.readLocksByTask.update(BoxesRunTime.boxToLong(taskAttemptId), ConcurrentHashMultiset.create());
   }

   public long org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId() {
      return BoxesRunTime.unboxToLong(Option$.MODULE$.apply(TaskContext$.MODULE$.get()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskContext x$1) {
            return x$1.taskAttemptId();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BlockInfo$.MODULE$.NON_TASK_WRITER();
         }
      }));
   }

   public synchronized Option<BlockInfo> lockForReading(BlockId blockId, boolean blocking) {
      this.logTrace(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " trying to acquire read lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$1}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$1 = blockId$1;
            }
         }
      });

      do {
         Option var3 = this.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId);
         if (None$.MODULE$.equals(var3)) {
            return None$.MODULE$;
         }

         if (!(var3 instanceof Some)) {
            throw new MatchError(var3);
         }

         Some var5 = (Some)var3;
         BlockInfo info = (BlockInfo)var5.x();
         if (info.writerTask() == BlockInfo$.MODULE$.NO_WRITER()) {
            info.readerCount_$eq(info.readerCount() + 1);
            ((ConcurrentHashMultiset)this.readLocksByTask.apply(BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()))).add(blockId);
            this.logTrace(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockInfoManager $outer;
               private final BlockId blockId$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " acquired read lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$1 = blockId$1;
                  }
               }
            });
            return new Some<BlockInfo>(info);
         }

         BoxedUnit var4 = BoxedUnit.UNIT;
         if (blocking) {
            this.wait();
         }
      } while(blocking);

      return None$.MODULE$;
   }

   public boolean lockForReading$default$2() {
      return true;
   }

   public synchronized Option<BlockInfo> lockForWriting(BlockId blockId, boolean blocking) {
      this.logTrace(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " trying to acquire write lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$2}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$2 = blockId$2;
            }
         }
      });

      do {
         Option var3 = this.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId);
         if (None$.MODULE$.equals(var3)) {
            return None$.MODULE$;
         }

         if (!(var3 instanceof Some)) {
            throw new MatchError(var3);
         }

         Some var5 = (Some)var3;
         BlockInfo info = (BlockInfo)var5.x();
         if (info.writerTask() == BlockInfo$.MODULE$.NO_WRITER() && info.readerCount() == 0) {
            info.writerTask_$eq(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId());
            ((MultiMap)this.writeLocksByTask).addBinding(BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), blockId);
            this.logTrace(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockInfoManager $outer;
               private final BlockId blockId$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " acquired write lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$2}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$2 = blockId$2;
                  }
               }
            });
            return new Some<BlockInfo>(info);
         }

         BoxedUnit var4 = BoxedUnit.UNIT;
         if (blocking) {
            this.wait();
         }
      } while(blocking);

      return None$.MODULE$;
   }

   public boolean lockForWriting$default$2() {
      return true;
   }

   public synchronized BlockInfo assertBlockIsLockedForWriting(BlockId blockId) {
      Option var2 = this.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId);
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         BlockInfo info = (BlockInfo)var3.x();
         if (info.writerTask() != this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()) {
            throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " has not locked block ", " for writing"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), blockId})));
         } else {
            return info;
         }
      } else if (None$.MODULE$.equals(var2)) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{blockId})));
      } else {
         throw new MatchError(var2);
      }
   }

   public synchronized Option<BlockInfo> get(BlockId blockId) {
      return this.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId);
   }

   public synchronized void downgradeLock(BlockId blockId) {
      this.logTrace(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$3;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " downgrading write lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$3}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$3 = blockId$3;
            }
         }
      });
      BlockInfo info = (BlockInfo)this.get(blockId).get();
      Predef$.MODULE$.require(info.writerTask() == this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId(), new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$3;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " tried to downgrade a write lock that it does not hold on"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$3}))).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$3 = blockId$3;
            }
         }
      });
      this.unlock(blockId, this.unlock$default$2());
      Option lockOutcome = this.lockForReading(blockId, false);
      Predef$.MODULE$.assert(lockOutcome.isDefined());
   }

   public synchronized void unlock(BlockId blockId, Option<Object> taskAttemptId) {
      long taskId = BoxesRunTime.unboxToLong(taskAttemptId.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.logTrace(new Serializable(this, blockId, taskId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$4;
         private final long taskId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " releasing lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1), this.blockId$4}));
         }

         public {
            this.blockId$4 = blockId$4;
            this.taskId$1 = taskId$1;
         }
      });
      BlockInfo info = (BlockInfo)this.get(blockId).getOrElse(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$4;

         public final Nothing$ apply() {
            throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4})));
         }

         public {
            this.blockId$4 = blockId$4;
         }
      });
      if (info.writerTask() != BlockInfo$.MODULE$.NO_WRITER()) {
         info.writerTask_$eq(BlockInfo$.MODULE$.NO_WRITER());
         ((MultiMap)this.writeLocksByTask).removeBinding(BoxesRunTime.boxToLong(taskId), blockId);
      } else {
         Predef$.MODULE$.assert(info.readerCount() > 0, new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " is not locked for reading"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4}));
            }

            public {
               this.blockId$4 = blockId$4;
            }
         });
         info.readerCount_$eq(info.readerCount() - 1);
         ConcurrentHashMultiset countsForTask = this.readLocksByTask.apply(BoxesRunTime.boxToLong(taskId));
         int newPinCountForTask = countsForTask.remove(blockId, 1) - 1;
         Predef$.MODULE$.assert(newPinCountForTask >= 0, new Serializable(this, blockId, taskId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$4;
            private final long taskId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " release lock on block ", " more times than it acquired it"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1), this.blockId$4}));
            }

            public {
               this.blockId$4 = blockId$4;
               this.taskId$1 = taskId$1;
            }
         });
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.notifyAll();
   }

   public Option<Object> unlock$default$2() {
      return None$.MODULE$;
   }

   public synchronized boolean lockNewBlockForWriting(BlockId blockId, BlockInfo newBlockInfo) {
      this.logTrace(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$5;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " trying to put ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$5}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$5 = blockId$5;
            }
         }
      });
      Option var3 = this.lockForReading(blockId, this.lockForReading$default$2());
      boolean var4;
      if (var3 instanceof Some) {
         var4 = false;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         this.org$apache$spark$storage$BlockInfoManager$$infos.update(blockId, newBlockInfo);
         this.lockForWriting(blockId, this.lockForWriting$default$2());
         var4 = true;
      }

      return var4;
   }

   public synchronized Seq<BlockId> releaseAllLocksForTask(long taskAttemptId) {
      ArrayBuffer blocksWithReleasedLocks = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      Multiset readLocks = (Multiset)this.readLocksByTask.remove(BoxesRunTime.boxToLong(taskAttemptId)).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ImmutableMultiset<BlockId> apply() {
            return ImmutableMultiset.of();
         }
      });
      Iterable writeLocks = (Iterable)this.writeLocksByTask.remove(BoxesRunTime.boxToLong(taskAttemptId)).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      writeLocks.foreach(new Serializable(this, taskAttemptId, blocksWithReleasedLocks) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         public final long taskAttemptId$2;
         private final ArrayBuffer blocksWithReleasedLocks$1;

         public final ArrayBuffer<BlockId> apply(BlockId blockId) {
            this.$outer.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(BlockInfo info) {
                  Predef$.MODULE$.assert(info.writerTask() == this.$outer.taskAttemptId$2);
                  info.writerTask_$eq(BlockInfo$.MODULE$.NO_WRITER());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            return this.blocksWithReleasedLocks$1.$plus$eq(blockId);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.taskAttemptId$2 = taskAttemptId$2;
               this.blocksWithReleasedLocks$1 = blocksWithReleasedLocks$1;
            }
         }
      });
      ((Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(readLocks.entrySet().iterator()).asScala()).foreach(new Serializable(this, blocksWithReleasedLocks) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final ArrayBuffer blocksWithReleasedLocks$1;

         public final void apply(Multiset.Entry<BlockId> entry) {
            BlockId blockId = (BlockId)entry.getElement();
            int lockCount = entry.getCount();
            this.blocksWithReleasedLocks$1.$plus$eq(blockId);
            this.$outer.get(blockId).foreach(new Serializable(this, lockCount) {
               public static final long serialVersionUID = 0L;
               private final int lockCount$1;

               public final void apply(BlockInfo info) {
                  info.readerCount_$eq(info.readerCount() - this.lockCount$1);
                  Predef$.MODULE$.assert(info.readerCount() >= 0);
               }

               public {
                  this.lockCount$1 = lockCount$1;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blocksWithReleasedLocks$1 = blocksWithReleasedLocks$1;
            }
         }
      });
      this.notifyAll();
      return blocksWithReleasedLocks;
   }

   public int getTaskLockCount(long taskAttemptId) {
      return BoxesRunTime.unboxToInt(this.readLocksByTask.get(BoxesRunTime.boxToLong(taskAttemptId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(ConcurrentHashMultiset<BlockId> x$2) {
            return x$2.size();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      })) + BoxesRunTime.unboxToInt(this.writeLocksByTask.get(BoxesRunTime.boxToLong(taskAttemptId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Set<BlockId> x$3) {
            return x$3.size();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
   }

   public synchronized int size() {
      return this.org$apache$spark$storage$BlockInfoManager$$infos.size();
   }

   public synchronized long getNumberOfMapEntries() {
      return (long)(this.size() + this.readLocksByTask.size() + BoxesRunTime.unboxToInt(((TraversableOnce)this.readLocksByTask.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Tuple2<Object, ConcurrentHashMultiset<BlockId>> x$4) {
            return ((ConcurrentHashMultiset)x$4._2()).size();
         }
      }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$)) + this.writeLocksByTask.size() + BoxesRunTime.unboxToInt(((TraversableOnce)this.writeLocksByTask.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Tuple2<Object, Set<BlockId>> x$5) {
            return ((TraversableOnce)x$5._2()).size();
         }
      }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$)));
   }

   public synchronized Iterator<Tuple2<BlockId, BlockInfo>> entries() {
      return Predef$.MODULE$.refArrayOps(this.org$apache$spark$storage$BlockInfoManager$$infos.toArray(ClassTag$.MODULE$.apply(Tuple2.class))).toIterator();
   }

   public synchronized void removeBlock(BlockId blockId) {
      this.logTrace(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockInfoManager $outer;
         private final BlockId blockId$6;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " trying to remove block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), this.blockId$6}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$6 = blockId$6;
            }
         }
      });
      Option var2 = this.org$apache$spark$storage$BlockInfoManager$$infos.get(blockId);
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         BlockInfo blockInfo = (BlockInfo)var3.x();
         if (blockInfo.writerTask() != this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()) {
            throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " called remove() on block ", " without a write lock"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), blockId})));
         } else {
            this.org$apache$spark$storage$BlockInfoManager$$infos.remove(blockId);
            blockInfo.readerCount_$eq(0);
            blockInfo.writerTask_$eq(BlockInfo$.MODULE$.NO_WRITER());
            HashMap var5 = (HashMap)(this.writeLocksByTask).removeBinding(BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), blockId);
            this.notifyAll();
         }
      } else if (None$.MODULE$.equals(var2)) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " called remove() on non-existent block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockInfoManager$$currentTaskAttemptId()), blockId})));
      } else {
         throw new MatchError(var2);
      }
   }

   public synchronized void clear() {
      this.org$apache$spark$storage$BlockInfoManager$$infos.valuesIterator().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(BlockInfo blockInfo) {
            blockInfo.readerCount_$eq(0);
            blockInfo.writerTask_$eq(BlockInfo$.MODULE$.NO_WRITER());
         }
      });
      this.org$apache$spark$storage$BlockInfoManager$$infos.clear();
      this.readLocksByTask.clear();
      this.writeLocksByTask.clear();
      this.notifyAll();
   }

   public BlockInfoManager() {
      Logging$class.$init$(this);
      this.org$apache$spark$storage$BlockInfoManager$$infos = new HashMap<BlockId, BlockInfo>();
      this.writeLocksByTask = new MultiMap<Object, BlockId>(this) {
         public Set<BlockId> makeSet() {
            return MultiMap$class.makeSet(this);
         }

         public MultiMap addBinding(Object key, Object value) {
            return MultiMap$class.addBinding(this, key, value);
         }

         public MultiMap removeBinding(Object key, Object value) {
            return MultiMap$class.removeBinding(this, key, value);
         }

         public boolean entryExists(Object key, Function1 p) {
            return MultiMap$class.entryExists(this, key, p);
         }

         public {
            MultiMap$class.$init$(this);
         }
      };
      this.readLocksByTask = new HashMap<Object, ConcurrentHashMultiset<BlockId>>();
      this.registerTask(BlockInfo$.MODULE$.NON_TASK_WRITER());
   }
}
