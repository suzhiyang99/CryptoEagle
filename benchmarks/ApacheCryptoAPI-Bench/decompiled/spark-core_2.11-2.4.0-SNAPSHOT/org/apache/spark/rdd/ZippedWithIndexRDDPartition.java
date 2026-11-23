package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2Q!\u0001\u0002\u0001\t)\u00111DW5qa\u0016$w+\u001b;i\u0013:$W\r\u001f*E\tB\u000b'\u000f^5uS>t'BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON!\u0001aC\t\u0016!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!cE\u0007\u0002\t%\u0011A\u0003\u0002\u0002\n!\u0006\u0014H/\u001b;j_:\u0004\"\u0001\u0004\f\n\u0005]i!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\r\u0001\u0005\u000b\u0007I\u0011A\u000e\u0002\tA\u0014XM^\u0002\u0001+\u0005\t\u0002\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\t\u0002\u000bA\u0014XM\u001e\u0011\t\u0011}\u0001!Q1A\u0005\u0002\u0001\n!b\u001d;beRLe\u000eZ3y+\u0005\t\u0003C\u0001\u0007#\u0013\t\u0019SB\u0001\u0003M_:<\u0007\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u0017M$\u0018M\u001d;J]\u0012,\u0007\u0010\t\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%ZC\u0006\u0005\u0002+\u00015\t!\u0001C\u0003\u001aM\u0001\u0007\u0011\u0003C\u0003 M\u0001\u0007\u0011\u0005C\u0004/\u0001\t\u0007I\u0011I\u0018\u0002\u000b%tG-\u001a=\u0016\u0003A\u0002\"\u0001D\u0019\n\u0005Ij!aA%oi\"1A\u0007\u0001Q\u0001\nA\na!\u001b8eKb\u0004\u0003"
)
public class ZippedWithIndexRDDPartition implements Partition {
   private final Partition prev;
   private final long startIndex;
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

   public Partition prev() {
      return this.prev;
   }

   public long startIndex() {
      return this.startIndex;
   }

   public int index() {
      return this.index;
   }

   public ZippedWithIndexRDDPartition(Partition prev, long startIndex) {
      this.prev = prev;
      this.startIndex = startIndex;
      Partition$class.$init$(this);
      this.index = prev.index();
   }
}
