package org.apache.spark.util.collection;

import org.apache.spark.Partitioner;
import org.apache.spark.SparkEnv$;
import org.apache.spark.serializer.Serializer;
import scala.None$;
import scala.Option;

public final class ExternalSorter$ {
   public static final ExternalSorter$ MODULE$;

   static {
      new ExternalSorter$();
   }

   public <K, V, C> None$ $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   public <K, V, C> Option<Partitioner> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   public <K, V, C> None$ $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public <K, V, C> Serializer $lessinit$greater$default$5() {
      return SparkEnv$.MODULE$.get().serializer();
   }

   private ExternalSorter$() {
      MODULE$ = this;
   }
}
