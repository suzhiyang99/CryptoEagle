package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.util.kvstore.KVIndex;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-3Q!\u0001\u0002\u0001\t)\u0011QC\u0015#E'R|'/Y4f\u0013:4wn\u0016:baB,'O\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007\u0002\u0003\n\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\t%tgm\\\u0002\u0001+\u0005)\u0002C\u0001\f\u001c\u001b\u00059\"B\u0001\r\u001a\u0003\t1\u0018G\u0003\u0002\u001b\u0005\u0005\u0019\u0011\r]5\n\u0005q9\"A\u0004*E\tN#xN]1hK&sgm\u001c\u0005\t=\u0001\u0011\t\u0011)A\u0005+\u0005)\u0011N\u001c4pA!)\u0001\u0005\u0001C\u0001C\u00051A(\u001b8jiz\"\"A\t\u0013\u0011\u0005\r\u0002Q\"\u0001\u0002\t\u000bIy\u0002\u0019A\u000b\t\u000b\u0019\u0002A\u0011A\u0014\u0002\u0005%$W#\u0001\u0015\u0011\u00051I\u0013B\u0001\u0016\u000e\u0005\rIe\u000e\u001e\u0015\u0003K1\u0002\"!\f\u001a\u000e\u00039R!a\f\u0019\u0002\u000f-48\u000f^8sK*\u0011\u0011\u0007B\u0001\u0005kRLG.\u0003\u00024]\t91JV%oI\u0016D\bFA\u00136!\t1t(D\u00018\u0015\tA\u0014(\u0001\u0006b]:|G/\u0019;j_:T!AO\u001e\u0002\u000f)\f7m[:p]*\u0011A(P\u0001\nM\u0006\u001cH/\u001a:y[2T\u0011AP\u0001\u0004G>l\u0017B\u0001!8\u0005)Q5o\u001c8JO:|'/\u001a\u0005\u0006\u0005\u0002!\taQ\u0001\u0007G\u0006\u001c\u0007.\u001a3\u0016\u0003\u0011\u0003\"\u0001D#\n\u0005\u0019k!a\u0002\"p_2,\u0017M\u001c\u0015\u0005\u00032B\u0015*A\u0003wC2,X-I\u0001CQ\t\tU\u0007"
)
public class RDDStorageInfoWrapper {
   private final RDDStorageInfo info;

   public RDDStorageInfo info() {
      return this.info;
   }

   @JsonIgnore
   @KVIndex
   public int id() {
      return this.info().id();
   }

   @JsonIgnore
   @KVIndex("cached")
   public boolean cached() {
      return this.info().numCachedPartitions() > 0;
   }

   public RDDStorageInfoWrapper(RDDStorageInfo info) {
      this.info = info;
   }
}
