package org.apache.spark;

import scala.collection.mutable.StringBuilder;

public final class InternalAccumulator$ {
   public static final InternalAccumulator$ MODULE$;
   private final String METRICS_PREFIX;
   private final String SHUFFLE_READ_METRICS_PREFIX;
   private final String SHUFFLE_WRITE_METRICS_PREFIX;
   private final String OUTPUT_METRICS_PREFIX;
   private final String INPUT_METRICS_PREFIX;
   private final String EXECUTOR_DESERIALIZE_TIME;
   private final String EXECUTOR_DESERIALIZE_CPU_TIME;
   private final String EXECUTOR_RUN_TIME;
   private final String EXECUTOR_CPU_TIME;
   private final String RESULT_SIZE;
   private final String JVM_GC_TIME;
   private final String RESULT_SERIALIZATION_TIME;
   private final String MEMORY_BYTES_SPILLED;
   private final String DISK_BYTES_SPILLED;
   private final String PEAK_EXECUTION_MEMORY;
   private final String UPDATED_BLOCK_STATUSES;
   private final String TEST_ACCUM;

   static {
      new InternalAccumulator$();
   }

   public String METRICS_PREFIX() {
      return this.METRICS_PREFIX;
   }

   public String SHUFFLE_READ_METRICS_PREFIX() {
      return this.SHUFFLE_READ_METRICS_PREFIX;
   }

   public String SHUFFLE_WRITE_METRICS_PREFIX() {
      return this.SHUFFLE_WRITE_METRICS_PREFIX;
   }

   public String OUTPUT_METRICS_PREFIX() {
      return this.OUTPUT_METRICS_PREFIX;
   }

   public String INPUT_METRICS_PREFIX() {
      return this.INPUT_METRICS_PREFIX;
   }

   public String EXECUTOR_DESERIALIZE_TIME() {
      return this.EXECUTOR_DESERIALIZE_TIME;
   }

   public String EXECUTOR_DESERIALIZE_CPU_TIME() {
      return this.EXECUTOR_DESERIALIZE_CPU_TIME;
   }

   public String EXECUTOR_RUN_TIME() {
      return this.EXECUTOR_RUN_TIME;
   }

   public String EXECUTOR_CPU_TIME() {
      return this.EXECUTOR_CPU_TIME;
   }

   public String RESULT_SIZE() {
      return this.RESULT_SIZE;
   }

   public String JVM_GC_TIME() {
      return this.JVM_GC_TIME;
   }

   public String RESULT_SERIALIZATION_TIME() {
      return this.RESULT_SERIALIZATION_TIME;
   }

   public String MEMORY_BYTES_SPILLED() {
      return this.MEMORY_BYTES_SPILLED;
   }

   public String DISK_BYTES_SPILLED() {
      return this.DISK_BYTES_SPILLED;
   }

   public String PEAK_EXECUTION_MEMORY() {
      return this.PEAK_EXECUTION_MEMORY;
   }

   public String UPDATED_BLOCK_STATUSES() {
      return this.UPDATED_BLOCK_STATUSES;
   }

   public String TEST_ACCUM() {
      return this.TEST_ACCUM;
   }

   private InternalAccumulator$() {
      MODULE$ = this;
      this.METRICS_PREFIX = "internal.metrics.";
      this.SHUFFLE_READ_METRICS_PREFIX = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"shuffle.read.").toString();
      this.SHUFFLE_WRITE_METRICS_PREFIX = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"shuffle.write.").toString();
      this.OUTPUT_METRICS_PREFIX = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"output.").toString();
      this.INPUT_METRICS_PREFIX = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"input.").toString();
      this.EXECUTOR_DESERIALIZE_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"executorDeserializeTime").toString();
      this.EXECUTOR_DESERIALIZE_CPU_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"executorDeserializeCpuTime").toString();
      this.EXECUTOR_RUN_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"executorRunTime").toString();
      this.EXECUTOR_CPU_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"executorCpuTime").toString();
      this.RESULT_SIZE = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"resultSize").toString();
      this.JVM_GC_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"jvmGCTime").toString();
      this.RESULT_SERIALIZATION_TIME = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"resultSerializationTime").toString();
      this.MEMORY_BYTES_SPILLED = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"memoryBytesSpilled").toString();
      this.DISK_BYTES_SPILLED = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"diskBytesSpilled").toString();
      this.PEAK_EXECUTION_MEMORY = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"peakExecutionMemory").toString();
      this.UPDATED_BLOCK_STATUSES = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"updatedBlockStatuses").toString();
      this.TEST_ACCUM = (new StringBuilder()).append((Object)this.METRICS_PREFIX()).append((Object)"testAccumulator").toString();
   }
}
