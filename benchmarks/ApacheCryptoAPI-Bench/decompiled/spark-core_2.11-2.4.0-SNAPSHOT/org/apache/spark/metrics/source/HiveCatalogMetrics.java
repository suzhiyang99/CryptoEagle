package org.apache.spark.metrics.source;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.annotation.Experimental;
import scala.reflect.ScalaSignature;

@Experimental
@ScalaSignature(
   bytes = "\u0006\u0001Y<Q!\u0001\u0002\t\u00025\t!\u0003S5wK\u000e\u000bG/\u00197pO6+GO]5dg*\u00111\u0001B\u0001\u0007g>,(oY3\u000b\u0005\u00151\u0011aB7fiJL7m\u001d\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u0001\u0001C\u0001\b\u0010\u001b\u0005\u0011a!\u0002\t\u0003\u0011\u0003\t\"A\u0005%jm\u0016\u001c\u0015\r^1m_\u001elU\r\u001e:jGN\u001c2a\u0004\n\u0019!\t\u0019b#D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005\u0019\te.\u001f*fMB\u0011a\"G\u0005\u00035\t\u0011aaU8ve\u000e,\u0007\"\u0002\u000f\u0010\t\u0003i\u0012A\u0002\u001fj]&$h\bF\u0001\u000e\u0011\u001dyrB1A\u0005B\u0001\n!b]8ve\u000e,g*Y7f+\u0005\t\u0003C\u0001\u0012&\u001d\t\u00192%\u0003\u0002%)\u00051\u0001K]3eK\u001aL!AJ\u0014\u0003\rM#(/\u001b8h\u0015\t!C\u0003\u0003\u0004*\u001f\u0001\u0006I!I\u0001\fg>,(oY3OC6,\u0007\u0005C\u0004,\u001f\t\u0007I\u0011\t\u0017\u0002\u001d5,GO]5d%\u0016<\u0017n\u001d;ssV\tQ\u0006\u0005\u0002/i5\tqF\u0003\u0002\u0006a)\u0011\u0011GM\u0001\tG>$\u0017\r[1mK*\t1'A\u0002d_6L!!N\u0018\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"1qg\u0004Q\u0001\n5\nq\"\\3ue&\u001c'+Z4jgR\u0014\u0018\u0010\t\u0005\bs=\u0011\r\u0011\"\u0001;\u0003eiU\t\u0016*J\u0007~\u0003\u0016I\u0015+J)&{ejU0G\u000bR\u001b\u0005*\u0012#\u0016\u0003m\u0002\"A\f\u001f\n\u0005uz#aB\"pk:$XM\u001d\u0005\u0007\u007f=\u0001\u000b\u0011B\u001e\u000255+EKU%D?B\u000b%\u000bV%U\u0013>s5k\u0018$F)\u000eCU\t\u0012\u0011\t\u000f\u0005{!\u0019!C\u0001u\u00059R*\u0012+S\u0013\u000e{f)\u0013'F'~#\u0015jU\"P-\u0016\u0013V\t\u0012\u0005\u0007\u0007>\u0001\u000b\u0011B\u001e\u000215+EKU%D?\u001aKE*R*`\t&\u001b6i\u0014,F%\u0016#\u0005\u0005C\u0004F\u001f\t\u0007I\u0011\u0001\u001e\u0002-5+EKU%D?\u001aKE*R0D\u0003\u000eCUi\u0018%J)NCaaR\b!\u0002\u0013Y\u0014aF'F)JK5i\u0018$J\u0019\u0016{6)Q\"I\u000b~C\u0015\nV*!\u0011\u001dIuB1A\u0005\u0002i\n\u0001$T#U%&\u001bu\fS%W\u000b~\u001bE*S#O)~\u001b\u0015\t\u0014'T\u0011\u0019Yu\u0002)A\u0005w\u0005IR*\u0012+S\u0013\u000e{\u0006*\u0013,F?\u000ec\u0015*\u0012(U?\u000e\u000bE\nT*!\u0011\u001diuB1A\u0005\u0002i\n\u0011%T#U%&\u001bu\fU!S\u00032cU\tT0M\u0013N#\u0016JT$`\u0015>\u0013ulQ(V\u001dRCaaT\b!\u0002\u0013Y\u0014AI'F)JK5i\u0018)B%\u0006cE*\u0012'`\u0019&\u001bF+\u0013(H?*{%iX\"P+:#\u0006\u0005C\u0003R\u001f\u0011\u0005!+A\u0003sKN,G\u000fF\u0001T!\t\u0019B+\u0003\u0002V)\t!QK\\5u\u0011\u00159v\u0002\"\u0001Y\u0003iIgn\u0019:f[\u0016tGOR3uG\",G\rU1si&$\u0018n\u001c8t)\t\u0019\u0016\fC\u0003[-\u0002\u00071,A\u0001o!\t\u0019B,\u0003\u0002^)\t\u0019\u0011J\u001c;\t\u000b}{A\u0011\u00011\u00021%t7M]3nK:$h)\u001b7fg\u0012K7oY8wKJ,G\r\u0006\u0002TC\")!L\u0018a\u00017\")1m\u0004C\u0001I\u00061\u0012N\\2sK6,g\u000e\u001e$jY\u0016\u001c\u0015m\u00195f\u0011&$8\u000f\u0006\u0002TK\")!L\u0019a\u00017\")qm\u0004C\u0001Q\u0006A\u0012N\\2sK6,g\u000e\u001e%jm\u0016\u001cE.[3oi\u000e\u000bG\u000e\\:\u0015\u0005MK\u0007\"\u0002.g\u0001\u0004Y\u0006\"B6\u0010\t\u0003a\u0017\u0001I5oGJ,W.\u001a8u!\u0006\u0014\u0018\r\u001c7fY2K7\u000f^5oO*{'mQ8v]R$\"aU7\t\u000biS\u0007\u0019A.)\u0005=y\u0007C\u00019t\u001b\u0005\t(B\u0001:\u0007\u0003)\tgN\\8uCRLwN\\\u0005\u0003iF\u0014A\"\u0012=qKJLW.\u001a8uC2D#\u0001A8"
)
public final class HiveCatalogMetrics {
   public static void incrementParallelListingJobCount(int var0) {
      HiveCatalogMetrics$.MODULE$.incrementParallelListingJobCount(var0);
   }

