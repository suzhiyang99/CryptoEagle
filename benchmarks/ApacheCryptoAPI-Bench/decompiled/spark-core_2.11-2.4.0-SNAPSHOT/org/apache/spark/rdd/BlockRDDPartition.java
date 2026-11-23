package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.storage.BlockId;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I2Q!\u0001\u0002\u0001\t)\u0011\u0011C\u00117pG.\u0014F\t\u0012)beRLG/[8o\u0015\t\u0019A!A\u0002sI\u0012T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013'5\tA!\u0003\u0002\u0015\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\t-\u0001\u0011)\u0019!C\u00011\u00059!\r\\8dW&#7\u0001A\u000b\u00023A\u0011!$H\u0007\u00027)\u0011A\u0004B\u0001\bgR|'/Y4f\u0013\tq2DA\u0004CY>\u001c7.\u00133\t\u0011\u0001\u0002!\u0011!Q\u0001\ne\t\u0001B\u00197pG.LE\r\t\u0005\tE\u0001\u0011\t\u0011)A\u0005G\u0005\u0019\u0011\u000e\u001a=\u0011\u00051!\u0013BA\u0013\u000e\u0005\rIe\u000e\u001e\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%ZC\u0006\u0005\u0002+\u00015\t!\u0001C\u0003\u0017M\u0001\u0007\u0011\u0004C\u0003#M\u0001\u00071\u0005C\u0004/\u0001\t\u0007I\u0011A\u0018\u0002\u000b%tG-\u001a=\u0016\u0003\rBa!\r\u0001!\u0002\u0013\u0019\u0013AB5oI\u0016D\b\u0005"
)
public class BlockRDDPartition implements Partition {
   private final BlockId blockId;
   private final int index;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int hashCode() {
      return Partition$class.hashCode(this);
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public BlockId blockId() {
      return this.blockId;
   }

   public int index() {
      return this.index;
   }

   public BlockRDDPartition(BlockId blockId, int idx) {
      this.blockId = blockId;
      Partition$class.$init$(this);
      this.index = idx;
   }
}
