package org.apache.spark.storage;

import java.io.ObjectInput;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;

public final class StorageLevel$ implements Serializable {
   public static final StorageLevel$ MODULE$;
   private final StorageLevel NONE;
   private final StorageLevel DISK_ONLY;
   private final StorageLevel DISK_ONLY_2;
   private final StorageLevel MEMORY_ONLY;
   private final StorageLevel MEMORY_ONLY_2;
   private final StorageLevel MEMORY_ONLY_SER;
   private final StorageLevel MEMORY_ONLY_SER_2;
   private final StorageLevel MEMORY_AND_DISK;
   private final StorageLevel MEMORY_AND_DISK_2;
   private final StorageLevel MEMORY_AND_DISK_SER;
   private final StorageLevel MEMORY_AND_DISK_SER_2;
   private final StorageLevel OFF_HEAP;
   private final ConcurrentHashMap<StorageLevel, StorageLevel> storageLevelCache;

   static {
      new StorageLevel$();
   }

   public StorageLevel NONE() {
      return this.NONE;
   }

   public StorageLevel DISK_ONLY() {
      return this.DISK_ONLY;
   }

   public StorageLevel DISK_ONLY_2() {
      return this.DISK_ONLY_2;
   }

   public StorageLevel MEMORY_ONLY() {
      return this.MEMORY_ONLY;
   }

   public StorageLevel MEMORY_ONLY_2() {
      return this.MEMORY_ONLY_2;
   }

   public StorageLevel MEMORY_ONLY_SER() {
      return this.MEMORY_ONLY_SER;
   }

   public StorageLevel MEMORY_ONLY_SER_2() {
      return this.MEMORY_ONLY_SER_2;
   }

   public StorageLevel MEMORY_AND_DISK() {
      return this.MEMORY_AND_DISK;
   }

   public StorageLevel MEMORY_AND_DISK_2() {
      return this.MEMORY_AND_DISK_2;
   }

   public StorageLevel MEMORY_AND_DISK_SER() {
      return this.MEMORY_AND_DISK_SER;
   }

   public StorageLevel MEMORY_AND_DISK_SER_2() {
      return this.MEMORY_AND_DISK_SER_2;
   }

   public StorageLevel OFF_HEAP() {
      return this.OFF_HEAP;
   }

   @DeveloperApi
   public StorageLevel fromString(String s) {
      StorageLevel var3;
      if ("NONE".equals(s)) {
         var3 = this.NONE();
      } else if ("DISK_ONLY".equals(s)) {
         var3 = this.DISK_ONLY();
      } else if ("DISK_ONLY_2".equals(s)) {
         var3 = this.DISK_ONLY_2();
      } else if ("MEMORY_ONLY".equals(s)) {
         var3 = this.MEMORY_ONLY();
      } else if ("MEMORY_ONLY_2".equals(s)) {
         var3 = this.MEMORY_ONLY_2();
      } else if ("MEMORY_ONLY_SER".equals(s)) {
         var3 = this.MEMORY_ONLY_SER();
      } else if ("MEMORY_ONLY_SER_2".equals(s)) {
         var3 = this.MEMORY_ONLY_SER_2();
      } else if ("MEMORY_AND_DISK".equals(s)) {
         var3 = this.MEMORY_AND_DISK();
      } else if ("MEMORY_AND_DISK_2".equals(s)) {
         var3 = this.MEMORY_AND_DISK_2();
      } else if ("MEMORY_AND_DISK_SER".equals(s)) {
         var3 = this.MEMORY_AND_DISK_SER();
      } else if ("MEMORY_AND_DISK_SER_2".equals(s)) {
         var3 = this.MEMORY_AND_DISK_SER_2();
      } else {
         if (!"OFF_HEAP".equals(s)) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid StorageLevel: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{s})));
         }

         var3 = this.OFF_HEAP();
      }

      return var3;
   }

   @DeveloperApi
   public StorageLevel apply(boolean useDisk, boolean useMemory, boolean useOffHeap, boolean deserialized, int replication) {
      return this.getCachedStorageLevel(new StorageLevel(useDisk, useMemory, useOffHeap, deserialized, replication));
   }

   @DeveloperApi
   public StorageLevel apply(boolean useDisk, boolean useMemory, boolean deserialized, int replication) {
      return this.getCachedStorageLevel(new StorageLevel(useDisk, useMemory, false, deserialized, replication));
   }

   @DeveloperApi
   public StorageLevel apply(int flags, int replication) {
      return this.getCachedStorageLevel(new StorageLevel(flags, replication));
   }

   @DeveloperApi
   public StorageLevel apply(ObjectInput in) {
      StorageLevel obj = new StorageLevel();
      obj.readExternal(in);
      return this.getCachedStorageLevel(obj);
   }

   public int apply$default$4() {
      return 1;
   }

   public ConcurrentHashMap<StorageLevel, StorageLevel> storageLevelCache() {
      return this.storageLevelCache;
   }

   public StorageLevel getCachedStorageLevel(StorageLevel level) {
      this.storageLevelCache().putIfAbsent(level, level);
      return (StorageLevel)this.storageLevelCache().get(level);
   }

   public int org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5() {
      return 1;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StorageLevel$() {
      MODULE$ = this;
      this.NONE = new StorageLevel(false, false, false, false, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.DISK_ONLY = new StorageLevel(true, false, false, false, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.DISK_ONLY_2 = new StorageLevel(true, false, false, false, 2);
      this.MEMORY_ONLY = new StorageLevel(false, true, false, true, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.MEMORY_ONLY_2 = new StorageLevel(false, true, false, true, 2);
      this.MEMORY_ONLY_SER = new StorageLevel(false, true, false, false, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.MEMORY_ONLY_SER_2 = new StorageLevel(false, true, false, false, 2);
      this.MEMORY_AND_DISK = new StorageLevel(true, true, false, true, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.MEMORY_AND_DISK_2 = new StorageLevel(true, true, false, true, 2);
      this.MEMORY_AND_DISK_SER = new StorageLevel(true, true, false, false, this.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
      this.MEMORY_AND_DISK_SER_2 = new StorageLevel(true, true, false, false, 2);
      this.OFF_HEAP = new StorageLevel(true, true, true, false, 1);
      this.storageLevelCache = new ConcurrentHashMap();
   }
}
