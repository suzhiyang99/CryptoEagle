package org.apache.spark.shuffle;

import java.io.InputStream;
import org.apache.spark.Aggregator;
import org.apache.spark.InterruptibleIterator;
import org.apache.spark.MapOutputTracker;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.TempShuffleReadMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.package$;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.ShuffleBlockFetcherIterator;
import org.apache.spark.util.CompletionIterator;
import org.apache.spark.util.CompletionIterator$;
import org.apache.spark.util.collection.ExternalSorter;
import org.apache.spark.util.collection.ExternalSorter$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=c!B\u0001\u0003\u0001\u0011Q!a\u0006\"m_\u000e\\7\u000b^8sKNCWO\u001a4mKJ+\u0017\rZ3s\u0015\t\u0019A!A\u0004tQV4g\r\\3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,2a\u0003\r$'\u0011\u0001ABE\u0013\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\u0011\u0019BC\u0006\u0012\u000e\u0003\tI!!\u0006\u0002\u0003\u001bMCWO\u001a4mKJ+\u0017\rZ3s!\t9\u0002\u0004\u0004\u0001\u0005\u000be\u0001!\u0019A\u000e\u0003\u0003-\u001b\u0001!\u0005\u0002\u001d?A\u0011Q\"H\u0005\u0003=9\u0011qAT8uQ&tw\r\u0005\u0002\u000eA%\u0011\u0011E\u0004\u0002\u0004\u0003:L\bCA\f$\t\u0015!\u0003A1\u0001\u001c\u0005\u0005\u0019\u0005C\u0001\u0014*\u001b\u00059#B\u0001\u0015\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0016(\u0005\u001daunZ4j]\u001eD\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006I!L\u0001\u0007Q\u0006tG\r\\31\u00059\u0012\u0004#B\n0-E\u0012\u0013B\u0001\u0019\u0003\u0005E\u0011\u0015m]3TQV4g\r\\3IC:$G.\u001a\t\u0003/I\"\u0011bM\u0016\u0002\u0002\u0003\u0005)\u0011A\u000e\u0003\u0007}#\u0013\u0007\u0003\u00056\u0001\t\u0005\t\u0015!\u00037\u00039\u0019H/\u0019:u!\u0006\u0014H/\u001b;j_:\u0004\"!D\u001c\n\u0005ar!aA%oi\"A!\b\u0001B\u0001B\u0003%a'\u0001\u0007f]\u0012\u0004\u0016M\u001d;ji&|g\u000e\u0003\u0005=\u0001\t\u0005\t\u0015!\u0003>\u0003\u001d\u0019wN\u001c;fqR\u0004\"AP \u000e\u0003\u0011I!\u0001\u0011\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e\u0005\t\u0005\u0002\u0011\t\u0011)A\u0005\u0007\u0006\t2/\u001a:jC2L'0\u001a:NC:\fw-\u001a:\u0011\u0005\u0011;U\"A#\u000b\u0005\u0019#\u0011AC:fe&\fG.\u001b>fe&\u0011\u0001*\u0012\u0002\u0012'\u0016\u0014\u0018.\u00197ju\u0016\u0014X*\u00198bO\u0016\u0014\b\u0002\u0003&\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002\u0019\tdwnY6NC:\fw-\u001a:\u0011\u00051{U\"A'\u000b\u00059#\u0011aB:u_J\fw-Z\u0005\u0003!6\u0013AB\u00117pG.l\u0015M\\1hKJD\u0001B\u0015\u0001\u0003\u0002\u0003\u0006IaU\u0001\u0011[\u0006\u0004x*\u001e;qkR$&/Y2lKJ\u0004\"A\u0010+\n\u0005U#!\u0001E'ba>+H\u000f];u)J\f7m[3s\u0011\u00159\u0006\u0001\"\u0001Y\u0003\u0019a\u0014N\\5u}QA\u0011LW0aC\n\u001cG\r\u0005\u0003\u0014\u0001Y\u0011\u0003\"\u0002\u0017W\u0001\u0004Y\u0006G\u0001/_!\u0015\u0019rFF/#!\t9b\fB\u000545\u0006\u0005\t\u0011!B\u00017!)QG\u0016a\u0001m!)!H\u0016a\u0001m!)AH\u0016a\u0001{!9!I\u0016I\u0001\u0002\u0004\u0019\u0005b\u0002&W!\u0003\u0005\ra\u0013\u0005\b%Z\u0003\n\u00111\u0001T\u0011\u001d1\u0007A1A\u0005\n\u001d\f1\u0001Z3q+\u0005A\u0007GA5n!\u0015q$N\u00067#\u0013\tYGAA\tTQV4g\r\\3EKB,g\u000eZ3oGf\u0004\"aF7\u0005\u0013MZ\u0013\u0011!A\u0001\u0006\u0003Y\u0002BB8\u0001A\u0003%\u0001.\u0001\u0003eKB\u0004\u0003\"B9\u0001\t\u0003\u0012\u0018\u0001\u0002:fC\u0012$\u0012a\u001d\t\u0004ir|hBA;{\u001d\t1\u00180D\u0001x\u0015\tA($\u0001\u0004=e>|GOP\u0005\u0002\u001f%\u00111PD\u0001\ba\u0006\u001c7.Y4f\u0013\tihP\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\tYh\u0002E\u0003\u000e\u0003\u00031\"%C\u0002\u0002\u00049\u0011\u0001\u0002\u0015:pIV\u001cGOM\u0004\u000b\u0003\u000f\u0011\u0011\u0011!E\u0001\t\u0005%\u0011a\u0006\"m_\u000e\\7\u000b^8sKNCWO\u001a4mKJ+\u0017\rZ3s!\r\u0019\u00121\u0002\u0004\n\u0003\t\t\t\u0011#\u0001\u0005\u0003\u001b\u00192!a\u0003\r\u0011\u001d9\u00161\u0002C\u0001\u0003#!\"!!\u0003\t\u0015\u0005U\u00111BI\u0001\n\u0003\t9\"A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%N\u000b\u0007\u00033\ty#!\r\u0016\u0005\u0005m!fA\"\u0002\u001e-\u0012\u0011q\u0004\t\u0005\u0003C\tY#\u0004\u0002\u0002$)!\u0011QEA\u0014\u0003%)hn\u00195fG.,GMC\u0002\u0002*9\t!\"\u00198o_R\fG/[8o\u0013\u0011\ti#a\t\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004\u001a\u0003'\u0011\ra\u0007\u0003\u0007I\u0005M!\u0019A\u000e\t\u0015\u0005U\u00121BI\u0001\n\u0003\t9$A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HEN\u000b\u0007\u0003s\ti$a\u0010\u0016\u0005\u0005m\"fA&\u0002\u001e\u00111\u0011$a\rC\u0002m!a\u0001JA\u001a\u0005\u0004Y\u0002BCA\"\u0003\u0017\t\n\u0011\"\u0001\u0002F\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uI]*b!a\u0012\u0002L\u00055SCAA%U\r\u0019\u0016Q\u0004\u0003\u00073\u0005\u0005#\u0019A\u000e\u0005\r\u0011\n\tE1\u0001\u001c\u0001"
)
public class BlockStoreShuffleReader<K, C> implements ShuffleReader<K, C>, Logging {
   private final BaseShuffleHandle<K, ?, C> handle;
   private final int startPartition;
   private final int endPartition;
   public final TaskContext org$apache$spark$shuffle$BlockStoreShuffleReader$$context;
   public final SerializerManager org$apache$spark$shuffle$BlockStoreShuffleReader$$serializerManager;
   private final BlockManager blockManager;
   private final MapOutputTracker mapOutputTracker;
   private final ShuffleDependency<K, ?, C> dep;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static <K, C> MapOutputTracker $lessinit$greater$default$7() {
      return BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$7();
   }

