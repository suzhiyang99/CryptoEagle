package org.apache.spark.metrics.source;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.annotation.Experimental;

@Experimental
public final class HiveCatalogMetrics$ implements Source {
   public static final HiveCatalogMetrics$ MODULE$;
   private final String sourceName;
   private final MetricRegistry metricRegistry;
   private final Counter METRIC_PARTITIONS_FETCHED;
   private final Counter METRIC_FILES_DISCOVERED;
   private final Counter METRIC_FILE_CACHE_HITS;
   private final Counter METRIC_HIVE_CLIENT_CALLS;
   private final Counter METRIC_PARALLEL_LISTING_JOB_COUNT;

   static {
      new HiveCatalogMetrics$();
   }

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public Counter METRIC_PARTITIONS_FETCHED() {
      return this.METRIC_PARTITIONS_FETCHED;
   }

   public Counter METRIC_FILES_DISCOVERED() {
      return this.METRIC_FILES_DISCOVERED;
   }

   public Counter METRIC_FILE_CACHE_HITS() {
      return this.METRIC_FILE_CACHE_HITS;
   }

   public Counter METRIC_HIVE_CLIENT_CALLS() {
      return this.METRIC_HIVE_CLIENT_CALLS;
   }

   public Counter METRIC_PARALLEL_LISTING_JOB_COUNT() {
      return this.METRIC_PARALLEL_LISTING_JOB_COUNT;
   }

   public void reset() {
      this.METRIC_PARTITIONS_FETCHED().dec(this.METRIC_PARTITIONS_FETCHED().getCount());
      this.METRIC_FILES_DISCOVERED().dec(this.METRIC_FILES_DISCOVERED().getCount());
      this.METRIC_FILE_CACHE_HITS().dec(this.METRIC_FILE_CACHE_HITS().getCount());
      this.METRIC_HIVE_CLIENT_CALLS().dec(this.METRIC_HIVE_CLIENT_CALLS().getCount());
      this.METRIC_PARALLEL_LISTING_JOB_COUNT().dec(this.METRIC_PARALLEL_LISTING_JOB_COUNT().getCount());
   }

   public void incrementFetchedPartitions(int n) {
      this.METRIC_PARTITIONS_FETCHED().inc((long)n);
   }

   public void incrementFilesDiscovered(int n) {
      this.METRIC_FILES_DISCOVERED().inc((long)n);
   }

   public void incrementFileCacheHits(int n) {
      this.METRIC_FILE_CACHE_HITS().inc((long)n);
   }

   public void incrementHiveClientCalls(int n) {
      this.METRIC_HIVE_CLIENT_CALLS().inc((long)n);
   }

   public void incrementParallelListingJobCount(int n) {
      this.METRIC_PARALLEL_LISTING_JOB_COUNT().inc((long)n);
   }

   private HiveCatalogMetrics$() {
      MODULE$ = this;
      this.sourceName = "HiveExternalCatalog";
      this.metricRegistry = new MetricRegistry();
      this.METRIC_PARTITIONS_FETCHED = this.metricRegistry().counter(MetricRegistry.name("partitionsFetched", new String[0]));
      this.METRIC_FILES_DISCOVERED = this.metricRegistry().counter(MetricRegistry.name("filesDiscovered", new String[0]));
      this.METRIC_FILE_CACHE_HITS = this.metricRegistry().counter(MetricRegistry.name("fileCacheHits", new String[0]));
      this.METRIC_HIVE_CLIENT_CALLS = this.metricRegistry().counter(MetricRegistry.name("hiveClientCalls", new String[0]));
      this.METRIC_PARALLEL_LISTING_JOB_COUNT = this.metricRegistry().counter(MetricRegistry.name("parallelListingJobCount", new String[0]));
   }
}
