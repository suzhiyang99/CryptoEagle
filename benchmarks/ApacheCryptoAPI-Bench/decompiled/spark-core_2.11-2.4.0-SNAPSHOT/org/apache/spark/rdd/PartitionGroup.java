package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001a3A!\u0001\u0002\u0001\u0017\tq\u0001+\u0019:uSRLwN\\$s_V\u0004(BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011C\u0001\u0001\r!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fM\"A1\u0003\u0001BC\u0002\u0013\u0005A#A\u0004qe\u00164Gj\\2\u0016\u0003U\u00012!\u0004\f\u0019\u0013\t9bB\u0001\u0004PaRLwN\u001c\t\u00033qq!!\u0004\u000e\n\u0005mq\u0011A\u0002)sK\u0012,g-\u0003\u0002\u001e=\t11\u000b\u001e:j]\u001eT!a\u0007\b\t\u0011\u0001\u0002!\u0011!Q\u0001\nU\t\u0001\u0002\u001d:fM2{7\r\t\u0005\u0006E\u0001!\taI\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u00112\u0003CA\u0013\u0001\u001b\u0005\u0011\u0001bB\n\"!\u0003\u0005\r!\u0006\u0005\bQ\u0001\u0011\r\u0011\"\u0001*\u0003)\u0001\u0018M\u001d;ji&|gn]\u000b\u0002UA\u00191\u0006\r\u001a\u000e\u00031R!!\f\u0018\u0002\u000f5,H/\u00192mK*\u0011qFD\u0001\u000bG>dG.Z2uS>t\u0017BA\u0019-\u0005-\t%O]1z\u0005V4g-\u001a:\u0011\u0005M\"T\"\u0001\u0003\n\u0005U\"!!\u0003)beRLG/[8o\u0011\u00199\u0004\u0001)A\u0005U\u0005Y\u0001/\u0019:uSRLwN\\:!\u0011\u0015I\u0004\u0001\"\u0001;\u00035qW/\u001c)beRLG/[8ogV\t1\b\u0005\u0002\u000ey%\u0011QH\u0004\u0002\u0004\u0013:$\bF\u0001\u0001@!\t\u00015)D\u0001B\u0015\t\u0011E!\u0001\u0006b]:|G/\u0019;j_:L!\u0001R!\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\b\u000f\u0019\u0013\u0011\u0011!E\u0001\u000f\u0006q\u0001+\u0019:uSRLwN\\$s_V\u0004\bCA\u0013I\r\u001d\t!!!A\t\u0002%\u001b\"\u0001\u0013\u0007\t\u000b\tBE\u0011A&\u0015\u0003\u001dCq!\u0014%\u0012\u0002\u0013\u0005a*A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%M\u000b\u0002\u001f*\u0012Q\u0003U\u0016\u0002#B\u0011!KV\u0007\u0002'*\u0011A+V\u0001\nk:\u001c\u0007.Z2lK\u0012T!A\u0011\b\n\u0005]\u001b&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u0002"
)
public class PartitionGroup {
   private final Option<String> prefLoc;
   private final ArrayBuffer<Partition> partitions;

   public static Option<String> $lessinit$greater$default$1() {
      return PartitionGroup$.MODULE$.$lessinit$greater$default$1();
   }

   public Option<String> prefLoc() {
      return this.prefLoc;
   }

   public ArrayBuffer<Partition> partitions() {
      return this.partitions;
   }

   public int numPartitions() {
      return this.partitions().size();
   }

   public PartitionGroup(Option<String> prefLoc) {
      this.prefLoc = prefLoc;
      this.partitions = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
   }
}
