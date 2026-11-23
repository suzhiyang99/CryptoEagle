package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000112Q!\u0001\u0002\u0001\t)\u0011A\u0004U1si&$\u0018n\u001c8QeVt\u0017N\\4S\t\u0012\u0003\u0016M\u001d;ji&|gN\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!cE\u0007\u0002\t%\u0011A\u0003\u0002\u0002\n!\u0006\u0014H/\u001b;j_:D\u0001B\u0006\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0004S\u0012D8\u0001\u0001\t\u0003\u0019eI!AG\u0007\u0003\u0007%sG\u000f\u0003\u0005\u001d\u0001\t\u0015\r\u0011\"\u0001\u001e\u0003-\u0001\u0018M]3oiN\u0003H.\u001b;\u0016\u0003EA\u0001b\b\u0001\u0003\u0002\u0003\u0006I!E\u0001\ra\u0006\u0014XM\u001c;Ta2LG\u000f\t\u0005\u0006C\u0001!\tAI\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\r*c\u0005\u0005\u0002%\u00015\t!\u0001C\u0003\u0017A\u0001\u0007\u0001\u0004C\u0003\u001dA\u0001\u0007\u0011\u0003C\u0004)\u0001\t\u0007I\u0011I\u0015\u0002\u000b%tG-\u001a=\u0016\u0003aAaa\u000b\u0001!\u0002\u0013A\u0012AB5oI\u0016D\b\u0005"
)
public class PartitionPruningRDDPartition implements Partition {
   private final Partition parentSplit;
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

   public Partition parentSplit() {
      return this.parentSplit;
   }

   public int index() {
      return this.index;
   }

   public PartitionPruningRDDPartition(int idx, Partition parentSplit) {
      this.parentSplit = parentSplit;
      Partition$class.$init$(this);
      this.index = idx;
   }
}
