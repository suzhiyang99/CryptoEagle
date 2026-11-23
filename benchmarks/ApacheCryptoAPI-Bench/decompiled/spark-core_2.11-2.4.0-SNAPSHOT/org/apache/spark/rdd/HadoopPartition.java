package org.apache.spark.rdd;

import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.SerializableWritable;
import scala.Predef;
import scala.Predef$;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001i3Q!\u0001\u0002\u0001\t)\u0011q\u0002S1e_>\u0004\b+\u0019:uSRLwN\u001c\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\u0011I!\u0001\u0006\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\u000bI$G-\u00133\u0004\u0001A\u0011A\"G\u0005\u000355\u00111!\u00138u\u0011!a\u0002A!b\u0001\n\u0003j\u0012!B5oI\u0016DX#\u0001\r\t\u0011}\u0001!\u0011!Q\u0001\na\ta!\u001b8eKb\u0004\u0003\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u0003M\u0004\"a\t\u0015\u000e\u0003\u0011R!!\n\u0014\u0002\r5\f\u0007O]3e\u0015\t9c!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003S\u0011\u0012!\"\u00138qkR\u001c\u0006\u000f\\5u\u0011\u0015Y\u0003\u0001\"\u0001-\u0003\u0019a\u0014N\\5u}Q!Qf\f\u00192!\tq\u0003!D\u0001\u0003\u0011\u00151\"\u00061\u0001\u0019\u0011\u0015a\"\u00061\u0001\u0019\u0011\u0015\t#\u00061\u0001#\u0011\u001d\u0019\u0004A1A\u0005\u0002Q\n!\"\u001b8qkR\u001c\u0006\u000f\\5u+\u0005)\u0004c\u0001\n7E%\u0011q\u0007\u0002\u0002\u0015'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.Z,sSR\f'\r\\3\t\re\u0002\u0001\u0015!\u00036\u0003-Ig\u000e];u'Bd\u0017\u000e\u001e\u0011\t\u000bm\u0002A\u0011\t\u001f\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012\u0001\u0007\u0005\u0006}\u0001!\teP\u0001\u0007KF,\u0018\r\\:\u0015\u0005\u0001\u001b\u0005C\u0001\u0007B\u0013\t\u0011UBA\u0004C_>dW-\u00198\t\u000b\u0011k\u0004\u0019A#\u0002\u000b=$\b.\u001a:\u0011\u000511\u0015BA$\u000e\u0005\r\te.\u001f\u0005\u0006\u0013\u0002!\tAS\u0001\u000fO\u0016$\b+\u001b9f\u000b:4h+\u0019:t)\u0005Y\u0005\u0003\u0002'R'Nk\u0011!\u0014\u0006\u0003\u001d>\u000b\u0011\"[7nkR\f'\r\\3\u000b\u0005Ak\u0011AC2pY2,7\r^5p]&\u0011!+\u0014\u0002\u0004\u001b\u0006\u0004\bC\u0001+X\u001d\taQ+\u0003\u0002W\u001b\u00051\u0001K]3eK\u001aL!\u0001W-\u0003\rM#(/\u001b8h\u0015\t1V\u0002"
)
public class HadoopPartition implements Partition {
   private final int rddId;
   private final int index;
   private final SerializableWritable<InputSplit> inputSplit;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int index() {
      return this.index;
   }

   public SerializableWritable<InputSplit> inputSplit() {
      return this.inputSplit;
   }

   public int hashCode() {
      return 31 * (31 + this.rddId) + this.index();
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public Map<String, String> getPipeEnvVars() {
      Map var10000;
      if (this.inputSplit().value() instanceof FileSplit) {
         FileSplit is = (FileSplit)this.inputSplit().value();
         var10000 = (Map)Map$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("map_input_file"), is.getPath().toString()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("mapreduce_map_input_file"), is.getPath().toString())}));
      } else {
         var10000 = (Map)Map$.MODULE$.apply(Nil$.MODULE$);
      }

      Map envVars = var10000;
      return envVars;
   }

   public HadoopPartition(int rddId, int index, InputSplit s) {
      this.rddId = rddId;
      this.index = index;
      Partition$class.$init$(this);
      this.inputSplit = new SerializableWritable<InputSplit>(s);
   }
}
