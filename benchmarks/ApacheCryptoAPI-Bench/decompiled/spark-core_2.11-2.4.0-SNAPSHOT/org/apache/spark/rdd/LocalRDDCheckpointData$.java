package org.apache.spark.rdd;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import scala.Serializable;

public final class LocalRDDCheckpointData$ implements Serializable {
   public static final LocalRDDCheckpointData$ MODULE$;
   private final StorageLevel DEFAULT_STORAGE_LEVEL;

   static {
      new LocalRDDCheckpointData$();
   }

   public StorageLevel DEFAULT_STORAGE_LEVEL() {
      return this.DEFAULT_STORAGE_LEVEL;
   }

   public StorageLevel transformStorageLevel(StorageLevel level) {
      return StorageLevel$.MODULE$.apply(true, level.useMemory(), level.deserialized(), level.replication());
   }

   private Object readResolve() {
      return MODULE$;
   }

   private LocalRDDCheckpointData$() {
      MODULE$ = this;
      this.DEFAULT_STORAGE_LEVEL = StorageLevel$.MODULE$.MEMORY_AND_DISK();
   }
}
