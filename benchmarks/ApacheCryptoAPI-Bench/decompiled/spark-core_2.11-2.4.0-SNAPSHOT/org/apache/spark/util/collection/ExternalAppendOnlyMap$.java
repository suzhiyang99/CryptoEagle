package org.apache.spark.util.collection;

import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockManager;
import scala.Serializable;

public final class ExternalAppendOnlyMap$ implements Serializable {
   public static final ExternalAppendOnlyMap$ MODULE$;

   static {
      new ExternalAppendOnlyMap$();
   }

   public <T> int org$apache$spark$util$collection$ExternalAppendOnlyMap$$hash(T obj) {
      return obj == null ? 0 : obj.hashCode();
   }

   public <K, V, C> Serializer $lessinit$greater$default$4() {
      return SparkEnv$.MODULE$.get().serializer();
   }

   public <K, V, C> BlockManager $lessinit$greater$default$5() {
      return SparkEnv$.MODULE$.get().blockManager();
   }

   public <K, V, C> TaskContext $lessinit$greater$default$6() {
      return TaskContext$.MODULE$.get();
   }

   public <K, V, C> SerializerManager $lessinit$greater$default$7() {
      return SparkEnv$.MODULE$.get().serializerManager();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExternalAppendOnlyMap$() {
      MODULE$ = this;
   }
}