   public static <K, C> BlockManager $lessinit$greater$default$6() {
      return BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$6();
   }

   public static <K, C> SerializerManager $lessinit$greater$default$5() {
      return BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$5();
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

   private ShuffleDependency<K, ?, C> dep() {
      return this.dep;
   }

   public Iterator<Product2<K, C>> read() {
      ShuffleBlockFetcherIterator wrappedStreams = new ShuffleBlockFetcherIterator(this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context, this.blockManager.shuffleClient(), this.blockManager, this.mapOutputTracker.getMapSizesByExecutorId(this.handle.shuffleId(), this.startPartition, this.endPartition), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockStoreShuffleReader $outer;

         public final InputStream apply(BlockId blockId, InputStream s) {
            return this.$outer.org$apache$spark$shuffle$BlockStoreShuffleReader$$serializerManager.wrapStream(blockId, s);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, SparkEnv$.MODULE$.get().conf().getSizeAsMb("spark.reducer.maxSizeInFlight", "48m") * 1024L * 1024L, SparkEnv$.MODULE$.get().conf().getInt("spark.reducer.maxReqsInFlight", Integer.MAX_VALUE), BoxesRunTime.unboxToInt(SparkEnv$.MODULE$.get().conf().get(package$.MODULE$.REDUCER_MAX_BLOCKS_IN_FLIGHT_PER_ADDRESS())), BoxesRunTime.unboxToLong(SparkEnv$.MODULE$.get().conf().get(package$.MODULE$.MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM())), SparkEnv$.MODULE$.get().conf().getBoolean("spark.shuffle.detectCorrupt", true));
      SerializerInstance serializerInstance = this.dep().serializer().newInstance();
      Iterator recordIter = wrappedStreams.flatMap(new Serializable(this, serializerInstance) {
         public static final long serialVersionUID = 0L;
         private final SerializerInstance serializerInstance$1;

         public final Iterator<Tuple2<Object, Object>> apply(Tuple2<BlockId, InputStream> x0$1) {
            if (x0$1 != null) {
               InputStream wrappedStream = (InputStream)x0$1._2();
               Iterator var4 = this.serializerInstance$1.deserializeStream(wrappedStream).asKeyValueIterator();
               return var4;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.serializerInstance$1 = serializerInstance$1;
         }
      });
      TempShuffleReadMetrics readMetrics = this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context.taskMetrics().createTempShuffleReadMetrics();
      CompletionIterator metricIter = CompletionIterator$.MODULE$.apply(recordIter.map(new Serializable(this, readMetrics) {
         public static final long serialVersionUID = 0L;
         private final TempShuffleReadMetrics readMetrics$1;

         public final Tuple2<Object, Object> apply(Tuple2<Object, Object> record) {
            this.readMetrics$1.incRecordsRead(1L);
            return record;
         }

         public {
            this.readMetrics$1 = readMetrics$1;
         }
      }), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockStoreShuffleReader $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$shuffle$BlockStoreShuffleReader$$context.taskMetrics().mergeShuffleReadMetrics();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      InterruptibleIterator interruptibleIter = new InterruptibleIterator(this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context, metricIter);
      Object var10000;
      if (this.dep().aggregator().isDefined()) {
         var10000 = this.dep().mapSideCombine() ? ((Aggregator)this.dep().aggregator().get()).combineCombinersByKey(interruptibleIter, this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context) : ((Aggregator)this.dep().aggregator().get()).combineValuesByKey(interruptibleIter, this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context);
      } else {
         Predef$.MODULE$.require(!this.dep().mapSideCombine(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Map-side combine without Aggregator specified!";
            }
         });
         var10000 = interruptibleIter;
      }

      Iterator aggregatedIter = (Iterator)var10000;
      Option var10 = this.dep().keyOrdering();
      Object var14;
      if (var10 instanceof Some) {
         Some var11 = (Some)var10;
         Ordering keyOrd = (Ordering)var11.x();
         if (keyOrd != null) {
            TaskContext x$1 = this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context;
            Some x$2 = new Some(keyOrd);
            Serializer x$3 = this.dep().serializer();
            None$ x$4 = ExternalSorter$.MODULE$.$lessinit$greater$default$2();
            Option x$5 = ExternalSorter$.MODULE$.$lessinit$greater$default$3();
            ExternalSorter sorter = new ExternalSorter(x$1, x$4, x$5, x$2, x$3);
            sorter.insertAll(aggregatedIter);
            this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context.taskMetrics().incMemoryBytesSpilled(sorter.memoryBytesSpilled());
            this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context.taskMetrics().incDiskBytesSpilled(sorter.diskBytesSpilled());
            this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context.taskMetrics().incPeakExecutionMemory(sorter.peakMemoryUsedBytes());
            var14 = CompletionIterator$.MODULE$.apply(sorter.iterator(), new Serializable(this, sorter) {
               public static final long serialVersionUID = 0L;
               private final ExternalSorter sorter$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.sorter$1.stop();
               }

               public {
                  this.sorter$1 = sorter$1;
               }
            });
            return (Iterator<Product2<K, C>>)var14;
         }
      }

      if (!None$.MODULE$.equals(var10)) {
         throw new MatchError(var10);
      } else {
         var14 = aggregatedIter;
         return (Iterator<Product2<K, C>>)var14;
      }
   }

   public BlockStoreShuffleReader(BaseShuffleHandle<K, ?, C> handle, int startPartition, int endPartition, TaskContext context, SerializerManager serializerManager, BlockManager blockManager, MapOutputTracker mapOutputTracker) {
      this.handle = handle;
      this.startPartition = startPartition;
      this.endPartition = endPartition;
      this.org$apache$spark$shuffle$BlockStoreShuffleReader$$context = context;
      this.org$apache$spark$shuffle$BlockStoreShuffleReader$$serializerManager = serializerManager;
      this.blockManager = blockManager;
      this.mapOutputTracker = mapOutputTracker;
      Logging$class.$init$(this);
      this.dep = handle.dependency();
   }
}
