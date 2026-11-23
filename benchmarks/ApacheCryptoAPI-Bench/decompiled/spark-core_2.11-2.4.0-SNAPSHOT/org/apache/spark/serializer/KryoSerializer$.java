package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.nio.ByteBuffer;
import org.apache.spark.SparkConf;
import org.apache.spark.scheduler.CompressedMapStatus;
import org.apache.spark.scheduler.HighlyCompressedMapStatus;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.BoundedPriorityQueue;
import org.apache.spark.util.collection.CompactBuffer;
import org.roaringbitmap.RoaringBitmap;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;

public final class KryoSerializer$ implements Serializable {
   public static final KryoSerializer$ MODULE$;
   private final Seq<Class<?>> org$apache$spark$serializer$KryoSerializer$$toRegister;
   private final Map<Class<?>, com.esotericsoftware.kryo.Serializer<?>> org$apache$spark$serializer$KryoSerializer$$toRegisterSerializer;

   static {
      new KryoSerializer$();
   }

   public Seq<Class<?>> org$apache$spark$serializer$KryoSerializer$$toRegister() {
      return this.org$apache$spark$serializer$KryoSerializer$$toRegister;
   }

   public Map<Class<?>, com.esotericsoftware.kryo.Serializer<?>> org$apache$spark$serializer$KryoSerializer$$toRegisterSerializer() {
      return this.org$apache$spark$serializer$KryoSerializer$$toRegisterSerializer;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private KryoSerializer$() {
      MODULE$ = this;
      this.org$apache$spark$serializer$KryoSerializer$$toRegister = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new Class[]{ByteBuffer.allocate(1).getClass(), StorageLevel.class, CompressedMapStatus.class, HighlyCompressedMapStatus.class, CompactBuffer.class, BlockManagerId.class, boolean[].class, byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class, char[].class, String[].class, String[][].class, BoundedPriorityQueue.class, SparkConf.class})));
      this.org$apache$spark$serializer$KryoSerializer$$toRegisterSerializer = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(RoaringBitmap.class), new com.esotericsoftware.kryo.Serializer<RoaringBitmap>() {
         public void write(Kryo kryo, Output output, RoaringBitmap bitmap) {
            bitmap.serialize(new KryoOutputObjectOutputBridge(kryo, output));
         }

         public RoaringBitmap read(Kryo kryo, Input input, Class<RoaringBitmap> cls) {
            RoaringBitmap ret = new RoaringBitmap();
            ret.deserialize(new KryoInputObjectInputBridge(kryo, input));
            return ret;
         }
      })}));
   }
}
