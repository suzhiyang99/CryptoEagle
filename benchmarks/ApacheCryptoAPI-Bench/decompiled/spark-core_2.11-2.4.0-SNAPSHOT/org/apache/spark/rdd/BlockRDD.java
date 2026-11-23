package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.BlockManager$;
import org.apache.spark.storage.BlockResult;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mb!B\u0001\u0003\u0001\u0011Q!\u0001\u0003\"m_\u000e\\'\u000b\u0012#\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\f%M\u0011\u0001\u0001\u0004\t\u0004\u001b9\u0001R\"\u0001\u0002\n\u0005=\u0011!a\u0001*E\tB\u0011\u0011C\u0005\u0007\u0001\t\u0015\u0019\u0002A1\u0001\u0016\u0005\u0005!6\u0001A\t\u0003-q\u0001\"a\u0006\u000e\u000e\u0003aQ\u0011!G\u0001\u0006g\u000e\fG.Y\u0005\u00037a\u0011qAT8uQ&tw\r\u0005\u0002\u0018;%\u0011a\u0004\u0007\u0002\u0004\u0003:L\b\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u0005M\u001c\u0007C\u0001\u0012$\u001b\u0005!\u0011B\u0001\u0013\u0005\u00051\u0019\u0006/\u0019:l\u0007>tG/\u001a=u\u0011!1\u0003A!b\u0001\n\u00039\u0013\u0001\u00032m_\u000e\\\u0017\nZ:\u0016\u0003!\u00022aF\u0015,\u0013\tQ\u0003DA\u0003BeJ\f\u0017\u0010\u0005\u0002-_5\tQF\u0003\u0002/\t\u000591\u000f^8sC\u001e,\u0017B\u0001\u0019.\u0005\u001d\u0011En\\2l\u0013\u0012D\u0001B\r\u0001\u0003\u0002\u0003\u0006I\u0001K\u0001\nE2|7m[%eg\u0002B#!\r\u001b\u0011\u0005])\u0014B\u0001\u001c\u0019\u0005%!(/\u00198tS\u0016tG\u000f\u0003\u00059\u0001\t\r\t\u0015a\u0003:\u0003))g/\u001b3f]\u000e,G%\r\t\u0004uu\u0002R\"A\u001e\u000b\u0005qB\u0012a\u0002:fM2,7\r^\u0005\u0003}m\u0012\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006\u0001\u0002!\t!Q\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\t+e\t\u0006\u0002D\tB\u0019Q\u0002\u0001\t\t\u000baz\u00049A\u001d\t\u000b\u0001z\u0004\u0019A\u0011\t\u000b\u0019z\u0004\u0019\u0001\u0015\t\u0011!\u0003\u0001R1A\u0005\u0002%\u000b!b\u00187pG\u0006$\u0018n\u001c8t+\u0005Q\u0005\u0003B&OWEs!a\u0006'\n\u00055C\u0012A\u0002)sK\u0012,g-\u0003\u0002P!\n\u0019Q*\u00199\u000b\u00055C\u0002c\u0001*[;:\u00111\u000b\u0017\b\u0003)^k\u0011!\u0016\u0006\u0003-R\ta\u0001\u0010:p_Rt\u0014\"A\r\n\u0005eC\u0012a\u00029bG.\fw-Z\u0005\u00037r\u00131aU3r\u0015\tI\u0006\u0004\u0005\u0002L=&\u0011q\f\u0015\u0002\u0007'R\u0014\u0018N\\4\t\u0011\u0005\u0004\u0001\u0012!Q!\n)\u000b1b\u00187pG\u0006$\u0018n\u001c8tA!\u0012\u0001\r\u000e\u0005\bI\u0002\u0001\r\u0011\"\u0003f\u0003!y\u0016n\u001d,bY&$W#\u00014\u0011\u0005]9\u0017B\u00015\u0019\u0005\u001d\u0011un\u001c7fC:DqA\u001b\u0001A\u0002\u0013%1.\u0001\u0007`SN4\u0016\r\\5e?\u0012*\u0017\u000f\u0006\u0002m_B\u0011q#\\\u0005\u0003]b\u0011A!\u00168ji\"9\u0001/[A\u0001\u0002\u00041\u0017a\u0001=%c!1!\u000f\u0001Q!\n\u0019\f\u0011bX5t-\u0006d\u0017\u000e\u001a\u0011)\u0005E$\bCA\fv\u0013\t1\bD\u0001\u0005w_2\fG/\u001b7f\u0011\u0015A\b\u0001\"\u0011z\u000359W\r\u001e)beRLG/[8ogV\t!\u0010E\u0002\u0018Sm\u0004\"A\t?\n\u0005u$!!\u0003)beRLG/[8o\u0011\u0019y\b\u0001\"\u0011\u0002\u0002\u000591m\\7qkR,GCBA\u0002\u0003\u0013\ti\u0001\u0005\u0003S\u0003\u000b\u0001\u0012bAA\u00049\nA\u0011\n^3sCR|'\u000f\u0003\u0004\u0002\fy\u0004\ra_\u0001\u0006gBd\u0017\u000e\u001e\u0005\b\u0003\u001fq\b\u0019AA\t\u0003\u001d\u0019wN\u001c;fqR\u00042AIA\n\u0013\r\t)\u0002\u0002\u0002\f)\u0006\u001c8nQ8oi\u0016DH\u000fC\u0004\u0002\u001a\u0001!\t%a\u0007\u0002+\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\fG/[8ogR\u0019\u0011+!\b\t\u000f\u0005-\u0011q\u0003a\u0001w\"A\u0011\u0011\u0005\u0001\u0005\u0002\u0011\t\u0019#\u0001\u0007sK6|g/\u001a\"m_\u000e\\7\u000fF\u0001m\u0011\u001d\t9\u0003\u0001C\u0001\t\u0015\fq![:WC2LG\r\u0003\u0005\u0002,\u0001!\t\u0001BA\u0012\u0003-\t7o]3siZ\u000bG.\u001b3\t\u000f\u0005=\u0002\u0001\"\u0005\u00022\u0005\u0019r-\u001a;CY>\u001c7.\u00133M_\u000e\fG/[8ogR\t!\n"
)
public class BlockRDD<T> extends RDD<T> {
   private final transient BlockId[] blockIds;
   private final ClassTag<T> evidence$1;
   private transient Map<BlockId, Seq<String>> _locations;
   private volatile boolean _isValid;
   private transient volatile boolean bitmap$trans$0;

