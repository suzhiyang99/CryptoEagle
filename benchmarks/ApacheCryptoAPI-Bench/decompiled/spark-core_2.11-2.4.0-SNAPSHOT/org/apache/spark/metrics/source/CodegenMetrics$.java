package org.apache.spark.metrics.source;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.annotation.Experimental;

@Experimental
public final class CodegenMetrics$ implements Source {
   public static final CodegenMetrics$ MODULE$;
   private final String sourceName;
   private final MetricRegistry metricRegistry;
   private final Histogram METRIC_SOURCE_CODE_SIZE;
   private final Histogram METRIC_COMPILATION_TIME;
   private final Histogram METRIC_GENERATED_CLASS_BYTECODE_SIZE;
   private final Histogram METRIC_GENERATED_METHOD_BYTECODE_SIZE;

   static {
      new CodegenMetrics$();
   }

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public Histogram METRIC_SOURCE_CODE_SIZE() {
      return this.METRIC_SOURCE_CODE_SIZE;
   }

   public Histogram METRIC_COMPILATION_TIME() {
      return this.METRIC_COMPILATION_TIME;
   }

   public Histogram METRIC_GENERATED_CLASS_BYTECODE_SIZE() {
      return this.METRIC_GENERATED_CLASS_BYTECODE_SIZE;
   }

   public Histogram METRIC_GENERATED_METHOD_BYTECODE_SIZE() {
      return this.METRIC_GENERATED_METHOD_BYTECODE_SIZE;
   }

   private CodegenMetrics$() {
      MODULE$ = this;
      this.sourceName = "CodeGenerator";
      this.metricRegistry = new MetricRegistry();
      this.METRIC_SOURCE_CODE_SIZE = this.metricRegistry().histogram(MetricRegistry.name("sourceCodeSize", new String[0]));
      this.METRIC_COMPILATION_TIME = this.metricRegistry().histogram(MetricRegistry.name("compilationTime", new String[0]));
      this.METRIC_GENERATED_CLASS_BYTECODE_SIZE = this.metricRegistry().histogram(MetricRegistry.name("generatedClassSize", new String[0]));
      this.METRIC_GENERATED_METHOD_BYTECODE_SIZE = this.metricRegistry().histogram(MetricRegistry.name("generatedMethodSize", new String[0]));
   }
}
