package org.apache.spark.memory;

import org.apache.spark.SparkConf;
import scala.Predef$;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public final class StaticMemoryManager$ {
   public static final StaticMemoryManager$ MODULE$;
   private final int MIN_MEMORY_BYTES;

   static {
      new StaticMemoryManager$();
   }

   private int MIN_MEMORY_BYTES() {
      return this.MIN_MEMORY_BYTES;
   }

   public long org$apache$spark$memory$StaticMemoryManager$$getMaxStorageMemory(SparkConf conf) {
      long systemMaxMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime().maxMemory());
      double memoryFraction = conf.getDouble("spark.storage.memoryFraction", 0.6);
      double safetyFraction = conf.getDouble("spark.storage.safetyFraction", 0.9);
      return (long)((double)systemMaxMemory * memoryFraction * safetyFraction);
   }

   public long org$apache$spark$memory$StaticMemoryManager$$getMaxExecutionMemory(SparkConf conf) {
      long systemMaxMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime().maxMemory());
      if (systemMaxMemory < (long)this.MIN_MEMORY_BYTES()) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"System memory ", " must "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(systemMaxMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be at least ", ". Please increase heap size using the --driver-memory "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.MIN_MEMORY_BYTES())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"option or spark.driver.memory in Spark configuration."})))).s(Nil$.MODULE$)).toString());
      } else {
         if (conf.contains("spark.executor.memory")) {
            long executorMemory = conf.getSizeAsBytes("spark.executor.memory");
            if (executorMemory < (long)this.MIN_MEMORY_BYTES()) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor memory ", " must be at least "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(executorMemory)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ". Please increase executor memory using the "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.MIN_MEMORY_BYTES())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"--executor-memory option or spark.executor.memory in Spark configuration."})))).s(Nil$.MODULE$)).toString());
            }
         }

         double memoryFraction = conf.getDouble("spark.shuffle.memoryFraction", 0.2);
         double safetyFraction = conf.getDouble("spark.shuffle.safetyFraction", 0.8);
         return (long)((double)systemMaxMemory * memoryFraction * safetyFraction);
      }
   }

   private StaticMemoryManager$() {
      MODULE$ = this;
      this.MIN_MEMORY_BYTES = 33554432;
   }
}
