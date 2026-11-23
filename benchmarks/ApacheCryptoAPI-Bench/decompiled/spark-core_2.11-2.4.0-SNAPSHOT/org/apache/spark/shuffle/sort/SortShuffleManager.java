package org.apache.spark.shuffle.sort;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.shuffle.BaseShuffleHandle;
import org.apache.spark.shuffle.BlockStoreShuffleReader;
import org.apache.spark.shuffle.BlockStoreShuffleReader$;
import org.apache.spark.shuffle.IndexShuffleBlockResolver;
import org.apache.spark.shuffle.IndexShuffleBlockResolver$;
import org.apache.spark.shuffle.ShuffleHandle;
import org.apache.spark.shuffle.ShuffleManager;
import org.apache.spark.shuffle.ShuffleReader;
import org.apache.spark.shuffle.ShuffleWriter;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}c!B\u0001\u0003\u0001\u0019a!AE*peR\u001c\u0006.\u001e4gY\u0016l\u0015M\\1hKJT!a\u0001\u0003\u0002\tM|'\u000f\u001e\u0006\u0003\u000b\u0019\tqa\u001d5vM\u001adWM\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\u0011\u0001QbE\f\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0005\u0013\t1BA\u0001\bTQV4g\r\\3NC:\fw-\u001a:\u0011\u0005aYR\"A\r\u000b\u0005i1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005qI\"a\u0002'pO\u001eLgn\u001a\u0005\t=\u0001\u0011\t\u0011)A\u0005A\u0005!1m\u001c8g\u0007\u0001\u0001\"!\t\u0012\u000e\u0003\u0019I!a\t\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007\"B\u0013\u0001\t\u00031\u0013A\u0002\u001fj]&$h\b\u0006\u0002(SA\u0011\u0001\u0006A\u0007\u0002\u0005!)a\u0004\na\u0001A!11\u0006\u0001Q\u0001\n1\n\u0011C\\;n\u001b\u0006\u00048OR8s'\",hM\u001a7f!\u0011iCG\u000e\u001c\u000e\u00039R!a\f\u0019\u0002\u0015\r|gnY;se\u0016tGO\u0003\u00022e\u0005!Q\u000f^5m\u0015\u0005\u0019\u0014\u0001\u00026bm\u0006L!!\u000e\u0018\u0003#\r{gnY;se\u0016tG\u000fS1tQ6\u000b\u0007\u000f\u0005\u0002\u000fo%\u0011\u0001h\u0004\u0002\u0004\u0013:$\bb\u0002\u001e\u0001\u0005\u0004%\teO\u0001\u0015g\",hM\u001a7f\u00052|7m\u001b*fg>dg/\u001a:\u0016\u0003q\u0002\"\u0001F\u001f\n\u0005y\"!!G%oI\u0016D8\u000b[;gM2,'\t\\8dWJ+7o\u001c7wKJDa\u0001\u0011\u0001!\u0002\u0013a\u0014!F:ik\u001a4G.\u001a\"m_\u000e\\'+Z:pYZ,'\u000f\t\u0005\u0006\u0005\u0002!\teQ\u0001\u0010e\u0016<\u0017n\u001d;feNCWO\u001a4mKV!AiU/a)\u0011)\u0005J\u0013'\u0011\u0005Q1\u0015BA$\u0005\u00055\u0019\u0006.\u001e4gY\u0016D\u0015M\u001c3mK\")\u0011*\u0011a\u0001m\u0005I1\u000f[;gM2,\u0017\n\u001a\u0005\u0006\u0017\u0006\u0003\rAN\u0001\b]VlW*\u00199t\u0011\u0015i\u0015\t1\u0001O\u0003)!W\r]3oI\u0016t7-\u001f\t\u0006C=\u000bFlX\u0005\u0003!\u001a\u0011\u0011c\u00155vM\u001adW\rR3qK:$WM\\2z!\t\u00116\u000b\u0004\u0001\u0005\u000bQ\u000b%\u0019A+\u0003\u0003-\u000b\"AV-\u0011\u000599\u0016B\u0001-\u0010\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"A\u0004.\n\u0005m{!aA!osB\u0011!+\u0018\u0003\u0006=\u0006\u0013\r!\u0016\u0002\u0002-B\u0011!\u000b\u0019\u0003\u0006C\u0006\u0013\r!\u0016\u0002\u0002\u0007\")1\r\u0001C!I\u0006Iq-\u001a;SK\u0006$WM]\u000b\u0004K*dG#\u00024n_F\u001c\b\u0003\u0002\u000bhS.L!\u0001\u001b\u0003\u0003\u001bMCWO\u001a4mKJ+\u0017\rZ3s!\t\u0011&\u000eB\u0003UE\n\u0007Q\u000b\u0005\u0002SY\u0012)\u0011M\u0019b\u0001+\")aN\u0019a\u0001\u000b\u00061\u0001.\u00198eY\u0016DQ\u0001\u001d2A\u0002Y\nab\u001d;beR\u0004\u0016M\u001d;ji&|g\u000eC\u0003sE\u0002\u0007a'\u0001\u0007f]\u0012\u0004\u0016M\u001d;ji&|g\u000eC\u0003uE\u0002\u0007Q/A\u0004d_:$X\r\u001f;\u0011\u0005\u00052\u0018BA<\u0007\u0005-!\u0016m]6D_:$X\r\u001f;\t\u000be\u0004A\u0011\t>\u0002\u0013\u001d,Go\u0016:ji\u0016\u0014X#B>\u0002\u0002\u0005\u0015Ac\u0002?\u0002\b\u0005%\u0011Q\u0002\t\u0006)u|\u00181A\u0005\u0003}\u0012\u0011Qb\u00155vM\u001adWm\u0016:ji\u0016\u0014\bc\u0001*\u0002\u0002\u0011)A\u000b\u001fb\u0001+B\u0019!+!\u0002\u0005\u000byC(\u0019A+\t\u000b9D\b\u0019A#\t\r\u0005-\u0001\u00101\u00017\u0003\u0015i\u0017\r]%e\u0011\u0015!\b\u00101\u0001v\u0011\u001d\t\t\u0002\u0001C!\u0003'\t\u0011#\u001e8sK\u001eL7\u000f^3s'\",hM\u001a7f)\u0011\t)\"a\u0007\u0011\u00079\t9\"C\u0002\u0002\u001a=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0004J\u0003\u001f\u0001\rA\u000e\u0005\b\u0003?\u0001A\u0011IA\u0011\u0003\u0011\u0019Ho\u001c9\u0015\u0005\u0005\r\u0002c\u0001\b\u0002&%\u0019\u0011qE\b\u0003\tUs\u0017\u000e^\u0004\t\u0003W\u0011\u0001\u0012\u0001\u0004\u0002.\u0005\u00112k\u001c:u'\",hM\u001a7f\u001b\u0006t\u0017mZ3s!\rA\u0013q\u0006\u0004\b\u0003\tA\tABA\u0019'\u0011\ty#D\f\t\u000f\u0015\ny\u0003\"\u0001\u00026Q\u0011\u0011Q\u0006\u0005\u000b\u0003s\tyC1A\u0005\u0002\u0005m\u0012!M'B1~\u001b\u0006*\u0016$G\u0019\u0016{v*\u0016+Q+R{\u0006+\u0011*U\u0013RKuJT*`\r>\u0013vlU#S\u0013\u0006c\u0015JW#E?6{E)R\u000b\u0002m!A\u0011qHA\u0018A\u0003%a'\u0001\u001aN\u0003b{6\u000bS+G\r2+ulT+U!V#v\fU!S)&#\u0016j\u0014(T?\u001a{%kX*F%&\u000bE*\u0013.F\t~ku\nR#!\u0011!\t\u0019%a\f\u0005\u0002\u0005\u0015\u0013aF2b]V\u001bXmU3sS\u0006d\u0017N_3e'\",hM\u001a7f)\u0011\t)\"a\u0012\t\u000f5\u000b\t\u00051\u0001\u0002JAB\u00111JA(\u0003+\nY\u0006\u0005\u0005\"\u001f\u00065\u00131KA-!\r\u0011\u0016q\n\u0003\f\u0003#\n9%!A\u0001\u0002\u000b\u0005QKA\u0002`IU\u00022AUA+\t-\t9&a\u0012\u0002\u0002\u0003\u0005)\u0011A+\u0003\u0007}#c\u0007E\u0002S\u00037\"1\"!\u0018\u0002H\u0005\u0005\t\u0011!B\u0001+\n\u0019q\fJ\u001c"
)
public class SortShuffleManager implements ShuffleManager, Logging {
   private final SparkConf conf;
   private final ConcurrentHashMap<Object, Object> numMapsForShuffle;
   private final IndexShuffleBlockResolver shuffleBlockResolver;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean canUseSerializedShuffle(ShuffleDependency<?, ?, ?> var0) {
      return SortShuffleManager$.MODULE$.canUseSerializedShuffle(var0);
   }

