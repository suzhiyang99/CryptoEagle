package org.apache.spark.shuffle.sort;

import java.io.File;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.scheduler.MapStatus$;
import org.apache.spark.shuffle.BaseShuffleHandle;
import org.apache.spark.shuffle.IndexShuffleBlockResolver;
import org.apache.spark.shuffle.IndexShuffleBlockResolver$;
import org.apache.spark.shuffle.ShuffleWriter;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.ShuffleBlockId;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.collection.ExternalSorter;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}f!B\u0001\u0003\u0001\u0019a!!E*peR\u001c\u0006.\u001e4gY\u0016<&/\u001b;fe*\u00111\u0001B\u0001\u0005g>\u0014HO\u0003\u0002\u0006\r\u000591\u000f[;gM2,'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0016\t5!\"%N\n\u0004\u00019!\u0003\u0003B\b\u0011%\u0005j\u0011\u0001B\u0005\u0003#\u0011\u0011Qb\u00155vM\u001adWm\u0016:ji\u0016\u0014\bCA\n\u0015\u0019\u0001!Q!\u0006\u0001C\u0002]\u0011\u0011aS\u0002\u0001#\tAb\u0004\u0005\u0002\u001a95\t!DC\u0001\u001c\u0003\u0015\u00198-\u00197b\u0013\ti\"DA\u0004O_RD\u0017N\\4\u0011\u0005ey\u0012B\u0001\u0011\u001b\u0005\r\te.\u001f\t\u0003'\t\"Qa\t\u0001C\u0002]\u0011\u0011A\u0016\t\u0003K!j\u0011A\n\u0006\u0003O\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003S\u0019\u0012q\u0001T8hO&tw\r\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003-\u0003Q\u0019\b.\u001e4gY\u0016\u0014En\\2l%\u0016\u001cx\u000e\u001c<feB\u0011q\"L\u0005\u0003]\u0011\u0011\u0011$\u00138eKb\u001c\u0006.\u001e4gY\u0016\u0014En\\2l%\u0016\u001cx\u000e\u001c<fe\"A\u0001\u0007\u0001B\u0001B\u0003%\u0011'\u0001\u0004iC:$G.\u001a\t\u0006\u001fI\u0012\u0012\u0005N\u0005\u0003g\u0011\u0011\u0011CQ1tKNCWO\u001a4mK\"\u000bg\u000e\u001a7f!\t\u0019R\u0007B\u00037\u0001\t\u0007qCA\u0001D\u0011!A\u0004A!A!\u0002\u0013I\u0014!B7ba&#\u0007CA\r;\u0013\tY$DA\u0002J]RD\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006IAP\u0001\bG>tG/\u001a=u!\ty\u0004)D\u0001\u0007\u0013\t\teAA\u0006UCN\\7i\u001c8uKb$\b\"B\"\u0001\t\u0003!\u0015A\u0002\u001fj]&$h\bF\u0003F\u000f\"K%\nE\u0003G\u0001I\tC'D\u0001\u0003\u0011\u0015Y#\t1\u0001-\u0011\u0015\u0001$\t1\u00012\u0011\u0015A$\t1\u0001:\u0011\u0015i$\t1\u0001?\u0011\u001da\u0005A1A\u0005\n5\u000b1\u0001Z3q+\u0005q\u0005#B P%\u0005\"\u0014B\u0001)\u0007\u0005E\u0019\u0006.\u001e4gY\u0016$U\r]3oI\u0016t7-\u001f\u0005\u0007%\u0002\u0001\u000b\u0011\u0002(\u0002\t\u0011,\u0007\u000f\t\u0005\b)\u0002\u0011\r\u0011\"\u0003V\u00031\u0011Gn\\2l\u001b\u0006t\u0017mZ3s+\u00051\u0006CA,[\u001b\u0005A&BA-\u0007\u0003\u001d\u0019Ho\u001c:bO\u0016L!a\u0017-\u0003\u0019\tcwnY6NC:\fw-\u001a:\t\ru\u0003\u0001\u0015!\u0003W\u00035\u0011Gn\\2l\u001b\u0006t\u0017mZ3sA!9q\f\u0001a\u0001\n\u0013\u0001\u0017AB:peR,'/F\u0001ba\t\u00117\u000eE\u0003dQJ\t#.D\u0001e\u0015\t)g-\u0001\u0006d_2dWm\u0019;j_:T!a\u001a\u0004\u0002\tU$\u0018\u000e\\\u0005\u0003S\u0012\u0014a\"\u0012=uKJt\u0017\r\\*peR,'\u000f\u0005\u0002\u0014W\u0012IA.\\A\u0001\u0002\u0003\u0015\ta\u0006\u0002\u0004?\u0012\n\u0004B\u00028\u0001A\u0003&q.A\u0004t_J$XM\u001d\u00111\u0005A\u0014\b#B2i%\u0005\n\bCA\ns\t%aW.!A\u0001\u0002\u000b\u0005q\u0003C\u0004u\u0001\u0001\u0007I\u0011B;\u0002\u0015M|'\u000f^3s?\u0012*\u0017\u000f\u0006\u0002wsB\u0011\u0011d^\u0005\u0003qj\u0011A!\u00168ji\"9!p]A\u0001\u0002\u0004Y\u0018a\u0001=%cA\u0012AP \t\u0006G\"\u0014\u0012% \t\u0003'y$\u0011\u0002\\7\u0002\u0002\u0003\u0005)\u0011A\f\t\u0013\u0005\u0005\u0001\u00011A\u0005\n\u0005\r\u0011\u0001C:u_B\u0004\u0018N\\4\u0016\u0005\u0005\u0015\u0001cA\r\u0002\b%\u0019\u0011\u0011\u0002\u000e\u0003\u000f\t{w\u000e\\3b]\"I\u0011Q\u0002\u0001A\u0002\u0013%\u0011qB\u0001\rgR|\u0007\u000f]5oO~#S-\u001d\u000b\u0004m\u0006E\u0001\"\u0003>\u0002\f\u0005\u0005\t\u0019AA\u0003\u0011!\t)\u0002\u0001Q!\n\u0005\u0015\u0011!C:u_B\u0004\u0018N\\4!\u0011%\tI\u0002\u0001a\u0001\n\u0013\tY\"A\u0005nCB\u001cF/\u0019;vgV\u0011\u0011Q\u0004\t\u0005\u0003?\t)#\u0004\u0002\u0002\")\u0019\u00111\u0005\u0004\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014\u0018\u0002BA\u0014\u0003C\u0011\u0011\"T1q'R\fG/^:\t\u0013\u0005-\u0002\u00011A\u0005\n\u00055\u0012!D7baN#\u0018\r^;t?\u0012*\u0017\u000fF\u0002w\u0003_A\u0011B_A\u0015\u0003\u0003\u0005\r!!\b\t\u0011\u0005M\u0002\u0001)Q\u0005\u0003;\t!\"\\1q'R\fG/^:!\u0011%\t9\u0004\u0001b\u0001\n\u0013\tI$\u0001\u0007xe&$X-T3ue&\u001c7/\u0006\u0002\u0002<A!\u0011QHA\"\u001b\t\tyDC\u0002\u0002B\u0019\t\u0001\"\u001a=fGV$xN]\u0005\u0005\u0003\u000b\nyDA\nTQV4g\r\\3Xe&$X-T3ue&\u001c7\u000f\u0003\u0005\u0002J\u0001\u0001\u000b\u0011BA\u001e\u000359(/\u001b;f\u001b\u0016$(/[2tA!9\u0011Q\n\u0001\u0005B\u0005=\u0013!B<sSR,Gc\u0001<\u0002R!A\u00111KA&\u0001\u0004\t)&A\u0004sK\u000e|'\u000fZ:\u0011\r\u0005]\u0013qMA7\u001d\u0011\tI&a\u0019\u000f\t\u0005m\u0013\u0011M\u0007\u0003\u0003;R1!a\u0018\u0017\u0003\u0019a$o\\8u}%\t1$C\u0002\u0002fi\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002j\u0005-$\u0001C%uKJ\fGo\u001c:\u000b\u0007\u0005\u0015$\u0004E\u0003\u001a\u0003_\u0012\u0012%C\u0002\u0002ri\u0011\u0001\u0002\u0015:pIV\u001cGO\r\u0005\b\u0003k\u0002A\u0011IA<\u0003\u0011\u0019Ho\u001c9\u0015\t\u0005e\u0014q\u0010\t\u00063\u0005m\u0014QD\u0005\u0004\u0003{R\"AB(qi&|g\u000e\u0003\u0005\u0002\u0002\u0006M\u0004\u0019AA\u0003\u0003\u001d\u0019XoY2fgN<\u0001\"!\"\u0003\u0011\u00031\u0011qQ\u0001\u0012'>\u0014Ho\u00155vM\u001adWm\u0016:ji\u0016\u0014\bc\u0001$\u0002\n\u001a9\u0011A\u0001E\u0001\r\u0005-5\u0003BAE\u0003\u001b\u00032!GAH\u0013\r\t\tJ\u0007\u0002\u0007\u0003:L(+\u001a4\t\u000f\r\u000bI\t\"\u0001\u0002\u0016R\u0011\u0011q\u0011\u0005\t\u00033\u000bI\t\"\u0001\u0002\u001c\u0006)2\u000f[8vY\u0012\u0014\u0015\u0010]1tg6+'oZ3T_J$HCBA\u0003\u0003;\u000b9\u000b\u0003\u0005\u0002 \u0006]\u0005\u0019AAQ\u0003\u0011\u0019wN\u001c4\u0011\u0007}\n\u0019+C\u0002\u0002&\u001a\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000f1\u000b9\n1\u0001\u0002*BB\u00111VAX\u0003k\u000bY\f\u0005\u0005@\u001f\u00065\u00161WA]!\r\u0019\u0012q\u0016\u0003\f\u0003c\u000b9+!A\u0001\u0002\u000b\u0005qCA\u0002`II\u00022aEA[\t-\t9,a*\u0002\u0002\u0003\u0005)\u0011A\f\u0003\u0007}#3\u0007E\u0002\u0014\u0003w#1\"!0\u0002(\u0006\u0005\t\u0011!B\u0001/\t\u0019q\f\n\u001b"
)
public class SortShuffleWriter<K, V, C> extends ShuffleWriter<K, V> implements Logging {
   private final IndexShuffleBlockResolver shuffleBlockResolver;
   private final int mapId;
   private final TaskContext context;
   private final ShuffleDependency<K, V, C> dep;
   private final BlockManager blockManager;
   private ExternalSorter<K, V, ?> sorter;
   private boolean stopping;
   private MapStatus mapStatus;
   private final ShuffleWriteMetrics writeMetrics;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean shouldBypassMergeSort(SparkConf var0, ShuffleDependency<?, ?, ?> var1) {
      return SortShuffleWriter$.MODULE$.shouldBypassMergeSort(var0, var1);
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

   private ShuffleDependency<K, V, C> dep() {
      return this.dep;
   }

   private BlockManager blockManager() {
      return this.blockManager;
   }

   private ExternalSorter<K, V, ?> sorter() {
      return this.sorter;
   }

   private void sorter_$eq(ExternalSorter<K, V, ?> x$1) {
      this.sorter = x$1;
   }

   private boolean stopping() {
      return this.stopping;
   }

   private void stopping_$eq(boolean x$1) {
      this.stopping = x$1;
   }

   private MapStatus mapStatus() {
      return this.mapStatus;
   }

   private void mapStatus_$eq(MapStatus x$1) {
      this.mapStatus = x$1;
   }

   private ShuffleWriteMetrics writeMetrics() {
      return this.writeMetrics;
   }

   public void write(Iterator<Product2<K, V>> records) {
      ExternalSorter var10001;
      if (this.dep().mapSideCombine()) {
         Predef$.MODULE$.require(this.dep().aggregator().isDefined(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Map-side combine without Aggregator specified!";
            }
         });
         var10001 = new ExternalSorter(this.context, this.dep().aggregator(), new Some(this.dep().partitioner()), this.dep().keyOrdering(), this.dep().serializer());
      } else {
         var10001 = new ExternalSorter(this.context, None$.MODULE$, new Some(this.dep().partitioner()), None$.MODULE$, this.dep().serializer());
      }

      this.sorter_$eq(var10001);
      this.sorter().insertAll(records);
      File output = this.shuffleBlockResolver.getDataFile(this.dep().shuffleId(), this.mapId);
      File tmp = Utils$.MODULE$.tempFileWith(output);

      try {
         ShuffleBlockId blockId = new ShuffleBlockId(this.dep().shuffleId(), this.mapId, IndexShuffleBlockResolver$.MODULE$.NOOP_REDUCE_ID());
         long[] partitionLengths = this.sorter().writePartitionedFile(blockId, tmp);
         this.shuffleBlockResolver.writeIndexFileAndCommit(this.dep().shuffleId(), this.mapId, partitionLengths, tmp);
         this.mapStatus_$eq(MapStatus$.MODULE$.apply(this.blockManager().shuffleServerId(), partitionLengths));
      } finally {
         if (tmp.exists() && !tmp.delete()) {
            this.logError(new Serializable(this, tmp) {
               public static final long serialVersionUID = 0L;
               private final File tmp$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error while deleting temp file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.tmp$1.getAbsolutePath()}));
               }

               public {
                  this.tmp$1 = tmp$1;
               }
            });
         }

      }

   }

   public Option<MapStatus> stop(boolean success) {
      Option var10000;
      try {
         if (!this.stopping()) {
            this.stopping_$eq(true);
            if (success) {
               var10000 = Option$.MODULE$.apply(this.mapStatus());
               return var10000;
            }

            var10000 = None$.MODULE$;
            return var10000;
         }

         var10000 = None$.MODULE$;
      } finally {
         if (this.sorter() != null) {
            long startTime = System.nanoTime();
            this.sorter().stop();
            this.writeMetrics().incWriteTime(System.nanoTime() - startTime);
            this.sorter_$eq((ExternalSorter)null);
         }

      }

      return var10000;
   }

   public SortShuffleWriter(IndexShuffleBlockResolver shuffleBlockResolver, BaseShuffleHandle<K, V, C> handle, int mapId, TaskContext context) {
      this.shuffleBlockResolver = shuffleBlockResolver;
      this.mapId = mapId;
      this.context = context;
      Logging$class.$init$(this);
      this.dep = handle.dependency();
      this.blockManager = SparkEnv$.MODULE$.get().blockManager();
      this.sorter = null;
      this.stopping = false;
      this.mapStatus = null;
      this.writeMetrics = context.taskMetrics().shuffleWriteMetrics();
   }
}
