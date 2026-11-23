package org.apache.spark.storage;

import java.io.ObjectInput;
import java.util.concurrent.ConcurrentHashMap;
import scala.None$;
import scala.Option;
import scala.Serializable;

public final class BlockManagerId$ implements Serializable {
   public static final BlockManagerId$ MODULE$;
   private final ConcurrentHashMap<BlockManagerId, BlockManagerId> blockManagerIdCache;

   static {
      new BlockManagerId$();
   }

   public BlockManagerId apply(String execId, String host, int port, Option<String> topologyInfo) {
      return this.getCachedBlockManagerId(new BlockManagerId(execId, host, port, topologyInfo));
   }

   public BlockManagerId apply(ObjectInput in) {
      BlockManagerId obj = new BlockManagerId();
      obj.readExternal(in);
      return this.getCachedBlockManagerId(obj);
   }

   public Option<String> apply$default$4() {
      return None$.MODULE$;
   }

   public ConcurrentHashMap<BlockManagerId, BlockManagerId> blockManagerIdCache() {
      return this.blockManagerIdCache;
   }

   public BlockManagerId getCachedBlockManagerId(BlockManagerId id) {
      this.blockManagerIdCache().putIfAbsent(id, id);
      return (BlockManagerId)this.blockManagerIdCache().get(id);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlockManagerId$() {
      MODULE$ = this;
      this.blockManagerIdCache = new ConcurrentHashMap();
   }
}
