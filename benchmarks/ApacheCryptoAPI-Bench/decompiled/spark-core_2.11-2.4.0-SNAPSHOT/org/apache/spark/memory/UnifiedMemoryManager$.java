package org.apache.spark.memory;

import org.apache.spark.SparkConf;
import scala.Predef$;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichDouble$;

public final class UnifiedMemoryManager$ {
   public static final UnifiedMemoryManager$ MODULE$;
   private final int RESERVED_SYSTEM_MEMORY_BYTES;

   static {
      new UnifiedMemoryManager$();
   }

   private int RESERVED_SYSTEM_MEMORY_BYTES() {
      return this.RESERVED_SYSTEM_MEMORY_BYTES;
   }

   public UnifiedMemoryManager apply(SparkConf conf, int numCores) {
      long maxMemory = this.getMaxMemory(conf);
      return new UnifiedMemoryManager(conf, maxMemory, (long)((double)maxMemory * conf.getDouble("spark.memory.storageFraction", (double)0.5F)), numCores);
   }

   private long getMaxMemory(SparkConf conf) {
      long systemMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime().maxMemory());
      long reservedMemory = conf.getLong("spark.testing.reservedMemory", conf.contains("spark.testing") ? 0L : (long)this.RESERVED_SYSTEM_MEMORY_BYTES());
      long minSystemMemory = (long)RichDouble$.MODULE$.ceil$extension(Predef$.MODULE$.doubleWrapper((double)reservedMemory * (double)1.5F));
      if (systemMemory < minSystemMemory) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"System memory ", " must "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(systemMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be at least ", ". Please increase heap size using the --driver-memory "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(minSystemMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"option or spark.driver.memory in Spark configuration."})))).s(Nil$.MODULE$)).toString());
      } else {
         if (conf.contains("spark.executor.memory")) {
            long executorMemory = conf.getSizeAsBytes("spark.executor.memory");
            if (executorMemory < minSystemMemory) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor memory ", " must be at least "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(executorMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ". Please increase executor memory using the "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(minSystemMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"--executor-memory option or spark.executor.memory in Spark configuration."})))).s(Nil$.MODULE$)).toString());
            }
         }

         long usableMemory = systemMemory - reservedMemory;
         double memoryFraction = conf.getDouble("spark.memory.fraction", 0.6);
         return (long)((double)usableMemory * memoryFraction);
      }
   }

   private UnifiedMemoryManager$() {
      MODULE$ = this;
      this.RESERVED_SYSTEM_MEMORY_BYTES = 314572800;
   }
}