   private Map _locations$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$trans$0) {
            this._locations = BlockManager$.MODULE$.blockIdsToHosts(this.blockIds(), SparkEnv$.MODULE$.get(), BlockManager$.MODULE$.blockIdsToHosts$default$3());
            this.bitmap$trans$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this._locations;
   }

   public BlockId[] blockIds() {
      return this.blockIds;
   }

   public Map<BlockId, Seq<String>> _locations() {
      return this.bitmap$trans$0 ? this._locations : this._locations$lzycompute();
   }

   private boolean _isValid() {
      return this._isValid;
   }

   private void _isValid_$eq(boolean x$1) {
      this._isValid = x$1;
   }

   public Partition[] getPartitions() {
      this.assertValid();
      return (Partition[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.blockIds().length).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockRDD $outer;

         public final Partition apply(int i) {
            return new BlockRDDPartition(this.$outer.blockIds()[i], i);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class));
   }

   public Iterator<T> compute(Partition split, TaskContext context) {
      this.assertValid();
      BlockManager blockManager = SparkEnv$.MODULE$.get().blockManager();
      BlockId blockId = ((BlockRDDPartition)split).blockId();
      Option var5 = blockManager.get(blockId, this.evidence$1);
      if (var5 instanceof Some) {
         Some var6 = (Some)var5;
         BlockResult block = (BlockResult)var6.x();
         Iterator var8 = block.data();
         return var8;
      } else if (None$.MODULE$.equals(var5)) {
         throw new Exception((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not compute split, block ", " of RDD ", " not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{blockId, BoxesRunTime.boxToInteger(this.id())})));
      } else {
         throw new MatchError(var5);
      }
   }

   public Seq<String> getPreferredLocations(Partition split) {
      this.assertValid();
      return (Seq)this._locations().apply(((BlockRDDPartition)split).blockId());
   }

   public void removeBlocks() {
      Predef$.MODULE$.refArrayOps(this.blockIds()).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockRDD $outer;

         public final void apply(BlockId blockId) {
            this.$outer.sparkContext().env().blockManager().master().removeBlock(blockId);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this._isValid_$eq(false);
   }

   public boolean isValid() {
      return this._isValid();
   }

   public void assertValid() {
      if (!this.isValid()) {
         throw new SparkException((new StringOps(Predef$.MODULE$.augmentString("Attempted to use %s after its blocks have been removed!"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.toString()})));
      }
   }

   public Map<BlockId, Seq<String>> getBlockIdLocations() {
      return this._locations();
   }

   public BlockRDD(SparkContext sc, BlockId[] blockIds, ClassTag<T> evidence$1) {
      super(sc, Nil$.MODULE$, evidence$1);
      this.blockIds = blockIds;
      this.evidence$1 = evidence$1;
      this._isValid = true;
   }
}