   public static int MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE() {
      return SortShuffleManager$.MODULE$.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE();
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

   public IndexShuffleBlockResolver shuffleBlockResolver() {
      return this.shuffleBlockResolver;
   }

   public <K, V, C> ShuffleHandle registerShuffle(int shuffleId, int numMaps, ShuffleDependency<K, V, C> dependency) {
      return (ShuffleHandle)(SortShuffleWriter$.MODULE$.shouldBypassMergeSort(this.conf, dependency) ? new BypassMergeSortShuffleHandle(shuffleId, numMaps, dependency) : (SortShuffleManager$.MODULE$.canUseSerializedShuffle(dependency) ? new SerializedShuffleHandle(shuffleId, numMaps, dependency) : new BaseShuffleHandle(shuffleId, numMaps, dependency)));
   }

   public <K, C> ShuffleReader<K, C> getReader(ShuffleHandle handle, int startPartition, int endPartition, TaskContext context) {
      return new BlockStoreShuffleReader<K, C>((BaseShuffleHandle)handle, startPartition, endPartition, context, BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$5(), BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$6(), BlockStoreShuffleReader$.MODULE$.$lessinit$greater$default$7());
   }

   public <K, V> ShuffleWriter<K, V> getWriter(ShuffleHandle handle, int mapId, TaskContext context) {
      this.numMapsForShuffle.putIfAbsent(BoxesRunTime.boxToInteger(handle.shuffleId()), BoxesRunTime.boxToInteger(((BaseShuffleHandle)handle).numMaps()));
      SparkEnv env = SparkEnv$.MODULE$.get();
      Object var7;
      if (handle instanceof SerializedShuffleHandle) {
         SerializedShuffleHandle var6 = (SerializedShuffleHandle)handle;
         var7 = new UnsafeShuffleWriter(env.blockManager(), this.shuffleBlockResolver(), context.taskMemoryManager(), var6, mapId, context, env.conf());
      } else if (handle instanceof BypassMergeSortShuffleHandle) {
         BypassMergeSortShuffleHandle var8 = (BypassMergeSortShuffleHandle)handle;
         var7 = new BypassMergeSortShuffleWriter(env.blockManager(), this.shuffleBlockResolver(), var8, mapId, context, env.conf());
      } else {
         if (!(handle instanceof BaseShuffleHandle)) {
            throw new MatchError(handle);
         }

         BaseShuffleHandle var9 = (BaseShuffleHandle)handle;
         var7 = new SortShuffleWriter(this.shuffleBlockResolver(), var9, mapId, context);
      }

      return (ShuffleWriter<K, V>)var7;
   }

   public boolean unregisterShuffle(int shuffleId) {
      Option$.MODULE$.apply(this.numMapsForShuffle.remove(BoxesRunTime.boxToInteger(shuffleId))).foreach(new Serializable(this, shuffleId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SortShuffleManager $outer;
         public final int shuffleId$1;

         public final void apply(int numMaps) {
            this.apply$mcVI$sp(numMaps);
         }

         public void apply$mcVI$sp(int numMaps) {
            RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numMaps).foreach$mVc$sp(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(int mapId) {
                  this.apply$mcVI$sp(mapId);
               }

               public void apply$mcVI$sp(int mapId) {
                  this.$outer.org$apache$spark$shuffle$sort$SortShuffleManager$$anonfun$$$outer().shuffleBlockResolver().removeDataByMap(this.$outer.shuffleId$1, mapId);
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
         public SortShuffleManager org$apache$spark$shuffle$sort$SortShuffleManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.shuffleId$1 = shuffleId$1;
            }
         }
      });
      return true;
   }

   public void stop() {
      this.shuffleBlockResolver().stop();
   }

   public SortShuffleManager(SparkConf conf) {
      this.conf = conf;
      Logging$class.$init$(this);
      if (!conf.getBoolean("spark.shuffle.spill", true)) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "spark.shuffle.spill was set to false, but this configuration is ignored as of Spark 1.6+. Shuffle will continue to spill to disk when necessary.";
            }
         });
      }

      this.numMapsForShuffle = new ConcurrentHashMap();
      this.shuffleBlockResolver = new IndexShuffleBlockResolver(conf, IndexShuffleBlockResolver$.MODULE$.$lessinit$greater$default$2());
   }
}
