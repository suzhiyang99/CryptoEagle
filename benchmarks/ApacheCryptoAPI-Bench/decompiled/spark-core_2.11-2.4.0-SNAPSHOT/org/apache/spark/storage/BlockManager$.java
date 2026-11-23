package org.apache.spark.storage;

import org.apache.spark.SparkEnv;
import org.apache.spark.util.IdGenerator;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Map;
import scala.collection.mutable.HashMap;
import scala.runtime.RichInt$;

public final class BlockManager$ {
   public static final BlockManager$ MODULE$;
   private final IdGenerator org$apache$spark$storage$BlockManager$$ID_GENERATOR;

   static {
      new BlockManager$();
   }

   public IdGenerator org$apache$spark$storage$BlockManager$$ID_GENERATOR() {
      return this.org$apache$spark$storage$BlockManager$$ID_GENERATOR;
   }

   public Map<BlockId, Seq<String>> blockIdsToHosts(BlockId[] blockIds, SparkEnv env, BlockManagerMaster blockManagerMaster) {
      Predef$.MODULE$.assert(env != null || blockManagerMaster != null);
      Seq blockLocations = (Seq)(blockManagerMaster == null ? Predef$.MODULE$.wrapRefArray(env.blockManager().org$apache$spark$storage$BlockManager$$getLocationBlockIds(blockIds)) : blockManagerMaster.getLocations(blockIds));
      HashMap blockManagers = new HashMap();
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), blockIds.length).foreach$mVc$sp(new Serializable(blockIds, blockLocations, blockManagers) {
         public static final long serialVersionUID = 0L;
         private final BlockId[] blockIds$1;
         private final Seq blockLocations$1;
         private final HashMap blockManagers$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.blockManagers$1.update(this.blockIds$1[i], ((TraversableLike)this.blockLocations$1.apply(i)).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(BlockManagerId x$15) {
                  return x$15.host();
               }
            }, Seq$.MODULE$.canBuildFrom()));
         }

         public {
            this.blockIds$1 = blockIds$1;
            this.blockLocations$1 = blockLocations$1;
            this.blockManagers$1 = blockManagers$1;
         }
      });
      return blockManagers.toMap(Predef$.MODULE$.$conforms());
   }

   public BlockManagerMaster blockIdsToHosts$default$3() {
      return null;
   }

   private BlockManager$() {
      MODULE$ = this;
      this.org$apache$spark$storage$BlockManager$$ID_GENERATOR = new IdGenerator();
   }
}