   public static void incrementHiveClientCalls(int var0) {
      HiveCatalogMetrics$.MODULE$.incrementHiveClientCalls(var0);
   }

   public static void incrementFileCacheHits(int var0) {
      HiveCatalogMetrics$.MODULE$.incrementFileCacheHits(var0);
   }

   public static void incrementFilesDiscovered(int var0) {
      HiveCatalogMetrics$.MODULE$.incrementFilesDiscovered(var0);
   }

   public static void incrementFetchedPartitions(int var0) {
      HiveCatalogMetrics$.MODULE$.incrementFetchedPartitions(var0);
   }

   public static void reset() {
      HiveCatalogMetrics$.MODULE$.reset();
   }

   public static Counter METRIC_PARALLEL_LISTING_JOB_COUNT() {
      return HiveCatalogMetrics$.MODULE$.METRIC_PARALLEL_LISTING_JOB_COUNT();
   }

   public static Counter METRIC_HIVE_CLIENT_CALLS() {
      return HiveCatalogMetrics$.MODULE$.METRIC_HIVE_CLIENT_CALLS();
   }

   public static Counter METRIC_FILE_CACHE_HITS() {
      return HiveCatalogMetrics$.MODULE$.METRIC_FILE_CACHE_HITS();
   }

   public static Counter METRIC_FILES_DISCOVERED() {
      return HiveCatalogMetrics$.MODULE$.METRIC_FILES_DISCOVERED();
   }

   public static Counter METRIC_PARTITIONS_FETCHED() {
      return HiveCatalogMetrics$.MODULE$.METRIC_PARTITIONS_FETCHED();
   }

   public static MetricRegistry metricRegistry() {
      return HiveCatalogMetrics$.MODULE$.metricRegistry();
   }

   public static String sourceName() {
      return HiveCatalogMetrics$.MODULE$.sourceName();
   }
}
